/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/
package org.eclipse.persistence.testing.models.jpa.composite.advanced.member_1;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedNativeQueries;

import static javax.persistence.GenerationType.TABLE;

import org.eclipse.persistence.annotations.ReadOnly;

@Entity
@Table(name="MBR1_ADV_EQUIP_CODE")
@ReadOnly
@NamedNativeQueries({
    @NamedNativeQuery(
        name="findSQLEquipmentCodeA", 
        query="select * from MBR1_ADV_EQUIP_CODE where CODE='A'",
        resultClass=org.eclipse.persistence.testing.models.jpa.composite.advanced.member_1.EquipmentCode.class),
    @NamedNativeQuery(
        name="findSQLEquipmentCodeB", 
        query="select * from MBR1_ADV_EQUIP_CODE where CODE='B'",
        resultClass=org.eclipse.persistence.testing.models.jpa.composite.advanced.member_1.EquipmentCode.class),
    @NamedNativeQuery(
        name="findSQLEquipmentCodeC", 
        query="select * from MBR1_ADV_EQUIP_CODE where CODE='C'",
        resultClass=org.eclipse.persistence.testing.models.jpa.composite.advanced.member_1.EquipmentCode.class)
})
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
        table="MBR1_SEQ", 
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
