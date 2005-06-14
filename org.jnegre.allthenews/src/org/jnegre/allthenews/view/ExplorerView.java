/*
 * Created on 15 mai 2004
 * Copyright 2004 Jérôme Nègre
 */
package org.jnegre.allthenews.view;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.part.ViewPart;
import org.jnegre.allthenews.Channel;
import org.jnegre.allthenews.Folder;
import org.jnegre.allthenews.IconManager;
import org.jnegre.allthenews.Item;
import org.jnegre.allthenews.Plugin;
import org.jnegre.allthenews.RssListener;
import org.jnegre.allthenews.dialogs.ChannelPropertiesDialog;
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
		treeViewer.getTree().setMenu(createContextMenu(parent));
		
		//FIXME D'n'D start of experiments
		Transfer[] types = new Transfer[] {NewsTransfer.getInstance()};
		int operations = DND.DROP_MOVE;
		
		final DragSource source = new DragSource (treeViewer.getTree(), operations);
		source.setTransfer(types);
		source.addDragListener (new DragSourceListener () {
			
			Object dragData;
			
			public void dragStart(DragSourceEvent event) {
				Object selected = ((StructuredSelection)treeViewer.getSelection()).getFirstElement();
				if (selected != null) {
					event.doit = true;
					dragData = selected;
				} else {
					event.doit = false;
				}
				System.out.println("dragStart "+event);
			}
			public void dragSetData (DragSourceEvent event) {
				event.data = dragData;//.toString();
				System.out.println("dragSetData "+event);
			}
			public void dragFinished(DragSourceEvent event) {
				System.out.println("dragFinished "+event);
			}
		});
		
		
		
		
		
		
		DropTarget target = new DropTarget(treeViewer.getTree(), operations);
		target.setTransfer(types);
		target.addDropListener (new DropTargetAdapter() {
			
			//FIXME should be "after"
			Boolean before = null;
			Object dropTarget = null;
			
			public void dragOver(DropTargetEvent event) {
				event.feedback = DND.FEEDBACK_SCROLL;
				if (event.item != null) {
					TreeItem item = (TreeItem)event.item;
					dropTarget = item.getData();
					boolean isFolder = dropTarget instanceof Folder;
					boolean isChannel = dropTarget instanceof Channel;

					Point pt = treeViewer.getTree().getDisplay().map(null, treeViewer.getTree(), event.x, event.y);
					Rectangle bounds = item.getBounds();
					if(isFolder) {
						if (pt.y < bounds.y + bounds.height/3) {
							event.feedback |= DND.FEEDBACK_INSERT_BEFORE;
							before = Boolean.TRUE;
						} else if (pt.y > bounds.y + 2*bounds.height/3) {
							event.feedback |= DND.FEEDBACK_INSERT_AFTER;
							before = Boolean.FALSE;
						} else {
							event.feedback |= DND.FEEDBACK_SELECT;
							before = null;
						}
						event.feedback |= DND.FEEDBACK_EXPAND;
						event.detail = DND.DROP_MOVE;
					} else if(isChannel) {
						//channels can not be selected
						if (pt.y < bounds.y + bounds.height/2) {
							event.feedback |= DND.FEEDBACK_INSERT_BEFORE;
							before = Boolean.TRUE;
						} else {
							event.feedback |= DND.FEEDBACK_INSERT_AFTER;
							before = Boolean.FALSE;
						}
						event.detail = DND.DROP_MOVE;
					} else {
						event.detail = DND.DROP_NONE;
					}
				} else {
					event.detail = DND.DROP_NONE;
				}
				System.out.println("dragOver"+event);
			}
			public void drop(DropTargetEvent event) {
				System.out.println("drop "+event.data+" on "+dropTarget+", before="+before+", class="+dropTarget.getClass());
				if(dropTarget instanceof Channel) {
					//FIXME race condition when moving a channel
					Channel target = (Channel)dropTarget;
					Channel source = (Channel)event.data;
					Folder targetFolder = target.getParentFolder();
					Folder sourceFolder = source.getParentFolder();
					ArrayList targetFolderContent = new ArrayList(targetFolder.getContent());
					ArrayList sourceFolderContent;
					if(targetFolder == sourceFolder) {
						sourceFolderContent = targetFolderContent;
					} else {
						sourceFolderContent = new ArrayList(sourceFolder.getContent());
					}
					int targetIndex = targetFolderContent.indexOf(target);
					if(!before.booleanValue()) {
						targetIndex += 1;
					}
					int sourceIndex = sourceFolderContent.indexOf(source);
					if(targetFolder == sourceFolder && targetIndex < sourceIndex) {
						sourceIndex += 1;
					}
					System.out.println("should be moved from "+sourceIndex+" to position "+targetIndex);
					targetFolderContent.add(targetIndex, source);
					sourceFolderContent.remove(sourceIndex);
					targetFolder.setContent(targetFolderContent);
					sourceFolder.setContent(sourceFolderContent);
					Plugin.getDefault().notifyChannelListChanged(null);
				}
			}
		});

		//FIXME D'n'D end

		createActions();
        createMenu();
        createToolBar();

        Plugin.getDefault().addRssListener(this);
		treeViewer.setInput(Plugin.getDefault().getRootFolder());
	}
	
	private Menu createContextMenu(Composite parent) {
		final Menu menu = new Menu (parent);
		MenuItem item = new MenuItem (menu, SWT.PUSH);
		item.setText ("Properties...");
		item.addListener (SWT.Selection, new Listener () {
			public void handleEvent (Event e) {
				Object selected = ((StructuredSelection)treeViewer.getSelection()).getFirstElement();
				if(selected instanceof Channel) {
	                ChannelPropertiesDialog ncd = new ChannelPropertiesDialog(ExplorerView.this.getViewSite().getShell(), (Channel)selected);
	                ncd.open();
				}
			}
		});
		
		menu.addListener (SWT.Show, new Listener () {
			public void handleEvent (Event event) {
				Object selected = ((StructuredSelection)treeViewer.getSelection()).getFirstElement();
				boolean isChannel = selected instanceof Channel;
				menu.getItem(0).setEnabled(isChannel);//Properties
			}
		});
		
		return menu;
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
