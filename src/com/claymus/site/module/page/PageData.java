package com.claymus.site.module.page;

import java.io.File;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.claymus.Datastore;
import com.claymus.DatastoreTransaction;
import com.claymus.Logger;
import com.claymus.Memcache;
import com.claymus.PMF;
import com.claymus.site.Module;
import com.claymus.site.module.page.pages.gwt.PageDTO;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

public class PageData {

	public static PageLayout getPageLayout(String className) {
		try {
			return (PageLayout) Class.forName(Module.MODULE_PACKAGE + ".page.layouts." + className).newInstance();
		} catch(Exception ex) {
			Logger.get().log(Level.SEVERE, null, ex);
			return null;
		}
	}

	public static List<PageLayout> getPageLayouts() {
		LinkedList<PageLayout> layouts = new LinkedList<PageLayout>();
		URL url = PageData.class.getResource(Module.MODULE_PATH + "page/layouts");
		File dir = new File(url.getPath());
		File[] list = dir.listFiles();
		for(File file : list){
			if(file.isFile()) {
				String className = file.getName().split("[.]")[0];
				PageLayout pageLayout = PageData.getPageLayout(className);
				if(pageLayout != null)
					layouts.add(pageLayout);
			}
		}
		return layouts;
	}


	public static Page getPage(String uri) {
		return getPage(KeyFactory.createKey(Page.class.getSimpleName(), uri));
	}

	public static Page getPage(Key key) {
		return Datastore.getEntity(Page.class, key);
	}

	@Deprecated
	@SuppressWarnings("unchecked")
	public static List<Page> getAllPages() {
		LinkedList<String> pageUris = (LinkedList<String>) Memcache.get("Pages");

		if(pageUris == null) {
			pageUris = new  LinkedList<String>();

			PersistenceManager pm = PMF.get();
			Query query = pm.newQuery(Page.class);
			query.setOrdering("created DESC");
			query.setResult("uri");
			pageUris.addAll((List<String>) query.execute());
			query.closeAll();
			pm.close();

			Memcache.put("Pages", pageUris);
		}

		LinkedList<Page> pageList = new LinkedList<Page>();
		for(String pageUri : pageUris) {
			Page page = PageData.getPage(pageUri);
			if(page != null)
				pageList.add(page);
		}

		return pageList;
	}


	public static Page createPage(final Page page) {
		Page newPage = new DatastoreTransaction<Page>() {

			@Override
			protected boolean updateEntity(Page datastoreEntity, Object... argvs) {
				if(datastoreEntity != page) // Page with same uri exists. Stored page entity will be returned.
					return false;
				return true; // New page
			};

		}.makePersistent(page);

		if(newPage != null)
			Memcache.remove("Pages");

		return newPage;
	}

	public static Page updatePage(PageDTO pageDTO) {
		return new DatastoreTransaction<Page>() {

			@Override
			protected boolean updateEntity(Page datastoreEntity, Object... argvs) {
				datastoreEntity.update((PageDTO) argvs[0]);
				return true;
			}

		}.makePersistent(Page.class, pageDTO.getUri(), pageDTO);
	}

}
