/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import static javax.persistence.CascadeType.DETACH;
import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.CascadeType.REFRESH;
import static javax.persistence.GenerationType.TABLE;

@Entity(name="OR_Vehicle")
@Table(name="JPA_OR_VEHICLE")
public class Vehicle {
    @Id
    @GeneratedValue(strategy=TABLE, generator="JPA_OR_VEHICLE_TABLE_GENERATOR")
    @TableGenerator(
        name="JPA_OR_VEHICLE_TABLE_GENERATOR", 
        table="JPA_ORPHAN_REMOVAL_SEQUENCE",
        pkColumnName="SEQ_NAME", 
        valueColumnName="SEQ_COUNT",
        pkColumnValue="VEHICLE_SEQ"
    )
    protected int id;
    
    protected String model;
    
    @OneToOne(cascade={DETACH, MERGE, PERSIST, REFRESH}, orphanRemoval=true)
    protected Chassis chassis; // orphanRemoval 1:1
    
    @OneToOne(orphanRemoval=true)
    protected Engine engine; // orphanRemoval 1:1 with no cascade options.

    public Vehicle() {
        super();
    }
    
    public Vehicle(String model) {
        this();
        this.model = model;
    }
    
    public Chassis getChassis() {
        return chassis;
    }

    public Engine getEngine() {
        return engine;
    }
    
    public int getId() {
        return id;
    }

    public String getModel() {
        return model;
    }

    public void setChassis(Chassis chassis) {
        this.chassis = chassis;
    }

    public void setEngine(Engine engine) {
        this.engine = engine;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public void setModel(String model) {
        this.model = model;
    }
    
    public String toString() {
        return "Vehicle ["+ id +"]";
    }
}
