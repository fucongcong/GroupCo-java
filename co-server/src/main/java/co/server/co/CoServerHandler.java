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
import co.server.util.MethodReflectUtil;

import java.lang.reflect.Method;

public class CoServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        // Discard the received data silently.
        ByteBuf in = (ByteBuf) msg;

        String info = in.toString(CharsetUtil.UTF_8);
        System.out.println("info = " + info);

        Data data = JSON.parseObject(info, Data.class);
        String res = invoke(data.getCmd(), data.getData());
        if (res == null) {
            res = "0";
        }

        Response response = new Response();
        response.setCode(200);
        response.setData(res);
        res = JSON.toJSONString(response);

        ctx.writeAndFlush(res);
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }

    private String invoke(String cmd, String data) {
        if (cmd == null) {
            return null;
        }

        try {
            String[] serviceAndMethod = cmd.split("::");
            String className = serviceAndMethod[0].toLowerCase() + "Service";
            Object servobj = ApplicationContextUtil.getBean(className);
            String methodName = serviceAndMethod[1];
            Class service = servobj.getClass();
            Method[] methods = service.getMethods();
            for (int i = 0; i < methods.length; i++) {
                if (methodName.equals(methods[i].getName())) {
                    String[] parameterNames = MethodReflectUtil.getMethodParameterNamesByAnnotation(methods[i]);

                    if (parameterNames.length > 0) {
                        JSONObject jsonObj = JSON.parseObject(data);
                        Object[] args = new Object[parameterNames.length];
                        for (int j = 0; j < parameterNames.length; j++) {
                            if (!jsonObj.containsKey(parameterNames[j])) {
                                return null;
                            }

                            args[j] = jsonObj.get(parameterNames[j]);
                        }

                        return methods[i].invoke(servobj, args).toString();
                    } else {
                        return methods[i].invoke(servobj).toString();
                    }
                }
            }
        } catch (Exception e) {
            return null;
        }

        return null;
    }
}