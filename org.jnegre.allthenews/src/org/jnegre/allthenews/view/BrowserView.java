/*
 * Created on 16 mai 2004
 * Copyright 2004 Jérôme Nègre
 */
package org.jnegre.allthenews.view;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.browser.StatusTextEvent;
import org.eclipse.swt.browser.StatusTextListener;
import org.eclipse.swt.browser.TitleEvent;
import org.eclipse.swt.browser.TitleListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.jnegre.allthenews.Channel;
import org.jnegre.allthenews.IconManager;
import org.jnegre.allthenews.Item;
import org.jnegre.allthenews.Messages;
import org.jnegre.allthenews.Plugin;
import org.jnegre.allthenews.RssListener;

/**
 * @author Jérôme Nègre
 */
public class BrowserView extends ViewPart implements RssListener, TitleListener, ProgressListener, StatusTextListener {

	private static final String LINK_MEMENTO_KEY = "link"; //$NON-NLS-1$
	private static final String SHOW_DESCRIPTION_MEMENTO_KEY = "showDescription"; //$NON-NLS-1$

	//0 = description; 1=description with BR; 2=url; 3=title
	private final static String HTML = Messages.getString("BrowserView.DescriptionTemplate"); //$NON-NLS-1$
	private final static String HTML_NO_DESCRIPTION = Messages.getString("BrowserView.NoDescription"); //$NON-NLS-1$

	Browser browser;
	Label statusLine;
	private boolean uiReady = false;
	private String title = null;
	private int loadFraction = 100;
	
    private Action backAction;
    private Action forwardAction;
    private Action refreshAction;
    private Action clearAction;
    private Action linkAction;
    private Action showDescritionAction;

    private boolean linkActionInitState = true;
    private boolean showDescriptionActionInitState = true;
	
	public BrowserView() {
		super();
		Plugin.getDefault().addRssListener(this);
	}

	public void dispose() {
		Plugin.getDefault().removeRssListener(this);
		super.dispose();
	}

	public void createPartControl(Composite parent) {
		GridLayout layout = new GridLayout(1,true);
		parent.setLayout(layout);
		browser = new Browser(parent, SWT.NONE);
		GridData data = new GridData(GridData.FILL_BOTH);
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = true;
		browser.setLayoutData(data);
		statusLine = new Label(parent, SWT.NONE);
		statusLine.setText("All The News - http://www.jnegre.org/allthenews/");
		data = new GridData(GridData.FILL_HORIZONTAL);
		statusLine.setLayoutData(data);
		browser.addTitleListener(this);
		browser.addProgressListener(this);
		browser.addStatusTextListener(this);
		uiReady = true;

        createActions();
        createMenu();
        createToolBar();
	}

	public void setFocus() {
		browser.setFocus();
	}

	public void onChannelListChanged(ArrayList channels) {
		//NOP
	}

	public void onChannelStatusChanged(Channel channel) {
		//NOP
	}
	public void onChannelSelected(Channel channel) {
		//NOP
	}

	public void onItemStatusChanged(Item item) {
		//NOP
	}

	public void onItemSelected(Item item) {
		if(item != null && uiReady && linkAction.isChecked()) {
			if(showDescritionAction.isChecked()) {
				String desc = item.getDescription();
				if(desc == null)
					desc = HTML_NO_DESCRIPTION;
				browser.setText(MessageFormat.format(HTML,new String[]{desc, encodeNewLine(desc), item.getUsableLink(), item.getUsableTitle()}));
			} else {
				browser.setUrl(item.getUsableLink());
			}
			//XXX this is a hack, should be done otherwise
			boolean channelStatus = item.getChannel().isUnread();
			item.setReadFlag(true);
			Plugin.getDefault().notifyItemStatusChanged(item, this);
			if(channelStatus != item.getChannel().isUnread()) {
				Plugin.getDefault().notifyChannelStatusChanged(item.getChannel(),this);
			}
		}
	}

	public void changed(TitleEvent event) {
		this.title = event.title;
		if(loadFraction == 100) {
			this.setContentDescription(this.title);
		}
	}
	
    private void createActions() {
    	//back
        backAction = new Action("Back", PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_BACK)) { //$NON-NLS-1$
            public void run() {
                (BrowserView.this).browser.back();
            }
        };
        backAction.setToolTipText("Go Back"); //$NON-NLS-1$
    	
    	//forward
        forwardAction = new Action("Forward", PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_FORWARD)) { //$NON-NLS-1$
            public void run() {
                (BrowserView.this).browser.forward();
            }
        };
        forwardAction.setToolTipText("Go Forward"); //$NON-NLS-1$
    	
    	//refresh
        refreshAction = new Action("Refresh", IconManager.getImageDescriptor(IconManager.ICON_ACTION_REFRESH)) { //$NON-NLS-1$
            public void run() {
                (BrowserView.this).browser.refresh();
            }
        };
        refreshAction.setToolTipText("Refresh"); //$NON-NLS-1$
        
        //clear
        clearAction = new Action("Clear", PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_DELETE)) { //$NON-NLS-1$
            public void run() {
                (BrowserView.this).browser.setUrl("about:blank"); //$NON-NLS-1$
            }
        };
        clearAction.setToolTipText("Clear"); //$NON-NLS-1$

        //link
        linkAction = new Action("Link", IAction.AS_CHECK_BOX) { //$NON-NLS-1$
        };
        linkAction.setImageDescriptor(IconManager.getImageDescriptor(IconManager.ICON_ACTION_LINK));
        linkAction.setChecked(linkActionInitState);
        linkAction.setToolTipText("Link With Views"); //$NON-NLS-1$

        //link
        showDescritionAction = new Action("Show Local Description First", IAction.AS_CHECK_BOX) { //$NON-NLS-1$
        };
        showDescritionAction.setChecked(showDescriptionActionInitState);
    }

    private void createMenu() {
        IMenuManager mgr = getViewSite().getActionBars().getMenuManager();
        mgr.add(showDescritionAction);
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
			Integer status = memento.getInteger(LINK_MEMENTO_KEY);
			if(status != null && status.intValue() == 0) {
				linkActionInitState = false;
			}
			
			status = memento.getInteger(SHOW_DESCRIPTION_MEMENTO_KEY);
			if(status != null && status.intValue() == 0) {
				showDescriptionActionInitState = false;
			}
		}
	}

	public void saveState(IMemento memento) {
		super.saveState(memento);
		memento.putInteger(LINK_MEMENTO_KEY,linkAction.isChecked()?1:0);
		memento.putInteger(SHOW_DESCRIPTION_MEMENTO_KEY,showDescritionAction.isChecked()?1:0);
	}

	public void changed(ProgressEvent event) {
		if(event.total!=0) {
			this.loadFraction = (100*event.current)/event.total;
		} else {
			this.loadFraction = 100;
		}
		if(loadFraction == 100) {
			this.setContentDescription(this.title);
		} else {
			this.setContentDescription("Loading: "+this.loadFraction+"%"); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	public void completed(ProgressEvent event) {
		this.setContentDescription(this.title);
	}
	
	private String encodeNewLine(String text) {
		StringTokenizer tokenizer = new StringTokenizer(text, "\n\r"); //$NON-NLS-1$
		StringBuffer buffer = new StringBuffer(text.length()*2);
		while(tokenizer.hasMoreTokens()) {
			buffer.append(tokenizer.nextToken());
			if(tokenizer.hasMoreTokens()) {
				buffer.append("<br/>"); //$NON-NLS-1$
			}
		}
		return buffer.toString();
	}

	public void changed(StatusTextEvent event) {
		statusLine.setText(event.text);
	}
}
