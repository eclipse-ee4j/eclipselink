/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.test.scplugin.model.read;

import java.net.URL;

import org.eclipse.persistence.tools.workbench.test.scplugin.AllSCTests;

import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.ConnectionPoolAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.DatabaseLoginAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.DatabaseSessionAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.LoginAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.ServerSessionAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.SessionAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.TopLinkSessionsAdapter;


import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class SessionsXml904Test extends TestCase
{
	public SessionsXml904Test(String name)
	{
		super(name);
	}

	public static Test suite()
	{
		return new TestSuite(SessionsXml904Test.class, "sessions.xml 9.0.4 Test");
	}

	public void test1() throws Exception
	{
		URL location = getClass().getResource("/9.0.4/sessions1.xml");
		TopLinkSessionsAdapter sessions = AllSCTests.loadSessions(location);
		assertNotNull(sessions);

		// Session
		assertTrue(sessions.sessionsSize() == 1);

		SessionAdapter session = sessions.sessionNamed("DatabaseSession");
		assertNotNull(session);
		assertTrue(session instanceof DatabaseSessionAdapter);

		DatabaseSessionAdapter databaseSession = (DatabaseSessionAdapter) session;

		// Project XML
		assertEquals("C:/toplink-deployment-descriptor.xml", databaseSession.getPrimaryProjectName());

		// Login
		LoginAdapter login = databaseSession.getLogin();
		assertNotNull(login);
		assertTrue(login instanceof DatabaseLoginAdapter);
		verifyDatabaseLogin((DatabaseLoginAdapter) login);
	}

	public void test2() throws Exception
	{
		URL location = getClass().getResource("/9.0.4/sessions2.xml");
		TopLinkSessionsAdapter sessions = AllSCTests.loadSessions(location);
		assertNotNull(sessions);

		// Session
		assertTrue(sessions.sessionsSize() == 2);

		SessionAdapter session = sessions.sessionNamed("ServerSession");
		assertNotNull(session);
		assertTrue(session instanceof ServerSessionAdapter);

		ServerSessionAdapter serverSession = (ServerSessionAdapter) session;

		// Project XML
		assertEquals("C:/toplink-deployment-descriptor.xml", serverSession.getPrimaryProjectName());

		// Login
		LoginAdapter login = serverSession.getLogin();
		assertNotNull(login);
		assertTrue(login instanceof DatabaseLoginAdapter);

		DatabaseLoginAdapter databaseLogin = (DatabaseLoginAdapter) login;
		assertEquals(databaseLogin.getDataSourceName(),  "MyDataSource");
		assertEquals(databaseLogin.getDriverClassName(), "sun.jdbc.odbc.JdbcOdbcDriver");
		assertEquals(databaseLogin.getPlatformClass(),   "org.eclipse.persistence.platform.database.SQLServerPlatform");
		assertEquals(databaseLogin.getUserName(),        "username");
		assertEquals("", databaseLogin.getPassword());
		assertNull(databaseLogin.getConnectionURL());

		// Connection Pool
		assertNotNull(serverSession.getReadConnectionPool());
		assertTrue(serverSession.poolsSize() == 1);

		ConnectionPoolAdapter pool = serverSession.poolNamed("Named Connection Pool");
		assertNotNull(pool);
	}

	private void verifyDatabaseLogin(DatabaseLoginAdapter login)
	{
		assertEquals("jdbc:oracle:thin:@tlsvrdb1.ca.oracle.com:1521:TOPLINK", login.getConnectionURL());
		assertEquals("oracle.jdbc.driver.OracleDriver",               						login.getDriverClassName());
		assertEquals("org.eclipse.persistence.platform.database.oracle.Oracle9Platform", 	login.getPlatformClass());
		assertEquals("tle",                                            						login.getUserName());
		assertEquals("password",                                      						login.getPassword());
		assertNull  (login.getDataSourceName());
	}
}