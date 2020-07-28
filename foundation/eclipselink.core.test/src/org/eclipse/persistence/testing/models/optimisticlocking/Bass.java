/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
