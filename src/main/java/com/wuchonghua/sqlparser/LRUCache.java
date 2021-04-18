package com.wuchonghua.sqlparser;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wuchonghua
 * @date 2021/4/15 11:36
 */
public enum LRUCache {

    /**
     * 使用枚举实现单例
     */
    CACHE(20);

    private class LRU<K, V> extends LinkedHashMap<K, V> {
        int capacity;
        public LRU(int capacity) {
            super(capacity,0.75f,true);
            this.capacity = capacity;
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry<K, V> eldest){
            return size() > capacity;
        }
    }

    private LRU<String, List<List<Condition>>> map;

    private LRUCache(int capacity) {
        this.map = new LRU(capacity);
    }

    public List<List<Condition>> get(String key) {
        return this.map.get(key);
    }

    public void put(String key, List<List<Condition>> value) {
        this.map.put(key, value);
    }
}

