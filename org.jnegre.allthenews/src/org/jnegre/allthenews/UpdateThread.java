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
                Channel channel = (Channel)iterator.next();
                channel.setRefreshing(true);
                plugin.notifyChannelStatusChanged(channel, null);
                channel.update();
                channel.setRefreshing(false);
                plugin.notifyChannelStatusChanged(channel, null);
            }
        } finally {
            Plugin.getDefault().updateThread = null;
        }
    }

}