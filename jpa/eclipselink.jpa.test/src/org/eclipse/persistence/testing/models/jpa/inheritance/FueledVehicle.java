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


package org.eclipse.persistence.testing.models.jpa.inheritance;

import javax.persistence.*;

@Entity
@EntityListeners(org.eclipse.persistence.testing.models.jpa.inheritance.listeners.FueledVehicleListener.class)
@Table(name="CMP3_FUEL_VEH")
@DiscriminatorValue("F")
public class FueledVehicle extends Vehicle implements Cloneable {
    private Integer fuelCapacity;
    private String description;
    private String fuelType;
    private String colour;

    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException exception) {
            throw new InternalError(exception.toString());
        }
    }

    public void change() {
        this.setPassengerCapacity(new Integer(100));
        this.setFuelType("HOT AIR");
    }

    @Column(name="COLOUR")
    public String getColor() {
        return colour;
    }

    @Column(name="DESCRIP")
    public String getDescription() {
        return description;
    }

    @Column(name="FUEL_CAP")
    public Integer getFuelCapacity() {
        return fuelCapacity;
    }

    @Column(name="FUEL_TYP")
    public String getFuelType() {
        return fuelType;
    }

    public void setColor(String colour) {
        this.colour = colour;
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
