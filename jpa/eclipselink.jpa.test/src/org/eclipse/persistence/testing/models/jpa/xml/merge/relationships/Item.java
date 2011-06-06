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
 *     09/23/2008-1.1 Guy Pelletier 
 *       - 241651: JPA 2.0 Access Type support
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.xml.merge.relationships;

import javax.persistence.*;
import java.util.Collection;

import org.eclipse.persistence.annotations.*;

import static javax.persistence.AccessType.PROPERTY;
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
    // Not mapped in XML. Field is access but itemId has been explicitly marked
    // as property access. The metadata from the method should get processed.
    private Integer itemId;
    
    // Mapped in XML, annotations should get ignored.
    @Version
    @Column(name="INVALID_ITEM_VERSION")
	private int version;
    
    // Name is mapped in XML and is not marked as mutable. Therefore, the 
    // Mutable annotation should be ignored. There is a JUnit test to verify 
    // the setting.
    @Mutable
	public String name;
    
    // Mapped in xml and as property. Unless we mark this field as transient, 
    // it will get processed into another mapping.
    @Transient
	private String desc;
	
    // Mapped in XML, annotations should get ignored.
    @Column(name="INVALID_IMAGE")
    public byte[] image;
    
    // Mapped in XML, annotations should get ignored.
    @OneToOne(mappedBy="invalid_item")
    private Order order;
    
    // Mapped in XML, annotations should get ignored.
    @ManyToMany(mappedBy="invalid_items")
    private Collection<PartsList> partsLists;

	public Item() {}

	/**
     * Description is mapped in XML, therefore, the Temporal annotation should 
     * be ignored. If it is not and is processed, the metadata processing will 
     * throw an exception (invalid temporal type).
     */
    @Temporal(TemporalType.DATE)
    public String getDescription() { 
        return desc; 
    }
    
    public byte[] getImage() {
        return image;
    }
    
    @Id
    @Access(PROPERTY)
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
    @Column(name="ID")
    public Integer getItemId() { 
        return itemId; 
    }
    
    public Order getOrder() {
        return order;
    }
    
    public Collection<PartsList> getPartsLists() {
        return partsLists;
    }
    
    public void setDescription(String desc) { 
        this.desc = desc; 
    }
    
    public void setImage(byte[] image) {
        this.image = image;
    }
    
    public void setItemId(Integer id) { 
        this.itemId = id; 
    }
    
    public void setOrder(Order newOrder) {
        order = newOrder;
    }
    
    public void setPartsLists(Collection<PartsList> partsLists) {
        this.partsLists = partsLists;
    }
}
