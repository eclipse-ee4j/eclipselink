/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.advanced;

import javax.persistence.*;

@Entity
@Table(name="CMP3_WORLDRANK")
public class WorldRank implements java.io.Serializable {
    private int id;
    
    public WorldRank() {}
    
    @Id
    @Column(name="ID")
    public int getId() { 
        return id; 
    }
    
    public void setId(int id) { 
        this.id = id; 
    }
}
 
