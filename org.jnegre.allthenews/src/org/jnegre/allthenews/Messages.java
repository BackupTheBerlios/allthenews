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
 * Created on 27 juil. 2004
 */
package org.jnegre.allthenews;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * @author Jérôme Nègre
 */
public class Messages {
	private static final String BUNDLE_NAME = "org.jnegre.allthenews.messages";//$NON-NLS-1$

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
			.getBundle(BUNDLE_NAME);

	private Messages() {
	}

	public static String getString(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
}