/*
 * Created on 9 juin 2004
 * Copyright 2004 Jérôme Nègre
 */
package org.jnegre.allthenews.pref;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.jnegre.allthenews.Channel;
import org.jnegre.allthenews.Item;
import org.jnegre.allthenews.Plugin;

/**
 * @author Jérôme Nègre
 */
public class ChannelStore {
	
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
			addChannel(section,channel.getTitle(),channel.getUrl());
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
		//TODO add some default channels from config file
		addChannel(section,"NEW LinuxFR","http://linuxfr.org/backend.rss");
		addChannel(section,"NEW Java.net","http://today.java.net/pub/q/java_today_rss?x-ver=1.0");
		addChannel(section,"NEW BBC Euro 2004","http://news.bbc.co.uk/rss/sportonline_uk_edition/football/euro_2004/rss091.xml");
		
		return section;
	}
	
	private static void addChannel(IDialogSettings backendSection, String title, String url) {
		//FIXME check that section does not already exist before
		//creating it, and if it exists, add it to the order key
		//only if it's not already in it.
		IDialogSettings section = backendSection.getSection(title);
		//create section
		String uid = Channel.computeUID(url);
		section = backendSection.addNewSection(uid);
		section.put(TITLE_KEY, title);
		section.put(URL_KEY, url);
		section.put(TYPE_KEY, TYPE_CHANNEL);
		//set order key
		String[] oldOrder = backendSection.getArray(CHANNELS_ORDER_KEY);
		String[] newOrder = new String[oldOrder.length+1];
		System.arraycopy(oldOrder, 0, newOrder, 0, oldOrder.length);
		newOrder[oldOrder.length] = uid;
		backendSection.put(CHANNELS_ORDER_KEY,newOrder);
	}
	
}
