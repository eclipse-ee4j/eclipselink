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
//     bdoughan - Mar 27/2009 - 2.0 - Initial implementation
package org.eclipse.persistence.testing.sdo.helper.jaxbhelper.changesummary;

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
