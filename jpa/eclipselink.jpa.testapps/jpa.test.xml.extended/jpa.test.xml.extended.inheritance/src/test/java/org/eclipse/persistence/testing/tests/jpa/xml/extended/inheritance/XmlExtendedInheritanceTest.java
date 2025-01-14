/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
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
//     12/18/2009-2.1 Guy Pelletier
//       - 211323: Add class extractor support to the EclipseLink-ORM.XML Schema
//     01/05/2010-2.1 Guy Pelletier
//       - 211324: Add additional event(s) support to the EclipseLink-ORM.XML Schema
package org.eclipse.persistence.testing.tests.jpa.xml.extended.inheritance;

import jakarta.persistence.EntityManager;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.testing.models.jpa.xml.inheritance.MacBook;
import org.eclipse.persistence.testing.models.jpa.xml.inheritance.MacBookPro;
import org.eclipse.persistence.testing.tests.jpa.xml.inheritance.XmlInheritanceTest;

import java.util.List;

/**
 * JUnit test case(s) xml specified inheritance metadata.
 */
public class XmlExtendedInheritanceTest extends XmlInheritanceTest {

    public XmlExtendedInheritanceTest() {
        super();
    }

    public XmlExtendedInheritanceTest(String name) {
        super(name);
    }

    @Override
    public String getPersistenceUnitName() {
        return "xml-extended-inheritance";
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("Inheritance Model - xml-extended-inheritance");

        suite.addTest(new XmlExtendedInheritanceTest("testSetup"));
        suite.addTest(new XmlExtendedInheritanceTest("testCreateFueledVehicle"));
        suite.addTest(new XmlExtendedInheritanceTest("testCreateBusFueledVehicle"));
        suite.addTest(new XmlExtendedInheritanceTest("testCreateNonFueledVehicle"));
        suite.addTest(new XmlExtendedInheritanceTest("testReadFueledVehicle"));
        suite.addTest(new XmlExtendedInheritanceTest("testReadNonFueledVehicle"));
        suite.addTest(new XmlExtendedInheritanceTest("testNamedNativeQueryOnSportsCar"));
        suite.addTest(new XmlExtendedInheritanceTest("testUpdateBusFueledVehicle"));
        suite.addTest(new XmlExtendedInheritanceTest("testUpdateFueledVehicle"));
        suite.addTest(new XmlExtendedInheritanceTest("testUpdateNonFueledVehicle"));
        suite.addTest(new XmlExtendedInheritanceTest("testDeleteBusFueledVehicle"));
        suite.addTest(new XmlExtendedInheritanceTest("testDeleteFueledVehicle"));
        suite.addTest(new XmlExtendedInheritanceTest("testDeleteNonFueledVehicle"));
        suite.addTest(new XmlExtendedInheritanceTest("testPKJoinColumnAssociation"));
        suite.addTest(new XmlExtendedInheritanceTest("testAppleComputers"));

        return suite;
    }


    public void testAppleComputers() {
        EntityManager em = createEntityManager();
        beginTransaction(em);

        MacBook macBook1 = new MacBook();
        macBook1.setRam(2);
        MacBook macBook2 = new MacBook();
        macBook2.setRam(4);

        MacBookPro macBookPro1 = new MacBookPro();
        macBookPro1.setRam(4);
        macBookPro1.setColor("Black");
        MacBookPro macBookPro2 = new MacBookPro();
        macBookPro2.setRam(6);
        macBookPro2.setColor("Red");
        MacBookPro macBookPro3 = new MacBookPro();
        macBookPro3.setRam(8);
        macBookPro3.setColor("Green");
        MacBookPro macBookPro4 = new MacBookPro();
        macBookPro4.setRam(8);
        macBookPro4.setColor("Blue");

        try {
            em.persist(macBook1);
            em.persist(macBook2);

            em.persist(macBookPro1);
            em.persist(macBookPro2);
            em.persist(macBookPro3);
            em.persist(macBookPro4);

            commitTransaction(em);
        } catch (Exception exception ) {
            fail("Error persisting macbooks: " + exception.getMessage());
        } finally {
            closeEntityManager(em);
        }

        clearCache();
        em = createEntityManager();

        List<?> macBooks = em.createNamedQuery("findAllXMLMacBooks").getResultList();
        assertEquals("The wrong number of mac books were returned: " + macBooks.size() + ", expected: 6", 6, macBooks.size());

        List<?> macBookPros = em.createNamedQuery("findAllXMLMacBookPros").getResultList();
        assertEquals("The wrong number of mac book pros were returned: " + macBookPros.size() + ", expected: 4", 4, macBookPros.size());
    }
}
