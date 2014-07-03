package com.claymus.site.module.page.pages;

import com.claymus.User;
import com.claymus.site.module.content.ContentType;
import com.claymus.site.module.page.ModuleHelper;
import com.claymus.site.module.page.Page;
import com.claymus.site.module.page.PageData;
import com.google.appengine.api.datastore.KeyFactory;

@SuppressWarnings("serial")
public class ManagePages extends ContentType {

	private int accessLevel;
	private int contentAccessLevel;
	private User user;

	public ManagePages(int accessLevel, int contentAccessLevel, User user) {
		this.accessLevel = accessLevel;
		this.contentAccessLevel = contentAccessLevel;
		this.user = user;
	}

	/*
	 * Inherited Methods
	 */

	@Override
	public String getName() {
		return "Manage Pages";
	}

	@Override
	@SuppressWarnings("deprecation")
	protected String getHTML() {
		String html = this.accessLevel >= ModuleHelper.ADD
				? "<div class='claymus-h1'>" + getName() + "<button type='button' class='gwt-Button' style='float:right' onClick=\"location.href='/_ah/page/new'\">Add Page</button></div>"
				: "<div class='claymus-h1'>" + getName() + "<button type='button' class='gwt-Button' style='float:right' disabled='disabled'>Add Page</button></div>";

		html += "<table class='claymus-formatted-table'>";

		html += "<tr style='display:none'/>";

		for(Page page : PageData.getAllPages()) {
			html += "<tr>";
				html += page.getTitle() != null
						? "<td><a href='" + page.getUri() + "'>" + page.getTitle() + "</a> <span class='claymus-sub-text claymus-faded-text'>created by " + page.getCreator().getName() + "</span></td>"
						: "<td><a href='" + page.getUri() + "'>" +  "(no title)"   + "</a> <span class='claymus-sub-text claymus-faded-text'>created by " + page.getCreator().getName() + "</span></td>";

				String encoded = KeyFactory.keyToString(page.getKey());
				html += "<td style='text-align:right'>";
					html += this.accessLevel >= ModuleHelper.ADD_EDIT || (this.accessLevel == ModuleHelper.ADD && page.getCreator().equals(this.user))
							? "<button type='button' class='gwt-Button' onClick=\"location.href='/_ah/page/edit?key=" + encoded + "'\">Edit</button>"
							: "<button type='button' class='gwt-Button' disabled='disabled'>Edit</button>";

					html += this.contentAccessLevel >= com.claymus.site.module.content.ModuleHelper.VIEW_ONLY
							? "<button type='button' class='gwt-Button' onClick=\"location.href='/_ah/content?page=" + encoded + "'\">Contents</button>"
							: "<button type='button' class='gwt-Button' disabled='disabled'>Contents</button>";
				html += "</td>";

			html += "</tr>";
		}
		html += "</table>";

		return html;
	}

}
