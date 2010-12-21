/*******************************************************************************
 * Copyright (c) 2010 Oracle. All rights reserved.
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

import org.eclipse.persistence.oxm.annotations.XmlClassExtractor;
import org.eclipse.persistence.sessions.Record;

@XmlClassExtractor(MyClassExtractor.class)
@XmlRootElement(name="vehicle")
public class Vehicle {
    public String model;
    public String manufacturer;
    @XmlElement(name="top-speed")
    public int topSpeed;

    public boolean equals(Object theVehicle) {
        if (theVehicle instanceof Vehicle) {
            if (topSpeed == (((Vehicle) theVehicle).topSpeed)) {
                if (((model == null) && (((Vehicle) theVehicle).model == null)) || ((model != null) && (((Vehicle) theVehicle).model != null) && model.equals(((Vehicle) theVehicle).model))) {
                    if (((manufacturer == null) && (((Vehicle) theVehicle).manufacturer == null)) || ((manufacturer != null) && (((Vehicle) theVehicle).manufacturer != null) && manufacturer.equals(((Vehicle) theVehicle).manufacturer))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static Class getClassForRow(Record databaseRow) {
        return Car.class;
    }
}
