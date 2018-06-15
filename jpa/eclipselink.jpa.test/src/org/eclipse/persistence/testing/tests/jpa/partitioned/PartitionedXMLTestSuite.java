/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     2010-10-27 - James Sutherland (Oracle) initial impl
package org.eclipse.persistence.testing.tests.jpa.partitioned;

import junit.framework.*;

public class PartitionedXMLTestSuite extends PartitionedTestSuite {

    public static Test suite() {
        TestSuite suite = new TestSuite("PartitioningXMLTests");
        suite.addTest(new PartitionedXMLTestSuite("testSetup"));
        suite.addTest(new PartitionedXMLTestSuite("testReadEmployee"));
        suite.addTest(new PartitionedXMLTestSuite("testReadAllEmployee"));
        suite.addTest(new PartitionedXMLTestSuite("testPersistEmployee"));
        suite.addTest(new PartitionedXMLTestSuite("testRemoveEmployee"));
        suite.addTest(new PartitionedXMLTestSuite("testUpdateEmployee"));
        suite.addTest(new PartitionedXMLTestSuite("testReadProject"));
        suite.addTest(new PartitionedXMLTestSuite("testReadAllProject"));
        suite.addTest(new PartitionedXMLTestSuite("testPersistProject"));
        suite.addTest(new PartitionedXMLTestSuite("testRemoveProject"));
        suite.addTest(new PartitionedXMLTestSuite("testUpdateProject"));
        suite.addTest(new PartitionedXMLTestSuite("testPartitioning"));
        return suite;
    }

    public PartitionedXMLTestSuite(String name) {
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
