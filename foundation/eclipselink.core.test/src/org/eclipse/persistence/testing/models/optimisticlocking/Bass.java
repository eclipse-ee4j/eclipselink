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


public class Bass extends Guitar {

    public Bass() {
        super();
    }

    public static Guitar example1() {
        Bass bass = new Bass();
        bass.colour = "red";
        bass.isAcoustic = false;
        bass.make = "Pevey";
        bass.numberOfStrings = 4;
        return bass;
    }

    public static Guitar example2() {
        Bass bass = new Bass();
        bass.colour = "black";
        bass.isAcoustic = true;
        bass.make = "Ibanez";
        bass.numberOfStrings = 5;
        return bass;
    }
}
