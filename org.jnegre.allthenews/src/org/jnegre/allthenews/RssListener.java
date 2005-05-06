/*
 * Created on 15 mai 2004
 * Copyright 2004 J�r�me N�gre
 */
package org.jnegre.allthenews;

import java.util.List;

/**
 * @author J�r�me N�gre
 */
public interface RssListener {
	
	public void onChannelListChanged(List channels);
	
	public void onChannelStatusChanged(Channel channel);

	public void onChannelSelected(Channel channel);

	public void onItemStatusChanged(Item tiem);
	
	public void onItemSelected(Item tiem);
}
