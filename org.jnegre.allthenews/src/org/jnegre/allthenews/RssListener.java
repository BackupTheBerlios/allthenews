/*
 * Created on 15 mai 2004
 * Copyright 2004 Jérôme Nègre
 */
package org.jnegre.allthenews;

import java.util.List;

/**
 * @author Jérôme Nègre
 */
public interface RssListener {
	
	public void onChannelListChanged(List channels);
	
	public void onChannelStatusChanged(Channel channel);

	public void onChannelSelected(Channel channel);

	public void onItemStatusChanged(Item tiem);
	
	public void onItemSelected(Item tiem);
}
