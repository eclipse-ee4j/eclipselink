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
 *     10/19/2016 - Will Dazey
 *       - 506168 : Nested Embeddables AttributeOverride Test
 ******************************************************************************/
package org.eclipse.persistence.jpa.embeddable.model;

import javax.persistence.Embeddable;

@Embeddable
public class DeepOrderPK {

    private OrderPK orderpk;

    public DeepOrderPK() {}

    public OrderPK getOrderpk() {
        return orderpk;
    }

    public void setOrderpk(OrderPK orderpk) {
        this.orderpk = orderpk;
    }
}
