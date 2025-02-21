/*
 * Copyright (c) 2025 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation
package org.eclipse.persistence.testing.models.jpa.deadlock.diagnostic.weaving.impl;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import org.eclipse.persistence.testing.models.jpa.deadlock.diagnostic.weaving.EquipmentCacheKey;
import org.eclipse.persistence.testing.models.jpa.deadlock.diagnostic.weaving.PortCacheKey;

@Entity
@Table(name="PortCacheKey")
public class PortCacheKeyDAO implements PortCacheKey, java.io.Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "JDOID")
    @GeneratedValue(strategy=GenerationType.IDENTITY, generator="PKGen")
    private long entityId;

    @ManyToOne(targetEntity= EquipmentCacheKeyDAO.class)
    @JoinColumn(name="equipment")
    private EquipmentCacheKeyDAO equipment;

    @Column(name="portOrder")
    private int portOrder;

    @ManyToOne(targetEntity= EquipmentCacheKeyDAO.class)
    @JoinColumn(name="virtualEquipment")
    private EquipmentCacheKeyDAO virtualEquipment;

    private String id;

    @Column(name="JDOVERSION")
    @Version
    int entityVersion;

    @Override
    public int getPortCacheKeyOrder() {
        return portOrder;
    }

    @Override
    public void setPortCacheKeyOrder(int portOrder) {
        this.portOrder = portOrder;
    }

    @Override
    public long getEntityId() {
        return entityId;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id ) {
        this.id = id;
    }

    @Override
    public EquipmentCacheKey getEquipmentCacheKey() {
        return equipment;
    }

    @Override
    public void setEquipmentCacheKey(EquipmentCacheKey equipmentCacheKey) {
        this.equipment = (EquipmentCacheKeyDAO) equipmentCacheKey;
    }

    @Override
    public EquipmentCacheKey getVirtualEquipmentCacheKey() {
        return virtualEquipment;
    }

    @Override
    public void setVirtualEquipmentCacheKey(EquipmentCacheKey virtualEquipmentCacheKey) {
        this.virtualEquipment = (EquipmentCacheKeyDAO) virtualEquipmentCacheKey;
    }

    @Override
    public String toString() {
        return "PortDAO{" +
                "entityId=" + entityId +
                ", portOrder=" + portOrder +
                ", id='" + id + '\'' +
                ", entityVersion=" + entityVersion +
                '}';
    }
}
