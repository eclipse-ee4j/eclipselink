/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     10/22/2009 - Guy Pelletier/Prakash Selvaraj - added tests for DDL generation of table per class
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.ddlgeneration.tableperclass;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.TableGenerator;
import javax.persistence.Version;

@Entity
@NamedQueries({
  @NamedQuery(name = "Customer.findAll", query = "select o from Customer o")
})
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@TableGenerator(name = "keyGen", table = "KEY_GEN", pkColumnName = "ID",
    pkColumnValue = "0", valueColumnName = "PK")
public abstract class Customer implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "keygen")
    private Integer customerId;
    @Version
    private Integer version;

    String fullName;
    
    String nationality;
    
    String gender;
    
    Integer age;
    
    Double subscriptionPrice;
    
    public Customer() {
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getNationality() {
        return nationality;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getGender() {
        return gender;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Integer getAge() {
        return age;
    }

    public void setSubscriptionPrice(Double subscriptionPrice) {
        this.subscriptionPrice = subscriptionPrice;
    }

    public Double getSubscriptionPrice() {
        return subscriptionPrice;
    }
}
