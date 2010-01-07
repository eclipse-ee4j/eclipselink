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
package org.eclipse.persistence.testing.models.jpa.jpaadvancedproperties;

public class Order implements java.io.Serializable {
	private Integer orderId;
	private int version;
	private Item item;
	private int quantity;
	private String shippingAddress;
	private Customer customer;
    private Customer billedCustomer;
    private SalesPerson salesPerson;
	
	public Order() {}

	public Integer getOrderId() { 
        return orderId; 
    }
    
	public void setOrderId(Integer id) { 
        this.orderId = id; 
    }

	protected int getVersion() { 
        return version; 
    }
    
	protected void setVersion(int version) { 
        this.version = version; 
    }

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

	public String getShippingAddress() { 
        return shippingAddress; 
    }
    
	public void setShippingAddress(String shippingAddress) {
		this.shippingAddress = shippingAddress;
	}

	public Customer getCustomer() { 
        return customer; 
    }
    
	public void setCustomer(Customer customer) { 
        this.customer = customer; 
    }
    
	public Customer getBilledCustomer() { 
        return billedCustomer; 
    }
    
	public void setBilledCustomer(Customer billedCustomer) { 
        this.billedCustomer = billedCustomer; 
    }
    
    public SalesPerson getSalesPerson() {
        return salesPerson;
    }
    
    public void setSalesPerson(SalesPerson salesPerson) {
        this.salesPerson = salesPerson;
    }
}
