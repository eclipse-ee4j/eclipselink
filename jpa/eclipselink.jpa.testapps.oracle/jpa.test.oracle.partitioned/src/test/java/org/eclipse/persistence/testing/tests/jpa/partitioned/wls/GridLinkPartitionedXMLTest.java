/*
 * Copyright (c) 2011, 2025 Oracle and/or its affiliates. All rights reserved.
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
//     2010-10-27 - James Sutherland (Oracle) initial impl
package org.eclipse.persistence.testing.tests.jpa.partitioned.wls;

import junit.framework.Test;
import junit.framework.TestSuite;

public class GridLinkPartitionedXMLTest extends GridLinkPartitionedTest {

    public static Test suite() {
        TestSuite suite = new TestSuite("GridLinkPartitionedXMLTest");
        suite.addTest(new GridLinkPartitionedXMLTest("testSetup"));
        suite.addTest(new GridLinkPartitionedXMLTest("testReadEmployee"));
        suite.addTest(new GridLinkPartitionedXMLTest("testReadAllEmployee"));
        suite.addTest(new GridLinkPartitionedXMLTest("testPersistEmployee"));
        suite.addTest(new GridLinkPartitionedXMLTest("testRemoveEmployee"));
        suite.addTest(new GridLinkPartitionedXMLTest("testUpdateEmployee"));
        suite.addTest(new GridLinkPartitionedXMLTest("testReadProject"));
        suite.addTest(new GridLinkPartitionedXMLTest("testReadAllProject"));
        suite.addTest(new GridLinkPartitionedXMLTest("testPersistProject"));
        suite.addTest(new GridLinkPartitionedXMLTest("testRemoveProject"));
        suite.addTest(new GridLinkPartitionedXMLTest("testUpdateProject"));
        suite.addTest(new GridLinkPartitionedXMLTest("testPartitioning"));
        return suite;
    }

    public GridLinkPartitionedXMLTest(String name) {
        super(name);
    }

    /**
     * Return the name of the persistence context this test uses.
     * This allow a subclass test to set this only in one place.
     */
    @Override
    public String getPersistenceUnitName() {
        return "partitioned-xml";
    }
}
