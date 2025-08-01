/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.models.optimisticlocking;

import org.eclipse.persistence.indirection.ValueHolder;
import org.eclipse.persistence.indirection.ValueHolderInterface;

import java.util.Vector;

public class RockBand implements SelfUpdatable {
    public int id;
    public String name;
    public ValueHolderInterface bandMembers;
    public Vector releasedAlbumNames;

    public RockBand() {
        super();
        bandMembers = new ValueHolder(new Vector());
    }

    public void addBandMember(RockMusician member) {
        getBandMembers().add(member);
        member.setBand(this);
    }

    public static RockBand example1() {
        RockBand rockBand = new RockBand();
        rockBand.name = "Crusher Kings";
        rockBand.addBandMember(RockMusician.example1());
        rockBand.addBandMember(RockMusician.example2());
        rockBand.addBandMember(RockMusician.example3());
        return rockBand;
    }

    public static RockBand example2() {
        RockBand rockBand = new RockBand();
        rockBand.name = "Pickle This";
        rockBand.addBandMember(RockMusician.example4());
        rockBand.addBandMember(RockMusician.example5());
        rockBand.addBandMember(RockMusician.example6());
        return rockBand;
    }

    public Vector getBandMembers() {
        return (Vector)this.bandMembers.getValue();

    }

    public void setBandMembers(Vector bandMembers) {
        this.bandMembers.setValue(bandMembers);

    }

    @Override
    public void update() {
        name = name + ".";
    }

    public void verify() {
    }
}
