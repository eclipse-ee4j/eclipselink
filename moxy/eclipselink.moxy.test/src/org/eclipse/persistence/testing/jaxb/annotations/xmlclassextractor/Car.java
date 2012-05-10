/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * Oracle - 2.2 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.annotations.xmlclassextractor;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="car")
public class Car extends Vehicle {
    @XmlElement(name="number-of-doors")
    public int numberOfDoors;
    @XmlElement(name="miles-per-gallon")
    public int milesPerGallon;
       
   public boolean equals(Object theVehicle) {
        
        if (!(theVehicle instanceof Car)){
        	return false;
        }
        boolean isEqual = super.equals(theVehicle);
        if (isEqual) {
            if (numberOfDoors == ((Car)theVehicle).numberOfDoors) {
                if (milesPerGallon == ((Car)theVehicle).milesPerGallon) {
                    return true;
                }
            }
        }
        return isEqual;
    }
}
