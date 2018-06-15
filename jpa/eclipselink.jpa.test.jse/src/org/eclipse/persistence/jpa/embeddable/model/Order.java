/*
 * Copyright (c) 2016, 2018 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2016 IBM Corporation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     08/24/2016 - Will Dazey
//       - 500145 : Nested Embeddables Test
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
