package co.server;

import co.server.pack.Request;
import co.server.proxy.ProxyFactory;
import co.server.registry.RegistryProcesser;
import co.server.transport.Client;
import co.server.transport.CoChannel;
import co.server.transport.CoFuture;
import org.springframework.stereotype.Component;

import java.net.ConnectException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component("services")
public class Services {
    private RegistryProcesser processer;

    private static Map<String, Map<String, String>> references;

    private Map<String, Client> services = new ConcurrentHashMap<>();

    private Map<String, List<String>> addrs = new ConcurrentHashMap<>();

    public Map<String, Map<String, String>> getReferences() {
        return references;
    }

    public Services setReferences(Map<String, Map<String, String>> references) {
        this.references = references;
        return this;
    }

    public void init(RegistryProcesser processer) {
        this.processer = processer;
        this.processer.subscribe();

        Map<String, Set<String>> servicesList = this.processer.getServerList();
        Set<String> serviceNames = servicesList.keySet();
        for (String serviceName : serviceNames) {
            this.createServerClient(serviceName, servicesList);
        }
    }

    public Client getClient(String serviceName) {
        if (this.services.containsKey(serviceName)) {
            return this.services.get(serviceName);
        }

        return null;
    }

    public void close() {
        for (String key : this.services.keySet()) {
            this.services.get(key).close();
        }
    }

    public Object call(String serviceName, Request request) throws Exception {
        Client client = this.getClient(serviceName);
        if (client == null) {
            throw new Exception("找不到服务：" + serviceName);
        }

        try {
            client.connect();
        } catch (ConnectException e) {
            //抛一个事件出去。方便做故障切换
            throw e;
        }

        CoChannel cch = client.getChannel();
        CoFuture coFuture = new CoFuture(cch, request);
        cch.sendMsg(request);
        return coFuture.get();
    }

    public static <T> T getBean(String beanName) {
        if (references.containsKey(beanName)) {
            try {
                Object ret = ProxyFactory.newMapperProxy(references.get(beanName).get("interface"),
                        references.get(beanName).get("serverName"), beanName);
                if (ret != null) return (T) ret;
            } catch (Exception e) {
                return null;
            }
        }

        return null;
    }

    public void refreshServices(String serviceName) {
        Client client = this.getClient(serviceName);
        if (client != null) {
            Set addrs = this.processer.getServerList().get(serviceName);
            if (!addrs.contains(client.getAddr())) {
                client.close();
                this.createServerClient(serviceName);
            }
        } else {
            this.createServerClient(serviceName);
        }
    }

    private void createServerClient(String serviceName) {
        this.createServerClient(serviceName, this.processer.getServerList());
    }

    private void createServerClient(String serviceName, Map<String, Set<String>> servicesList) {
        //这里可以做一次负载算法,目前就随机取
        Set addrs = servicesList.get(serviceName);
        if (addrs == null) return;

        List laddrs = new ArrayList<>(addrs);
        this.addrs.put(serviceName, laddrs);

        boolean init = false;
        while (!init) {
            String addr = getRandomAddr(serviceName);
            if (addr == null) {
                break;
            }
            String[] url = addr.split(":");
            try {
                this.services.put(serviceName, new Client(url[0], Integer.parseInt(url[1])));
                init = true;
            } catch (ConnectException e) {

            }
        }
    }

    private String getRandomAddr(String serviceName) {
        int len = this.addrs.get(serviceName).size();
        if (len < 1) return null;

        Random rand = new Random();
        int i = rand.nextInt(len);
        String addr = this.addrs.get(serviceName).get(i);
        this.addrs.remove(i);
        return addr;
    }
}
