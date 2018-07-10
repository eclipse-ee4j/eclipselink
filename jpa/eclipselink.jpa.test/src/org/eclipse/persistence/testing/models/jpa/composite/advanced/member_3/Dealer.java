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
package org.eclipse.persistence.testing.models.jpa.composite.advanced.member_3;

import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.GenerationType.TABLE;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Version;

import org.eclipse.persistence.testing.models.jpa.composite.advanced.member_1.Customer;

@Entity
@Table(name="MBR3_DEALER")
public class Dealer {
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
    @Id
    @GeneratedValue(strategy=TABLE, generator="DEALER_TABLE_GENERATOR")
    @TableGenerator(
        name="DEALER_TABLE_GENERATOR",
        table="MBR3_SEQ",
        pkColumnName="SEQ_NAME",
        valueColumnName="SEQ_COUNT",
        pkColumnValue="DEALER_SEQ",
        initialValue=50
    )
    @Column(name="DEALER_ID")
    public Integer getId() {
        return id;
    }

    @Column(name="F_NAME")
    public String getFirstName() {
        return firstName;
    }

    @Column(name="L_NAME")
    public String getLastName() {
        return lastName;
    }

    public String getStatus() {
        return status;
    }

    @OneToMany(cascade={PERSIST, MERGE})
    @JoinColumn(name="FK_DEALER_ID")
    public List<Customer> getCustomers() {
        return customers;
    }

    @Version
    @Column(name="VERSION")
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
