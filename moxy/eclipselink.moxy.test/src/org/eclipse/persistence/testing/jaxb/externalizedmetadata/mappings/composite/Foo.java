/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
// dmccann - March 19/2010 - 2.1 - Initial implementation
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.composite;

public class Foo {
    public String foodata = "";

    public String getFoodata() {
        return foodata;
    }

    public boolean equals(Object obj) {
        if (obj == null) { return false; }

        Foo fObj;
        try {
            fObj = (Foo) obj;
        } catch (ClassCastException e) {
            return false;
        }

        return foodata.equals(fObj.foodata);
    }
}
