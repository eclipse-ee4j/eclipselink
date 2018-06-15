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
//     02/25/2009-2.0 Guy Pelletier
//       - 265359: JPA 2.0 Element Collections - Metadata processing portions
package org.eclipse.persistence.testing.models.jpa.complexaggregate;

import javax.persistence.*;

import java.io.Serializable;
import java.util.Collection;

@Embeddable
public class CoachVitals implements Serializable {
    private HockeyTeam hockeyTeam;
    private PersonalVitals personalVitals;
    private Collection<String> nickNames;

    public CoachVitals() {}

    @OneToOne(fetch=javax.persistence.FetchType.LAZY)
    @JoinColumn(name="TEAM_ID", referencedColumnName="ID")
    public HockeyTeam getHockeyTeam() {
        return hockeyTeam;
    }

    @Embedded
    public PersonalVitals getPersonalVitals() {
        return personalVitals;
    }

    public void setHockeyTeam(HockeyTeam hockeyTeam) {
        this.hockeyTeam = hockeyTeam;
    }

    public void setPersonalVitals(PersonalVitals personalVitals) {
        this.personalVitals = personalVitals;
    }

    @ElementCollection
    public Collection<String> getNickNames() {
        return nickNames;
    }

    public void setNickNames(Collection<String> nickNames) {
        this.nickNames = nickNames;
    }
}
