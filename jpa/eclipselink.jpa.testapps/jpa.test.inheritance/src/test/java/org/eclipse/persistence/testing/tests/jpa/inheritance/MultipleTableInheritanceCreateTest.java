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
package org.eclipse.persistence.testing.tests.jpa.inheritance;

import jakarta.persistence.EntityManager;
import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.testing.framework.jpa.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.inheritance.Bus;
import org.eclipse.persistence.testing.models.jpa.inheritance.Person;

/**
 * BUG - 4219218. Tests multiple table inheritance with differently named
 * primary key columns in the parent and subclass table.
 *
 * @author Guy Pelletier
 */
public class MultipleTableInheritanceCreateTest extends JUnitTestCase {
    protected boolean m_reset = false;    // reset gets called twice on error
    protected Exception m_exception;

    public MultipleTableInheritanceCreateTest() {
//        setDescription("Tests the creation of an inheritance subclass that uses multiple tables with a different pk column than its parent");
    }

    public MultipleTableInheritanceCreateTest(String name) {
        super(name);
    }

    @Override
    public void setUp () {
        super.setUp();
        clearCache();
    }

    public void testCreateSubClass() {
        EntityManager em = createEntityManager();
        try {
            Person busDriver = new Person();
            busDriver.setName("Guy Pelletier");

            Bus bus = new Bus();
            bus.setBusDriver(busDriver);
            bus.setFuelCapacity(275);
            bus.setPassengerCapacity(100);

            beginTransaction(em);
            em.persist(bus);
            em.persist(busDriver);
            commitTransaction(em);
        } catch (DatabaseException e) {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            throw e;
        } finally {
            closeEntityManager(em);
        }
    }

}
