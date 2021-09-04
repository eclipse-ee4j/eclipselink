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
package org.eclipse.persistence.testing.tests.transparentindirection;

import java.util.*;

import junit.framework.TestCase;
import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.mappings.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.indirection.IndirectContainer;
import org.eclipse.persistence.testing.models.transparentindirection.AbstractOrder;
import org.eclipse.persistence.testing.models.transparentindirection.AbstractOrderLine;
import org.eclipse.persistence.testing.models.transparentindirection.AbstractSalesRep;
import org.eclipse.persistence.testing.models.transparentindirection.OrderLine;
import org.eclipse.persistence.testing.models.transparentindirection.SalesRep;
import org.eclipse.persistence.testing.models.transparentindirection.Order;

/**
 * Test the IndirectList with assorted DatabaseSessions and UnitsOfWork.
 * @author Big Country
 */
public class IndirectContainerTestDatabase extends ZTestCase {
    int originalID;

    /**
 * Constructor
 * @param name java.lang.String
 */
    public IndirectContainerTestDatabase(String name) {
        super(name);
    }

    /**
 *
 */
    protected AbstractOrder buildOrderShell() {
        return new Order();
    }

    /**
 *
 */
    protected String buildTestContact1() {
        return "Freddy";
    }

    /**
 *
 */
    protected String buildTestContact2() {
        return "Richard";
    }

    /**
 *
 */
    protected AbstractOrder buildTestOrder1() {
        AbstractOrder order = buildTestOrderShell("Tommy 2Tone");

        order.addSalesRep(newSalesRep("Slippery Sam"));
        order.addSalesRep(newSalesRep("Slippery Sam's Brother"));
        order.addSalesRep(newSalesRep("Slippery Samantha"));

        order.addSalesRep2(newSalesRep("Edgar"));
        order.addSalesRep2(newSalesRep("Rick"));
        order.addSalesRep2(newSalesRep("Dan"));
        order.addSalesRep2(newSalesRep("Johnny"));

        order.addContact("Tommy");
        order.addContact("Pato");
        order.addContact("Ranking Roger");

        order.addContact2("Jimmy");
        order.addContact2("Robert");
        order.addContact2("John");
        order.addContact2("Keith");

        order.addLine(newOrderLine("Specials", 1));
        order.addLine(newOrderLine("General Public", 3));
        order.addLine(newOrderLine("Madness", 1));

        order.setTotal(765);
        order.total2 = 987;

        return order;
    }

    protected AbstractOrder buildTestOrder2() {
        AbstractOrder order = buildTestOrderShell("Ferdinand Fox");

        order.addSalesRep(newSalesRep("Tricky Dick"));
        order.addSalesRep(newSalesRep("Shady Shakeem"));

        order.addSalesRep2(newSalesRep("John"));
        order.addSalesRep2(newSalesRep("Paul"));
        order.addSalesRep2(newSalesRep("George"));
        order.addSalesRep2(newSalesRep("Ringo"));

        order.addContact("Ferdy");
        order.addContact("Franny");

        order.addContact2("Gene");
        order.addContact2("Paul");
        order.addContact2("Ace");
        order.addContact2("Peter");

        order.addLine(newOrderLine("Squirrel Feeder", 3));
        order.addLine(newOrderLine("Squirrel Trap", 6));
        order.addLine(newOrderLine("Squirrel Cookbook", 1));

        order.setTotal(1234);
        order.total2 = 7890;

        return order;
    }
    /**
     *
     */
    protected AbstractOrder buildTestOrder3() {
        AbstractOrder order = buildTestOrderShell("Conform Test");

        order.addSalesRep(newSalesRep("Tom"));

        order.addSalesRep2(newSalesRep("Mark"));

        order.addContact("Jason");

        order.addContact2("Guy");

        order.addLine(newOrderLine(null, 1));

        order.setTotal(3456);
        order.total2 = 5678;

        return order;
    }
    /**
 *
 */
    protected AbstractOrderLine buildTestOrderLine1() {
        return newOrderLine("Spice Girls", 5);
    }

    /**
 *
 */
    protected AbstractOrder buildTestOrderShell(String customerName) {
        return new Order(customerName);
    }

    protected AbstractOrderLine newOrderLine(String item, int quanity) {
        return new OrderLine(item, quanity);
    }

    protected AbstractSalesRep newSalesRep(String name) {
        return new SalesRep(name);
    }

    /**
 *
 */
    protected AbstractSalesRep buildTestSalesRep1() {
        return newSalesRep("Sales Weasel");
    }

    /**
 *
 */
    protected AbstractSalesRep buildTestSalesRep2() {
        return newSalesRep("Uncle Ernie");
    }

    /**
 *
 */
    protected void compareOrders(AbstractOrder expected, AbstractOrder actual) {
        assertEquals("The customer name is incorrect.", expected.customerName, actual.customerName);

        assertTrue("The sales reps should NOT be populated yet.", !((IndirectContainer)actual.getSalesRepContainer()).isInstantiated());
        assertEquals("The number of sales reps is incorrect.", expected.getNumberOfSalesReps(), actual.getNumberOfSalesReps());
        assertTrue("The sales reps should be populated.", ((IndirectContainer)actual.getSalesRepContainer()).isInstantiated());
        assertUnorderedElementsEqual("The sales reps are not correct.", expected.getSalesRepVector(), actual.getSalesRepVector());

        assertTrue("The contacts should NOT be populated.", !((IndirectContainer)actual.getContactContainer()).isInstantiated());
        assertEquals("Number of contacts is incorrect.", expected.getNumberOfContacts(), actual.getNumberOfContacts());
        assertTrue("The contacts should be populated.", ((IndirectContainer)actual.getContactContainer()).isInstantiated());
        assertUnorderedElementsEqual("The contacts are not correct.", expected.getContactVector(), actual.getContactVector());

        assertTrue("The order lines should NOT be populated yet.", !((IndirectContainer)actual.getLineContainer()).isInstantiated());
        assertEquals("The number of order lines is incorrect.", expected.getNumberOfLines(), actual.getNumberOfLines());
        assertTrue("The order lines should be populated.", ((IndirectContainer)actual.getLineContainer()).isInstantiated());
        assertUnorderedElementsEqual("The order lines are not correct.", expected.getLineVector(), actual.getLineVector());

        assertEquals("Number of contacts2 is incorrect.", expected.getNumberOfContacts2(), actual.getNumberOfContacts2());
        assertUnorderedElementsEqual("The contacts2 are not correct.", expected.getContactVector2(), actual.getContactVector2());

        assertEquals("The number of sales reps 2 is incorrect.", expected.getNumberOfSalesReps2(), actual.getNumberOfSalesReps2());
        assertUnorderedElementsEqual("The sales reps 2 are not correct.", expected.getSalesRepVector2(), actual.getSalesRepVector2());

        assertTrue("The total should NOT be instantiated yet.", !(actual.total.isInstantiated()));
        assertEquals("The total is incorrect.", expected.getTotal(), actual.getTotal());
        assertTrue("The total should be instantiated.", actual.total.isInstantiated());

        assertEquals("The total 2 is incorrect.", expected.total2, actual.total2);
    }

    /**
 * Return the database session.
 */
    protected Session getBackdoorSession() {
        return this.getSession();
    }

    /**
 * no Gromit support for this feature yet
 */
    public static void modifyOrderDescriptor(RelationalDescriptor d) {
        ((CollectionMapping)d.getMappingForAttributeName("salesReps")).useTransparentCollection();
        ((CollectionMapping)d.getMappingForAttributeName("contacts")).useTransparentCollection();
        ((CollectionMapping)d.getMappingForAttributeName("lines")).useTransparentCollection();
    }

    protected AbstractOrder readOrder(AbstractOrder key) {
        return (AbstractOrder)this.getSession().readObject(key);
    }

    /**
     * set up test fixtures:
     *   log in to database
     */
    @Override
    protected void setUp() {
        super.setUp();
        AbstractOrder order = this.buildTestOrder1();
        this.writeNewOrder(order);
        originalID = order.id;
        this.getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    @Override
    protected void tearDown() {
        super.tearDown();
    }

    public void testReadAndWriteObject() {
        AbstractOrder key = this.buildOrderShell();
        key.id = originalID;
        AbstractOrder orderFromDB = this.readOrder(key);
        orderFromDB.customerName = "DoubleTake";
        this.updateOrder(orderFromDB);

        this.compareOrders(orderFromDB, orderFromDB);// just verify the instantiations
    }

    /**
 *
 */
    public void testRefreshNewObject() {
        AbstractOrder originalOrder = this.buildTestOrder2();
        this.writeNewOrder(originalOrder);
        // re-read the object to get the clone if necessary
        originalOrder = (AbstractOrder)this.getSession().readObject(originalOrder);

        AbstractOrderLine orderLine = (AbstractOrderLine)((AbstractOrderLine)originalOrder.getLineStream().nextElement()).clone();
        orderLine.itemName = "munged";
        this.getBackdoorSession().executeNonSelectingSQL("update ORDLINE set ITEM_NAME = '" + orderLine.itemName + "' where ID = " + orderLine.id);
        AbstractOrder orderFromDB = (AbstractOrder)this.getSession().refreshObject(originalOrder);

        assertTrue("The order lines should NOT be populated.", !((IndirectContainer)orderFromDB.getLineContainer()).isInstantiated());
        assertTrue("New order line not found.", orderFromDB.containsLine(orderLine));
        assertTrue("The order lines should be populated.", ((IndirectContainer)orderFromDB.getLineContainer()).isInstantiated());

        // there were problems with TransformationMappings, so make sure they work too
        assertTrue("The total should NOT be instantiated yet.", !(orderFromDB.total.isInstantiated()));
        assertEquals("The total is incorrect.", this.buildTestOrder2().getTotal(), orderFromDB.getTotal());
        assertTrue("The total should be instantiated.", orderFromDB.total.isInstantiated());

        assertEquals("The total 2 is incorrect.", this.buildTestOrder2().total2, orderFromDB.total2);
    }

    /**
 *
 */
    public void testRefreshObject() {
        AbstractOrder key = this.buildOrderShell();
        key.id = originalID;
        AbstractOrder orderFromDB = (AbstractOrder)this.getSession().readObject(key);

        assertTrue("The order lines should NOT be populated.", !((IndirectContainer)orderFromDB.getLineContainer()).isInstantiated());
        AbstractOrderLine orderLine = (AbstractOrderLine)orderFromDB.getLineStream().nextElement();
        assertTrue("The order lines should be populated.", ((IndirectContainer)orderFromDB.getLineContainer()).isInstantiated());

        // there were problems with TransformationMappings, so make sure they work too
        AbstractOrder expected = this.buildTestOrder1();
        assertTrue("The total should NOT be instantiated yet.", !(orderFromDB.total.isInstantiated()));
        assertEquals("The total is incorrect.", expected.getTotal(), orderFromDB.getTotal());
        assertTrue("The total should be instantiated.", orderFromDB.total.isInstantiated());

        assertEquals("The total 2 is incorrect.", expected.total2, orderFromDB.total2);

        orderLine = (AbstractOrderLine)orderLine.clone();
        orderLine.itemName = "munged";
        this.getBackdoorSession().executeNonSelectingSQL("update ORDLINE set ITEM_NAME = '" + orderLine.itemName + "' where ID = " + orderLine.id);
        orderFromDB = (AbstractOrder)this.getSession().refreshObject(orderFromDB);

        assertTrue("The order lines should NOT be populated.", !((IndirectContainer)orderFromDB.getLineContainer()).isInstantiated());
        assertTrue("New order line not found.", orderFromDB.containsLine(orderLine));
        assertTrue("The order lines should be populated.", ((IndirectContainer)orderFromDB.getLineContainer()).isInstantiated());

        // there were problems with TransformationMappings, so make sure they work too
        assertTrue("The total should NOT be instantiated yet.", !(orderFromDB.total.isInstantiated()));
        assertEquals("The total is incorrect.", expected.getTotal(), orderFromDB.getTotal());
        assertTrue("The total should be instantiated.", orderFromDB.total.isInstantiated());

        assertEquals("The total 2 is incorrect.", expected.total2, orderFromDB.total2);
    }

    /**
 *
 */
    public void testUOWAddContact() {
        UnitOfWork uow = this.getSession().acquireUnitOfWork();

        AbstractOrder key = this.buildOrderShell();
        key.id = originalID;
        AbstractOrder orderFromDB = (AbstractOrder)uow.readObject(key);
        String contact = this.buildTestContact1();
        orderFromDB.addContact(contact);
        uow.commit();

        this.getSession().getIdentityMapAccessor().initializeAllIdentityMaps();

        orderFromDB = (AbstractOrder)this.getSession().readObject(key);
        assertTrue("The contacts should NOT be populated.", !((IndirectContainer)orderFromDB.getContactContainer()).isInstantiated());
        assertEquals("The number of contacts is incorrect.", this.buildTestOrder1().getNumberOfContacts() + 1, orderFromDB.getNumberOfContacts());
        assertTrue("New contact not found.", orderFromDB.containsContact(contact));
    }

    /**
 *
 */
    public void testUOWAddContact2() {
        UnitOfWork uow = this.getSession().acquireUnitOfWork();

        AbstractOrder key = this.buildOrderShell();
        key.id = originalID;
        AbstractOrder orderFromDB = (AbstractOrder)uow.readObject(key);
        String contact = this.buildTestContact2();
        orderFromDB.addContact2(contact);
        uow.commit();

        this.getSession().getIdentityMapAccessor().initializeAllIdentityMaps();

        orderFromDB = (AbstractOrder)this.getSession().readObject(key);
        assertEquals("The number of contacts2 is incorrect.", this.buildTestOrder1().getNumberOfContacts2() + 1, orderFromDB.getNumberOfContacts2());
        assertTrue("New contact2 not found.", orderFromDB.containsContact2(contact));
    }

    /**
 *
 */
    public void testUOWAddLine() {
        UnitOfWork uow = this.getSession().acquireUnitOfWork();

        AbstractOrder key = this.buildOrderShell();
        key.id = originalID;
        AbstractOrder orderFromDB = (AbstractOrder)uow.readObject(key);
        AbstractOrderLine orderLine = this.buildTestOrderLine1();
        orderFromDB.addLine(orderLine);
        uow.commit();

        this.getSession().getIdentityMapAccessor().initializeAllIdentityMaps();

        orderFromDB = (AbstractOrder)this.getSession().readObject(key);
        assertTrue("The order lines should NOT be populated.", !((IndirectContainer)orderFromDB.getLineContainer()).isInstantiated());
        assertEquals("The number of order lines is incorrect.", this.buildTestOrder1().getNumberOfLines() + 1, orderFromDB.getNumberOfLines());
        assertTrue("New order line not found.", orderFromDB.containsLine(orderLine));
    }

    /**
 *
 */
    public void testUOWAddSalesRep() {
        UnitOfWork uow = this.getSession().acquireUnitOfWork();

        AbstractOrder key = this.buildOrderShell();
        key.id = originalID;
        AbstractOrder orderFromDB = (AbstractOrder)uow.readObject(key);
        AbstractSalesRep salesRep = this.buildTestSalesRep1();
        orderFromDB.addSalesRep(salesRep);
        uow.commit();

        this.getSession().getIdentityMapAccessor().initializeAllIdentityMaps();

        orderFromDB = (AbstractOrder)this.getSession().readObject(key);
        assertTrue("The sales reps should NOT be populated.", !((IndirectContainer)orderFromDB.getSalesRepContainer()).isInstantiated());
        assertEquals("The number of sales reps is incorrect.", this.buildTestOrder1().getNumberOfSalesReps() + 1, orderFromDB.getNumberOfSalesReps());
        assertTrue("New sales rep not found.", orderFromDB.containsSalesRep(salesRep));
    }

    /**
 *
 */
    public void testUOWAddSalesRep2() {
        UnitOfWork uow = this.getSession().acquireUnitOfWork();

        AbstractOrder key = this.buildOrderShell();
        key.id = originalID;
        AbstractOrder orderFromDB = (AbstractOrder)uow.readObject(key);
        AbstractSalesRep salesRep = this.buildTestSalesRep2();
        orderFromDB.addSalesRep2(salesRep);
        uow.commit();

        this.getSession().getIdentityMapAccessor().initializeAllIdentityMaps();

        orderFromDB = (AbstractOrder)this.getSession().readObject(key);
        assertEquals("The number of sales reps 2 is incorrect.", this.buildTestOrder1().getNumberOfSalesReps2() + 1, orderFromDB.getNumberOfSalesReps2());
        assertTrue("New sales rep 2 not found.", orderFromDB.containsSalesRep2(salesRep));
    }

    /**
 *
 */
    public void testUOWChangeTotal() {
        UnitOfWork uow = this.getSession().acquireUnitOfWork();

        AbstractOrder key = this.buildOrderShell();
        key.id = originalID;
        AbstractOrder orderFromDB = (AbstractOrder)uow.readObject(key);
        int newTotal = 1111;
        orderFromDB.setTotal(newTotal);
        uow.commit();

        this.getSession().getIdentityMapAccessor().initializeAllIdentityMaps();

        orderFromDB = (AbstractOrder)this.getSession().readObject(key);
        assertTrue("The total should NOT be instantiated.", !orderFromDB.total.isInstantiated());
        assertEquals("The total is incorrect.", newTotal, orderFromDB.getTotal());
        assertTrue("The total should be instantiated.", orderFromDB.total.isInstantiated());
    }

    public void testUOWChangeTotal2() {
        UnitOfWork uow = this.getSession().acquireUnitOfWork();

        AbstractOrder key = this.buildOrderShell();
        key.id = originalID;
        AbstractOrder orderFromDB = (AbstractOrder)uow.readObject(key);
        int newTotal2 = 2222;
        orderFromDB.total2 = newTotal2;
        uow.commit();

        this.getSession().getIdentityMapAccessor().initializeAllIdentityMaps();

        orderFromDB = (AbstractOrder)this.getSession().readObject(key);
        assertEquals("The total 2 is incorrect.", newTotal2, orderFromDB.total2);
    }

    public void testUOWNewObject() {
        UnitOfWork uow = this.getSession().acquireUnitOfWork();
        AbstractOrder originalOrder = this.buildTestOrder2();
        uow.registerObject(originalOrder);
        uow.commit();
        this.getSession().getIdentityMapAccessor().initializeAllIdentityMaps();

        AbstractOrder key = this.buildOrderShell();
        key.id = originalOrder.id;
        AbstractOrder orderFromDB = (AbstractOrder)this.getSession().readObject(key);

        this.compareOrders(originalOrder, orderFromDB);
    }

    /**
 *
 */
    public void testUOWRemoveContact() {
        UnitOfWork uow = this.getSession().acquireUnitOfWork();

        AbstractOrder key = this.buildOrderShell();
        key.id = originalID;
        AbstractOrder orderFromDB = (AbstractOrder)uow.readObject(key);
        String contact = (String)orderFromDB.getContactStream().nextElement();
        orderFromDB.removeContact(contact);
        uow.commit();

        this.getSession().getIdentityMapAccessor().initializeAllIdentityMaps();

        orderFromDB = (AbstractOrder)this.getSession().readObject(key);
        assertTrue("The contacts should NOT be populated.", !((IndirectContainer)orderFromDB.getContactContainer()).isInstantiated());
        assertEquals("The number of contacts is incorrect.", this.buildTestOrder1().getNumberOfContacts() - 1, orderFromDB.getNumberOfContacts());
        assertTrue("Removed contact still present.", !orderFromDB.containsContact(contact));
    }

    /**
 *
 */
    public void testUOWRemoveContact2() {
        UnitOfWork uow = this.getSession().acquireUnitOfWork();

        AbstractOrder key = this.buildOrderShell();
        key.id = originalID;
        AbstractOrder orderFromDB = (AbstractOrder)uow.readObject(key);
        String contact = (String)orderFromDB.getContactStream2().nextElement();
        orderFromDB.removeContact2(contact);
        uow.commit();

        this.getSession().getIdentityMapAccessor().initializeAllIdentityMaps();

        orderFromDB = (AbstractOrder)this.getSession().readObject(key);
        assertEquals("The number of contacts2 is incorrect.", this.buildTestOrder1().getNumberOfContacts2() - 1, orderFromDB.getNumberOfContacts2());
        assertTrue("Removed contact2 still present.", !orderFromDB.containsContact2(contact));
    }

    /**
 *
 */
    public void testUOWRemoveLine() {
        UnitOfWork uow = this.getSession().acquireUnitOfWork();

        AbstractOrder key = this.buildOrderShell();
        key.id = originalID;
        AbstractOrder orderFromDB = (AbstractOrder)uow.readObject(key);
        AbstractOrderLine orderLine = (AbstractOrderLine)orderFromDB.getLineStream().nextElement();
        orderFromDB.removeLine(orderLine);
        uow.commit();

        this.getSession().getIdentityMapAccessor().initializeAllIdentityMaps();

        orderFromDB = (AbstractOrder)this.getSession().readObject(key);
        assertTrue("The order lines should NOT be populated.", !((IndirectContainer)orderFromDB.getLineContainer()).isInstantiated());
        assertEquals("The number of order lines is incorrect.", this.buildTestOrder1().getNumberOfLines() - 1, orderFromDB.getNumberOfLines());
        assertTrue("Removed order line still present.", !orderFromDB.containsLine(orderLine));
    }

    /**
 *
 */
    public void testUOWRemoveSalesRep() {
        UnitOfWork uow = this.getSession().acquireUnitOfWork();

        AbstractOrder key = this.buildOrderShell();
        key.id = originalID;
        AbstractOrder orderFromDB = (AbstractOrder)uow.readObject(key);
        AbstractSalesRep salesRep = (AbstractSalesRep)orderFromDB.getSalesRepStream().nextElement();
        orderFromDB.removeSalesRep(salesRep);
        uow.commit();

        this.getSession().getIdentityMapAccessor().initializeAllIdentityMaps();

        orderFromDB = (AbstractOrder)this.getSession().readObject(key);
        assertTrue("The sales reps should NOT be populated.", !((IndirectContainer)orderFromDB.getSalesRepContainer()).isInstantiated());
        assertEquals("The number of sales reps is incorrect.", this.buildTestOrder1().getNumberOfSalesReps() - 1, orderFromDB.getNumberOfSalesReps());
        assertTrue("Removed sales rep still present.", !orderFromDB.containsSalesRep(salesRep));
    }

    /**
 *
 */
    public void testUOWRemoveSalesRep2() {
        UnitOfWork uow = this.getSession().acquireUnitOfWork();

        AbstractOrder key = this.buildOrderShell();
        key.id = originalID;
        AbstractOrder orderFromDB = (AbstractOrder)uow.readObject(key);
        AbstractSalesRep salesRep = (AbstractSalesRep)orderFromDB.getSalesRepStream2().nextElement();
        orderFromDB.removeSalesRep2(salesRep);
        uow.commit();

        this.getSession().getIdentityMapAccessor().initializeAllIdentityMaps();

        orderFromDB = (AbstractOrder)this.getSession().readObject(key);
        assertEquals("The number of sales reps 2 is incorrect.", this.buildTestOrder1().getNumberOfSalesReps2() - 1, orderFromDB.getNumberOfSalesReps2());
        assertTrue("Removed sales rep 2 still present.", !orderFromDB.containsSalesRep2(salesRep));
    }

    /**
 *
 */
    public void testUOWUnchanged() {
        UnitOfWork uow = this.getSession().acquireUnitOfWork();

        AbstractOrder key = this.buildOrderShell();
        key.id = originalID;
        AbstractOrder orderFromDB = (AbstractOrder)uow.readObject(key);
        assertTrue("The sales reps should NOT be populated.", !((IndirectContainer)orderFromDB.getSalesRepContainer()).isInstantiated());
        assertTrue("The order lines should NOT be populated.", !((IndirectContainer)orderFromDB.getLineContainer()).isInstantiated());
        assertTrue("The contacts should NOT be populated.", !((IndirectContainer)orderFromDB.getContactContainer()).isInstantiated());
        uow.commit();

        assertTrue("The sales reps should NOT be populated.", !((IndirectContainer)orderFromDB.getSalesRepContainer()).isInstantiated());
        assertEquals("The number of sales reps is incorrect.", this.buildTestOrder1().getNumberOfSalesReps(), orderFromDB.getNumberOfSalesReps());
        assertTrue("The sales reps should be populated.", ((IndirectContainer)orderFromDB.getSalesRepContainer()).isInstantiated());

        assertTrue("The contacts should NOT be populated.", !((IndirectContainer)orderFromDB.getContactContainer()).isInstantiated());
        assertEquals("The number of contacts is incorrect.", this.buildTestOrder1().getNumberOfContacts(), orderFromDB.getNumberOfContacts());
        assertTrue("The contacts should be populated.", ((IndirectContainer)orderFromDB.getContactContainer()).isInstantiated());

        assertTrue("The order lines should NOT be populated.", !((IndirectContainer)orderFromDB.getLineContainer()).isInstantiated());
        assertEquals("The number of order lines is incorrect.", this.buildTestOrder1().getNumberOfLines(), orderFromDB.getNumberOfLines());
        assertTrue("The order lines should be populated.", ((IndirectContainer)orderFromDB.getLineContainer()).isInstantiated());

        assertEquals("The number of contacts2 is incorrect.", this.buildTestOrder1().getNumberOfContacts2(), orderFromDB.getNumberOfContacts2());

        assertEquals("The number of sales reps 2 is incorrect.", this.buildTestOrder1().getNumberOfSalesReps2(), orderFromDB.getNumberOfSalesReps2());

        assertTrue("The total should NOT be instantiated yet.", !(orderFromDB.total.isInstantiated()));
        assertEquals("The total is incorrect.", this.buildTestOrder1().getTotal(), orderFromDB.getTotal());
        assertTrue("The total should be instantiated.", orderFromDB.total.isInstantiated());

        assertEquals("The total 2 is incorrect.", this.buildTestOrder1().total2, orderFromDB.total2);
    }

    /**
     * Test that the uow works when the collection is replaced without first accessing the original.
     */
    public void testUOWReplaceCollection() {
        UnitOfWork uow = this.getSession().acquireUnitOfWork();

        AbstractOrder key = this.buildOrderShell();
        key.id = originalID;
        AbstractOrder orderFromDB = (AbstractOrder)uow.readObject(key);
        orderFromDB.clearLines();
        uow.commit();

        this.getSession().getIdentityMapAccessor().initializeAllIdentityMaps();

        orderFromDB = (AbstractOrder)this.getSession().readObject(key);
        assertTrue("Order lines were not removed.", orderFromDB.getLineVector().isEmpty());
    }

    /**
 *
 */
    public void testMergeCloneWithSerializedTransparentIndirection() {
        UnitOfWork uow = getSession().acquireUnitOfWork();
        Order originalClone = (Order)uow.readObject(Order.class);
        originalClone.addSalesRep(new SalesRep("George the Customer"));
        uow.release();
        uow = this.getSession().acquireUnitOfWork();
        uow.readObject(originalClone);
        uow.mergeCloneWithReferences(originalClone);
        try {
            uow.commit();
        } catch (NullPointerException ex) {
            assertTrue("Merging of the clone did not trigger the back up value holder" + ex.toString(), false);
        }
    }

    /**
 *
 */
    public void testWriteAndReadObject() {
        AbstractOrder originalOrder = this.buildTestOrder2();
        this.writeNewOrder(originalOrder);

        this.getSession().getIdentityMapAccessor().initializeAllIdentityMaps();

        AbstractOrder key = this.buildOrderShell();
        key.id = originalOrder.id;
        AbstractOrder orderFromDB = (AbstractOrder)this.getSession().readObject(key);

        this.compareOrders(originalOrder, orderFromDB);
    }

    public void updateOrder(AbstractOrder order) {
        this.getDatabaseSession().writeObject(order);
    }

    /**
     * write out the new order
     */
    protected void writeNewOrder(AbstractOrder order) {
        for (Enumeration stream = order.getSalesRepStream(); stream.hasMoreElements();) {
            this.getDatabaseSession().writeObject(stream.nextElement());
        }
        for (Enumeration stream = order.getSalesRepStream2(); stream.hasMoreElements();) {
            this.getDatabaseSession().writeObject(stream.nextElement());
        }
        this.getDatabaseSession().writeObject(order);
    }
}
