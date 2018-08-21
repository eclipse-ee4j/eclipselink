/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.jaxb.events;

import javax.xml.bind.annotation.*;
import javax.xml.bind.*;
import java.util.ArrayList;
@XmlRootElement(name="employee")
public class Employee {

    public Employee() {
        triggeredEvents = new ArrayList();
        phoneNumbers = new ArrayList();
    }
    @XmlTransient
    ArrayList triggeredEvents;

    @XmlElement(name="address")
    public Address address;

    @XmlElementWrapper(name="phone-numbers")
    @XmlElement(name="phone")
    public java.util.ArrayList<PhoneNumber> phoneNumbers;

    public void beforeMarshal(Marshaller marshaller) {
        this.triggeredEvents.add(JAXBMarshalListenerImpl.EMPLOYEE_BEFORE_MARSHAL);
    }

    public void afterMarshal(Marshaller marshaller) {
        this.triggeredEvents.add(JAXBMarshalListenerImpl.EMPLOYEE_AFTER_MARSHAL);
    }

    public void beforeUnmarshal(Unmarshaller unmarshaller, Object parent) {
        this.triggeredEvents.add(JAXBUnmarshalListenerImpl.EMPLOYEE_BEFORE_UNMARSHAL);
    }

    public void afterUnmarshal(Unmarshaller unmarshaller, Object parent) {
        this.triggeredEvents.add(JAXBUnmarshalListenerImpl.EMPLOYEE_AFTER_UNMARSHAL);
    }
    public boolean equals(Object obj) {
        boolean equal = false;
        if(!(obj instanceof Employee)) {
            return false;
        }
        Employee objEmp = (Employee)obj;
        equal = (objEmp.address == address) || (objEmp.address != null && address != null && objEmp.address.equals(address));

        equal = equal && ((objEmp.phoneNumbers == phoneNumbers) || (objEmp.phoneNumbers != null && phoneNumbers != null && objEmp.phoneNumbers.equals(phoneNumbers)));

        return equal;
    }

}
