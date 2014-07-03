package com.claymus.site.module.administer.server;

import com.claymus.UserData;
import com.claymus.UserRole;
import com.claymus.gwt.ServerException;
import com.claymus.gwt.UserException;
import com.claymus.site.Module;
import com.claymus.site.ModuleData;
import com.claymus.site.module.administer.ModuleHelper;
import com.claymus.site.module.administer.gwt.AdministerService;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

@SuppressWarnings("serial")
public class AdministerServiceImpl extends RemoteServiceServlet implements AdministerService {

	@Override
	public void setModuleAccessLevel(String moduleId, String encoded, int accessLevel) throws ServerException, UserException {
		Module module = ModuleData.getModule(ModuleHelper.class);
		UserRole userRole = UserData.getUser().getRole();

		if(module.getAccessLevel(userRole) < ModuleHelper.VIEW_N_EDIT)
			throw new UserException();

		module = ModuleData.getModule(moduleId);
		if(module == null)
			throw new ServerException();

		module.setAccessLevel(UserData.getUserRole(KeyFactory.stringToKey(encoded)), accessLevel);
		if(! ModuleData.saveModule(module))
			throw new ServerException();
	}

}