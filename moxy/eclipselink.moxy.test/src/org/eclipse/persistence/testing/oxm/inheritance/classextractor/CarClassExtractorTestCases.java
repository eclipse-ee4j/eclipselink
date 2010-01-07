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
package org.eclipse.persistence.testing.oxm.inheritance.classextractor;

import java.util.ArrayList;
import java.util.List;
import junit.textui.TestRunner;
import org.eclipse.persistence.testing.oxm.inheritance.Car;
import org.eclipse.persistence.testing.oxm.inheritance.ParkingLot;
import org.eclipse.persistence.testing.oxm.inheritance.Vehicle;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;

public class CarClassExtractorTestCases extends XMLMappingTestCases {
    public CarClassExtractorTestCases(String name) throws Exception {
        super(name);
        setProject(new InheritanceClassExtractorProject());
        setControlDocument("org/eclipse/persistence/testing/oxm/inheritance/parkinglotextractor.xml");
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.oxm.inheritance.classextractor.CarClassExtractorTestCases" };
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

        lot.setVehicles(vehicles);
        return lot;
    }
}
