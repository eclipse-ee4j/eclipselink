/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     02/02/2009-2.0 Chris delahunt
//       - 241765: JPA 2.0 Derived identities
package org.eclipse.persistence.testing.models.jpa.xml.advanced.derivedid;

import org.eclipse.persistence.testing.models.jpa.xml.advanced.compositepk.DepartmentPK;

public class DepartmentAdminRolePK {
    public DepartmentPK department;

    public Integer admin;

    public DepartmentAdminRolePK(String depName, String depRole, String depLocation, Integer employeeId) {
        this.department = new DepartmentPK(depName, depRole, depLocation);
        this.admin = employeeId;
    }
}
