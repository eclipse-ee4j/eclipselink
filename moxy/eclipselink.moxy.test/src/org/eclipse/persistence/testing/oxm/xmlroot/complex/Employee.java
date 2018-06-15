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
package org.eclipse.persistence.testing.oxm.xmlroot.complex;

import java.util.Vector;

public class Employee {

    private Object anyObject;

    private Vector anyCollection;

    public Employee() {
        super();
        anyCollection = new Vector();
    }

    public Object getAnyObject() {
        return anyObject;
    }

    public void setAnyObject(Object anyObject) {
        this.anyObject = anyObject;
    }

    public Vector getAnyCollection() {
        return anyCollection;
    }

    public void setAnyCollection(Vector anyCollection) {
        this.anyCollection = anyCollection;
    }

}
