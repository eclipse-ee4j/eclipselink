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
 *     tware - bug 282571 testing of ManyToMany Map with Entity key and Entity value
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.relationships;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.GenerationType.TABLE;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Id;

@Entity
@Table(name="CMP3_CUSTOMER_SERVICE_REP")
public class CustomerServiceRepresentative {

    private int id;
    private String name = null;
    private List customers = new ArrayList<Customer>();
    
    @Id
    @GeneratedValue(strategy=TABLE, generator="CUSTOMER_TABLE_GENERATOR")
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    
    @ManyToMany(cascade=ALL, mappedBy="CSInteractions")
    public List<Customer> getCustomers() {
        return customers;
    }
    public void setCustomers(List<Customer> customers) {
        this.customers = customers;
    }
    
    public void addCustomer(Customer customer){
        this.customers.add(customer);
    }
    
    public void removeCustomer(Customer customer){
        this.customers.remove(customer);
    }
}

