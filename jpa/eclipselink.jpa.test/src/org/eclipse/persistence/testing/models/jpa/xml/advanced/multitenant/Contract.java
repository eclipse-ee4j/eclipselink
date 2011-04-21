/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     03/23/2011-2.3 Guy Pelletier 
 *       - 337323: Multi-tenant with shared schema support (part 1)
 *     04/21/2011-2.3 Guy Pelletier 
 *       - 337323: Multi-tenant with shared schema support (part 5)
 ******************************************************************************/  
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
