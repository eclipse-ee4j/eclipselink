/*
 * Copyright (c) 2005, 2018 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2005, 2015 SAP. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     SAP - initial API and implementation

package org.eclipse.persistence.testing.models.wdf.jpa2.employee;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

@Entity(name = "Employee2")
public class Employee extends Person {
    @Embedded
    private ContactInfo contactinfo;

    private int shoesize;

    @OneToOne
    private Employee spouse;

    @Column(scale = 2, precision = 10)
    private BigDecimal salary;

    @ManyToOne
    private Department department;

    // @Embedded
    // private FrequentFlyerPlan frequentFlierPlan;

    public ContactInfo getContactinfo() {
        return contactinfo;
    }

    public void setContactinfo(ContactInfo contactinfo) {
        this.contactinfo = contactinfo;
    }

    public int getShoesize() {
        return shoesize;
    }

    public void setShoesize(int shoesize) {
        this.shoesize = shoesize;
    }

    public Employee getSpouse() {
        return spouse;
    }

    public void setSpouse(Employee spouse) {
        this.spouse = spouse;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    // public FrequentFlyerPlan getFrequentFlierPlan() {
    // return frequentFlierPlan;
    // }
    //
    // public void setFrequentFlierPlan(FrequentFlyerPlan frequentFlierPlan) {
    // this.frequentFlierPlan = frequentFlierPlan;
    // }

}
