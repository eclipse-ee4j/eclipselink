/*
 * Copyright (c) 2011, 2025 Oracle and/or its affiliates. All rights reserved.
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
//     03/04/09 tware - test for bug 350599 copied from advanced model
package org.eclipse.persistence.testing.models.jpa.xml.advanced;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.TableGenerator;
import org.eclipse.persistence.annotations.ReadOnly;

import static jakarta.persistence.GenerationType.TABLE;


@Entity(name = "XMLMergeEquipmentCode")
@Table(name="CMP3_MERGE_EQUIP_CODE")
@ReadOnly
public class EquipmentCode  {
    private Integer id;
    private String code;

    public EquipmentCode() {}

    public String getCode() {
        return code;
    }

    @Id
    @GeneratedValue(strategy=TABLE, generator="EQUIPMENT_CODE_TABLE_GENERATOR")
    @TableGenerator(
        name="EQUIPMENT_CODE_TABLE_GENERATOR",
        table="CMP3_ADV_EQUIPMENT_CODE_SEQ",
        pkColumnName="SEQ_NAME",
        valueColumnName="SEQ_COUNT",
        pkColumnValue="CODE_SEQ"
    )
    public Integer getId() {
        return id;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
