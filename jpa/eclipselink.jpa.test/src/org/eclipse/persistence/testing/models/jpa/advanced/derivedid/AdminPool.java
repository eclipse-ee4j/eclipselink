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
//     tware - testing for bug 294811 - https://bugs.eclipse.org/bugs/show_bug.cgi?id=294811
package org.eclipse.persistence.testing.models.jpa.advanced.derivedid;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.EAGER;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.MapKey;
import javax.persistence.Table;

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
