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
package org.eclipse.persistence.testing.models.jpa.relationships;

import javax.persistence.*;
import static javax.persistence.GenerationType.*;
import static javax.persistence.CascadeType.*;
import static javax.persistence.FetchType.*;

import org.eclipse.persistence.annotations.CopyPolicy;

@Entity(name="OrderBean")
@Table(name="CMP3_ORDER")
@CopyPolicy(org.eclipse.persistence.testing.models.jpa.relationships.TestInstantiationCopyPolicy.class)
@NamedQuery(
	name="findAllOrdersByItem",
	query="SELECT OBJECT(theorder) FROM OrderBean theorder WHERE theorder.item.itemId = :id"
)
@NamedNativeQueries({/*empty*/}) //Test for GF#1624 - Weaving failed if there is empty annotation array value
public class Order implements java.io.Serializable {
	private Integer orderId;
	private int version;
	private Item item;
	private int quantity;
	private String shippingAddress;
	private Customer customer;
    private Customer billedCustomer;
    private SalesPerson salesPerson;
    private Auditor auditor;
    private OrderLabel orderLabel;
    private OrderCard orderCard;

    public Order() {}

    @ManyToOne
    @JoinTable(
      name = "JPA_ORDER_AUDITOR",
      joinColumns = @JoinColumn(name="ORDER_ID"),
      inverseJoinColumns = @JoinColumn(name="AUDITOR_ID")
    )
    public Auditor getAuditor() {
        return auditor;
    }

    public void setAuditor(Auditor auditor) {
        this.auditor = auditor;
    }
    
    @OneToOne(cascade=PERSIST)
    @JoinTable(
        name = "JPA_ORDER_ORDER_LABEL",
        joinColumns = @JoinColumn(name="ORDER_ID"),
        inverseJoinColumns = @JoinColumn(name="ORDER_LABEL_ID")
    )
    public OrderLabel getOrderLabel() {
        return orderLabel;
    }

    public void setOrderLabel(OrderLabel orderLabel) {
        this.orderLabel = orderLabel;
    }

    @OneToOne(mappedBy="order", cascade=PERSIST)
    public OrderCard getOrderCard() {
        return orderCard;
    }

    public void setOrderCard(OrderCard orderCard) {
        this.orderCard = orderCard;
        if (this.orderCard != null) {
            this.orderCard.setOrder(this);
        }
    }
    
	@Id
    @GeneratedValue(strategy=TABLE, generator="ORDER_TABLE_GENERATOR")
	@TableGenerator(
        name="ORDER_TABLE_GENERATOR", 
        table="CMP3_CUSTOMER_SEQ", 
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
	public Item getItem() { 
        return item; 
    }
    
	public void setItem(Item item) { 
        this.item = item; 
    }

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

	@ManyToOne(fetch=LAZY)
	public Customer getCustomer() { 
        return customer; 
    }
    
	public void setCustomer(Customer customer) { 
        this.customer = customer; 
    }
    
    @ManyToOne(fetch=LAZY)
	public Customer getBilledCustomer() { 
        return billedCustomer; 
    }
    
	public void setBilledCustomer(Customer billedCustomer) { 
        this.billedCustomer = billedCustomer; 
    }
    
    @ManyToOne(fetch=LAZY)
    public SalesPerson getSalesPerson() {
        return salesPerson;
    }
    
    public void setSalesPerson(SalesPerson salesPerson) {
        this.salesPerson = salesPerson;
    }
}
