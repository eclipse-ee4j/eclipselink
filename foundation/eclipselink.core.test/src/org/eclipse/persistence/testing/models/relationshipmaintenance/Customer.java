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

import java.util.Collection;

import org.eclipse.persistence.indirection.IndirectCollectionsFactory;

public class Customer {
    public java.util.Collection salespeople;
    public int id;
    public java.lang.String name;

    /**
     * Customer constructor comment.
     */
    public Customer() {
        salespeople = IndirectCollectionsFactory.createIndirectSet();
    }

    public void addSalesPerson(SalesPerson aSalesPerson) {
        getSalespeople().add(aSalesPerson);
    }

    public static Customer example1() {
        Customer instance = new Customer();
        instance.setName("Bigfoot");
        return instance;
    }

    public static Customer example2() {
        Customer instance = new Customer();
        instance.setName("Nessie");
        return instance;
    }

    public static Customer example3() {
        Customer instance = new Customer();
        instance.setName("Ogopogo");
        return instance;
    }

    public static Customer example4() {
        Customer instance = new Customer();
        instance.setName("Chupacabra");
        return instance;
    }

    public static Customer example5() {
        Customer instance = new Customer();
        instance.setName("Yeti");
        return instance;
    }

    /**
     * Insert the method's description here.
     * Creation date: (1/30/01 6:52:00 PM)
     * @return int
     */
    public int getId() {
        return id;
    }

    /**
     * Insert the method's description here.
     * Creation date: (1/30/01 7:28:01 PM)
     * @return java.lang.String
     */
    public java.lang.String getName() {
        return name;
    }

    public Collection getSalespeople() {
        return this.salespeople;
    }

    /**
     * Insert the method's description here.
     * Creation date: (1/30/01 6:52:00 PM)
     * @param newId int
     */
    public void setId(int newId) {
        id = newId;
    }

    /**
     * Insert the method's description here.
     * Creation date: (1/30/01 7:28:01 PM)
     * @param newName java.lang.String
     */
    public void setName(java.lang.String newName) {
        name = newName;
    }

    public void setSalespeople(Collection collection) {
        this.salespeople = collection;
    }

    public String toString() {
        return org.eclipse.persistence.internal.helper.Helper.getShortClassName(this) + "(" + id + ", " + System.identityHashCode(this) + ")";
    }
}
