/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.optimisticlocking;


public class DrumKit extends MusicalInstrument {
    public int kitSize;

    public DrumKit() {
        super();
    }

    public static DrumKit example1() {
        DrumKit kit = new DrumKit();
        kit.colour = "black";
        kit.kitSize = 5;
        kit.make = "Yamaha";
        return kit;
    }

    public static DrumKit example2() {
        DrumKit kit = new DrumKit();
        kit.colour = "silver";
        kit.kitSize = 4;
        kit.make = "ACME";
        return kit;
    }

    public void update() {
        colour = colour + ".";
        kitSize = kitSize + 1;
        make = make + ".";
    }
}