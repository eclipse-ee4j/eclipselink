/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.jaxb.xmlidref.array;

import java.util.List;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlID;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

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
