/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
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
// dmccann - September 14/2010 - 2.2 - Initial implementation
package org.eclipse.persistence.testing.jaxb.annotations.xmljoinnode;

import java.util.List;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Company {
    @XmlElement(name="employee-data")
    public List<Employee> employees;
    @XmlElement(name="business-address")
    public List<Address> buildingAddresses;

    public Company() {}
    public Company(List<Employee> employees, List<Address> buildingAddresses) {
        this.employees = employees;
        this.buildingAddresses = buildingAddresses;
    }

    /**
     * This method only verifies that the Employees are equal. The list of Addresses
     * instances is simply checked for size equality. This is because the purpose
     * of this suite is to make sure that the Employee(s) have their Address(es)
     * populated correctly through XmlJoinNode.
     */
    public boolean equals(Object obj) {
        Company co;
        try {
            co = (Company) obj;
        } catch (ClassCastException cce) {
            return false;
        }
        if (employees == null) {
            if (co.employees != null) {
                return false;
            }
        } else {
            if (co.employees == null) {
                return false;
            }
            if (employees.size() != co.employees.size()) {
                return false;
            }
            for (Employee emp1 : employees) {
                boolean found = false;
                for (Employee emp2 : co.employees) {
                    if (emp1.equals(emp2)) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    return false;
                }
            }
        }
        if (buildingAddresses == null) {
            if (co.buildingAddresses != null) {
                return false;
            }
        } else {
            if (co.buildingAddresses == null) {
                return false;
            }
            if (buildingAddresses.size() != co.buildingAddresses.size()) {
                return false;
            }
        }
        return true;
    }
}
