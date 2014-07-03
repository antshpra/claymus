package com.claymus.site.module.theme;

import javax.jdo.annotations.PersistenceCapable;

import com.claymus.User;
import com.claymus.site.Error401;
import com.claymus.site.Module;
import com.claymus.site.module.content.ContentType;
import com.claymus.site.module.theme.pages.ManageThemes;

@PersistenceCapable(detachable = "true")
public class ModuleHelper extends Module {

	private static final long serialVersionUID = -8727912353463081710L;

	public static final int VIEW_ONLY 		= 1;
	public static final int VIEW_N_CHANGE 	= 2;

	/*
	 * Inherited Methods
	 */

	@Override
	public double getVersion() {
		return 0.99;
	}

	@Override
	public String getName() {
		return "Theme";
	}

	@Override
	public String getDescription() {
		return "<a href='/_ah/theme'>Manage Themes</a>";
	}


	@Override
	public String[] getAccessLevelNames() {
		return new String[] {"View Only", "View & Change"};
	}

	@Override
	public int getFullAccessLevel() {
		return ModuleHelper.VIEW_N_CHANGE;
	}


	@Override
	public ContentType getPageContent(String[] tokens, User user) {
		int accessLevel = getAccessLevel(user.getRole());

		if(tokens.length == 1) {
			if(accessLevel >= ModuleHelper.VIEW_ONLY)
				return new ManageThemes(accessLevel);
			else
				return new Error401();

		} else {
			return null;
		}

	}

}
