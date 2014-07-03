package com.claymus.site.module.block.pages;

import java.util.List;

import com.claymus.Resources;
import com.claymus.User;
import com.claymus.site.module.block.Block;
import com.claymus.site.module.block.BlockData;
import com.claymus.site.module.block.ModuleHelper;
import com.claymus.site.module.content.ContentType;
import com.claymus.site.module.theme.ThemeData;
import com.google.appengine.api.datastore.KeyFactory;

@SuppressWarnings("serial")
public class ManageBlocks extends ContentType {

	private int accessLevel;
	private User user;

	public ManageBlocks(int accessLevel, User user) {
		this.accessLevel = accessLevel;
		this.user = user;
	}

	/*
	 * Inherited Methods
	 */

	@Override
	public String getName() {
		return "Manage Blocks";
	}

	@Override
	public String getHTML() {
		String html = this.accessLevel >= ModuleHelper.ADD
				? "<div class='claymus-h1'>" + getName() + "<button type='button' class='gwt-Button' style='float:right' onClick=\"location.href='/_ah/block/add'\">Add Block</button>"
				: "<div class='claymus-h1'>" + getName() + "<button type='button' class='gwt-Button' style='float:right' disabled='disabled'>Add Block</button>";

		html += "<button type='button' class='gwt-Button' style='float:right' disabled='disabled' id='claymus-Blocks-SaveOrderButton'>Save Blocks Order</button></div>";

		List<Block> disabledBlocks = BlockData.getBlocks();

		html += "<table id='claymus-Blocks'>";
		for(Object[][] row : ThemeData.getTheme().getLayout()) {
			html += "<tr>";
			for(Object[] col : row) {
				html += "<td colspan='" + col[0] + "' rowspan='" + col[1] + "'>";
				String location = (String) col[2];
				if(location == null){
					html += "<div class='claymus-h2' style='text-align:center;'>";
					html += "Page Contents";
					html += "</div>";
				} else {
					html += "<ul id='claymus-Blocks-" + col[2] + "' class='claymus-Blocks-Sortable'>";

					for(Block block : BlockData.getBlocks(location)) {
						disabledBlocks.remove(block);
						html += createBlockDummy(block, this.accessLevel);
					}

					html += "</ul>";
				}
				html += "</td>";
			}
			html += "</tr>";
		}
		html += "</table>";

		html += "<div class='claymus-h2' style='text-align:center; padding:15px'>";
		html += "Disabled Blocks";
		html += "</div>";

		html += "<ul id='claymus-Blocks-DISABLED' class='claymus-Blocks-Sortable'>";

		for (Block block : disabledBlocks)
			html += createBlockDummy(block, this.accessLevel);

		html += "</ul>";

		html += Resources.getJQuery("jquery-1.4.2.js");
		html += Resources.getJQuery("ui/jquery.ui.core.js");
		html += Resources.getJQuery("ui/jquery.ui.widget.js");
		html += Resources.getJQuery("ui/jquery.ui.mouse.js");
		html += Resources.getJQuery("ui/jquery.ui.sortable.js");
		html += "<script type='text/javascript' src='/com.claymus.site/com.claymus.site.module.block.pages.manage.js'></script>";

		if(this.accessLevel >= ModuleHelper.ADD)
			html += "<script type='text/javascript' src='/com.claymus.site.module.block.pages.manage.gwt/com.claymus.site.module.block.pages.manage.gwt.nocache.js'></script>";

		return html;
	}


	private String createBlockDummy(Block block, int accessLevel) {
		String encoded = KeyFactory.keyToString(block.getKey());
		String html = "<li id='claymus-Blocks-" + encoded + "' class='claymus-t3'>";
			html += block.getName();
			if(block.hasEditor()) {
				html += " <span class='claymus-sub-text'>(";
					if(accessLevel >= ModuleHelper.ADD_EDIT || (accessLevel == ModuleHelper.ADD && block.getOwner().equals(this.user)))
						html += "<a href='/_ah/block/edit?key=" + encoded + "'>edit</a>";
					else
						html += "<span class='claymus-faded-text'>edit</span>";
				html += ")</span>";
			}
		return html += "</li>";
	}

}
