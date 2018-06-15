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
