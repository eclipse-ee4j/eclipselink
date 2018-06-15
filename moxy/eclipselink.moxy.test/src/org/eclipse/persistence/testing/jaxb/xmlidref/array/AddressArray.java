/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.jaxb.xmlidref.array;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.persistence.oxm.annotations.XmlInverseReference;

@XmlRootElement(name="address")
@XmlType(name="address")
public class AddressArray {
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

    @XmlInverseReference(mappedBy = "address")
    public List<EmployeeArray> emp;

    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof AddressArray)) {
            return false;
        }
        AddressArray tgtAddress = (AddressArray) obj;
        return (tgtAddress.city.equals(city) &&
                tgtAddress.country.equals(country) &&
                tgtAddress.id.equals(id) &&
                tgtAddress.street.equals(street) &&
                tgtAddress.zip.equals(zip) && (tgtAddress.emp == emp || (emp != null && tgtAddress.emp != null)));
    }
}
