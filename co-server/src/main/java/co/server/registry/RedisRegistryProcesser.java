package co.server.registry;

import co.server.Services;
import co.server.common.Constants;
import co.server.common.util.RedisKeyUtil;
import co.server.context.ApplicationContextUtil;
import org.redisson.api.RSet;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.redisson.api.listener.MessageListener;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class RedisRegistryProcesser implements RegistryProcesser {

    private Map<String, Set<String>> serviceList = new ConcurrentHashMap<>();

    private String serviceName;

    private String address;

    private Set<String> refServiceNames;

    private  RedisKeyUtil redisKeyUtil = (RedisKeyUtil) ApplicationContextUtil.getBean("redisKeyUtil");

    private RedissonClient redissonClient = (RedissonClient) ApplicationContextUtil.getBean("coRedissonClient");;

    public RedisRegistryProcesser(String serviceName, Set<String> refServiceNames, String localIp) {
        this.serviceName = serviceName;
        this.address = localIp;
        this.refServiceNames = refServiceNames;
    }

    /**
     * 注册服务
     *
     */
    public void register() {
        RSet<String> set = redissonClient.getSet(redisKeyUtil.getKey(Constants.PROVIDER, serviceName));
        set.add(address);
        RTopic<String> topic = redissonClient.getTopic(redisKeyUtil.getKey(serviceName));
        topic.publish("register");
    }

    /**
     * 移除服务
     *
     */
    public void unRegister() {
        RSet<String> set = redissonClient.getSet(redisKeyUtil.getKey(Constants.PROVIDER, serviceName));
        set.remove(address);
        RTopic<String> topic = redissonClient.getTopic(redisKeyUtil.getKey(serviceName));
        topic.publish("unRegister");
    }

    /**
     * 订阅服务
     *
     */
    public void subscribe() {
        if (refServiceNames == null) return;

        for (String serviceName : refServiceNames) {
            RTopic<String> topic = redissonClient.getTopic(redisKeyUtil.getKey(serviceName));
            topic.addListener(new MessageListener<String>() {
                @Override
                public void onMessage(String channel, String message) {
                    RSet list = redissonClient.getSet(redisKeyUtil.getKey(Constants.PROVIDER, serviceName));
                    if (list != null) {
                        serviceList.put(serviceName, list);

                        //通知services刷新服务列表
                        ((Services) ApplicationContextUtil.getBean("services")).refreshServices(serviceName);
                    }
                }
            });

            RSet list = redissonClient.getSet(redisKeyUtil.getKey(Constants.PROVIDER, serviceName));
            if (list != null) {
                serviceList.put(serviceName, list);
            }
            RSet<String> set = redissonClient.getSet(redisKeyUtil.getKey(Constants.CONSUMER, serviceName));
            set.add(address);
        }
    }

    /**
     * 取消订阅
     */
    public void unSubscribe() {
        if (refServiceNames == null) return;
        for (String serviceName : refServiceNames) {
            RSet<String> set = redissonClient.getSet(redisKeyUtil.getKey(Constants.CONSUMER, serviceName));
            set.remove(address);
        }
    }

    /**
     * 获取当前的服务列表
     */
    public Map<String, Set<String>> getServerList() {
        return this.serviceList;
    }
}
