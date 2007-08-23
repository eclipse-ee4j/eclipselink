/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.oxm.inheritance;


/**
 *  @version $Header: Vehicle.java 11-jun-2003.15:35:35 mmacivor Exp $
 *  @author  mmacivor
 *  @since   release specific (what release of product did this appear in)
 */
public class Vehicle {
    public String model;
    public String manufacturer;
    public int topSpeed;

    public boolean equals(Object theVehicle) {
        if (theVehicle instanceof Vehicle) {
            if (topSpeed == (((Vehicle)theVehicle).topSpeed)) {
                if (((model == null) && (((Vehicle)theVehicle).model == null)) || ((model != null) && (((Vehicle)theVehicle).model != null) && model.equals(((Vehicle)theVehicle).model))) {
                    if (((manufacturer == null) && (((Vehicle)theVehicle).manufacturer == null)) || ((manufacturer != null) && (((Vehicle)theVehicle).manufacturer != null) && manufacturer.equals(((Vehicle)theVehicle).manufacturer))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}