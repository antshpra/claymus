package com.claymus.site.module.content;

import javax.jdo.annotations.PersistenceCapable;

import com.claymus.ClaymusMain;
import com.claymus.User;
import com.claymus.site.Error401;
import com.claymus.site.Module;
import com.claymus.site.module.content.pages.ContentTypes;
import com.claymus.site.module.content.pages.ManageContents;
import com.claymus.site.module.page.Page;
import com.claymus.site.module.page.PageData;
import com.google.appengine.api.datastore.KeyFactory;

@PersistenceCapable(detachable = "true")
public class ModuleHelper extends Module {

	private static final long serialVersionUID = -4424680069550102155L;

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
		return "Content";
	}

	@Override
	public String getDescription() {
		return "<a href='/_ah/content'>Manage Contents</a>";
	}


	@Override
	public String[] getAccessLevelNames() {
		return new String[] {"View Only", "Add Content", "Add & Edit Content", "Add, Edit & Delete Content"};
	}

	@Override
	protected int getFullAccessLevel() {
		return ModuleHelper.ADD_EDIT_DELETE;
	}


	@Override
	@SuppressWarnings("serial")
	public ContentType getPageContent(String[] tokens, User user) {
		String pageEncoded = ClaymusMain.getRequest().getParameter("page");
		if(pageEncoded == null)
			return null;

		final Page page = PageData.getPage(KeyFactory.stringToKey(pageEncoded));
		if(page == null)
			return null;

		int accessLevel = getAccessLevel(user.getRole());

		if(tokens.length == 1) {
			if(accessLevel >= ModuleHelper.VIEW_ONLY)
				return new ManageContents(accessLevel, page, user);
			else
				return new Error401();

		} else if(tokens.length == 2 && tokens[1].equals("add")) {
			if(accessLevel >= ModuleHelper.ADD)
				return new ContentTypes(page);
			else
				return new Error401();

		} else if(tokens.length == 2 && tokens[1].equals("new")) {
			String contentType = ClaymusMain.getRequest().getParameter("type");
			if(contentType == null)
				return null;

			final ContentType contentData = ContentData.getContentType(contentType);
			if(contentData == null)
				return null;

			if(! contentData.hasEditor())
				return null;

			if(accessLevel >= ModuleHelper.ADD)
				return new ContentType() {

					@Override
					public String getName() {
						return page.getTitle() != null
								? page.getTitle() + " \u00BB Add Content \u00BB " + contentData.getName()
								: "(no title)" 	  + " \u00BB Add Content \u00BB " + contentData.getName();
					}

					@Override
					protected String getHTML() {
						String html = "<div class='claymus-h1'>" + getName() + "</div>";
						html += contentData.getEditor();
						return html;
					}

				};
			else
				return new Error401();

		} else if(tokens.length == 2 && tokens[1].equals("edit")) {
			String encoded = ClaymusMain.getRequest().getParameter("key");
			if(encoded == null)
				return null;

			final Content content = ContentData.getContent(KeyFactory.stringToKey(encoded));
			if(content == null)
				return null;

			if(page.getId() != content.getPageId())
				return null;

			if(accessLevel >= ModuleHelper.ADD_EDIT || (accessLevel == ModuleHelper.ADD && content.getOwner().equals(user)))
				return new ContentType() {

					@Override
					public String getName() {
						return page.getTitle() != null
								? page.getTitle() + " \u00BB Edit Content \u00BB " + content.getName()
								: "(no title)" 	  + " \u00BB Edit Content \u00BB " + content.getName();
					}

					@Override
					protected String getHTML() {
						String html = "<div class='claymus-h1'>" + getName() + "</div>";
						html += content.getEditor();
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
