package com.co.server.co;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import com.co.server.pack.Data;
import com.co.server.pack.Response;
import com.co.server.util.MethodReflectUtil;
import com.co.server.util.SocketUtil;

import java.lang.reflect.Method;

public class CoServerHandler extends ChannelInboundHandlerAdapter { // (1)

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) { // (2)
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

        ctx.writeAndFlush(res); // (1)
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { // (4)
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }

    private String invoke(String cmd, String data) {
        if (cmd == null) {
            return null;
        }
        String[] serviceAndMethod = cmd.split("::");

        String className = "services.impl." + SocketUtil.uCfirst(serviceAndMethod[0]) + "ServiceImpl";
        String methodName = serviceAndMethod[1];
        Class service = null;
        Method m = null;

        try {
            service = Class.forName(className);
            Method[] methods = service.getMethods();
            for (int i = 0; i < methods.length; i++) {
                if (methodName.equals(methods[i].getName())) {
                    Object servobj = service.newInstance();
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