/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Blaise Doughan - 2.4 - initial implementation
package org.eclipse.persistence.testing.jaxb.annotations.xmldiscriminator.ns;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class XmlDiscriminatorRootNSTestCases extends JAXBWithJSONTestCases {

    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/annotations/xmldiscriminator/ns/root.xml";
    private static final String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/annotations/xmldiscriminator/ns/root.json";

    public XmlDiscriminatorRootNSTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[]{ Root.class, Car.class});
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
    }

    public Car getControlObject() {
        Car car = new Car();
        car.numberOfDoors = 2;
        return car;
    }

}
