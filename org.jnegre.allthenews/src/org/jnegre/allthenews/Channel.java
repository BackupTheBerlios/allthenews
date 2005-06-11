package org.jnegre.allthenews;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PushbackInputStream;
import java.io.Serializable;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashSet;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.core.runtime.Preferences;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author jnegre - http://www.jnegre.org/
 *
 * (c)Copyright 2002 Jérôme Nègre
 * 
 */
public class Channel implements Serializable {

	private static final long serialVersionUID = 1L; //must not change
	
    private String url;
    private String title;

    private boolean refreshing = false;
    private String errorMessage = null;
    private boolean unread = false;
    
    private ArrayList items = new ArrayList();
    private HashSet readUids = null;
    
    private ArrayList transformations = new ArrayList();

    /**
     * Constructor for Channel.
     */
    public Channel(String title, String url) {
    	this(title, url, null);
    }

    public Channel(String title, String url, HashSet readUids) {
        this.title = title;
        this.url = url;
        this.readUids = readUids;
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
            if(prefs.getBoolean(Plugin.FORCE_CACHE_PREFERENCE)) {
            	conn.setRequestProperty("Pragma", "no-cache");
        		conn.setRequestProperty("Cache-Control", "no-cache");
            }
            InputStream stream = conn.getInputStream();
            
            /* workaround a bug of crimson (it seems to ignore the encoding
             * if it does not get it the first time it reads bytes from
             * the stream. We use a PushbackInputStream to be sure that the
             * encoding declaration is in the buffer)
             */
            PushbackInputStream pbStream = new PushbackInputStream(stream,64);
            byte[] buffer = new byte[64];
            int pos = 0;
            while(pos != 64) {
            	pos += pbStream.read(buffer, pos, 64-pos);
            }
            pbStream.unread(buffer);
            //end workaround
            
            DocumentBuilder parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = parser.parse(pbStream);
            pbStream.close();
            NodeList itemNodes = doc.getElementsByTagName("item");
            for (int i = 0; i < itemNodes.getLength(); i++) {
                Item aNewItem = new Item(this, (Element) itemNodes.item(i));
                if(aNewItem.isBanned()) continue;
                if(readUids!=null && readUids.remove(aNewItem.getUID())) {
                	aNewItem.setReadFlag(true);
                }
                int indexOld = items.indexOf(aNewItem);
                if(indexOld != -1) {
                    newItems.add(items.get(indexOld));
                } else {
                    newItems.add(aNewItem);
                }
                
            }
            this.readUids = null;
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
	 * @param url The url to set.
	 */
	public void setUrl(String url) {
		this.url = url;
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

    /**
	 * @param title The title to set.
	 */
	public void setTitle(String title) {
		this.title = title;
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
    
    public String getUID() {
    	return "CHA" + url;
    }

	/**
	 * @return Returns the transformations.
	 */
	public ArrayList getTransformations() {
		return transformations;
	}
	/**
	 * @param transformations The transformations to set.
	 */
	public void setTransformations(ArrayList transformations) {
		this.transformations = transformations;
	}
   
    private void writeObject(ObjectOutputStream out) throws IOException {
    	out.writeInt(1);//serialization version number
    	out.writeObject(items);
    	out.writeObject(title);
    	out.writeObject(url);
    	out.writeBoolean(unread);
    	out.writeObject(transformations);
    }
    
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException{
    	int serializationVersion = in.readInt();
    	switch(serializationVersion) {
    		case 1:
    			items = (ArrayList)in.readObject();
    			title = (String)in.readObject();
    			url = (String)in.readObject();
    			unread = in.readBoolean();
    			transformations = (ArrayList)in.readObject();
    			break;
    		default:
    			throw new IOException("Unsupported serialization version: "+serializationVersion);
    	}
    }
}
