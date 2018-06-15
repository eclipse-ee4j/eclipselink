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
// dmccann - March 25/2010 - 2.1 - Initial implementation
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.objectreference;

import java.util.List;

public class Root {
    public List<Employee> employees;
    public List<Address> addresses;

    /**
     * This method only checks the employee list for equality.  The Employee
     * object will validate that it has the correct Address set.
     */
    public boolean equals(Object obj) {
        if (obj == null) { return false; }

        Root rootObj;
        try {
            rootObj = (Root) obj;
        } catch (ClassCastException e) {
            return false;
        }

        if (employees == null) {
            return rootObj.employees == null;
        }

        if (rootObj.employees == null) {
            return false;
        }

        for (Employee emp : employees) {
            if (!empExistsInList(emp, rootObj.employees)) {
                return false;
            }
        }
        return true;
    }
    private boolean empExistsInList(Employee emp, List<Employee> empList) {
        for (Employee listEmp : empList) {
            if (listEmp.equals(emp)) {
                return true;
            }
        }
        return false;
    }
}
