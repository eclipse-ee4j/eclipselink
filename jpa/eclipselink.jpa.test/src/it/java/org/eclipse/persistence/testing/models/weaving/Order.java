/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.models.weaving;

import jakarta.persistence.*;
import static jakarta.persistence.GenerationType.*;
import static jakarta.persistence.CascadeType.*;
import static jakarta.persistence.FetchType.*;

@Entity(name="OrderBean")
@Table(name="CMP3_ORDER")
@NamedQuery(
    name="findAllOrdersByItem",
    query="SELECT OBJECT(theorder) FROM OrderBean theorder WHERE theorder.item.itemId = :id"
)
public class Order implements java.io.Serializable {

    private Integer orderId;
    private int version;
    private Item item;
    private int quantity;
    private String shippingAddress;
    private Customer customer;

    public Order() {
    }

    @Id
    @GeneratedValue(strategy=TABLE, generator="ORDER_TABLE_GENERATOR")
    @TableGenerator(
        name="ORDER_TABLE_GENERATOR",
        table="CMP3_CUSTOMER_SEQ",
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
