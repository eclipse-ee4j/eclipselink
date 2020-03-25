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
package org.eclipse.persistence.testing.models.jpa.composite.advanced.member_1;

import static jakarta.persistence.GenerationType.TABLE;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.TableGenerator;
import jakarta.persistence.Transient;
import jakarta.persistence.Version;

@Entity(name="AdvancedCustomer")
@Access(value=AccessType.PROPERTY)
@Table(name="MBR1_ADV_CUSTOMER")
public class Customer {
    private Integer id;
    private Integer version;
    private String firstName;
    private String lastName;
    private int budget;

    // this field should not be mapped, but the combination of
    // annotations should be allowed
    @Transient
    @Access(value=AccessType.FIELD)
    private String transientField;

    public Customer() {
        super();
    }
    public Customer(String firstName, String lastName) {
        this();
        this.firstName = firstName;
        this.lastName = lastName;
    }

    @Id
    @GeneratedValue(strategy=TABLE, generator="BR1_ADV_CUSTOMER_TABLE_GENERATOR")
    @TableGenerator(
        name="BR1_ADV_CUSTOMER_TABLE_GENERATOR",
        table="BR1_SEQ",
        pkColumnName="SEQ_NAME",
        valueColumnName="SEQ_COUNT",
        pkColumnValue="ADV_CUSTOMER_SEQ",
        initialValue=50
    )
    @Column(name="CUSTOMER_ID")
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

    public int getBudget() {
        return budget;
    }

    @Version
    @Column(name="VERSION")
    public Integer getVersion() {
        return version;
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

    public void setBudget(int budget) {
        this.budget = budget;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}
