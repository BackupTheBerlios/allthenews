package org.jnegre.allthenews;

import java.util.Iterator;

public class UpdateThread extends Thread {


    /**
     * Constructor for UpdateThread
     */
    public UpdateThread() {
        super();
        this.setDaemon(true);
    }

    /**
     * @see Runnable#run()
     */
    public void run() {
        try {
        	Plugin plugin = Plugin.getDefault();
            Iterator iterator = Plugin.getDefault().getChannelList().iterator();
            while(iterator.hasNext()) {
            	//FIXME remove old notification system
                Channel channel = (Channel)iterator.next();
                channel.setRefreshing(true);
                plugin.refreshChannelContentInViews(channel);
                plugin.notifyChannelStatusChanged(channel);
                channel.update();
                channel.setRefreshing(false);
                plugin.refreshChannelContentInViews(channel);
                plugin.notifyChannelStatusChanged(channel);
            }
        } finally {
            Plugin.getDefault().updateThread = null;
        }
    }

}