/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
//     04/30/2009-2.0 Michael O'Brien
//       - 266912: JPA 2.0 Metamodel API (part of Criteria API)
package org.eclipse.persistence.testing.tests.jpa.metamodel;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * The following functions from Chapter 5 of the JSR-317 JPA 2.0 API PFD are tested here.
 *
 */
public class MetamodelTestSuite extends TestSuite {

    public static Test suite() {
        TestSuite suite = new TestSuite("Metamodel");
        suite.addTest(EntityManagerFactoryImplTest.suite());
        suite.addTest(EntityManagerImplTest.suite());
        suite.addTest(MetamodelMetamodelTest.suite());
        return suite;
    }
}
