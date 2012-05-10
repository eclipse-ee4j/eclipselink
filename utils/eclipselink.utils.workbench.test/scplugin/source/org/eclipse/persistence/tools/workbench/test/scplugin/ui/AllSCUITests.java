/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
******************************************************************************/
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
