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
//     06/30/2011-2.3.1 Guy Pelletier
//       - 341940: Add disable/enable allowing native queries
package org.eclipse.persistence.testing.tests.jpa.xml.advanced;

import junit.framework.*;

import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.multitenant.AdvancedMultiTenantTableCreator;

public class EntityMappingsMultitenantSharedEMFJUnitTestCase extends EntityMappingsMultitenantJUnitTestCase {
    public static final String MULTI_TENANT_PU = "MulitPU-1";

    public EntityMappingsMultitenantSharedEMFJUnitTestCase() {
        super();
    }

    public EntityMappingsMultitenantSharedEMFJUnitTestCase(String name) {
        super(name);
        setPuName(MULTI_TENANT_PU);
    }

    public void setUp() {}

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("Extended Advanced Multitenant Test Suite");
        if (! JUnitTestCase.isJPA10()) {
            suite.addTest(new EntityMappingsMultitenantSharedEMFJUnitTestCase("testSetup"));
            suite.addTest(new EntityMappingsMultitenantSharedEMFJUnitTestCase("testCreateMafiaFamily707"));
            suite.addTest(new EntityMappingsMultitenantSharedEMFJUnitTestCase("testCreateMafiaFamily007"));
            suite.addTest(new EntityMappingsMultitenantSharedEMFJUnitTestCase("testValidateMafiaFamily707"));
            suite.addTest(new EntityMappingsMultitenantSharedEMFJUnitTestCase("testValidateMafiaFamily007"));
            suite.addTest(new EntityMappingsMultitenantSharedEMFJUnitTestCase("testValidateMafiaFamily707and007WithSameEM"));
        }
        return suite;
    }

    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
        new AdvancedMultiTenantTableCreator().replaceTables(JUnitTestCase.getServerSession(MULTI_TENANT_PU));
    }

}

