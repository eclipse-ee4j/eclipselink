/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     08/13/2010-2.2 Guy Pelletier
//       - 296078: JPA 2.0 with @MapsId, em.persist generates Internal Exception IllegalArgumentException
package org.eclipse.persistence.testing.models.jpa.advanced.derivedid.nested;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

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
