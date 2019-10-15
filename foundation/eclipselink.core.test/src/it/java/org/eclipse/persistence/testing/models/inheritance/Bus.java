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
package org.eclipse.persistence.testing.models.inheritance;

public class Bus extends FueledVehicle {
    public Person busDriver;

    public static Bus example2(Company company) {
        Bus example = new Bus();

        example.setPassengerCapacity(new Integer(30));
        example.setFuelCapacity(new Integer(100));
        example.setDescription("SCHOOL BUS");
        example.setFuelType("Petrol");
        example.getOwner().setValue(company);
        example.addPartNumber("188298SU-k");
        example.addPartNumber("199211HI-x");
        example.addPartNumber("023392SY-x");
        example.addPartNumber("002345DP-s");
        return example;
    }

    public static Bus example3(Company company) {
        Bus example = new Bus();

        example.setPassengerCapacity(new Integer(30));
        example.setFuelCapacity(new Integer(100));
        example.setDescription("TOUR BUS");
        example.setFuelType("Petrol");
        example.getOwner().setValue(company);
        example.addPartNumber("188298SU-k");
        example.addPartNumber("199211HI-x");
        example.addPartNumber("023392SY-x");
        example.addPartNumber("002345DP-s");
        return example;
    }

    public static Bus example4(Company company) {
        Bus example = new Bus();

        example.setPassengerCapacity(new Integer(30));
        example.setFuelCapacity(new Integer(100));
        example.setDescription("TRANSIT BUS");
        example.setFuelType("Gas");
        example.getOwner().setValue(company);
        example.addPartNumber("188298SU-k");
        example.addPartNumber("199211HI-x");
        example.addPartNumber("023392SY-x");
        example.addPartNumber("002345DP-s");
        return example;
    }
}
