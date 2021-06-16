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

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlID;
import jakarta.xml.bind.annotation.XmlIDREF;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;


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
