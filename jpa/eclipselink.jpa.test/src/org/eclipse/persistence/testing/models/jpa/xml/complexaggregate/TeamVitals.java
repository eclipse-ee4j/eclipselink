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
//     04/02/2008-1.0M6 Guy Pelletier
//       - 224155: embeddable-attributes should be extended in the EclipseLink ORM.XML schema
//     08/28/2008-1.1 Guy Pelletier
//       - 245120: unidir one-to-many within embeddable fails to deploy for missing primary key field
package org.eclipse.persistence.testing.models.jpa.xml.complexaggregate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TeamVitals implements Serializable {
    private String position;
    private int jerseyNumber;
    private HockeyTeam hockeyTeam;
    private List<Role> roles;

    public TeamVitals() {
        roles = new ArrayList<Role>();
    }

    public String getPosition() {
        return position;
    }

    public HockeyTeam getHockeyTeam() {
        return hockeyTeam;
    }

    public int getJerseyNumber() {
        return jerseyNumber;
    }

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
