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
 * Created on 3 juin 2005
 */
package org.jnegre.allthenews;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Jérôme Nègre
 */
public class Transformation implements Serializable {

	private static final long serialVersionUID = 1L; //must not change
	
	private String name;
	private Pattern pattern;
	private String replacement;
	private boolean replaceAll;
	
	/**
	 * @param pattern
	 * @param replacement
	 */
	public Transformation(final String name, final String regex, final String replacement, final boolean replaceAll) {
		this.name = name;
		this.pattern = Pattern.compile(regex);
		this.replacement = replacement;
		this.replaceAll = replaceAll;
	}

	public String apply(final String description) {
		Matcher matcher = pattern.matcher(description);
		if(replaceAll) {
			return matcher.replaceAll(replacement);
		} else {
			return matcher.replaceFirst(replacement);
		}
	}
	
	/**
	 * @return Returns the pattern.
	 */
	public String getRegex() {
		return pattern.pattern();
	}

	/**
	 * @return Returns the replaceAll.
	 */
	public boolean isReplaceAll() {
		return replaceAll;
	}

	/**
	 * @return Returns the replacement.
	 */
	public String getReplacement() {
		return replacement;
	}

	public String getName() {
		return name;
	}
	
    private void writeObject(ObjectOutputStream out) throws IOException {
    	out.writeInt(1);//serialization version number
    	out.writeObject(name);
    	out.writeObject(pattern.pattern());
    	out.writeBoolean(replaceAll);
    	out.writeObject(replacement);
    }
    
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException{
    	int serializationVersion = in.readInt();
    	switch(serializationVersion) {
    		case 1:
    			name = (String)in.readObject();
    			pattern = Pattern.compile((String)in.readObject());
    			replaceAll = in.readBoolean();
    			replacement = (String)in.readObject();
    			break;
    		default:
    			throw new IOException("Unsupported serialization version: "+serializationVersion);
    	}
    }

}
