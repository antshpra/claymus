package com.claymus.site.module.content.types;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import com.claymus.site.Module;
import com.claymus.site.module.content.ContentType;
import com.claymus.site.module.content.gwt.ContentDTO;
import com.claymus.site.module.content.types.gwt.HTMLJavaScriptDTO;
import com.google.appengine.api.datastore.Text;

@PersistenceCapable(detachable = "true")
public class HTMLJavaScript extends ContentType {

	private static final long serialVersionUID = 8383361452674468518L;

	@Persistent(column = "Title")
	private String title;

	@Persistent(column = "Content")
	private Text content;

	/*
	 * Inherited Methods
	 */

	{
		this.gwtModule = Module.MODULE_PACKAGE + ".content.types.htmljavascript.gwt";
	}


	@Override
	public String getName() {
		return "HTML/JavaScript";
	}

	@Override
	protected String getHTML() {
		String html = "";

		if(this.title != null)
			html += "<div class='claymus-h1'>" + this.title + "</div>";

		if(this.content != null)
			html += this.content.getValue();

		return html.length() == 0 ? null : html;
	}


	@Override
	protected ContentDTO getDTO() {
		HTMLJavaScriptDTO htmljavascriptDTO = new HTMLJavaScriptDTO();
		htmljavascriptDTO.setTitle(this.title);
		htmljavascriptDTO.setContent(this.content == null ? null : this.content.getValue());
		return htmljavascriptDTO;
	}

	@Override
	protected void update(ContentDTO contentDTO) {
		HTMLJavaScriptDTO htmljavascriptDTO = (HTMLJavaScriptDTO) contentDTO;
		this.title = htmljavascriptDTO.getTitle();
		this.content = htmljavascriptDTO.getContent() == null ? null : new Text(htmljavascriptDTO.getContent());
	}

}
