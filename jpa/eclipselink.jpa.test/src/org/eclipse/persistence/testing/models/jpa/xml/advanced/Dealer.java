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
package org.eclipse.persistence.testing.models.jpa.xml.advanced;

import java.util.ArrayList;
import java.util.List;

public class Dealer extends BusinessPerson {
    private Integer id;
    private Integer version;
    private String firstName;
    private String lastName;
    private String status;
    private List<Customer> customers;

    public void addCustomer(Customer customer) {
        customers.add(customer);
    }

    public Dealer() {
        super();
        customers = new ArrayList<Customer>();
    }
    public Dealer(String firstName, String lastName) {
        this();
        this.firstName = firstName;
        this.lastName = lastName;
    }
    public Integer getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getStatus() {
        return status;
    }

    public List<Customer> getCustomers() {
        return customers;
    }

    public Integer getVersion() {
        return version;
    }

    public void removeCustomer(Customer customer) {
        customers.remove(customer);
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setCustomers(List<Customer> customers) {
        this.customers = customers;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}
