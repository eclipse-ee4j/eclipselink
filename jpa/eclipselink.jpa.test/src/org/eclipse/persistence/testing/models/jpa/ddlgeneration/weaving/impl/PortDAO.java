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
 *     01/12/2009-1.1 Daniel Lo
 *       - 247041: Null element inserted in the ArrayList 
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.ddlgeneration.weaving.impl;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import org.eclipse.persistence.testing.models.jpa.ddlgeneration.weaving.Equipment;
import org.eclipse.persistence.testing.models.jpa.ddlgeneration.weaving.Port;

@Entity
@Table(name="Port")
public class PortDAO implements Port, java.io.Serializable {
    private static final long serialVersionUID = 1L;
 
    @Id
    @Column(name = "JDOID")
    @GeneratedValue(strategy=GenerationType.IDENTITY, generator="PKGen")
    private long entityId;

    @ManyToOne(targetEntity=EquipmentDAO.class)
    @JoinColumn(name="equipment")
    private EquipmentDAO equipment;
    
    @Column(name="portOrder")
    private int portOrder;

    @ManyToOne(targetEntity=EquipmentDAO.class)
    @JoinColumn(name="virtualEquipment")
    private EquipmentDAO virtualEquipment;

    private java.lang.String id; 
    
    @Column(name="JDOVERSION")
    @Version
    int entityVersion;

    public int getPortOrder() {
        return portOrder;
    }
    
    public void setPortOrder(int portOrder) {
        this.portOrder = portOrder;
    }
    
    public long getEntityId() {
        return entityId;
    }

    public String getId() {
        return id;
    }
    
    public void setId( String id ) {
        this.id = id;
    }

    public Equipment getEquipment() {
		return equipment;
	}
    
	public void setEquipment(Equipment equipment) {
		this.equipment = (EquipmentDAO) equipment;
	}
    
    public Equipment getVirtualEquipment() {
        return virtualEquipment;
    }
    
    public void setVirtualEquipment(Equipment virtualEquipment) {
        this.virtualEquipment = (EquipmentDAO) virtualEquipment;
    }
}
