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
// Oracle - 2.2 - Initial implementation
package org.eclipse.persistence.testing.jaxb.annotations.xmlclassextractor;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.persistence.oxm.annotations.XmlPath;


@XmlRootElement(name="parking-lot")
public class ParkingLot {
    @XmlPath("vehicles/vehicle")
    private List<Vehicle> vehicles;

    public ParkingLot() {
        vehicles = new ArrayList();
    }

    public void setVehicles(List vehicles) {
        this.vehicles = vehicles;
    }

    public List getVehicles() {
        return vehicles;
    }

    public boolean equals(Object theObject) {
        if (theObject instanceof ParkingLot) {
            int size = getVehicles().size();
            if (size == ((ParkingLot) theObject).getVehicles().size()) {
                for (int i = 0; i < size; i++) {
                    Object nextVehicle = getVehicles().get(i);
                    Object nextOtherVehicle = ((ParkingLot) theObject).getVehicles().get(i);
                    if (nextVehicle.getClass() != nextOtherVehicle.getClass()) {
                        return false;
                    }
                    if (!(nextVehicle.equals(nextOtherVehicle))) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    public String toString() {
        String returnString = "ParkingLot has " + vehicles.size() + " vehicles:";
        for (int i = 0; i < vehicles.size(); i++) {
            Vehicle nextVehicle = (Vehicle) vehicles.get(i);
            returnString += (" " + nextVehicle.toString());
        }
        return returnString;
    }
}
