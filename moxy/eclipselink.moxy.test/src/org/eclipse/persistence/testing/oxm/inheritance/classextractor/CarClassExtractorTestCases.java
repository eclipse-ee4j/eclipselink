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
package org.eclipse.persistence.testing.oxm.inheritance.classextractor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.textui.TestRunner;

import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.testing.oxm.inheritance.Car;
import org.eclipse.persistence.testing.oxm.inheritance.ParkingLot;
import org.eclipse.persistence.testing.oxm.inheritance.Vehicle;
import org.eclipse.persistence.testing.oxm.mappings.XMLWithJSONMappingTestCases;

public class CarClassExtractorTestCases extends XMLWithJSONMappingTestCases {
    public CarClassExtractorTestCases(String name) throws Exception {
        super(name);
        setProject(new InheritanceClassExtractorProject());
        setControlDocument("org/eclipse/persistence/testing/oxm/inheritance/parkinglotextractor.xml");
        setControlJSON("org/eclipse/persistence/testing/oxm/inheritance/parkinglotextractor.json");
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.oxm.inheritance.classextractor.CarClassExtractorTestCases" };
        TestRunner.main(arguments);
    }

    protected Map<String, String> getNamespaces(){
        Map map = new HashMap<String, String>();
        map.put("mynamespaceuri", "prefix");
        return map;
    }

    public Object getControlObject() {
        ParkingLot lot = new ParkingLot();

        List vehicles = new ArrayList();

        Car car = new Car();
        car.numberOfDoors = 2;
        car.milesPerGallon = 30;
        car.model = "Grand Am";
        car.manufacturer = "Pontiac";
        car.topSpeed = 220;
        vehicles.add(car);

        lot.setVehicles(vehicles);
        return lot;
    }
}
