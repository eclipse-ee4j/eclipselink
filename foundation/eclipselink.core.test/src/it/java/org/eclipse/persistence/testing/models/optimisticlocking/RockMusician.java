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
package org.eclipse.persistence.testing.models.optimisticlocking;

import org.eclipse.persistence.indirection.*;

public class RockMusician extends Musician {
    public String stageName;
    public ValueHolderInterface band;

    public RockMusician() {
        super();
        band = new ValueHolder();
    }

    public static RockMusician example1() {
        RockMusician rockMusician = new RockMusician();
        rockMusician.legalName = "Paul Smith";
        rockMusician.stageName = "Snake Boy";
        rockMusician.mainInstrument = Guitar.example1();
        return rockMusician;
    }

    public static RockMusician example2() {
        RockMusician rockMusician = new RockMusician();
        rockMusician.legalName = "Ida Jones";
        rockMusician.stageName = rockMusician.legalName;
        rockMusician.mainInstrument = Bass.example1();
        return rockMusician;
    }

    public static RockMusician example3() {
        RockMusician rockMusician = new RockMusician();
        rockMusician.legalName = "Peter Mary";
        rockMusician.stageName = "Petra Murder";
        rockMusician.mainInstrument = DrumKit.example1();
        return rockMusician;
    }

    public static RockMusician example4() {
        RockMusician rockMusician = new RockMusician();
        rockMusician.legalName = "Kyle Herrick";
        rockMusician.stageName = "Kyle \"Shredder\" Herrick";
        rockMusician.mainInstrument = Guitar.example2();
        return rockMusician;
    }

    public static RockMusician example5() {
        RockMusician rockMusician = new RockMusician();
        rockMusician.legalName = "Paula High";
        rockMusician.stageName = rockMusician.legalName + " Lust";
        rockMusician.mainInstrument = Bass.example2();
        return rockMusician;
    }

    public static RockMusician example6() {
        RockMusician rockMusician = new RockMusician();
        rockMusician.legalName = "Pat Hirt";
        rockMusician.stageName = "Patch Hurt";
        rockMusician.mainInstrument = DrumKit.example2();
        return rockMusician;
    }

    public RockBand getBand() {
        return (RockBand)this.band.getValue();
    }

    public void setBand(RockBand band) {
        this.band.setValue(band);
    }

    public void update() {
        legalName = legalName + ".";
        stageName = stageName + ".";
        //new instrument
        mainInstrument = Guitar.example1();

    }
}
