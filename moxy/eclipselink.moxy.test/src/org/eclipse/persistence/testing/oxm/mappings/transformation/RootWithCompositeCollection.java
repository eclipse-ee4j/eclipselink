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
package org.eclipse.persistence.testing.oxm.mappings.transformation;

import java.util.Vector;

public class RootWithCompositeCollection {
    public Vector employees;

    public RootWithCompositeCollection() {
        super();
        employees = new Vector();
    }

    public boolean equals(Object object) {
        try {
            RootWithCompositeCollection rootWithCompositeCollection = (RootWithCompositeCollection)object;
            if (this.employees == rootWithCompositeCollection.employees) {
                return true;
            }
            if ((null == this.employees) || (null == rootWithCompositeCollection.employees)) {
                return false;
            }
            if (this.employees.size() != rootWithCompositeCollection.employees.size()) {
                return false;
            }
            for (int x = 0; x < this.employees.size(); x++) {
                if (!this.employees.get(x).equals(rootWithCompositeCollection.employees.get(x))) {
                    return false;
                }
            }
            return true;
        } catch (ClassCastException e) {
            return false;
        }
    }

    public String toString() {
        String string = "RootWithCompositeCollection(";
        for (int x = 0; x < employees.size(); x++) {
            if (x > 0) {
                string += ", ";
            }
            string += employees.get(x).toString();
        }
        string += ")";
        return string;
    }
}
