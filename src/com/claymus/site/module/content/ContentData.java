package com.claymus.site.module.content;

import java.io.File;
import java.net.URL;
import java.util.Date;
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
import com.claymus.UserData;
import com.claymus.site.Module;
import com.claymus.site.module.content.gwt.ContentDTO;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

public class ContentData {

	public static ContentType getContentType(String className) {
		try {
			return (ContentType) Class.forName(Module.MODULE_PACKAGE + ".content.types." + className).newInstance();
		} catch(Exception ex) {
			Logger.get().log(Level.SEVERE, null, ex);
			return null;
		}
	}

	public static List<ContentType> getContentTypes() {
		LinkedList<ContentType> types = new LinkedList<ContentType>();
		URL url = ContentData.class.getResource(Module.MODULE_PATH + "content/types");
		File dir = new File(url.getPath());
		File[] list = dir.listFiles();
		for(File file : list) {
			if(file.isFile() && file.getName().endsWith(".class")) {
				String className = file.getName().split("[.]")[0];
				ContentType contentType = ContentData.getContentType(className);
				if(contentType != null)
					types.add(contentType);
			}
		}
		return types;
	}


    public static Content getContent(String id) {
		return ContentData.getContent(KeyFactory.createKey(Content.class.getSimpleName(), id));
	}

	public static Content getContent(Key key) {
		return Datastore.getEntity(Content.class, key);
	}


    public static List<Content> getContents(long pageId) {
    	return ContentData.getContents(pageId, null);
    }

	@SuppressWarnings("unchecked")
	public static List<Content> getContents(long pageId, String contentLocation) {
		LinkedList<Key> contentKeys = contentLocation == null
				? (LinkedList<Key>) Memcache.get("Contents." + pageId)
				: (LinkedList<Key>) Memcache.get("Contents." + pageId + "." + contentLocation);

		if(contentKeys == null) {
			contentKeys = new  LinkedList<Key>();

			PersistenceManager pm = PMF.get();
			Query query = pm.newQuery(Content.class);
			query.setFilter("pageId == " + pageId + (contentLocation == null ? "" : " && location == '" + contentLocation + "'"));
			query.setOrdering("weight");
			query.setResult("key");
			contentKeys.addAll((List<Key>) query.execute());
			query.closeAll();
			pm.close();

			if(contentLocation == null)
				Memcache.put("Contents." + pageId, contentKeys);
			else
				Memcache.put("Contents." + pageId + "." + contentLocation, contentKeys);
		}

		LinkedList<Content> contentList = new LinkedList<Content>();
		for (Key contentKey : contentKeys) {
			Content content = ContentData.getContent(contentKey);
			if(content != null)
				contentList.add(content);
		}

		return contentList;
	}

	public static List<Content> getContents(long pageId, String contentLocation, Key userRoleKey) {
		List<Content> contentList = ContentData.getContents(pageId, contentLocation);
		for(int i = 0; i < contentList.size(); i++)
			if(! contentList.get(i).isVisibleTo(userRoleKey))
				contentList.remove(i--);
		return contentList;
	}


	public static Content createContent(final Content content) {
		Content newContent = new DatastoreTransaction<Content>() {

			@Override
			protected boolean updateEntity(Content datastoreEntity, Object... argvs) {
				if(datastoreEntity != content) // Entity with same key exists. Stored entity will be returned.
					return false;
				else // New entity with or without key
					return true;
			}

		}.makePersistent(content);

		if(newContent != null) {
			Memcache.remove("Contents." + newContent.getPageId());
			Memcache.remove("Contents." + newContent.getPageId() + "." + newContent.getLocation());
		}

		return newContent;
	}

	public static Content updateContent(Key key, ContentDTO contentDTO) {
		String oldLocation = ContentData.getContent(key).getLocation();

		Content content = new DatastoreTransaction<Content>() {

			@Override
			protected boolean updateEntity(Content datastoreEntity, Object... argvs) {
				datastoreEntity.update((ContentDTO) argvs[0]);
				datastoreEntity.setUpdated(new Date());
				datastoreEntity.setUpdater(UserData.getUser().getKey());
				return true;
			}

		}.makePersistent(Content.class, key, contentDTO);

		if(content != null) {
			String newLocation = content.getLocation();
			if(! oldLocation.equals(newLocation)) {
//				Memcache.remove("Contents." + content.getPageId());
				Memcache.remove("Contents." + content.getPageId() + "." + oldLocation);
				Memcache.remove("Contents." + content.getPageId() + "." + newLocation);
			}
		}

		return content;
	}

	public static Content updateContentOrder(Key key, long weight, String location) {
		String oldLocation = ContentData.getContent(key).getLocation();

		Content content = new DatastoreTransaction<Content>() {

			@Override
			protected boolean updateEntity(Content datastoreEntity, Object... argvs) {
				datastoreEntity.setWeight((Long) argvs[0]);
				datastoreEntity.setLocation((String) argvs[1]);
				return true;
			}

		}.makePersistent(Content.class, key, weight, location);

		if(content != null) {
			Memcache.remove("Contents." + content.getPageId());
			Memcache.remove("Contents." + content.getPageId() + "." + oldLocation);

			String newLocation = content.getLocation();
			if(! oldLocation.equals(newLocation))
				Memcache.remove("Contents." + content.getPageId() + "." + newLocation);
		}

		return content;
	}

	public static boolean deleteContent(Content content) {  // TODO : use transaction
		return Datastore.makeTrasient(content);
	}

}
