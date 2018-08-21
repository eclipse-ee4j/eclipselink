/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     03/23/2011-2.3 Guy Pelletier
//       - 337323: Multi-tenant with shared schema support (part 1)
//     04/01/2011-2.3 Guy Pelletier
//       - 337323: Multi-tenant with shared schema support (part 2)
//     04/21/2011-2.3 Guy Pelletier
//       - 337323: Multi-tenant with shared schema support (part 5)
package org.eclipse.persistence.testing.tests.jpa.advanced.multitenant;

import junit.framework.*;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.advanced.multitenant.AdvancedMultiTenantTableCreator;

public class AdvancedMultiTenantTableJunitTest extends AdvancedMultiTenantJunitTest {

    public AdvancedMultiTenantTableJunitTest() {
        super();
    }

    public String getMULTI_TENANT_TABLE_PER_TENANT_PU() { return "MulitPU-4"; }

    public AdvancedMultiTenantTableJunitTest(String name) {
        super(name);
        setPuName(getMULTI_TENANT_TABLE_PER_TENANT_PU());
    }

    public void setUp() {}

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("AdvancedMultiTenantTableJunitTest");
        if (! JUnitTestCase.isJPA10()) {
            if (System.getProperty("run.metadata.cache.test.suite").compareTo("true") == 0) {
                suite.addTest(new AdvancedMultiTenantTableJunitTest("testWriteProjectCache"));
            }
            suite.addTest(new AdvancedMultiTenantTableJunitTest("testSetup"));
            suite.addTest(new AdvancedMultiTenantTableJunitTest("testTablePerTenantA"));
            suite.addTest(new AdvancedMultiTenantTableJunitTest("testTablePerTenantAQueries"));
            suite.addTest(new AdvancedMultiTenantTableJunitTest("testTablePerTenantB"));
            suite.addTest(new AdvancedMultiTenantTableJunitTest("testTablePerTenantBQueries"));
        }
        return suite;
    }

    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
        new AdvancedMultiTenantTableCreator().replaceTables(JUnitTestCase.getServerSession(getMULTI_TENANT_TABLE_PER_TENANT_PU()));

    }

    public void testWriteProjectCache(){
        new org.eclipse.persistence.testing.tests.jpa.advanced.MetadataCachingTestSuite().testFileBasedProjectCacheLoading(getMULTI_TENANT_TABLE_PER_TENANT_PU());
    }

}
