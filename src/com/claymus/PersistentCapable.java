package com.claymus;

import java.io.Serializable;

import javax.jdo.PersistenceManager;

import com.google.appengine.api.datastore.Key;

@SuppressWarnings("serial")
public abstract class PersistentCapable implements Serializable {

	public abstract Key getKey();

	public boolean cache() {
		return true;
	}

	public boolean cacheLocally() {
		return this.cache();
	}

	protected void detachFields(PersistenceManager pm) {}

}