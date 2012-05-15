/*******************************************************************************
 * Copyright (c) 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Blaise Doughan - 2.4 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.annotations.xmldiscriminator.ns;

import org.eclipse.persistence.testing.jaxb.JAXBTestCases;

public class XmlDiscriminatorNSTestCases extends JAXBTestCases {

    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/annotations/xmldiscriminator/ns/vehicle.xml";

    public XmlDiscriminatorNSTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[]{ Root.class, Car.class});
        setControlDocument(XML_RESOURCE);
    }

    public Root getControlObject() {
        Car car = new Car();
        car.numberOfDoors = 2;

        Root root = new Root();
        root.setVehicle(car);
        return root;
    }

}