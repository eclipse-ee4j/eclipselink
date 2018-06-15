/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     02/06/2009-2.0 Guy Pelletier
//       - 248293: JPA 2.0 Element Collections (part 2)
package org.eclipse.persistence.testing.models.jpa.inherited;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;

@Embeddable
public class Venue {
    private String name;
    private Integer attendance;

    @Embedded
    // There are no attribute overrides at this level, however, novice beer
    // consumer is going to provide the following overrides:
    // - yearBuilt -> VENUE_YEAR_BUILT
    // - builder -> VENUE_BUILDER
    // And expert beer consumer is going to use the defaults (columns) of
    // - YEAR_BUILT
    // - BUILDER
    private VenueHistory history;

    public Integer getAttendance() {
        return attendance;
    }

    public VenueHistory getHistory() {
        return history;
    }

    public String getName() {
        return name;
    }

    public void setAttendance(Integer attendance) {
        this.attendance = attendance;
    }

    public void setHistory(VenueHistory history) {
        this.history = history;
    }

    public void setName(String name) {
        this.name = name;
    }
}
