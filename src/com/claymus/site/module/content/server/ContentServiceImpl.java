package com.claymus.site.module.content.server;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import com.claymus.User;
import com.claymus.UserData;
import com.claymus.UserRole;
import com.claymus.gwt.ServerException;
import com.claymus.gwt.UserException;
import com.claymus.site.Module;
import com.claymus.site.ModuleData;
import com.claymus.site.module.content.Content;
import com.claymus.site.module.content.ContentData;
import com.claymus.site.module.content.ContentType;
import com.claymus.site.module.content.ModuleHelper;
import com.claymus.site.module.content.gwt.ContentDTO;
import com.claymus.site.module.content.gwt.ContentService;
import com.claymus.site.module.page.Page;
import com.claymus.site.module.page.PageData;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

@SuppressWarnings("serial")
public class ContentServiceImpl extends RemoteServiceServlet implements ContentService {

	@Override
	public String[][] getLocations(String pageEncoded) {
		Page page = PageData.getPage(KeyFactory.stringToKey(pageEncoded));
		return page.getLayout().getLocations();
	}

	@Override
	public String[][] getRoles(){
		List<UserRole> userRoles = UserData.getUserRoles();
		String[][] roleList = new String[userRoles.size()][2];
		for(int i = 0; i < userRoles.size(); i++) {
			UserRole userRole = userRoles.get(i);
			roleList[i][0] = KeyFactory.keyToString(userRole.getKey());
			roleList[i][1] = userRole.getName();
		}
		return roleList;
	}


	@Override
	public ContentDTO get(String pageEncoded, String encoded) throws ServerException, UserException {
		Module module = ModuleData.getModule(ModuleHelper.class);
		User user = UserData.getUser();
		Page page = PageData.getPage(KeyFactory.stringToKey(pageEncoded));
		Content content = ContentData.getContent(KeyFactory.stringToKey(encoded));

		if(page.getId() != content.getPageId())
			throw new ServerException();

		int accessLevel = module.getAccessLevel(user.getRole());
		if(accessLevel >= ModuleHelper.ADD_EDIT || (accessLevel == ModuleHelper.ADD && content.getOwner().equals(user))) {
			ContentDTO contentDTO = content.getDTO();
			contentDTO.setLocations(page.getLayout().getLocations());
			contentDTO.setRoles(getRoles());
			return contentDTO;
		} else {
			throw new UserException();
		}

	}


	@Override
	public void add(String pageEncoded, ContentDTO contentDTO) throws ServerException, UserException {
		Module module = ModuleData.getModule(ModuleHelper.class);
		User user = UserData.getUser();

		int accessLevel = module.getAccessLevel(user.getRole());
		if(accessLevel < ModuleHelper.ADD)
			throw new UserException();

		Page page = PageData.getPage(KeyFactory.stringToKey(pageEncoded));
		ContentType contentData = ContentData.getContentType(contentDTO.getClass().getSimpleName().replace("DTO", ""));
		Content content = new Content(contentData, page.getId(), contentDTO.getLocation());
		content.update(contentDTO);
		content = ContentData.createContent(content);
		if(content == null)
			throw new ServerException("Content could not be created. Please try again later.");
	}

	@Override
	public void update(String encoded, ContentDTO contentDTO) throws ServerException, UserException {
		Module module = ModuleData.getModule(ModuleHelper.class);
		User user = UserData.getUser();
		Key key = KeyFactory.stringToKey(encoded);
		Content content = ContentData.getContent(key);

		int accessLevel = module.getAccessLevel(user.getRole());
		if(accessLevel >= ModuleHelper.ADD_EDIT || (accessLevel == ModuleHelper.ADD && content.getOwner().equals(user))) {
			if(ContentData.updateContent(key, contentDTO) == null)
				throw new ServerException("Content could not be saved. Please try again later.");
		} else {
			throw new UserException();
		}
	}


	@Override
	public void saveOrder(String pageEncoded, LinkedList<String> locations, LinkedList<String> encodedList) throws ServerException, UserException {
		Module module = ModuleData.getModule(ModuleHelper.class);
		User user = UserData.getUser();
		Page page = PageData.getPage(KeyFactory.stringToKey(pageEncoded));

		int accessLevel = module.getAccessLevel(user.getRole());
		if(accessLevel < ModuleHelper.ADD)
			throw new UserException();

		for(int i = 0; i < locations.size(); i++) {
			LinkedList<Content> contentList = new LinkedList<Content>();
			LinkedList<Long> weights = new LinkedList<Long>();

			StringTokenizer st = new StringTokenizer(encodedList.get(i), ",");
			while(st.hasMoreTokens()) {
				String token = st.nextToken();
				Content content = ContentData.getContent(KeyFactory.stringToKey(token));
				if(content.getPageId() == page.getId()) {
					contentList.add(content);
					weights.add(content.getWeight());
				}
			}

			Collections.sort(weights);

			for(int j = 0; j < contentList.size(); j++) {
				Content content = contentList.get(j);
				content = ContentData.updateContentOrder(content.getKey(), weights.get(j), locations.get(i));
				if(content == null)
					throw new ServerException("Operation failed partially or fully. <a href=''>Refresh</a> page to see the changes.");
			}
		}
	}

}