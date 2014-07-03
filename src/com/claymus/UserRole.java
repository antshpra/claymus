package com.claymus;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import com.google.appengine.api.NamespaceManager;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

@PersistenceCapable(detachable = "true")
public class UserRole extends PersistentCapable {

	private static final long serialVersionUID = -5050784822497346803L;

	@Persistent(column = "Key", primaryKey = "true")
	private Key key;

	@Persistent(column = "Name")
	private String name;

	/*
	 * Constructors
	 */

	public UserRole(String keyName, String name){
		String namespace = NamespaceManager.get();
		NamespaceManager.set(null);
		this.key = KeyFactory.createKey(UserRole.class.getSimpleName(), keyName);
		NamespaceManager.set(namespace);
		this.name = name;
	}

	/*
	 * Inherited Methods
	 */

	@Override
	public boolean equals(Object obj) {
		if(this == obj)
			return true;
		else if(obj instanceof UserRole)
			return ((UserRole) obj).getKey().equals(this.getKey());
		else
			return false;
	}

	/*
	 * Getter and Setter methods
	 */

	@Override
	public Key getKey() {
		return this.key;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
