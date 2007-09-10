/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  

package org.eclipse.persistence.testing.models.jpa.advanced.compositepk;

public class DepartmentPK {
    public String name;
    public String role;
    public String location;

    public DepartmentPK(String name, String role, String location) {
        this.name = name;
        this.role = role;
        this.location = location;
    }

    public boolean equals(Object other) {
        if (other instanceof DepartmentPK) {
            final DepartmentPK otherDepartmentPK = (DepartmentPK) other;
            return (otherDepartmentPK.name.equals(name) && otherDepartmentPK.role.equals(role) && otherDepartmentPK.location.equals(location));
        }
        
        return false;
    }
}
