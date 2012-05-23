/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * dmccann - September 14/2010 - 2.2 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.annotations.xmljoinnode;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

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
