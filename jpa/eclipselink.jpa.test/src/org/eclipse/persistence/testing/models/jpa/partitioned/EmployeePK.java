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
//     James Sutherland - initial API and implementation

package org.eclipse.persistence.testing.models.jpa.partitioned;

public class EmployeePK {
    public Integer id;
    public String location;

    public EmployeePK() {
    }
    public EmployeePK(int id, String location) {
        this.id = id;
        this.location = location;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * equals: Answer true if the ids are equal
     */
    public boolean equals(Object pk) {
        if (pk.getClass() != EmployeePK.class) {
            return false;
        }
        return (getId().equals(((EmployeePK) pk).getId()));
    }
}
