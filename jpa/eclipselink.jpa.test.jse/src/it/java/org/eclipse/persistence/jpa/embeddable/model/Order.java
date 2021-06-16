/*
 * Copyright (c) 2016, 2020 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2016 IBM Corporation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     08/24/2016 - Will Dazey
//       - 500145 : Nested Embeddables Test
package org.eclipse.persistence.jpa.embeddable.model;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

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
