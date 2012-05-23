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
 *     09/23/2008-1.1 Guy Pelletier 
 *       - 241651: JPA 2.0 Access Type support
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.xml.merge.relationships;

import java.util.Collection;
import javax.persistence.*;

import org.eclipse.persistence.annotations.PrivateOwned;

import static javax.persistence.AccessType.FIELD;
import static javax.persistence.AccessType.PROPERTY;
import static javax.persistence.GenerationType.*;
import static javax.persistence.CascadeType.*;

/**
 * This class is mapped in the following file:
 *  - eclipselink.jpa.test\resource\eclipselink-xml-merge-model\orm-annotation-merge-relationships-entity-mappings.xml
 * 
 * Its equivalent testing file is:
 *  - org.eclipse.persistence.testing.tests.jpa.xml.merge.relationships.EntityMappingsMergeRelationshipsJUnitTestCase  
 */
@Entity(name="XMLMergeCustomer")
@Table(name="CMP3_XML_MERGE_CUSTOMER")
@Access(PROPERTY)
public class Customer implements java.io.Serializable{
	private Integer customerId;
	
	@Access(FIELD)
	@Version
	@Column(name="CUST_VERSION")
	private int version;
	
	private String city;
	private String name;
	private Collection<Order> orders;

	public Customer() {}

    @Id
    @GeneratedValue(strategy=TABLE, generator="XML_MERGE_CUSTOMER_TABLE_GENERATOR")
    // This table generator is overridden in the XML, therefore it should
    // not be processed. If it is processed, because the table name is so long
    // it will cause an error. No error means everyone is happy.
	@TableGenerator(
        name="XML_MERGE_CUSTOMER_TABLE_GENERATOR", 
        table="CMP3_XML_MERGE_CUSTOMER_SEQ_INCORRECT_LONG_NAME_WILL_CAUSE_ERROR", 
        pkColumnName="SEQ_NAME", 
        valueColumnName="SEQ_COUNT",
        pkColumnValue="CUST_SEQ"
    )
	@Column(name="CUST_ID")
    public Integer getCustomerId() { 
        return customerId; 
    }
    
    public void setCustomerId(Integer id) { 
        this.customerId = id; 
    }

	/**
	 * City is mapped in XML, therefore, the Lob annotation should be ignored. 
     * If it is not and is processed, the metadata processing will throw an 
     * exception (invalid lob type).
	 */
	@Lob
	public String getCity() {
        return city; 
    }
    
    public void setCity(String aCity) { 
        this.city = aCity; 
    }

    /**
     * Name is mapped in XML, therefore, the Enumerated annotation should be 
     * ignored. If it is not and is processed, the metadata processing will 
     * throw an exception (invalid enumerated type).
     */
    @Enumerated
    public String getName() { 
        return name; 
    }
    
    public void setName(String aName) { 
        this.name = aName; 
    }

    /**
     * Order is mapped in XML as follows:
     *  <one-to-many name="orders" target-entity="Order" mapped-by="customer">
     *    <cascade>
     *      <cascade-persist/>
     *      <cascade-remove/>
     *    </cascade>
     * </one-to-many>
     * The annotations should be ignored. If OrderBy is processed, the value
     * will cause an exception during processing.
     * 
     */
	@OneToMany(cascade=ALL, mappedBy="customer")
	@PrivateOwned
	@OrderBy("valueThatShouldCauseAnException")
    public Collection<Order> getOrders() { 
        return orders; 
    }
    
    public void setOrders(Collection<Order> newValue) { 
        this.orders = newValue; 
    }

    public void addOrder(Order anOrder) {
        getOrders().add(anOrder);
		anOrder.setCustomer(this);
    }

    public void removeOrder(Order anOrder) {
        getOrders().remove(anOrder);
    }
}
