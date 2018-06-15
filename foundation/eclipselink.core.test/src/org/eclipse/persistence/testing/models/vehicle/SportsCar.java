/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Vikram Bhatia - initial API and implementation
package org.eclipse.persistence.testing.models.vehicle;

public class SportsCar implements java.io.Serializable {
    public Integer id;
    public Integer fuelCapacity;
    public String description;
    public FuelType fuelType;
    public EngineType engineType;

    public SportsCar() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setDescription(String aDescription) {
        description = aDescription;
    }

    public void setFuelCapacity(Integer capacity) {
        fuelCapacity = capacity;
    }

    public void setFuelType(FuelType type) {
        fuelType = type;
    }

    public void setEngineType(EngineType type) {
        engineType = type;
    }

    public Integer getFuelCapacity() {
        return fuelCapacity;
    }

    public String getDescription() {
        return description;
    }

    public FuelType getFuelType() {
        return fuelType;
    }

    public EngineType getEngineType() {
        return engineType;
    }

    public static SportsCar example1() {
        SportsCar example = new SportsCar();

        example.setId(10);
        example.setFuelCapacity(new Integer(30));
        example.setDescription("TOYOTA");
        example.setFuelType(FuelType.example1());
        example.setEngineType(EngineType.example1());
        return example;
    }

    public static SportsCar example2() {
        SportsCar example = new SportsCar();

        example.setId(20);
        example.setFuelCapacity(new Integer(50));
        example.setDescription("TATA INDICA");
        example.setFuelType(FuelType.example2());
        example.setEngineType(EngineType.example2());
        return example;
    }
}
