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
//     tware - test for bug 312146
package org.eclipse.persistence.testing.models.jpa.inheritance;

import java.util.HashMap;
import java.util.Map;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.MapKey;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name="CMP3_VEHICLE_DIRECTORY")
public class VehicleDirectory {

    private int id;
    private Map<Company, Vehicle> vehicleDirectory;
    private String name;

    public VehicleDirectory(){
        vehicleDirectory = new HashMap<Company, Vehicle>();
    }

    @Id
    @GeneratedValue
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @OneToMany(mappedBy="directory")
    @MapKey(name="owner")
    public Map<Company, Vehicle> getVehicleDirectory() {
        return vehicleDirectory;
    }
    public void setVehicleDirectory(Map<Company, Vehicle> vehicleDirectory) {
        this.vehicleDirectory = vehicleDirectory;
    }


}
