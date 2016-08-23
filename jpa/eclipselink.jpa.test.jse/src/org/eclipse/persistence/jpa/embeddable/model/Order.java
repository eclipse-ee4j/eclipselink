/*******************************************************************************
 * Copyright (c) 2016 IBM Corporation. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     08/24/2016 - Will Dazey
 *       - 500145 : Nested Embeddables Test
 ******************************************************************************/
package org.eclipse.persistence.jpa.embeddable.model;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Version;

@Entity
@Table(name="CUST_ORDER")
public class Order {
    
    @EmbeddedId
    @AttributeOverrides({
        @AttributeOverride(name = "billingAddress.zipcode.zip", column = @Column(name = "BILL_ZIP")),
        @AttributeOverride(name = "shippingAddress.zipcode.zip", column = @Column(name = "SHIP_ZIP"))
    })
    private OrderPK id;
    
    @Version
    int version;
    
    public Order() { }
    
    public OrderPK getId() {
        return id;
    }
    
    public void setId(OrderPK id) {
        this.id = id;
    }
    
    public int getVersion() {
        return version;
    }
    
    public void setVersion(int version) {
        this.version = version;
    }
}
