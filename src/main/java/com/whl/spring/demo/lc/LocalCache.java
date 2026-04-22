package com.whl.spring.demo.lc;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LocalCache {
    private static final Map<String, Object> LOCAL_CACHE = new ConcurrentHashMap<>();

    public Object get(String key) {
        return LOCAL_CACHE.get(key);
    }

    public void set(String key, Object value) {
        LOCAL_CACHE.put(key, value);
    }

    public void del(String key) {
        LOCAL_CACHE.remove(key);
    }

    public Map<String, Object> getAll() {
        return new HashMap<>(LOCAL_CACHE);
    }

    public void clear() {
        LOCAL_CACHE.clear();
    }

}
