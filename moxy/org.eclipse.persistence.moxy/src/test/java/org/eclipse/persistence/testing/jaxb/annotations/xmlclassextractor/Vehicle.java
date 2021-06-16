/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
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
// Oracle - 2.2 - Initial implementation
package org.eclipse.persistence.testing.jaxb.annotations.xmlclassextractor;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

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
