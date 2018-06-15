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
//     bdoughan - Jan 27/2009 - 1.1 - Initial implementation
package org.eclipse.persistence.testing.sdo.helper.jaxbhelper.oppositeproperty;

import java.util.Vector;

public class Child1 {

    private int id;
    private Child2 child2;
    private Vector child2Collection;

    public Child1() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Child2 getChild2() {
        return child2;
    }

    public void setChild2(Child2 child2) {
        this.child2 = child2;
    }

    public Vector getChild2Collection() {
        return child2Collection;
    }

    public void setChild2Collection(Vector child2Collection) {
        this.child2Collection = child2Collection;
    }

}
