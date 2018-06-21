/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     08/13/2010-2.2 Guy Pelletier
//       - 296078: JPA 2.0 with @MapsId, em.persist generates Internal Exception IllegalArgumentException
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
