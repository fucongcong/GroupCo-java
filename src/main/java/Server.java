import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;

import Core.Data;
import Core.Param;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class Server {

    protected static ServerSocket server;

    protected static int port = 9394;

    public static void main(String[] args) {
        run();
    }

    public static void run() {
        try {
            server = new ServerSocket(port);

            //while (true) {
            Socket client = server.accept();

            Data data = read(client);

            String res = invoke(data.getCmd(), data.getData(), client);
            if (res == null) {
                res = "0";
            }

            Data response = new Data();
            response.setCmd(data.getCmd());
            response.setData(res);
            res = JSON.toJSONString(response);
            DataOutputStream out = new DataOutputStream(client.getOutputStream());
            out.writeInt(res.length());
            out.writeBytes(res);

            close(client);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void close(Socket client) {
        try {
            client.shutdownInput();
            client.close();
            server.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Data read(Socket client) {
        try {
            BufferedInputStream is = new BufferedInputStream(client.getInputStream());
            byte[] buf = new byte[4];
            is.read(buf, 0, 4);

            int bodylEN = Integer.parseInt(bytesToHexString(buf), 16);
            byte[] body = new byte[bodylEN];
            is.read(body, 0, bodylEN);

            String bodyData = byteArrayToStr(body);
            return JSON.parseObject(bodyData, Data.class);
        } catch (IOException e) {
            return null;
        }
    }

    public static String invoke(String cmd, String data, Socket client) {
        if (cmd == null) {
            return null;
        }
        String[] serviceAndMethod = cmd.split("::");

        String className = "Service." + uCfirst(serviceAndMethod[0]) + "Service";
        String methodName = serviceAndMethod[1];
        Class service = null;
        Method m = null;

        try {
            service = Class.forName(className);
            Method[] methods = service.getMethods();
            for (int i = 0; i < methods.length; i++) {
                if (methodName.equals(methods[i].getName())) {
                    Object servobj = service.newInstance();
                    String[] parameterNames = getMethodParameterNamesByAnnotation(methods[i]);

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
                        return methods[i].invoke(servobj, null).toString();
                    }
                }
            }
        } catch (Exception e) {
            return null;
        }

        return null;
    }

    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    public static String byteArrayToStr(byte[] byteArray) {
        if (byteArray == null) {
            return null;
        }
        String str = new String(byteArray);
        return str;
    }

    public static String uCfirst(String str)
    {
        return str.replaceFirst(str.substring(0, 1),str.substring(0, 1).toUpperCase()) ;
    }

    public static String[] getMethodParameterNamesByAnnotation(Method method) {
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        if (parameterAnnotations == null || parameterAnnotations.length == 0) {
            return null;
        }
        String[] parameterNames = new String[parameterAnnotations.length];
        int i = 0;
        for (Annotation[] parameterAnnotation : parameterAnnotations) {
            for (Annotation annotation : parameterAnnotation) {
                if (annotation instanceof Param) {
                    Param param = (Param) annotation;
                    parameterNames[i++] = param.value();
                }
            }
        }
        return parameterNames;
    }
}
