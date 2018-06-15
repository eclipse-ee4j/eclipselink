/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
// dmccann - March 25/2010 - 2.1 - Initial implementation
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.objectreference;

public class Address {
    public String id = "";

    public boolean equals(Object obj) {
        if (obj == null) { return false; }

        Address theObj;
        try {
            theObj = (Address) obj;
        } catch (ClassCastException e) {
            return false;
        }

        return id.equals(theObj.id);
    }
}
