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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.GenerationType.TABLE;

@Entity(name="OR_Wheel")
@Table(name="JPA_OR_WHEEL")
public class Wheel {
    @Id
    @GeneratedValue(strategy=TABLE, generator="JPA_OR_WHEEL_TABLE_GENERATOR")
    @TableGenerator(
        name="JPA_OR_WHEEL_TABLE_GENERATOR", 
        table="JPA_ORPHAN_REMOVAL_SEQUENCE",
        pkColumnName="SEQ_NAME", 
        valueColumnName="SEQ_COUNT",
        pkColumnValue="WHEEL_SEQ"
    )
    protected int id;
    
    @OneToOne(cascade=ALL)
    protected WheelRim wheelRim; // non-orphanRemoval 1:1

    @Embedded
    protected Tire tire; // embedded (aggregate)
    
    protected long serialNumber;

    @ManyToOne
    protected Chassis chassis;
    
    @OneToMany (cascade=ALL, mappedBy="wheel")
    protected List<WheelNut> wheelNuts; // non-orphanRemoval 1:M
    
    public Wheel() {
        super();
        this.wheelNuts = new ArrayList<WheelNut>();
    }
    
    public Wheel(long serialNumber) {
        this();
        this.serialNumber = serialNumber;
    }

    public void addWheelNut(WheelNut wheelNut) {
        getWheelNuts().add(wheelNut);
        wheelNut.setWheel(this);
    }
    
    public Chassis getChassis() {
        return chassis;
    }
    
    public int getId() {
        return id;
    }
    
    public long getSerialNumber() {
        return serialNumber;
    }

    public Tire getTire() {
        return tire;
    }
    
    public List<WheelNut> getWheelNuts() {
        return wheelNuts;
    }
    
    public WheelRim getWheelRim() {
        return wheelRim;
    }

    public void removeWheelNut(WheelNut wheelNut) {
        getWheelNuts().remove(wheelNut);
        wheelNut.setWheel(null);
    }

    public void setChassis(Chassis chassis) {
        this.chassis = chassis;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public void setSerialNumber(long serialNumber) {
        this.serialNumber = serialNumber;
    }

    public void setTire(Tire tire) {
        this.tire = tire;
    }

    public void setWheelNuts(List<WheelNut> wheelNuts) {
        this.wheelNuts = wheelNuts;
    }
    
    public void setWheelRim(WheelRim wheelRim) {
        this.wheelRim = wheelRim;
    }
    
    public String toString() {
        return "Wheel ["+ id +"]";
    }
}
