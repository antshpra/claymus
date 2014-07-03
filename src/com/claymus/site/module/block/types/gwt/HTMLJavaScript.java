package com.claymus.site.module.block.types.gwt;

import com.claymus.gwt.form.FormField;
import com.claymus.gwt.form.fields.TextAreaFormField;
import com.claymus.gwt.form.fields.TextBoxFormField;
import com.claymus.site.module.block.gwt.BlockDTO;
import com.claymus.site.module.block.gwt.BlockEditor;

public class HTMLJavaScript extends BlockEditor {

	private TextBoxFormField title = new TextBoxFormField("Title", false, null);
	private TextAreaFormField content = new TextAreaFormField("Content", true, "Any HTML and/or JavaScript code is allowed.");

	@Override
	public FormField<?>[] getFields() {
		return new FormField[] {
				this.title, this.content
		};
	}

	@Override
	public BlockDTO getDTO() {
		HTMLJavaScriptDTO texthtmlDTO = new HTMLJavaScriptDTO();
		texthtmlDTO.setTitle(this.title.getText());
		texthtmlDTO.setContent(this.content.getText());
		return texthtmlDTO;
	}

	@Override
	public void putDTO(BlockDTO blockDTO) {
		HTMLJavaScriptDTO texthtmlDTO = (HTMLJavaScriptDTO) blockDTO;
		this.title.setText(texthtmlDTO.getTitle());
		this.content.setText(texthtmlDTO.getContent());
	}

}
