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
// dmccann - September 14/2010 - 2.2 - Initial implementation
// Martin Vojtek - November 14/2014 - 2.6 - Added XmlIDExtension
package org.eclipse.persistence.testing.jaxb.annotations.xmljoinnode;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.persistence.oxm.annotations.XmlIDExtension;
import org.eclipse.persistence.oxm.annotations.XmlKey;
import org.eclipse.persistence.oxm.annotations.XmlPath;

public class Address {
    @XmlID
    @XmlAttribute
    @XmlIDExtension
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
