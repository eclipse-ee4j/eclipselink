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
package org.eclipse.persistence.testing.models.transparentindirection;

import java.util.*;

/**
 * Simple order object. Just a test fixture.
 * Collections are held in Maps.
 * @author: Big Country
 */
public class MappedOrder extends AbstractOrder {
    public Map salesReps;
    public Collection contacts;// Maps are not allowed for DirectCollections
    public Map lines;

    /**
     * TopLink constructor
     */
    public MappedOrder() {
        super();
    }

    /**
     * Constructor
     */
    public MappedOrder(String customerName) {
        super(customerName);
    }

    public void addContact(String contact) {
        contacts.add(contact);
    }

    public void addLine(AbstractOrderLine line) {
        lines.put(line.getKey(), line);
        line.order = this;
    }

    public void addSalesRep(AbstractSalesRep salesRep) {
        salesReps.put(salesRep.getKey(), salesRep);
        salesRep.addOrder(this);
    }

    public boolean containsContact(String contactName) {
        return contacts.contains(contactName);
    }

    public boolean containsLine(AbstractOrderLine line) {
        return lines.containsValue(line);
    }

    public boolean containsSalesRep(AbstractSalesRep salesRep) {
        return salesReps.containsValue(salesRep);
    }

    public Object getContactContainer() {
        return contacts;
    }

    public Enumeration getContactStream() {
        return (new Vector(contacts)).elements();
    }

    public Object getLineContainer() {
        return lines;
    }

    public Enumeration getLineStream() {
        return (new Vector(lines.values())).elements();
    }

    public int getNumberOfContacts() {
        return contacts.size();
    }

    public int getNumberOfLines() {
        return lines.size();
    }

    public int getNumberOfSalesReps() {
        return salesReps.size();
    }

    public Object getSalesRepContainer() {
        return salesReps;
    }

    public Enumeration getSalesRepStream() {
        return (new Vector(salesReps.values())).elements();
    }

    /**
     * initialize the instance
     */
    protected void initialize() {
        super.initialize();
        salesReps = new Hashtable();
        contacts = new Vector();
        lines = new Hashtable();
    }

    public void removeContact(String contact) {
        contacts.remove(contact);
    }

    public void removeLine(AbstractOrderLine line) {
        lines.remove(line.getKey());
        //    line.order = null;
    }

    public void removeSalesRep(AbstractSalesRep salesRep) {
        salesReps.remove(salesRep.getKey());
        salesRep.removeOrder(this);
    }

    public void clearLines() {
        lines = new Hashtable();
    }
}
