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
 *     02/19/09 dminsky - initial API and implementation
 ******************************************************************************/ 
package org.eclipse.persistence.testing.models.jpa.privateowned;

import javax.persistence.*;

import java.util.*;

@Entity(name="PO_Wheel")
@Table(name="CMP3_PO_WHEEL")
public class Wheel {
    
    @Id
    @GeneratedValue(strategy=GenerationType.TABLE, generator="CMP3_PO_WHEEL_TABLE_GENERATOR")
    @TableGenerator(
        name="CMP3_PO_WHEEL_TABLE_GENERATOR", 
        table="CMP3_PRIVATE_OWNED_SEQUENCE",
        pkColumnName="SEQ_NAME", 
        valueColumnName="SEQ_COUNT",
        pkColumnValue="WHEEL_SEQ"
    )
    protected int id;
    
    @OneToOne (cascade=CascadeType.ALL)
    protected WheelRim wheelRim; // non-private-owned 1:1

    @Embedded
    protected Tire tire; // embedded (aggregate)
    
    protected long serialNumber;

    @ManyToOne
    protected Chassis chassis;
    
    @OneToMany (cascade=CascadeType.ALL, mappedBy="wheel")
    protected List<WheelNut> wheelNuts; // non-private-owned 1:M
    
    public Wheel() {
        super();
        this.wheelNuts = new ArrayList<WheelNut>();
    }
    
    public Wheel(long serialNumber) {
        this();
        this.serialNumber = serialNumber;
    }

    public long getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(long serialNumber) {
        this.serialNumber = serialNumber;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public WheelRim getWheelRim() {
        return wheelRim;
    }

    public void setWheelRim(WheelRim wheelRim) {
        this.wheelRim = wheelRim;
    }

    public Tire getTire() {
        return tire;
    }

    public void setTire(Tire tire) {
        this.tire = tire;
    }

    public Chassis getChassis() {
        return chassis;
    }

    public void setChassis(Chassis chassis) {
        this.chassis = chassis;
    }

    public List<WheelNut> getWheelNuts() {
        return wheelNuts;
    }

    public void setWheelNuts(List<WheelNut> wheelNuts) {
        this.wheelNuts = wheelNuts;
    }
    
    public void addWheelNut(WheelNut wheelNut) {
        getWheelNuts().add(wheelNut);
        wheelNut.setWheel(this);
    }
    
    public void removeWheelNut(WheelNut wheelNut) {
        getWheelNuts().remove(wheelNut);
        wheelNut.setWheel(null);
    }

}
