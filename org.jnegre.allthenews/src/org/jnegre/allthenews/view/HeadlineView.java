/*
 * Created on 15 mai 2004
 * Copyright 2004 Jérôme Nègre
 */
package org.jnegre.allthenews.view;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.part.ViewPart;
import org.jnegre.allthenews.Channel;
import org.jnegre.allthenews.Item;
import org.jnegre.allthenews.Plugin;
import org.jnegre.allthenews.RssListener;

/**
 * @author Jérôme Nègre
 */
public class HeadlineView extends ViewPart implements RssListener {
	
	Table table;

	public HeadlineView() {
		super();
	}

	public void dispose() {
		Plugin.getDefault().removeRssListener(this);
		super.dispose();
	}

	private TableColumn createColumn(int style, int width, String text) {
		TableColumn col = new TableColumn(table, style);
		col.setWidth(width);
		col.setText(text);
		return col;
	}
	
	public void createPartControl(Composite parent) {
        table = new Table(parent, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
        createColumn(SWT.LEFT, 120, "Publication Date");
        createColumn(SWT.CENTER, 20, "");
        createColumn(SWT.LEFT, 600, "Title");
        table.setHeaderVisible(true);
        table.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
                Item item = (Item) e.item.getData();
                Plugin.getDefault().notifyItemSelected(item,HeadlineView.this);
			}
        });
        Plugin.getDefault().addRssListener(this);
	}
	
	public void setFocus() {
		table.setFocus();
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
		fillTable(channel);
	}
	/* (non-Javadoc)
	 * @see org.jnegre.allthenews.RssListener#onItemSelected(org.jnegre.allthenews.Item)
	 */
	public void onItemSelected(Item item) {
		fillTable(item.getChannel());
		int index = item.getChannel().getItems().indexOf(item);
		table.setSelection(index);
	}
	/* (non-Javadoc)
	 * @see org.jnegre.allthenews.RssListener#onItemStatusChanged(org.jnegre.allthenews.Item)
	 */
	public void onItemStatusChanged(Item item) {
		fillTable(item.getChannel());
		int index = item.getChannel().getItems().indexOf(item);
		table.setSelection(index);
	}
	
	private void fillTable(Channel channel) {
		Iterator items = channel.getItems().iterator();
		table.removeAll();
		while(items.hasNext()) {
			Item item = (Item)items.next();
			TableItem tableItem = new TableItem(table,SWT.NONE);
			tableItem.setText(0,item.getDate());
			String image = item.isReadFlag()? Plugin.ICON_LED_DARK_GREEN : Plugin.ICON_LED_LIGHT_GREEN;
			tableItem.setImage(1,Plugin.getDefault().getImageRegistry().get(image));
			tableItem.setText(2,item.getUsableTitle());
			tableItem.setData(item);
		}
	}
}
