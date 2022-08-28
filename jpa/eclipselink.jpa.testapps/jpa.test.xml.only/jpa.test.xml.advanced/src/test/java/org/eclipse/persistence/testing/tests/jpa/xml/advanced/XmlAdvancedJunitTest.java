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
package org.eclipse.persistence.testing.tests.jpa.xml.advanced;

import jakarta.persistence.EntityManager;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.internal.sessions.RepeatableWriteUnitOfWork;
import org.eclipse.persistence.jpa.JpaEntityManager;
import org.eclipse.persistence.testing.framework.jpa.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.Address;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.AdvancedTableCreator;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.Employee;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.LargeProject;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.Man;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.PartnerLink;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.Woman;

public class XmlAdvancedJunitTest extends JUnitTestCase {

    public XmlAdvancedJunitTest() {
        super();
    }

    public XmlAdvancedJunitTest(String name) {
        super(name);
        setPuName(getPersistenceUnitName());
    }

    @Override
    public String getPersistenceUnitName() {
        return "default";
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("XmlAdvancedJunitTest - default");

        suite.addTest(new XmlAdvancedJunitTest("testSetup"));
        suite.addTest(new XmlAdvancedJunitTest("testEL254937"));
        suite.addTest(new XmlAdvancedJunitTest("testGF1894"));
        suite.addTest(new XmlAdvancedJunitTest("testManAndWoman"));

        return suite;
    }

    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
        new AdvancedTableCreator().replaceTables(getPersistenceUnitServerSession());

        clearCache();
    }

    /*public static EntityManager createEntityManager() {
        if (persistenceUnit==null){
            String ormTesting = TestingProperties.getProperty(TestingProperties.ORM_TESTING, TestingProperties.JPA_ORM_TESTING);
            persistenceUnit = ormTesting.equals(TestingProperties.JPA_ORM_TESTING)? "default" : "extended-advanced";
        }
    }*/

    public void testEL254937(){
        EntityManager em = createEntityManager();
        beginTransaction(em);
        LargeProject lp1 = new LargeProject();
        lp1.setName("one");
        em.persist(lp1);
        commitTransaction(em);
        em = createEntityManager();
        beginTransaction(em);
        em.remove(em.find(LargeProject.class, lp1.getId()));
        em.flush();
        JpaEntityManager eclipselinkEm = (JpaEntityManager)em.getDelegate();
        RepeatableWriteUnitOfWork uow =
            (RepeatableWriteUnitOfWork)eclipselinkEm.getActiveSession();
        //duplicate the beforeCompletion call
        uow.issueSQLbeforeCompletion();
        //commit the transaction
        uow.setShouldTerminateTransaction(true);
        uow.commitTransaction();
        //duplicate the AfterCompletion call.  This should merge, removing the LargeProject from the shared cache
        uow.mergeClonesAfterCompletion();
        em = createEntityManager();
        LargeProject cachedLargeProject = em.find(LargeProject.class, lp1.getId());
        closeEntityManager(em);
        assertNull("Entity removed during flush was not removed from the shared cache on commit", cachedLargeProject);
    }

    public void testGF1894() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        Employee emp = new Employee("Guy", "Pelletier");

        Address address = new Address();
        address.setCity("College Town");

        emp.setAddress(address);

        try {
            Employee empClone = em.merge(emp);
            assertNotNull("The id field for the merged new employee object was not generated.", empClone.getId());
            commitTransaction(em);

            Employee empFromDB = em.find(Employee.class, empClone.getId());
            assertNotNull("The version locking field for the merged new employee object was not updated after commit.", empFromDB.getVersion());

            beginTransaction(em);
            Employee empClone2 = em.merge(empFromDB);
            assertEquals("The id field on a existing merged employee object was modified on a subsequent merge.", empFromDB.getId(), empClone2.getId());
            commitTransaction(em);
        } catch (jakarta.persistence.OptimisticLockException e) {
            fail("An optimistic locking exception was caught on the merge of a new object. An insert should of occurred instead.");
        }

        closeEntityManager(em);
    }

    public void testManAndWoman() {
        EntityManager em = createEntityManager();
        beginTransaction(em);

        try {
            PartnerLink pLink1 = new PartnerLink();
            pLink1.setMan(new Man());
            em.persist(pLink1);

            PartnerLink pLink2 = new PartnerLink();
            pLink2.setWoman(new Woman());
            em.persist(pLink2);

            PartnerLink pLink3 = new PartnerLink();
            pLink3.setMan(new Man());
            pLink3.setWoman(new Woman());
            em.persist(pLink3);

            commitTransaction(em);

        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }

            closeEntityManager(em);
            fail("An exception was caught: [" + e.getMessage() + "]");
        }

        closeEntityManager(em);
    }

}
