package com.claymus.site.module.content;

import java.util.Date;
import java.util.LinkedList;

import javax.jdo.PersistenceManager;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import com.claymus.PersistentCapable;
import com.claymus.User;
import com.claymus.UserData;
import com.claymus.site.module.content.gwt.ContentDTO;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

@PersistenceCapable(detachable = "true")
public class Content extends PersistentCapable {

	private static final long serialVersionUID = 1892460672522954442L;

	@Persistent(column = "Key", primaryKey = "true", valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key key;


	@Persistent(column = "ContentData")
	private ContentType contentData;


	@Persistent(column = "Page")
	private Long pageId;

	@Persistent(column = "Location")
	private String location;

	@Persistent(column = "Weight")
	private Long weight;


	// 0 == all, 1 == only to listed ones, -1 == all except listed ones
	@Persistent(column = "Visibility")
	private Integer visibleTo;

	@Persistent(column = "VisibleTo")
	private LinkedList<Key> roleList;


	@Persistent(column = "Created")
	private Date added;

	@Persistent(column = "Creator")
	private Key owner;

	@Persistent(column = "Updated")
	private Date updated;

	@Persistent(column = "Updater")
	private Key updater;

	/*
	 * Constructors
	 */

	public Content(ContentType contentData, long pageId, String location) {
		this(contentData, pageId, location, (String) null);
	}

	public Content(ContentType contentData, long pageId, String location, String id) {
		if(id != null) {
			this.key = KeyFactory.createKey(Content.class.getSimpleName(), id);
			contentData.setKey(KeyFactory.createKey(this.key, contentData.getClass().getSimpleName(), id));
		}
		this.contentData = contentData;
		this.pageId = pageId;
		this.location = location;
		this.weight = new Date().getTime();
		this.visibleTo = 0;
		this.roleList = new LinkedList<Key>();
		this.added = new Date();
		this.owner = UserData.getUser().getKey();
//		this.updated = this.added;
//		this.updater = this.owner;
	}

	/*
	 * Inherited Methods
	 */

	@Override
	public Key getKey() {
		return this.key;
	}

	@Override
	protected void detachFields(PersistenceManager pm) {
		this.contentData = pm.detachCopy(this.contentData);
	}

	@Override
	public boolean equals(Object obj) {
		if(this == obj)
			return true;
		else if(obj instanceof Content)
			return ((Content) obj).getKey().equals(getKey());
		else
			return false;
	}

	/*
	 * Getter and Setter methods
	 */

	// Accessor methods for methods in ContentType class

	public String getName() {
		return this.contentData.getName();
	}

	public boolean hasEditor() {
		return this.contentData.hasEditor();
	}

	public String getEditor() {
		return this.contentData.getEditor();
	}


	public String getHTML() {
		return this.contentData.getHTML();
	}


	public ContentDTO getDTO() {
		ContentDTO contentDTO = this.contentData.getDTO();

		contentDTO.setLocation(getLocation());

		contentDTO.setVisibleTo(getVisibleTo());
		LinkedList<String> roleList = new LinkedList<String>();
		for(Key role : getRoleList())
			roleList.add(KeyFactory.keyToString(role));
		contentDTO.setRoleList(roleList);

		return contentDTO;
	}

	public void update(ContentDTO contentDTO) {
		this.contentData.update(contentDTO);

		setLocation(contentDTO.getLocation());

		setVisibleTo(contentDTO.getVisibleTo());
		if(contentDTO.getVisibleTo() != 0) {
			LinkedList<Key> roleList = new LinkedList<Key>();
			for(String encoded : contentDTO.getRoleList())
				roleList.add(KeyFactory.stringToKey(encoded));
			setRoleList(this.roleList);
		}
	}

	// END : Accessor methods for methods in ContentType class

	public long getPageId() {
		return this.pageId;
	}

	public void setPageId(long pageId) {
		this.pageId = pageId;
	}

	public String getLocation() {
		return this.location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public long getWeight() {
		return this.weight;
	}

	public void setWeight(long weight) {
		this.weight = weight;
	}


	public int getVisibleTo() {
		return this.visibleTo;
	}

	public void setVisibleTo(int visibleTo) {
		this.visibleTo = visibleTo;
	}

	public LinkedList<Key> getRoleList() {
		return this.roleList;
	}

	public void setRoleList(LinkedList<Key> roleList) {
		this.roleList = roleList;
	}


	public Date getAdded() {
		return this.added;
	}

	public User getOwner() {
		User user = UserData.getUser(this.owner);
		return user == null ? UserData.getSystem() : user;
	}

	public Date getUpdated() {
		return this.updated;
	}

	void setUpdated(Date updated) {
		this.updated  = updated;
	}

	public User getUpdater() {
		return UserData.getUser(this.updater);
	}

	void setUpdater(Key updater) {
		this.updater = updater;
	}

	/*
	 * Helper Methods
	 */

	public boolean isVisibleTo(Key userRoleKey) {
		if(getVisibleTo() == 0)
			return true;

		if(this.roleList.contains(userRoleKey))
			return getVisibleTo() == 1;

		return getVisibleTo() == -1;
	}

}