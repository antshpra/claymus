package com.claymus;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import com.google.appengine.api.datastore.Key;

/*
 * Thread-Safe: Synchronized class methods. Member methods need not to be synchronized.
 */
public class Localcache<T extends Serializable> {

	private static final int cacheSize = 1000; // TODO : set this value on system property

	private static final int cacheExpiry = 900000; // 15 mins TODO : set this value on system property

	@SuppressWarnings("serial")
	private static final LinkedHashMap<Key, Localcache<?>> cache
			= new LinkedHashMap<Key, Localcache<?>>((int) Math.ceil(Localcache.cacheSize/0.75f) + 1, 0.75f, true) {

		@Override
		protected boolean removeEldestEntry(Map.Entry<Key, Localcache<?>> eldest) {
			return size() > Localcache.cacheSize;
		}

	};


	private byte[] bytes;
	private long timestamp;

	private Localcache(T obj) {
		setObject(obj);
		this.timestamp = new Date().getTime();
	}

	/*
	 * Getters and Setters
	 */

	@SuppressWarnings("unchecked")
	private T getObject() {
		T obj = null;

		if(this.bytes != null) {
			ByteArrayInputStream bStream = new ByteArrayInputStream(this.bytes);
			ObjectInputStream oStream = null;

			try {
				oStream = new ObjectInputStream(bStream);
				obj = (T) oStream.readObject();
				oStream.close();
			} catch (IOException ex) {
				Logger.get().warning(ex.getMessage());
			} catch (ClassNotFoundException ex) {
				Logger.get().warning(ex.getMessage());
			}
		}

		return obj;
	}

	private void setObject(T obj) {
		ByteArrayOutputStream bStream = new ByteArrayOutputStream();
		ObjectOutputStream oStream;

		try {
			oStream = new ObjectOutputStream(bStream);
			oStream.writeObject(obj);
			oStream.close();
		} catch (IOException ex) {
			Logger.get().warning(ex.getMessage());
			this.bytes = null;
			return;
		}

		this.bytes = bStream.toByteArray();
	}

	/*
	 * Helper Methods
	 */

	@SuppressWarnings("unchecked")
	public synchronized static <T extends Serializable> T get(Key key) {
		Localcache<T> cache = (Localcache<T>) Localcache.cache.get(key);
		if(cache != null && cache.timestamp + Localcache.cacheExpiry >= new Date().getTime())
			return cache.getObject();
		else
			return null;
	}

	@SuppressWarnings("unchecked")
	public synchronized static <T extends Serializable> void put(Key key, T obj) {
		Localcache<T> cache = (Localcache<T>) Localcache.cache.get(key);
		if(cache != null) {
			cache.setObject(obj);
			cache.timestamp = new Date().getTime();
		} else {
			Localcache.cache.put(key, new Localcache<T>(obj));
		}
	}

	public synchronized static void remove(Key key) {
		Localcache.cache.remove(key);
	}

	public synchronized static void flush() {
		Localcache.cache.clear();
	}

}
