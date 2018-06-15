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
//     tware - test for bug 280436
package org.eclipse.persistence.testing.models.jpa.advanced.compositepk;

public class OfficePK {

    protected int id;
    protected String location;

    public OfficePK(int id, String location) {
        this.id = id;
        this.location = location;
    }

    public boolean equals(Object other) {
        if (other instanceof OfficePK) {
            final OfficePK otherOfficePK = (OfficePK) other;
            return (otherOfficePK.id == id && otherOfficePK.location.equals(location));
        }

        return false;
    }

}
