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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlTransient;

@javax.xml.bind.annotation.XmlAccessorType(XmlAccessType.FIELD)
//@javax.xml.bind.annotation.XmlRootElement
public class Foo {
    //@javax.xml.bind.annotation.XmlElementRef(type=Bar.class)
    public Bar item;

    @XmlTransient
    public boolean accessedViaMethod = false;

    public Bar getBarItem() {
        accessedViaMethod = true;
        return item;
    }
    
    public void setBarItem(Bar item) {
        this.item = item;
    }
    
    public boolean equals(Object obj){
    	if(obj instanceof Foo){
    		if(item == null){
    			if(((Foo)obj).item != null){
    				return false;
    			}
    			return true;
    		}else{
         		return item.equals(((Foo)obj).item);
    		}
    	}
    	return false;
    }

}
