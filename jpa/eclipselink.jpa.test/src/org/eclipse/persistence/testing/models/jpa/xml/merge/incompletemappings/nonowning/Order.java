/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.models.jpa.xml.merge.incompletemappings.nonowning;

import javax.persistence.*;

import static javax.persistence.GenerationType.*;
import static javax.persistence.CascadeType.*;
import static javax.persistence.FetchType.*;

@Entity(name="XMLIncompleteOrderBean")
@Table(name="CMP3_XML_INC_ORDER")
public class Order implements java.io.Serializable {
    private Integer orderId;
    private int version;
    private Item item;
    private int quantity;
    private String shippingAddress;
    private Customer customer;

    public Order() {}

    @Id
    @GeneratedValue(strategy=TABLE, generator="XML_INCOMPLETE_ORDER_TABLE_GENERATOR")
    @TableGenerator(
        name="XML_INCOMPLETE_ORDER_TABLE_GENERATOR",
        table="CMP3_XML_INC_CUSTOMER_SEQ",
        pkColumnName="SEQ_NAME",
        valueColumnName="SEQ_COUNT",
        pkColumnValue="ORDER_SEQ"
    )
    @Column(name="ORDER_ID")
    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer id) {
        this.orderId = id;
    }

    @Version
    @Column(name="ORDER_VERSION")
    protected int getVersion() {
        return version;
    }

    protected void setVersion(int version) {
        this.version = version;
    }

    @OneToOne(cascade=PERSIST, fetch=LAZY)
    @JoinColumn(name="ITEM_ID", referencedColumnName="ITEM_ID")
    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Column(name="SHIP_ADDR")
    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    @ManyToOne(fetch=LAZY)
    @JoinColumn(name="CUST_ID")
    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
}
