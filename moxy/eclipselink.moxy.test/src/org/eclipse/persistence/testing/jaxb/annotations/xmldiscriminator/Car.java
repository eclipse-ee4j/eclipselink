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
 * dmccann - August 24/2010 - 2.2 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.annotations.xmldiscriminator;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.persistence.oxm.annotations.XmlDiscriminatorValue;

@XmlRootElement(name = "car-data")
@XmlDiscriminatorValue("car")
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