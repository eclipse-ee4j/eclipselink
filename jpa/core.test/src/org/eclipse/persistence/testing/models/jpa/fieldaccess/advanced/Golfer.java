/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced;

import javax.persistence.*;

@Entity(name="Golfer")
@Table(name="CMP3_FA_GOLFER")
public class Golfer implements java.io.Serializable {
	@EmbeddedId
    private GolferPK golferPK;
	@OneToOne
    @JoinColumn(name="ID", referencedColumnName="ID")
    private WorldRank worldRank;
    
    public Golfer() {}
    
    public GolferPK getGolferPK() {
        return golferPK;
    }
        
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
