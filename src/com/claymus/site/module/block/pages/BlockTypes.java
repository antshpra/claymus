package com.claymus.site.module.block.pages;

import com.claymus.site.module.block.BlockData;
import com.claymus.site.module.block.BlockType;
import com.claymus.site.module.content.ContentType;

@SuppressWarnings("serial")
public class BlockTypes extends ContentType {

	@Override
	public String getName() {
		return "Add Block";
	}

	@Override
	protected String getHTML() {
		String html = "<div class='claymus-h1'>" + getName() + "</div>";

		html += "<table class='claymus-formatted-table'>";

		html += "<tr style='display:none'/>";

		for(BlockType blockType : BlockData.getBlockTypes()) {
			if(blockType.hasEditor()) {
				html += "<tr><td>";
					html += blockType.getName();
				html += "</td><td>";
					html += "<button type='button' class='gwt-Button' style='float:right' onClick=\"location.href='/_ah/block/new?type=" + blockType.getClass().getSimpleName() + "'\">Add</button>";
				html += "</td></tr>";
			}
		}

		html += "</table>";

		return html;
	}

}
