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
