package co.server.registry;

import co.server.common.Constants;
import co.server.common.util.RedisKeyUtil;
import co.server.context.ApplicationContextUtil;
import org.redisson.api.RSet;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;

public class RedisRegistryProcesser implements RegistryProcesser {

    private String serviceName;

    private String address;

    private RedissonClient redissonClient = (RedissonClient) ApplicationContextUtil.getBean("coRedissonClient");;

    public RedisRegistryProcesser(String serviceName, String localIp) {
        this.serviceName = serviceName;
        this.address = localIp;
    }

    /**
     * 注册服务
     *
     * @return boolean
     */
    public void register() {
        RSet<String> set = redissonClient.getSet(RedisKeyUtil.getKey(Constants.PROVIDER, serviceName));
        set.add(address);
        RTopic<String> topic = redissonClient.getTopic(serviceName);
        topic.publish("register");
    }

    /**
     * 移除服务
     *
     * @return boolean
     */
    public void unRegister() {
        RSet<String> set = redissonClient.getSet(RedisKeyUtil.getKey(Constants.PROVIDER, serviceName));
        set.remove(address);
        RTopic<String> topic = redissonClient.getTopic(serviceName);
        topic.publish("unRegister");
    }

    /**
     * 订阅服务
     *
     * @return obj swoole_process|null
     */
    public void subscribe() {

    }

    /**
     * 取消订阅
     */
    public void unSubscribe() {

    }

    /**
     * 获取当前的服务列表
     */
    public void getServerList() {

    }
}
