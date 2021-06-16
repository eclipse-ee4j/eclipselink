/*
 * Copyright (c) 2018, 2021 Oracle and/or its affiliates. All rights reserved.
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

package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlelement;

import java.io.Serializable;
import java.util.List;

public class Team implements Serializable {
    private List players;
    private String name;

    public Team() {
    }

    public Team(String name, List players) {
        setName(name);
        setPlayers(players);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setPlayers(List players) {
        this.players = players;
    }

    public List getPlayers() {
        return players;
    }

    public void setRosterCount(int n) {
    } // no-op but needed for property

    public int getRosterCount() {
        return (players == null) ? 0 : players.size();
    }
}
