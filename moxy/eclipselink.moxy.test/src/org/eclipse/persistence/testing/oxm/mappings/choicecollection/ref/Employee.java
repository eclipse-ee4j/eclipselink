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
// Oracle = 2.2 - Initial implementation
package org.eclipse.persistence.testing.oxm.mappings.choicecollection.ref;

import java.util.List;

public class Employee {

    public String name;

    public List contact;

    public boolean equals(Object obj) {
        Employee emp = (Employee)obj;

        return name.equals(emp.name)&& contact.equals(emp.contact);
    }
}
