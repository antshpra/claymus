package com.claymus.site;

import com.claymus.ClaymusMain;
import com.claymus.UserData;
import com.claymus.UserRole;
import com.claymus.site.module.content.ContentType;

@SuppressWarnings("serial")
public class Error401 extends ContentType {

	@Override
	public String getName() {
		return "Error 401";
	}

	@Override
	protected String getHTML() {
		ClaymusMain.getResponse().setStatus(401);

		String html = "";

		UserRole userRole = UserData.getUser().getRole();
		if(userRole.equals(UserData.getGuestRole())) {
			html += "<div class='claymus-h1'>Login Required !!!</div>";

			html += "<div class='claymus-t1'>";
			html += "<p><a href='" + UserData.getLoginURL() + "'>Login</a> is required to view content of this page.</p>";
			html += "</div>";
		} else {
			html += "<div class='claymus-h1'>Insufficient Permissions !!!</div>";

			html += "<div class='claymus-t1'>";
			html += "<p>You don't have permission to view content of this page.</p>";
			html += "</div>";
		}

		return html;
	}

}
