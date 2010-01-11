/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.models.jpa.xml.merge.incompletemappings.nonowning;

import javax.persistence.*;
import java.util.Collection;

import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.GenerationType.*;

@Entity(name="XMLIncompletePartsList")
@Table(name="CMP3_XML_INC_PARTSLIST")
public class PartsList implements java.io.Serializable {
	private Integer partsListId;
	private int version;
	private Collection<Item> items;

	public PartsList() {}

	@Id
    @GeneratedValue(strategy=TABLE, generator="XML_INCOMPLETE_PARTSLIST_TABLE_GENERATOR")
    @TableGenerator(
        name="XML_INCOMPLETE_PARTSLIST_TABLE_GENERATOR", 
        table="CMP3_XML_INC_CUSTOMER_SEQ", 
        pkColumnName="SEQ_NAME", 
        valueColumnName="SEQ_COUNT",
        pkColumnValue="PARTSLIST_SEQ"
    )
	@Column(name="ID")
    public Integer getPartsListId() { 
        return partsListId; 
    }
    
    public void setPartsListId(Integer id) { 
        this.partsListId = id; 
    }

	@Version
	@Column(name="VERSION")
	protected int getVersion() { 
        return version; 
    }
    
	protected void setVersion(int version) { 
        this.version = version; 
    }
	
	@ManyToMany(cascade=PERSIST)
    @JoinTable(
		name="CMP3_XML_INC_PARTSLIST_ITEM",
        joinColumns=@JoinColumn(name="PARTSLIST_ID", referencedColumnName="ID"),
		inverseJoinColumns=@JoinColumn(name="ITEM_ID", referencedColumnName="ITEM_ID")
	)
	public Collection<Item> getItems() { 
        return items; 
    }
    
	public void setItems(Collection<Item> items) {
		this.items = items;
	}
}
