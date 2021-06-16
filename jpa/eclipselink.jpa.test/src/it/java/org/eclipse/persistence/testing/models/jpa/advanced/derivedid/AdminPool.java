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
//     tware - testing for bug 294811 - https://bugs.eclipse.org/bugs/show_bug.cgi?id=294811
package org.eclipse.persistence.testing.models.jpa.advanced.derivedid;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.EAGER;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.MapKey;
import jakarta.persistence.Table;

import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name="CMP3_ADMIN_POOL")
public class AdminPool {

    @Id
    private int id;

    private String description;

    @OneToMany(mappedBy="pool", cascade=ALL, fetch=EAGER)
    @MapKey
    private Map<DepartmentAdminRolePK, DepartmentAdminRole> admins = new HashMap<DepartmentAdminRolePK, DepartmentAdminRole>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Map<DepartmentAdminRolePK, DepartmentAdminRole> getAdmins() {
        return admins;
    }

    public void setAdmins(Map<DepartmentAdminRolePK, DepartmentAdminRole> admins) {
        this.admins = admins;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void addAdmin(DepartmentAdminRole admin){
        admins.put(admin.buildDepartmentAdminRolePK(), admin);
    }

    public void removeAdmin(DepartmentAdminRolePK key){
        admins.remove(key);
    }
}
