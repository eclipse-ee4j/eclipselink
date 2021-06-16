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

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.TableGenerator;
import jakarta.persistence.NamedNativeQuery;
import jakarta.persistence.NamedNativeQueries;

import static jakarta.persistence.GenerationType.TABLE;

import org.eclipse.persistence.annotations.ReadOnly;

@Entity(name="EquipmentCode")
@Table(name="CMP3_FA_ADV_EQUIP_CODE")
@ReadOnly
@NamedNativeQueries({
    @NamedNativeQuery(
        name="findSQLEquipmentCodeA",
        query="select * from CMP3_FA_ADV_EQUIP_CODE where CODE='A'",
        resultClass=org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced.EquipmentCode.class),
    @NamedNativeQuery(
        name="findSQLEquipmentCodeB",
        query="select * from CMP3_FA_ADV_EQUIP_CODE where CODE='B'",
        resultClass=org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced.EquipmentCode.class),
    @NamedNativeQuery(
        name="findSQLEquipmentCodeC",
        query="select * from CMP3_FA_ADV_EQUIP_CODE where CODE='C'",
        resultClass=org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced.EquipmentCode.class)
})
public class EquipmentCode implements Serializable  {
    @Id
    @GeneratedValue(strategy=TABLE, generator="FA_EQUIP_CODE_TABLE_GENERATOR")
    @TableGenerator(
        name="FA_EQUIP_CODE_TABLE_GENERATOR",
        table="CMP3_FA_ADV_EQUIP_CODE_SEQ",
        pkColumnName="SEQ_NAME",
        valueColumnName="SEQ_COUNT",
        pkColumnValue="CODE_SEQ"
    )
    private Integer id;
    private String code;

    public EquipmentCode() {}

    public String getCode() {
        return code;
    }

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
