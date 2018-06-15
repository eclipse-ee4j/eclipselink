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
package org.eclipse.persistence.testing.jaxb.xmlidref;

import java.util.Arrays;
import java.util.Collection;

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

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Employee)) {
            return false;
        }
        Employee emp = (Employee) obj;
        if (this.address == null && emp.address != null) {
            return false;
        }
        if (emp.address == null) {
            return false;
        }
        if (!address.equals(emp.address)) {
            return false;
        }

        PhoneNumber[] phoneNumbers1 = new PhoneNumber[phones.size()];
        phones.toArray(phoneNumbers1);
        PhoneNumber[] phoneNumbers2 = new PhoneNumber[emp.phones.size()];
        emp.phones.toArray(phoneNumbers2);

        return Arrays.equals(phoneNumbers1, phoneNumbers2);
    }

    @Override
    public int hashCode() {
        int result = address != null ? address.hashCode() : 0;
        if (phones != null) {
            result = 31 * result + Arrays.hashCode(phones.toArray());
        }
        return result;
    }

    public boolean equalsWithoutCyclicDependency(Object obj) {
        if (obj == null || !(obj instanceof Employee)) {
            return false;
        }
        Employee emp = (Employee) obj;
        if (this.address == null && emp.address != null) {
            return false;
        }
        if (emp.address == null) {
            return false;
        }
        if (!address.equals(emp.address)) {
            return false;
        }
        return phones.size() == emp.phones.size();
    }

    public int hashCodeWithoutCyclicDependency() {
        int result = address != null ? address.hashCode() : 0;
        if (phones != null) {
            result = 31 * result + phones.size();
        }
        return result;
    }
}
