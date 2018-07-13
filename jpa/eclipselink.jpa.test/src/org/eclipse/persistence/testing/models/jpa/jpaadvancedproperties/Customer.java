/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.models.jpa.jpaadvancedproperties;

import java.util.Vector;
import java.util.HashSet;
import java.util.Collection;

public class Customer implements java.io.Serializable{
    private Integer customerId;
    private int version;
    private String city;
    private String name;
    private Collection<Order> orders = new HashSet<Order>();
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

    public Collection getOrders() {
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
        if (controlledCustomers == null) {
            return new Vector();
        }

        return controlledCustomers;
    }

    public void setCCustomers(Collection<Customer> controlledCustomers) {
        this.controlledCustomers = controlledCustomers;
    }

    public void addCCustomer(Customer controlledCustomer) {
        getCCustomers().add(controlledCustomer);
    }
}
