/*******************************************************************************
 * Copyright (c) 2004 Jérôme Nègre.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.jnegre.org/cpl1_0.html
 * 
 * Contributors:
 *     Jérôme Nègre - initial API and implementation
 *******************************************************************************/

/*
 * Created on 12 juin 2004
 */
package org.jnegre.allthenews;

import java.net.URL;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;

/**
 * @author Jérôme Nègre
 */
public class IconManager {
	//the root folder containing the icons
	private static final String ICON_FOLDER = "icons/";
	
	//real file locations
	private static final String LOC_LED_DARK_GREEN = "led_dark_green.gif";
	private static final String LOC_LED_LIGHT_GREEN = "led_light_green.gif";
	private static final String LOC_LED_RED = "led_red.gif";
	private static final String LOC_LED_YELLOW = "led_yellow.gif";
	private static final String LOC_LINK = "link.gif";
	private static final String LOC_REFRESH = "refresh.gif";

	//list of all icon files to put in the ImageRegistry
	private static final String[] LOCATIONS = new String[]{
			LOC_LED_DARK_GREEN,
			LOC_LED_LIGHT_GREEN,
			LOC_LED_RED,
			LOC_LED_YELLOW,
			LOC_LINK,
			LOC_REFRESH
	};

	//public names
	public static final String ICON_STATUS_ERROR   = LOC_LED_RED;
	public static final String ICON_STATUS_UNREAD  = LOC_LED_LIGHT_GREEN;
	public static final String ICON_STATUS_READ    = LOC_LED_DARK_GREEN;
	public static final String ICON_STATUS_REFRESH = LOC_LED_YELLOW;
	
	public static final String ICON_ACTION_REFRESH = LOC_REFRESH;
	public static final String ICON_ACTION_LINK = LOC_LINK;
	
	/**
	 * Populates an image registry with all the locations
	 * @param registry
	 */
	protected static void populateImageRegistry(ImageRegistry registry) {
		for(int i=0; i<LOCATIONS.length; i++) {
			registry.put(LOCATIONS[i],createImageDescriptor(LOCATIONS[i]));
		}
	}

	/**
	 * Creates the ImageDescriptor of a file given its path in the
	 * ICON_FOLDER.
	 * @param relativePath
	 * @return the ImageDescriptor
	 */
    private static ImageDescriptor createImageDescriptor(String relativePath) {
		try {
			URL url = new URL(Plugin.getDefault().getDescriptor().getInstallURL(),
					ICON_FOLDER + relativePath);
			return ImageDescriptor.createFromURL(url);
		} catch (java.net.MalformedURLException e) {
			return ImageDescriptor.getMissingImageDescriptor();
		}
	}
	
	public static ImageDescriptor getImageDescriptor(String key) {
		return Plugin.getDefault().getImageRegistry().getDescriptor(key);
	}
	
	public static Image getImage(String key) {
		return Plugin.getDefault().getImageRegistry().get(key);
	}
	
	/**
	 * This class should not be instanciated
	 */
	private IconManager() {
		//NOP
	}
}
