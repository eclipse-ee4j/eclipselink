/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     06/1/2011-2.3 Guy Pelletier 
 *       - 337323: Multi-tenant with shared schema support (part 9)
 ******************************************************************************/
package org.eclipse.persistence.testing.models.jpa.advanced.multitenant;

import static org.eclipse.persistence.annotations.MultitenantType.SINGLE_TABLE;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.Multitenant;
import org.eclipse.persistence.annotations.TenantDiscriminatorColumn;

@Entity
@Multitenant(SINGLE_TABLE)
@TenantDiscriminatorColumn(name="T_ID", contextProperty="tenant.id")
@Table(name="JPA_REWARD")
public class Reward {
    private int id;
    private String description;
    private Mafioso mafioso;

    public Reward() {}
    
    public Reward(String description) {
        this.description = description;
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
    
    @ManyToOne
    @JoinColumn(name="MAFIOSO_ID")
    public Mafioso getMafioso() {
        return mafioso;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public void setMafioso(Mafioso mafioso) {
        this.mafioso = mafioso;
    }
}
