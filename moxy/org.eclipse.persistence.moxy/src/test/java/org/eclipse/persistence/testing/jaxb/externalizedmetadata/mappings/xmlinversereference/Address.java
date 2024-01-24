/*
 * Copyright (c) 2011, 2024 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
// mmacivor - April 20/2010 - 2.1 - Initial implementation
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.xmlinversereference;

import java.util.List;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlID;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="address")
public class Address {
    @XmlID
    @XmlAttribute(name="aid")
    public String id;

    @XmlElement(name="street")
    public String street;

    @XmlElement(name="city")
    public String city;

    @XmlElement(name="country")
    public String country;

    @XmlElement(name="zip")
    public String zip;

    public List<Object> emp;

    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Address tgtAddress)) {
            return false;
        }
        return (tgtAddress.city.equals(city) &&
                tgtAddress.country.equals(country) &&
                tgtAddress.id.equals(id) &&
                tgtAddress.street.equals(street) &&
                tgtAddress.zip.equals(zip) && (tgtAddress.emp == emp || (emp != null && tgtAddress.emp != null)));
    }
}
