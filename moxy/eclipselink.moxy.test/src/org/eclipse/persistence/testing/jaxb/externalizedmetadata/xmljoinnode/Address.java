/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
// dmccann - August 26/2010 - 2.2 - Initial implementation
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
