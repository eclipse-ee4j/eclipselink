/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * Denise Smith - March 4/2010 - 2.0.2
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlschema.namespace;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="address")
public class Address {
    public String state;
    public String street;
    public String city;
    public String zip;
    
    public boolean equals(Object theObject){
    	if(!(theObject instanceof Address)){
    		return false;
    	}
    	if(!compareString(state, ((Address)theObject).state)){
    		return false;
    	}
    	if(!compareString(city, ((Address)theObject).city)){
    		return false;
    	}
    	if(!compareString(zip, ((Address)theObject).zip)){
    		return false;
    	}
    	if(!compareString(street, ((Address)theObject).street)){
    		return false;
    	}
    	return true;
    }
    
    private boolean compareString(String control, String test){
    	if(control == null){
    		if(test != null){
    			return false;
    		}
    	}else{
    		if(test == null){
    			return false;
    		}else{
    			if(!control.equals(test)){
    				return false;
    			}
    		}
    	}
    	return true;
    }
}
