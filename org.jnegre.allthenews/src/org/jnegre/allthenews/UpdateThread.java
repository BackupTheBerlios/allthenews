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
            Iterator iterator = Plugin.getDefault().getChannelList().iterator();
            while(iterator.hasNext()) {
                Channel channel = (Channel)iterator.next();
                channel.setRefreshing(true);
                Plugin.getDefault().refreshChannelContentInViews(channel);
                channel.update();
                channel.setRefreshing(false);
                Plugin.getDefault().refreshChannelContentInViews(channel);
            }
        } finally {
            Plugin.getDefault().updateThread = null;
        }
    }

}