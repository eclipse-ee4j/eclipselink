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
package org.eclipse.persistence.testing.models.jpa.relationships;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Collection;
import java.util.Map;

import javax.persistence.*;

import static javax.persistence.GenerationType.*;
import static javax.persistence.CascadeType.*;

@Entity
@Table(name="CMP3_CUSTOMER")
@NamedQuery(
	name="findAllCustomers",
	query="SELECT OBJECT(thecust) FROM Customer thecust"
)
@NamedNativeQueries(value={
    @NamedNativeQuery(name="findAllSQLCustomers",
        query="select * from CMP3_CUSTOMER"),
    @NamedNativeQuery(name="insertCustomer1111SQL",
        query="INSERT INTO CMP3_CUSTOMER (CUST_ID, NAME, CITY, CUST_VERSION) VALUES (1111, NULL, NULL, 1)"),
    @NamedNativeQuery(name="deleteCustomer1111SQL",
        query="DELETE FROM CMP3_CUSTOMER WHERE (CUST_ID=1111)")})
public class Customer implements java.io.Serializable{
    private Integer customerId;
    private int version;
    private String city;
    private String name;
    private Collection<Order> orders = new HashSet<Order>();
    private CustomerCollection controlledCustomers = new CustomerCollection();
    private Map<ServiceCall, CustomerServiceRepresentative> customerServiceInteractions = new HashMap<ServiceCall, CustomerServiceRepresentative>();
        
    public Customer() {}

    @Id
    @GeneratedValue(strategy=TABLE, generator="CUSTOMER_TABLE_GENERATOR")
    @TableGenerator(
        name="CUSTOMER_TABLE_GENERATOR", 
        table="CMP3_CUSTOMER_SEQ", 
        pkColumnName="SEQ_NAME", 
        valueColumnName="SEQ_COUNT",
        pkColumnValue="CUST_SEQ"
    )
    @Column(name="CUST_ID")
    public Integer getCustomerId() { 
        return customerId; 
    }
    
    public void setCustomerId(Integer id) { 
        this.customerId = id; 
    }

    @Version
    @Column(name="CUST_VERSION")
    public int getVersion() { 
        return version; 
    }
    
    protected void setVersion(int version) {
            this.version = version;
    }

    public String getCity() {
        return city; 
    }
    
    public void setCity(String aCity) { 
        this.city = aCity; 
    }

    public String getName() { 
        return name; 
    }
    
    public void setName(String aName) { 
        this.name = aName; 
    }

    @OneToMany(targetEntity=Order.class, cascade=ALL, mappedBy="customer")
    public Collection getOrders() { 
        return orders; 
    }
    
    public void setOrders(Collection<Order> newValue) { 
        this.orders = newValue; 
    }

    public void addOrder(Order anOrder) {
        getOrders().add(anOrder);
        anOrder.setCustomer(this);
    }

    public void removeOrder(Order anOrder) {
        getOrders().remove(anOrder);
    }
    
    //bug 236275: changed to test collection implementations with eagerly fetched mappings
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name="CMP3_CUSTOMER_CUSTOMER")
    public CustomerCollection<Customer> getCCustomers() {
        return controlledCustomers;
    }
    
    public void setCCustomers(CustomerCollection controlledCustomers) {
        this.controlledCustomers = controlledCustomers;
    }
    
    public void addCCustomer(Customer controlledCustomer) {
        getCCustomers().add(controlledCustomer);
    }
    
    @ManyToMany(cascade={ALL})
    @MapKeyClass(ServiceCall.class)
    @JoinTable(
            name="CMP3_CUST_REP",
            joinColumns=
            @JoinColumn(name="CUST_ID", referencedColumnName="CUST_ID"),
            inverseJoinColumns=
            @JoinColumn(name="REP_ID", referencedColumnName="ID")
    )
    public Map<ServiceCall, CustomerServiceRepresentative> getCSInteractions(){
        return customerServiceInteractions;
    }
    
    public void setCSInteractions(Map<ServiceCall, CustomerServiceRepresentative> interactions){
        this.customerServiceInteractions = interactions;
    }
    
    public void addCSInteraction(ServiceCall call, CustomerServiceRepresentative rep){
        customerServiceInteractions.put(call, rep);
        rep.addCustomer(this);
    }
    
    public void removeCSInteraction(ServiceCall call){
        customerServiceInteractions.remove(call);
    }
}

