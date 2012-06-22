/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     02/19/09 dminsky - initial API and implementation
 ******************************************************************************/ 
package org.eclipse.persistence.testing.models.jpa.privateowned;

import java.util.*;
import javax.persistence.*;

import org.eclipse.persistence.annotations.*;

@Entity(name="PO_Chassis")
@Table(name="CMP3_PO_CHASSIS")
public class Chassis {
    
    @Id
    @GeneratedValue(strategy=GenerationType.TABLE, generator="CMP3_PO_CHASSIS_TABLE_GENERATOR")
    @TableGenerator(
        name="CMP3_PO_CHASSIS_TABLE_GENERATOR", 
        table="CMP3_PRIVATE_OWNED_SEQUENCE",
        pkColumnName="SEQ_NAME", 
        valueColumnName="SEQ_COUNT",
        pkColumnValue="CHASSIS_SEQ"
    )
    protected int id;
    
    protected long serialNumber;
    
    @OneToMany (cascade=CascadeType.ALL, mappedBy="chassis")
    @PrivateOwned
    protected List<Wheel> wheels; // private-owned 1:M

    @OneToMany(cascade=CascadeType.ALL, mappedBy="chassis")
    @PrivateOwned
    protected List<Mount> vehicleMounts;
    
    public Chassis() {
        super();
        this.wheels = new ArrayList<Wheel>();
        this.vehicleMounts = new ArrayList<Mount>();
    }
    
    public Chassis(long serialNumber) {
        this();
        this.serialNumber = serialNumber;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(long serialNumber) {
        this.serialNumber = serialNumber;
    }
    
    public List<Wheel> getWheels() {
        return wheels;
    }

    public void setWheels(List<Wheel> wheels) {
        this.wheels = wheels;
    }
    
    public void addWheel(Wheel wheel) {
        getWheels().add(wheel);
        wheel.setChassis(this);
    }
    
    public void removeWheel(Wheel wheel) {
        getWheels().remove(wheel);
        wheel.setChassis(null);
    }

    public List<Mount> getVehicleMounts() {
        return vehicleMounts;
    }

    public void setVehicleMounts(List<Mount> vehicleMounts) {
        this.vehicleMounts = vehicleMounts;
    }
    
    public void addMount(Mount mount) {
        getVehicleMounts().add(mount);
        mount.setChassis(this);
    }
    
    public void removeMount(Mount mount) {
        getVehicleMounts().remove(mount);
        mount.setChassis(null);
    }
    
}
