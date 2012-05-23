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
 *     04/28/2011-2.3 Guy Pelletier 
 *       - 337323: Multi-tenant with shared schema support (part 6)
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.ddlgeneration.multitenant;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import static javax.persistence.CascadeType.ALL;

@Entity
@DiscriminatorValue("DON")
@Table(name="DDL_BOSS")
@NamedQuery(
    name="UpdateBossName",
    query="UPDATE Boss b set b.firstName = :name where b.id = :id"
)
public class Boss extends Mafioso {
    private Underboss underboss;

    public Boss() {}

    @OneToOne(cascade=ALL)
    @JoinColumn(name="UNDERBOSS_ID")
    public Underboss getUnderboss() { 
        return underboss; 
    }

    public void setUnderboss(Underboss underboss) {
        this.underboss = underboss;
        
        if (underboss != null) {
            underboss.setBoss(this);
        }
    }
}
