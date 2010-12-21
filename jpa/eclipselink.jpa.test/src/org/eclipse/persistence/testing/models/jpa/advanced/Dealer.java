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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.advanced;

import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.GenerationType.TABLE;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Version;

@Entity
@Table(name="CMP3_DEALER")
public class Dealer {
    private Integer id;
    private Integer version;
    private String firstName;
    private String lastName;
    private String status;
    private List<Customer> customers;
    
    public void addCustomer(Customer customer) {
        customers.add(customer);
    }
    
    public Dealer() {
        super();
        customers = new ArrayList<Customer>();
    }
    public Dealer(String firstName, String lastName) {
        this();
        this.firstName = firstName;
        this.lastName = lastName;
    }
    @Id
    @GeneratedValue(strategy=TABLE, generator="DEALER_TABLE_GENERATOR")
    @TableGenerator(
        name="DEALER_TABLE_GENERATOR", 
        table="CMP3_EMPLOYEE_SEQ", 
        pkColumnName="SEQ_NAME", 
        valueColumnName="SEQ_COUNT",
        pkColumnValue="DEALER_SEQ",
        initialValue=50
    )
    @Column(name="DEALER_ID")
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

    public String getStatus() { 
        return status; 
    }

    @OneToMany(cascade={PERSIST, MERGE})
    @JoinColumn(name="FK_DEALER_ID")
    public List<Customer> getCustomers() {
        return customers;
    }

    @Version
    @Column(name="VERSION")
    public Integer getVersion() {
        return version; 
    }

    public void removeCustomer(Customer customer) {
        customers.remove(customer);
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

    public void setStatus(String status) { 
        this.status = status; 
    }

    public void setCustomers(List<Customer> customers) {
        this.customers = customers;
    }

    public void setVersion(Integer version) {
        this.version = version; 
    }
}
