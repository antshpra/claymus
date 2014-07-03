package com.claymus.site;

import java.io.Serializable;
import java.util.Date;
import java.util.logging.Level;

import javax.jdo.JDOException;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import com.claymus.Datastore;
import com.claymus.Logger;
import com.claymus.PersistentCapable;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

/*
 * Thread-Safe: Immutable class
 */
@PersistenceCapable(detachable = "true")
public class Property extends PersistentCapable {

	private static final long serialVersionUID = -8253253625211038330L;

	@Persistent(column = "Key", primaryKey = "true")
	private String key;

	@Persistent(column = "Value", serialized = "true", defaultFetchGroup = "true")
	private Object value;

	@Persistent(column = "Expiry")
	private Long expiry;

	@Persistent(column = "CacheLocally")
	private Boolean cacheLocally;

	/*
	 * Constructor(s)
	 */

	private Property(String key, Serializable value, long expirationDeltaMillis, boolean cacheLocally){
		this.key = key;
		this.value = value;
		this.cacheLocally = cacheLocally;
		if(expirationDeltaMillis > 0)
			this.expiry = new Date().getTime() + expirationDeltaMillis;
	}

	/*
	 * Inherited Method(s)
	 */

	@Override
	public Key getKey() {
		return KeyFactory.createKey(Property.class.getSimpleName(), this.key);
	}

	@Override
	public boolean cacheLocally() {
		return this.cacheLocally;
	}

	/*
	 * Helper Methods
	 */

	@SuppressWarnings("unchecked")
	public static <T extends Serializable> T get(String key) {
		Property property = null;
		try {
			property = Datastore.getEntity(Property.class, key);
		} catch(JDOException ex) {
			Logger.get().log(Level.SEVERE, null, ex);
		}

		if (property != null && (property.expiry == null || property.expiry >= new Date().getTime()))
			return (T) property.value;
		else
			return null;
	}


	public static boolean put(String key, Serializable value) {
		return Property.put(key, value, true);
	}

	public static boolean put(String key, Serializable value, boolean cacheLocally) {
		return Datastore.makePersistent(new Property(key, value, -1, cacheLocally)) != null;
	}

	public static boolean put(String key, Serializable value, long expirationDeltaMillis) {
		return Property.put(key, value, expirationDeltaMillis, true);
	}

	public static boolean put(String key, Serializable value, long expirationDeltaMillis, boolean cacheLocally) {
		if(expirationDeltaMillis >= 0)
			return Datastore.makePersistent(new Property(key, value, expirationDeltaMillis, cacheLocally)) != null;
		else
			return false;
	}


	public static boolean remove(String key) {
		return Datastore.makeTrasient(Property.class, key);
	}

}
