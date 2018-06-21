/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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

import java.util.Collection;
import java.util.Iterator;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.persistence.oxm.annotations.XmlInverseReference;

@XmlRootElement(name="employee")
public class Employee {
    @XmlID
    @XmlAttribute(name="id")
    public String id;

    @XmlElement(name="name")
    public String name;

    @XmlIDREF
    @XmlAttribute(name="address-id")
    public Address address;

    @XmlIDREF
    @XmlElement(name="phone-id")
    public Collection<PhoneNumber> phones;

    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Employee)) {
            return false;
        }
        Employee emp = (Employee) obj;
        if (this.address == null) {
            return emp.address == null;
        }
        if (emp.address == null) {
            return false;
        }
        boolean equal = true;
        equal = equal && address.equals(emp.address);

        Iterator<PhoneNumber> phones1 = phones.iterator();
        Iterator<PhoneNumber> phones2 = emp.phones.iterator();

        while(phones1.hasNext() && phones2.hasNext()) {
            equal = phones1.next().equals(phones2.next()) && equal;
        }

        return equal;
    }
}
