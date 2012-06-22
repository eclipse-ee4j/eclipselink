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

import javax.persistence.*;

import org.eclipse.persistence.annotations.PrivateOwned;

@Entity(name="PO_Vehicle")
@Table(name="CMP3_PO_VEHICLE")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorValue("V")
public class Vehicle {
    
    @Id
    @GeneratedValue(strategy=GenerationType.TABLE, generator="CMP3_PO_VEHICLE_TABLE_GENERATOR")
    @TableGenerator(
        name="CMP3_PO_VEHICLE_TABLE_GENERATOR", 
        table="CMP3_PRIVATE_OWNED_SEQUENCE",
        pkColumnName="SEQ_NAME", 
        valueColumnName="SEQ_COUNT",
        pkColumnValue="VEHICLE_SEQ"
    )
    protected int id;
    
    protected String model;
    
    @OneToOne (fetch=FetchType.LAZY, cascade=CascadeType.ALL)
    @PrivateOwned
    protected Chassis chassis; // private-owned 1:1
    
    @OneToOne (cascade=CascadeType.ALL)
    protected Engine engine; // non-private-owned 1:1

    public Vehicle() {
        super();
    }
    
    public Vehicle(String model) {
        this();
        this.model = model;
    }
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Chassis getChassis() {
        return chassis;
    }

    public void setChassis(Chassis chassis) {
        this.chassis = chassis;
    }

    public Engine getEngine() {
        return engine;
    }

    public void setEngine(Engine engine) {
        this.engine = engine;
    }
    
}
