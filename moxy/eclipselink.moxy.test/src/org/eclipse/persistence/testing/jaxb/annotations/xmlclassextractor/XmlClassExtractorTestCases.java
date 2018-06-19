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
//     Oracle = 2.2 - Initial contribution
package org.eclipse.persistence.testing.jaxb.annotations.xmlclassextractor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class XmlClassExtractorTestCases extends JAXBWithJSONTestCases {
    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/annotations/xmlclassextractor/parkinglot.xml";
    private static final String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/annotations/xmlclassextractor/parkinglot.json";
    public XmlClassExtractorTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[]{Car.class, Vehicle.class, ParkingLot.class});
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
    }

    public Object getControlObject() {
        Car car = new Car();
        car.numberOfDoors = 2;
        car.milesPerGallon = 30;
        car.model = "Grand Am";
        car.manufacturer = "Pontiac";
        car.topSpeed = 220;

        List vehicles = new ArrayList();
        vehicles.add(car);

        ParkingLot lot = new ParkingLot();
        lot.setVehicles(vehicles);
        return lot;    }

}
