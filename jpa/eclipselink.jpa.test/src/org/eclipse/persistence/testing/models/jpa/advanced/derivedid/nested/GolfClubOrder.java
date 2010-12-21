/*******************************************************************************
 * Copyright (c) 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     08/13/2010-2.2 Guy Pelletier 
 *       - 296078: JPA 2.0 with @MapsId, em.persist generates Internal Exception IllegalArgumentException
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.advanced.derivedid.nested;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity 
@Table(name="JPA_GOLF_CLUB_ORDER")
public class GolfClubOrder {
    @EmbeddedId
    protected GolfClubPK golfClubPK;

    @MapsId
    @OneToOne
    protected GolfClub golfClub;

    public GolfClub getGolfClub() {
        return golfClub;
    }
    
    public GolfClubPK getGolfClubPK() {
        return golfClubPK;
    }

    public void setGolfClub(GolfClub golfClub) {
        this.golfClub = golfClub;
    }

    public void setGolfClubPK(GolfClubPK golfClubPK) {
        this.golfClubPK = golfClubPK;
    }
}
