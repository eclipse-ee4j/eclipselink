/*
 * Copyright (c) 1998, 2024 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.oxm.inheritance;

import java.util.ArrayList;
import java.util.List;

public class ParkingLot {
    private List vehicles;

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
            if (size == ((ParkingLot)theObject).getVehicles().size()) {
                for (int i = 0; i < size; i++) {
                    Object nextVehicle = getVehicles().get(i);
                    Object nextOtherVehicle = ((ParkingLot)theObject).getVehicles().get(i);
                    if (nextVehicle.getClass() != nextOtherVehicle.getClass()) {
                        return false;
                    }

                    return nextVehicle.equals(nextOtherVehicle);
                }
            }
        }
        return false;
    }

    public String toString() {
        StringBuilder returnString = new StringBuilder("ParkingLot has " + vehicles.size() + " vehicles:");
        for (Object vehicle : vehicles) {
            Vehicle nextVehicle = (Vehicle) vehicle;
            returnString.append(" ").append(nextVehicle.toString());
        }
        return returnString.toString();
    }
}
