/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     01/28/2009-2.0 Guy Pelletier
//       - 248293: JPA 2.0 Element Collections (part 1)
//     02/06/2009-2.0 Guy Pelletier
//       - 248293: JPA 2.0 Element Collections (part 2)
package org.eclipse.persistence.testing.models.jpa.inherited;

import java.sql.Date;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import static jakarta.persistence.CascadeType.PERSIST;

@Embeddable
public class Record {
    @Column(name="R_DATE")
    private Date date;

    @Column(name="DESCR")
    private String description;

    @ManyToOne(cascade=PERSIST)
    @JoinColumn(name="L_ID")
    private Location location;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name="name", column=@Column(name="VENUE_NAME")),
        @AttributeOverride(name="attendance", column=@Column(name="VENUE_ATTENDANCE"))
    })
    // Novice beer consumer is going to override VENUE_NAME to VENUE and use
    // VENUE_ATTENDANCE as is.
    // Expert beer consumer is going to override VENUE_ATTENDANCE to WITNESSES
    // and user VENUE_NAME as is.
    private Venue venue;

    public Date getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public Location getLocation() {
        return location;
    }

    public Venue getVenue() {
        return venue;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setVenue(Venue venue) {
        this.venue = venue;
    }
}
