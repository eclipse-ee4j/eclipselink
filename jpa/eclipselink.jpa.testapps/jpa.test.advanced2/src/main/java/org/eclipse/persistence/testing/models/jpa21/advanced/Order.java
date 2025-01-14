/*
 * Copyright (c) 2013, 2025 Oracle and/or its affiliates. All rights reserved.
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
//     02/06/2013-2.5 Guy Pelletier
//       - 382503: Use of @ConstructorResult with createNativeQuery(sqlString, resultSetMapping) results in NullPointerException
//     02/11/2013-2.5 Guy Pelletier
//       - 365931: @JoinColumn(name="FK_DEPT",insertable = false, updatable = true) causes INSERT statement to include this data value that it is associated with
package org.eclipse.persistence.testing.models.jpa21.advanced;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.ColumnResult;
import jakarta.persistence.ConstructorResult;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SqlResultSetMapping;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@SqlResultSetMapping(
    name="OrderConstructorResult",
    classes = {
        @ConstructorResult(
            targetClass = Order.class,
            columns = {
                @ColumnResult(name = "O_ID"),
                @ColumnResult(name = "O_QUANTITY"),
                @ColumnResult(name = "O_ITEM_NAME")
            }
        )
    }
)
@Entity
@Table(name="JPA21_ORDER")
public class Order {
    @Id
    @GeneratedValue
    @Column(name="ORDER_ID")
    private Integer orderId;

    @Basic
    private int quantity;

    @OneToOne
    @JoinColumn(name = "ITEM_ID", insertable=true, updatable=false)
    private Item item;

    @OneToOne
    @JoinColumn(name = "ITEM_PAIR_ID", insertable=false, updatable=true)
    private Item itemPair;

    @Transient
    private String itemName;

    public Order() {}

    // Constructor result
    public Order(Integer id, Integer quantity, String itemName) {
        this.orderId = id;
        this.quantity = quantity;
        this.itemName = itemName;
    }

    public Item getItem() {
        return item;
    }

    public Item getItemPair() {
        return itemPair;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public void setItemPair(Item itemPair) {
        this.itemPair = itemPair;
    }

    public void setOrderId(Integer id) {
        this.orderId = id;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String toString() {
        return "Order [" + orderId + "]";
    }
}
