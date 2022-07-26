/*
 * Copyright (c) 2011, 2022 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.jpa.partitioned;

import junit.framework.*;

public class PartitionedXMLTest extends PartitionedTest {

    public static Test suite() {
        TestSuite suite = new TestSuite("PartitioningXMLTests");
        suite.addTest(new PartitionedXMLTest("testSetup"));
        suite.addTest(new PartitionedXMLTest("testReadEmployee"));
        suite.addTest(new PartitionedXMLTest("testReadAllEmployee"));
        suite.addTest(new PartitionedXMLTest("testPersistEmployee"));
        suite.addTest(new PartitionedXMLTest("testRemoveEmployee"));
        suite.addTest(new PartitionedXMLTest("testUpdateEmployee"));
        suite.addTest(new PartitionedXMLTest("testReadProject"));
        suite.addTest(new PartitionedXMLTest("testReadAllProject"));
        suite.addTest(new PartitionedXMLTest("testPersistProject"));
        suite.addTest(new PartitionedXMLTest("testRemoveProject"));
        suite.addTest(new PartitionedXMLTest("testUpdateProject"));
        suite.addTest(new PartitionedXMLTest("testPartitioning"));
        return suite;
    }

    public PartitionedXMLTest(String name) {
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
