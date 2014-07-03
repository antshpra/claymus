package com.claymus.site.module.administer.pages;

import java.util.List;

import com.claymus.UserData;
import com.claymus.UserRole;
import com.claymus.site.Module;
import com.claymus.site.ModuleData;
import com.claymus.site.module.administer.ModuleHelper;
import com.claymus.site.module.content.ContentType;
import com.google.appengine.api.datastore.KeyFactory;

@SuppressWarnings("serial")
public class ManageModules extends ContentType {

	private int accessLevel;

	public ManageModules(int accessLevel) {
		this.accessLevel = accessLevel;
	}

	/*
	 * Inherited Methods
	 */

	@Override
	public String getName() {
		return "Installed Modules";
	}

	@Override
	protected String getHTML() {
		List<UserRole> userRoles = UserData.getUserRoles();
		UserRole adminRole = UserData.getAdministratorRole();

		String html = "<div class='claymus-h1'>" + getName() + "</div>";

		html += "<table class='claymus-formatted-table'>";

		html += "<tr style='display:none'/>";

		for(Module module : ModuleData.getModules()) {
			html += "<tr><td>";
				html += module.getName() + " <span class='claymus-sub-text claymus-faded-text'>" + module.getVersion() + "</span>";
				html += "<br/>";
				html += module.getDescription();
			html += "</td><td>";
				html += "<table>";
				for(UserRole userRole : userRoles) {
					html += "<tr>";
						html += "<td>";
							html += userRole.getName();
						html += "</td>";
						html += "<td>";
							String[] accessLevelNames = module.getAccessLevelNames();
							int accessLevel = module.getAccessLevel(userRole);
							html += "<select" + (userRole.equals(adminRole) || this.accessLevel < ModuleHelper.VIEW_N_EDIT
									? " disabled='disabled'"
									: " onchange='claymus_administer_setModuleAccessLevel(\"" + module.getId() + "\", \"" + KeyFactory.keyToString(userRole.getKey()) + "\", value)'") + ">";
							html += "<option value='0'" + (accessLevel == 0 ? " selected='selected'" : "") + ">(none)</option>";
							for(int i = 1; i <= accessLevelNames.length; i++)
								html += "<option value='" + i + "'" + (accessLevel == i ? " selected='selected'" : "") + ">" + accessLevelNames[i - 1] + "</option>";
							html += "</select>";
						html += "</td>";
					html += "</tr>";
				}
				html += "</table>";
			html += "</td></tr>";
		}

		html += "</table>";

		if(this.accessLevel >= ModuleHelper.VIEW_N_EDIT)
			html += "<script type='text/javascript' src='/com.claymus.site.module.administer.pages.managemodules.gwt/com.claymus.site.module.administer.pages.managemodules.gwt.nocache.js'></script>";

		return html;
	}

}
