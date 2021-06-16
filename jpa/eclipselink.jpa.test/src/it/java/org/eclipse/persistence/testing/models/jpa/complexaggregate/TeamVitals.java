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
//     Oracle - initial API and implementation from Oracle TopLink
//     08/28/2008-1.1 Guy Pelletier
//       - 245120: unidir one-to-many within embeddable fails to deploy for missing primary key field
//     02/06/2009-2.0 Guy Pelletier
//       - 248293: JPA 2.0 Element Collections (part 2)
package org.eclipse.persistence.testing.models.jpa.complexaggregate;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.*;
import static jakarta.persistence.CascadeType.PERSIST;

@Embeddable
public class TeamVitals implements Serializable {
    private String position;
    private int jerseyNumber;
    private HockeyTeam hockeyTeam;
    private List<Role> roles;

    public TeamVitals() {
        roles = new ArrayList<Role>();
    }

    @Column(name="PLAYER_POSITION")
    public String getPosition() {
        return position;
    }

    @OneToOne(fetch=LAZY)
    @JoinColumn(name="TEAM_ID", referencedColumnName="ID")
    public HockeyTeam getHockeyTeam() {
        return hockeyTeam;
    }

    // Going to set this as a nested attribute override on HockeyPlayer.
    //@Column(name="JERSEY_NUMBER")
    public int getJerseyNumber() {
        return jerseyNumber;
    }

    @OneToMany(cascade=PERSIST)
    @JoinTable(
        name="PLAYER_ROLES",
        joinColumns=@JoinColumn(name="PLAYER_ID"),
        inverseJoinColumns=@JoinColumn(name="ROLE_ID")
    )
    public List<Role> getRoles() {
        return roles;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public void setHockeyTeam(HockeyTeam hockeyTeam) {
        this.hockeyTeam = hockeyTeam;
    }

    public void setJerseyNumber(int jerseyNumber) {
        this.jerseyNumber = jerseyNumber;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public String toString() {
        return "Team Vitals: Team [" + getHockeyTeam().getName() + "], Position [" + getPosition() + "]";
    }
}
