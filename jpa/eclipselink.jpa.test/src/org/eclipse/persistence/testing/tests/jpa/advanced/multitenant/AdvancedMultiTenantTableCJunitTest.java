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

public class AdvancedMultiTenantTableCJunitTest extends AdvancedMultiTenantJunitTest {

    public AdvancedMultiTenantTableCJunitTest() {
        super();
    }
    public String getMULTI_TENANT_TABLE_PER_TENANT_C_PU(){ return "MulitPU-5"; }

    public AdvancedMultiTenantTableCJunitTest(String name) {
        super(name);
        setPuName(getMULTI_TENANT_TABLE_PER_TENANT_C_PU());
    }

    public void setUp() {}

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("AdvancedMultiTenantTableCJunitTest");
        if (! JUnitTestCase.isJPA10()) {
            if (System.getProperty("run.metadata.cache.test.suite").compareTo("true") == 0) {
                suite.addTest(new AdvancedMultiTenantTableCJunitTest("testWriteProjectCache"));
            }
            suite.addTest(new AdvancedMultiTenantTableCJunitTest("testSetup"));
            suite.addTest(new AdvancedMultiTenantTableCJunitTest("testTablePerTenantC"));
            suite.addTest(new AdvancedMultiTenantTableCJunitTest("testTablePerTenantCQueries"));
        }
        return suite;
    }

    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
        new AdvancedMultiTenantTableCreator().replaceTables(JUnitTestCase.getServerSession(getMULTI_TENANT_TABLE_PER_TENANT_C_PU()));
    }

    public void testWriteProjectCache(){
        new org.eclipse.persistence.testing.tests.jpa.advanced.MetadataCachingTestSuite().testFileBasedProjectCacheLoading(getMULTI_TENANT_TABLE_PER_TENANT_C_PU());
    }

}
