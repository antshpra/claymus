package com.claymus.site.module.administer.pages;

import com.claymus.site.SiteData;
import com.claymus.site.module.content.ContentType;

@SuppressWarnings("serial")
public class Administer extends ContentType {

	private int accesLevel;

	public Administer(int accessLevel) {
		this.accesLevel = accessLevel;
	}

	/*
	 * Inherited Methods
	 */

	@Override
	public String getName() {
		return "Administer";
	}

	@Override
	protected String getHTML() {
		String html = "<div class='claymus-h1'>Site Info</div>";

			html += "<table class='claymus-formatted-table'>";

			html += "<tr style='display:none'/>";

			html += "<tr><td>";
				html += "Site Version</td><td>" + SiteData.getSiteVersion();
			html += "</td></tr><tr><td>";
				html += "Site Title</td><td>" + SiteData.getSiteTitle();
			html += "</td></tr><tr><td>";
				html += "Title Separator</td><td>" + SiteData.getSiteTitleSeparator();
			html += "</td></tr><tr><td>";
				html += "Site Description</td><td>" + SiteData.getSiteDescription();
			html += "</td></tr>";

		html += "</table>";

//		html += "<script type='text/javascript' src='/com.claymus.site.pages.manage.gwt/com.claymus.site.pages.manage.gwt.nocache.js'></script>";

		return html;
	}

}
