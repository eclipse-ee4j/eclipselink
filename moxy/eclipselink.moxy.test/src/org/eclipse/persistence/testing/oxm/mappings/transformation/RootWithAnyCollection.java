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

public class RootWithAnyCollection {
    public Vector objects;

    public RootWithAnyCollection() {
        super();
        objects = new Vector();
    }

    public boolean equals(Object object) {
        try {
            RootWithAnyCollection rootWithAnyCollection = (RootWithAnyCollection)object;
            if (this.objects == rootWithAnyCollection.objects) {
                return true;
            }
            if ((null == this.objects) || (null == rootWithAnyCollection.objects)) {
                return false;
            }
            if (this.objects.size() != rootWithAnyCollection.objects.size()) {
                return false;
            }
            for (int x = 0; x < this.objects.size(); x++) {
                if (!this.objects.get(x).equals(rootWithAnyCollection.objects.get(x))) {
                    return false;
                }
            }
            return true;
        } catch (ClassCastException e) {
            return false;
        }
    }

    public String toString() {
        String string = "RootWithAnyCollection(";
        for (int x = 0; x < objects.size(); x++) {
            if (x > 0) {
                string += ", ";
            }
            string += objects.get(x).toString();
        }
        string += ")";
        return string;
    }
}
