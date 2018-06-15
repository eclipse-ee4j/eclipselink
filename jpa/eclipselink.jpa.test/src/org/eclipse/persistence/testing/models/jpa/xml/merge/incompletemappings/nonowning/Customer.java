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

import java.util.Collection;
import javax.persistence.*;
import static javax.persistence.GenerationType.*;
import static javax.persistence.CascadeType.*;

@Entity(name="XMLIncompleteCustomer")
@Table(name="CMP3_XML_INC_CUSTOMER")
public class Customer implements java.io.Serializable{
    private Integer customerId;
    private int version;
    private String city;
    private String name;
    private Collection<Order> orders;
    private Address billingAddress;

    public Customer() {}

    @Id
    @GeneratedValue(strategy=TABLE, generator="XML_INCOMPLETE_CUSTOMER_TABLE_GENERATOR")
    @TableGenerator(
        name="XML_INCOMPLETE_CUSTOMER_TABLE_GENERATOR",
        table="CMP3_XML_INC_CUSTOMER_SEQ",
        pkColumnName="SEQ_NAME",
        valueColumnName="SEQ_COUNT",
        pkColumnValue="CUST_SEQ"
    )
    @Column(name="CUST_ID")
    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer id) {
        this.customerId = id;
    }

    @Version
    @Column(name="CUST_VERSION")
    public int getVersion() {
        return version;
    }

    protected void setVersion(int version) {
        this.version = version;
    }

    public Address getBillingAddress() {
        return billingAddress;
    }

    protected void setBillingAddress(Address billingAddress) {
        this.billingAddress = billingAddress;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String aCity) {
        this.city = aCity;
    }

    public String getName() {
        return name;
    }

    public void setName(String aName) {
        this.name = aName;
    }

    @OneToMany(cascade=ALL, mappedBy="customer")
    public Collection<Order> getOrders() {
        return orders;
    }

    public void setOrders(Collection<Order> newValue) {
        this.orders = newValue;
    }

    public void addOrder(Order anOrder) {
        getOrders().add(anOrder);
        anOrder.setCustomer(this);
    }

    public void removeOrder(Order anOrder) {
        getOrders().remove(anOrder);
    }
}
