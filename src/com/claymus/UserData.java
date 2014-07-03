package com.claymus;

import java.util.LinkedList;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.google.appengine.api.NamespaceManager;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class UserData {

	private static final UserService userService = UserServiceFactory.getUserService();

	/*
	 * Login / Logout URL
	 */

	public static String getLoginURL() {
		return UserData.getLoginURL(ClaymusMain.getRequest().getRequestURI());
	}

	public static String getLoginURL(String destinationURL) {
		return UserData.userService.createLoginURL(destinationURL);
	}

	public static String getLogoutURL() {
		return UserData.getLogoutURL("/");
	}

	public static String getLogoutURL(String destinationURL) {
		return UserData.userService.createLogoutURL(destinationURL);
	}

	/*
	 * User Roles
	 */

	@Deprecated
	public static UserRole getUserRole() {
		return UserData.getUser().getRole();
	}

	public static UserRole getGuestRole() {
		UserRole role = UserData.getUserRole("guest");
		if(role == null) {
			role = new UserRole("guest", "Guest");
			UserData.createRole(role);
		}
		return role;
	}

	public static UserRole getMemberRole() {
		UserRole role = UserData.getUserRole("member");
		if(role == null) {
			role = new UserRole("member", "Member");
			UserData.createRole(role);
		}
		return role;
	}

	public static UserRole getAdministratorRole() {
		UserRole role = UserData.getUserRole("administrator");
		if(role == null) {
			role = new UserRole("administrator", "Administrator");
			UserData.createRole(role);
		}
		return role;
	}

	public static UserRole getUserRole(String id) {
		String namespace = NamespaceManager.get();
		NamespaceManager.set(null);
		Key key = KeyFactory.createKey(UserRole.class.getSimpleName(), id);
		NamespaceManager.set(namespace);
		return UserData.getUserRole(key);
	}

	public static UserRole getUserRole(Key key) {
		String namespace = NamespaceManager.get();
		NamespaceManager.set(null);
		UserRole userRole = Datastore.getEntity(UserRole.class, key);
		NamespaceManager.set(namespace);
		return userRole;
	}

	@SuppressWarnings("unchecked")
	public static List<UserRole> getUserRoles() {
		String namespace = NamespaceManager.get();
		NamespaceManager.set(null);

		LinkedList<Key> roleKeys = (LinkedList<Key>) Memcache.get("UserRoles");

		if(roleKeys == null) {
			roleKeys = new  LinkedList<Key>();
			PersistenceManager pm = PMF.get();
			Query query = pm.newQuery(UserRole.class);
			query.setResult("key");
			roleKeys.addAll((List<Key>) query.execute());
			query.closeAll();
			Memcache.put("UserRoles", roleKeys);
		}

		NamespaceManager.set(namespace);

		LinkedList<UserRole> roleList = new LinkedList<UserRole>();
		for (Key roleKey : roleKeys) {
			UserRole role = UserData.getUserRole(roleKey);
			if(role != null)
				roleList.add(role);
		}

		return roleList;
	}


	public static UserRole createRole(UserRole role) {
		String namespace = NamespaceManager.get();
		NamespaceManager.set(null);
		role = Datastore.makePersistent(role);
		if(role != null)
			Memcache.remove("UserRoles");
		NamespaceManager.set(namespace);
		return role;
	}


	public static UserRole updateRole(UserRole role) {
		String namespace = NamespaceManager.get();
		NamespaceManager.set(null);
		role = Datastore.makePersistent(role);
		NamespaceManager.set(namespace);
		return role;
	}

	/*
	 * Users
	 */

	public static User getUser() {
		if(! UserData.userService.isUserLoggedIn())
			return UserData.getAnonymous();

		com.google.appengine.api.users.User googleUser = UserData.userService.getCurrentUser();
		User user = UserData.getUser(googleUser.getUserId());

		if(user == null) {
			user = new User(googleUser.getUserId(), googleUser.getNickname(), googleUser.getEmail(), UserData.getMemberRole());
			user = UserData.createUser(user);
		}

		if(user != null && UserData.userService.isUserAdmin()) // TODO : remove this case. when claymus is being installed first time
			user.setRole(UserData.getAdministratorRole());	   // it will make current google user (admin) an Administrator

		return user == null ? UserData.getAnonymous() : user;
	}

	public static User getAnonymous() {
		User user = UserData.getUser("anonymous");

		if (user == null) {
			user = new User("anonymous", "Anonymous", null, UserData.getGuestRole());
			UserData.createUser(user);
		}

		return user;
	}

	public static User getSystem() {
		User user = UserData.getUser("system");

		if (user == null) {
			user = new User("system", "System", null, UserData.getAdministratorRole());
			UserData.createUser(user);
		}

		return user;
	}

	public static User getUser(String id) {
		String namespace = NamespaceManager.get();
		NamespaceManager.set(null);
		Key key = KeyFactory.createKey(User.class.getSimpleName(), id);
		NamespaceManager.set(namespace);
		return UserData.getUser(key);
	}

	public static User getUser(Key key) {
		String namespace = NamespaceManager.get();
		NamespaceManager.set(null);
		User user = Datastore.getEntity(User.class, key);
		NamespaceManager.set(namespace);
		return user;
	}


	public static User createUser(User user) {
		String namespace = NamespaceManager.get();
		NamespaceManager.set(null);
		user = Datastore.makePersistent(user);
		NamespaceManager.set(namespace);
		return user;
	}


	public static User updateUser(User user) {
		String namespace = NamespaceManager.get();
		NamespaceManager.set(null);
		user = Datastore.makePersistent(user);
		NamespaceManager.set(namespace);
		return user;
	}

}
