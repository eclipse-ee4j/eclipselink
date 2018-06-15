/*
 * Copyright (c) 2016, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     03/30/2016-2.7 Tomas Kraus
//       - 490677: Initial API and implementation.
package org.eclipse.persistence.testing.tests.eis.nosql;

import org.eclipse.persistence.eis.EISLogin;
import org.eclipse.persistence.nosql.adapters.nosql.OracleNoSQLConnectionSpec;
import org.eclipse.persistence.nosql.adapters.nosql.OracleNoSQLPlatform;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.tests.nosql.NoSQLProperties;
import org.eclipse.persistence.testing.tests.nosql.SessionHelper;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Test EclipseLink EIS with the Oracle NoSQL database.
 */
@RunWith(Suite.class)
@SuiteClasses({
    NoSQLSessionTest.class,
    NoSQLSimpleTest.class,
    NoSQLModelTest.class
})
public class NoSQLTestSuite {

    /** NoSQL database login information. Shared with whole test suite. */
    static final EISLogin login = initLogin();

    /** EclipseLink configuration without test model. Shared with whole test suite. */
    static final Project project  = SessionHelper.createProject(login, NoSQLTestSuite.class);

    /** EclipseLink configuration with test model. Shared with whole test suite. */
    static final Project modelProject  = SessionHelper.createModelProject(login, NoSQLTestSuite.class);

    /**
     * Initializes {@link EISLogin} with NoSQL connection specifications for test suite.
     * Class initialization helper method.
     * @return {@link EISLogin} with NoSQL connection specifications.
     */
    static EISLogin initLogin() {
        final EISLogin login = new EISLogin(new OracleNoSQLPlatform());
        login.setConnectionSpec(new OracleNoSQLConnectionSpec());
        NoSQLProperties.setEISLoginProperties(login);
        return login;
    }

}
