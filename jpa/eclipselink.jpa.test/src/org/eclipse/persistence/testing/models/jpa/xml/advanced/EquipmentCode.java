/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     03/04/09 tware - test for bug 350599 copied from advanced model
 ******************************************************************************/ 
package org.eclipse.persistence.testing.models.jpa.xml.advanced;

import static javax.persistence.GenerationType.TABLE;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.eclipse.persistence.annotations.ReadOnly;


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
