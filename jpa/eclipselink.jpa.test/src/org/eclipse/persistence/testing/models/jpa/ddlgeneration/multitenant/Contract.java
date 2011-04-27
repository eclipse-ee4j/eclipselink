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
 *     04/28/2011-2.3 Guy Pelletier 
 *       - 337323: Multi-tenant with shared schema support (part 6)
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.ddlgeneration.multitenant;

import java.util.Collection;
import java.util.Vector;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Version;

import org.eclipse.persistence.annotations.Multitenant;
import org.eclipse.persistence.annotations.TenantDiscriminatorColumn;

import static javax.persistence.CascadeType.ALL;
import static org.eclipse.persistence.annotations.MultitenantType.SINGLE_TABLE;

@Entity
@Table(name="DDL_CONTRACT")
@Multitenant(SINGLE_TABLE)
@NamedQueries({
  @NamedQuery(
    name="FindAllContracts",
    query="SELECT c FROM Contract c"),
  @NamedQuery(
    name="UpdateAllContractDescriptions",
    query="UPDATE Contract c SET c.description = 'voided'")
})
// Will default the tenant discriminator column to TENANT_ID, which is mapped below.
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
    
    @Column(name="DESCRIP")
    public String getDescription() {
        return description;
    }

    @Id
    @Column(name="ID")
    @GeneratedValue
    public int getId() { 
        return id; 
    }

    @ManyToMany(cascade=ALL)
    @JoinTable(
        name="DDL_CONTRACT_SOLDIER",
        joinColumns=@JoinColumn(name="CONTRACT_ID"),
        inverseJoinColumns=@JoinColumn(name="SOLDIER_ID")
    )
    public Collection<Soldier> getSoldiers() { 
        return soldiers; 
    }
    
    @Column(name="TENANT_ID", insertable=false, updatable=false)
    public String getTenantId() {
        return tenantId;
    }
    
    @Version
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
