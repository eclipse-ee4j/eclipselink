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
package org.eclipse.persistence.testing.models.inheritance;

public class Bicycle extends NonFueledVehicle {
    public String description;

    public void change() {
        this.setPassengerCapacity(new Integer(100));
        this.addPartNumber("NEWBIKEPART 1");
        this.setDescription("This Bike is easy to handle");

    }

    public static Bicycle example1(Company company) {
        Bicycle example = new Bicycle();

        example.setPassengerCapacity(new Integer(1));
        example.getOwner().setValue(company);
        example.setDescription("Hercules");
        example.addPartNumber("1288H8HH-f");
        example.addPartNumber("199448GY-s");
        return example;
    }

    public static Bicycle example2(Company company) {
        Bicycle example = new Bicycle();

        example.setPassengerCapacity(new Integer(2));
        example.getOwner().setValue(company);
        example.setDescription("Atlas");
        example.addPartNumber("176339GT-a");
        example.addPartNumber("199448GY-s");
        example.addPartNumber("166761UO-z");
        return example;
    }

    public static Bicycle example3(Company company) {
        Bicycle example = new Bicycle();

        example.setPassengerCapacity(new Integer(3));
        example.getOwner().setValue(company);
        example.setDescription("Aone");
        example.addPartNumber("188181TT-a");
        example.addPartNumber("696969BO-b");
        return example;
    }

    public void setDescription(String aDescription) {
        description = aDescription;
    }
}
