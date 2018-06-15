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
// dmccann - July 28/2010 - 2.2 - Initial implementation
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlclassextractor;

import org.eclipse.persistence.oxm.annotations.XmlClassExtractor;
import org.eclipse.persistence.sessions.Record;

@XmlClassExtractor(MyOtherClassExtractor.class)
public class Vehicle {
    public String model;
    public String manufacturer;
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
