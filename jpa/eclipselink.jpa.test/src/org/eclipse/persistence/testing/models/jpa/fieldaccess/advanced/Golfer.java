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
package org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced;

import javax.persistence.*;

import org.eclipse.persistence.annotations.IdValidation;
import org.eclipse.persistence.annotations.PrimaryKey;

@Entity(name="Golfer")
@Table(name="CMP3_FA_GOLFER")
@PrimaryKey(validation=IdValidation.NULL)
public class Golfer implements java.io.Serializable {
    @EmbeddedId
    private GolferPK golferPK;
    @OneToOne
    @JoinColumn(name="ID", referencedColumnName="ID")
    private WorldRank worldRank;

    public Golfer() {}

    public GolferPK getGolferPK() {
        return golferPK;
    }

    public WorldRank getWorldRank() {
        return worldRank;
    }

    public void setGolferPK(GolferPK golferPK) {
        this.golferPK = golferPK;
    }

    public void setWorldRank(WorldRank worldRank) {
        this.worldRank = worldRank;
    }

    public String toString() {
        return "Golfer: golferPK(" + golferPK + "), WorldRank(" + worldRank.getId() + ")";
    }
}
