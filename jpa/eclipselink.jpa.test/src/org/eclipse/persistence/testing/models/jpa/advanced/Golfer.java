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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.models.jpa.advanced;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.*;

@Entity
@Table(name="CMP3_GOLFER")
public class Golfer implements java.io.Serializable {
    private GolferPK golferPK;
    private WorldRank worldRank;
    private Map<String, Integer> sponsorDollars = null;

    public Golfer() {
        sponsorDollars = new HashMap<String, Integer>();
    }

    @EmbeddedId
    public GolferPK getGolferPK() {
        return golferPK;
    }

    //@OneToOne - Test annotation defaulting.
    //@JoinColumn(name="ID", referencedColumnName="ID")
    public WorldRank getWorldRank() {
        return worldRank;
    }

    public void setGolferPK(GolferPK golferPK) {
        this.golferPK = golferPK;
    }


    public void setWorldRank(WorldRank worldRank) {
        this.worldRank = worldRank;
    }

    @ElementCollection
    @MapKeyColumn(name = "SPONSOR_NAME")
    @Column(name = "SPONSOR_VALUE")
    public Map<String, Integer> getSponsorDollars() {
        return sponsorDollars;
    }

    public void setSponsorDollars(Map<String, Integer> sponsorDollars) {
        this.sponsorDollars = sponsorDollars;
    }

    public String toString() {
        return "Golfer: golferPK(" + golferPK + "), WorldRank(" + worldRank.getId() + ")";
    }
}
