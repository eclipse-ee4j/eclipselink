/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     06/14/2010-2.2 Karen Moore 
 *       - 264417:  Table generation is incorrect for JoinTables in AssociationOverrides
 ******************************************************************************/
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