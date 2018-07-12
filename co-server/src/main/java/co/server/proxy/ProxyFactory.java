package co.server.proxy;

import co.server.Services;
import co.server.common.util.MethodReflectUtil;
import co.server.context.ApplicationContextUtil;
import co.server.pack.Request;
import co.server.transport.Client;
import co.server.transport.CoChannel;
import co.server.transport.CoFuture;
import com.alibaba.fastjson.JSON;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

public class ProxyFactory implements InvocationHandler {

    private String serverName;

    private String beanName;

    public static Object newMapperProxy(String targetName, String serverName, String beanName) throws ClassNotFoundException {
        Class mapperInterface = Class.forName(targetName, true, Thread.currentThread().getContextClassLoader());
        Class<?>[] interfaces = new Class[]{mapperInterface};

        ProxyFactory pf = new ProxyFactory();
        pf.setServerName(serverName);
        pf.setBeanName(beanName);
        return Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), interfaces, pf);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args)
            throws Throwable {
        Request request = new Request();
        request.setCmd(this.serverName + "\\" + this.getServiceName() + "::" + method.getName());
        request.setId(Request.getAtomicId());
        Map param = new HashMap();
        String[] parameterNames = MethodReflectUtil.getMethodParameterNamesByAnnotation(method);
        if (parameterNames.length > 0) {
            for (int j = 0; j < parameterNames.length; j++) {
                param.put(parameterNames[j], args[j]);
            }
        }
        request.setData(JSON.toJSONString(param));

        return ((Services) ApplicationContextUtil.getBean("services")).call(this.serverName, request);
    }

    public String getServiceName() {
        return this.beanName.substring(0, this.beanName.length() - 7);
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }
}
