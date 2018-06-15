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
package org.eclipse.persistence.testing.oxm.mappings.choice;

public class Employee {
    public String name;
    public Object choice;
    public String phone;

    public boolean equals(Object obj) {
        Employee emp = (Employee)obj;
        return emp.name.equals(name) && emp.choice.equals(choice) && emp.phone.equals(phone);
    }
}

