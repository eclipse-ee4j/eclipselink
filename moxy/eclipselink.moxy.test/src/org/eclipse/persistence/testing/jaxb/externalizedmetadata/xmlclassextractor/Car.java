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
// dmccann - July 28/2010 - 2.2 - Initial implementation
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlclassextractor;

public class Car extends Vehicle {
    public int numberOfDoors;
    public int milesPerGallon;

    public boolean equals(Object theVehicle) {
        boolean isEqual = super.equals(theVehicle);
        if (isEqual && theVehicle instanceof Car) {
            if (numberOfDoors == ((Car) theVehicle).numberOfDoors) {
                if (milesPerGallon == ((Car) theVehicle).milesPerGallon) {
                    return true;
                }
            }
        }
        return isEqual;
    }
}
