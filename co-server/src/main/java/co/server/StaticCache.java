package co.server;

import java.util.Hashtable;

public class StaticCache {

    protected static Hashtable store = new Hashtable();

    /**
     * 设置一个静态缓存
     *
     * @param key   String
     * @param value Object
     */
    public synchronized static void set(String key, Object value) {
        StaticCache.store.put(key, value);
    }

    public static Object get(String key) {
        return StaticCache.store.get(key);
    }

    public synchronized static void flush() {
        StaticCache.store.clear();
    }
}
