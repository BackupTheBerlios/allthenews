package org.jnegre.allthenews.view;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
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
public class TabViewContent implements ViewContent {

    private TabFolder tabFolder;

    /**
     * Constructor for TabViewContent.
     */
    public TabViewContent() {
        super();
    }

    public void setFocus() {
        tabFolder.setFocus();
    }

    public void createPartControl(Composite parent) {
        tabFolder = new TabFolder(parent, SWT.NONE);
    }

    public void fillChannelTable() {
        Iterator iterator = Plugin.getDefault().getChannelList().iterator();
        synchronized(tabFolder) {
            TabItem[] items = tabFolder.getItems();
            for(int i = 0; i<items.length; i++) {
                items[i].dispose();
            }
            while(iterator.hasNext()) {
                Channel channel = (Channel)iterator.next();
                TabItem tabItem = new TabItem(tabFolder,SWT.NONE);
                tabItem.setText(channel.getTitle());
                tabItem.setImage(channel.getIcon());
                tabItem.setToolTipText(channel.getErrorMessage());
                tabItem.setData(channel);

                Table itemTable = new Table(tabFolder, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
                new TableColumn(itemTable, SWT.LEFT);
                new TableColumn(itemTable, SWT.CENTER);
                new TableColumn(itemTable, SWT.LEFT);
                itemTable.addSelectionListener(new SelectionAdapter() {
                    public void widgetDefaultSelected(SelectionEvent e) {
                        try {
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
                            item.fillTableItem((TableItem)e.item);
                        } catch (Exception exp) {
                            Plugin.logError("Exception in All The News",exp);
                       }
                    }
                });

                tabItem.setControl(itemTable);

				//menu
				Menu menu = new Menu(tabFolder.getShell(), SWT.POP_UP);
				MenuItem menuItem;
				menuItem = new MenuItem(menu, SWT.PUSH);
				menuItem.setText("Mark As Read");
				menuItem.addSelectionListener(new SelectionAdapter() {
		            public void widgetSelected(SelectionEvent e) {
		            	TabItem tabItem = tabFolder.getSelection()[0];
		            	Table itemTable = (Table)tabItem.getControl();
		            	if(itemTable.getSelectionCount() != 0) {
		            		TableItem tableItem = itemTable.getSelection()[0];
			                Item item = (Item) tableItem.getData();
			                item.setReadFlag(true);
			                Plugin.getDefault().refreshChannelContentInViews(item.getChannel());
		            	}
		            }
				});
		
				menuItem = new MenuItem(menu, SWT.PUSH);
				menuItem.setText("Mark All As Read");
				menuItem.addSelectionListener(new SelectionAdapter() {
		            public void widgetSelected(SelectionEvent e) {
		            	TabItem tabItem = tabFolder.getSelection()[0];
		            	Table itemTable = (Table)tabItem.getControl();
		            	TableItem[] items = itemTable.getItems();
		            	for(int i=0; i<items.length; i++) {
		            		TableItem tableItem = items[i];
			                Item item = (Item) tableItem.getData();
			                item.setReadFlag(true);
			                if(i == items.length-1) {
			                	Plugin.getDefault().refreshChannelContentInViews(item.getChannel());
			                }
		            	}
		            }
				});
		
				new MenuItem(menu, SWT.SEPARATOR);
		
				menuItem = new MenuItem(menu, SWT.PUSH);
				menuItem.setText("Mark As New");
				menuItem.addSelectionListener(new SelectionAdapter() {
		            public void widgetSelected(SelectionEvent e) {
		            	TabItem tabItem = tabFolder.getSelection()[0];
		            	Table itemTable = (Table)tabItem.getControl();
		            	if(itemTable.getSelectionCount() != 0) {
		            		TableItem tableItem = itemTable.getSelection()[0];
			                Item item = (Item) tableItem.getData();
			                item.setReadFlag(false);
			                Plugin.getDefault().refreshChannelContentInViews(item.getChannel());
		            	}
		            }
				});
		
				itemTable.setMenu(menu);

            }
        }
    }

    public void refreshChannelContent(Channel channel) {
        synchronized(tabFolder) {
            TabItem[] tabItems = tabFolder.getItems();
            for(int i=0;i<tabItems.length;i++) {
                if(tabItems[i].getData() == channel) {
                    tabItems[i].setImage(channel.getIcon());
                    tabItems[i].setToolTipText(channel.getErrorMessage());

                    Table itemTable = (Table)tabItems[i].getControl();
                    itemTable.removeAll();
                    ArrayList items = channel.getItems();
                    for (int j = 0; j < items.size(); j++) {
                        ((Item)items.get(j)).toTableItem(itemTable);
                    }
                    itemTable.getColumn(0).pack();
                    itemTable.getColumn(1).pack();
                    itemTable.getColumn(2).pack();
                    
                }
            }
        }        
    }

    /**
     * @see org.jnegre.allthenews.ViewContent#refreshChannelIcon(Channel)
     */
    public void refreshChannelIcon(Channel channel) {
        synchronized(tabFolder) {
            TabItem[] tabItems = tabFolder.getItems();
            for(int i=0;i<tabItems.length;i++) {
                if(tabItems[i].getData() == channel) {
                    tabItems[i].setImage(channel.getIcon());
                }
            }
        }        
    }

}
