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
//     03/23/2011-2.3 Guy Pelletier
//       - 337323: Multi-tenant with shared schema support (part 1)
//     04/21/2011-2.3 Guy Pelletier
//       - 337323: Multi-tenant with shared schema support (part 5)
package org.eclipse.persistence.testing.models.jpa.xml.advanced.multitenant;

import java.util.Collection;
import java.util.Vector;

public class Contract {
    private int id;
    private Integer version;
    private String description;
    private String tenantId;
    private Collection<Soldier> soldiers;

    public Contract() {
        this.soldiers = new Vector<Soldier>();
    }

    public void addSoldier(Soldier soldier) {
        soldiers.add(soldier);
        soldier.addContract(this);
    }

    public String getDescription() {
        return description;
    }

    public int getId() {
        return id;
    }

    public Collection<Soldier> getSoldiers() {
        return soldiers;
    }
    public String getTenantId() {
        return tenantId;
    }
    public Integer getVersion() {
        return version;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setSoldiers(Collection<Soldier> soldiers) {
        this.soldiers = soldiers;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }
    public void setVersion(Integer version) {
        this.version = version;
    }
}
