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
 *     03/04/09 tware - test for bug 350599 copied from advanced model
 ******************************************************************************/ 
package org.eclipse.persistence.testing.models.jpa.privateowned;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

@Entity(name="PO_Mount")
@Table(name="CMP3_PO_MOUNT")
@IdClass(MountPK.class)
public class Mount {

    @Id
    @GeneratedValue(strategy=GenerationType.TABLE, generator="CMP3_PO_MOUNT_TABLE_GENERATOR")
    @TableGenerator(
        name="CMP3_PO_MOUNT_TABLE_GENERATOR", 
        table="CMP3_PRIVATE_OWNED_SEQUENCE",
        pkColumnName="SEQ_NAME", 
        valueColumnName="SEQ_COUNT",
        pkColumnValue="MOUNT_SEQ"
    )
    private int id;
    
    @Id
    @Column(updatable=false, insertable=false)
    private int id2;

    @OneToOne
    private Vehicle vehicle;
    
    @ManyToOne
    @JoinColumn(name="ID2", referencedColumnName="ID")
    private Chassis chassis;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    public void setId2(int id2) {
        this.id2 = id2;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public Chassis getChassis() {
        return chassis;
    }

    public void setChassis(Chassis chassis) {
        this.chassis = chassis;
    }

    public int getId2() {
        return id2;
    }
}

