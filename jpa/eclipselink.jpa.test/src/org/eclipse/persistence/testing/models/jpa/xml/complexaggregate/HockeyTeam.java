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
package org.eclipse.persistence.testing.models.jpa.xml.complexaggregate;

import java.util.List;
import java.util.ArrayList;
import java.io.Serializable;

public class HockeyTeam implements Serializable {
    private HockeyTeamDetails teamDetails;
    private HockeyTeamId id;

    private List<HockeyPlayer> players;
    private OwnershipGroup ownershipGroup;

    private String name;

    public HockeyTeam() {
        teamDetails = new HockeyTeamDetails();
        players = new ArrayList();
    }

    public HockeyTeam(String name) {
        teamDetails = new HockeyTeamDetails();
        players = new ArrayList();

        id = new HockeyTeamId();
        id.setId(System.currentTimeMillis());
        id.setDescription(name + " - id");

        this.name = name;
    }

    public HockeyTeamId getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public OwnershipGroup getOwnershipGroup() {
        return ownershipGroup;
    }

    public List<HockeyPlayer> getPlayers() {
        return players;
    }

    public HockeyTeamDetails getTeamDetails() {
        return teamDetails;
    }

    public void setId(HockeyTeamId id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOwnershipGroup(OwnershipGroup ownershipGroup) {
        this.ownershipGroup = ownershipGroup;
    }

    public void setPlayers(List<HockeyPlayer> players) {
        this.players = players;
    }

    public void setTeamDetails(HockeyTeamDetails teamDetails) {
        this.teamDetails = teamDetails;
    }

    public String toString() {
        return "Hockey team: " + name;
    }
}
