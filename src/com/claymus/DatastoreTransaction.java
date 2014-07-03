package com.claymus;

import java.util.logging.Level;

import javax.jdo.JDOException;
import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.apphosting.api.ApiProxy.CapabilityDisabledException;

/*
 * Thread-Safe: Immutable class
 */
public class DatastoreTransaction <T extends PersistentCapable> {

	private int retryCount;
	private int retryInterval;

	/*
	 * Constructors
	 */

	public DatastoreTransaction() {
		this.retryCount = 10;
		this.retryInterval = 200;
	}

	public DatastoreTransaction(int retryCount) {
		this.retryCount = retryCount;
		this.retryInterval = 200;
	}

	public DatastoreTransaction(int retryCount, int retryInterval) {
		this.retryCount = retryCount;
		this.retryInterval = retryInterval;
	}

	/*
	 * Helper Methods
	 */

	protected boolean updateEntity(T datastoreEntity, Object... argvs) {
		return true;
	}

	/*
	 * Saving Entity
	 */

	@SuppressWarnings("unchecked")
	public T makePersistent(T entity, Object... argvs) {
		PersistenceManager pm = PMF.get();
		Transaction tx = pm.currentTransaction();
		T datastoreEntity = null;

		try {
			for(int i = 0; i < this.retryCount; i++) {
				if(i != 0)
					Thread.sleep(this.retryInterval);

				try {
					if(entity.getKey() == null) {
						if(! updateEntity(entity, argvs)) // Nothing to update
							return entity;
						tx.begin();
						pm.makePersistent(entity);
						tx.commit();
						datastoreEntity = entity;

					} else {
						tx.begin();
						try {
							datastoreEntity = (T) pm.getObjectById(entity.getClass(), entity.getKey());
							if(! updateEntity(datastoreEntity, argvs)) { // Nothing to update
								datastoreEntity.detachFields(pm);
								return pm.detachCopy(datastoreEntity);
							}
							pm.makePersistent(datastoreEntity);
						} catch(JDOObjectNotFoundException ex) {
							if(! updateEntity(entity, argvs)) // Nothing to update
								return entity;
							pm.makePersistent(entity);
							datastoreEntity = entity;
						}
						tx.commit();
					}

					datastoreEntity.detachFields(pm);
					entity = pm.detachCopy(datastoreEntity);

					if(entity.cache())
						Memcache.put(entity.getKey(), entity);
					if(entity.cacheLocally())
						Localcache.put(entity.getKey(), entity);

					return entity;

		//		} catch(JDODataStoreException ex) {
				} catch(JDOException ex) {
					Logger.get().log(Level.SEVERE, null, ex);
					if(tx.isActive()) {
						tx.rollback();
					}
				}
			}

		} catch(CapabilityDisabledException ex) {
			Logger.get().log(Level.SEVERE, null, ex);
		} catch (InterruptedException ex) {
			Logger.get().log(Level.SEVERE, null, ex);
		} finally {
			if(tx.isActive()) {
				tx.rollback();
			}
			pm.close();
		}

		return null;
	}


	public T makePersistent(Class<T> klass, long id, Object... argvs) {
		return makePersistent(klass, KeyFactory.createKey(klass.getSimpleName(), id), argvs);
	}

	public T makePersistent(Class<T> klass, String id, Object... argvs) {
		return makePersistent(klass, KeyFactory.createKey(klass.getSimpleName(), id), argvs);
	}

	public T makePersistent(Class<T> klass, Key key, Object... argvs) {
		PersistenceManager pm = PMF.get();
		Transaction tx = pm.currentTransaction();
		T entity = null;
		T datastoreEntity = null;

		try {
			for(int i = 0; i < this.retryCount; i++) {
				if(i != 0)
					Thread.sleep(this.retryInterval);

				try {
					tx.begin();
					try {
						datastoreEntity = pm.getObjectById(klass, key);
						if(! updateEntity(datastoreEntity, argvs)) { // Nothing to update
							datastoreEntity.detachFields(pm);
							return pm.detachCopy(datastoreEntity);
						}
						pm.makePersistent(datastoreEntity);
					} catch(JDOObjectNotFoundException ex) {
						Logger.get().warning(ex.getMessage());
						return null;
					}
					tx.commit();

					datastoreEntity.detachFields(pm);
					entity = pm.detachCopy(datastoreEntity);

					if(entity.cache())
						Memcache.put(entity.getKey(), entity);
					if(entity.cacheLocally())
						Localcache.put(entity.getKey(), entity);

					return entity;

		//		} catch(JDODataStoreException ex) {
				} catch(JDOException ex) {
					Logger.get().log(Level.SEVERE, null, ex);
					if(tx.isActive()) {
						tx.rollback();
					}
				}
			}

		} catch(CapabilityDisabledException ex) {
			Logger.get().log(Level.SEVERE, null, ex);
		} catch (InterruptedException ex) {
			Logger.get().log(Level.SEVERE, null, ex);
		} finally {
			if(tx.isActive()) {
				tx.rollback();
			}
			pm.close();
		}

		return null;
	}

}
