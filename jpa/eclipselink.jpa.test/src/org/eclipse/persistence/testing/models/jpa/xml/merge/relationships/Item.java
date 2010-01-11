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
package org.eclipse.persistence.testing.models.jpa.xml.merge.relationships;

import javax.persistence.*;
import java.util.Collection;

import org.eclipse.persistence.annotations.*;

import static javax.persistence.GenerationType.*;

/**
 * This class is mapped in the following file:
 *  - eclipselink.jpa.test\resource\eclipselink-xml-merge-model\orm-annotation-merge-relationships-entity-mappings.xml
 * 
 * Its equivalent testing file is:
 *  - org.eclipse.persistence.testing.tests.jpa.xml.merge.relationships.EntityMappingsMergeRelationshipsJUnitTestCase  
 */
@Entity(name="XMLMergeItem")
@Table(name="CMP3_XML_MERGE_ITEM")
public class Item implements java.io.Serializable {
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
	private Integer itemId;
	
	private int version;
	public String name;
	private String description;
    private byte[] image;
    private Order order;
    private Collection<PartsList> partsLists;

	public Item() {}

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

	/**
	 * Description is mapped in XML, therefore, the Temporal annotation should 
     * be ignored. If it is not and is processed, the metadata processing will 
     * throw an exception (invalid temporal type).
	 */
	@Temporal(TemporalType.DATE)
	public String getDescription() { 
        return description; 
    }
    
    public void setDescription(String desc) { 
        this.description = desc; 
    }

    /**
     * Name is mapped in XML and is not marked as mutable. Therefore, the 
     * Mutable annotation should be ignored. There is a JUnit test to verify 
     * the setting.
     */
    @Mutable
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
