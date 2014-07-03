package com.claymus.site.module.content.pages;

import com.claymus.site.module.content.ContentData;
import com.claymus.site.module.content.ContentType;
import com.claymus.site.module.page.Page;
import com.google.appengine.api.datastore.KeyFactory;

@SuppressWarnings("serial")
public class ContentTypes extends ContentType {

	private Page page;

	public ContentTypes(Page page) {
		this.page = page;
	}

	/*
	 * Inherited Methods
	 */

	@Override
	public String getName() {
		return this.page.getTitle() != null
				? this.page.getTitle() + " \u00BB Add Content"
				: "(no title)" 		   + " \u00BB Add Content";
	}

	@Override
	protected String getHTML() {
		String html = "<div class='claymus-h1'>" + getName() + "</div>";

		html += "<table class='claymus-formatted-table'>";

		html += "<tr style='display:none'/>";

		String encoded = KeyFactory.keyToString(this.page.getKey());
		for(ContentType contentType : ContentData.getContentTypes()) {
			if(contentType.hasEditor()) {
				html += "<tr><td>";
					html += contentType.getName();
				html += "</td><td>";
					html += "<button type='button' class='gwt-Button' style='float:right' onClick=\"location.href='/_ah/content/new?page=" + encoded + "&type=" + contentType.getClass().getSimpleName() + "'\">Add</button>";
				html += "</td></tr>";
			}
		}

		html += "</table>";

		return html;
	}

}
