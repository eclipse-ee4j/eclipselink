/*
 * Copyright (c) 2016, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     10/19/2016 - 2.6 Will Dazey
//       - 506168 : Nested Embeddables AttributeOverride Test
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
