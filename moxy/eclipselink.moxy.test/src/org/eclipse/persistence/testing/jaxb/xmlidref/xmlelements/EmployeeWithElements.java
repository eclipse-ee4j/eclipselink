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
// Oracle = 2.2 - Initial implementation
package org.eclipse.persistence.testing.jaxb.xmlidref.xmlelements;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="employee")
public class EmployeeWithElements {
    @XmlID
    @XmlAttribute(name="id")
    public String id;

    @XmlElement(name="name")
    public String name;

    @XmlIDREF
    @XmlElements({@XmlElement(name="address-id", type=Address.class), @XmlElement(name="phone-id", type=PhoneNumber.class)})
    public List<Object> addressOrPhone;

    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof EmployeeWithElements)) {
            return false;
        }
        EmployeeWithElements emp = (EmployeeWithElements) obj;
        if(addressOrPhone.size() != emp.addressOrPhone.size()) {
            return false;
        }

        boolean equal = true;

        Iterator<Object> choice1 = this.addressOrPhone.iterator();
        Iterator<Object> choice2 = emp.addressOrPhone.iterator();


        while(choice1.hasNext() && choice2.hasNext()) {
            equal = choice1.next().equals(choice2.next()) && equal;
        }

        return equal;
    }
}
