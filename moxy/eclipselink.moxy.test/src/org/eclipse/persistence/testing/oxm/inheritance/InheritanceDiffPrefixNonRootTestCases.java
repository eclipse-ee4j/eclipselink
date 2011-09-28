/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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

import java.util.ArrayList;
import java.util.List;
import junit.textui.TestRunner;
import org.eclipse.persistence.testing.oxm.mappings.XMLWithJSONMappingTestCases;

public class InheritanceDiffPrefixNonRootTestCases extends XMLWithJSONMappingTestCases {
    public InheritanceDiffPrefixNonRootTestCases(String name) throws Exception {
        super(name);
        setProject(new InheritanceProject());
        setControlDocument("org/eclipse/persistence/testing/oxm/inheritance/parkinglot_difference_prefix.xml");
        setControlJSON("org/eclipse/persistence/testing/oxm/inheritance/parkinglot.json");
        setWriteControlDocument("org/eclipse/persistence/testing/oxm/inheritance/parkinglot.xml");
        setControlJSONWrite("org/eclipse/persistence/testing/oxm/inheritance/parkinglot_write.json");        
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.oxm.inheritance.InheritanceDiffPrefixNonRootTestCases" };
        TestRunner.main(arguments);
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

        Vehicle vehicle = new Vehicle();
        vehicle.model = "Blah Blah";
        vehicle.manufacturer = "Some Place";
        vehicle.topSpeed = 10000;

        vehicles.add(vehicle);

        lot.setVehicles(vehicles);
        return lot;
    }

}
