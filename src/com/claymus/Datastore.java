package com.claymus;

import java.util.logging.Level;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.apphosting.api.ApiProxy.CapabilityDisabledException;

/*
 * Quota Details: http://code.google.com/appengine/docs/quotas.html#Datastore
 * .---------------------.--------------------------.
 * |					 | Free & Billing Enabled   |
 * | Resource			 |-------------.------------|
 * |					 | Daily	   | Per Minute |
 * |---------------------|-------------|------------|
 * | Datastore API Calls | 141,241,791 |  	784,676 |
 * | Datastore Queries	 | 417,311,168 |  2,318,395 |
 * '---------------------'-------------'------------'
 *
 * ASSUMPTION : "Datastore API Calls" and "Datastore Queries" will never reach
 * 			    its quota limit. Hence, no need to handle "OverQuotaException".
 */

/*
 * Thread-Safe: No global data
 */
public class Datastore {

	/*
	 * Get Entity
	 */

	public static <T extends PersistentCapable> T getEntity(Class<T> entityClass, long id) {
		return Datastore.getEntity(entityClass, KeyFactory.createKey(entityClass.getSimpleName(), id));
	}

	public static <T extends PersistentCapable> T getEntity(Class<T> entityClass, String name) {
		return Datastore.getEntity(entityClass, KeyFactory.createKey(entityClass.getSimpleName(), name));
	}

	public static <T extends PersistentCapable> T getEntity(Class<T> entityClass, Key key) {
		T entity = null;

		switch(0) {
			case 0: // Checking Localcache
				entity = Localcache.get(key);
				if(entity != null) {
//					System.err.println(entity.getKey() + " found in local cache");
					break;
				}
			case 1: // Checking Memcache
				entity = Memcache.get(key);
				if(entity != null) {
//					System.err.println(entity.getKey() + " found in memcache");
					if(entity.cacheLocally())
						Localcache.put(key, entity);
					break;
				}
			case 2: // Checking Datastore
				PersistenceManager pm = PMF.get();
				try {
					entity = pm.getObjectById(entityClass, key);
					entity.detachFields(pm);
					entity = pm.detachCopy(entity);
//					System.err.println(entity.getKey() + " found in datastore");
				} catch (JDOObjectNotFoundException ex) {
					Logger.get().warning(ex.getMessage());
					break;
				} finally {
					pm.close();
				}
				if(entity.cache())
					Memcache.put(key, entity);
				if(entity.cacheLocally())
					Localcache.put(key, entity);
		}

		return entity;
	}

	/*
	 * Saving entity
	 */

	public static <T extends PersistentCapable> T makePersistent(T entity) {
		PersistenceManager pm = PMF.get();
		T detachedCopy = null;

		try {
			pm.makePersistent(entity);
			entity.detachFields(pm);
			detachedCopy = pm.detachCopy(entity);
		} catch(CapabilityDisabledException ex) {
			Logger.get().log(Level.SEVERE, null, ex);
			return null;
		} finally {
			pm.close();
		}

		if(entity.cache())
			Memcache.put(entity.getKey(), detachedCopy);
		if(entity.cacheLocally())
			Localcache.put(entity.getKey(), detachedCopy);

		return detachedCopy;
	}

	/*
	 * Deleting entity
	 */

	public static <T extends PersistentCapable> boolean makeTrasient(T entity) {
		return Datastore.makeTrasient(entity.getClass(), entity.getKey());
	}

	public static boolean makeTrasient(Class<? extends PersistentCapable> entityClass, long id) {
		return Datastore.makeTrasient(entityClass, KeyFactory.createKey(entityClass.getSimpleName(), id));
	}

	public static boolean makeTrasient(Class<? extends PersistentCapable> entityClass, String name) {
		return Datastore.makeTrasient(entityClass, KeyFactory.createKey(entityClass.getSimpleName(), name));
	}

	public static boolean makeTrasient(Class<? extends PersistentCapable> entityClass, Key key) {
		PersistenceManager pm = PMF.get();
		PersistentCapable entity = null;

		try {
			entity = pm.getObjectById(entityClass, key);
			pm.deletePersistent(entity);
		} catch (JDOObjectNotFoundException ex) {
			Logger.get().warning(ex.getMessage());
			return true;
		} catch(CapabilityDisabledException ex) {
			Logger.get().log(Level.SEVERE, null, ex);
			return false;
		} finally {
			pm.close();
		}

		if(entity.cache())
			Memcache.remove(key);
		if(entity.cacheLocally())
			Localcache.remove(key);

		return true;
	}

}
