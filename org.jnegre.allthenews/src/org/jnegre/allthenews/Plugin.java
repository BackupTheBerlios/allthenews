package org.jnegre.allthenews;

import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.core.runtime.IPluginDescriptor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.jnegre.allthenews.pref.ListEncoder;
import org.jnegre.allthenews.view.MainView;

public class Plugin extends AbstractUIPlugin {

    public static final String VERSION_PREFERENCE = "org.jnegre.allthenews.version";
	public static final String REFRESH_INTERVAL_PREFERENCE = "org.jnegre.allthenews.refreshinterval";
	public static final String BACKENDS_PREFERENCE = "org.jnegre.allthenews.backends";
	public static final String BROWSER_PREFERENCE = "org.jnegre.allthenews.browser";
    public static final String BROWSER_TYPE_PREFERENCE = "org.jnegre.allthenews.browser.type";
    public static final String VIEW_TYPE_PREFERENCE = "org.jnegre.allthenews.viewtype";
    public static final String BANNED_ITEMS_PREFERENCE = "org.jnegre.allthenews.banneditems";

	//Default values
    public static final int CURRENT_VERSION = 1;
	public static final int DEFAULT_REFRESH_INTERVAL = 60;
	public static final String DEFAULT_BACKENDS = ListEncoder.encode(new String[] {
        "LinuxFR \u00B6 http://linuxfr.org/backend.rss",
        "Slashdot \u00B6 http://slashdots.org/slashdot.rdf"});
	public static final String DEFAULT_BROWSER = "C:\\Program Files\\Internet Explorer\\IEXPLORE.EXE";
    public static final String DEFAULT_BROWSER_TYPE = "2";
    public static final String DEFAULT_VIEW_TYPE = "1";
    public static final String DEFAULT_BANNED_ITEMS = "";

	//Icons
	public static final String ICON_ALERT = "alert.gif";
	public static final String ICON_OK = "ok.gif";
	public static final String ICON_REFRESH = "refresh.gif";
    public static final String ICON_UNREAD = "unread.gif";

    //User-Agent
    public static String userAgent;

    protected UpdateThread updateThread;

	protected ArrayList views = new ArrayList();
    protected Timer timer;

    protected ArrayList channelList;
    protected Object channelLock = new Object();
    
    protected ArrayList banList = new ArrayList();

    /**
     * Constructor for Plugin
     */
    public Plugin(IPluginDescriptor descriptor) {
        super(descriptor);

        StringBuffer buffer = new StringBuffer();
        buffer.append("AllTheNews/")
              .append(descriptor.getVersionIdentifier())
              .append(" (")
              .append(System.getProperty("os.name"))
              .append("; ")
              .append(System.getProperty("os.arch"))
              .append("; http://www.jnegre.org/)");
        userAgent =  buffer.toString();
        
        //convert backends using the old system to the new one if needed
        if(this.getPreferenceStore().getInt(VERSION_PREFERENCE) == 0
            && !DEFAULT_BACKENDS.equals(this.getPreferenceStore().getString(BACKENDS_PREFERENCE))) {
            String back = this.getPreferenceStore().getString(BACKENDS_PREFERENCE);
            if(!"".equals(back)) {
                StringTokenizer tokenizer = new StringTokenizer(back," ");
                int countTokens = tokenizer.countTokens();
                String[] result = new String[countTokens];
                for(int i=0;i<countTokens;i++) {
                    result[i] = URLDecoder.decode(tokenizer.nextToken());
                }
                this.getPreferenceStore().setValue(BACKENDS_PREFERENCE,ListEncoder.encode(result));
            }
        }
        this.getPreferenceStore().setValue(VERSION_PREFERENCE,CURRENT_VERSION);
        
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

    public ImageDescriptor getImageDescriptor(String relativePath) {
        String iconPath = "icons/";
        try {
            URL url = new URL(getDescriptor().getInstallURL(), iconPath + relativePath);
            return ImageDescriptor.createFromURL(url);
        } catch (java.net.MalformedURLException e) {
            return ImageDescriptor.getMissingImageDescriptor();
        }
    }


    /*
     * @see AbstractUIPlugin#initializeDefaultPreferences(IPreferenceStore)
     */
    protected void initializeDefaultPreferences(IPreferenceStore store) {
        store.setDefault(REFRESH_INTERVAL_PREFERENCE,DEFAULT_REFRESH_INTERVAL);
        store.setDefault(BACKENDS_PREFERENCE,DEFAULT_BACKENDS);
        store.setDefault(BROWSER_PREFERENCE,DEFAULT_BROWSER);
        store.setDefault(BROWSER_TYPE_PREFERENCE,DEFAULT_BROWSER_TYPE);
        store.setDefault(VIEW_TYPE_PREFERENCE,DEFAULT_VIEW_TYPE);
        store.setDefault(BANNED_ITEMS_PREFERENCE,DEFAULT_BANNED_ITEMS);
    }

	protected ImageRegistry createImageRegistry() {
		ImageRegistry registry = super.createImageRegistry();
		registry.put(ICON_ALERT,getImageDescriptor(ICON_ALERT));
		registry.put(ICON_OK,getImageDescriptor(ICON_OK));
		registry.put(ICON_REFRESH,getImageDescriptor(ICON_REFRESH));
        registry.put(ICON_UNREAD,getImageDescriptor(ICON_UNREAD));
		return registry;
	}

	public void addView(MainView view) {
		synchronized(views) {
			views.add(view);
		}
	}

	public void removeView(MainView view) {
		synchronized(views) {
			views.remove(view);
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
            channelList = new ArrayList();
    
            String backends = this.getPreferenceStore().getString(Plugin.BACKENDS_PREFERENCE);
            String[] entries = ListEncoder.decode(backends);
            for (int i = 0; i < entries.length; i++) {
                int index = entries[i].indexOf('\u00B6');
                String title = entries[i].substring(0, index - 1);
                String url = entries[i].substring(index + 2);
                Channel channel = new Channel(title, url);
                channelList.add(channel);
            }
        }
        synchronized(views) {
            Iterator iterator = views.iterator();
            while(iterator.hasNext()) {
                MainView view = ((MainView)iterator.next());
                view.fillChannelTable();
            }
        }
    }

    public ArrayList getChannelList() {
        synchronized(channelLock) {
            return new ArrayList(channelList);
        }
    }


    public void refreshChannelContentInViews(Channel channel) {
        synchronized(views) {
            Iterator iterator = views.iterator();
            while(iterator.hasNext()) {
                MainView view = ((MainView)iterator.next());
                view.getDisplay().asyncExec(new RefreshChannelContent(channel, view));
            }
        }
    }

    public void refreshChannelIconInViews(Channel channel) {
        synchronized(views) {
            Iterator iterator = views.iterator();
            while(iterator.hasNext()) {
                MainView view = ((MainView)iterator.next());
                view.getDisplay().asyncExec(new RefreshChannelIcon(channel, view));
            }
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

    
    protected class RefreshChannelContent implements Runnable {
        
        Channel channel;
        MainView view;
        
        RefreshChannelContent(Channel channel, MainView view) {
            this.channel = channel;
            this.view = view;
        }
        
        public void run() {
            view.refreshChannelContent(channel);
        }
    }

    protected class RefreshChannelIcon implements Runnable {
        
        Channel channel;
        MainView view;
        
        RefreshChannelIcon(Channel channel, MainView view) {
            this.channel = channel;
            this.view = view;
        }
        
        public void run() {
            view.refreshChannelIcon(channel);
        }
    }

}

