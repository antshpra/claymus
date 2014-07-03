package com.claymus.site.module.content.types.gwt;

import com.claymus.gwt.form.FormField;
import com.claymus.gwt.form.fields.TextAreaFormField;
import com.claymus.gwt.form.fields.TextBoxFormField;
import com.claymus.site.module.content.gwt.ContentDTO;
import com.claymus.site.module.content.gwt.ContentEditor;

public class HTMLJavaScript extends ContentEditor {

	private TextBoxFormField title = new TextBoxFormField("Title", false, null);
	private TextAreaFormField content = new TextAreaFormField("Content", true, "Any HTML and/or JavaScript code is allowed.");

	@Override
	public FormField<?>[] getFields() {
		return new FormField[]{
				this.title, this.content
		};
	}

	@Override
	public ContentDTO getDTO(){
		HTMLJavaScriptDTO texthtmlDTO = new HTMLJavaScriptDTO();
		texthtmlDTO.setTitle(this.title.getText());
		texthtmlDTO.setContent(this.content.getText());
		return texthtmlDTO;
	}

	@Override
	public void putDTO(ContentDTO contentDTO){
		HTMLJavaScriptDTO texthtmlDTO = (HTMLJavaScriptDTO) contentDTO;
		this.title.setText(texthtmlDTO.getTitle());
		this.content.setText(texthtmlDTO.getContent());
	}

}
