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
package org.eclipse.persistence.testing.tests.validation;

import java.util.Map;

import org.eclipse.persistence.indirection.ValueHolder;


public class Employee {
    public int id;
    public String name;
    public String gender;
    public ValueHolder address;
    public Map phoneNumbers;

    public Employee() {
        super();
    }

    public ValueHolder getAddress() {
        return address;
    }

    public int getID() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setAddress(ValueHolder address) {
        this.address = address;
    }

    public void setID(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }
}
