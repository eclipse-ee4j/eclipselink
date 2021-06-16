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
//     02/02/2009-2.0 Chris delahunt
//       - 241765: JPA 2.0 Derived identities
package org.eclipse.persistence.testing.models.jpa.xml.advanced.derivedid;

import org.eclipse.persistence.testing.models.jpa.xml.advanced.compositepk.Department;

/**
 * @author cdelahun
 *
 */

public class DepartmentAdminRole {
    Administrator admin;
    Department department;

    public DepartmentAdminRole () {
    }

    public Administrator getAdmin(){
        return admin;
    }

    public Department getDepartment(){
        return department;
    }

    public void setAdmin(Administrator admin){
        this.admin = admin;
    }

    public void setDepartment(Department department){
        this.department = department;
    }

}
