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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedNativeQuery;
import jakarta.persistence.Table;
import jakarta.persistence.TableGenerator;

import static jakarta.persistence.GenerationType.TABLE;

@Entity(name="Equipment")
@Table(name="CMP3_FA_ADV_EQUIP")
@NamedNativeQuery(
    name="findAllSQLEquipment",
    query="select * from CMP3_FA_ADV_EQUIP",
    resultClass=org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced.Equipment.class
)
public class Equipment implements Serializable  {
    @Id
    @GeneratedValue(strategy=TABLE, generator="FA_EQUIP_TABLE_GENERATOR")
    @TableGenerator(
        name="FA_EQUIP_TABLE_GENERATOR",
        table="CMP3_FA_ADV_EQUIP_SEQ",
        pkColumnName="SEQ_NAME",
        valueColumnName="SEQ_COUNT",
        pkColumnValue="EQUIP_SEQ"
    )
    private Integer id;
    @Column(name="DESCRIP")
    private String description;
    @ManyToOne
    @JoinColumn(name="DEPT_ID")
    private Department department;
    @ManyToOne
    @JoinColumn(name="CODE_ID")
    private EquipmentCode equipmentCode;

    public Equipment() {}

    public Department getDepartment() {
        return department;
    }

    public String getDescription() {
        return description;
    }

    public EquipmentCode getEquipmentCode() {
        return equipmentCode;
    }

    public Integer getId() {
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

    public void setId(Integer id) {
        this.id = id;
    }
}
