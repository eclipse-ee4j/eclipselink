/*******************************************************************************
 * Copyright (c) 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Vikram Bhatia - initial API and implementation
 ******************************************************************************/  
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
