package co.server.co;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import co.server.context.ApplicationContextUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import co.server.pack.Data;
import co.server.pack.Response;
import co.server.common.util.MethodReflectUtil;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import java.lang.reflect.Method;
import java.lang.reflect.Type;


public class CoServerHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LogManager.getLogger(CoServerHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        // Discard the received data silently.
        ByteBuf in = (ByteBuf) msg;

        String info = in.toString(CharsetUtil.UTF_8);
        logger.debug("info = " + info);
        if (info.equals("ping")) {
            ctx.writeAndFlush("pong");
        } else {
            Data data = JSON.parseObject(info, Data.class);
            Object res = invoke(data.getCmd(), data.getData());
            if (res == null) {
                res = new Integer(0);
            }

            Response response = new Response();
            response.setCmd(data.getCmd());
            response.setData(res);
            ctx.writeAndFlush(JSON.toJSONString(response));
        }

        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }

    private Object invoke(String cmd, String data) {
        if (cmd == null) {
            return null;
        }

        try {
            String[] serviceAndMethod = cmd.split("::");
            String[] groupAndService = serviceAndMethod[0].split("\\\\");

            checkServiceName(groupAndService[0]);

            String className = groupAndService[1].toLowerCase() + "Service";
            Object servObj = ApplicationContextUtil.getBean(className);
            String methodName = serviceAndMethod[1];
            Class service = servObj.getClass();
            Method[] methods = service.getMethods();
            for (int i = 0; i < methods.length; i++) {
                if (methodName.equals(methods[i].getName())) {
                    String[] parameterNames = MethodReflectUtil.getMethodParameterNamesByAnnotation(methods[i]);

                    if (parameterNames.length > 0) {
                        JSONObject jsonObj = JSON.parseObject(data);
                        Object[] args = new Object[parameterNames.length];
                        // Object[] rargs = new Object[parameterNames.length];
                        Type[] parameterTypes = methods[i].getParameterTypes();

                        for (int j = 0; j < parameterNames.length; j++) {
                            if (!jsonObj.containsKey(parameterNames[j])) {
                                return null;
                            }

                            args[j] = jsonObj.getObject(parameterNames[j], parameterTypes[j]);
                        }
                        return methods[i].invoke(servObj, args);
                    } else {
                        return methods[i].invoke(servObj);
                    }
                }
            }
        } catch (Exception e) {
            return null;
        }

        return null;
    }

    private void checkServiceName(String serverName) throws Exception {
        CoServer server = (CoServer) ApplicationContextUtil.getBean("groupCoServer");
        if (!server.getServiceName().equals(serverName)) {
            throw new Exception("error serverName");
        }
    }
}