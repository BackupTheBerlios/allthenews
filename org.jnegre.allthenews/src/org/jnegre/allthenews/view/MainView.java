package org.jnegre.allthenews.view;


import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.part.ViewPart;
import org.jnegre.allthenews.Channel;
import org.jnegre.allthenews.Plugin;

public class MainView extends ViewPart {

    protected Action refreshAction;
    private ViewContent viewContent;

    public MainView() {
        super();
        Plugin.getDefault().addView(this);
    }

    public void setFocus() {
        viewContent.setFocus();
    }

    public Display getDisplay() {
        int tentatives = 50;
        Display display = null;
        do {
            try {
                return this.getViewSite().getShell().getDisplay();
            } catch (NullPointerException e) {
                Object object = new Object();
                synchronized(object) {
                    tentatives -= 1;
                    try {
                        object.wait(500);
                    } catch(InterruptedException ie) {
                    }
                }
            }
        } while(display==null && tentatives != 0);
        return display;
    }

    public void createPartControl(Composite parent) {
        switch(Plugin.getDefault().getPreferenceStore().getInt(Plugin.VIEW_TYPE_PREFERENCE)) {
            case 1:
                viewContent = new SplitViewContent();
                break;
            case 2:
            default:
                viewContent = new TabViewContent();
                break;
            case 3:
                viewContent = new TreeViewContent();
                break;
        }
        
        viewContent.createPartControl(parent);

        fillChannelTable();
        createActions();
        createMenu();
        createToolBar();
    }

    protected void createActions() {
        refreshAction = new Action("Refresh", Plugin.getDefault().getImageDescriptor(Plugin.ICON_REFRESH)) {
            public void run() {
                Plugin.getDefault().update();
            }
        };
        refreshAction.setToolTipText("Refresh");
    }

    protected void createMenu() {
        //IMenuManager mgr = getViewSite().getActionBars().getMenuManager();
        //mgr.add(clearAction);
    }

    protected void createToolBar() {
        IToolBarManager mgr = getViewSite().getActionBars().getToolBarManager();
        mgr.add(refreshAction);
    }

    /**
     * @see org.eclipse.ui.IWorkbenchPart#dispose()
     */
    public void dispose() {
        Plugin.getDefault().removeView(this);
        super.dispose();
    }

    public void fillChannelTable() {
        viewContent.fillChannelTable();
    }

    public void refreshChannelContent(Channel channel) {
        viewContent.refreshChannelContent(channel);
    }

    public void refreshChannelIcon(Channel channel) {
        viewContent.refreshChannelIcon(channel);
    }

}