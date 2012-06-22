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
package org.eclipse.persistence.testing.models.aggregate;

import java.io.*;
import java.util.*;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.mappings.*;
import org.eclipse.persistence.tools.schemaframework.TableDefinition;
import org.eclipse.persistence.indirection.*;

public class Agent implements Serializable {
    private String firstName;
    private String lastName;
    private java.math.BigDecimal id;
    private ValueHolderInterface houses;
    private java.util.List houses2;
    private ValueHolderInterface customers;

    public Agent() {
        super();
        houses = new ValueHolder(new Vector());
        houses2 = new ArrayList(2);
        customers = new ValueHolder(new Vector());
    }

    public void addCustomer(Customer customer) {
        getCustomers().addElement(customer);
    }

    public void addHouse(House house) {
        getHouses().addElement(house);
    }

    public static void addToDescriptor(ClassDescriptor descriptor) {
        OneToManyMapping houses = (OneToManyMapping)descriptor.getMappingForAttributeName("houses");
        houses.useTransparentCollection();
    }

    public static Agent example1() {
        Agent example1 = new Agent();

        example1.setFirstName("Micheal");
        example1.setLastName("Jordan");
        Vector customers = new Vector();
        customers.addElement(Customer.example1());
        customers.addElement(Customer.example2());
        example1.setCustomers(customers);

        Vector houses = new Vector();
        houses.addElement(SingleHouse.example1());
        houses.addElement(TownHouse.example3());
        example1.setHouses(houses);
        return example1;
    }

    public static Agent example2() {
        Agent example2 = new Agent();

        //example1.setId(new java.math.BigDecimal(1));
        example2.setFirstName("Dennis");
        example2.setLastName("Rodman");
        Vector customers = new Vector();
        customers.addElement(Customer.example3());
        example2.setCustomers(customers);

        Vector houses = new Vector();
        houses.addElement(SingleHouse.example2());
        houses.addElement(TownHouse.example4());
        example2.setHouses(houses);
        return example2;
    }

    public Vector getCustomers() {
        return (Vector)getCustomerValueHolder().getValue();
    }

    public ValueHolderInterface getCustomerValueHolder() {
        return customers;
    }

    public String getFirstName() {
        return firstName;
    }

    public Vector getHouses() {
        return (Vector)getHouseValueHolder().getValue();
    }

    public java.util.List getHouses2() {
        return houses2;
    }

    public ValueHolderInterface getHouseValueHolder() {
        return houses;
    }

    public java.math.BigDecimal getId() {
        return id;
    }

    public String getLastName() {
        return lastName;
    }

    public void removeCustomer(Customer customer) {
        getCustomers().removeElement(customer);
    }

    public void removeHouse(House house) {
        getHouses().removeElement(house);
    }

    public void setCustomers(Vector customerVector) {
        customers.setValue(customerVector);
    }

    public void setCustomerValueHolder(ValueHolderInterface customers) {
        this.customers = customers;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setHouses(Vector houseVector) {
        getHouseValueHolder().setValue(houseVector);
    }

    public void setHouses2(java.util.List newHouses2) {
        houses2 = newHouses2;

    }

    public void setHouseValueHolder(ValueHolderInterface houses) {
        this.houses = houses;
    }

    public void setId(java.math.BigDecimal id) {
        this.id = id;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Return a platform independant definition of the database table.
     */
    public static TableDefinition tableDefinition() {
        TableDefinition definition = new TableDefinition();

        definition.setName("AGENT");

        definition.addIdentityField("AGENT_ID", java.math.BigDecimal.class, 15);
        definition.addField("FNAME", String.class, 30);
        definition.addField("LNAME", String.class, 30);
        definition.addField("VERSION", java.math.BigDecimal.class, 15);
        return definition;
    }
}
