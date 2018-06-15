/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Blaise Doughan - 2.4.2 - initial implementation
package org.eclipse.persistence.testing.jaxb.json.wrapper;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder={"strings", "employees", "phoneNumbers"})
public class Company {

    @XmlElementWrapper(namespace="urn:BAR")
    @XmlElement(name="string")
    public List<String> strings = new ArrayList<String>();

    @XmlElementWrapper(namespace="urn:BAR")
    @XmlElement(name="employee")
    public List<Employee> employees = new ArrayList<Employee>();

    @XmlElementWrapper(name="phone-numbers", namespace="urn:BAR")
    @XmlElement(name="phone-number")
    public List<PhoneNumber> phoneNumbers = new ArrayList<PhoneNumber>();

    @Override
    public boolean equals(Object obj) {
        if(null == obj || obj.getClass() != this.getClass()) {
            return false;
        }

        Company test = (Company) obj;
        if(!equals(strings, test.strings)) {
            return false;
        }
        if(!equals(employees, test.employees)) {
            return false;
        }
        if(!equals(phoneNumbers, test.phoneNumbers)) {
            return false;
        }
        return true;
    }

    private boolean equals(List<?> control, List<?> test) {
        if(control == test) {
            return true;
        }
        if(null == control || null == test) {
            return false;
        }
        if(control.size() != test.size()) {
            return false;
        }
        for(int x=0; x<control.size(); x++) {
            if(!control.get(x).equals(test.get(x))) {
                return false;
            }
        }
        return true;
    }

}
