/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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

    @Override
    public void addContact(String contact) {
        contacts.add(contact);
    }

    @Override
    public void addLine(AbstractOrderLine line) {
        lines.put(line.getKey(), line);
        line.order = this;
    }

    @Override
    public void addSalesRep(AbstractSalesRep salesRep) {
        salesReps.put(salesRep.getKey(), salesRep);
        salesRep.addOrder(this);
    }

    @Override
    public boolean containsContact(String contactName) {
        return contacts.contains(contactName);
    }

    @Override
    public boolean containsLine(AbstractOrderLine line) {
        return lines.containsValue(line);
    }

    @Override
    public boolean containsSalesRep(AbstractSalesRep salesRep) {
        return salesReps.containsValue(salesRep);
    }

    @Override
    public Object getContactContainer() {
        return contacts;
    }

    @Override
    public Enumeration getContactStream() {
        return (new Vector(contacts)).elements();
    }

    @Override
    public Object getLineContainer() {
        return lines;
    }

    @Override
    public Enumeration getLineStream() {
        return (new Vector(lines.values())).elements();
    }

    @Override
    public int getNumberOfContacts() {
        return contacts.size();
    }

    @Override
    public int getNumberOfLines() {
        return lines.size();
    }

    @Override
    public int getNumberOfSalesReps() {
        return salesReps.size();
    }

    @Override
    public Object getSalesRepContainer() {
        return salesReps;
    }

    @Override
    public Enumeration getSalesRepStream() {
        return (new Vector(salesReps.values())).elements();
    }

    /**
     * initialize the instance
     */
    @Override
    protected void initialize() {
        super.initialize();
        salesReps = new Hashtable();
        contacts = new Vector();
        lines = new Hashtable();
    }

    @Override
    public void removeContact(String contact) {
        contacts.remove(contact);
    }

    @Override
    public void removeLine(AbstractOrderLine line) {
        lines.remove(line.getKey());
        //    line.order = null;
    }

    @Override
    public void removeSalesRep(AbstractSalesRep salesRep) {
        salesReps.remove(salesRep.getKey());
        salesRep.removeOrder(this);
    }

    @Override
    public void clearLines() {
        lines = new Hashtable();
    }
}
