/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.models.jpa.advanced;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.TableGenerator;
import jakarta.persistence.Version;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.CascadeType.MERGE;
import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.GenerationType.TABLE;

@Entity
@Table(name="CMP3_DEALER")
public class Dealer implements Serializable {
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
        customers = new ArrayList<>();
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
        table="CMP3_EMPLOYEE_SEQ",
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

    @Override
    public String toString() {
        return "Dealer [id=" + id + ", version=" + version + ", firstName="
                + firstName + ", lastName=" + lastName + ", status=" + status
                + "]";
    }
}
