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
package org.eclipse.persistence.testing.oxm.xmlbinder.keybasedmappingtests;

public class Employee {
    public String id;
    public String name;
    public Address address;

    public boolean equals(Object obj) {
        if(!(obj instanceof Employee)) {
            return false;
        }

        Employee emp = (Employee)obj;
        if(id == emp.id || (emp.id != null && id != null && id.equals(emp.id))) {
            if(name == emp.name || (emp.name != null && name != null && name.equals(emp.name))) {
                if(address == emp.address || (address != null && emp.address != null && emp.address.equals(address))) {
                    return true;
                }
            }
        }
        return false;
    }
}
