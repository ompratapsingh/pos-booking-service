package com.pos.booking.cache;

import java.util.WeakHashMap;

public class ApplicationCahce<K, V> {

	private static WeakHashMap<Object, Object> CACHE_MAP;
	static {
		CACHE_MAP = new WeakHashMap<>();
	}

	public void put(K key, V value) {
		synchronized (CACHE_MAP) {
			CACHE_MAP.put(key, value);
		}
	}

	@SuppressWarnings("unchecked")
	public V getValue(K key) {
		synchronized (CACHE_MAP) {
			return (V) CACHE_MAP.get(key);
		}
	}

	public void remove(K key) {
		synchronized (CACHE_MAP) {
			if (CACHE_MAP.get(key) != null) {
				CACHE_MAP.remove(key);
			}

		}
	}
}
