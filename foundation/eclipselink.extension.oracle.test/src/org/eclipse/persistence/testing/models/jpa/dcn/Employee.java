/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.dcn;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.SecondaryTable;
import javax.persistence.Table;
import javax.persistence.Version;

@Entity
@IdClass(EmployeeID.class)
@Table(name="DCN_EMPLOYEE")
@SecondaryTable(name="DCN_SALARY")
public class Employee implements Serializable {

    @Id
    @Column(name="ID")
    @GeneratedValue(strategy=GenerationType.SEQUENCE)
    private Integer id;

    @Id
    // Test composite ids
    private int id2 = 12345;

    @Column(name="NAME")
    private String name;
    
    @Column(name="SALARY", table="DCN_SALARY")
    private long salary;

    public long getSalary() {
        return salary;
    }

    public void setSalary(long salary) {
        this.salary = salary;
    }

    @Version
    @Column(name="VERSION")
    private Integer version;
     
    public Employee() {}

    public Integer getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getVersion() {
        return version; 
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
    
    public int getId2() {
        return id2;
    }

    public void setId2(int id2) {
        this.id2 = id2;
    }
    
}

