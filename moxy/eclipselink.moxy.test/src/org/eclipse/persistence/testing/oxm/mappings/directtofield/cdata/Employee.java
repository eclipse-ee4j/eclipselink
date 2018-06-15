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
package org.eclipse.persistence.testing.oxm.mappings.directtofield.cdata;

/**
 *  @version $Header: Employee.java 01-may-2007.14:08:13 mmacivor Exp $
 *  @author  mmacivor
 *  @since   release specific (what release of product did this appear in)
 */

public class Employee {
    public String firstName;
    public String lastName;

    public String data;
    public String nestedCData;

    public boolean equals(Object obj) {
        if(!(obj instanceof Employee)) {
            return false;
        }
        Employee emp = (Employee) obj;
        return emp.firstName.equals(firstName) && emp.lastName.equals(lastName)
                && emp.data.trim().equals(data.trim())
                && emp.nestedCData.trim().equals(nestedCData.trim());
    }

    public String toString() {
        return "Employee: " + firstName + " - " + lastName + " - " + data + " - " + nestedCData;
    }

}
