/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     05/1/2009-2.0 Guy Pelletier/David Minsky
 *       - 249033: JPA 2.0 Orphan removal
 ******************************************************************************/ 
package org.eclipse.persistence.testing.models.jpa.orphanremoval;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import static javax.persistence.CascadeType.DETACH;
import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.CascadeType.REFRESH;

import static javax.persistence.GenerationType.TABLE;

@Entity(name="OR_Chassis")
@Table(name="JPA_OR_CHASSIS")
public class Chassis {
    @Id
    @GeneratedValue(strategy=TABLE, generator="JPA_OR_CHASSIS_TABLE_GENERATOR")
    @TableGenerator(
        name="JPA_OR_CHASSIS_TABLE_GENERATOR", 
        table="JPA_ORPHAN_REMOVAL_SEQUENCE",
        pkColumnName="SEQ_NAME", 
        valueColumnName="SEQ_COUNT",
        pkColumnValue="CHASSIS_SEQ"
    )
    protected int id;
    
    protected long serialNumber;
    
    @OneToMany (cascade={DETACH, MERGE, PERSIST, REFRESH}, mappedBy="chassis", orphanRemoval=true)
    protected List<Wheel> wheels; // orphanRemoval 1:M

    public Chassis() {
        super();
        this.wheels = new ArrayList<Wheel>();
    }
    
    public Chassis(long serialNumber) {
        this();
        this.serialNumber = serialNumber;
    }

    public void addWheel(Wheel wheel) {
        getWheels().add(wheel);
        wheel.setChassis(this);
    }
    
    public int getId() {
        return id;
    }

    public long getSerialNumber() {
        return serialNumber;
    }
    
    public List<Wheel> getWheels() {
        return wheels;
    }

    public void removeWheel(Wheel wheel) {
        getWheels().remove(wheel);
        wheel.setChassis(null);
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public void setSerialNumber(long serialNumber) {
        this.serialNumber = serialNumber;
    }
    
    public void setWheels(List<Wheel> wheels) {
        this.wheels = wheels;
    }
    
    public String toString() {
        return "Chassis ["+ id +"]";
    }
}
