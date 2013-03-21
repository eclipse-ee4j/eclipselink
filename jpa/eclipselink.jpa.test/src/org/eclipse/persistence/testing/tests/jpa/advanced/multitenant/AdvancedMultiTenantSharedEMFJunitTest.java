/*******************************************************************************
 * Copyright (c) 2011, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     03/23/2011-2.3 Guy Pelletier 
 *       - 337323: Multi-tenant with shared schema support (part 1)
 *     04/01/2011-2.3 Guy Pelletier 
 *       - 337323: Multi-tenant with shared schema support (part 2)
 *     04/21/2011-2.3 Guy Pelletier 
 *       - 337323: Multi-tenant with shared schema support (part 5)
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.jpa.advanced.multitenant;

import junit.framework.*;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.advanced.multitenant.AdvancedMultiTenantTableCreator;

public class AdvancedMultiTenantSharedEMFJunitTest extends AdvancedMultiTenantJunitTest { 
    
    public AdvancedMultiTenantSharedEMFJunitTest() {
        super();
    }

    public String getMULTI_TENANT_PU() { return "MulitPU-1"; }
    
    public AdvancedMultiTenantSharedEMFJunitTest(String name) {
        super(name);
        setPuName(getMULTI_TENANT_PU());
    }
    
    public void setUp() {}
    
    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("AdvancedMultiTenantSharedEMFJunitTest");
        if (! JUnitTestCase.isJPA10()) {
            if (System.getProperty("run.metadata.cache.test.suite").compareTo("true") == 0) {
                suite.addTest(new AdvancedMultiTenantSharedEMFJunitTest("testWriteProjectCache"));
            }
            suite.addTest(new AdvancedMultiTenantSharedEMFJunitTest("testSetup"));
            suite.addTest(new AdvancedMultiTenantSharedEMFJunitTest("testCreateMafiaFamily707"));
            suite.addTest(new AdvancedMultiTenantSharedEMFJunitTest("testCreateMafiaFamily007"));
            suite.addTest(new AdvancedMultiTenantSharedEMFJunitTest("testValidateMafiaFamily707"));
            suite.addTest(new AdvancedMultiTenantSharedEMFJunitTest("testValidateMafiaFamily007"));
            suite.addTest(new AdvancedMultiTenantSharedEMFJunitTest("testValidateMafiaFamily707and007WithSameEM"));
            suite.addTest(new AdvancedMultiTenantSharedEMFJunitTest("testMultitenantOneToOneReadObjectRead"));
            suite.addTest(new AdvancedMultiTenantSharedEMFJunitTest("testMultitenantPrimaryKeyWithIdClass"));
        }
        return suite;
    }

    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
        new AdvancedMultiTenantTableCreator().replaceTables(JUnitTestCase.getServerSession(getMULTI_TENANT_PU()));
    }

    public void testWriteProjectCache(){
        new org.eclipse.persistence.testing.tests.jpa.advanced.MetadataCachingTestSuite().testFileBasedProjectCacheLoading(getMULTI_TENANT_PU());
    }

}
