/*******************************************************************************
 * Copyright (c) 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     James Sutherland - initial API and implementation
 ******************************************************************************/

package org.eclipse.persistence.testing.models.jpa.partitioned;

public class EmployeePK {
    public Integer id;
    public String location;

    public EmployeePK() {
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
