/*
 * Created on 15 mai 2004
 * Copyright 2004 J�r�me N�gre
 */
package org.jnegre.allthenews.view;

import java.util.ArrayList;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
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
 * @author J�r�me N�gre
 */
public class ExplorerView extends ViewPart implements RssListener {
	
	private TreeViewer treeViewer;
	private NewsTreeViewerProvider provider;

    private Action refreshAction;

	public ExplorerView() {
		super();
	}

	public void dispose() {
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

        createActions();
        createMenu();
        createToolBar();

        Plugin.getDefault().addRssListener(this);
		treeViewer.setInput(Plugin.getDefault());
	}

	public void setFocus() {
		treeViewer.getControl().setFocus();
	}

	public void onChannelListChanged(ArrayList channels) {
		treeViewer.setInput(Plugin.getDefault());
	}
	
	public void onChannelStatusChanged(final Channel channel) {
		treeViewer.getControl().getDisplay().asyncExec(new Runnable() {
			public void run() {
				treeViewer.refresh(channel);
			}
		});
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

	public void onItemStatusChanged(final Item item) {
		treeViewer.getControl().getDisplay().asyncExec(new Runnable() {
			public void run() {
				treeViewer.refresh(item);
			}
		});
	}

    private void createActions() {
        refreshAction = new Action("Refresh", Plugin.getDefault().getImageRegistry().getDescriptor(Plugin.ICON_REFRESH)) {
            public void run() {
                Plugin.getDefault().update();
            }
        };
        refreshAction.setToolTipText("Refresh");
    }

    private void createMenu() {
        //IMenuManager mgr = getViewSite().getActionBars().getMenuManager();
        //mgr.add(clearAction);
    }

    private void createToolBar() {
        IToolBarManager mgr = getViewSite().getActionBars().getToolBarManager();
        mgr.add(refreshAction);
    }

}
