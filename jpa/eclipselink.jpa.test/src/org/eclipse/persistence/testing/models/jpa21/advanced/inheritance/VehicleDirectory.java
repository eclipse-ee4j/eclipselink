/*
 * Copyright (c) 2019 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2019 IBM Corporation. All rights reserved.
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
package org.eclipse.persistence.testing.models.jpa21.advanced.inheritance;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="JPA21_VEHICLE_DIRECTORY")
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
