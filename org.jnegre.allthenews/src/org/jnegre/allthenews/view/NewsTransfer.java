/*******************************************************************************
 * Copyright (c) 2005 Jérôme Nègre.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.jnegre.org/cpl1_0.html
 * 
 * Contributors:
 *     Jérôme Nègre - initial API and implementation
 *******************************************************************************/

/*
 * Created on 12 juin 2005
 */
package org.jnegre.allthenews.view;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.TransferData;

/**
 * @author Jérôme Nègre
 */
public class NewsTransfer extends ByteArrayTransfer {

	private static NewsTransfer instance = new NewsTransfer();
	
	private static final String TYPE_NAME = "allthenews-transfer-format";//$NON-NLS-1$
	private static final int TYPEID = registerType(TYPE_NAME);

	public static NewsTransfer getInstance() {
		return instance;
	}
	
	private NewsTransfer() {
		super();
	}

	protected int[] getTypeIds() {
		return new int[] {TYPEID};
	}
	protected String[] getTypeNames() {
		return new String[] {TYPE_NAME};
	}

	protected void javaToNative(Object object, TransferData transferData) {
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ObjectOutputStream dataOut = new ObjectOutputStream(out);

			dataOut.writeObject(object);

			//cleanup
			dataOut.close();
			out.close();
			byte[] bytes = out.toByteArray();
			super.javaToNative(bytes, transferData);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected Object nativeToJava(TransferData transferData) {
		try {
			byte[] bytes =  (byte[])super.nativeToJava(transferData);
			ByteArrayInputStream in = new ByteArrayInputStream(bytes);
			ObjectInputStream dataIn = new ObjectInputStream(in);
			
			Object result = dataIn.readObject();
			
			dataIn.close();
			in.close();
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
