package com.claymus.site.module.content.types.gwt;

import com.claymus.gwt.form.FormField;
import com.claymus.gwt.form.fields.RichTextAreaFormField;
import com.claymus.gwt.form.fields.TextBoxFormField;
import com.claymus.site.module.content.gwt.ContentDTO;
import com.claymus.site.module.content.gwt.ContentEditor;

public class RichText extends ContentEditor {

	private TextBoxFormField title = new TextBoxFormField("Title", false, null);
	private RichTextAreaFormField richtext = new RichTextAreaFormField("Content", true, null);

	@Override
	public FormField<?>[] getFields() {
		return new FormField[]{
				this.title, this.richtext
		};
	}

	@Override
	public ContentDTO getDTO() {
		RichTextDTO richtextDTO = new RichTextDTO();
		richtextDTO.setTitle(this.title.getText());
		richtextDTO.setContent(this.richtext.getHTML());
		return richtextDTO;
	}

	@Override
	public void putDTO(ContentDTO contentDTO) {
		RichTextDTO contentrichtextDTO = (RichTextDTO) contentDTO;
		this.title.setText(contentrichtextDTO.getTitle());
		this.richtext.setHTML(contentrichtextDTO.getContent());
	}

}
