/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
// dmccann - August 25/2009 - 2.2 - Initial implementation
package org.eclipse.persistence.testing.jaxb.annotations.xmldiscriminator;

import jakarta.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class XmlDiscriminatorTestCases extends JAXBWithJSONTestCases {
    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/annotations/xmldiscriminator/vehicle.xml";
    private static final String XML_RESOURCE_WRITE = "org/eclipse/persistence/testing/jaxb/annotations/xmldiscriminator/vehicle-write.xml";
    private static final String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/annotations/xmldiscriminator/vehicle.json";
    private static final String JSON_RESOURCE_WRITE = "org/eclipse/persistence/testing/jaxb/annotations/xmldiscriminator/vehicle-write.json";
    public XmlDiscriminatorTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[]{ Car.class, Vehicle.class });
        setControlDocument(XML_RESOURCE);
        setWriteControlDocument(XML_RESOURCE_WRITE);
        setControlJSON(JSON_RESOURCE);
        setWriteControlJSON(JSON_RESOURCE_WRITE);
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

    public Object getReadControlObject() {
           Car car = new Car();
           car.numberOfDoors = 2;
           car.milesPerGallon = 26;
           car.model = "Mustang GT";
           car.manufacturer = "Ford";
           car.topSpeed = 354;
           return car;
    }
    public void testRoundTrip() throws Exception{}

}
