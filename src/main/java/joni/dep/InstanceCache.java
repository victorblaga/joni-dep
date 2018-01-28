package joni.dep;

import java.util.HashMap;
import java.util.Map;

public class InstanceCache {
    private final Map<String, Object> cache;

    public InstanceCache() {
        this.cache = new HashMap<>();
    }

    public boolean contains(Class klass, String qualifier) {
        return this.cache.containsKey(key(klass, qualifier));
    }

    public Object get(Class klass, String qualifier) {
        return this.cache.get(key(klass, qualifier));
    }

    public void put(Class klass, String qualifier, Object instance) {
        this.cache.put(key(klass, qualifier), instance);
    }

    private String key(Class klass, String qualifier) {
        return String.format("%s:%s", klass.getCanonicalName(), qualifier);
    }
}
