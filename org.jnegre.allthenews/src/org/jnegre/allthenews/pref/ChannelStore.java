/*
 * Created on 9 juin 2004
 * Copyright 2004 Jérôme Nègre
 */
package org.jnegre.allthenews.pref;

import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.jnegre.allthenews.Channel;
import org.jnegre.allthenews.Item;
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
	private final static String TYPE_KEY = "type";
	private final static String READ_KEY = "read";
	
	private final static int TYPE_CHANNEL = 1;
	
	private static Plugin plugin = null;
	
	public static void init(Plugin plugin) {
		ChannelStore.plugin = plugin;
	}
	
	public static synchronized ArrayList getChannels() {
		IDialogSettings section = getChannelsSection();
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
		return result;
	}

	public static synchronized void setChannels(ArrayList channels) {
		IDialogSettings section = getChannelsSection();
		section.put(CHANNELS_ORDER_KEY,new String[0]);
		int newSize = channels.size();
		for(int i=0; i<newSize; i++) {
			Channel channel = (Channel)channels.get(i); 
			addChannel(section,channel);
		}
	}
	
	public static synchronized void saveReadStatus(ArrayList channels) {
		IDialogSettings channelsSection = getChannelsSection();
		Iterator channelIter = channels.iterator();
		while(channelIter.hasNext()) {
			Channel channel = (Channel)channelIter.next();
			IDialogSettings section = channelsSection.getSection(channel.getUID());
			Iterator itemIter = channel.getItems().iterator();
			ArrayList readItems = new ArrayList();
			while(itemIter.hasNext()) {
				Item item = (Item)itemIter.next();
				if(item.isReadFlag()) {
					readItems.add(item.getUID());
				}
			}
			section.put(READ_KEY,(String[])readItems.toArray(new String[0]));
		}
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
	
	/**
	 * Returns a non null Channels Section,
	 * creating it if needed.
	 * @return
	 */
	private static IDialogSettings getChannelsSection() {
        IDialogSettings section = plugin.getDialogSettings().getSection(BACKENDS_SECTION);
        if(section == null) {
        	section = createDefaultChannelsSection();
        }
        return section;
	}
	
	private static IDialogSettings createDefaultChannelsSection() {
		IDialogSettings section = plugin.getDialogSettings().addNewSection(BACKENDS_SECTION);
		section.put(CHANNELS_ORDER_KEY,new String[0]);
		//add some default channels from config file
		Iterator iterator = getDefaultChannels().iterator();
		while(iterator.hasNext()) {
			addChannel(section, (Channel)iterator.next());
		}
		return section;
	}
	
	private static void addChannel(IDialogSettings backendSection, Channel channel) {
		String title = channel.getTitle();
		String url = channel.getUrl();
		String uid = channel.getUID();
		//check that section does not already exist before
		//creating it, and if it exists, add it to the order key
		//only if it's not already in it.
		IDialogSettings section = backendSection.getSection(uid);
		boolean addInOrder = true;
		if(section == null) {
			//create section
			section = backendSection.addNewSection(uid);
		} else {
			//check if the section is already in the order key
			String[] orders = backendSection.getArray(CHANNELS_ORDER_KEY);
			for(int i=0; i<orders.length; i++) {
				if(orders[i].equals(uid)) {
					addInOrder = false;
					break;
				}
			}
		}
		//set data
		section.put(TITLE_KEY, title);
		section.put(URL_KEY, url);
		section.put(TYPE_KEY, TYPE_CHANNEL);
		//set order key if needed
		if(addInOrder) {
			String[] oldOrder = backendSection.getArray(CHANNELS_ORDER_KEY);
			String[] newOrder = new String[oldOrder.length+1];
			System.arraycopy(oldOrder, 0, newOrder, 0, oldOrder.length);
			newOrder[oldOrder.length] = uid;
			backendSection.put(CHANNELS_ORDER_KEY,newOrder);
		}
	}
	
}
