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
package org.eclipse.persistence.testing.models.jpa.relationships;

import javax.persistence.*;
import static javax.persistence.GenerationType.*;

@Entity
@Table(name="CMP3_ISOLATED_ITEM")
public class IsolatedItem implements java.io.Serializable {
	private Integer itemId;
	private String name;
	private String description;

	public IsolatedItem() {}

	@Id
    @GeneratedValue(strategy=TABLE, generator="ISOLATED_ITEM_TABLE_GENERATOR")
    @TableGenerator(
        name="ISOLATED_ITEM_TABLE_GENERATOR", 
        table="CMP3_CUSTOMER_SEQ", 
        pkColumnName="SEQ_NAME", 
        valueColumnName="SEQ_COUNT",
        pkColumnValue="ISOLATED_ITEM_SEQ"
    )
	@Column(name="ID")
    public Integer getItemId() { 
        return itemId; 
    }
    
    public void setItemId(Integer id) { 
        this.itemId = id; 
    }

	public String getDescription() { 
        return description; 
    }
    
    public void setDescription(String desc) { 
        this.description = desc; 
    }

    public String getName() { 
        return name; 
    }
    
    public void setName(String name) {
        this.name = name; 
    }
}
