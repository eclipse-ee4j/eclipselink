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
 * dmccann - Sept.22/2010 - 2.2 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmltransient;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement(name="contact-info")
public class ContactInfo {
    public List<Address> addresses;
    public Address primaryAddress;
    @XmlTransient
    public String phoneNumber;   
    
    public ContactInfo(){
    	addresses = new ArrayList<Address>();
    	primaryAddress = new Address();
    }
    
    public boolean equals(Object obj) {
        if (obj == null) { return false; }
        if (!(obj instanceof ContactInfo)){
        	return false;
        }
        if(phoneNumber == null && ((ContactInfo)obj).phoneNumber != null){
        	return false;
        }
        if(!phoneNumber.equals(((ContactInfo)obj).phoneNumber)){
        	return false;
        }
        
        if(primaryAddress == null && ((ContactInfo)obj).primaryAddress != null){
        	return false;
        }
        if(!primaryAddress.equals(((ContactInfo)obj).primaryAddress)){
        	return false;
        }
        
        List testList = ((ContactInfo)obj).addresses;
        
        if(addresses == null && testList != null){
        	return false;
        }
        
        if(addresses.size() != testList.size()){
        	return false;
        }
        if(!addresses.containsAll(testList)){
        	return false;
        }
        return true;
    }
}
