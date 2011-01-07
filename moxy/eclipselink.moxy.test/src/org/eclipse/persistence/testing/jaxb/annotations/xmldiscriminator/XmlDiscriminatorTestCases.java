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
 * dmccann - August 25/2009 - 2.2 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.annotations.xmldiscriminator;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.eclipse.persistence.testing.jaxb.JAXBTestCases;

public class XmlDiscriminatorTestCases extends JAXBTestCases {
    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/annotations/xmldiscriminator/vehicle.xml";
    private static final String XML_RESOURCE_WRITE = "org/eclipse/persistence/testing/jaxb/annotations/xmldiscriminator/vehicle-write.xml";

    public XmlDiscriminatorTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[]{ Car.class, Vehicle.class });
        setControlDocument(XML_RESOURCE);
        setWriteControlDocument(XML_RESOURCE_WRITE);
    }
    
    public Object getControlObject() {
        Car car = new Car();
        car.numberOfDoors = 2;
        car.milesPerGallon = 26;
        car.model = "Mustang GT";
        car.manufacturer = "Ford";
        car.topSpeed = 354;
        return new JAXBElement(new QName("vehicle-data"), Vehicle.class, car);
    }
}
