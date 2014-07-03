package com.claymus.site.module.page.server;

import java.util.List;

import com.claymus.User;
import com.claymus.UserData;
import com.claymus.gwt.ServerException;
import com.claymus.gwt.UserException;
import com.claymus.site.Module;
import com.claymus.site.ModuleData;
import com.claymus.site.module.page.ModuleHelper;
import com.claymus.site.module.page.Page;
import com.claymus.site.module.page.PageData;
import com.claymus.site.module.page.PageLayout;
import com.claymus.site.module.page.gwt.PageService;
import com.claymus.site.module.page.pages.gwt.PageDTO;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

@SuppressWarnings("serial")
public class PageServiceImpl extends RemoteServiceServlet implements PageService {

	@Override
	public String[][] getLayouts() {
		List<PageLayout> layouts = PageData.getPageLayouts();
		String[][] layoutArr = new String[layouts.size()][2];
		for(int i = 0; i < layouts.size(); i++) {
			layoutArr[i][0] = layouts.get(i).getName();
			layoutArr[i][1] = layouts.get(i).getClass().getSimpleName();
		}
		return layoutArr;
	}


	@Override
	public PageDTO get(String encoded) throws UserException {
		Module module = ModuleData.getModule(ModuleHelper.class);
		User user = UserData.getUser();
		Page page = PageData.getPage(KeyFactory.stringToKey(encoded));

		int accessLevel = module.getAccessLevel(user.getRole());
		if(accessLevel >= ModuleHelper.ADD_EDIT || (accessLevel == ModuleHelper.ADD && page.getCreator().equals(user))) {
			PageDTO pageDTO = page.getDTO();
			pageDTO.setLayouts(getLayouts());
			return pageDTO;
		} else {
			throw new UserException();
		}

	}


	@Override
	public String add(PageDTO pageDTO) throws ServerException, UserException {
		Module module = ModuleData.getModule(ModuleHelper.class);
		User user = UserData.getUser();

		int accessLevel = module.getAccessLevel(user.getRole());
		if(accessLevel < ModuleHelper.ADD)
			throw new UserException();

		Page page = PageData.getPage(pageDTO.getUri());
		if(page != null)
			throw new ServerException("Page with uri " + "<a href='" + page.getUri() + "'>" + page.getUri() + "</a> already exists. Please enter a different uri.");

		page = new Page(pageDTO);
		if(PageData.createPage(page) == null)
			throw new ServerException("Page could not be created. Please try again later.");

		return KeyFactory.keyToString(page.getKey());
	}

	@Override
	public void update(PageDTO pageDTO) throws ServerException, UserException {
		Module module = ModuleData.getModule(ModuleHelper.class);
		User user = UserData.getUser();
		Page page = PageData.getPage(pageDTO.getUri());

		int accessLevel = module.getAccessLevel(user.getRole());
		if(accessLevel >= ModuleHelper.ADD_EDIT || (accessLevel == ModuleHelper.ADD && page.getCreator().equals(user))) {
			if(PageData.updatePage(pageDTO) == null)
				throw new ServerException("Page could not be saved. Please try again.");
		} else {
			throw new UserException();
		}

	}

}