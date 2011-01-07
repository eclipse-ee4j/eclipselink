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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/
package org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedNativeQueries;

import static javax.persistence.GenerationType.TABLE;

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
public class EquipmentCode  {
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
