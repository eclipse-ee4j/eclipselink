/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle = 2.2 - Initial contribution
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.annotations.xmlclassextractor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.testing.jaxb.JAXBTestCases;

public class XmlClassExtractorTestCases extends JAXBTestCases {
    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/annotations/xmlclassextractor/parkinglot.xml";
    public XmlClassExtractorTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[]{Car.class, Vehicle.class, ParkingLot.class});
        setControlDocument(XML_RESOURCE);
        
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
