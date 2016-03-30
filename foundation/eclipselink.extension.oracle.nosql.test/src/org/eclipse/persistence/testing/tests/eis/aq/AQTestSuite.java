/*******************************************************************************
 * Copyright (c) 1998, 2016 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 *     03/30/2016-2.7 Tomas Kraus
 *       - 490677: Database connection properties made configurable in test.properties
 ******************************************************************************/
package org.eclipse.persistence.testing.tests.eis.aq;

import org.eclipse.persistence.eis.EISLogin;
import org.eclipse.persistence.eis.adapters.aq.AQEISConnectionSpec;
import org.eclipse.persistence.eis.adapters.aq.AQPlatform;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.tests.nosql.NoSQLProperties;
import org.eclipse.persistence.testing.tests.nosql.SessionHelper;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Test TopLink EIS with the Oracle IP JCA drivers.
 * <p>
 * Test suite requires AQ extension to be installed in Oracle database:<ul>
 * <li>Log in as SYSDBA: {@code sqlplus <admin_user>/<admin_password> as SYSDBA}</li>
 * <li>Install AQ extension: {@code @@<orahome>\rdbms\admin\catproc.sql}</li>
 * <li>Create user and grant him required privileges:</li><ul>
 * <li>{@code CREATE USER <user> IDENTIFIED BY <password>;}</li>
 * <li>{@code GRANT CONNECT, RESOURCE, AQ_ADMINISTRATOR_ROLE TO <user> IDENTIFIED BY <password>;}</li>
 * <li>{@code GRANT EXECUTE ON dbms_aq TO <user>}</li>
 * <li>{@code GRANT EXECUTE ON dbms_aqin TO <user>}</li>
 * <li>{@code GRANT EXECUTE ON dbms_aqadm TO <user>}</li>
 * <li>{@code GRANT EXECUTE ON dbms_lock TO <user>}</li>
 * </ul></ul>
 */
@RunWith(Suite.class)
@SuiteClasses({
    JMSDirectConnectTest.class,
    JMSDirectInteractionTest.class,
    JavaDirectInteractionTest.class,
    ConnectTest.class,
    AuthenticationTest.class,
    OrderQueueTest.class
})
public class AQTestSuite {

    /** AQ database login information. Shared with whole test suite. */
    static final EISLogin login = initLogin();

    /** EclipseLink configuration. Shared with whole test suite. */
    static final Project project  = SessionHelper.createModelProject(login, AQTestSuite.class);

    /**
     * Initializes {@link EISLogin} with AQ connection specifications for test suite.
     * Class initialization helper method.
     * @return {@link EISLogin} with AQ connection specifications.
     */
    static EISLogin initLogin() {
        final EISLogin login = new EISLogin(new AQPlatform());
        login.setConnectionSpec(new AQEISConnectionSpec());
        login.setProperty(AQEISConnectionSpec.URL, NoSQLProperties.getDBURL());
        login.setUserName(NoSQLProperties.getDBUserName());
        login.setPassword(NoSQLProperties.getDBPassword());
        return login;
    }

}
