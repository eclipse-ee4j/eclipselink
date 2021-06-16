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
package org.eclipse.persistence.testing.oxm.mappings.choicecollection;

import java.util.Collection;
import java.util.Iterator;
public class Employee {
    public String name;
    public Collection<Object> choice;
    public String phone;

    public boolean equals(Object obj) {
        Employee emp = (Employee)obj;
        boolean equal = emp.name.equals(name) && emp.phone.equals(phone);
        if(choice.size() != emp.choice.size()) {
            return false;
        }
        Iterator<Object> iter1 = choice.iterator();
        Iterator<Object> iter2 = emp.choice.iterator();
        while(iter1.hasNext()) {
            equal = iter1.next().equals(iter2.next()) && equal;
        }
        return equal;
    }
}

