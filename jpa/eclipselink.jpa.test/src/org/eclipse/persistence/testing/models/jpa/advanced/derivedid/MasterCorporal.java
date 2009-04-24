/*******************************************************************************
 * Copyright (c) 1998, 2009 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     04/24/2009-2.0 Guy Pelletier 
 *       - 270011: JPA 2.0 MappedById support
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.advanced.derivedid;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.MappedById;
import javax.persistence.Table;

@Entity
@Table(name="JPA_MASTER_CORPORAL")
public class MasterCorporal {
    @EmbeddedId 
    MasterCorporalId id;
    
    @ManyToOne 
    @MappedById("sargeantPK")
    Sargeant sargeant;
    
    public MasterCorporalId getId() {
        return id;
    }
    
    public Sargeant getSargeant() {
        return sargeant;
    }
    
    public void setId(MasterCorporalId id) {
        this.id = id;
    }

    public void setSargeant(Sargeant sargeant) {
        this.sargeant = sargeant;
        id.setSargeantPK(sargeant.getSargeantId());
    }
}

