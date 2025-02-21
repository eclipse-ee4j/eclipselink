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

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.persistence.TableGenerator;
import jakarta.persistence.Version;

import org.eclipse.persistence.annotations.PrivateOwned;
import org.eclipse.persistence.testing.models.jpa.deadlock.diagnostic.weaving.EquipmentCacheKey;
import org.eclipse.persistence.testing.models.jpa.deadlock.diagnostic.weaving.PortCacheKey;

import java.util.ArrayList;

@Entity
@Table(name="EquipmentCacheKey")
@NamedQueries({
    @NamedQuery(name="EquipmentCacheKey.findEquipmentById", query="select o from EquipmentCacheKeyDAO o where o.id = :id")
})
public class EquipmentCacheKeyDAO implements EquipmentCacheKey, java.io.Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "JDOID")
    @GeneratedValue(strategy=GenerationType.IDENTITY, generator="PKGen")
    @TableGenerator(
            name="PKGen",
            table="JDO_SEQUENCE_CACHE_KEY",
            pkColumnName="ID",
            pkColumnValue="jdoid",
            valueColumnName="SEQUENCE_VALUE",
            initialValue=0,
            allocationSize=50)
    protected long entityId;

    private String id;

    @OneToMany(cascade={CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.REMOVE},
            fetch=FetchType.LAZY,
            targetEntity= PortCacheKeyDAO.class,
            mappedBy="equipment")
    @PrivateOwned
    @OrderBy("portOrder ASC")
    private java.util.List<PortCacheKey> portCacheKeys = new ArrayList<>();

    @Column(name="JDOVERSION")
    @Version
    int entityVersion;

    public EquipmentCacheKeyDAO() {}

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
    public java.util.List<PortCacheKey> getPorts() {
            return portCacheKeys;
    }

    @Override
    public void setPortCacheKeys(ArrayList<PortCacheKey> portCacheKeys) {
        this.portCacheKeys = portCacheKeys;
    }

    @Override
    public void addPortCacheKey(PortCacheKey p) {
        getPorts().add(p);
        p.setEquipmentCacheKey(this);
    }

    @Override
    public PortCacheKey removePort(int i) {
        PortCacheKey portCacheKey = getPorts().remove(i);

        if (portCacheKey != null) {
            portCacheKey.setEquipmentCacheKey(null);
        }

        return portCacheKey;
    }

    @Override
    public String toString() {
        return "EquipmentDAO{" +
                "entityId=" + entityId +
                ", id='" + id + '\'' +
                ", entityVersion=" + entityVersion +
                '}';
    }
}
