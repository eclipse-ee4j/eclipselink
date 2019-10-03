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

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.GenerationType.TABLE;

import java.util.Collection;
import java.util.HashSet;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Version;

import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.config.CacheIsolationType;

@Entity(name="FieldAccessCustomer")
@Table(name="CMP3_FIELDACCESS_CUSTOMER")
@NamedQuery(
    name="findAllCustomersFieldAccess",
    query="SELECT OBJECT(thecust) FROM FieldAccessCustomer thecust"
)
@NamedNativeQueries(value={
    @NamedNativeQuery(name="findAllSQLCustomersFieldAccess",
        query="select * from CMP3_FIELDACCESS_CUSTOMER"),
    @NamedNativeQuery(name="insertCustomer1111SQLFieldAccess",
        query="INSERT INTO CMP3_FIELDACCESS_CUSTOMER (CUST_ID, NAME, CITY, CUST_VERSION) VALUES (1111, NULL, NULL, 1)"),
    @NamedNativeQuery(name="deleteCustomer1111SQLFieldAccess",
        query="DELETE FROM CMP3_FIELDACCESS_CUSTOMER WHERE (CUST_ID=1111)")})
@Cache(isolation=CacheIsolationType.ISOLATED)
public class Customer implements java.io.Serializable{
    @Id
    @GeneratedValue(strategy=TABLE, generator="FIELDACCESS_CUSTOMER_TABLE_GENERATOR")
    @TableGenerator(
        name="FIELDACCESS_CUSTOMER_TABLE_GENERATOR",
        table="CMP3_FIELDACCESS_CUSTOMER_SEQ",
        pkColumnName="SEQ_NAME",
        valueColumnName="SEQ_COUNT",
        pkColumnValue="CUST_SEQ"
    )
    @Column(name="CUST_ID")
    private Integer customerId;
    @Version
    @Column(name="CUST_VERSION")
    private int version;
    private String city;
    private String name;
    @OneToMany(cascade=ALL, mappedBy="customer")
    private Collection<Order> orders = new HashSet<Order>();
    @ManyToMany
    @JoinTable(name="CMP3_FIELDACCESS_CUST_CUST")
    private Collection<Customer> controlledCustomers = new HashSet<Customer>();

    public Customer() {}

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer id) {
        this.customerId = id;
    }

    public int getVersion() {
        return version;
    }

    protected void setVersion(int version) {
        this.version = version;
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

    public Collection<Customer> getCCustomers() {
        return controlledCustomers;
    }

    public void setCCustomers(Collection<Customer> controlledCustomers) {
        this.controlledCustomers = controlledCustomers;
    }

    public void addCCustomer(Customer controlledCustomer) {
        getCCustomers().add(controlledCustomer);
    }
}
