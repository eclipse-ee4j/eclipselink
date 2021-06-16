/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     01/12/2009-1.1 Daniel Lo
//       - 247041: Null element inserted in the ArrayList
package org.eclipse.persistence.testing.models.jpa.ddlgeneration.weaving.impl;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

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
