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
package org.eclipse.persistence.testing.models.jpa.advanced;

import static javax.persistence.GenerationType.TABLE;

import java.io.Serializable;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;
import javax.persistence.Version;

@Entity(name="AdvancedCustomer")
@Access(value=AccessType.PROPERTY)
@Table(name="CMP3_ADV_CUSTOMER")
public class Customer implements Serializable {
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
    @GeneratedValue(strategy=TABLE, generator="ADV_CUSTOMER_TABLE_GENERATOR")
    @TableGenerator(
        name="ADV_CUSTOMER_TABLE_GENERATOR",
        table="CMP3_EMPLOYEE_SEQ",
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
    @Override
    public String toString() {
        return "Customer [id=" + id + ", version=" + version + ", firstName="
                + firstName + ", lastName=" + lastName + ", budget=" + budget
                + "]";
    }
}
