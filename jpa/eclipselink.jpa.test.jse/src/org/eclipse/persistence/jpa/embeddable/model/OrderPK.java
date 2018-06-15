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

import javax.persistence.Embeddable;
import javax.persistence.Embedded;

@Embeddable
public class OrderPK {
    
    @Embedded
    private Address billingAddress;
    
    @Embedded
    private Address shippingAddress;
    
    public OrderPK() { }
    
    public Address getBillingAddress() {
        return billingAddress;
    }
    
    public void setBillingAddress(Address billingAddress) {
        this.billingAddress = billingAddress;
    }
    
    public Address getShippingAddress() {
        return shippingAddress;
    }
    
    public void setShippingAddress(Address shippingAddress) {
        this.shippingAddress = shippingAddress;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((billingAddress == null) ? 0 : billingAddress.hashCode());
        result = prime * result + ((shippingAddress == null) ? 0 : shippingAddress.hashCode());
        return result;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        OrderPK other = (OrderPK) obj;
        if (billingAddress == null) {
            if (other.billingAddress != null)
                return false;
        } else if (!billingAddress.equals(other.billingAddress))
            return false;
        if (shippingAddress == null) {
            if (other.shippingAddress != null)
                return false;
        } else if (!shippingAddress.equals(other.shippingAddress))
            return false;
        return true;
    }
}
