/*
 * Created on 15 mai 2004
 * Copyright 2004 Jérôme Nègre
 */
package org.jnegre.allthenews.view;

import java.util.ArrayList;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.jnegre.allthenews.Channel;
import org.jnegre.allthenews.Item;
import org.jnegre.allthenews.Plugin;
import org.jnegre.allthenews.RssListener;

/**
 * @author Jérôme Nègre
 */
public class ExplorerView extends ViewPart implements RssListener {
	
	private TreeViewer treeViewer;
	private NewsTreeViewerProvider provider;
	private boolean uiReady = false;
	
	public ExplorerView() {
		super();
		Plugin.getDefault().addRssListener(this);
	}

	public void dispose() {
		uiReady = false;
		Plugin.getDefault().removeRssListener(this);
		super.dispose();
	}

	public void createPartControl(Composite parent) {
		treeViewer = new TreeViewer(parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		provider = new NewsTreeViewerProvider();
		treeViewer.setContentProvider(provider);
		treeViewer.setLabelProvider(provider);
		treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				Object selected = ((StructuredSelection)event.getSelection()).getFirstElement();
				if(selected != null) {
					if(selected instanceof Channel) {
						Plugin.getDefault().notifyChannelSelected((Channel)selected);
					} else if(selected instanceof Item) {
						Plugin.getDefault().notifyItemSelected((Item)selected);
					}
				}
			}
		});
		uiReady = true;
		treeViewer.setInput(Plugin.getDefault());
	}

	public void setFocus() {
		treeViewer.getControl().setFocus();
	}

	public void onChannelListChanged(ArrayList channels) {
		if(uiReady) {
			treeViewer.setInput(Plugin.getDefault());
		}
	}
	
	public void onChannelStatusChanged(final Channel channel) {
		if(uiReady) {
			treeViewer.getControl().getDisplay().asyncExec(new Runnable() {
				public void run() {
					treeViewer.refresh(channel);
				}
			});
		}
	}

	public void onChannelSelected(Channel channel) {
		System.out.println("Explorer.onChannelSelected -> "+channel);
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
