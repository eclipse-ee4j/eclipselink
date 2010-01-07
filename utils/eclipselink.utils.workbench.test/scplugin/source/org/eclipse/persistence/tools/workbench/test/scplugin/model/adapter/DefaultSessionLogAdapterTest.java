/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
package org.eclipse.persistence.tools.workbench.test.scplugin.model.adapter;

import org.eclipse.persistence.tools.workbench.test.scplugin.AllSCTests;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.tools.workbench.scplugin.SCProblemsConstants;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.DataSource;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.DatabaseSessionAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.DefaultSessionLogAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.TopLinkSessionsAdapter;

public class DefaultSessionLogAdapterTest extends AbstractAdapterTest
{
	public DefaultSessionLogAdapterTest(String name)
	{
		super(name);
	}

	public static Test suite()
	{
		return new TestSuite(DefaultSessionLogAdapterTest.class, "DefaultSessionLogAdapter Test");
	}

	public void testVerifyProblemMappingProject() throws Exception
	{
		TopLinkSessionsAdapter sessions = AllSCTests.createNewSessions();
		DataSource ds = buildOracleDataSource();
		DatabaseSessionAdapter session = sessions.addDatabaseSessionNamed("MyDatabaseSession", noServerPlatform(), ds);
		DefaultSessionLogAdapter log = session.setDefaultLogging();

		// File Name should fail
		log.setFileName("");

		assertTrue("File Name - Should have problem",
					  hasProblem(SCProblemsConstants.DEFAULT_LOGGING_FILE_NAME, log));

		// Fix File Name
		log.setFileName("/toplink-dd.log");

		assertFalse("Has a File Name - Should not have problem",
						hasProblem(SCProblemsConstants.DEFAULT_LOGGING_FILE_NAME, log));

		// Fix Name should fail
		log.setFileName(DefaultSessionLogAdapter.DEFAULT_LOG_FILE);

		assertFalse("Has a File Name - Should not have problem",
						hasProblem(SCProblemsConstants.DEFAULT_LOGGING_FILE_NAME, log));
	}
}
