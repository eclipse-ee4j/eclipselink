/*
 * Copyright (c) 1998, 2022 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.jpa.xml.extended.relationships;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.copying.CloneCopyPolicy;
import org.eclipse.persistence.descriptors.copying.CopyPolicy;
import org.eclipse.persistence.descriptors.copying.InstantiationCopyPolicy;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.testing.framework.jpa.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.xml.relationships.Auditor;
import org.eclipse.persistence.testing.models.jpa.xml.relationships.CEO;
import org.eclipse.persistence.testing.models.jpa.xml.relationships.Customer;
import org.eclipse.persistence.testing.models.jpa.xml.relationships.Item;
import org.eclipse.persistence.testing.models.jpa.xml.relationships.Lego;
import org.eclipse.persistence.testing.models.jpa.xml.relationships.Mattel;
import org.eclipse.persistence.testing.models.jpa.xml.relationships.MegaBrands;
import org.eclipse.persistence.testing.models.jpa.xml.relationships.Namco;
import org.eclipse.persistence.testing.models.jpa.xml.relationships.Order;
import org.eclipse.persistence.testing.models.jpa.xml.relationships.OrderCard;
import org.eclipse.persistence.testing.models.jpa.xml.relationships.OrderLabel;
import org.eclipse.persistence.testing.models.jpa.xml.relationships.RelationshipsTableManager;
import org.eclipse.persistence.testing.models.jpa.xml.relationships.TestInstantiationCopyPolicy;
import org.eclipse.persistence.testing.tests.jpa.xml.relationships.XmlRelationshipsTest;

/**
 * JUnit test case(s) for the TopLink EntityMappingsXMLProcessor.
 */
public class XmlExtendedRelationshipsTest extends XmlRelationshipsTest {
    private static Integer customerId;
    private static Integer itemId;
    private static Integer extendedItemId;
    private static Integer orderId;

    public XmlExtendedRelationshipsTest() {
        super();
    }

    public XmlExtendedRelationshipsTest(String name) {
        super(name);
    }

    @Override
    public String getPersistenceUnitName() {
        return "xml-extended-relationships";
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("Relationships Model - xml-extended-relationships");
        suite.addTest(new XmlExtendedRelationshipsTest("testSetup"));
        suite.addTest(new XmlExtendedRelationshipsTest("testCreateCustomer"));
        suite.addTest(new XmlExtendedRelationshipsTest("testCreateItem"));
        suite.addTest(new XmlExtendedRelationshipsTest("testCreateOrder"));

        suite.addTest(new XmlExtendedRelationshipsTest("testReadCustomer"));
        suite.addTest(new XmlExtendedRelationshipsTest("testReadItem"));
        suite.addTest(new XmlExtendedRelationshipsTest("testReadOrder"));

        suite.addTest(new XmlExtendedRelationshipsTest("testNamedQueryOnCustomer"));
        suite.addTest(new XmlExtendedRelationshipsTest("testNamedQueryOnItem"));
        suite.addTest(new XmlExtendedRelationshipsTest("testNamedQueryOnOrder"));

        suite.addTest(new XmlExtendedRelationshipsTest("testUpdateCustomer"));
        suite.addTest(new XmlExtendedRelationshipsTest("testUpdateItem"));
        suite.addTest(new XmlExtendedRelationshipsTest("testUpdateOrder"));

        suite.addTest(new XmlExtendedRelationshipsTest("testExcludeDefaultMappings"));
        suite.addTest(new XmlExtendedRelationshipsTest("testCreateExtendedItem"));
        suite.addTest(new XmlExtendedRelationshipsTest("testModifyExtendedItem"));
        suite.addTest(new XmlExtendedRelationshipsTest("testVerifyExtendedItem"));
        suite.addTest(new XmlExtendedRelationshipsTest("testCopyPolicy"));
        suite.addTest(new XmlExtendedRelationshipsTest("testCloneCopyPolicy"));
        suite.addTest(new XmlExtendedRelationshipsTest("testInstantiationCopyPolicy"));
        suite.addTest(new XmlExtendedRelationshipsTest("testNamedNativeQueryFromMappedSuperclass"));

        suite.addTest(new XmlExtendedRelationshipsTest("testDeleteOrder"));
        suite.addTest(new XmlExtendedRelationshipsTest("testDeleteCustomer"));
        suite.addTest(new XmlExtendedRelationshipsTest("testDeleteItem"));

        return suite;
    }

    public void testNamedNativeQueryFromMappedSuperclass() {
        EntityManager em = createEntityManager();
        Object customer = em.createNamedQuery("findAllSQLXMLCustomers").getSingleResult();
        assertNotNull("Error executing named native query 'findAllSQLXMLCustomers'", customer);
        closeEntityManager(em);
    }

    public void testExcludeDefaultMappings() {
        ClassDescriptor descriptor = getPersistenceUnitServerSession().getDescriptor(Mattel.class);
        assertNull("The 'ignoredBasic' attribute from the class Mattel was mapped despite an exclude-default-mappings setting of true.", descriptor.getMappingForAttributeName("ignoredBasic"));
        assertNull("The 'ignoredOneToOne' attribute from the class Mattel was mapped despite an exclude-default-mappings setting of true.", descriptor.getMappingForAttributeName("ignoredOneToOne"));
        assertNull("The 'ignoredVariableOneToOne' attribute from the class Mattel was mapped despite an exclude-default-mappings setting of true.", descriptor.getMappingForAttributeName("ignoredVariableOneToOne"));
        assertNull("The 'ignoredOneToMany' attribute from the class Mattel was mapped despite an exclude-default-mappings setting of true.", descriptor.getMappingForAttributeName("ignoredOneToMany"));
    }

    /**
     * Create a new item that has a variable one to one to a manufacturer.
     */
    public void testCreateExtendedItem() {
        EntityManager em = createEntityManager();
        beginTransaction(em);

        try {
            Item item = new Item();
            item.setName("Synergizer2000");
            item.setDescription("Every kid must have one ... ");

            // Manufacturer does not cascade persist
            Mattel mattel = new Mattel();
            mattel.setName("Mattel Inc.");

            // CEO will cascade persist
            CEO mattelCEO = new CEO();
            mattelCEO.setName("Mr. Mattel");
            mattel.setCeo(mattelCEO);

            em.persist(mattel);
            item.setManufacturer(mattel);

            // Distributor will cascade persist
            Namco namco = new Namco();
            namco.setName("Namco Games");

            // CEO will cascade persist
            CEO namcoCEO = new CEO();
            namcoCEO.setName("Mr. Namco");
            namco.setCeo(namcoCEO);

            item.setDistributor(namco);

            em.persist(item);
            extendedItemId = item.getItemId();
            commitTransaction(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }

            closeEntityManager(em);
            throw e;
        }

        closeEntityManager(em);
    }

    /**
     * Read an item, verify it contents, modify it and commit.
     */
    public void testModifyExtendedItem() {
        EntityManager em = createEntityManager();
        beginTransaction(em);

        try {
            Item item = em.find(Item.class, extendedItemId);
            item.setName("Willy Waller");
            item.setDescription("For adults only!");

            assertNotNull("The manufacturer was not persisted", item.getManufacturer());
            assertEquals("The manufacturer of the item was incorrect", "Mattel Inc.", item.getManufacturer().getName());

            Lego lego = new Lego();
            lego.setName("The LEGO Group");
            em.persist(lego);
            item.setManufacturer(lego);

            assertNotNull("The distributor was not persisted", item.getDistributor());
            assertEquals("The distributor of the item was incorrect", "Namco Games", item.getDistributor().getName());

            MegaBrands megaBrands = new MegaBrands();
            megaBrands.setName("MegaBrands Inc.");
            em.persist(megaBrands);
            item.setDistributor(megaBrands);

            em.merge(item); // no op really ...

            commitTransaction(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }

            closeEntityManager(em);
            throw e;
        }

        closeEntityManager(em);
    }

    /**
     * Verify the final contents of item.
     */
    public void testVerifyExtendedItem() {
        EntityManager em = createEntityManager();
        Item item = em.find(Item.class, extendedItemId);

        assertNotNull("The manufacturer was not persisted", item.getManufacturer());
        assertEquals("The manufacturer of the item was incorrect [" + item.getManufacturer().getName() + "]", "The LEGO Group", item.getManufacturer().getName());

        assertNotNull("The distributor was not persisted", item.getDistributor());
        assertEquals("The distributor of the item was incorrect [" + item.getDistributor().getName() + "]", "MegaBrands Inc.", item.getDistributor().getName());

        closeEntityManager(em);
    }

    public void testInstantiationCopyPolicy(){
        assertTrue("The InstantiationCopyPolicy was not properly set.", getPersistenceUnitServerSession().getDescriptor(Item.class).getCopyPolicy() instanceof InstantiationCopyPolicy);
    }

    public void testCopyPolicy(){
        assertTrue("The CopyPolicy was not properly set.", getPersistenceUnitServerSession().getDescriptor(Order.class).getCopyPolicy() instanceof TestInstantiationCopyPolicy);
    }

    public void testCloneCopyPolicy(){
        CopyPolicy copyPolicy = getPersistenceUnitServerSession().getDescriptor(Namco.class).getCopyPolicy();
        assertTrue("The CloneCopyPolicy was not properly set.", copyPolicy  instanceof CloneCopyPolicy);
        assertEquals("The method on CloneCopyPolicy was not properly set.", "cloneNamco", ((CloneCopyPolicy) copyPolicy).getMethodName());
        assertEquals("The workingCopyMethod on CloneCopyPolicy was not properly set.", "cloneWorkingCopyNamco", ((CloneCopyPolicy) copyPolicy).getWorkingCopyMethodName());
    }

    /**
     * This tests a couple scenarios:
     * - 1-M mapped by a M-1 using a JoinTable
     * - 1-1 mapped using a JoinTable (uni-directional)
     * - 1-1 mapped using a JoinTable (bi-directional)
     */
    public void testOne2OneRelationTables() {
        EntityManager em = createEntityManager();
        beginTransaction(em);

        Order order1 = new Order();
        Order order2 = new Order();
        Auditor auditor = new Auditor();
        try {
            OrderCard order1Card = new OrderCard();
            OrderLabel order1Label = new OrderLabel();
            order1Label.setDescription("I describe order 1");
            order1.setOrderLabel(order1Label);
            order1.setOrderCard(order1Card);
            em.persist(order1);

            OrderCard order2Card = new OrderCard();
            OrderLabel order2Label = new OrderLabel();
            order2Label.setDescription("I describe order 2");
            order2.setOrderLabel(order2Label);
            order2.setOrderCard(order2Card);
            em.persist(order2);

            auditor.setName("Guillaume");
            auditor.addOrder(order1);
            auditor.addOrder(order2);
            em.persist(auditor);

            commitTransaction(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }

            closeEntityManager(em);
            throw e;
        }

        closeEntityManager(em);


        clearCache();
        em = createEntityManager();

        Auditor refreshedAuditor = em.find(Auditor.class, auditor.getId());
        Order refreshedOrder1 = em.find(Order.class, order1.getOrderId());
        Order refreshedOrder2 = em.find(Order.class, order2.getOrderId());

        assertTrue("Auditor read back did not match the original", getPersistenceUnitServerSession().compareObjects(auditor, refreshedAuditor));
        assertTrue("Order1 read back did not match the original", getPersistenceUnitServerSession().compareObjects(order1, refreshedOrder1));
        assertTrue("Order2 read back did not match the original", getPersistenceUnitServerSession().compareObjects(order2, refreshedOrder2));

        closeEntityManager(em);
    }
}
