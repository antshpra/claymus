package com.claymus.site.module.page;

import javax.jdo.annotations.PersistenceCapable;

import com.claymus.ClaymusMain;
import com.claymus.User;
import com.claymus.site.Error401;
import com.claymus.site.Module;
import com.claymus.site.ModuleData;
import com.claymus.site.module.content.ContentType;
import com.claymus.site.module.page.pages.ManagePages;
import com.google.appengine.api.datastore.KeyFactory;

@PersistenceCapable(detachable = "true")
public class ModuleHelper extends Module {

	private static final long serialVersionUID = -5999759106462932706L;

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
		return "Page";
	}

	@Override
	public String getDescription() {
		return "<a href='/_ah/page'>Manage Pages</a>, " +
			   "<a href='/_ah/page/new'>Add Page</a>";
	}


	@Override
	public String[] getAccessLevelNames() {
		return new String[] {"View Only", "Add Page", "Add & Edit Page", "Add, Edit & Delete Page"};
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
			if(accessLevel >= ModuleHelper.VIEW_ONLY) {
				Module contentModule = ModuleData.getModule(com.claymus.site.module.content.ModuleHelper.class);
				if(contentModule == null)
					return new ManagePages(accessLevel, Module.NO_ACCESS, user);
				else
					return new ManagePages(accessLevel, contentModule.getAccessLevel(user.getRole()), user);
			} else {
				return new Error401();
			}

		} else if(tokens.length == 2 && tokens[1].equals("new")) {
			if(accessLevel >= ModuleHelper.ADD)
				return new ContentType() {

					@Override
					public String getName() {
						return "Add Page";
					}

					@Override
					protected String getHTML() {
						return "<div class='claymus-h1'>" + getName() + "</div>" +
								"<div id='claymus-PageEditor'></div>" +
								"<script type='text/javascript' src='/com.claymus.site.module.page.pages.editor.gwt/com.claymus.site.module.page.pages.editor.gwt.nocache.js'></script>";
					}

				};
			else
				return new Error401();

		} else if(tokens.length == 2 && tokens[1].equals("edit")) {
			String encoded = ClaymusMain.getRequest().getParameter("key");
			if(encoded == null)
				return null;

			final Page page = PageData.getPage(KeyFactory.stringToKey(encoded));
			if(page == null)
				return null;

			if(accessLevel >= ModuleHelper.ADD_EDIT || (accessLevel == ModuleHelper.ADD && page.getCreator().equals(user)))
				return new ContentType() {

					@Override
					public String getName() {
						return page.getTitle() != null
								? "Editing Page: " + page.getTitle()
								: "Editing Page: " + "(no title)";
					}

					@Override
					protected String getHTML() {
						return "<div class='claymus-h1'>" + getName() + "</div>" +
								"<div id='claymus-PageEditor'></div>" +
								"<script type='text/javascript' src='/com.claymus.site.module.page.pages.editor.gwt/com.claymus.site.module.page.pages.editor.gwt.nocache.js'></script>";
					}

				};
			else
				return new Error401();

		} else {
			return null;
		}
	}

}
