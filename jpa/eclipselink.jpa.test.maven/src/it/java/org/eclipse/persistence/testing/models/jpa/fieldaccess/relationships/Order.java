/*
 * Copyright (c) 1998, 2019 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.models.jpa.fieldaccess.relationships;

import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.TABLE;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Version;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.config.CacheIsolationType;

@Entity(name="FieldAccessOrderBean")
@Table(name="CMP3_FIELDACCESS_ORDER")
@NamedQuery(
        name="findAllFieldAccessOrdersByItem",
        query="SELECT OBJECT(theorder) FROM FieldAccessOrderBean theorder WHERE theorder.item.itemId = :id"
)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
@Cache(isolation=CacheIsolationType.ISOLATED)
public class Order implements java.io.Serializable {
    @Id
    @GeneratedValue(strategy=TABLE, generator="FIELDACCESS_ORDER_TABLE_GENERATOR")
    @TableGenerator(
            name="FIELDACCESS_ORDER_TABLE_GENERATOR",
            table="CMP3_FIELDACCESS_CUSTOMER_SEQ",
            pkColumnName="SEQ_NAME",
            valueColumnName="SEQ_COUNT",
            pkColumnValue="ORDER_SEQ"
    )
    @Column(name="ORDER_ID")
    private Integer orderId;
    @Version
    @Column(name="ORDER_VERSION")
    private int version;
    @OneToOne(cascade=PERSIST, fetch=LAZY)
    private Item item;
    private int quantity;
    @Column(name="SHIP_ADDR")
    private String shippingAddress;
    @ManyToOne(fetch=LAZY)
    private Customer customer;
    @ManyToOne(fetch=LAZY)
    private Customer billedCustomer;
    @ManyToOne(fetch=LAZY)
    private SalesPerson salesPerson;

    public Order() {}

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer id) {
        this.orderId = id;
    }

    protected int getVersion() {
        return version;
    }

    protected void setVersion(int version) {
        this.version = version;
    }

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

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Customer getBilledCustomer() {
        return billedCustomer;
    }

    public void setBilledCustomer(Customer billedCustomer) {
        this.billedCustomer = billedCustomer;
    }

    public SalesPerson getSalesPerson() {
        return salesPerson;
    }

    public void setSalesPerson(SalesPerson salesPerson) {
        this.salesPerson = salesPerson;
    }
}
