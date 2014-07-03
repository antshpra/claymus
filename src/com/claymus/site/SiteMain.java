package com.claymus.site;

import java.io.IOException;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.claymus.Logger;
import com.claymus.User;
import com.claymus.UserData;
import com.claymus.UserRole;
import com.claymus.site.module.block.Block;
import com.claymus.site.module.block.BlockData;
import com.claymus.site.module.content.Content;
import com.claymus.site.module.content.ContentData;
import com.claymus.site.module.content.ContentType;
import com.claymus.site.module.page.Page;
import com.claymus.site.module.page.PageData;
import com.claymus.site.module.page.PageLayout;
import com.claymus.site.module.page.layouts.Simple;
import com.claymus.site.module.theme.ThemeData;
import com.google.appengine.api.datastore.Key;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/*
 * Thread-Safe: Synchronized wherever required. HashTable is synchronized.
 */
@SuppressWarnings("serial")
public class SiteMain extends HttpServlet {

	private static final Map<String, RemoteServiceServlet> rpcServlets = new Hashtable<String, RemoteServiceServlet>();

	/*
	 * Request Handlers
	 */

	@Override
	protected void service(HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
		if(req.getHeader("X-GWT-Module-Base") != null && req.getHeader("X-GWT-Permutation") != null) {
			String moduleId = req.getRequestURI().split("/")[2];
			RemoteServiceServlet servlet = SiteMain.rpcServlets.get(moduleId);
			if(servlet == null) {
				try {
					servlet = (RemoteServiceServlet) Class.forName(Module.MODULE_PACKAGE + "." + moduleId + ".server." + Character.toUpperCase(moduleId.charAt(0)) + moduleId.substring(1) + "ServiceImpl").newInstance();
				} catch (Exception ex) {
					Logger.get().log(Level.SEVERE, null, ex);
					resp.sendError(500);
					return;
				}
				servlet.init(this.getServletConfig());
				SiteMain.rpcServlets.put(moduleId, servlet);
			}
			servlet.service(req, resp);
		} else {
			super.service(req, resp);
		}
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doRequest(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doRequest(req, resp);
	}

	protected void doRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String uri = req.getRequestURI();
		Page page = null;
		List<List<Content>> contents = null;
		User user = UserData.getUser();
		UserRole userRole = user.getRole();
		Key userRoleKey = userRole.getKey();

		if(uri.startsWith("/_ah/")) { // Administer pages
			String str = uri.substring(5);
			String moduleId = str.split("/")[0];

			Module module = ModuleData.getModule(moduleId);
			List<ContentType> contentsData = module == null
					? new LinkedList<ContentType>()
					: module.getPageContents(str.split("/"), user);

			if(contentsData.size() == 0)
				contentsData.add(new Error404());

			PageLayout pageLayout = new Simple();

			List<Content> contentList = new LinkedList<Content>();
			for(ContentType contentData : contentsData)
				contentList.add(new Content(contentData, 0, pageLayout.getDefaultLocation()));

			page = new Page(uri, contentList.get(0).getName(), pageLayout);

			contents = new LinkedList<List<Content>>();
			contents.add(contentList);

		} else if((page = PageData.getPage(uri)) != null) { // User pages
			PageLayout pageLayout = page.getLayout();
			contents = new LinkedList<List<Content>>();
			for(String[] location : pageLayout.getLocations())
				contents.add(ContentData.getContents(page.getId(), location[1], userRoleKey));

		} else { // Error Pages
			PageLayout pageLayout = new Simple();

			Content content = new Content(new Error404(), 0, pageLayout.getDefaultLocation());
			List<Content> contentList = new LinkedList<Content>();
			contentList.add(content);

			page = new Page(uri, content.getName(), pageLayout);

			contents = new LinkedList<List<Content>>();
			contents.add(contentList);
		}

		List<List<Block>> blocks = new LinkedList<List<Block>>();
		String[][] blockLocations = ThemeData.getTheme().getLocations();
		for(String[] location : blockLocations)
			blocks.add(BlockData.getBlocks(location[1], uri, userRoleKey));

		page.serve(contents, blocks, ThemeData.getTheme());
	}

}
