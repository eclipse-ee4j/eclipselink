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
 *     04/24/2009-2.0 Guy Pelletier 
 *       - 270011: JPA 2.0 MappedById support
 *     11/23/2009-2.0 Guy Pelletier 
 *       - 295790: JPA 2.0 adding @MapsId to one entity causes initialization errors in other entities
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.advanced.derivedid;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class MasterCorporalId {
    @Column(name="NAME")
    String name;
    
    // In the derived id case from MasterCorporal this column name is ignored
    // In the non derived id case from MasterCorporalClone, this column name is used.
    @Column(name="SARGEANTPK")
    long sargeantPK;
    
    public String getName() {
        return name;
    }
    
    public long getSargeantPK() {
        return sargeantPK;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public void setSargeantPK(long sargeantPK) {
        this.sargeantPK = sargeantPK;
    }
}

