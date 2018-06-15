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
package org.eclipse.persistence.testing.models.jpa.xml.inheritance;

import javax.persistence.*;

@Entity(name="XMLFueledVehicle")
@EntityListeners(org.eclipse.persistence.testing.models.jpa.xml.inheritance.listeners.FueledVehicleListener.class)
@Table(name="CMP3_XML_FUEL_VEH")
@DiscriminatorValue("F")
@ExcludeDefaultListeners
@ExcludeSuperclassListeners
public class FueledVehicle extends Vehicle {
    private Integer fuelCapacity;
    private String description;
    private String fuelType;

    @Column(name="DESCRIP")
    public String getDescription() {
        return description;
    }

    public void setDescription(String aDescription) {
        description = aDescription;
    }

    @Column(name="FUEL_CAP")
    public Integer getFuelCapacity() {
        return fuelCapacity;
    }

    public void setFuelCapacity(Integer capacity) {
        fuelCapacity = capacity;
    }
    @Column(name="FUEL_TYP")
    public String getFuelType() {
        return fuelType;
    }

    public void setFuelType(String type) {
        fuelType = type;
    }

    public void change() {
        this.setPassengerCapacity(new Integer(100));
        this.setFuelType("HOT AIR");

    }
}
