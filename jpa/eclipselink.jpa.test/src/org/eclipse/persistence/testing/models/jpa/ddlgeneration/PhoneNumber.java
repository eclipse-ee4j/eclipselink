/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     06/14/2010-2.2 Karen Moore
//       - 264417:  Table generation is incorrect for JoinTables in AssociationOverrides
package org.eclipse.persistence.testing.models.jpa.ddlgeneration;

import static javax.persistence.FetchType.EAGER;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

@Entity(name="DDL_PHONE")
public class PhoneNumber {
    @Id
    @Column(name="NUMB")
    @GeneratedValue
    public int number;

    @ManyToMany(mappedBy="contactInfo.phoneNumbers", fetch=EAGER)
    public Collection<Employee> employees;

    public PhoneNumber() {
        employees = new ArrayList<Employee>();
    }

    public void addEmployee(Employee employee) {
        employees.add(employee);
    }

    public Collection<Employee> getEmployees() {
        return employees;
    }

    public int getNumber() {
        return number;
    }

    public void setEmployees(Collection<Employee> employees) {
        this.employees = employees;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
