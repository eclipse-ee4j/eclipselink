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
 *     tware - test for bug 312146
 ******************************************************************************/
package org.eclipse.persistence.testing.models.jpa.inheritance;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.Table;

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
