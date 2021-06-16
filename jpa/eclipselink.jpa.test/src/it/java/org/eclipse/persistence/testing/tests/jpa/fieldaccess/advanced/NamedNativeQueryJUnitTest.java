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


package org.eclipse.persistence.testing.tests.jpa.fieldaccess.advanced;

import junit.framework.*;

import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced.AdvancedTableCreator;

public class NamedNativeQueryJUnitTest extends JUnitTestCase {
    public NamedNativeQueryJUnitTest() {
        super();
    }

    public NamedNativeQueryJUnitTest(String name) {
        super(name);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("NamedNativeQueryJUnitTest (fieldaccess)");
        suite.addTest(new NamedNativeQueryJUnitTest("testSetup"));
        suite.addTest(new NamedNativeQueryJUnitTest("testNamedNativeQuery"));

        return suite;
    }

    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
        new AdvancedTableCreator().replaceTables(JUnitTestCase.getServerSession("fieldaccess"));
        clearCache("fieldaccess");
    }

    public void testNamedNativeQuery() {
        Exception exception = null;

        try {
            createEntityManager("fieldaccess").createNamedQuery("findAllFieldAccessSQLAddresses").getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            exception = e;
        }

        assertNull("Exception was caught: " + exception, exception);
    }
}
