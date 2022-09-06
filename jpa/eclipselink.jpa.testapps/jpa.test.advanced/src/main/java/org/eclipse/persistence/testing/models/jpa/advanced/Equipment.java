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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.models.jpa.advanced;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedNativeQuery;
import jakarta.persistence.Table;
import jakarta.persistence.TableGenerator;

import java.io.Serializable;

import static jakarta.persistence.GenerationType.TABLE;

@Entity
@Table(name="CMP3_ADV_EQUIP")
@NamedNativeQuery(
    name="findAllSQLEquipment",
    query="select * from CMP3_ADV_EQUIP",
    resultClass=org.eclipse.persistence.testing.models.jpa.advanced.Equipment.class
)
public class Equipment implements Serializable {
    private int id;
    private String description;
    private Department department;
    private EquipmentCode equipmentCode;

    public Equipment() {}

    @ManyToOne
    @JoinColumn(name="DEPT_ID")
    public Department getDepartment() {
        return department;
    }

    @Column(name="DESCRIP")
    public String getDescription() {
        return description;
    }

    @ManyToOne
    @JoinColumn(name="CODE_ID")
    public EquipmentCode getEquipmentCode() {
        return equipmentCode;
    }

    @Id
    @GeneratedValue(strategy=TABLE, generator="EQUIPMENT_TABLE_GENERATOR")
    @TableGenerator(
        name="EQUIPMENT_TABLE_GENERATOR",
        table="CMP3_ADV_EQUIPMENT_SEQ",
        pkColumnName="SEQ_NAME",
        valueColumnName="SEQ_COUNT",
        pkColumnValue="EQUIP_SEQ"
    )
    public int getId() {
        return id;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setEquipmentCode(EquipmentCode equipmentCode) {
        this.equipmentCode = equipmentCode;
    }

    public void setId(int id) {
        this.id = id;
    }
}
