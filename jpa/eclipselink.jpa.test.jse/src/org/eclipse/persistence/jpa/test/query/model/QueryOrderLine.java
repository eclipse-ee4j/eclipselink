/*
 * Copyright (c) 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.persistence.jpa.test.query.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "QUERY_ORDER_LINE")
public class QueryOrderLine {

    @Id
    private long orderLineKey;

    @ManyToOne
    private QueryOrder queryOrder;

    public long getOrderLineKey() {
        return orderLineKey;
    }

    public void setOrderLineKey(long orderLineKey) {
        this.orderLineKey = orderLineKey;
    }

    public QueryOrder getOrder() {
        return queryOrder;
    }

    public void setOrder(QueryOrder queryOrder) {
        this.queryOrder = queryOrder;
    }
}
