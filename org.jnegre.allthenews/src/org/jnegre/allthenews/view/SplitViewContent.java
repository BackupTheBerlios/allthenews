package org.jnegre.allthenews.view;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
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
public class SplitViewContent implements ViewContent {

    private Table channelTable;
    private Table itemTable;
    private Label messageLabel;

    /**
     * Constructor for SplitViewContent.
     */
    public SplitViewContent() {
        super();
    }

    public void setFocus() {
        channelTable.setFocus();
    }

    public void createPartControl(Composite parent) {
        Composite composite = new Composite(parent, 0);
        GridLayout layout = new GridLayout();
        layout.numColumns = 1;
        layout.marginHeight = 1;
        layout.marginWidth = 1;
        layout.verticalSpacing = 3;
        composite.setLayout(layout);
        GridData data = new GridData(GridData.FILL_BOTH);
        data.grabExcessHorizontalSpace = true;
        data.grabExcessVerticalSpace = true;
        composite.setLayoutData(data);

        SashForm sashForm = new SashForm(composite, SWT.HORIZONTAL);
        GridData gd = new GridData();
        gd.horizontalAlignment = GridData.FILL;
        gd.verticalAlignment = GridData.FILL;
        gd.grabExcessHorizontalSpace = true;
        gd.grabExcessVerticalSpace = true;
        sashForm.setLayoutData(gd);

        //channels
        channelTable = new Table(sashForm, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
        new TableColumn(channelTable, SWT.LEFT).setWidth(20);
        new TableColumn(channelTable, SWT.LEFT);
        channelTable.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                Channel channel = (Channel)e.item.getData();
                fillItemTable(channel);
            }    
        });


        //items

        itemTable = new Table(sashForm, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
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

		//menu
		Menu menu = new Menu(parent.getShell(), SWT.POP_UP);
		MenuItem menuItem;
		menuItem = new MenuItem(menu, SWT.PUSH);
		menuItem.setText("Mark As Read");
		menuItem.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
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
            	if(itemTable.getSelectionCount() != 0) {
            		TableItem tableItem = itemTable.getSelection()[0];
	                Item item = (Item) tableItem.getData();
	                item.setReadFlag(false);
	                Plugin.getDefault().refreshChannelContentInViews(item.getChannel());
            	}
            }
		});

		itemTable.setMenu(menu);

        //message
        messageLabel = new Label(composite,SWT.NULL);
        gd = new GridData();
        gd.horizontalAlignment = GridData.FILL;
        messageLabel.setLayoutData(gd);
        
        sashForm.setWeights(new int[] {1,5});
    }

    private void fillItemTable(Channel channel) {
        itemTable.removeAll();
        ArrayList items = channel.getItems();
        for (int i = 0; i < items.size(); i++) {
            ((Item)items.get(i)).toTableItem(itemTable);
        }
        itemTable.getColumn(0).pack();
        itemTable.getColumn(1).pack();
        itemTable.getColumn(2).pack();
        setMessage(channel.getErrorMessage());
    }

    private void setMessage(String message) {
        messageLabel.setText(message!=null?message:"Ok");
        messageLabel.setToolTipText(message!=null?message:"Ok");
    }

    public void fillChannelTable() {
        Iterator iterator = Plugin.getDefault().getChannelList().iterator();
        synchronized(channelTable) {
            channelTable.removeAll();
            while(iterator.hasNext()) {
                Channel channel = (Channel)iterator.next();
                channel.toTableItem(channelTable);
            }
            channelTable.getColumn(1).pack();
            if(channelTable.getItemCount() != 0) {
                channelTable.setSelection(0);
            }
        }
    }

    public void refreshChannelContent(Channel channel) {
        synchronized(channelTable) {
            TableItem[] tableItems = channelTable.getItems();
            for(int i=0;i<tableItems.length;i++) {
                if(tableItems[i].getData() == channel) {
                    channel.fillTableItem(tableItems[i]);
                    if(i == channelTable.getSelectionIndex()) {
                        fillItemTable(channel);
                    }
                }
            }
        }        
    }

    /**
     * @see org.jnegre.allthenews.ViewContent#refreshChannelIcon(Channel)
     */
    public void refreshChannelIcon(Channel channel) {
        synchronized(channelTable) {
            TableItem[] tableItems = channelTable.getItems();
            for(int i=0;i<tableItems.length;i++) {
                if(tableItems[i].getData() == channel) {
                    channel.fillTableItem(tableItems[i]);
                }
            }
        }        
    }

}
