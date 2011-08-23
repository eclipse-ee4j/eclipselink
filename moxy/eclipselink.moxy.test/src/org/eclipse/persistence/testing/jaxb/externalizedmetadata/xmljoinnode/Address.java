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
 * dmccann - August 26/2010 - 2.2 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmljoinnode;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlID;

import org.eclipse.persistence.oxm.annotations.XmlKey;
import org.eclipse.persistence.oxm.annotations.XmlPath;

public class Address {
    @XmlID
    @XmlPath("@id")
    @XmlAttribute
    public String id;
    public String street;
    public String suite;
    
    @XmlKey
    @XmlPath("city/text()")
    public String cityName;
    public String postal;
    
    public Address() {}
    
    public Address(String id, String street, String suite, String cityName, String postal) {
        this.id = id;
        this.street = street;
        this.suite = suite;
        this.cityName = cityName;
        this.postal = postal;
    }
    
    public boolean equals(Object obj){
       if(obj instanceof Address){
    	   Address addrObj = (Address)obj;
    	   return addrObj.id.equals(addrObj.id)
    	       && addrObj.street.equals(addrObj.street)
    	       && addrObj.suite.equals(addrObj.suite)
    	       && addrObj.cityName.equals(addrObj.cityName)
    	       && addrObj.postal.equals(addrObj.postal);
       }
       return false;
    }
    
    
}
