/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.oxm.inheritance;


/**
 *  @version $Header: Car.java 29-may-2006.14:05:32 dmahar Exp $
 *  @author  mmacivor
 *  @since   release specific (what release of product did this appear in)
 */
public class Car extends Vehicle {
    public int numberOfDoors;
    public int milesPerGallon;

    public boolean equals(Object theVehicle) {
        boolean isEqual = super.equals(theVehicle);
        if (isEqual && theVehicle instanceof Car) {
            if (numberOfDoors == ((Car)theVehicle).numberOfDoors) {
                if (milesPerGallon == ((Car)theVehicle).milesPerGallon) {
                    return true;
                }
            }
        }
        return isEqual;
    }
}
