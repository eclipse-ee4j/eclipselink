/*
 * Copyright (c) 2011, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     tware - test for bug 312146
package org.eclipse.persistence.testing.models.jpa.inheritance;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.MapKey;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name="CMP3_VEHICLE_DIRECTORY")
public class VehicleDirectory {

    private int id;
    private Map<Company, Vehicle> vehicleDirectory;
    private String name;

    public VehicleDirectory(){
        vehicleDirectory = new HashMap<>();
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
