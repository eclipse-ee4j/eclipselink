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
//     Oracle - initial API and implementation from Oracle TopLink

package org.eclipse.persistence.testing.models.jpa.advanced.compositepk;

public class DepartmentPK {
    public String name;
    public String role;
    public String location;

    public DepartmentPK(){
    }

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

    public int hashCode(){
        return name.hashCode() + role.hashCode() + location.hashCode();
    }
}
