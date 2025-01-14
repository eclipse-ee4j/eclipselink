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
import org.eclipse.persistence.testing.tests.jpa.partitioned.PartitionedTest;

public class GridLinkPartitionedTest extends PartitionedTest {

    public GridLinkPartitionedTest(String name) {
        super(name);
        setPuName(getPersistenceUnitName());
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("GridLinkPartitionedTest");
        suite.addTest(new GridLinkPartitionedTest("testSetup"));
        suite.addTest(new GridLinkPartitionedTest("testReadEmployee"));
        suite.addTest(new GridLinkPartitionedTest("testReadAllEmployee"));
        suite.addTest(new GridLinkPartitionedTest("testPersistEmployee"));
        suite.addTest(new GridLinkPartitionedTest("testRemoveEmployee"));
        suite.addTest(new GridLinkPartitionedTest("testUpdateEmployee"));
        suite.addTest(new GridLinkPartitionedTest("testReadProject"));
        suite.addTest(new GridLinkPartitionedTest("testReadAllProject"));
        suite.addTest(new GridLinkPartitionedTest("testPersistProject"));
        suite.addTest(new GridLinkPartitionedTest("testRemoveProject"));
        suite.addTest(new GridLinkPartitionedTest("testUpdateProject"));
        suite.addTest(new GridLinkPartitionedTest("testPartitioning"));
        suite.addTest(new GridLinkPartitionedTest("testPersistPartitioning"));
        suite.addTest(new GridLinkPartitionedTest("testPersistOfficeWithLongName"));
        return suite;
    }

    /**
     * Return the name of the persistence context this test uses.
     * This allow a subclass test to set this only in one place.
     */
    @Override
    public String getPersistenceUnitName() {
        return "partitioned";
    }

}
