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
 *     08/13/2010-2.2 Guy Pelletier 
 *       - 296078: JPA 2.0 with @MapsId, em.persist generates Internal Exception IllegalArgumentException
 ******************************************************************************/
package org.eclipse.persistence.testing.models.jpa.advanced.derivedid.nested;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;

@Entity 
@Table(name="JPA_GOLF_CLUB")
public class GolfClub {
    @EmbeddedId
    protected GolfClubPK clubPK;

    @MapsId("headId")
    @ManyToOne
    protected GolfClubHead head;

    @MapsId("shaftId")
    @ManyToOne
    protected GolfClubShaft shaft;

    @OneToOne(mappedBy="golfClub")
    protected GolfClubOrder order;

    public GolfClubPK getClubPK() {
        return clubPK;
    }
    
	public GolfClubHead getHead() {
        return head;
    }

	public GolfClubOrder getOrder() {
        return order;
    }
	
	public GolfClubShaft getShaft() {
        return shaft;
	}

	public void setClubPK(GolfClubPK clubPK) {
        this.clubPK = clubPK;
    }

    public void setHead(GolfClubHead head) {
        this.head = head;
    }

    public void setOrder(GolfClubOrder order) {
        this.order = order;
    }
    
	public void setShaft(GolfClubShaft shaft) {
        this.shaft = shaft;
    }
}