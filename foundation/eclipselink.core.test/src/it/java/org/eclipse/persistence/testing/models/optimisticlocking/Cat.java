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
//     tware - added cascaded locking testing
package org.eclipse.persistence.testing.models.optimisticlocking;

import java.util.List;

import org.eclipse.persistence.indirection.ValueHolder;
import org.eclipse.persistence.indirection.ValueHolderInterface;

public class Cat extends Animal {

    private String name;
    private ValueHolderInterface toys;

    public Cat(){
        toys = new ValueHolder();
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public ValueHolderInterface getToysVH() {
        return toys;
    }
    public void setToysVH(ValueHolderInterface toys) {
        this.toys = toys;
    }

    public List getToys() {
        return (List)toys.getValue();
    }
    public void setToys(List toys) {
        this.toys.setValue(toys);
    }
}
