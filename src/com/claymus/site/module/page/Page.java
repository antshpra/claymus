package com.claymus.site.module.page;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.servlet.ServletException;

import com.claymus.ClaymusMain;
import com.claymus.PersistentCapable;
import com.claymus.User;
import com.claymus.UserData;
import com.claymus.site.SiteData;
import com.claymus.site.module.block.Block;
import com.claymus.site.module.content.Content;
import com.claymus.site.module.page.layouts.Simple;
import com.claymus.site.module.page.pages.gwt.PageDTO;
import com.claymus.site.module.theme.Theme;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

@PersistenceCapable(detachable = "true")
public class Page extends PersistentCapable {

	private static final long serialVersionUID = -9205940445471912636L;

	@Persistent(column = "Uri", primaryKey = "true", valueStrategy = IdGeneratorStrategy.IDENTITY)
	private String uri;

	@Persistent(column = "ID")
	private Long id;

	@Persistent(column = "Title")
	private String title;

	@Persistent(column = "Layout")
	private String layout;

	@Persistent(column = "Created")
	private Date created;

	@Persistent(column = "Creator")
	private Key creator;

	/*
	 * Constructors
	 */

	public Page(PageDTO pageDTO) {
		this(pageDTO.getUri(), pageDTO.getTitle(), pageDTO.getLayout());

	}

	public Page(String uri, String title, PageLayout layout) {
		this(uri, title, layout.getClass().getSimpleName());
	}

	private Page(String uri, String title, String layout) {
		this.uri = uri;
		this.id = new Date().getTime();
		this.title = title;
		this.layout = layout;
		this.created = new Date(this.id);
		this.creator = UserData.getUser().getKey();
	}

	/*
	 * Inherited method(s)
	 */

	@Override
	public Key getKey() {
		return KeyFactory.createKey(Page.class.getSimpleName(), this.uri);
	}

	@Override
	public boolean equals(Object obj) {
		if(this == obj)
			return true;
		else if(obj instanceof Page)
			return ((Page) obj).getKey().equals(getKey());
		else
			return false;
	}

	/*
	 * Getter and Setter methods
	 */

	public String getUri(){
		return this.uri;
	}

	public long getId() {
		return this.id;
	}

	public String getTitle() {
		return this.title;
	}

	private void setTitle(String title) {
		this.title = title;
	}

	public PageLayout getLayout() {
		PageLayout pageLayout = PageData.getPageLayout(this.layout);
		return pageLayout == null ? new Simple() : pageLayout;
	}

	private void setLayout(String pageLayout) {
		this.layout = pageLayout;
	}

	public Date getCreated() {
		return this.created;
	}

	public User getCreator() {
		User user = UserData.getUser(this.creator);
		return user == null ? UserData.getSystem() : user;
	}


	public PageDTO getDTO() {
		PageDTO pageDTO = new PageDTO();
		pageDTO.setUri(getUri());
		pageDTO.setTitle(getTitle());
		pageDTO.setLayout(getLayout().getClass().getSimpleName());
		return pageDTO;
	}

	public void update(PageDTO pageDTO) {
		setTitle(pageDTO.getTitle());
		setLayout(pageDTO.getLayout());
	}

	/*
	 * Helper Methods
	 */

	protected String getDocType() {
		return "<!DOCTYPE html PUBLIC "
				+ "\"-//W3C//DTD HTML 4.01 Transitional//EN\" "
				+ "\"http://www.w3.org/TR/html4/loose.dtd\"" + ">";
	}

	protected String getContentType() { // TODO : utf?
		return "<meta " + "http-equiv=\"Content-Type\" "
				+ "content=\"text/html; charset=ISO-8859-1\"" + ">";
	}

	public void serve(List<List<Content>> contents, List<List<Block>> blocks, Theme theme) throws ServletException, IOException {

		PrintWriter out = ClaymusMain.getResponse().getWriter();

		// DOC TYPE
		out.print(getDocType());

		out.print("<html>");

			// PAGE HEAD
			out.print("<head>");
				out.print("<link rel=\"shortcut icon\" href=\"" + theme.getFavicon() + "\" type=\"image/x-icon\">");
				out.print(getContentType());
				out.print(theme.getCSS());
				out.print("<title>");
					String pageTitle = getTitle();
					if(pageTitle != null)
						out.print(pageTitle + SiteData.getSiteTitleSeparator() + SiteData.getSiteTitle());
					else
						out.print(SiteData.getSiteTitle());
				out.print("</title>");
			out.print("</head>");

			// PAGE BODY
			out.print("<body>");

				out.print("<div id='claymus-body'>");

					out.print("<iframe src=\"javascript:''\" id=\"__gwt_historyFrame\" style=\"width:0;height:0;border:0\"></iframe>");

//					for(Block block : BlockData.getBlocks("INVISIBLE_TOP"))
//						out.print(block.getContent());

					theme.generateLayout(contents, blocks, getLayout(), out);

//					for(Block block : BlockData.getBlocks("INVISIBLE_BOTTOM"))
//						out.print(block.getContent());

				out.print("</div>");

			out.print("</body>");

		out.print("</html>");

		out.close();

	}

}