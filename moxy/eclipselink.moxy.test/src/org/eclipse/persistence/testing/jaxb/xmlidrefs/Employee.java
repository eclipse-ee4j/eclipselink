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
package org.eclipse.persistence.testing.jaxb.xmlidrefs;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlList;

public class Employee {

    @XmlElement(name="element-phone-id")
    @XmlIDREF
    @XmlList
    public List<PhoneNumber> elementPhoneNumbers;

    @XmlAttribute(name="attribute-phone-id")
    @XmlIDREF
    public List<PhoneNumber> attributePhoneNumbers;

    public boolean equals(Object object) {
        try {
            if(null == object) {
                return false;
            }
            Employee testEmployee = (Employee) object;
            if(!equals(elementPhoneNumbers, testEmployee.elementPhoneNumbers)) {
                return false;
            }
            if(!equals(attributePhoneNumbers, testEmployee.attributePhoneNumbers)) {
                return false;
            }
            return true;
        } catch(Exception e) {
            return false;
        }
    }

    private boolean equals(List controlList, List testList) {
        if(controlList == null) {
            return testList == null;
        }
        int controlListSize = elementPhoneNumbers.size();
        if(controlListSize != testList.size()) {
            return false;
        }
        for(int x=0; x<controlListSize; x++) {
            if(!controlList.get(x).equals(testList.get(x))) {
                return false;
            }
        }
        return true;
    }
}
