/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.optimisticlocking;

import java.util.*;
import org.eclipse.persistence.indirection.*;

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
        getBandMembers().addElement(member);
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

    public void update() {
        name = name + ".";
    }

    public void verify() {
    }
}
