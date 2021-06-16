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


public class Guitar extends MusicalInstrument {
    public int numberOfStrings;
    public boolean isAcoustic;

    public Guitar() {
        super();
    }

    public static Guitar example1() {
        Guitar guitar = new Guitar();
        guitar.colour = "gold";
        guitar.isAcoustic = false;
        guitar.numberOfStrings = 6;
        guitar.make = "Ibanez";
        return guitar;
    }

    public static Guitar example2() {
        Guitar guitar = new Guitar();
        guitar.colour = "brown";
        guitar.isAcoustic = true;
        guitar.numberOfStrings = 12;
        guitar.make = "Yamaha";
        return guitar;
    }

    public void update() {
        colour = colour + ".";
        isAcoustic = !isAcoustic;
        numberOfStrings = numberOfStrings + 1;
        make = make + ".";
    }
}
