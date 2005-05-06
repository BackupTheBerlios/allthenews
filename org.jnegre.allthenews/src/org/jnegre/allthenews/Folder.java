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

    private List content;

    public void setContent(List newContent) {
    	content = Collections.unmodifiableList(newContent);
    }

    public List getContent() {
    	return content;
    }
    
    private void writeObject(ObjectOutputStream out) throws IOException {
    	out.writeInt(1);//serialization version number
    	out.writeObject(content);
    }
    
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException{
    	int serializationVersion = in.readInt();
    	switch(serializationVersion) {
    		case 1:
    			content = (List)in.readObject();
    			break;
    		default:
    			throw new IOException("Unsupported serialization version: "+serializationVersion);
    	}
    }

}
