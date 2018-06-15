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

public class FueledVehicle extends Vehicle {
    public Integer fuelCapacity;
    public String description;
    public String fuelType;

    public void change() {
        this.setPassengerCapacity(new Integer(100));
        this.addPartNumber("NEWPART 1");
        this.setFuelType("HOT AIR");

    }

    public static FueledVehicle example1(Company company) {
        FueledVehicle example = new FueledVehicle();

        example.setPassengerCapacity(new Integer(1));
        example.setFuelCapacity(new Integer(10));
        example.setDescription("Motercycle");
        example.getOwner().setValue(company);
        return example;
    }

    public void setDescription(String aDescription) {
        description = aDescription;
    }

    public void setFuelCapacity(Integer capacity) {
        fuelCapacity = capacity;
    }

    public void setFuelType(String type) {
        fuelType = type;
    }
}
