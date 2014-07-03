package com.claymus.site.module.block;

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
import com.claymus.site.module.block.gwt.BlockDTO;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

public class BlockData {

	public static BlockType getBlockType(String className) {
		try {
			return (BlockType) Class.forName(Module.MODULE_PACKAGE + ".block.types." + className).newInstance();
		} catch(Exception ex) {
			Logger.get().log(Level.SEVERE, null, ex);
			return null;
		}
	}

	public static List<BlockType> getBlockTypes() {
		LinkedList<BlockType> types = new LinkedList<BlockType>();
		URL url = BlockData.class.getResource(Module.MODULE_PATH + "block/types");
		File dir = new File(url.getPath());
		File[] list = dir.listFiles();
		for(File file : list) {
			if(file.isFile() && file.getName().endsWith(".class")) {
				String className = file.getName().split("[.]")[0];
				BlockType blockType = BlockData.getBlockType(className);
				if(blockType != null)
					types.add(blockType);
			}
		}
		return types;
	}


	public static Block getBlock(String id) {
		return BlockData.getBlock(KeyFactory.createKey(Block.class.getSimpleName(), id));
	}

	public static Block getBlock(Key key) {
		return Datastore.getEntity(Block.class, key);
	}


	public static List<Block> getBlocks() {
		return BlockData.getBlocks(null);
	}

	@SuppressWarnings("unchecked")
	public static List<Block> getBlocks(String blockLocation) {
		LinkedList<Key> blockKeys = blockLocation == null
				? (LinkedList<Key>) Memcache.get("Blocks")
				: (LinkedList<Key>) Memcache.get("Blocks." + blockLocation);

		if(blockKeys == null) {
			blockKeys = new  LinkedList<Key>();

			PersistenceManager pm = PMF.get();
			Query query = pm.newQuery(Block.class);
			if(blockLocation != null)
				query.setFilter("location == '" + blockLocation + "'");
			query.setOrdering("weight");
			query.setResult("key");
			blockKeys.addAll((List<Key>) query.execute());
			query.closeAll();
			pm.close();

			if(blockLocation == null)
				Memcache.put("Blocks", blockKeys);
			else
				Memcache.put("Blocks." + blockLocation, blockKeys);
		}

		LinkedList<Block> blockList = new LinkedList<Block>();
		for (Key blockKey : blockKeys) {
			Block block = BlockData.getBlock(blockKey);
			if(block != null)
				blockList.add(block);
		}

		return blockList;
	}

	public static List<Block> getBlocks(String blockLocation, String uri, Key userRoleKey) {
		List<Block> blocks = BlockData.getBlocks(blockLocation);
		for(int i = 0; i < blocks.size(); i++)
			if(! blocks.get(i).isVisibleAt(uri) || ! blocks.get(i).isVisibleTo(userRoleKey))
				blocks.remove(i--);
		return blocks;
	}


	public static Block createBlock(final Block block) {
		Block newBlock = new DatastoreTransaction<Block>() {

			@Override
			protected boolean updateEntity(Block datastoreEntity, Object... argvs) {
				if(datastoreEntity != block) // Entity with same key exists. Stored entity will be returned.
					return false;
				else // New entity with or without key
					return true;
			}

		}.makePersistent(block);

		if(newBlock != null) {
			Memcache.remove("Blocks");
			Memcache.remove("Blocks." + newBlock.getLocation());
		}

		return newBlock;
	}

	public static Block updateBlock(Key key, BlockDTO blockDTO) {
		String oldLocation = BlockData.getBlock(key).getLocation();

		Block block = new DatastoreTransaction<Block>() {

			@Override
			protected boolean updateEntity(Block datastoreEntity, Object... argvs) {
				datastoreEntity.update((BlockDTO) argvs[0]);
				datastoreEntity.setUpdated(new Date());
				datastoreEntity.setUpdater(UserData.getUser().getKey());
				return true;
			}

		}.makePersistent(Block.class, key, blockDTO);

		if(block != null) {
			String newLocation = block.getLocation();
			if(! oldLocation.equals(newLocation)) {
//				Memcache.remove("Blocks");
				Memcache.remove("Blocks." + oldLocation);
				Memcache.remove("Blocks." + newLocation);
			}
		}

		return block;
	}

	public static Block updateBlockOrder(Key key, long weight, String location) {
		String oldLocation = BlockData.getBlock(key).getLocation();

		Block block = new DatastoreTransaction<Block>() {

			@Override
			protected boolean updateEntity(Block datastoreEntity, Object... argvs) {
				datastoreEntity.setWeight((Long) argvs[0]);
				datastoreEntity.setLocation((String) argvs[1]);
				return true;
			}

		}.makePersistent(Block.class, key, weight, location);

		if(block != null) {
			Memcache.remove("Blocks");
			Memcache.remove("Blocks." + oldLocation);

			String newLocation = block.getLocation();
			if(! oldLocation.equals(newLocation))
				Memcache.remove("Blocks." + newLocation);
		}

		return block;
	}

	public static boolean deleteBlock(Block block) { // TODO : use transaction
		return Datastore.makeTrasient(block);
	}

}
