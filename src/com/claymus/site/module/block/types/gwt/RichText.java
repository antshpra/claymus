package com.claymus.site.module.block.types.gwt;

import com.claymus.gwt.form.FormField;
import com.claymus.gwt.form.fields.RichTextAreaFormField;
import com.claymus.gwt.form.fields.TextBoxFormField;
import com.claymus.site.module.block.gwt.BlockDTO;
import com.claymus.site.module.block.gwt.BlockEditor;

public class RichText extends BlockEditor {

	private TextBoxFormField title = new TextBoxFormField("Title", false, null);
	private RichTextAreaFormField richtext = new RichTextAreaFormField("Content", true, null);

	@Override
	public FormField<?>[] getFields() {
		return new FormField[]{
				this.title, this.richtext
		};
	}

	@Override
	public BlockDTO getDTO() {
		RichTextDTO richtextDTO = new RichTextDTO();
		richtextDTO.setTitle(this.title.getText());
		richtextDTO.setContent(this.richtext.getHTML());
		return richtextDTO;
	}

	@Override
	public void putDTO(BlockDTO blockDTO) {
		RichTextDTO richtextDTO = (RichTextDTO) blockDTO;
		this.title.setText(richtextDTO.getTitle());
		this.richtext.setHTML((richtextDTO.getContent()));
	}

}
