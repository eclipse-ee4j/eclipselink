/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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

import java.util.*;
import org.eclipse.persistence.tools.schemaframework.TableDefinition;
import org.eclipse.persistence.indirection.*;

public class Customer {
    private java.lang.String name;
    private java.math.BigDecimal id;
    private int income;
    private ValueHolderInterface dependants;
    private ValueHolderInterface company;

    /**
     * PhoneAgg constructor comment.
     */
    public Customer() {
        super();
        dependants = new ValueHolder(new Vector());
        company = new ValueHolder();
    }

    public void addDependant(Dependant dependant) {
        getDependants().addElement(dependant);
    }

    public static Customer example1() {
        Customer example1 = new Customer();

        example1.setName("Bob Smith");
        example1.setIncome(94320);
        example1.setCompany(Company.example1());
        Vector dependants = new Vector(2);
        dependants.addElement(Dependant.example1());
        dependants.addElement(Dependant.example2());
        example1.setDependants(dependants);
        return example1;
    }

    public static Customer example2() {
        Customer example2 = new Customer();

        example2.setName("Jack Johnson");
        example2.setIncome(773388);
        example2.setCompany(Company.example2());
        Vector dependants = new Vector(1);
        dependants.addElement(Dependant.example3());
        example2.setDependants(dependants);
        return example2;
    }

    public static Customer example3() {
        Customer example3 = new Customer();

        example3.setName("Linda Lindros");
        example3.setIncome(38338);
        example3.setCompany(Company.example3());
        Vector dependants = new Vector(2);
        dependants.addElement(Dependant.example4());
        dependants.addElement(Dependant.example5());
        example3.setDependants(dependants);

        return example3;
    }

    public Company getCompany() {
        return (Company)company.getValue();
    }

    public org.eclipse.persistence.indirection.ValueHolderInterface getCompanyValueHolder() {
        return company;
    }

    public Vector getDependants() {
        return (Vector)dependants.getValue();
    }

    public ValueHolderInterface getDependantValueHolder() {
        return dependants;
    }

    public java.math.BigDecimal getId() {
        return id;
    }

    public int getIncome() {
        return income;
    }

    /**
     * Insert the method's description here.
     * Creation date: (04/11/2000 11:43:54 AM)
     * @return java.lang.String
     */
    public java.lang.String getName() {
        return name;
    }

    public void removeDependant(Dependant dependant) {
        getDependants().removeElement(dependant);
    }

    public void setCompany(Company newCompany) {
        company.setValue(newCompany);
    }

    public void setCompanyValueHolder(ValueHolderInterface newCompany) {
        company = newCompany;
    }

    public void setDependants(Vector dependants) {
        getDependantValueHolder().setValue(dependants);
    }

    public void setDependantValueHolder(ValueHolderInterface dependants) {
        this.dependants = dependants;
    }

    /**
     * Insert the method's description here.
     * Creation date: (04/11/2000 11:45:34 AM)
     * @param newId java.math.BigDecimal
     */
    public void setId(java.math.BigDecimal newId) {
        id = newId;
    }

    /**
     * Insert the method's description here.
     * Creation date: (04/11/2000 12:10:54 PM)
     * @param newIncome int
     */
    public void setIncome(int newIncome) {
        income = newIncome;
    }

    /**
     * Insert the method's description here.
     * Creation date: (04/11/2000 11:43:54 AM)
     * @param newName java.lang.String
     */
    public void setName(java.lang.String newName) {
        name = newName;
    }

    /**
     * Return a platform independant definition of the database table.
     */
    public static TableDefinition tableDefinition() {
        TableDefinition definition = new TableDefinition();

        definition.setName("CUSTOMER");

        definition.addIdentityField("CUSTOMER_ID", java.math.BigDecimal.class, 15);
        definition.addField("AGENT_ID", java.math.BigDecimal.class, 15);
        definition.addField("COMPANY_ID", java.math.BigDecimal.class, 15);
        definition.addField("NAME", String.class, 30);
        definition.addField("INCOME", Integer.class, 20);
        return definition;
    }
}
