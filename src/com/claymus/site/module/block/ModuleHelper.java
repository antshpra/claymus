package com.claymus.site.module.block;

import javax.jdo.annotations.PersistenceCapable;

import com.claymus.ClaymusMain;
import com.claymus.User;
import com.claymus.site.Error401;
import com.claymus.site.Module;
import com.claymus.site.module.block.pages.BlockTypes;
import com.claymus.site.module.block.pages.ManageBlocks;
import com.claymus.site.module.content.ContentType;
import com.google.appengine.api.datastore.KeyFactory;

@PersistenceCapable(detachable = "true")
public class ModuleHelper extends Module {

	private static final long serialVersionUID = 1751245622124669028L;

	public static final int VIEW_ONLY 		= 1;
	public static final int ADD 			= 2;
	public static final int ADD_EDIT 		= 3;
	public static final int ADD_EDIT_DELETE = 4;

	/*
	 * Inherited Method
	 */

	@Override
	public double getVersion() {
		return 0.99;
	}

	@Override
	public String getName() {
		return "Block";
	}

	@Override
	public String getDescription() {
		return "<a href='/_ah/block'>Manage Blocks</a>";
	}


	@Override
	public String[] getAccessLevelNames() {
		return new String[] {"View Only", "Add Block", "Add & Edit Block", "Add, Edit & Delete Block"};
	}

	@Override
	protected int getFullAccessLevel() {
		return ModuleHelper.ADD_EDIT_DELETE;
	}


	@Override
	@SuppressWarnings("serial")
	public ContentType getPageContent(String[] tokens, User user) {
		int accessLevel = getAccessLevel(user.getRole());

		if(tokens.length == 1) {
			if(accessLevel >= ModuleHelper.VIEW_ONLY)
				return new ManageBlocks(accessLevel, user);
			else
				return new Error401();

		} else if(tokens.length == 2 && tokens[1].equals("add")) {
			if(accessLevel >= ModuleHelper.ADD)
				return new BlockTypes();
			else
				return new Error401();

		} else if(tokens.length == 2 && tokens[1].equals("new")) {
			String blockType = ClaymusMain.getRequest().getParameter("type");
			if(blockType == null)
				return null;

			final BlockType blockData = BlockData.getBlockType(blockType);
			if(blockData == null)
				return null;

			if(! blockData.hasEditor())
				return null;

			if(accessLevel >= ModuleHelper.ADD)
				return new ContentType() {

					@Override
					public String getName() {
						return "Add Block \u00BB " + blockData.getName();
					}

					@Override
					protected String getHTML() {
						String html = "<div class='claymus-h1'>" + getName() + "</div>";
						html += blockData.getEditor();
						return html;
					}

				};
			else
				return new Error401();

		} else if(tokens.length == 2 && tokens[1].equals("edit")) {
			String encoded = ClaymusMain.getRequest().getParameter("key");
			if(encoded == null)
				return null;

			final Block block = BlockData.getBlock(KeyFactory.stringToKey(encoded));
			if(block == null)
				return null;

			if(accessLevel >= ModuleHelper.ADD_EDIT || (accessLevel == ModuleHelper.ADD && block.getOwner().equals(user)))
				return new ContentType() {

					@Override
					public String getName() {
						return "Edit Block \u00BB " + block.getName();
					}

					@Override
					protected String getHTML() {
						String html = "<div class='claymus-h1'>" + getName() + "</div>";
						html += block.getEditor();
						return html;
					}

				};
			else
				return new Error401();

		} else {
			return null;

		}
	}

}

