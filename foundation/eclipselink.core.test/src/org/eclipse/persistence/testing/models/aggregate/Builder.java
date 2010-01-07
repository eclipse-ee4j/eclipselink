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
 *     ailitchev - jpa 2.0 element collections support
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.aggregate;

import java.io.*;
import java.util.*;

import org.eclipse.persistence.tools.schemaframework.FieldDefinition;
import org.eclipse.persistence.tools.schemaframework.TableDefinition;

public class Builder implements Serializable {
    private String firstName;
    private String lastName;
    private java.math.BigDecimal id;
    private List houses;
    private List customers;
    private List vehicles;

    public Builder() {
        super();
        houses = new ArrayList(2);
        customers = new ArrayList(2);
        vehicles = new ArrayList(2);
    }

    public void addCustomer(Customer customer) {
        getCustomers().add(customer);
    }

    public void addHouse(House house) {
        getHouses().add(house);
    }

    public void addVehicle(Vehicle vehicle) {
        getVehicles().add(vehicle);
    }

    public static Builder example1() {
        Builder example1 = new Builder();

        example1.setFirstName("Micheal");
        example1.setLastName("Jordan");
        
        example1.addCustomer(Customer.example1());
        example1.addCustomer(Customer.example2());

        example1.addHouse(SingleHouse.example1());
        example1.addHouse(TownHouse.example3());

        example1.addVehicle(Car.example1());
        example1.addVehicle(Car.example2());
        example1.addVehicle(Car.example3());
        example1.addVehicle(Bicycle.example1());
        example1.addVehicle(Bicycle.example2());
        
        return example1;
    }

    public List getCustomers() {
        return customers;
    }

    public String getFirstName() {
        return firstName;
    }

    public List getHouses() {
        return houses;
    }

    public java.math.BigDecimal getId() {
        return id;
    }

    public String getLastName() {
        return lastName;
    }

    public List getVehicles() {
        return vehicles;
    }

    public void removeCustomer(Customer customer) {
        getCustomers().remove(customer);
    }

    public void removeHouse(House house) {
        getHouses().remove(house);
    }

    public void removeVehicle(Vehicle vehicle) {
        getVehicles().remove(vehicle);
    }

    public void setCustomers(List customers) {
        this.customers = customers;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setHouses(List houses) {
        this.houses = houses;
    }

    public void setId(java.math.BigDecimal id) {
        this.id = id;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setVehicles(List vehicles) {
        this.vehicles = vehicles;
    }

    /**
     * Return a platform independent definition of the database table.
     */
    public static TableDefinition tableDefinition() {
        TableDefinition definition = new TableDefinition();

        definition.setName("BUILDER");

        definition.addIdentityField("BUILDER_ID", java.math.BigDecimal.class, 15);
        definition.addField("FNAME", String.class, 30);
        definition.addField("LNAME", String.class, 30);
        definition.addField("VERSION", java.math.BigDecimal.class, 15);
        return definition;
    }

    /**
     * Return a platform independent definition of the database table.
     */
    public static TableDefinition houseTableDefinition() {
        TableDefinition definition = House.tableDefinition();
        
        definition.setName("BUILDER_HOUSE");
        
        for(int i=0; i < definition.getFields().size(); i++) {
            FieldDefinition field = (FieldDefinition)definition.getFields().get(i);
            if(field.getName().equals("AGENT_ID")) {
                field.setName("BUILDER_ID");
                break;
            }
        }
        return definition;
    }

    /**
     * Return a platform independent definition of the database table.
     */
    public static TableDefinition singleHouseTableDefinition() {
        TableDefinition definition = SingleHouse.tableDefinition();
        
        definition.setName("BUILDER_SINGLE_HOUSE");
        
        for(int i=0; i < definition.getFields().size(); i++) {
            FieldDefinition field = (FieldDefinition)definition.getFields().get(i);
            if(field.getName().equals("AGENT_ID")) {
                field.setName("BUILDER_ID");
                break;
            }
        }
        return definition;
    }

    /**
     * Return a platform independent definition of the database table.
     */
    public static TableDefinition sellingPointTableDefinition() {
        TableDefinition definition = SellingPoint.tableDefinition();
        
        definition.setName("BUILDER_SELLING_POINT");
        
        for(int i=0; i < definition.getFields().size(); i++) {
            FieldDefinition field = (FieldDefinition)definition.getFields().get(i);
            if(field.getName().equals("AGENT_ID")) {
                field.setName("BUILDER_ID");
                break;
            }
        }
        return definition;
    }

    /**
     * Return a platform independent definition of the database table.
     */
    public static TableDefinition customerTableDefinition() {
        TableDefinition definition = Customer.tableDefinition();
        
        definition.setName("BUILDER_CUSTOMER");
        
        for(int i=0; i < definition.getFields().size(); i++) {
            FieldDefinition field = (FieldDefinition)definition.getFields().get(i);
            if(field.getName().equals("AGENT_ID")) {
                field.setName("BUILDER_ID");
                break;
            }
        }
        return definition;
    }

    /**
     * Return a platform independent definition of the database table.
     */
    public static TableDefinition dependantTableDefinition() {
        TableDefinition definition = Dependant.tableDefinition();
        
        definition.setName("BUILDER_DEPENDANT");
        
        return definition;
    }

    /**
     * Return a platform independent definition of the database table.
     */
    public static TableDefinition vehicleTableDefinition() {
        TableDefinition definition = Vehicle.tableDefinition();

        definition.setName("BUILDER_VEHICLE");

        for(int i=0; i < definition.getFields().size(); i++) {
            FieldDefinition field = (FieldDefinition)definition.getFields().get(i);
            if(field.getName().equals("ID")) {
                field.setName("BUILDER_ID");
                field.setIsPrimaryKey(false);
                break;
            }
        }

        return definition;
    }
}