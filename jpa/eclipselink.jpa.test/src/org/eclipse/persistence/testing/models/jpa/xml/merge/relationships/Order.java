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

import static javax.persistence.GenerationType.*;
import static javax.persistence.CascadeType.*;
import static javax.persistence.FetchType.*;

/**
 * This class is mapped in the following file:
 *  - eclipselink.jpa.test\resource\eclipselink-xml-merge-model\orm-annotation-merge-relationships-entity-mappings.xml
 * 
 * Its equivalent testing file is:
 *  - org.eclipse.persistence.testing.tests.jpa.xml.merge.relationships.EntityMappingsMergeRelationshipsJUnitTestCase  
 */
@Entity(name="XMLMergeOrderBean")
@Table(name="CMP3_XML_MERGE_ORDER")
public class Order implements java.io.Serializable {
	private Integer orderId;
	private int version;
	private Item item;
	private int quantity;
	private String shippingAddress;
	private Customer customer;
	
	public Order() {}

	@Id
    @GeneratedValue(strategy=TABLE, generator="XML_MERGE_ORDER_TABLE_GENERATOR")
    // This table generator is overridden in the XML, therefore it should
    // not be processed. If it is processed, because the table name is so long
    // it will cause an error. No error means everyone is happy.
	@TableGenerator(
        name="XML_MERGE_ORDER_TABLE_GENERATOR", 
        table="CMP3_XML_MERGE_CUSTOMER_SEQ_INCORRECT_LONG_NAME_WILL_CAUSE_ERROR", 
        pkColumnName="SEQ_NAME", 
        valueColumnName="SEQ_COUNT",
        pkColumnValue="ORDER_SEQ"
    )
	@Column(name="ORDER_ID")
	public Integer getOrderId() { 
        return orderId; 
    }
    
	public void setOrderId(Integer id) { 
        this.orderId = id; 
    }

	@Version
	@Column(name="ORDER_VERSION")
	protected int getVersion() { 
        return version; 
    }
    
	protected void setVersion(int version) { 
        this.version = version; 
    }

	@OneToOne(cascade=PERSIST, fetch=LAZY)
	@JoinColumn(name="ITEM_ID", referencedColumnName="ITEM_ID")
	public Item getItem() { 
        return item; 
    }
    
	public void setItem(Item item) { 
        this.item = item; 
    }

	/**
	 * Quantity is mapped in XML as follows:
     *  <basic name="quantity"/>
     *
     * Note, no column definition meaning it should default to QUANTITY.
     * The Column annotation below should be ignored. If it is not and is
     * processed, the existing tests for this model will fail.
	 * 
	 */
	@Column(name="INVALID_QUANTITY_FIELD")
	public int getQuantity() { 
        return quantity; 
    }
    
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	@Column(name="SHIP_ADDR")	
	public String getShippingAddress() { 
        return shippingAddress; 
    }
    
	public void setShippingAddress(String shippingAddress) {
		this.shippingAddress = shippingAddress;
	}

	/**
	 * Customer is mapped in XML as follows:
     *  <many-to-one name="customer" target-entity="Customer" fetch="LAZY"/>
     *    
     * Note, no join columns are specified and therefore they should default 
     * and the annotation should be ignored. If JoinColumn is processed, the 
     * model will be processed incorrectly and the existing tests for this
     * model will fail. 
	 */
	@ManyToOne(fetch=EAGER)
	@JoinColumn(name="INVALID_FK", referencedColumnName="INVALID_PK")
	public Customer getCustomer() { 
        return customer; 
    }
    
	public void setCustomer(Customer customer) { 
        this.customer = customer; 
    }
}
