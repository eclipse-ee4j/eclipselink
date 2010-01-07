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
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.DatabaseLoginAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.DatabaseSessionAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.ServerPlatform;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.ServerPlatformAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.TopLinkSessionsAdapter;

public class DatabaseSessionAdapterTest extends AbstractAdapterTest
{
	public DatabaseSessionAdapterTest(String name)
	{
		super(name);
	}

	public static Test suite()
	{
		return new TestSuite(DatabaseSessionAdapterTest.class, "DatabaseSessionAdapter Test");
	}

	public void testVerifyProblemServerPlatform() throws Exception
	{
		TopLinkSessionsAdapter sessions = AllSCTests.createNewSessions();
		DataSource ds = buildOracleDataSource();
		ServerPlatform sp = new ServerPlatform( "CustomServerPlatform");
		DatabaseSessionAdapter session = sessions.addDatabaseSessionNamed("MyDatabaseSession", sp, ds);
		ServerPlatformAdapter serverPlatform = session.getServerPlatform();
		
		// Custom Server Platform should fail
		assertTrue("Custom Server Platform - Should not have problem",
						hasProblem(SCProblemsConstants.CUSTOM_SERVER_PLATFORM_SERVER_CLASS_NAME, serverPlatform));
	}

	public void testVerifyProblemDataSource() throws Exception
	{
		TopLinkSessionsAdapter sessions = AllSCTests.createNewSessions();
		DataSource ds = buildOracleDataSource();
		DatabaseSessionAdapter session = sessions.addDatabaseSessionNamed("MyDatabaseSession", noServerPlatform(), ds);
		DatabaseLoginAdapter login = (DatabaseLoginAdapter) session.getLogin();
		login.setDatabaseDriverAsDataSource();

		// Data Source Name should fail
		assertTrue("Data Source Name - Should have problem",
					  hasProblem(SCProblemsConstants.DATABASE_LOGIN_DATA_SOURCE_NAME, login));

		// Fix Data Source Name
		login.setDataSourceName("OracleDB");

		assertFalse("Contain Data Source Name - Should not have problem",
						hasProblem(SCProblemsConstants.DATABASE_LOGIN_DATA_SOURCE_NAME, login));
	}

	public void testVerifyProblemDriverManager() throws Exception
	{
		TopLinkSessionsAdapter sessions = AllSCTests.createNewSessions();
		DataSource ds = buildOracleDataSource();
		DatabaseSessionAdapter session = sessions.addDatabaseSessionNamed("MyDatabaseSession", noServerPlatform(), ds);
		DatabaseLoginAdapter login = (DatabaseLoginAdapter) session.getLogin();

		// Driver Class should fail
		assertTrue("Driver Class - Should have problem",
					  hasProblem(SCProblemsConstants.DATABASE_LOGIN_DRIVER_CLASS, login));

		// Driver URL should fail
		assertTrue("Driver URL - Should have problem",
			  		  hasProblem(SCProblemsConstants.DATABASE_LOGIN_CONNECTION_URL, login));

		// Fix Driver Class
		login.setDriverClassName("oracle.jdbc.driver.OracleDriver");

		assertFalse("Contain Driver Class - Should not have problem",
						hasProblem(SCProblemsConstants.DATABASE_LOGIN_DRIVER_CLASS, login));

		// Fix Driver URL
		login.setConnectionURL("jdbc:oracle:thin:@144.23.214.115:1521:toplink");

		assertFalse("Contain Driver URL - Should not have problem",
						hasProblem(SCProblemsConstants.DATABASE_LOGIN_CONNECTION_URL, login));
	}
}
