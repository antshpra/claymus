package com.claymus.site.module.content.pages;

import java.util.List;

import com.claymus.Resources;
import com.claymus.User;
import com.claymus.site.module.content.Content;
import com.claymus.site.module.content.ContentData;
import com.claymus.site.module.content.ContentType;
import com.claymus.site.module.content.ModuleHelper;
import com.claymus.site.module.page.Page;
import com.google.appengine.api.datastore.KeyFactory;

@SuppressWarnings("serial")
public class ManageContents extends ContentType {

	private int accessLevel;
	private Page page;
	private User user;

	public ManageContents(int accessLevel, Page page, User user) {
		this.accessLevel = accessLevel;
		this.page = page;
		this.user = user;
	}

	/*
	 * Inherited Methods
	 */

	@Override
	public String getName() {
		return this.page.getTitle() != null
				? this.page.getTitle() + " \u00BB Manage Contents"
				: "(no title)" 		   + " \u00BB Manage Contents";
	}

	@Override
	public String getHTML() {
		String pageEncoded = KeyFactory.keyToString(this.page.getKey());

		String html = this.accessLevel >= ModuleHelper.ADD
				? "<div class='claymus-h1'>" + getName() + "<button type='button' class='gwt-Button' style='float:right' onClick=\"location.href='/_ah/content/add?page=" + pageEncoded + "'\">Add Content</button>"
				: "<div class='claymus-h1'>" + getName() + "<button type='button' class='gwt-Button' style='float:right' disabled='disabled'>Add Content</button>";

		html += "<button type='button' class='gwt-Button' style='float:right' disabled='disabled' id='claymus-Contents-SaveOrderButton'>Save Contents Order</button></div>";

		List<Content> disbaledContents = ContentData.getContents(this.page.getId());

		html += "<table id='claymus-Contents'>";
		for(Object[][] row : this.page.getLayout().get()) {
			html += "<tr>";
			for(Object[] col : row) {
				html += "<td colspan='" + col[0] + "' rowspan='" + col[1] + "'>";
				html += "<ul id='claymus-Contents-" + col[2] + "' class='claymus-Contents-Sortable'>";

				for(Content content : ContentData.getContents(this.page.getId(),(String) col[2])) {
					disbaledContents.remove(content);
					html += createContentDummy(content, this.accessLevel, pageEncoded);
				}

				html += "</ul>";
				html += "</td>";
			}
			html += "</tr>";
		}
		html += "</table>";

		html += "<div class='claymus-h2' style='text-align:center; padding:15px'>";
		html += "Disabled Contents";
		html += "</div>";

		html += "<ul id='claymus-Contents-DISABLED' class='claymus-Contents-Sortable'>";

		for(Content content : disbaledContents)
			html += createContentDummy(content, this.accessLevel, pageEncoded);

		html += "</ul>";

		html += Resources.getJQuery("jquery-1.4.2.js");
		html += Resources.getJQuery("ui/jquery.ui.core.js");
		html += Resources.getJQuery("ui/jquery.ui.widget.js");
		html += Resources.getJQuery("ui/jquery.ui.mouse.js");
		html += Resources.getJQuery("ui/jquery.ui.sortable.js");
		html += "<script type='text/javascript' src='/com.claymus.site/com.claymus.site.module.content.pages.manage.js'></script>";

		if(this.accessLevel >= ModuleHelper.ADD)
			html += "<script type='text/javascript' src='/com.claymus.site.module.content.pages.manage.gwt/com.claymus.site.module.content.pages.manage.gwt.nocache.js'></script>";

		return html;
	}


	private String createContentDummy(Content content, int accessLevel, String pageEncoded) {
		String encoded = KeyFactory.keyToString(content.getKey());
		String html = "<li id='claymus-Contents-" + encoded + "' class='claymus-t3'>";
			html += content.getName();
			if(content.hasEditor()) {
				html += " <span class='claymus-sub-text'>(";
					if(accessLevel >= ModuleHelper.ADD_EDIT || (accessLevel == ModuleHelper.ADD && content.getOwner().equals(this.user)))
						html += "<a href='/_ah/content/edit?page=" + pageEncoded + "&key=" + encoded + "'>edit</a>";
					else
						html += "<span class='claymus-faded-text'>edit</span>";
				html += ")</span>";
			}
		return html += "</li>";
	}

}
