package com.claymus.site.module.block;

import java.util.Date;
import java.util.LinkedList;

import javax.jdo.PersistenceManager;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import com.claymus.PersistentCapable;
import com.claymus.User;
import com.claymus.UserData;
import com.claymus.site.module.block.gwt.BlockDTO;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

@PersistenceCapable(detachable = "true")
public class Block extends PersistentCapable {

	private static final long serialVersionUID = 8875942014433704218L;

	@Persistent(column = "Key", primaryKey = "true", valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key key;


	@Persistent(column = "BlockData")
	private BlockType blockData;


	@Persistent(column = "Location")
	private String location;

	@Persistent(column = "Weight")
	private Long weight;


	// 0 == all, 1 == only to listed roles, -1 == all except listed roles
	@Persistent(column = "Visibility")
	private Integer visibleTo;

	@Persistent(column = "VisibleTo")
	private LinkedList<Key> roleList;


	@Persistent(column = "ShowAt")
	private LinkedList<String> visibleAt;

	@Persistent(column = "ShowNotAt")
	private LinkedList<String> notVisibleAt;


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

	public Block(BlockType blockData, String location) {
		this(blockData, location, (String) null);
	}

	public Block(BlockType blockData, String location, String id) {
		if(id != null) {
			this.key = KeyFactory.createKey(Block.class.getSimpleName(), id);
			blockData.setKey(KeyFactory.createKey(this.key, "BlockData", id));
		}
		this.blockData = blockData;
		this.location = location;
		this.weight = new Date().getTime();
		this.visibleTo = 0;
		this.roleList = new LinkedList<Key>();
		this.visibleAt = new LinkedList<String>();
		this.notVisibleAt = new LinkedList<String>();
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
		this.blockData = pm.detachCopy(this.blockData);
	}

	@Override
	public boolean equals(Object obj) {
		if(this == obj)
			return true;
		else if(obj instanceof Block)
			return ((Block) obj).getKey().equals(getKey());
		else
			return false;
	}

	/*
	 * Getter and Setter methods
	 */

	// Accessor methods for methods in BlockType class

	public String getName() {
		return this.blockData.getName();
	}

	public boolean hasEditor() {
		return this.blockData.hasEditor();
	}

	public String getEditor() {
		return this.blockData.getEditor();
	}


	public String getHTML() {
		return this.blockData.getHTML();
	}


	public BlockDTO getDTO() {
		BlockDTO blockDTO = this.blockData.getDTO();

		blockDTO.setLocation(getLocation());

		blockDTO.setVisibleTo(getVisibleTo());
		LinkedList<String> roleList = new LinkedList<String>();
		for(Key role : getRoleList())
			roleList.add(KeyFactory.keyToString(role));
		blockDTO.setRoleList(roleList);

		blockDTO.setVisibleAt(getVisibleAtList());
		blockDTO.setNotVisibleAt(getNotVisibleAtList());

		return blockDTO;
	}

	public void update(BlockDTO blockDTO) {
		this.blockData.update(blockDTO);

		setLocation(blockDTO.getLocation());

		setVisibleTo(blockDTO.getVisibleTo());
		if(blockDTO.getVisibleTo() != 0) {
			LinkedList<Key> roleList = new LinkedList<Key>();
			for(String encoded : blockDTO.getRoleList())
				roleList.add(KeyFactory.stringToKey(encoded));
			setRoleList(roleList);
		}

		setVisibleAt(blockDTO.getVisibleAt());
		setNotVisibleAt(blockDTO.getNotVisibleAt());
	}

	// END : Accessor methods for methods in BlockType class

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


	public LinkedList<String> getVisibleAtList(){
		return this.visibleAt;
	}

	public void setVisibleAt(LinkedList<String> uriList){
		this.visibleAt = uriList;
	}

	public LinkedList<String> getNotVisibleAtList(){
		return this.notVisibleAt;
	}

	public void setNotVisibleAt(LinkedList<String> uriList) {
		this.notVisibleAt = uriList;
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

	public boolean isVisibleTo(Key userRoleKey){
		if(getVisibleTo() == 0)
			return true;

		if(getRoleList().contains(userRoleKey))
			return getVisibleTo() == 1;

		return getVisibleTo() == -1;
	}

	public boolean isVisibleAt(String uri) { // TODO : support regx or wildcards
		if(getVisibleAtList().size() != 0 && getNotVisibleAtList().size() != 0)
			return ! getNotVisibleAtList().contains(uri) && getNotVisibleAtList().contains(uri);

		if(getVisibleAtList().size() != 0 && getNotVisibleAtList().size() == 0)
			return getVisibleAtList().contains(uri);

		if(getVisibleAtList().size() == 0 && getNotVisibleAtList().size() != 0)
			return ! getNotVisibleAtList().contains(uri);

		return true;
	}

 }