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
