/*
 * Created on 16 mai 2004
 * Copyright 2004 J�r�me N�gre
 */
package org.jnegre.allthenews.view;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;
import org.jnegre.allthenews.Channel;
import org.jnegre.allthenews.Item;
import org.jnegre.allthenews.Plugin;

/**
 * @author J�r�me N�gre
 */
public class NewsTreeViewerProvider
		implements
			ITreeContentProvider,
			ILabelProvider {

	public Object[] getChildren(Object parentElement) {
		if(parentElement instanceof Channel) {
			return ((Channel)parentElement).getItems().toArray();
		} else {
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
	 */
	public Object getParent(Object element) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean hasChildren(Object element) {
		if(element instanceof Channel) {
			return !((Channel)element).getItems().isEmpty();
		} else {
			return false;
		}
	}

	public Object[] getElements(Object inputElement) {
		return ((Plugin)inputElement).getChannelList().toArray();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// TODO Auto-generated method stub
	}

	public Image getImage(Object element) {

		String iconId;
		
		if(element instanceof Channel) {
			Channel channel = (Channel)element;
			if(channel.isRefreshing()) {
				iconId = Plugin.ICON_REFRESH;
			} else if(channel.getErrorMessage()!=null) {
				iconId = Plugin.ICON_LED_RED;
			} else if(channel.isUnread()) {
				iconId = Plugin.ICON_LED_LIGHT_GREEN;
			} else {
				iconId = Plugin.ICON_LED_DARK_GREEN;
			}
		} else if(element instanceof Item) {
			Item item = (Item)element;
			if(item.isReadFlag()) {
				iconId = Plugin.ICON_LED_DARK_GREEN;
			} else {
				iconId = Plugin.ICON_LED_LIGHT_GREEN;
			}
		} else {
			return null;
		}
		return Plugin.getDefault().getImageRegistry().getDescriptor(iconId).createImage();
	}

	public String getText(Object element) {
		if(element instanceof Channel) {
			return ((Channel)element).getTitle();
		} else if(element instanceof Item) {
			return ((Item)element).getUsableTitle();
		} else {
			return "Unexpected object: "+element;
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
	 */
	public void addListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object, java.lang.String)
	 */
	public boolean isLabelProperty(Object element, String property) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
	 */
	public void removeListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub
		
	}
}
