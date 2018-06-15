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
package org.eclipse.persistence.testing.models.transparentindirection;

import java.io.*;

public class Dog implements Serializable {
    public int id;
    public String name;
    public SalesRepContainer owner;

    public Dog() {
        this.initialize(null);
    }

    public Dog(String aNewName) {
        this.initialize(aNewName);
    }

    public String getName() {
        return this.name;
    }

    public SalesRep getOwner() {
        return owner.getSalesRep();
    }

    public void initialize(String aName) {
        name = aName;
        owner = new SalesRepContainer();
    }

    public void setName(String newName) {
        name = newName;
    }

    public void setOwner(SalesRep rep) {
        owner.setSalesRep(rep);
    }

    public String toString() {
        return "Dog(" + id + ": " + name + ")" + System.identityHashCode(this);
    }
}
