package com.claymus.site.module.block.types;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import com.claymus.site.Module;
import com.claymus.site.module.block.BlockType;
import com.claymus.site.module.block.gwt.BlockDTO;
import com.claymus.site.module.block.types.gwt.HTMLJavaScriptDTO;
import com.google.appengine.api.datastore.Text;

@PersistenceCapable(detachable = "true")
public class HTMLJavaScript extends BlockType {

	private static final long serialVersionUID = 551108153963432377L;

	@Persistent(column = "Title")
	private String title;

	@Persistent(column = "Content")
	private Text content;

	/*
	 * Inherited Methods
	 */

	{
		this.gwtModule = Module.MODULE_PACKAGE + ".block.types.htmljavascript.gwt";
	}


	@Override
	public String getName() {
		return "HTML/JavaScript";
	}

	@Override
	protected String getHTML() {
		String html = "";

		if(this.title != null)
			html += "<div class='claymus-h2'>" + this.title + "</div>";

		if(this.content != null)
			html += this.content.getValue();

		return html.length() == 0 ? null : html;
	}


	@Override
	protected BlockDTO getDTO() {
		HTMLJavaScriptDTO htmljavascriptDTO = new HTMLJavaScriptDTO();
		htmljavascriptDTO.setTitle(this.title);
		htmljavascriptDTO.setContent(this.content == null ? null : this.content.getValue());
		return htmljavascriptDTO;
	}

	@Override
	protected void update(BlockDTO blockDTO) {
		HTMLJavaScriptDTO htmljavascriptDTO = (HTMLJavaScriptDTO) blockDTO;
		this.title = htmljavascriptDTO.getTitle();
		this.content = htmljavascriptDTO.getContent() == null ? null : new Text(htmljavascriptDTO.getContent());
	}

}
