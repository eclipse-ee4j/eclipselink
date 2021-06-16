/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.jpa.inheritance;

import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.testing.models.jpa.inheritance.*;
import org.eclipse.persistence.internal.jpa.EntityManagerImpl;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.tests.jpa.EntityContainerTestBase;

/**
 * BUG - 4219218. Tests multiple table inheritance with differently named
 * primary key columns in the parent and subclass table.
 *
 * @author Guy Pelletier
 */
public class MultipleTableInheritanceCreateTest extends EntityContainerTestBase {
    protected boolean m_reset = false;    // reset gets called twice on error
    protected Exception m_exception;

    public MultipleTableInheritanceCreateTest() {
        setDescription("Tests the creation of an inheritance subclass that uses multiple tables with a different pk column than its parent");
    }

    public void setup () {
        super.setup();
        m_reset = true;
        m_exception = null;
        ((EntityManagerImpl)getEntityManager()).getActiveSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    public void test() throws Exception {
        try {
            Person busDriver = new Person();
            busDriver.setName("Guy Pelletier");

            Bus bus = new Bus();
            bus.setBusDriver(busDriver);
            bus.setFuelCapacity(new Integer(275));
            bus.setPassengerCapacity(new Integer(100));

            beginTransaction();
            getEntityManager().persist(bus);
            getEntityManager().persist(busDriver);
            commitTransaction();
        } catch (DatabaseException e) {
            m_exception = e;
        }
    }

    public void reset () {
        if (m_reset) {
            m_reset = false;
        }
    }

    public void verify() {
        if (m_exception != null) {
            throw new TestErrorException("Exception was thrown when creating a bus: " + m_exception.getMessage());
        }
    }
}
