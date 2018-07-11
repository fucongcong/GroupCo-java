package co.server;

import co.server.proxy.ProxyFactory;
import co.server.registry.RegistryProcesser;
import co.server.transport.Client;
import org.springframework.stereotype.Component;

import java.net.ConnectException;
import java.util.*;

@Component("services")
public class Services {
    private RegistryProcesser processer;

    private static Map<String, Map<String, String>> references;

    private Map<String, Client> services = new HashMap<>();

    private List<String> addrs;

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
            //这里可以做一次负载算法,目前就随机取
            Set addrs = servicesList.get(serviceName);
            if (addrs == null) continue;
            this.addrs = new ArrayList<>(addrs);

            boolean init = false;
            while (!init) {
                String addr = getRandomAddr();
                String[] url = addr.split(":");
                try {
                    this.services.put(serviceName, new Client(url[0], Integer.parseInt(url[1])));
                    init = true;
                } catch (ConnectException e) {

                }
            }
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

    private String getRandomAddr() {
        Random rand = new Random();
        int len = this.addrs.size();
        int i = rand.nextInt(len);
        String addr = this.addrs.get(i);
        this.addrs.remove(i);
        return addr;
    }
}
