/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.models.relationshipmaintenance;

import java.util.*;

import org.eclipse.persistence.indirection.IndirectCollectionsFactory;
import org.eclipse.persistence.indirection.ValueHolder;
import org.eclipse.persistence.indirection.ValueHolderInterface;

public class SalesPerson {
    public int id;
    public java.util.Collection customers;
    public java.lang.String name;
    public ValueHolderInterface fieldOffice;

    /**
     * SalesPerson constructor comment.
     */
    public SalesPerson() {
        fieldOffice = new ValueHolder();
        customers = IndirectCollectionsFactory.createIndirectSet();
    }

    public void addCustomer(Customer aCustomer) {
        getCustomers().add(aCustomer);
    }

    public static SalesPerson example1() {
        SalesPerson instance = new SalesPerson();
        instance.setName("Herb Tarlek");
        return instance;
    }

    public static SalesPerson example2() {
        SalesPerson instance = new SalesPerson();
        instance.setName("Katherine Sayn-W");
        return instance;
    }

    public static SalesPerson example3() {
        SalesPerson instance = new SalesPerson();
        instance.setName("Brian Skelton");
        return instance;
    }

    public static SalesPerson example4() {
        SalesPerson instance = new SalesPerson();
        instance.setName("Yash Gupta");
        return instance;
    }

    public static SalesPerson example5() {
        SalesPerson instance = new SalesPerson();
        instance.setName("Henri Richard");
        return instance;
    }

    public Collection getCustomers() {
        return this.customers;
    }

    public FieldOffice getFieldOffice() {
        return (FieldOffice)fieldOffice.getValue();
    }

    public int getId() {
        return id;
    }

    /**
     * Insert the method's description here.
     * Creation date: (1/30/01 7:29:14 PM)
     * @return java.lang.String
     */
    public java.lang.String getName() {
        return name;
    }

    public void setCustomers(Collection newCustomers) {
        this.customers = newCustomers;
    }

    public void setFieldOffice(FieldOffice newFieldOffice) {
        fieldOffice.setValue(newFieldOffice);
    }

    /**
     * Insert the method's description here.
     * Creation date: (1/30/01 7:06:40 PM)
     * @param newId int
     */
    public void setId(int newId) {
        id = newId;
    }

    /**
     * Insert the method's description here.
     * Creation date: (1/30/01 7:29:14 PM)
     * @param newName java.lang.String
     */
    public void setName(java.lang.String newName) {
        name = newName;
    }

    public String toString() {
        return org.eclipse.persistence.internal.helper.Helper.getShortClassName(this) + "(" + id + ", " + System.identityHashCode(this) + ")";
    }
}
