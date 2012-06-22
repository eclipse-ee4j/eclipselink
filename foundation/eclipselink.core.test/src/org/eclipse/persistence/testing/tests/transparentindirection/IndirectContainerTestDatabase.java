/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.transparentindirection;

import java.util.*;
import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.mappings.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.indirection.IndirectContainer;
import org.eclipse.persistence.testing.models.transparentindirection.AbstractOrder;
import org.eclipse.persistence.testing.models.transparentindirection.SalesRep;
import org.eclipse.persistence.testing.models.transparentindirection.OrderLine;
import org.eclipse.persistence.testing.models.transparentindirection.Order;

/**
 * Test the IndirectList with assorted DatabaseSessions and UnitsOfWork.
 * @author: Big Country
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

        order.addSalesRep(new SalesRep("Slippery Sam"));
        order.addSalesRep(new SalesRep("Slippery Sam's Brother"));
        order.addSalesRep(new SalesRep("Slippery Samantha"));

        order.addSalesRep2(new SalesRep("Edgar"));
        order.addSalesRep2(new SalesRep("Rick"));
        order.addSalesRep2(new SalesRep("Dan"));
        order.addSalesRep2(new SalesRep("Johnny"));

        order.addContact("Tommy");
        order.addContact("Pato");
        order.addContact("Ranking Roger");

        order.addContact2("Jimmy");
        order.addContact2("Robert");
        order.addContact2("John");
        order.addContact2("Keith");

        order.addLine(new OrderLine("Specials", 1));
        order.addLine(new OrderLine("General Public", 3));
        order.addLine(new OrderLine("Madness", 1));

        order.setTotal(765);
        order.total2 = 987;

        return order;
    }

    protected AbstractOrder buildTestOrder2() {
        AbstractOrder order = buildTestOrderShell("Ferdinand Fox");

        order.addSalesRep(new SalesRep("Tricky Dick"));
        order.addSalesRep(new SalesRep("Shady Shakeem"));

        order.addSalesRep2(new SalesRep("John"));
        order.addSalesRep2(new SalesRep("Paul"));
        order.addSalesRep2(new SalesRep("George"));
        order.addSalesRep2(new SalesRep("Ringo"));

        order.addContact("Ferdy");
        order.addContact("Franny");

        order.addContact2("Gene");
        order.addContact2("Paul");
        order.addContact2("Ace");
        order.addContact2("Peter");

        order.addLine(new OrderLine("Squirrel Feeder", 3));
        order.addLine(new OrderLine("Squirrel Trap", 6));
        order.addLine(new OrderLine("Squirrel Cookbook", 1));

        order.setTotal(1234);
        order.total2 = 7890;

        return order;
    }
	/**
	 * 
	 */
	protected AbstractOrder buildTestOrder3() {
		AbstractOrder order = buildTestOrderShell("Conform Test");

		order.addSalesRep(new SalesRep("Tom"));

		order.addSalesRep2(new SalesRep("Mark"));

		order.addContact("Jason");

		order.addContact2("Guy");

		order.addLine(new OrderLine());

		order.setTotal(3456);
		order.total2 = 5678;

		return order;
	}
    /**
 *
 */
    protected OrderLine buildTestOrderLine1() {
        return new OrderLine("Spice Girls", 5);
    }

    /**
 *
 */
    protected AbstractOrder buildTestOrderShell(String customerName) {
        return new Order(customerName);
    }

    /**
 *
 */
    protected SalesRep buildTestSalesRep1() {
        return new SalesRep("Sales Weasel");
    }

    /**
 *
 */
    protected SalesRep buildTestSalesRep2() {
        return new SalesRep("Uncle Ernie");
    }

    /**
 *
 */
    protected void compareOrders(AbstractOrder expected, AbstractOrder actual) {
        this.assertEquals("The customer name is incorrect.", expected.customerName, actual.customerName);

        this.assertTrue("The sales reps should NOT be populated yet.", !((IndirectContainer)actual.getSalesRepContainer()).isInstantiated());
        this.assertEquals("The number of sales reps is incorrect.", expected.getNumberOfSalesReps(), actual.getNumberOfSalesReps());
        this.assertTrue("The sales reps should be populated.", ((IndirectContainer)actual.getSalesRepContainer()).isInstantiated());
        this.assertUnorderedElementsEqual("The sales reps are not correct.", expected.getSalesRepVector(), actual.getSalesRepVector());

        this.assertTrue("The contacts should NOT be populated.", !((IndirectContainer)actual.getContactContainer()).isInstantiated());
        this.assertEquals("Number of contacts is incorrect.", expected.getNumberOfContacts(), actual.getNumberOfContacts());
        this.assertTrue("The contacts should be populated.", ((IndirectContainer)actual.getContactContainer()).isInstantiated());
        this.assertUnorderedElementsEqual("The contacts are not correct.", expected.getContactVector(), actual.getContactVector());

        this.assertTrue("The order lines should NOT be populated yet.", !((IndirectContainer)actual.getLineContainer()).isInstantiated());
        this.assertEquals("The number of order lines is incorrect.", expected.getNumberOfLines(), actual.getNumberOfLines());
        this.assertTrue("The order lines should be populated.", ((IndirectContainer)actual.getLineContainer()).isInstantiated());
        this.assertUnorderedElementsEqual("The order lines are not correct.", expected.getLineVector(), actual.getLineVector());

        this.assertEquals("Number of contacts2 is incorrect.", expected.getNumberOfContacts2(), actual.getNumberOfContacts2());
        this.assertUnorderedElementsEqual("The contacts2 are not correct.", expected.getContactVector2(), actual.getContactVector2());

        this.assertEquals("The number of sales reps 2 is incorrect.", expected.getNumberOfSalesReps2(), actual.getNumberOfSalesReps2());
        this.assertUnorderedElementsEqual("The sales reps 2 are not correct.", expected.getSalesRepVector2(), actual.getSalesRepVector2());

        this.assertTrue("The total should NOT be instantiated yet.", !(actual.total.isInstantiated()));
        this.assertEquals("The total is incorrect.", expected.getTotal(), actual.getTotal());
        this.assertTrue("The total should be instantiated.", actual.total.isInstantiated());

        this.assertEquals("The total 2 is incorrect.", expected.total2, actual.total2);
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
    protected void setUp() {
        super.setUp();
        AbstractOrder order = this.buildTestOrder1();
        this.writeNewOrder(order);
        originalID = order.id;
        this.getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

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

        OrderLine orderLine = (OrderLine)((OrderLine)originalOrder.getLineStream().nextElement()).clone();
        orderLine.itemName = "munged";
        this.getBackdoorSession().executeNonSelectingSQL("update ORDLINE set ITEM_NAME = '" + orderLine.itemName + "' where ID = " + orderLine.id);
        AbstractOrder orderFromDB = (AbstractOrder)this.getSession().refreshObject(originalOrder);

        this.assertTrue("The order lines should NOT be populated.", !((IndirectContainer)orderFromDB.getLineContainer()).isInstantiated());
        this.assertTrue("New order line not found.", orderFromDB.containsLine(orderLine));
        this.assertTrue("The order lines should be populated.", ((IndirectContainer)orderFromDB.getLineContainer()).isInstantiated());

        // there were problems with TransformationMappings, so make sure they work too
        this.assertTrue("The total should NOT be instantiated yet.", !(orderFromDB.total.isInstantiated()));
        this.assertEquals("The total is incorrect.", this.buildTestOrder2().getTotal(), orderFromDB.getTotal());
        this.assertTrue("The total should be instantiated.", orderFromDB.total.isInstantiated());

        this.assertEquals("The total 2 is incorrect.", this.buildTestOrder2().total2, orderFromDB.total2);
    }

    /**
 *
 */
    public void testRefreshObject() {
        AbstractOrder key = this.buildOrderShell();
        key.id = originalID;
        AbstractOrder orderFromDB = (AbstractOrder)this.getSession().readObject(key);

        this.assertTrue("The order lines should NOT be populated.", !((IndirectContainer)orderFromDB.getLineContainer()).isInstantiated());
        OrderLine orderLine = (OrderLine)orderFromDB.getLineStream().nextElement();
        this.assertTrue("The order lines should be populated.", ((IndirectContainer)orderFromDB.getLineContainer()).isInstantiated());

        // there were problems with TransformationMappings, so make sure they work too
        AbstractOrder expected = this.buildTestOrder1();
        this.assertTrue("The total should NOT be instantiated yet.", !(orderFromDB.total.isInstantiated()));
        this.assertEquals("The total is incorrect.", expected.getTotal(), orderFromDB.getTotal());
        this.assertTrue("The total should be instantiated.", orderFromDB.total.isInstantiated());

        this.assertEquals("The total 2 is incorrect.", expected.total2, orderFromDB.total2);

        orderLine = (OrderLine)orderLine.clone();
        orderLine.itemName = "munged";
        this.getBackdoorSession().executeNonSelectingSQL("update ORDLINE set ITEM_NAME = '" + orderLine.itemName + "' where ID = " + orderLine.id);
        orderFromDB = (AbstractOrder)this.getSession().refreshObject(orderFromDB);

        this.assertTrue("The order lines should NOT be populated.", !((IndirectContainer)orderFromDB.getLineContainer()).isInstantiated());
        this.assertTrue("New order line not found.", orderFromDB.containsLine(orderLine));
        this.assertTrue("The order lines should be populated.", ((IndirectContainer)orderFromDB.getLineContainer()).isInstantiated());

        // there were problems with TransformationMappings, so make sure they work too
        this.assertTrue("The total should NOT be instantiated yet.", !(orderFromDB.total.isInstantiated()));
        this.assertEquals("The total is incorrect.", expected.getTotal(), orderFromDB.getTotal());
        this.assertTrue("The total should be instantiated.", orderFromDB.total.isInstantiated());

        this.assertEquals("The total 2 is incorrect.", expected.total2, orderFromDB.total2);
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
        this.assertTrue("The contacts should NOT be populated.", !((IndirectContainer)orderFromDB.getContactContainer()).isInstantiated());
        this.assertEquals("The number of contacts is incorrect.", this.buildTestOrder1().getNumberOfContacts() + 1, orderFromDB.getNumberOfContacts());
        this.assertTrue("New contact not found.", orderFromDB.containsContact(contact));
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
        this.assertEquals("The number of contacts2 is incorrect.", this.buildTestOrder1().getNumberOfContacts2() + 1, orderFromDB.getNumberOfContacts2());
        this.assertTrue("New contact2 not found.", orderFromDB.containsContact2(contact));
    }

    /**
 *
 */
    public void testUOWAddLine() {
        UnitOfWork uow = this.getSession().acquireUnitOfWork();

        AbstractOrder key = this.buildOrderShell();
        key.id = originalID;
        AbstractOrder orderFromDB = (AbstractOrder)uow.readObject(key);
        OrderLine orderLine = this.buildTestOrderLine1();
        orderFromDB.addLine(orderLine);
        uow.commit();

        this.getSession().getIdentityMapAccessor().initializeAllIdentityMaps();

        orderFromDB = (AbstractOrder)this.getSession().readObject(key);
        this.assertTrue("The order lines should NOT be populated.", !((IndirectContainer)orderFromDB.getLineContainer()).isInstantiated());
        this.assertEquals("The number of order lines is incorrect.", this.buildTestOrder1().getNumberOfLines() + 1, orderFromDB.getNumberOfLines());
        this.assertTrue("New order line not found.", orderFromDB.containsLine(orderLine));
    }

    /**
 *
 */
    public void testUOWAddSalesRep() {
        UnitOfWork uow = this.getSession().acquireUnitOfWork();

        AbstractOrder key = this.buildOrderShell();
        key.id = originalID;
        AbstractOrder orderFromDB = (AbstractOrder)uow.readObject(key);
        SalesRep salesRep = this.buildTestSalesRep1();
        orderFromDB.addSalesRep(salesRep);
        uow.commit();

        this.getSession().getIdentityMapAccessor().initializeAllIdentityMaps();

        orderFromDB = (AbstractOrder)this.getSession().readObject(key);
        this.assertTrue("The sales reps should NOT be populated.", !((IndirectContainer)orderFromDB.getSalesRepContainer()).isInstantiated());
        this.assertEquals("The number of sales reps is incorrect.", this.buildTestOrder1().getNumberOfSalesReps() + 1, orderFromDB.getNumberOfSalesReps());
        this.assertTrue("New sales rep not found.", orderFromDB.containsSalesRep(salesRep));
    }

    /**
 *
 */
    public void testUOWAddSalesRep2() {
        UnitOfWork uow = this.getSession().acquireUnitOfWork();

        AbstractOrder key = this.buildOrderShell();
        key.id = originalID;
        AbstractOrder orderFromDB = (AbstractOrder)uow.readObject(key);
        SalesRep salesRep = this.buildTestSalesRep2();
        orderFromDB.addSalesRep2(salesRep);
        uow.commit();

        this.getSession().getIdentityMapAccessor().initializeAllIdentityMaps();

        orderFromDB = (AbstractOrder)this.getSession().readObject(key);
        this.assertEquals("The number of sales reps 2 is incorrect.", this.buildTestOrder1().getNumberOfSalesReps2() + 1, orderFromDB.getNumberOfSalesReps2());
        this.assertTrue("New sales rep 2 not found.", orderFromDB.containsSalesRep2(salesRep));
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
        this.assertTrue("The total should NOT be instantiated.", !orderFromDB.total.isInstantiated());
        this.assertEquals("The total is incorrect.", newTotal, orderFromDB.getTotal());
        this.assertTrue("The total should be instantiated.", orderFromDB.total.isInstantiated());
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
        this.assertEquals("The total 2 is incorrect.", newTotal2, orderFromDB.total2);
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
        this.assertTrue("The contacts should NOT be populated.", !((IndirectContainer)orderFromDB.getContactContainer()).isInstantiated());
        this.assertEquals("The number of contacts is incorrect.", this.buildTestOrder1().getNumberOfContacts() - 1, orderFromDB.getNumberOfContacts());
        this.assertTrue("Removed contact still present.", !orderFromDB.containsContact(contact));
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
        this.assertEquals("The number of contacts2 is incorrect.", this.buildTestOrder1().getNumberOfContacts2() - 1, orderFromDB.getNumberOfContacts2());
        this.assertTrue("Removed contact2 still present.", !orderFromDB.containsContact2(contact));
    }

    /**
 *
 */
    public void testUOWRemoveLine() {
        UnitOfWork uow = this.getSession().acquireUnitOfWork();

        AbstractOrder key = this.buildOrderShell();
        key.id = originalID;
        AbstractOrder orderFromDB = (AbstractOrder)uow.readObject(key);
        OrderLine orderLine = (OrderLine)orderFromDB.getLineStream().nextElement();
        orderFromDB.removeLine(orderLine);
        uow.commit();

        this.getSession().getIdentityMapAccessor().initializeAllIdentityMaps();

        orderFromDB = (AbstractOrder)this.getSession().readObject(key);
        this.assertTrue("The order lines should NOT be populated.", !((IndirectContainer)orderFromDB.getLineContainer()).isInstantiated());
        this.assertEquals("The number of order lines is incorrect.", this.buildTestOrder1().getNumberOfLines() - 1, orderFromDB.getNumberOfLines());
        this.assertTrue("Removed order line still present.", !orderFromDB.containsLine(orderLine));
    }

    /**
 *
 */
    public void testUOWRemoveSalesRep() {
        UnitOfWork uow = this.getSession().acquireUnitOfWork();

        AbstractOrder key = this.buildOrderShell();
        key.id = originalID;
        AbstractOrder orderFromDB = (AbstractOrder)uow.readObject(key);
        SalesRep salesRep = (SalesRep)orderFromDB.getSalesRepStream().nextElement();
        orderFromDB.removeSalesRep(salesRep);
        uow.commit();

        this.getSession().getIdentityMapAccessor().initializeAllIdentityMaps();

        orderFromDB = (AbstractOrder)this.getSession().readObject(key);
        this.assertTrue("The sales reps should NOT be populated.", !((IndirectContainer)orderFromDB.getSalesRepContainer()).isInstantiated());
        this.assertEquals("The number of sales reps is incorrect.", this.buildTestOrder1().getNumberOfSalesReps() - 1, orderFromDB.getNumberOfSalesReps());
        this.assertTrue("Removed sales rep still present.", !orderFromDB.containsSalesRep(salesRep));
    }

    /**
 *
 */
    public void testUOWRemoveSalesRep2() {
        UnitOfWork uow = this.getSession().acquireUnitOfWork();

        AbstractOrder key = this.buildOrderShell();
        key.id = originalID;
        AbstractOrder orderFromDB = (AbstractOrder)uow.readObject(key);
        SalesRep salesRep = (SalesRep)orderFromDB.getSalesRepStream2().nextElement();
        orderFromDB.removeSalesRep2(salesRep);
        uow.commit();

        this.getSession().getIdentityMapAccessor().initializeAllIdentityMaps();

        orderFromDB = (AbstractOrder)this.getSession().readObject(key);
        this.assertEquals("The number of sales reps 2 is incorrect.", this.buildTestOrder1().getNumberOfSalesReps2() - 1, orderFromDB.getNumberOfSalesReps2());
        this.assertTrue("Removed sales rep 2 still present.", !orderFromDB.containsSalesRep2(salesRep));
    }

    /**
 *
 */
    public void testUOWUnchanged() {
        UnitOfWork uow = this.getSession().acquireUnitOfWork();

        AbstractOrder key = this.buildOrderShell();
        key.id = originalID;
        AbstractOrder orderFromDB = (AbstractOrder)uow.readObject(key);
        this.assertTrue("The sales reps should NOT be populated.", !((IndirectContainer)orderFromDB.getSalesRepContainer()).isInstantiated());
        this.assertTrue("The order lines should NOT be populated.", !((IndirectContainer)orderFromDB.getLineContainer()).isInstantiated());
        this.assertTrue("The contacts should NOT be populated.", !((IndirectContainer)orderFromDB.getContactContainer()).isInstantiated());
        uow.commit();

        this.assertTrue("The sales reps should NOT be populated.", !((IndirectContainer)orderFromDB.getSalesRepContainer()).isInstantiated());
        this.assertEquals("The number of sales reps is incorrect.", this.buildTestOrder1().getNumberOfSalesReps(), orderFromDB.getNumberOfSalesReps());
        this.assertTrue("The sales reps should be populated.", ((IndirectContainer)orderFromDB.getSalesRepContainer()).isInstantiated());

        this.assertTrue("The contacts should NOT be populated.", !((IndirectContainer)orderFromDB.getContactContainer()).isInstantiated());
        this.assertEquals("The number of contacts is incorrect.", this.buildTestOrder1().getNumberOfContacts(), orderFromDB.getNumberOfContacts());
        this.assertTrue("The contacts should be populated.", ((IndirectContainer)orderFromDB.getContactContainer()).isInstantiated());

        this.assertTrue("The order lines should NOT be populated.", !((IndirectContainer)orderFromDB.getLineContainer()).isInstantiated());
        this.assertEquals("The number of order lines is incorrect.", this.buildTestOrder1().getNumberOfLines(), orderFromDB.getNumberOfLines());
        this.assertTrue("The order lines should be populated.", ((IndirectContainer)orderFromDB.getLineContainer()).isInstantiated());

        this.assertEquals("The number of contacts2 is incorrect.", this.buildTestOrder1().getNumberOfContacts2(), orderFromDB.getNumberOfContacts2());

        this.assertEquals("The number of sales reps 2 is incorrect.", this.buildTestOrder1().getNumberOfSalesReps2(), orderFromDB.getNumberOfSalesReps2());

        this.assertTrue("The total should NOT be instantiated yet.", !(orderFromDB.total.isInstantiated()));
        this.assertEquals("The total is incorrect.", this.buildTestOrder1().getTotal(), orderFromDB.getTotal());
        this.assertTrue("The total should be instantiated.", orderFromDB.total.isInstantiated());

        this.assertEquals("The total 2 is incorrect.", this.buildTestOrder1().total2, orderFromDB.total2);
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
        this.assertTrue("Order lines were not removed.", orderFromDB.getLineVector().isEmpty());
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
            this.assertTrue("Merging of the clone did not trigger the back up value holder" + ex.toString(), false);
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
