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

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "ORDER_HEADER")
public class OrderHeader {

    @Id
    private long id;

    @OneToMany(mappedBy = "header")
    List<OrderItems> orderItems;

    public OrderHeader(final int id) {
        this.id = id;
        this.orderItems = new ArrayList<>();
    }

    public OrderHeader() {
        this(-1);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<OrderItems> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItems> orderItems) {
        this.orderItems = orderItems;
    }

    public void addOrderItem(OrderItems item) {
        this.orderItems.add(item);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Order{id=");
        sb.append(id);
        sb.append(",orderItems=[");
        boolean first = true;
        for (OrderItems i : orderItems) {
            if (first) {
                first = false;
            } else {
                sb.append(",");
            }
            sb.append(i.toString());
        }
        sb.append("]}");
        return sb.toString();
    }

}
