/*
 * Created on 15 mai 2004
 * Copyright 2004 Jérôme Nègre
 */
package org.jnegre.allthenews.view;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.part.ViewPart;
import org.jnegre.allthenews.Channel;
import org.jnegre.allthenews.Item;
import org.jnegre.allthenews.Plugin;
import org.jnegre.allthenews.RssListener;

/**
 * @author Jérôme Nègre
 */
public class HeadlineView extends ViewPart implements RssListener {
	
	Table itemTable;

	public HeadlineView() {
		super();
		Plugin.getDefault().addRssListener(this);
	}

	public void dispose() {
		Plugin.getDefault().removeRssListener(this);
		super.dispose();
	}

	
	public void createPartControl(Composite parent) {
        itemTable = new Table(parent, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
        new TableColumn(itemTable, SWT.LEFT);
        new TableColumn(itemTable, SWT.CENTER);
        new TableColumn(itemTable, SWT.LEFT);
        itemTable.setHeaderVisible(true);
	}
	
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPart#setFocus()
	 */
	public void setFocus() {
		// TODO Auto-generated method stub
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
	 * @see org.jnegre.allthenews.RssListener#onItemSelected(org.jnegre.allthenews.Item)
	 */
	public void onItemSelected(Item tiem) {
		// TODO Auto-generated method stub
	}
	/* (non-Javadoc)
	 * @see org.jnegre.allthenews.RssListener#onItemStatusChanged(org.jnegre.allthenews.Item)
	 */
	public void onItemStatusChanged(Item tiem) {
		// TODO Auto-generated method stub
	}
}
