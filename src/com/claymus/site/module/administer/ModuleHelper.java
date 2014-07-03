package com.claymus.site.module.administer;

import java.util.LinkedList;
import java.util.List;

import javax.jdo.annotations.PersistenceCapable;

import com.claymus.User;
import com.claymus.site.Error401;
import com.claymus.site.Module;
import com.claymus.site.module.administer.pages.Administer;
import com.claymus.site.module.administer.pages.ManageModules;
import com.claymus.site.module.content.ContentType;

@PersistenceCapable(detachable = "true")
public class ModuleHelper extends Module {

	public static final int VIEW_ONLY 		= 1;
	public static final int VIEW_N_EDIT 	= 2;

	/*
	 * Inherited Method
	 */

	@Override
	public double getVersion() {
		return 0.99;
	}

	@Override
	public String getName() {
		return "Administer";
	}

	@Override
	public String getDescription() {
		return "<a href='/_ah/administer'>Administer</a>";
	}


	@Override
	public String[] getAccessLevelNames() {
		return new String[] {"View Only", "View & Change"};
	}

	@Override
	public int getFullAccessLevel() {
		return ModuleHelper.VIEW_N_EDIT;
	}


	@Override
	public List<ContentType> getPageContents(String[] tokens, User user) {
		LinkedList<ContentType> contentsData = new LinkedList<ContentType>();
		int accessLevel = getAccessLevel(user.getRole());

		if(tokens.length == 1) {
			if(accessLevel >= ModuleHelper.VIEW_ONLY) {
				contentsData.add(new Administer(accessLevel));
				contentsData.add(new ManageModules(accessLevel));
			} else {
				contentsData.add(new Error401());
			}
		}

		return contentsData;
	}

}
