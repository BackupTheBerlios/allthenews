/*
 * Created on 16 mai 2004
 * Copyright 2004 Jérôme Nègre
 */
package org.jnegre.allthenews.view;

import java.util.ArrayList;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.TitleEvent;
import org.eclipse.swt.browser.TitleListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.jnegre.allthenews.Channel;
import org.jnegre.allthenews.Item;
import org.jnegre.allthenews.Plugin;
import org.jnegre.allthenews.RssListener;

/**
 * @author Jérôme Nègre
 */
public class BrowserView extends ViewPart implements RssListener, TitleListener {

	private static final String LINK_MEMENTO_KEY = "link";
	
	Browser browser;
	private boolean uiReady = false;
	
    private Action backAction;
    private Action forwardAction;
    private Action refreshAction;
    private Action linkAction;
    private Action clearAction;

    private boolean linkActionInitState = false;
	
	public BrowserView() {
		super();
		Plugin.getDefault().addRssListener(this);
	}

	public void createPartControl(Composite parent) {
		browser = new Browser(parent, SWT.NONE);
		browser.addTitleListener(this);
		uiReady = true;

        createActions();
        createMenu();
        createToolBar();
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
		if(item != null && uiReady && linkAction.isChecked()) {
			browser.setUrl(item.getUsableLink());
			item.setReadFlag(true);
			Plugin.getDefault().notifyItemStatusChanged(item, this);
		}
	}

	public void changed(TitleEvent event) {
		this.setTitle(event.title);
	}
	
    private void createActions() {
    	//back
        backAction = new Action("Back", PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_BACK)) {
            public void run() {
                (BrowserView.this).browser.back();
            }
        };
        backAction.setToolTipText("Go Back");
    	
    	//forward
        forwardAction = new Action("Forward", PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_FORWARD)) {
            public void run() {
                (BrowserView.this).browser.forward();
            }
        };
        forwardAction.setToolTipText("Go Forward");
    	
    	//refresh
        refreshAction = new Action("Refresh", Plugin.getDefault().getImageRegistry().getDescriptor(Plugin.ICON_REFRESH)) {
            public void run() {
                (BrowserView.this).browser.refresh();
            }
        };
        refreshAction.setToolTipText("Refresh");
        
        //link
        linkAction = new Action("Link", IAction.AS_CHECK_BOX) {
        };
        linkAction.setImageDescriptor(Plugin.getDefault().getImageRegistry().getDescriptor(Plugin.ICON_LINK));
        linkAction.setChecked(linkActionInitState);
        linkAction.setToolTipText("Link With Views");

        //clear
        clearAction = new Action("Clear", PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_DELETE)) {
            public void run() {
                (BrowserView.this).browser.setUrl("about:blank");
            }
        };
        clearAction.setToolTipText("Clear");

    }

    private void createMenu() {
        //IMenuManager mgr = getViewSite().getActionBars().getMenuManager();
        //mgr.add(clearAction);
    }

    private void createToolBar() {
        IToolBarManager mgr = getViewSite().getActionBars().getToolBarManager();
        mgr.add(backAction);
        mgr.add(forwardAction);
        mgr.add(refreshAction);
        mgr.add(linkAction);
        mgr.add(clearAction);
    }

	public void init(IViewSite site, IMemento memento) throws PartInitException {
		super.init(site, memento);
		if(memento != null) {
			Integer link = memento.getInteger(LINK_MEMENTO_KEY);
			if(link != null && link.intValue() == 1) {
				linkActionInitState = true;
			}
		}
	}

	public void saveState(IMemento memento) {
		super.saveState(memento);
		memento.putInteger(LINK_MEMENTO_KEY,linkAction.isChecked()?1:0);
	}
}
