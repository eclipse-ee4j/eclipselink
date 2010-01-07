/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Denise Smith  June 05, 2009 - Initial implementation
 ******************************************************************************/  
package org.eclipse.persistence.internal.jaxb.many;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.annotation.XmlTransient;

/**
 * This abstract class is used to support JAXBContext creation with an array of non-primitives class.
 * Classes which extend this class will be dynamically generated when a non-primitive array class is given to JAXBContext  
 */
@XmlTransient
public abstract class ObjectArrayValue<T> extends ManyValue<T> {
	
	@XmlTransient
	public Object[] convertCollectionToArray(Collection  theCollection) {		
		return theCollection.toArray((Object[])Array.newInstance(getComponentClass(), theCollection.size()));		
	}
		
	@XmlTransient
	public List convertArrayToList(Object[] objects) {
		List<Object> theList = new ArrayList<Object>();
		for(Object object : objects){ 
	        theList.add(object); 
	    } 
	    return theList;
	}
	
	@XmlTransient
	public abstract Class getComponentClass();
	
	@XmlTransient
	public boolean isArray(){
		return true;
	}		
}
