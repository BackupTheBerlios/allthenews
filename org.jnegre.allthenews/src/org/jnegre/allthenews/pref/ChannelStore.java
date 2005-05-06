/*
 * Created on 9 juin 2004
 * Copyright 2004 Jérôme Nègre
 */
package org.jnegre.allthenews.pref;

import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Properties;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.jnegre.allthenews.Channel;
import org.jnegre.allthenews.Folder;
import org.jnegre.allthenews.Plugin;

/**
 * @author Jérôme Nègre
 */
public class ChannelStore {
	
	private final static String DEFAULT_CHANNELS_FILE = "default_feeds.properties";
	
	private final static String BACKENDS_SECTION = "backends";
	
	private final static String CHANNELS_ORDER_KEY = "order";
	private final static String TITLE_KEY = "title";
	private final static String URL_KEY = "url";
	private final static String READ_KEY = "read";
	
	private final static String ROOT_FOLDER_KEY = "rootFolder";
	
	private static Plugin plugin = null;
	
	public static void init(Plugin plugin) {
		ChannelStore.plugin = plugin;
	}
	
	public static synchronized Folder getRootFolder() {
		IDialogSettings section = plugin.getDialogSettings().getSection(BACKENDS_SECTION);
		
		if(section  == null) {
			Folder root = new Folder();
			root.setContent(getDefaultChannels());
			return root;
		}
		
		String b64 = section.get(ROOT_FOLDER_KEY);
		if(b64 != null) {
			Folder root = (Folder)Base64.decodeToObject(b64);
			return root;
		} else {
			//compatibility with old way to save channels
			String[] uids =  section.getArray(CHANNELS_ORDER_KEY);
			ArrayList result = new ArrayList();
			for(int i=0; i<uids.length; i++) {
				String uid = uids[i];
				IDialogSettings channelSection = section.getSection(uid);
				String title = channelSection.get(TITLE_KEY);
				String url = channelSection.get(URL_KEY);
				
				String[] readUids = channelSection.getArray(READ_KEY);
				HashSet set = new HashSet();
				if(readUids != null) {
					for(int k=0; k<readUids.length; k++) {
						set.add(readUids[k]);
					}
				}
				
				result.add(new Channel(title, url, set));
			}
			Folder root = new Folder();
			root.setContent(result);
			return root;
		}
	}

	public static synchronized void saveReadStatus(Folder rootFolder) {
		IDialogSettings channelsSection = plugin.getDialogSettings().getSection(BACKENDS_SECTION);
		if(channelsSection == null) {
			channelsSection = plugin.getDialogSettings().addNewSection(BACKENDS_SECTION);
		}

		String b64 = Base64.encodeObject(rootFolder, Base64.GZIP | Base64.DONT_BREAK_LINES);
		channelsSection.put(ROOT_FOLDER_KEY, b64);
	}
	
	public static synchronized ArrayList getDefaultChannels() {
		ArrayList result = new ArrayList();
		try {
			Properties prop = new Properties();
			URL propLocation = new URL(Plugin.getDefault().getDescriptor().getInstallURL(), DEFAULT_CHANNELS_FILE);
			prop.load(propLocation.openStream());
			Enumeration e = prop.propertyNames();
			while(e.hasMoreElements()) {
				String url = (String)e.nextElement();
				String title = prop.getProperty(url);
				result.add(new Channel(title, url));
			}
		} catch(Exception e) {
			Plugin.logError("Error while getting default feed list", e);
		}
		return result;
		
	}
	
}
