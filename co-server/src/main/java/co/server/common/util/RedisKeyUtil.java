package co.server.common.util;

public class RedisKeyUtil {

    private String prefix = "co";

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getKey(String... args)
    {
        StringBuffer key = new StringBuffer(prefix);
        for (String arg : args) {
            key.append(":").append(arg);
        }

        return key.toString();
    }
}
