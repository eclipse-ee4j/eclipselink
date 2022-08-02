/*
 * Copyright (c) 1998, 2022 Oracle and/or its affiliates. All rights reserved.
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
//     01/12/2009-1.1 Daniel Lo
//       - 247041: Null element inserted in the ArrayList
package org.eclipse.persistence.testing.models.jpa.ddlgeneration.weaving.impl;

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
import org.eclipse.persistence.testing.models.jpa.ddlgeneration.weaving.Equipment;
import org.eclipse.persistence.testing.models.jpa.ddlgeneration.weaving.Port;

import java.util.ArrayList;

@Entity
@Table(name="Equipment")
@NamedQueries({
    @NamedQuery(name="Equipment.findEquipmentById", query="select o from EquipmentDAO o where o.id = :id")
})
public class EquipmentDAO implements Equipment, java.io.Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "JDOID")
    @GeneratedValue(strategy=GenerationType.IDENTITY, generator="PKGen")
    @TableGenerator(
            name="PKGen",
            table="JDO_SEQUENCE",
            pkColumnName="ID",
            pkColumnValue="jdoid",
            valueColumnName="SEQUENCE_VALUE",
            initialValue=0,
            allocationSize=50)
    protected long entityId;

    private java.lang.String id;

    @OneToMany(cascade={CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.REMOVE},
            fetch=FetchType.LAZY,
            targetEntity=PortDAO.class,
            mappedBy="equipment")
    @PrivateOwned
    @OrderBy("portOrder ASC")
    private java.util.List<Port> ports = new ArrayList<>();

    @Column(name="JDOVERSION")
    @Version
    int entityVersion;

    public EquipmentDAO() {}

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
    public java.util.List<Port> getPorts() {
            return ports;
    }

    @Override
    public void setPorts(ArrayList<Port> ports ) {
        this.ports = ports;
    }

    @Override
    public void addPort(Port p) {
        getPorts().add(p);
        p.setEquipment(this);
    }

    @Override
    public Port removePort(int i) {
        Port port = getPorts().remove(i);

        if (port != null) {
            port.setEquipment(null);
        }

        return port;
    }
}
