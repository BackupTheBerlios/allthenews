package org.jnegre.allthenews.view;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableTree;
import org.eclipse.swt.custom.TableTreeItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.update.internal.ui.UpdatePerspective;
import org.eclipse.update.internal.ui.UpdateUI;
import org.eclipse.update.internal.ui.views.IEmbeddedWebBrowser;
import org.jnegre.allthenews.Channel;
import org.jnegre.allthenews.Item;
import org.jnegre.allthenews.Plugin;

/**
 * @author jnegre - http://www.jnegre.org/
 *
 * (c)Copyright 2002 Jérôme Nègre
 * 
 */
public class TreeViewContent implements ViewContent {

    private TableTree tree;

    /**
     * Constructor for TreeViewContent.
     */
    public TreeViewContent() {
        super();
    }

    public void setFocus() {
        tree.setFocus();
    }

    public void createPartControl(Composite parent) {
        tree = new TableTree(parent, SWT.SINGLE | SWT.FULL_SELECTION);
        Table table = tree.getTable();
        new TableColumn(table, SWT.LEFT);
        new TableColumn(table, SWT.CENTER);
        new TableColumn(table, SWT.LEFT);

        tree.addSelectionListener(new SelectionAdapter() {
            public void widgetDefaultSelected(SelectionEvent e) {
                try {
                    if(e.item.getData() instanceof Item) {
                        boolean win32 = SWT.getPlatform().equals("win32");
                        boolean useEmbedded =
                            (Plugin.getDefault().getPreferenceStore().getInt(Plugin.BROWSER_TYPE_PREFERENCE) == 1);
                        Item item = (Item) e.item.getData();
                        if (win32 && useEmbedded) {
                            IWorkbenchPage page = UpdateUI.getActivePage();
                            try {
                                IViewPart part = page.showView(UpdatePerspective.ID_BROWSER);
                                ((IEmbeddedWebBrowser) part).openTo(item.getUsableLink());
                            } catch (PartInitException ex) {
                                UpdateUI.logException(ex);
                            }
                        } else {
                            Runtime.getRuntime().exec(
                                new String[] {
                                    Plugin.getDefault().getPreferenceStore().getString(Plugin.BROWSER_PREFERENCE),
                                    item.getUsableLink() });
                        }
                        item.setReadFlag(true);
                        ((TableTreeItem)e.item).setText(1,"");
                    }
                } catch (Exception exp) {
                    Plugin.logError("Exception in All The News",exp);
                }
            }
        });

		//menu
		Menu menu = new Menu(parent.getShell(), SWT.POP_UP);
		MenuItem menuItem;
		menuItem = new MenuItem(menu, SWT.PUSH);
		menuItem.setText("Mark As Read");
		menuItem.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
            	if(tree.getSelectionCount() != 0) {
            		Object data = tree.getSelection()[0].getData();
            		if(data instanceof Item) {
            			Item item = ((Item) data);
		                item.setReadFlag(true);
		                Plugin.getDefault().refreshChannelContentInViews(item.getChannel());
            		} else { //mark all
            			Channel channel = (Channel)data;
            			Iterator items = channel.getItems().iterator();
            			while(items.hasNext()) {
            				((Item)items.next()).setReadFlag(true);
            			}
            			Plugin.getDefault().refreshChannelContentInViews(channel);
            		}
            	}
            }
		});

		new MenuItem(menu, SWT.SEPARATOR);

		menuItem = new MenuItem(menu, SWT.PUSH);
		menuItem.setText("Mark As New");
		menuItem.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
            	if(tree.getSelectionCount() != 0) {
            		Object data = tree.getSelection()[0].getData();
            		if(data instanceof Item) {
            			Item item = ((Item) data);
		                item.setReadFlag(false);
		                Plugin.getDefault().refreshChannelContentInViews(item.getChannel());
            		} else { //mark all
            			Channel channel = (Channel)data;
            			Iterator items = channel.getItems().iterator();
            			while(items.hasNext()) {
            				((Item)items.next()).setReadFlag(false);
            			}
            			Plugin.getDefault().refreshChannelContentInViews(channel);
            		}
            	}
            }
		});

		tree.setMenu(menu);
    }

    public void fillChannelTable() {
    	if(tree == null) {
    		return;
    	}
        Iterator iterator = Plugin.getDefault().getChannelList().iterator();
        synchronized(tree) {
            TableTreeItem[] items = tree.getItems();
            for(int i = 0; i<items.length; i++) {
                items[i].dispose();
            }
            while(iterator.hasNext()) {
                Channel channel = (Channel)iterator.next();
                TableTreeItem treeItem = new TableTreeItem(tree,SWT.NONE);
                treeItem.setText(0,channel.getTitle());
                treeItem.setImage(1,channel.getIcon());
                treeItem.setText(2,channel.getErrorMessage()==null?"":channel.getErrorMessage());
                treeItem.setData(channel);
            }
        }
    }

    public void refreshChannelContent(Channel channel) {
    	if(tree == null) {
    		return;
    	}
        synchronized(tree) {
            TableTreeItem[] treeItems = tree.getItems();
            boolean[] expandedStates = new boolean[treeItems.length];
            for(int i=0;i<treeItems.length;i++) {
                expandedStates[i] = treeItems[i].getExpanded();
                if(treeItems[i].getData() == channel) {
                    treeItems[i].setText(0,channel.getTitle());
                    treeItems[i].setImage(1,channel.getIcon());
                    treeItems[i].setText(2,channel.getErrorMessage()==null?"":channel.getErrorMessage());

                    TableTreeItem oldITems[] = treeItems[i].getItems();
                    for (int j = 0; j < oldITems.length; j++) {
                        oldITems[j].dispose();
                    }

                    ArrayList items = channel.getItems();
                    for (int j = 0; j < items.size(); j++) {
                        Item item = (Item)items.get(j);
                        TableTreeItem childItem = new TableTreeItem(treeItems[i],SWT.NONE);
                        childItem.setText(0,item.getDate());
                        childItem.setText(1,item.isReadFlag()?"":"*");
                        childItem.setText(2,item.getUsableTitle());
                        childItem.setData(item);
                    }
                }
                treeItems[i].setExpanded(true);
            }
            Table table = tree.getTable();
            table.getColumn(0).pack();
            table.getColumn(1).pack();
            table.getColumn(2).pack();
            for(int i=0;i<treeItems.length;i++) {
                treeItems[i].setExpanded(expandedStates[i]);
            }            
        }        
    }

    /**
     * @see org.jnegre.allthenews.ViewContent#refreshChannelIcon(Channel)
     */
    public void refreshChannelIcon(Channel channel) {
    	if(tree == null) {
    		return;
    	}
        synchronized(tree) {
            TableTreeItem[] treeItems = tree.getItems();
            for(int i=0;i<treeItems.length;i++) {
                if(treeItems[i].getData() == channel) {
                    treeItems[i].setImage(1,channel.getIcon());
                }
            }
        }        
    }

}
