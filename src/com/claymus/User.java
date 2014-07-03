package com.claymus;

import java.util.Date;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import com.google.appengine.api.NamespaceManager;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

@PersistenceCapable(detachable = "true")
public class User extends PersistentCapable {

	private static final long serialVersionUID = -652439634710815324L;

	@Persistent(column = "ID", primaryKey = "true")
	private String id;

	@Persistent(column = "Name")
	private String name;

	@Persistent(column = "Email")
	private String email;

	@Persistent(column = "Role")
	private Key roleKey;

	@Persistent(column = "Register")
	private Date register;

	@Persistent(column = "LastLogin")
	private Date lastLogin;

	/*
	 * CONSTRUCTORS
	 */

	public User(String id, String name, String email, UserRole role) {
		this.id = id;
		this.name = name;
		this.email = email == null ? null : email.toLowerCase();
		this.roleKey = role.getKey();
		this.register = new Date();
		this.lastLogin = this.register;
	}

	/*
	 * Inherited Methods
	 */

	@Override
	public Key getKey() {
		String namespace = NamespaceManager.get();
		NamespaceManager.set(null);
		Key key = KeyFactory.createKey(User.class.getSimpleName(), this.id);
		NamespaceManager.set(namespace);
		return key;
	}

	@Override
	public boolean equals(Object obj) {
		if(this == obj) {
			return true;
		} else if(obj instanceof User) {
			User user = (User) obj;
			return user.getKey().equals(this.getKey());
		} else {
			return false;
		}
	}

	/*
	 * Getter and Setter Methods
	 */

	public String getName() {
		return this.name;
	}

	public String getEmail() {
		return this.email;
	}

	public UserRole getRole() {
		return UserData.getUserRole(this.roleKey);
	}

	public void setRole(UserRole userRole) {
		this.roleKey = userRole.getKey();
	}

	public Date getRegister() {
		return this.register;
	}

	public Date getLastLogin() {
		return this.lastLogin;
	}

	public void setLastLogin(Date lastLogin) {
		this.lastLogin = lastLogin;
	}

}
