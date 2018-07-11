package co.server.registry;

import java.util.Map;
import java.util.Set;

public interface RegistryProcesser {

    /**
     * 注册服务
     *
     * @return boolean
     */
    public void register();

    /**
     * 移除服务
     *
     * @return boolean
     */
    public void unRegister();

    /**
     * 订阅服务
     *
     * @return obj swoole_process|null
     */
    public void subscribe();

    /**
     * 取消订阅
     */
    public void unSubscribe();

    /**
     * 获取当前的服务列表
     */
    public Map<String, Set<String>> getServerList();
}
