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
package org.eclipse.persistence.testing.models.aggregate;


/**
 * This type was created in VisualAge.
 */
public class Transport {
    public int id;
    public Vehicle vehicle;

    /**
     * Transport constructor comment.
     */
    public Transport() {
        super();
    }

    /**
     * Insert the method's description here.
     * Creation date: (12/14/00 3:53:34 PM)
     * @return org.eclipse.persistence.testing.models.aggregate.Transport
     */
    public static Transport example1() {
        Transport transport = new Transport();
        transport.setVehicle(Car.example1());
        transport.setId(5);
        return transport;
    }

    /**
     * Insert the method's description here.
     * Creation date: (12/14/00 3:53:34 PM)
     * @return org.eclipse.persistence.testing.models.aggregate.Transport
     */
    public static Transport example2() {
        Transport transport = new Transport();
        transport.setVehicle(Car.example2());
        transport.setId(1);
        return transport;
    }

    /**
     * Insert the method's description here.
     * Creation date: (12/14/00 3:53:34 PM)
     * @return org.eclipse.persistence.testing.models.aggregate.Transport
     */
    public static Transport example3() {
        Transport transport = new Transport();
        transport.setVehicle(Car.example3());
        transport.setId(2);
        return transport;
    }

    /**
     * Insert the method's description here.
     * Creation date: (12/14/00 3:53:34 PM)
     * @return org.eclipse.persistence.testing.models.aggregate.Transport
     */
    public static Transport example4() {
        Transport transport = new Transport();
        transport.setVehicle(Bicycle.example2());
        transport.setId(3);
        return transport;
    }

    /**
     * Insert the method's description here.
     * Creation date: (12/14/00 3:53:34 PM)
     * @return org.eclipse.persistence.testing.models.aggregate.Transport
     */
    public static Transport example5() {
        Transport transport = new Transport();
        transport.setVehicle(Bicycle.example3());
        transport.setId(4);
        return transport;
    }

    public static Transport example6() {
        Transport transport = new Transport();
        transport.setVehicle(Car.example4());
        transport.setId(6);
        return transport;
    }

    /**
     * This method was created in VisualAge.
     * @return int
     */
    public int getId() {
        return id;
    }

    /**
     * This method was created in VisualAge.
     * @return pr381InheritedAggregates.Vehicle
     */
    public Vehicle getVehicle() {
        return vehicle;
    }

    /**
     * This method was created in VisualAge.
     * @param newValue int
     */
    public void setId(int newValue) {
        this.id = newValue;
    }

    /**
     * This method was created in VisualAge.
     * @param newValue pr381InheritedAggregates.Vehicle
     */
    public void setVehicle(Vehicle newValue) {
        this.vehicle = newValue;
    }
}
