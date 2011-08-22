/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * dmccann - December 03/2009 - 2.0 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlelementref;

//@javax.xml.bind.annotation.XmlRootElement
public class Foos {
    //@javax.xml.bind.annotation.XmlElementWrapper(name="items")
    //@javax.xml.bind.annotation.XmlElementRef(type=Bar.class)
    public java.util.List<Bar> items;
    
    public boolean equals(Object obj){
    	if(obj instanceof Foos){
    		if(items.size() != ((Foos)obj).items.size()){
    			return false;
    		}
    		for(int i=0; i<items.size(); i++){
    			Object next = items.get(i);
    			Object nextCompare = ((Foos)obj).items.get(i);
    			if(!(next.equals(nextCompare))){
    				return false;
    			}
    		}
    		return true;
    	}
    	return false;
    }
}
