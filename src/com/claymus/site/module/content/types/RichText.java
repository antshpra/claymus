package com.claymus.site.module.content.types;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import com.claymus.site.Module;
import com.claymus.site.module.content.ContentType;
import com.claymus.site.module.content.gwt.ContentDTO;
import com.claymus.site.module.content.types.gwt.RichTextDTO;
import com.google.appengine.api.datastore.Text;

@PersistenceCapable(detachable = "true")
public class RichText extends ContentType {

	private static final long serialVersionUID = -3587953318212316181L;

	@Persistent(column = "Title")
	private String title;

	@Persistent(column = "Content")
	private Text content;

	/*
	 * Inherited Methods
	 */

	{
		this.gwtModule = Module.MODULE_PACKAGE + ".content.types.richtext.gwt";
		this.richTextEditor = true;
	}


	@Override
	public String getName() {
		return "Rich Text";
	}

	@Override
	protected String getHTML() {
		String html = "";

		if(this.title != null)
			html += "<div class='claymus-h1'>" + this.title + "</div>";

		if(this.content != null)
			html += "<div class='claymus-t1'>" + this.content.getValue() + "</div>";

		return html.length() == 0 ? null : html;
	}


	@Override
	public ContentDTO getDTO(){
		RichTextDTO richtextDTO = new RichTextDTO();
		richtextDTO.setTitle(this.title);
		richtextDTO.setContent(this.content == null ? null : this.content.getValue());
		return richtextDTO;
	}

	@Override
	protected void update(ContentDTO contentDTO) {
		RichTextDTO richtextDTO = (RichTextDTO) contentDTO;
		this.title = richtextDTO.getTitle();
		this.content = richtextDTO.getContent() == null ? null : new Text(richtextDTO.getContent());
	}

}