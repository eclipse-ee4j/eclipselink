/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     06/14/2010-2.2 Karen Moore
//       - 264417:  Table generation is incorrect for JoinTables in AssociationOverrides
package org.eclipse.persistence.testing.models.jpa.ddlgeneration;

import static jakarta.persistence.FetchType.EAGER;

import java.util.ArrayList;
import java.util.Collection;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;

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
