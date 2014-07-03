package com.claymus.site;

import com.claymus.ClaymusMain;
import com.claymus.site.module.content.ContentType;

@SuppressWarnings("serial")
public class Error404 extends ContentType {

	@Override
	public String getName() {
		return "Error 404";
	}

	@Override
	protected String getHTML() {
		ClaymusMain.getResponse().setStatus(404);

		String html = "";

		html += "<div class='claymus-h1'>Page Not Found !!!</div>";

		html += "<div class='claymus-t1'>";
		html += "<p>Requested page not found on server.</p>";
		html += "</div>";

		return html;
	}

}
