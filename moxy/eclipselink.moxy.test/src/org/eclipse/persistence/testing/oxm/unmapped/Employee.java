/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.oxm.unmapped;

import java.util.Vector;

public class Employee {
    private String firstName;
    private String lastName;
    private Object any;
    private Vector anyCollection;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Object getAny() {
        return any;
    }

    public void setAny(Object any) {
        this.any = any;
    }

    public Vector getAnyCollection() {
        return anyCollection;
    }

    public void setAnyCollection(Vector anyCollection) {
        this.anyCollection = anyCollection;
    }

    public boolean equals(Object object) {
        try {
            Employee testEmployee = (Employee)object;
            if (null == firstName) {
                if (null != testEmployee.getFirstName()) {
                    return false;
                }
            } else {
                if (!firstName.equals(testEmployee.getFirstName())) {
                    return false;
                }
            }
            if (null == lastName) {
                if (null != testEmployee.getLastName()) {
                    return false;
                }
            } else {
                if (!lastName.equals(testEmployee.getLastName())) {
                    return false;
                }
            }
            if (null == any) {
                if (null != testEmployee.getAny()) {
                    return false;
                }
            } else {
                if (!any.equals(testEmployee.getAny())) {
                    return false;
                }
            }
            return true;
        } catch (ClassCastException e) {
            return false;
        }
    }

    public String toString() {
        String string = "Employee(firstName=";
        string += firstName;
        string += ",lastName=";
        string += lastName;
        string += ", any=";
        string += any;
        string += ", anyCollection(size)=";
        if (null == anyCollection) {
            string += null;
        } else {
            string += anyCollection.size();
        }
        string += ")";
        return string;
    }
}
