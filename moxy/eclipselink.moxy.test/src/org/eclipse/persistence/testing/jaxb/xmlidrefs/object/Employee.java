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
//     bdoughan - April 14/2010 - 2.1 - Initial implementation
package org.eclipse.persistence.testing.jaxb.xmlidrefs.object;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlList;

public class Employee {

    @XmlIDREF
    @XmlList
    @XmlElement(name="phone-id")
    public List<Object> phones;

    public boolean equals(Object object) {
        try {
            if(null == object) {
                return false;
            }
            Employee testEmployee = (Employee) object;
            List testEmployeePhones = testEmployee.phones;
            if(phones == null) {
                return testEmployeePhones == null;
            }
            int phonesSize = phones.size();
            if(phonesSize != testEmployeePhones.size()) {
                return false;
            }
            for(int x=0; x<phonesSize; x++) {
                if(!phones.get(x).equals(testEmployeePhones.get(x))) {
                    return false;
                }
            }
            return true;
        } catch(Exception e) {
            return false;
        }
    }

}
