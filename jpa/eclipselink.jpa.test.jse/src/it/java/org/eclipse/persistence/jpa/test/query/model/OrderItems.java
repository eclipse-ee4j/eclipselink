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

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "ORDER_ITEMS")
public class OrderItems {

    @Id
    private long id;

    @ManyToOne
    private OrderHeader header;

    public OrderItems(final long id, final OrderHeader header) {
        this.id = id;
        this.header = header;
    }

    public OrderItems() {
        this(-1, null);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public OrderHeader getHeader() {
        return header;
    }

    public void setHeader(OrderHeader header) {
        this.header = header;
    }

    @Override
    public String toString() {
        return "OrderItems{" +
                "id=" + id +
                ", header=" + header +
                '}';
    }

}
