package com.claymus.site;

import java.util.LinkedList;
import java.util.List;

import javax.jdo.annotations.Discriminator;
import javax.jdo.annotations.DiscriminatorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import com.claymus.PersistentCapable;
import com.claymus.User;
import com.claymus.UserData;
import com.claymus.UserRole;
import com.claymus.site.module.content.ContentType;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

/*
 * Thread-Safe: Synchronized member methods (wherever required).
 */
@SuppressWarnings("serial")
@PersistenceCapable(detachable = "true")
@Discriminator(strategy = DiscriminatorStrategy.NONE)
public abstract class Module extends PersistentCapable implements Comparable<Module> {

	public static final String MODULE_PATH = "/com/claymus/site/module/";

	public static final String MODULE_PACKAGE = "com.claymus.site.module";

	public static final String MODULE_SETTINGS_CLASS = "ModuleHelper";


	@Persistent(column = "ID", primaryKey = "true")
	private String id;

	@Persistent(column = "AL_1")
	private LinkedList<Key> accessLevel_1;

	@Persistent(column = "AL_2")
	private LinkedList<Key> accessLevel_2;

	@Persistent(column = "AL_3")
	private LinkedList<Key> accessLevel_3;

	@Persistent(column = "AL_4")
	private LinkedList<Key> accessLevel_4;

	@Persistent(column = "AL_5")
	private LinkedList<Key> accessLevel_5;

	public static final int NO_ACCESS = 0;

	public static final int FULL_ACCESS = 5;

	/*
	 * Constructors
	 */

	public Module() {
		this.id = this.getClass().getPackage().getName().substring(Module.MODULE_PACKAGE.length() + 1);
		this.accessLevel_1 = new LinkedList<Key>();
		this.accessLevel_2 = new LinkedList<Key>();
		this.accessLevel_3 = new LinkedList<Key>();
		this.accessLevel_4 = new LinkedList<Key>();
		this.accessLevel_5 = new LinkedList<Key>();
	}

	/*
	 * Inherited Methods
	 */

	@Override
	public final Key getKey() {
		return KeyFactory.createKey(Module.class.getSimpleName(), this.id);
	}

	@Override
	public int compareTo(Module module) {
		return this.getId().compareTo(module.getId());
	}

	/*
	 * Getters and Setters
	 */

	public String getId() {
		return this.id;
	}

	public final synchronized int getAccessLevel(UserRole userRole) {
		if(userRole.equals(UserData.getAdministratorRole()))
			return getFullAccessLevel();

		Key userRoleKey = userRole.getKey();

		if(this.accessLevel_1.contains(userRoleKey))
			return 1;
		else if(this.accessLevel_2.contains(userRoleKey))
			return 2;
		else if(this.accessLevel_3.contains(userRoleKey))
			return 3;
		else if(this.accessLevel_4.contains(userRoleKey))
			return 4;
		else if(this.accessLevel_5.contains(userRoleKey))
			return 5;
		else
			return getDefaultAccessLevel();
	}

	public final synchronized void setAccessLevel(UserRole userRole, int accessLevel) {
		if(userRole.equals(UserData.getAdministratorRole()))
			return;

		Key userRoleKey = userRole.getKey();

		if(accessLevel == 1)
			this.accessLevel_1.add(userRoleKey);
		else if(this.accessLevel_1.remove(userRoleKey))
			this.accessLevel_1 = this.accessLevel_1.size() == 0 ? new LinkedList<Key>() : this.accessLevel_1;

		if(accessLevel == 2)
			this.accessLevel_2.add(userRoleKey);
		else if(this.accessLevel_2.remove(userRoleKey))
			this.accessLevel_2 = this.accessLevel_2.size() == 0 ? new LinkedList<Key>() : this.accessLevel_2;

		if(accessLevel == 3)
			this.accessLevel_3.add(userRoleKey);
		else if(this.accessLevel_3.remove(userRoleKey))
			this.accessLevel_3 = this.accessLevel_3.size() == 0 ? new LinkedList<Key>() : this.accessLevel_3;

		if(accessLevel == 4)
			this.accessLevel_4.add(userRoleKey);
		else if(this.accessLevel_4.remove(userRoleKey))
			this.accessLevel_4 = this.accessLevel_4.size() == 0 ? new LinkedList<Key>() : this.accessLevel_4;

		if(accessLevel == 5)
			this.accessLevel_5.add(userRoleKey);
		else if(this.accessLevel_5.remove(userRoleKey))
			this.accessLevel_5 = this.accessLevel_5.size() == 0 ? new LinkedList<Key>() : this.accessLevel_5;
	}

	/*
	 * Methods to be implemented/overridden by inheriting class
	 */

	public abstract double getVersion();

	public abstract String getName();

	public abstract String getDescription();


	public String[] getAccessLevelNames() {
		return new String[] {};
	}

	protected int getDefaultAccessLevel() {
		return Module.NO_ACCESS;
	}

	protected int getFullAccessLevel() {
		return Module.FULL_ACCESS;
	}


	protected List<ContentType> getPageContents(String[] tokens, User user) {
		LinkedList<ContentType> contentsData = new LinkedList<ContentType>();
		ContentType contentData = getPageContent(tokens, user);
		if(contentData != null)
			contentsData.add(contentData);
		return contentsData;
	}

	protected ContentType getPageContent(String[] tokens, User user) {
		return null;
	}

}
