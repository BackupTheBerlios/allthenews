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
 * Created on 6 mars 2005
 */
package org.jnegre.allthenews;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * @author Jérôme Nègre
 */
public class Folder implements Serializable {

	private static final long serialVersionUID = 1L; //must not change

	private String name;
    private List content;

    /**
     * Constructs the (anonymous) root folder
     *
     */
    public Folder() {
    	this("ROOT FOLDER");
    }
    
    /**
     * Constructs a named folder (not the root folder)
     * @param name
     */
    public Folder(String name) {
    	this.name = name;
    }

    public void setContent(List newContent) {
    	content = Collections.unmodifiableList(newContent);
    }

    public List getContent() {
    	return content;
    }
    
    public void setName(String newName) {
    	this.name = newName;
    }
    
    public String getName() {
    	return name;
    }
    
    private void writeObject(ObjectOutputStream out) throws IOException {
    	out.writeInt(1);//serialization version number
    	out.writeObject(name);
    	out.writeObject(content);
    }
    
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException{
    	int serializationVersion = in.readInt();
    	switch(serializationVersion) {
    		case 1:
    			name = (String)in.readObject();
    			content = (List)in.readObject();
    			break;
    		default:
    			throw new IOException("Unsupported serialization version: "+serializationVersion);
    	}
    }

}
