/*******************************************************************************
 * Copyright (c) 2005, 2009 SAP. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     SAP - initial API and implementation
 ******************************************************************************/

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
