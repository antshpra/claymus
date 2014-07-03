package com.claymus;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import javax.cache.Cache;
import javax.cache.CacheException;
import javax.cache.CacheManager;

import com.google.appengine.api.memcache.InvalidValueException;
import com.google.appengine.api.memcache.stdimpl.GCacheFactory;

/*
 * Quota Details: http://code.google.com/appengine/docs/quotas.html#Memcache
 * .--------------------.--------------------------.
 * |					| Free & Billing Enabled   |
 * | Resource			|-------------.------------|
 * |					| Daily		  | Per Minute |
 * |--------------------|-------------|------------|
 * | Memcache API Calls | 192,672,000 |  1,070,400 |
 * '--------------------'-------------'------------'
 *
 * ASSUMPTION : "Memcache API Calls" will never reach its quota limit. Hence, no
 * 				need to handle "OverQuotaException".
 */

/*
 * Thread-Safe: No Global Data.
 */
public class Memcache {

	// GETTING DATA FROM MEMCACHE

	@SuppressWarnings("unchecked")
	public static <K, T extends Serializable> T get(K key) {
		try {
			Cache cache = CacheManager.getInstance().getCacheFactory()
					.createCache(Collections.emptyMap());
			return (T) cache.get(key);
		} catch (InvalidValueException ex) {
			Logger.get().log(Level.WARNING, null, ex);
			return null;
		} catch (CacheException ex) {
			Logger.get().log(Level.SEVERE, null, ex);
			return null;
		}
	}


	// PUTTING DATA TO MEMCACHE

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <K, T extends Serializable> void put(K key, T value, long expirationDeltaMillis) {
		if(expirationDeltaMillis <= 0)
			return;

		Map props = new HashMap();
		props.put(GCacheFactory.EXPIRATION_DELTA_MILLIS, expirationDeltaMillis);

		try {
			Cache cache = CacheManager.getInstance().getCacheFactory()
					.createCache(props);
			cache.put(key, value);
		} catch (CacheException ex) {
			Logger.get().log(Level.SEVERE, null, ex);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <K, T extends Serializable> void put(K key, T value) {
		Map props = Collections.emptyMap();

		try {
			Cache cache = CacheManager.getInstance().getCacheFactory()
					.createCache(props);
			cache.put(key, value);
		} catch (CacheException ex) {
			Logger.get().log(Level.SEVERE, null, ex);
		}
	}


	// REMOVE A MEMCACHE ENTRY

	public static <K> void remove(K key) {
		try {
			Cache cache = CacheManager.getInstance().getCacheFactory()
					.createCache(Collections.emptyMap());
			cache.remove(key);
		} catch (CacheException ex) {
			Logger.get().log(Level.SEVERE, null, ex);
		}
	}


	// FLUSHING MEMCACHE DATA

	public static void flush() {
		try {
			Cache cache = CacheManager.getInstance().getCacheFactory()
					.createCache(Collections.emptyMap());
			cache.clear();
		} catch (CacheException ex) {
			Logger.get().log(Level.SEVERE, null, ex);
		}
	}

}
