/*
 * Created on 16 mai 2004
 * Copyright 2004 Jérôme Nègre
 */
package org.jnegre.allthenews.view;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.TitleEvent;
import org.eclipse.swt.browser.TitleListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.jnegre.allthenews.Channel;
import org.jnegre.allthenews.Item;
import org.jnegre.allthenews.Plugin;
import org.jnegre.allthenews.RssListener;

/**
 * @author Jérôme Nègre
 */
public class BrowserView extends ViewPart implements RssListener, TitleListener {

	Browser browser;
	private boolean uiReady = false;
	
	public BrowserView() {
		super();
		Plugin.getDefault().addRssListener(this);
	}

	public void createPartControl(Composite parent) {
		browser = new Browser(parent, SWT.NONE);
		browser.addTitleListener(this);
		uiReady = true;
	}

	public void setFocus() {
		browser.setFocus();
	}
	/* (non-Javadoc)
	 * @see org.jnegre.allthenews.RssListener#onChannelListChanged(java.util.ArrayList)
	 */
	public void onChannelListChanged(ArrayList channels) {
		// TODO Auto-generated method stub
	}
	/* (non-Javadoc)
	 * @see org.jnegre.allthenews.RssListener#onChannelStatusChanged(org.jnegre.allthenews.Channel)
	 */
	public void onChannelStatusChanged(Channel channel) {
		// TODO Auto-generated method stub
	}
	/* (non-Javadoc)
	 * @see org.jnegre.allthenews.RssListener#onChannelSelected(org.jnegre.allthenews.Channel)
	 */
	public void onChannelSelected(Channel channel) {
		// TODO Auto-generated method stub
	}
	/* (non-Javadoc)
	 * @see org.jnegre.allthenews.RssListener#onItemStatusChanged(org.jnegre.allthenews.Item)
	 */
	public void onItemStatusChanged(Item item) {
		// TODO Auto-generated method stub
	}

	public void onItemSelected(Item item) {
		if(item != null && uiReady) {
			browser.setUrl(item.getUsableLink());
			item.setReadFlag(true);
			Plugin.getDefault().notifyItemStatusChanged(item);
		}
	}

	public void changed(TitleEvent event) {
		this.setTitle(event.title);
	}
}
