package org.jnegre.allthenews;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import org.apache.xerces.parsers.DOMParser;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * @author jnegre - http://www.jnegre.org/
 *
 * (c)Copyright 2002 Jérôme Nègre
 * 
 */
public class Channel {

    protected String url;
    protected String title;

    protected boolean refreshing = false;
    protected String errorMessage = null;
    protected boolean unread = false;
    
    protected ArrayList items = new ArrayList();

    /**
     * Constructor for Channel.
     */
    public Channel(String title, String url) {
        this.title = title;
        this.url = url;
    }


    public void update() {
        update(Plugin.getDefault().getPluginPreferences());
    }


    public void update(Preferences prefs) {
            ArrayList newItems = new ArrayList();
            String newErrorMessage = null;
        try {
            
            URLConnection conn = new URL(url).openConnection();
            conn.setRequestProperty("User-Agent", Plugin.userAgent);
            InputStream stream = conn.getInputStream();
            DOMParser parser = new DOMParser();
            parser.setFeature("http://apache.org/xml/features/allow-java-encodings",true);
            parser.parse(new InputSource(stream));
            stream.close();
            Document doc = parser.getDocument();
            NodeList itemNodes = doc.getElementsByTagName("item");
            for (int i = 0; i < itemNodes.getLength(); i++) {
                Item aNewItem = new Item(this, (Element) itemNodes.item(i));
                if(aNewItem.isBanned()) continue;
                int indexOld = items.indexOf(aNewItem);
                if(indexOld != -1) {
                    newItems.add(items.get(indexOld));
                } else {
                    newItems.add(aNewItem);
                }
                
            }
        } catch(Exception e) {
            newErrorMessage = e.toString();
            Plugin.logInfo("Error in channel update",e);
        }
        
        synchronized(this) {
            this.errorMessage = newErrorMessage;
            if(newErrorMessage == null) {
                this.items = newItems;
                computeUnRead();
            }
        }
    }

    /**
     * Returns the url.
     * @return String
     */
    public String getUrl() {
        return url;
    }

    /**
     * Returns the errorMessage.
     * @return String
     */
    public synchronized String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Returns the items.
     * @return ArrayList
     */
    public synchronized ArrayList getItems() {
        return new ArrayList(items);
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "Channel at "+url;
    }

    /**
     * Returns the title.
     * @return String
     */
    public String getTitle() {
        return title;
    }

    /** @deprecated */
    public TableItem toTableItem(Table table) {
        TableItem tableItem = new TableItem(table, 0);
        tableItem.setData(this);
        fillTableItem(tableItem);
        return tableItem;
    }

    /** @deprecated */
    public void fillTableItem(TableItem tableItem) {
        tableItem.setImage(0,getIcon());
        tableItem.setText(1,this.getTitle());
    }

    /**
     * Returns the refreshing.
     * @return boolean
     */
    public boolean isRefreshing() {
        return refreshing;
    }

    /**
     * Sets the refreshing.
     * @param refreshing The refreshing to set
     */
    public void setRefreshing(boolean refreshing) {
        this.refreshing = refreshing;
    }

    /** @deprecated */
    public Image getIcon() {
        String iconId;
        if(refreshing) {
            iconId = Plugin.ICON_REFRESH;
        } else if(errorMessage == null && !unread){
            iconId = Plugin.ICON_OK;
        } else if(errorMessage == null && unread){
            iconId = Plugin.ICON_UNREAD;
        } else {
            iconId = Plugin.ICON_ALERT;
        }
        return Plugin.getDefault().getImageRegistry().getDescriptor(iconId).createImage();
    }

    /**
     * Returns the unread.
     * @return boolean
     */
    public boolean isUnread() {
        return unread;
    }

    public synchronized void computeUnRead() {
        this.unread = false;
        for (int i = 0; i < items.size(); i++) {
            this.unread = this.unread || !((Item)items.get(i)).isReadFlag();
        }
    }

}
