/*
 * Copyright (c) 2011, 2023 Oracle and/or its affiliates. All rights reserved.
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
// dmccann - December 01/2010 - 2.3 - Initial implementation
package org.eclipse.persistence.testing.oxm.mappings.choice.ref;

public class Employee {

    public String name;

    public Object contact;

    public boolean equals(Object obj) {
        Employee emp = (Employee)obj;

        return name.equals(emp.name)&& contact.equals(emp.contact);
    }
}
