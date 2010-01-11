/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.advanced;

import javax.persistence.*;

@Entity
@Table(name="CMP3_GOLFER")
public class Golfer implements java.io.Serializable {
    private GolferPK golferPK;
    private WorldRank worldRank;
    
    public Golfer() {}

    @EmbeddedId
    public GolferPK getGolferPK() {
        return golferPK;
    }
    
    //@OneToOne - Test annotation defaulting.
    //@JoinColumn(name="ID", referencedColumnName="ID")
    public WorldRank getWorldRank() {
        return worldRank;
    }

    public void setGolferPK(GolferPK golferPK) {
        this.golferPK = golferPK;
    }

    
    public void setWorldRank(WorldRank worldRank) {
        this.worldRank = worldRank;
    }
    
    public String toString() {
        return "Golfer: golferPK(" + golferPK + "), WorldRank(" + worldRank.getId() + ")";
    }
}
