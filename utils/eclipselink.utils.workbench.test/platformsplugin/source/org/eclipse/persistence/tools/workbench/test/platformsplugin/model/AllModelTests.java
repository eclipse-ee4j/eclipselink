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
package org.eclipse.persistence.tools.workbench.test.platformsplugin.model;

import junit.extensions.ActiveTestSuite;
import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.utility.ClassTools;

/**
 * decentralize test creation code
 */
public class AllModelTests {

    public static Test suite() {
        // *************************************************************************
        //   NOTE - NOTE - NOTE - NOTE - NOTE
        // run these tests in separate threads since they spend lots of time waiting on the database servers
        // *************************************************************************
        TestSuite suite = new ActiveTestSuite(ClassTools.packageNameFor(AllModelTests.class));

        suite.addTest(DatabasePlatformRepositoryTests.suite());
        suite.addTest(DatabasePlatformTests.suite());
        suite.addTest(DatabaseTypeTests.suite());
        suite.addTest(JavaTypeDeclarationTests.suite());
        suite.addTest(JDBCTypeRepositoryTests.suite());

        return suite;
    }

    private AllModelTests() {
        super();
    }

}
