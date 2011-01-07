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
package org.eclipse.persistence.testing.models.relationshipmaintenance;

import java.util.*;
import org.eclipse.persistence.indirection.*;

public class Customer {
    public java.util.Collection salespeople;
    public int id;
    public java.lang.String name;

    /**
     * Customer constructor comment.
     */
    public Customer() {
        salespeople = new IndirectSet();
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
