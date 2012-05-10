/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.nativeapitest;

import java.util.*;
import java.io.Serializable;

/**
 * <p><b>Purpose</b>: Represents the mailing address on an Employee
 * <p><b>Description</b>: Held in a private 1:1 relationship from Employee
 * @see Employee
 */
public class Address implements Serializable {
    private Integer id;
    private String street;
    private String city;
    private String province;
    private String postalCode;
    
    private String country;
    
    private Collection<Employee> employees;

    public Address() {
        city = "";
        province = "";
        postalCode = "";
        street = "";
        country = "";
        this.employees = new Vector<Employee>();
    }

    public Address(String street, String city, String province, String country, String postalCode) {
        this.street = street;
        this.city = city;
        this.province = province;
        this.country = country;
        this.postalCode = postalCode;
        this.employees = new Vector<Employee>();
    }

    public Address copy() {
        Address copy = new Address();
        copy.id = this.id;
        copy.street = this.street;
        copy.city = this.city;
        copy.province = this.province;
        copy.country = this.country;
        copy.postalCode = this.postalCode;
        return copy;
    }
    
    public TransferAddress transferCopy() {
        TransferAddress copy = new TransferAddress();
        copy.id = this.id;
        copy.street = this.street;
        copy.city = this.city;
        copy.province = this.province;
        copy.country = this.country;
        copy.postalCode = this.postalCode;
        return copy;
    }
    
    public Integer getId() { 
        return id; 
    }
    
    public void setId(Integer id) { 
        this.id = id; 
    }

    public String getStreet() { 
        return street; 
    }
    
    public void setStreet(String street) { 
        this.street = street; 
    }

    public String getCity() { 
        return city; 
    }
    
    public void setCity(String city) { 
        this.city = city; 
    }

    public String getProvince() { 
        return province; 
    }
        
    public void setProvince(String province) { 
        this.province = province; 
    }

    public String getPostalCode() { 
        return postalCode; 
    }
    
    public void setPostalCode(String postalCode) { 
        this.postalCode = postalCode; 
    }

    public String getCountry() { 
        return country; 
    }
    
    public void setCountry(String country) { 
        this.country = country;
    }
    
    public Collection<Employee> getEmployees() { 
        return employees; 
    }
    
    public void setEmployees(Collection<Employee> employees) {
        this.employees = employees;
    }
    
    public class TransferAddress {
        public Integer id;    
        public String street;
        public String city;
        public String province;
        public String postalCode;
        public String country;
    }
}
