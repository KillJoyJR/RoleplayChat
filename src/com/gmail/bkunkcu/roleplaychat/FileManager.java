package com.gmail.bkunkcu.roleplaychat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

public class FileManager {
	
	private RoleplayChat plugin;
	public List<String> spy = new ArrayList<String>();
	public Multimap<String,String> modes = ArrayListMultimap.create();
	public HashMap<String, String> mirrors = new HashMap<String, String>();
	
	public FileManager(RoleplayChat plugin) {
		this.plugin = plugin;
	}
	
	public void getFiles() {
		modes.clear();
		mirrors.clear();
		
		File config = new File(plugin.getDataFolder(), "config.yml");
		
		if(!config.exists()) {
			plugin.getDataFolder().mkdir();
			copy(plugin.getResource("config.yml"), config);
		}
		
		else {
			plugin.reloadConfig();
		}
		
		getMirrors();
		
		for(World world : Bukkit.getWorlds()) {
			
			if(!mirrors.containsKey(world.getName())) {
				createWorldFile(world.getName());
				getModes(world.getName());
			}		
		}		
	}
	
	private void getMirrors() {
		for(World world : Bukkit.getWorlds()) {
			
			if(plugin.getConfig().get("settings.mirrors." + world.getName()) != null) {
				
				for(String mirrored : plugin.getConfig().getStringList("settings.mirrors." + world.getName())) {
					mirrors.put(mirrored, world.getName());
				}
			}
		}
	}
	
	private void createWorldFile(String world) {
		File folder = new File(plugin.getDataFolder(), world);
		File file = new File(plugin.getDataFolder(), world + "/chat.yml");
		
		if(!file.exists()) {
			plugin.getDataFolder().mkdir();
			folder.mkdir();
			copy(plugin.getResource("chat.yml"), file);
		}
	}
	
	private void getModes(String world) {
		File file = new File(plugin.getDataFolder(), world + "/chat.yml");
		YamlConfiguration yml = new YamlConfiguration();
		
		try { 
			yml.load(file);
	    } 
		
		catch (Exception e) {
			plugin.getLogger().info("Couldn't load chat.yml file. Disabling plugin!");
			Bukkit.getPluginManager().disablePlugin(plugin);
		}
		
		for(String key : yml.getKeys(false)) {
			modes.put(world, key);
		}
	}
	
	private void copy(InputStream in, File file) {
	    try { OutputStream out = new FileOutputStream(file);
	    	byte[] buf = new byte[1024];
	        int len;
	        while ((len = in.read(buf)) > 0) {
	    	    out.write(buf, 0, len);
	        }
	        out.close();
	        in.close();
	    }
	    catch (Exception e) {
	        e.printStackTrace();
	    }
	}
}