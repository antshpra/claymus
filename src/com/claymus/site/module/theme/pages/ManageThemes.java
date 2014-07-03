package com.claymus.site.module.theme.pages;

import com.claymus.site.module.content.ContentType;
import com.claymus.site.module.theme.ModuleHelper;
import com.claymus.site.module.theme.Theme;
import com.claymus.site.module.theme.ThemeData;

@SuppressWarnings("serial")
public class ManageThemes extends ContentType {

	private int accessLevel;

	public ManageThemes(int accessLevel) {
		this.accessLevel = accessLevel;
	}

	/*
	 * Inherited Methods
	 */

	@Override
	public String getName() {
		return "Manage Themes";
	}

	@Override
	protected String getHTML() {
		String html = "<div class='claymus-h1'>" + getName() + "</div>";

		html += "<table class='claymus-formatted-table'>";

		html += "<tr style='display:none'/>";

		Theme currentTheme = ThemeData.getTheme();
		for(Theme theme : ThemeData.getThemes()) {
			html += "<tr>";
				html += "<td>";
					html += theme.equals(currentTheme)
							? theme.getName() + " <span class='claymus-highlighted-text'>(Active)</span>"
							: theme.getName();
				html += "</td>";
				html += "<td style='text-align:right'>";
					html += theme.equals(currentTheme) || this.accessLevel != ModuleHelper.VIEW_N_CHANGE
							? "<button type='button' class='gwt-Button' disabled='disabled'>Set Active</button>"
							: "<button type='button' class='gwt-Button' onClick='claymus_theme_setActive(\"" + theme.getClass().getSimpleName() + "\")'>Set Active</button>";
				html += "</td>";
			html += "</tr>";
		}

		html += "</table>";

		if(this.accessLevel == ModuleHelper.VIEW_N_CHANGE)
			html += "<script type='text/javascript' src='/com.claymus.site.module.theme.pages.manage.gwt/com.claymus.site.module.theme.pages.manage.gwt.nocache.js'></script>";

		return html;
	}

}
