/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
