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
// dmccann - August 24/2010 - 2.2 - Initial implementation
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmldiscriminator;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.persistence.oxm.annotations.XmlDiscriminatorValue;

@XmlRootElement(name = "car-data")
@XmlDiscriminatorValue("mustang")
public class Car extends Vehicle {
    @XmlElement(name = "number-of-doors")
    public int numberOfDoors;
    @XmlElement(name = "miles-per-gallon")
    public int milesPerGallon;

    public boolean equals(Object obj) {
        Car cObj;
        try {
            cObj = (Car) obj;
        } catch (ClassCastException cce) {
            return false;
        }
        return milesPerGallon == cObj.milesPerGallon &&
           numberOfDoors == cObj.numberOfDoors &&
           topSpeed == cObj.topSpeed &&
           manufacturer.equals(cObj.manufacturer) &&
           model.equals(cObj.model);
    }
}
