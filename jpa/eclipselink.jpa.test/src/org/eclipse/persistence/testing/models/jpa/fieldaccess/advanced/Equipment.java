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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedNativeQuery;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import static javax.persistence.GenerationType.TABLE;

@Entity(name="Equipment")
@Table(name="CMP3_FA_ADV_EQUIP")
@NamedNativeQuery(
    name="findAllSQLEquipment", 
    query="select * from CMP3_FA_ADV_EQUIP",
    resultClass=org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced.Equipment.class
)
public class Equipment  {
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
