/*
 * Created on 15 mai 2004
 * Copyright 2004 Jérôme Nègre
 */
package org.jnegre.allthenews.view;

import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.jnegre.allthenews.Channel;
import org.jnegre.allthenews.IconManager;
import org.jnegre.allthenews.Item;
import org.jnegre.allthenews.Plugin;
import org.jnegre.allthenews.RssListener;
import org.jnegre.allthenews.dialogs.NewChannelDialog;

/**
 * @author Jérôme Nègre
 */
public class ExplorerView extends ViewPart implements RssListener {
	
	TreeViewer treeViewer;
	private NewsTreeViewerProvider provider;

    private Action refreshAction;
    private Action newChannelAction;

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
						Plugin.getDefault().notifyChannelSelected((Channel)selected,ExplorerView.this);
					} else if(selected instanceof Item) {
						Plugin.getDefault().notifyItemSelected((Item)selected,ExplorerView.this);
					}
				}
			}
		});

        createActions();
        createMenu();
        createToolBar();

        Plugin.getDefault().addRssListener(this);
		treeViewer.setInput(Plugin.getDefault().getRootFolder());
	}

	public void setFocus() {
		treeViewer.getControl().setFocus();
	}

	public void onChannelListChanged(List channels) {
		treeViewer.setInput(Plugin.getDefault().getRootFolder());
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

	public void onItemSelected(Item tiem) {
		// NOP
	}

	public void onItemStatusChanged(final Item item) {
		treeViewer.getControl().getDisplay().asyncExec(new Runnable() {
			public void run() {
				treeViewer.refresh(item);
			}
		});
	}

    private void createActions() {
    	//refresh
        refreshAction = new Action("Refresh", IconManager.getImageDescriptor(IconManager.ICON_ACTION_REFRESH)) {
            public void run() {
                Plugin.getDefault().update();
            }
        };
        refreshAction.setToolTipText("Refresh");

    	//newChannel
        newChannelAction = new Action("Add New Channel") {
            public void run() {
                NewChannelDialog ncd = new NewChannelDialog(ExplorerView.this.getViewSite().getShell());
                ncd.open();
            }
        };
    }

    private void createMenu() {
        IMenuManager mgr = getViewSite().getActionBars().getMenuManager();
        mgr.add(newChannelAction);
    }

    private void createToolBar() {
        IToolBarManager mgr = getViewSite().getActionBars().getToolBarManager();
        mgr.add(refreshAction);
    }

}
