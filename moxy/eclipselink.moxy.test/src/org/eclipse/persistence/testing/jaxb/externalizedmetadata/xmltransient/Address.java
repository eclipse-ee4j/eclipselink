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
 * dmccann - June 17/2009 - 2.0 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmltransient;

import javax.xml.bind.annotation.XmlRootElement;
    
@XmlRootElement(name = "address")
public class Address {
    public String state;
    public String street;
    public String city;
    public String zip;
    
    public boolean equals(Object obj) {
        if (obj == null) { return false; }
        
        Address addObj;
        try {
            addObj = (Address) obj;
        } catch (ClassCastException e) {
            return false;
        }

        if(city == null){
        	if(addObj.city != null){
        		return false;
        	}        	
        }else if(!city.equals(addObj.city)){
        	return false;
        }
        
        if(street == null){
        	if(addObj.street != null){
        		return false;
        	}        	
        }else if(!street.equals(addObj.street)){
        	return false;
        }
        
        if(state == null){
        	if(addObj.state != null){
        		return false;
        	}        	
        }else if(!state.equals(addObj.state)){
        	return false;
        }
        
        if(zip == null){
        	if(addObj.zip != null){
        		return false;
        	}        	
        }else if(!zip.equals(addObj.zip)){
        	return false;
        }
        
        return true;
    }
}
