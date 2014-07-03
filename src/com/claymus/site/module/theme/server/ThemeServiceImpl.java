package com.claymus.site.module.theme.server;

import com.claymus.UserData;
import com.claymus.UserRole;
import com.claymus.gwt.ServerException;
import com.claymus.gwt.UserException;
import com.claymus.site.Module;
import com.claymus.site.ModuleData;
import com.claymus.site.module.theme.ModuleHelper;
import com.claymus.site.module.theme.ThemeData;
import com.claymus.site.module.theme.gwt.ThemeService;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

@SuppressWarnings("serial")
public class ThemeServiceImpl extends RemoteServiceServlet implements ThemeService {

	@Override
	public void setActive(String className) throws ServerException, UserException {
		Module module = ModuleData.getModule(ModuleHelper.class);
		UserRole userRole = UserData.getUser().getRole();

		if(module.getAccessLevel(userRole) != ModuleHelper.VIEW_N_CHANGE)
			throw new UserException();

		if(! ThemeData.setTheme(ThemeData.getTheme(className)))
			throw new ServerException("Theme could not be saved. Please try again.");
	}

}