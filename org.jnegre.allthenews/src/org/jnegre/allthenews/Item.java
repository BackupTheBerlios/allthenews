package org.jnegre.allthenews;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 * @author jnegre - http://www.jnegre.org/
 *
 * (c)Copyright 2002 J�r�me N�gre
 * 
 */
public class Item {

    protected static DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT,DateFormat.SHORT);
    protected static SimpleDateFormat pubDateParser = new SimpleDateFormat("EEE, d MMM yy hh:mm:ss z", new Locale("en","US"));

    protected Channel channel;

    protected String title;
    protected String link;
    protected String description;
    protected String author;
    protected String guid;
    protected boolean isPermaLink = true;
    protected String date;
    
    protected boolean readFlag = false;

    /**
     * Constructor for Item.
     */
    public Item(Channel channel, Element itemElement) {
        this.channel = channel;
        this.title = readValue("title", itemElement, 0);
        this.link = readValue("link", itemElement, 0);
        this.description = readValue("description", itemElement, 0);
        this.author = readValue("author", itemElement, 0);
        this.guid = readValue("guid", itemElement, 1);
        String pubDate = readValue("pubDate", itemElement, 0);
        String dcDate = readValue("dc:date", itemElement, 0);
            
        try {
            Date theDate;
            if(pubDate != null) {
                theDate = pubDateParser.parse(pubDate);
            } else if(dcDate != null) {
                theDate = decodeDCDate(dcDate);
            } else {
                theDate = new Date();
            }
            this.date = dateFormat.format(theDate);
        } catch(Exception e) {
            if(pubDate != null) {
            	this.date = pubDate;
            } else if(dcDate != null) {
            	this.date = dcDate;
            } else {
            	this.date = e.toString();
            }
            Plugin.logInfo("Unable to parse date",e);
        }
    }

    protected String readValue(String elementName, Element parent, int type) {
        Element element = (Element)parent.getElementsByTagName(elementName).item(0);
        if(element != null) {

            switch(type) {
                case 1:
                    if(element.hasAttribute("isPermaLink") && element.getAttribute("isPermaLink").equals("false")) {
                        this.isPermaLink = false;
                    }
            }
    
            NodeList children = element.getChildNodes();
            StringBuffer buffer = new StringBuffer();
            for(int i=0; i<children.getLength(); i++) {
            	Node node = children.item(i);
            	if(node.getNodeType()==Node.TEXT_NODE || node.getNodeType()==Node.CDATA_SECTION_NODE) {
            		buffer.append(((Text)node).getData());
            	}
            }
            return buffer.toString().trim();
        } else {
            return null;
        }
    }

    public String getUsableTitle() {
        if(title != null) {
            return title;
        } else if (description != null) {
            return description;
        } else {
            return "!! No title in feed !!";
        }
    }

    public String getUsableLink() {
        if(link != null) {
            return link;
        } else if (guid != null && isPermaLink) {
            return guid;
        } else {
            return "about:blank";
        }
    }

    public boolean isBanned() {
        return Plugin.getDefault().isBannedTitle(title);
    }

    public TableItem toTableItem(Table table) {
        TableItem tableItem = new TableItem(table, SWT.NONE);
        fillTableItem(tableItem);
        return tableItem;
    }

    public void fillTableItem(TableItem tableItem) {
        tableItem.setText(new String[] {date,readFlag?"":"*",getUsableTitle()});
        tableItem.setData(this);
    }

    /**
     * Sets the readFlag and notifies the listeners
     * that the status changed.
     * @param readFlag The readFlag to set
     */
    public void setReadFlag(boolean readFlag) {
        if(readFlag != this.readFlag) {
            this.readFlag = readFlag;
            channel.computeUnRead();
        }
    }

    /**
     * @see java.lang.Object#equals(Object)
     */
    public boolean equals(Object obj) {
        return (obj instanceof Item)
                && ((Item)obj).getUID().equals(this.getUID());
    }

    protected static Date decodeDCDate(String string) throws Exception {
        GregorianCalendar calendar = new GregorianCalendar(readInt(string,0,4),0,1,0,0,0);
        calendar.set(Calendar.MILLISECOND,0);
        calendar.set(Calendar.DST_OFFSET,0);
        if(checkChar(string,4,'-')) {
            calendar.set(Calendar.MONTH,readInt(string,5,2)-1);
            if(checkChar(string,7,'-')) {
                calendar.set(Calendar.DATE,readInt(string,8,2));
                if(checkChar(string,10,'T')) {
                    calendar.set(Calendar.HOUR_OF_DAY,readInt(string,11,2));
                    calendar.set(Calendar.MINUTE,readInt(string,14,2));
                    int length = string.length();
                    int position = 16;
                    
                    //les secondes + millisecondes
                    if(checkChar(string,16,':')) {
                        calendar.set(Calendar.SECOND,readInt(string,17,2));
                        position = 19;
                        if(checkChar(string,position,'.')) {
                            position += 1;
                            StringBuffer millisecondBuffer = new StringBuffer("0.");
                            while(position<length && Character.isDigit(string.charAt(position))) {
                                millisecondBuffer.append(string.charAt(position));
                                position += 1;
                            }
                            calendar.set(Calendar.MILLISECOND,(int)(Double.parseDouble(millisecondBuffer.toString())*1000));
                        }

                    }


                    //TZD
                    if(string.charAt(position) == 'Z') {
                        calendar.set(Calendar.ZONE_OFFSET,0);
                        if(length != position +1) {
                            //trop de caract�res
                            throw new Exception("Invalid format of dc:date (extra tokens)");
                        }
                    } else if(string.charAt(position) == '+' || string.charAt(position) == '-') {
                        int sign = 0;
                        sign = string.charAt(position) == '+'?1:-1;
                        int hour = readInt(string,position+1,2);
                        int minute = readInt(string,position+4,2);
                        calendar.set(Calendar.ZONE_OFFSET,sign*(hour*60*60*1000+minute*60*1000));
                        if(length != position +6) {
                            //trop de caract�res
                            throw new Exception("Invalid format of dc:date (extra tokens)");
                        }
                    } else {
                        throw new Exception("Invalid format of dc:date (invalid TZD)");
                    }
                    
                }
            }
        }
        return calendar.getTime();
    }

    private static int readInt(String buffer, int position, int length) {
        int result = Integer.parseInt(buffer.substring(position,position+length));
        return result;
    }
    
    private static boolean checkChar(String buffer, int position, char expectedChar) {
        if(buffer.length() > position && buffer.charAt(position) == expectedChar) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * @return the description of this item
     */
    public String getDescription() {
        return description;
    }
    /**
     * @return the author of this item
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Returns the date.
     * @return String
     */
    public String getDate() {
        return date;
    }

    /**
     * Returns the readFlag.
     * @return boolean
     */
    public boolean isReadFlag() {
        return readFlag;
    }

    /**
     * Returns the channel.
     * @return Channel
     */
    public Channel getChannel() {
        return channel;
    }

    /**
     * Returns a unique ID used to remember which
     * items were read in the ChannelStore
     * @return
     */
    public String getUID() {
    	return getUsableLink() + ") ~ (" + getUsableTitle();
    }
    
}
