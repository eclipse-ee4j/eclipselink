/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     rbarkhouse - 2009-10-06 13:06:00 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.oxm.mappings.choicecollection.reuse;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

public class Employee {

    public String name;
    public Collection<Object> choice = new LinkedList<Object>();
    public String phone;

    public boolean equals(Object obj) {
        Employee emp = (Employee) obj;
        boolean equal = emp.name.equals(name) && emp.phone.equals(phone);
        if (choice.size() != emp.choice.size()) {
            return false;
        }
        Iterator<Object> iter1 = choice.iterator();
        Iterator<Object> iter2 = emp.choice.iterator();
        while (iter1.hasNext()) {
            equal = iter1.next().equals(iter2.next()) && equal;
        }
        return equal;
    }

}