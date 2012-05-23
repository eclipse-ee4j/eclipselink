/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
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

public class FuelType implements java.io.Serializable {
    public Integer fuelId;
    public String fuelDescription;
    
    public FuelType() {
    }

    public void setFuelId(Integer id) {
        fuelId = id;
    }
    
    public void setFuelDescription(String aDescription) {
        fuelDescription = aDescription;
    }

    public Integer getFuelId() {
        return fuelId;
    }

    public String getFuelDescription() {
        return fuelDescription;
    }
    
    public String toString() {
        return "[FuelType] " + fuelId + " - " + fuelDescription;
    }

    public static FuelType example1() {
        FuelType example = new FuelType();
        example.setFuelId(new Integer(1));
        example.setFuelDescription("Petrol");
        return example;
    }
    
    public static FuelType example2() {
        FuelType example = new FuelType();
        example.setFuelId(new Integer(2));
        example.setFuelDescription("Diesel");
        return example;
    }
}
