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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.transparentindirection;

import java.util.*;
import java.io.*;
import org.eclipse.persistence.indirection.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.testing.tests.transparentindirection.TestHashtable;

/**
 * Simple order object. Just a test fixture.
 * @author: Big Country
 */
public abstract class AbstractOrder implements Serializable {
    public int id;
    public String customerName;
    public Stack contacts2;// test NoIndirectionPolicy for Vectors
    public TestHashtable salesReps2;// test NoIndirectionPolicy for Hashtables
    public ValueHolderInterface total;// test TransformationMapping
    public int total2;

    /**
     * TopLink constructor
     */
    public AbstractOrder() {
        super();
        this.initialize();
    }

    /**
     * Constructor
     */
    public AbstractOrder(String customerName) {
        this();
        this.initialize(customerName);
    }

    public abstract void addContact(String contact);

    public void addContact2(String contact) {
        contacts2.addElement(contact);
    }

    public abstract void addLine(OrderLine line);

    public abstract void addSalesRep(SalesRep salesRep);

    public void addSalesRep2(SalesRep salesRep) {
        salesReps2.put(salesRep.getKey(), salesRep);
        salesRep.addOrder2(this);
    }

    public abstract boolean containsContact(String contactName);

    public boolean containsContact2(String contactName) {
        return contacts2.contains(contactName);
    }

    public abstract boolean containsLine(OrderLine line);

    public abstract boolean containsSalesRep(SalesRep salesRep);

    public boolean containsSalesRep2(SalesRep salesRep) {
        return salesReps2.contains(salesRep);
    }

    public Vector getAttributeVector(Enumeration stream) {
        Vector result = new Vector();
        while (stream.hasMoreElements()) {
            result.addElement(stream.nextElement());
        }
        return result;
    }

    public abstract Object getContactContainer();

    public Object getContactContainer2() {
        return contacts2;
    }

    public abstract Enumeration getContactStream();

    public Enumeration getContactStream2() {
        return contacts2.elements();
    }

    public Vector getContactVector() {
        return getAttributeVector(this.getContactStream());
    }

    public Vector getContactVector2() {
        return getAttributeVector(this.getContactStream2());
    }

    public abstract Object getLineContainer();

    public abstract void clearLines();

    public abstract Enumeration getLineStream();

    public Vector getLineVector() {
        return getAttributeVector(this.getLineStream());
    }

    public abstract int getNumberOfContacts();

    public int getNumberOfContacts2() {
        return contacts2.size();
    }

    public abstract int getNumberOfLines();

    public abstract int getNumberOfSalesReps();

    public int getNumberOfSalesReps2() {
        return salesReps2.size();
    }

    public abstract Object getSalesRepContainer();

    public Object getSalesRepContainer2() {
        return salesReps2;
    }

    public abstract Enumeration getSalesRepStream();

    public Enumeration getSalesRepStream2() {
        return salesReps2.elements();
    }

    public Vector getSalesRepVector() {
        return getAttributeVector(this.getSalesRepStream());
    }

    public Vector getSalesRepVector2() {
        return getAttributeVector(this.getSalesRepStream2());
    }

    public int getTotal() {
        return ((Integer)total.getValue()).intValue();
    }

    public int getTotalFromRow(Record row, Session session) {
        int tens = ((Number)row.get("TOTT")).intValue();
        int ones = ((Number)row.get("TOTO")).intValue();
        return (tens * 10) + ones;
    }

    public int getTotalFromRow2(Record row, Session session) {
        int tens = ((Number)row.get("TOTT2")).intValue();
        int ones = ((Number)row.get("TOTO2")).intValue();
        return (tens * 10) + ones;
    }

    public int getTotalOnes() {
        return this.getTotal() - ((this.getTotal() / 10) * 10);
    }

    public int getTotalOnes2() {
        return total2 - ((total2 / 10) * 10);
    }

    public int getTotalTens() {
        return this.getTotal() / 10;
    }

    public int getTotalTens2() {
        return total2 / 10;
    }

    /**
     * initialize the instance
     */
    protected void initialize() {
        this.contacts2 = new Stack();
        this.salesReps2 = new TestHashtable();
        this.total = new ValueHolder(new Integer(1));
        this.total2 = 0;
    }

    /**
     * initialize the instance
     */
    protected void initialize(String customerName) {
        this.customerName = customerName;
    }

    public abstract void removeContact(String contact);

    public void removeContact2(String contact) {
        contacts2.removeElement(contact);
    }

    public abstract void removeLine(OrderLine line);

    public abstract void removeSalesRep(SalesRep salesRep);

    public void removeSalesRep2(SalesRep salesRep) {
        salesReps2.remove(salesRep.getKey());
        salesRep.removeOrder2(this);
    }

    public void setTotal(int total) {
        this.total.setValue(new Integer(total));
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer(1000);
        buffer.append("Order(");
        buffer.append(id);
        buffer.append(": ");
        buffer.append(customerName);
        buffer.append(")");
        Object container = this.getLineContainer();
        if (container == null) {
            return buffer.toString();
        }

        buffer.append(System.getProperty("line.separator"));
        if ((container instanceof IndirectContainer) && (!((IndirectContainer)container).isInstantiated())) {
            buffer.append("\t");
            buffer.append("uninstantiated order lines...");
        } else {
            for (Enumeration stream = getLineStream(); stream.hasMoreElements();) {
                buffer.append("\t");
                buffer.append(stream.nextElement());
                buffer.append(System.getProperty("line.separator"));
            }
        }
        return buffer.toString();
    }
}
