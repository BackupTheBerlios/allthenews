package org.jnegre.allthenews;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPluginDescriptor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.jnegre.allthenews.pref.ChannelStore;
import org.jnegre.allthenews.pref.ListEncoder;

public class Plugin extends AbstractUIPlugin {

	public static final String BACKENDS_SECTION = "backends";
	
	public static final String REFRESH_INTERVAL_PREFERENCE = "org.jnegre.allthenews.refreshinterval";
	/** @deprecated */
	public static final String BACKENDS_PREFERENCE = "org.jnegre.allthenews.backends";
	public static final String BROWSER_PREFERENCE = "org.jnegre.allthenews.browser";
    public static final String BANNED_ITEMS_PREFERENCE = "org.jnegre.allthenews.banneditems";
    public static final String FORCE_CACHE_PREFERENCE = "org.jnegre.allthenews.forcecache";

	//Default values
	public static final int DEFAULT_REFRESH_INTERVAL = 60;
	public static final String DEFAULT_BROWSER = "C:\\Program Files\\Internet Explorer\\IEXPLORE.EXE";
    public static final String DEFAULT_BANNED_ITEMS = "";
	public static final boolean DEFAULT_FORCE_CACHE = false;

    //User-Agent
    public static String userAgent;

    protected UpdateThread updateThread;

	protected ArrayList views = new ArrayList();
    protected Timer timer;

    protected ArrayList channelList;
    protected Object channelLock = new Object();
    
    protected ArrayList banList = new ArrayList();
    
    /**
     * List of RssListeners to notify
     */
    private ArrayList rssListeners = new ArrayList();

    /**
     * Constructor for Plugin
     */
    public Plugin(IPluginDescriptor descriptor) {
        super(descriptor);

        //set the user-agent ID
        StringBuffer buffer = new StringBuffer();
        buffer.append("AllTheNews/")
              .append(descriptor.getVersionIdentifier())
              .append(" (")
              .append(System.getProperty("os.name"))
              .append("; ")
              .append(System.getProperty("os.arch"))
              .append("; http://www.jnegre.org/)");
        userAgent =  buffer.toString();
        
        //init the channel store
        ChannelStore.init(this);
        
        updateBanList();
        updateChannelList();
        singleton = this;
        setTimer();
    }

	protected static Plugin singleton;
	
	public static Plugin getDefault() {
		return singleton;
	}

    public static void logError(String message, Throwable t) {
        getDefault().getLog().log(new Status(IStatus.ERROR,getDefault().getDescriptor().getUniqueIdentifier(),IStatus.OK,message,t));
    }

    public static void logInfo(String message, Throwable t) {
        getDefault().getLog().log(new Status(IStatus.INFO,getDefault().getDescriptor().getUniqueIdentifier(),IStatus.OK,message,t));
    }

    /*
     * @see AbstractUIPlugin#initializeDefaultPreferences(IPreferenceStore)
     */
    protected void initializeDefaultPreferences(IPreferenceStore store) {
        store.setDefault(REFRESH_INTERVAL_PREFERENCE,DEFAULT_REFRESH_INTERVAL);
        store.setDefault(BROWSER_PREFERENCE,DEFAULT_BROWSER);
        store.setDefault(BANNED_ITEMS_PREFERENCE,DEFAULT_BANNED_ITEMS);
        store.setDefault(FORCE_CACHE_PREFERENCE,DEFAULT_FORCE_CACHE);
    }

	protected ImageRegistry createImageRegistry() {
		ImageRegistry registry = super.createImageRegistry();
		IconManager.populateImageRegistry(registry);
		return registry;
	}

	public void addRssListener(RssListener listener) {
		synchronized(rssListeners) {
			rssListeners.add(listener);
		}
	}

	public void removeRssListener(RssListener listener) {
		synchronized(rssListeners) {
			rssListeners.remove(listener);
		}
	}

	public void notifyChannelListChanged(RssListener source) {
		Iterator iterator = rssListeners.iterator();
		ArrayList channels = getChannelList();
		while(iterator.hasNext()) {
			RssListener listener = (RssListener)iterator.next();
			if(listener != source) {
				listener.onChannelListChanged(channels);
			}
		}
	}

	public void notifyChannelStatusChanged(Channel channel, RssListener source) {
		Iterator iterator = rssListeners.iterator();
		while(iterator.hasNext()) {
			RssListener listener = (RssListener)iterator.next();
			if(listener != source) {
				listener.onChannelStatusChanged(channel);
			}
		}
	}

	public void notifyChannelSelected(Channel channel, RssListener source) {
		Iterator iterator = rssListeners.iterator();
		while(iterator.hasNext()) {
			RssListener listener = (RssListener)iterator.next();
			if(listener != source) {
				listener.onChannelSelected(channel);
			}
		}
	}

	public void notifyItemSelected(Item item, RssListener source) {
		Iterator iterator = rssListeners.iterator();
		while(iterator.hasNext()) {
			RssListener listener = (RssListener)iterator.next();
			if(listener != source) {
				listener.onItemSelected(item);
			}
		}
	}

	public void notifyItemStatusChanged(Item item, RssListener source) {
		Iterator iterator = rssListeners.iterator();
		while(iterator.hasNext()) {
			RssListener listener = (RssListener)iterator.next();
			if(listener != source) {
				listener.onItemStatusChanged(item);
			}
		}
	}

    public void setTimer() {
        if(timer != null) {
            timer.cancel();
        }
        long period = getPreferenceStore().getInt(Plugin.REFRESH_INTERVAL_PREFERENCE)*60000l;
        if(period != 0) {
            timer = new Timer(true);
            timer.schedule(new UpdateTimer(),0,period);
        }
    }

    public void updateBanList() {
        synchronized(banList) {
            banList.clear();
    
            String banned = this.getPreferenceStore().getString(Plugin.BANNED_ITEMS_PREFERENCE);
            String[] bannedTitles = ListEncoder.decode(banned);
            for (int i = 0; i < bannedTitles.length; i++) {
                banList.add(bannedTitles[i]);
            }
        }
    }
    
    public boolean isBannedTitle(String title) {
        synchronized(banList) {
            return banList.contains(title);
        }
    }
    
    public void updateChannelList() {
        synchronized(channelLock) {
            channelList = ChannelStore.getChannels();
        }
        notifyChannelListChanged(null);
    }

    public ArrayList getChannelList() {
        synchronized(channelLock) {
            return new ArrayList(channelList);
        }
    }

    public void update() {
        if (updateThread == null) {
            updateThread = new UpdateThread();
            updateThread.start();
        }
    }

    protected class UpdateTimer extends TimerTask {
        public void run() {
            update();
        }
    }

	public void shutdown() throws CoreException {
		ChannelStore.saveReadStatus(getChannelList());
		super.shutdown();
	}
}

