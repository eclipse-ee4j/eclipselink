/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
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
    private ValueHolderInterface<Vector<House>> houses;
    private java.util.List houses2;
    private ValueHolderInterface<Vector<Customer>> customers;

    public Agent() {
        super();
        houses = new ValueHolder<>(new Vector<>());
        houses2 = new ArrayList(2);
        customers = new ValueHolder<>(new Vector<Customer>());
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
        Vector<Customer> customers = new Vector<Customer>();
        customers.addElement(Customer.example1());
        customers.addElement(Customer.example2());
        example1.setCustomers(customers);

        Vector<House> houses = new Vector<>();
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
        Vector<Customer> customers = new Vector<>();
        customers.addElement(Customer.example3());
        example2.setCustomers(customers);

        Vector<House> houses = new Vector<>();
        houses.addElement(SingleHouse.example2());
        houses.addElement(TownHouse.example4());
        example2.setHouses(houses);
        return example2;
    }

    public Vector<Customer> getCustomers() {
        return getCustomerValueHolder().getValue();
    }

    public ValueHolderInterface<Vector<Customer>> getCustomerValueHolder() {
        return customers;
    }

    public String getFirstName() {
        return firstName;
    }

    public Vector<House> getHouses() {
        return getHouseValueHolder().getValue();
    }

    public java.util.List getHouses2() {
        return houses2;
    }

    public ValueHolderInterface<Vector<House>> getHouseValueHolder() {
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

    public void setCustomers(Vector<Customer> customerVector) {
        customers.setValue(customerVector);
    }

    public void setCustomerValueHolder(ValueHolderInterface<Vector<Customer>> customers) {
        this.customers = customers;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setHouses(Vector<House> houseVector) {
        getHouseValueHolder().setValue(houseVector);
    }

    public void setHouses2(java.util.List newHouses2) {
        houses2 = newHouses2;

    }

    public void setHouseValueHolder(ValueHolderInterface<Vector<House>> houses) {
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
