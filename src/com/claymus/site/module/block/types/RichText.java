package com.claymus.site.module.block.types;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import com.claymus.site.Module;
import com.claymus.site.module.block.BlockType;
import com.claymus.site.module.block.gwt.BlockDTO;
import com.claymus.site.module.block.types.gwt.RichTextDTO;
import com.google.appengine.api.datastore.Text;

@PersistenceCapable(detachable = "true")
public class RichText extends BlockType {

	private static final long serialVersionUID = 1185763700701987155L;

	@Persistent(column = "Title")
	private String title;

	@Persistent(column = "Content")
	private Text content;

	/*
	 * Inherited Methods
	 */

	{
		this.gwtModule = Module.MODULE_PACKAGE + ".block.types.richtext.gwt";
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
			html += "<div class='claymus-h2'>" + this.title + "</div>";

		if(this.content != null)
			html += "<div class='claymus-t2'>" + this.content.getValue() + "</div>";

		return html.length() == 0 ? null : html;
	}


	@Override
	protected BlockDTO getDTO() {
		RichTextDTO richtextDTO = new RichTextDTO();
		richtextDTO.setTitle(this.title);
		richtextDTO.setContent(this.content == null ? null : this.content.getValue());
		return richtextDTO;
	}

	@Override
	protected void update(BlockDTO blockDTO) {
		RichTextDTO richtextDTO = (RichTextDTO) blockDTO;
		this.title = richtextDTO.getTitle();
		this.content = richtextDTO.getContent() == null ? null : new Text(richtextDTO.getContent());
	}

}
