package com.claymus.site.module.block.server;

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
import com.claymus.site.module.block.Block;
import com.claymus.site.module.block.BlockData;
import com.claymus.site.module.block.BlockType;
import com.claymus.site.module.block.ModuleHelper;
import com.claymus.site.module.block.gwt.BlockDTO;
import com.claymus.site.module.block.gwt.BlockService;
import com.claymus.site.module.theme.ThemeData;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

@SuppressWarnings("serial")
public class BlockServiceImpl extends RemoteServiceServlet implements BlockService {

	@Override
    public String[][] getLocations() {
		return ThemeData.getTheme().getLocations();
    }

	@Override
	public String[][] getRoles() {
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
	public BlockDTO get(String encoded) throws UserException {
		Module module = ModuleData.getModule(ModuleHelper.class);
		User user = UserData.getUser();
		Block block = BlockData.getBlock(KeyFactory.stringToKey(encoded));

		int accessLevel = module.getAccessLevel(user.getRole());
		if(accessLevel >= ModuleHelper.ADD_EDIT || (accessLevel == ModuleHelper.ADD && block.getOwner().equals(user))) {
			BlockDTO blockDTO = block.getDTO();
			blockDTO.setLocations(getLocations());
			blockDTO.setRoles(getRoles());
			return blockDTO;
		} else {
			throw new UserException();
		}
	}


	@Override
	public void add(BlockDTO blockDTO) throws ServerException, UserException {
		Module module = ModuleData.getModule(ModuleHelper.class);
		User user = UserData.getUser();

		int accessLevel = module.getAccessLevel(user.getRole());
		if(accessLevel < ModuleHelper.ADD)
			throw new UserException();

		BlockType blockData = BlockData.getBlockType(blockDTO.getClass().getSimpleName().replace("DTO", ""));
		Block block = new Block(blockData, blockDTO.getLocation());
		block.update(blockDTO);
		block = BlockData.createBlock(block);
		if(block == null)
			throw new ServerException("Block could not be created. Please try again later.");
	}

	@Override
	public void update(String encoded, BlockDTO blockDTO) throws ServerException, UserException {
		Module module = ModuleData.getModule(ModuleHelper.class);
		User user = UserData.getUser();
		Key key = KeyFactory.stringToKey(encoded);
		Block block = BlockData.getBlock(key);

		int accessLevel = module.getAccessLevel(user.getRole());
		if(accessLevel >= ModuleHelper.ADD_EDIT || (accessLevel == ModuleHelper.ADD && block.getOwner().equals(user))) {
			if(BlockData.updateBlock(key, blockDTO) == null)
				throw new ServerException("Block could not be saved. Please try again later.");
		} else {
			throw new UserException();
		}
	}


	@Override
	public void saveOrder(LinkedList<String> locations, LinkedList<String> encodedList) throws ServerException, UserException {
		Module module = ModuleData.getModule(ModuleHelper.class);
		User user = UserData.getUser();

		int accessLevel = module.getAccessLevel(user.getRole());
		if(accessLevel < ModuleHelper.ADD)
			throw new UserException();

		for(int i = 0; i < locations.size(); i++) {
			LinkedList<Block> blocks = new LinkedList<Block>();
			LinkedList<Long> weights = new LinkedList<Long>();

			StringTokenizer st = new StringTokenizer(encodedList.get(i), ",");
			while(st.hasMoreTokens()) {
				String token = st.nextToken();
				Block block = BlockData.getBlock(KeyFactory.stringToKey(token));
				blocks.add(block);
				weights.add(block.getWeight());
			}

			Collections.sort(weights);

			for(int j = 0; j < blocks.size(); j++) {
				Block block = blocks.get(j);
				block = BlockData.updateBlockOrder(block.getKey(), weights.get(j), locations.get(i));
				if(block == null)
					throw new ServerException("Operation failed partially or fully. <a href=''>Refresh</a> the page to see the changes.");
			}
		}
	}

}