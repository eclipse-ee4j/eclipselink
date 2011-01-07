/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     11/23/2009-2.0 Guy Pelletier 
 *       - 295790: JPA 2.0 adding @MapsId to one entity causes initialization errors in other entities
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.advanced.derivedid;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * This model tests Example #1 of the mapsId cases.
 * 
 * @author gpelleti
 */
@Entity
@Table(name="JPA_MASTER_CORPORAL_CLONE")
public class MasterCorporalClone {
    @EmbeddedId 
    MasterCorporalId id;
        
    public MasterCorporalId getId() {
        return id;
    }
    
    public void setId(MasterCorporalId id) {
        this.id = id;
    }
}

