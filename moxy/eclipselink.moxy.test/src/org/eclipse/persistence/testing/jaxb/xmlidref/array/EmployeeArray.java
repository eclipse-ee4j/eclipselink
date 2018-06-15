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

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


@XmlRootElement(name="employee")
@XmlType(name = "employee")
public class EmployeeArray {
    @XmlID
    @XmlAttribute(name="id")
    public String id;

    @XmlElement(name="name")
    public String name;

    @XmlIDREF
    @XmlAttribute(name="address-id")
    public AddressArray address;

    @XmlIDREF
    @XmlElement(name="phone-id")
    public PhoneNumberArray[] phones;
    //public Collection<PhoneNumberArray> phones;

    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof EmployeeArray)) {
            return false;
        }
        EmployeeArray emp = (EmployeeArray) obj;
        if (this.address == null) {
            return emp.address == null;
        }
        if (emp.address == null) {
            return false;
        }
        boolean equal = true;
        equal = equal && address.equals(emp.address);

        PhoneNumberArray[] phones1 = phones;
        PhoneNumberArray[] phones2 = emp.phones;
        if(phones1.length != phones2.length){
            return false;
        }
        for(int i=0;i< phones1.length; i++){
            if(!phones1[i].equals(phones2[i])){
                return false;
            }
        }

        return equal;
    }
}
