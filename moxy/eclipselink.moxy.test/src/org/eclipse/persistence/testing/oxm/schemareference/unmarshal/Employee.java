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
package org.eclipse.persistence.testing.oxm.schemareference.unmarshal;

public class Employee {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean equals(Object object) {
        try {
            Employee employee = (Employee)object;

            if (this.getName() == employee.getName()) {
                return true;
            }

            if (this.getName() == null) {
                return false;
            }

            return this.getName().equals(employee.getName());
        } catch (ClassCastException e) {
            return false;
        }
    }

    public String toString() {
        String string = "Employee(name=";
        string += name;
        string += ")";
        return string;
    }
}
