package co.server.common.util;

public class RedisKeyUtil {

    private static String prefix;

    public static void setPrefix(String prefix) {
        RedisKeyUtil.prefix = prefix;
    }

    public static String getKey(String... args)
    {
        StringBuffer key = new StringBuffer(prefix);
        for (String arg : args) {
            key.append(":").append(arg);
        }

        return key.toString();
    }
}
