/*******************************************************************************
 * Copyright (c) 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * dmccann - September 14/2010 - 2.2 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.annotations.xmljoinnode;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.persistence.oxm.annotations.XmlKey;
import org.eclipse.persistence.oxm.annotations.XmlPath;

public class Address {
    @XmlID
    @XmlPath("@id")
    public int id;
    
    public String street;
    public String suite;
    
    @XmlKey
    @XmlPath("city/text()")
    public String cityName;
    
    public String postal;
    
    public Address() {}
    
    public Address(int id, String street, String suite, String cityName, String postal) {
        this.id = id;
        this.street = street;
        this.suite = suite;
        this.cityName = cityName;
        this.postal = postal;
    }
    
    public boolean equals(Object obj) {
        Address add;
        try {
            add = (Address) obj;
        } catch (ClassCastException cce) {
            return false;
        }
        return id == add.id &&
               street.equals(add.street) &&
               suite.equals(add.suite) &&
               cityName.equals(add.cityName) &&
               postal.equals(add.postal);
    }
}
