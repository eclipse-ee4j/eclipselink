/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.models.jpa.xml.merge.relationships;

import javax.persistence.*;
import java.util.Collection;
import static javax.persistence.GenerationType.*;

@Entity(name="XMLMergeItem")
@Table(name="CMP3_XML_MERGE_ITEM")
/*@NamedQuery(
	name="findAllXMLMergeItemsByName",
	query="SELECT OBJECT(item) FROM XMLMergeItem item WHERE item.name = ?1"
)*/
public class Item implements java.io.Serializable {
	private Integer itemId;
	private int version;
	private String name;
	private String description;
    private byte[] image;
    private Order order;
    private Collection<PartsList> partsLists;

	public Item() {}

	@Id
    @GeneratedValue(strategy=TABLE, generator="XML_MERGE_ITEM_TABLE_GENERATOR")
    // This table generator is overridden in the XML, therefore it should
    // not be processed. If it is processed, because the table name is so long
    // it will cause an error. No error means everyone is happy.
    @TableGenerator(
        name="XML_MERGE_ITEM_TABLE_GENERATOR", 
        table="CMP3_XML_MERGE_CUSTOMER_SEQ_INCORRECT_LONG_NAME_WILL_CAUSE_ERROR", 
        pkColumnName="SEQ_NAME", 
        valueColumnName="SEQ_COUNT",
        pkColumnValue="ITEM_SEQ"
    )
	@Column(name="ITEM_ID")
    public Integer getItemId() { 
        return itemId; 
    }
    
    public void setItemId(Integer id) { 
        this.itemId = id; 
    }

	@Version
	@Column(name="ITEM_VERSION")
	protected int getVersion() { 
        return version; 
    }
    
	protected void setVersion(int version) { 
        this.version = version; 
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
    
    @OneToOne(mappedBy="item")
    public Order getOrder() {
    	return order;
    }
    
    public void setOrder(Order newOrder) {
    	order = newOrder;
    }
    
    @ManyToMany(mappedBy="items")
    public Collection<PartsList> getPartsLists() {
    	return partsLists;
    }
    
    public void setPartsLists(Collection<PartsList> partsLists) {
    	this.partsLists = partsLists;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
    
    public byte[] getImage() {
        return image;
    }
}
