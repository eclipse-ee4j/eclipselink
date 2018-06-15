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
package org.eclipse.persistence.tools.workbench.test.scplugin.ui;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.test.scplugin.ui.broker.SessionBrokerGeneralPropertiesPageTest;
import org.eclipse.persistence.tools.workbench.test.scplugin.ui.pool.basic.EisPoolLoginPropertiesPageTest;
import org.eclipse.persistence.tools.workbench.test.scplugin.ui.pool.basic.EisReadPoolLoginPropertiesPageTest;
import org.eclipse.persistence.tools.workbench.test.scplugin.ui.pool.basic.PoolGeneralPropertiesPageTest;
import org.eclipse.persistence.tools.workbench.test.scplugin.ui.pool.basic.RdbmsPoolLoginPropertiesPageTest;
import org.eclipse.persistence.tools.workbench.test.scplugin.ui.pool.basic.RdbmsReadPoolLoginPropertiesPageTest;
import org.eclipse.persistence.tools.workbench.test.scplugin.ui.project.ProjectPropertiesPageTest;
import org.eclipse.persistence.tools.workbench.test.scplugin.ui.session.basic.SessionLoggingPropertiesPageTest;
import org.eclipse.persistence.tools.workbench.test.scplugin.ui.session.basic.SessionOptionsPropertiesPageTest;
import org.eclipse.persistence.tools.workbench.test.scplugin.ui.session.login.RdbmsConnectionPropertiesPageTest;
import org.eclipse.persistence.tools.workbench.test.scplugin.ui.session.login.RdbmsOptionsPropertiesPageTest;
import org.eclipse.persistence.tools.workbench.test.scplugin.ui.session.login.RdbmsSequencingPropertiesPageTest;


public final class AllSCUITests
{

    public static Test suite()
    {
        TestSuite suite = new TestSuite("PropertiesPage Tests");

        // ui.broker
        suite.addTestSuite(SessionBrokerGeneralPropertiesPageTest.class);

        // ui.pool.basic
        suite.addTestSuite(EisPoolLoginPropertiesPageTest.class);
        suite.addTestSuite(EisReadPoolLoginPropertiesPageTest.class);
        suite.addTestSuite(PoolGeneralPropertiesPageTest.class);
        suite.addTestSuite(RdbmsPoolLoginPropertiesPageTest.class);
        suite.addTestSuite(RdbmsReadPoolLoginPropertiesPageTest.class);

        // ui.project
        suite.addTestSuite(ProjectPropertiesPageTest.class);

        // ui.session.basic
        suite.addTestSuite(SessionLoggingPropertiesPageTest.class);
        suite.addTestSuite(SessionOptionsPropertiesPageTest.class);

        // ui.session.login
        suite.addTestSuite(RdbmsConnectionPropertiesPageTest.class);
        suite.addTestSuite(RdbmsOptionsPropertiesPageTest.class);
        suite.addTestSuite(RdbmsSequencingPropertiesPageTest.class);

        return suite;
    }
}
