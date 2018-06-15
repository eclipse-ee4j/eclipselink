/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     02/06/2013-2.5 Guy Pelletier
//       - 382503: Use of @ConstructorResult with createNativeQuery(sqlString, resultSetMapping) results in NullPointerException
//     02/11/2013-2.5 Guy Pelletier
//       - 365931: @JoinColumn(name="FK_DEPT",insertable = false, updatable = true) causes INSERT statement to include this data value that it is associated with
package org.eclipse.persistence.testing.models.jpa22.advanced;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.Table;
import javax.persistence.Transient;

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
@Table(name="JPA22_ORDER")
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
