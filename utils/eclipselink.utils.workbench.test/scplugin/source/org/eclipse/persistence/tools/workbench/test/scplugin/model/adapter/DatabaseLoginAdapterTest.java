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
package org.eclipse.persistence.tools.workbench.test.scplugin.model.adapter;

import org.eclipse.persistence.tools.workbench.test.scplugin.AllSCTests;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.scplugin.SCPlugin;
import org.eclipse.persistence.tools.workbench.scplugin.SCProblemsConstants;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.DataSource;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.DatabaseLoginAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.DatabaseSessionAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.LoginAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.ProjectAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.ServerPlatform;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.ServerPlatformAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.SessionBrokerAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.TopLinkSessionsAdapter;


/**
 * ECP stands for External Connection Pooling.
 *
 * @version 10.1.3
 * @author Pascal Filion
 */
public class DatabaseLoginAdapterTest extends AbstractAdapterTest
{
	public DatabaseLoginAdapterTest(String name)
	{
		super(name);
	}

	public static Test suite()
	{
		return new TestSuite(DatabaseLoginAdapterTest.class, "DatabaseLoginAdapter Test");
	}

	private void _test_ECP_Broker_Session_Imp(SessionBrokerAdapter sessionBroker,
																						DatabaseSessionAdapter session) throws Exception
	{
		ServerPlatformAdapter serverPlatform = session.getServerPlatform();
		LoginAdapter login = session.getLogin();

		// Default values
		assertTrue (serverPlatform.isNull());
		assertFalse(serverPlatform.getEnableJTA());

		if (session.platformIsRdbms())
			assertTrue(login.databaseDriverIsDriverManager());

		assertFalse(login.databaseDriverIsDataSource());
		assertFalse(session.usesExternalConnectionPooling());
		assertFalse(session.hasJTA());

		// Now manage the session
		sessionBroker.manage("MyDatabaseSession");

		assertFalse(serverPlatform.getEnableJTA());
		assertFalse(session.usesExternalConnectionPooling());
		assertFalse(session.hasJTA());

		// Make sure the session's values reflect the broker's values
		serverPlatform = sessionBroker.setServerPlatform(new ServerPlatform(SCPlugin.SERVER_PLATFORM_PREFERENCE_DEFAULT));

		assertTrue(serverPlatform.getEnableJTA());
		assertTrue(session.usesExternalConnectionPooling());
		assertTrue(session.hasJTA());
	}

	private void _test_ECP_ServerPlatform_Broker_Imp(SessionBrokerAdapter sessionBroker,
																								 DatabaseSessionAdapter session) throws Exception
	{
		ServerPlatformAdapter serverPlatform = session.getServerPlatform();
		LoginAdapter login = session.getLogin();

		// Default values
		assertTrue (serverPlatform.isNull());
		assertFalse(serverPlatform.getEnableJTA());

		if (session.platformIsRdbms())
			assertTrue(login.databaseDriverIsDriverManager());

		assertFalse(login.databaseDriverIsDataSource());
		assertFalse(session.usesExternalConnectionPooling());
		assertFalse(session.hasJTA());

		sessionBroker.manage("MyDatabaseSession");

		// Select a Server Platform
		serverPlatform = sessionBroker.setServerPlatform(new ServerPlatform(SCPlugin.SERVER_PLATFORM_PREFERENCE_DEFAULT));

		assertTrue(serverPlatform.getEnableJTA());
		assertTrue(session.usesExternalConnectionPooling());
		assertTrue(session.hasJTA());

		// Remove Server Platform, make sure it's back to false
		serverPlatform = sessionBroker.setServerPlatform(noServerPlatform());

		assertFalse(serverPlatform.getEnableJTA());
		assertFalse(session.usesExternalConnectionPooling());
		assertFalse(session.hasJTA());

		// Unmanage the session
		sessionBroker.unManage("MyDatabaseSession");

		assertFalse(session.usesExternalConnectionPooling());
		assertFalse(session.hasJTA());
	}

	private void _test_ECP_ServerPlatform_Imp(DatabaseSessionAdapter session) throws Exception
	{
		ServerPlatformAdapter serverPlatform = session.getServerPlatform();
		LoginAdapter login = session.getLogin();

		// Default values
		assertTrue (serverPlatform.isNull());
		assertFalse(serverPlatform.getEnableJTA());

		if (session.platformIsRdbms())
			assertTrue(login.databaseDriverIsDriverManager());

		assertFalse(login.databaseDriverIsDataSource());
		assertFalse(session.usesExternalConnectionPooling());
		assertFalse(session.hasJTA());

		// Select a Server Platform
		serverPlatform = session.setServerPlatform(new ServerPlatform(SCPlugin.SERVER_PLATFORM_PREFERENCE_DEFAULT));

		assertTrue(serverPlatform.getEnableJTA());
		assertTrue(session.usesExternalConnectionPooling());
		assertTrue (session.hasJTA());

		// Remove Server Platform, make sure it's back to false
		serverPlatform = session.setServerPlatform(noServerPlatform());

		assertFalse(serverPlatform.getEnableJTA());
		assertFalse(session.usesExternalConnectionPooling());
		assertFalse(session.hasJTA());
	}

	private void _test_ECP_ServerPlatform2_Imp(DatabaseSessionAdapter session) throws Exception
	{
		ServerPlatformAdapter serverPlatform = session.getServerPlatform();
		DatabaseLoginAdapter login = (DatabaseLoginAdapter) session.getLogin();

		// Default values
		assertTrue (serverPlatform.isNull());
		assertFalse(serverPlatform.getEnableJTA());

		if (session.platformIsRdbms())
			assertTrue(login.databaseDriverIsDriverManager());

		assertFalse(login.databaseDriverIsDataSource());
		assertFalse(session.usesExternalConnectionPooling());
		assertFalse(session.hasJTA());

		// Select a Server Platform
		serverPlatform = session.setServerPlatform(new ServerPlatform(SCPlugin.SERVER_PLATFORM_PREFERENCE_DEFAULT));

		assertTrue(serverPlatform.getEnableJTA());
		assertTrue(session.usesExternalConnectionPooling());
		assertTrue (session.hasJTA());

		// Change Server Platform values
		serverPlatform.setEnableJTA(false);

		assertFalse(session.usesExternalConnectionPooling());
		assertFalse(session.hasJTA());
	}

	public void test_ECP_Broker_Session_DatabaseDriver() throws Exception
	{
		TopLinkSessionsAdapter sessions = AllSCTests.createNewSessions();
		DatabaseSessionAdapter session = sessions.addDatabaseSessionNamed("MyDatabaseSession", noServerPlatform(), buildOracleDataSource());
		SessionBrokerAdapter sessionBroker = sessions.addSessionBrokerNamed("MySessionBroker", noServerPlatform());

		ServerPlatformAdapter serverPlatform = session.getServerPlatform();
		DatabaseLoginAdapter login = (DatabaseLoginAdapter) session.getLogin();

		// Default values
		assertTrue (serverPlatform.isNull());
		assertFalse(serverPlatform.getEnableJTA());
		assertTrue (login.databaseDriverIsDriverManager());
		assertFalse(login.databaseDriverIsDataSource());
		assertFalse(session.usesExternalConnectionPooling());
		assertFalse(session.hasJTA());

		// Now manage the session
		sessionBroker.manage("MyDatabaseSession");

		assertFalse(serverPlatform.getEnableJTA());
		assertFalse(session.usesExternalConnectionPooling());
		assertFalse(session.hasJTA());

		// Make sure the session's values reflect the broker's values
		serverPlatform = sessionBroker.setServerPlatform(new ServerPlatform(SCPlugin.SERVER_PLATFORM_PREFERENCE_DEFAULT));

		assertTrue(serverPlatform.getEnableJTA());
		assertTrue(session.usesExternalConnectionPooling());
		assertTrue(session.hasJTA());

		// Change to J2EE Data Source
		login.setDatabaseDriverAsDataSource();
		session.updateExternalConnectionPooling(); // Has to be done manually

		assertFalse(login.databaseDriverIsDriverManager());
		assertTrue (login.databaseDriverIsDataSource());
		assertTrue (session.usesExternalConnectionPooling());
		assertTrue (login.usesExternalTransactionController());
		assertTrue (session.hasJTA());
	}

	public void test_ECP_Broker_Session_EIS() throws Exception
	{
		TopLinkSessionsAdapter sessions = AllSCTests.createNewSessions();
		DatabaseSessionAdapter session = sessions.addDatabaseSessionNamed("MyDatabaseSession", noServerPlatform(), buildAQDataSource());
		SessionBrokerAdapter sessionBroker = sessions.addSessionBrokerNamed("MySessionBroker", noServerPlatform());

		_test_ECP_Broker_Session_Imp(sessionBroker, session);
	}

	public void test_ECP_Broker_Session_RDBMS() throws Exception
	{
		TopLinkSessionsAdapter sessions = AllSCTests.createNewSessions();
		DatabaseSessionAdapter session = sessions.addDatabaseSessionNamed("MyDatabaseSession", noServerPlatform(), buildOracleDataSource());
		SessionBrokerAdapter sessionBroker = sessions.addSessionBrokerNamed("MySessionBroker", noServerPlatform());

		_test_ECP_Broker_Session_Imp(sessionBroker, session);
	}

	public void test_ECP_Broker_Session_XML() throws Exception
	{
		TopLinkSessionsAdapter sessions = AllSCTests.createNewSessions();
		DatabaseSessionAdapter session = sessions.addDatabaseSessionNamed("MyDatabaseSession", noServerPlatform(), DataSource.buildXmlDataSource());
		SessionBrokerAdapter sessionBroker = sessions.addSessionBrokerNamed("MySessionBroker", noServerPlatform());

		_test_ECP_Broker_Session_Imp(sessionBroker, session);
	}

	public void test_ECP_DatabaseDriver() throws Exception
	{
		TopLinkSessionsAdapter sessions = AllSCTests.createNewSessions();
		DatabaseSessionAdapter session = sessions.addDatabaseSessionNamed("MyDatabaseSession", noServerPlatform(), buildOracleDataSource());
		DatabaseLoginAdapter login = (DatabaseLoginAdapter) session.getLogin();

		// Default values
		assertTrue (login.databaseDriverIsDriverManager());
		assertFalse(login.databaseDriverIsDataSource());
		assertFalse(session.usesExternalConnectionPooling());
		assertFalse(login.usesExternalTransactionController());

		// Change to J2EE Data Source
		login.setDatabaseDriverAsDataSource();
		session.updateExternalConnectionPooling(); // Has to be done manually

		assertFalse(login.databaseDriverIsDriverManager());
		assertTrue (login.databaseDriverIsDataSource());
		assertTrue (session.usesExternalConnectionPooling());
		assertFalse(login.usesExternalTransactionController());

		// Change back to Driver Manager
		login.setDatabaseDriverAsDriverManager();
		session.updateExternalConnectionPooling(); // Has to be done manually

		assertTrue (login.databaseDriverIsDriverManager());
		assertFalse(login.databaseDriverIsDataSource());
		assertFalse(session.usesExternalConnectionPooling());
		assertFalse(login.usesExternalTransactionController());
		assertFalse(session.hasJTA());
	}

	public void test_ECP_ServerPlatform_Broker_DatabaseDriver1() throws Exception
	{
		TopLinkSessionsAdapter sessions = AllSCTests.createNewSessions();
		DatabaseSessionAdapter session = sessions.addDatabaseSessionNamed("MyDatabaseSession", noServerPlatform(), buildOracleDataSource());
		SessionBrokerAdapter sessionBroker = sessions.addSessionBrokerNamed("MySessionBroker", noServerPlatform());

		ServerPlatformAdapter serverPlatform = session.getServerPlatform();
		DatabaseLoginAdapter login = (DatabaseLoginAdapter) session.getLogin();

		// Default values
		assertTrue (serverPlatform.isNull());
		assertFalse(serverPlatform.getEnableJTA());
		assertTrue (login.databaseDriverIsDriverManager());
		assertFalse(login.databaseDriverIsDataSource());
		assertFalse(session.usesExternalConnectionPooling());
		assertFalse(session.hasJTA());

		// Now manage the session
		sessionBroker.manage("MyDatabaseSession");

		// Change to J2EE Data Source
		login.setDatabaseDriverAsDataSource();
		session.updateExternalConnectionPooling(); // Has to be done manually

		// Select a Server Platform
		serverPlatform = sessionBroker.setServerPlatform(new ServerPlatform(SCPlugin.SERVER_PLATFORM_PREFERENCE_DEFAULT));

		assertTrue(serverPlatform.getEnableJTA());
		assertTrue(session.usesExternalConnectionPooling());
		assertTrue(session.hasJTA());

		// Remove Server Platform, make sure it's back to false
		serverPlatform = sessionBroker.setServerPlatform(noServerPlatform());

		assertFalse(serverPlatform.getEnableJTA());
		assertTrue (session.usesExternalConnectionPooling());
		assertFalse(session.hasJTA());

		// Unmanage the session
		sessionBroker.unManage("MyDatabaseSession");

		assertTrue (session.usesExternalConnectionPooling());
		assertFalse(session.hasJTA());
	}

	public void test_ECP_ServerPlatform_Broker_DatabaseDriver2() throws Exception
	{
		TopLinkSessionsAdapter sessions = AllSCTests.createNewSessions();
		DatabaseSessionAdapter session = sessions.addDatabaseSessionNamed("MyDatabaseSession", noServerPlatform(), buildOracleDataSource());
		SessionBrokerAdapter sessionBroker = sessions.addSessionBrokerNamed("MySessionBroker", noServerPlatform());

		ServerPlatformAdapter serverPlatform = session.getServerPlatform();
		DatabaseLoginAdapter login = (DatabaseLoginAdapter) session.getLogin();

		// Default values
		assertTrue (serverPlatform.isNull());
		assertFalse(serverPlatform.getEnableJTA());
		assertTrue (login.databaseDriverIsDriverManager());
		assertFalse(login.databaseDriverIsDataSource());
		assertFalse(session.usesExternalConnectionPooling());
		assertFalse(session.hasJTA());

		// Now manage the session
		sessionBroker.manage("MyDatabaseSession");

		// Change to J2EE Data Source
		login.setDatabaseDriverAsDataSource();
		session.updateExternalConnectionPooling(); // Has to be done manually

		// Select a Server Platform
		serverPlatform = sessionBroker.setServerPlatform(new ServerPlatform(SCPlugin.SERVER_PLATFORM_PREFERENCE_DEFAULT));

		assertTrue(serverPlatform.getEnableJTA());
		assertTrue(session.usesExternalConnectionPooling());
		assertTrue(session.hasJTA());

		// Remove Server Platform
		serverPlatform = sessionBroker.setServerPlatform(noServerPlatform());

		assertFalse(serverPlatform.getEnableJTA());
		assertTrue (session.usesExternalConnectionPooling());
		assertFalse(session.hasJTA());

		// Change back to Driver Manager
		login.setDatabaseDriverAsDriverManager();

		assertTrue (session.usesExternalConnectionPooling());
		assertFalse(session.hasJTA());

		// Unmanage the session
		sessionBroker.unManage("MyDatabaseSession");

		assertFalse(session.usesExternalConnectionPooling());
		assertFalse(session.hasJTA());
	}

	public void test_ECP_ServerPlatform_Broker_EIS() throws Exception
	{
		TopLinkSessionsAdapter sessions = AllSCTests.createNewSessions();
		DatabaseSessionAdapter session = sessions.addDatabaseSessionNamed("MyDatabaseSession", noServerPlatform(), buildAQDataSource());
		SessionBrokerAdapter sessionBroker = sessions.addSessionBrokerNamed("MySessionBroker", noServerPlatform());

		_test_ECP_ServerPlatform_Broker_Imp(sessionBroker, session);
	}

	public void test_ECP_ServerPlatform_Broker_RDBMS() throws Exception
	{
		TopLinkSessionsAdapter sessions = AllSCTests.createNewSessions();
		DatabaseSessionAdapter session = sessions.addDatabaseSessionNamed("MyDatabaseSession", noServerPlatform(), buildOracleDataSource());
		SessionBrokerAdapter sessionBroker = sessions.addSessionBrokerNamed("MySessionBroker", noServerPlatform());

		_test_ECP_ServerPlatform_Broker_Imp(sessionBroker, session);
	}

	public void test_ECP_ServerPlatform_Broker_XML() throws Exception
	{
		TopLinkSessionsAdapter sessions = AllSCTests.createNewSessions();
		DatabaseSessionAdapter session = sessions.addDatabaseSessionNamed("MyDatabaseSession", noServerPlatform(), DataSource.buildXmlDataSource());
		SessionBrokerAdapter sessionBroker = sessions.addSessionBrokerNamed("MySessionBroker", noServerPlatform());

		_test_ECP_ServerPlatform_Broker_Imp(sessionBroker, session);
	}

	public void test_ECP_ServerPlatform_DatabaseDriver1() throws Exception
	{
		TopLinkSessionsAdapter sessions = AllSCTests.createNewSessions();
		DatabaseSessionAdapter session = sessions.addDatabaseSessionNamed("MyDatabaseSession", noServerPlatform(), buildOracleDataSource());
		ServerPlatformAdapter serverPlatform = session.getServerPlatform();
		DatabaseLoginAdapter login = (DatabaseLoginAdapter) session.getLogin();

		// Default values
		assertTrue (serverPlatform.isNull());
		assertFalse(serverPlatform.getEnableJTA());
		assertTrue (login.databaseDriverIsDriverManager());
		assertFalse(login.databaseDriverIsDataSource());
		assertFalse(session.usesExternalConnectionPooling());
		assertFalse(session.hasJTA());

		// Change to J2EE Data Source
		login.setDatabaseDriverAsDataSource();
		session.updateExternalConnectionPooling(); // Has to be done manually

		// Select a Server Platform
		serverPlatform = session.setServerPlatform(new ServerPlatform(SCPlugin.SERVER_PLATFORM_PREFERENCE_DEFAULT));

		assertTrue(serverPlatform.getEnableJTA());
		assertTrue(session.usesExternalConnectionPooling());
		assertTrue(session.hasJTA());

		// Remove Server Platform, make sure it's back to false
		serverPlatform = session.setServerPlatform(noServerPlatform());

		assertFalse(serverPlatform.getEnableJTA());
		assertTrue (session.usesExternalConnectionPooling());
		assertFalse(session.hasJTA());
	}

	public void test_ECP_ServerPlatform_DatabaseDriver2() throws Exception
	{
		TopLinkSessionsAdapter sessions = AllSCTests.createNewSessions();
		DatabaseSessionAdapter session = sessions.addDatabaseSessionNamed("MyDatabaseSession", noServerPlatform(), buildOracleDataSource());
		ServerPlatformAdapter serverPlatform = session.getServerPlatform();
		DatabaseLoginAdapter login = (DatabaseLoginAdapter) session.getLogin();

		// Default values
		assertTrue (serverPlatform.isNull());
		assertFalse(serverPlatform.getEnableJTA());
		assertTrue (login.databaseDriverIsDriverManager());
		assertFalse(login.databaseDriverIsDataSource());
		assertFalse(session.usesExternalConnectionPooling());
		assertFalse(session.hasJTA());

		// Select a Server Platform
		serverPlatform = session.setServerPlatform(new ServerPlatform(SCPlugin.SERVER_PLATFORM_PREFERENCE_DEFAULT));

		assertTrue(serverPlatform.getEnableJTA());
		assertTrue(session.usesExternalConnectionPooling());
		assertTrue(session.hasJTA());

		// Change to J2EE Data Source
		login.setDatabaseDriverAsDataSource();
		session.updateExternalConnectionPooling(); // Has to be done manually

		// Remove Server Platform, make sure it's back to false
		serverPlatform = session.setServerPlatform(noServerPlatform());

		assertFalse(serverPlatform.getEnableJTA());
		assertTrue (session.usesExternalConnectionPooling());
		assertFalse(session.hasJTA());
	}

	public void test_ECP_ServerPlatform1_EIS() throws Exception
	{
		TopLinkSessionsAdapter sessions = AllSCTests.createNewSessions();
		DatabaseSessionAdapter session = sessions.addDatabaseSessionNamed("MyDatabaseSession", noServerPlatform(), buildAQDataSource());

		_test_ECP_ServerPlatform_Imp(session);
	}

	public void test_ECP_ServerPlatform1_RDBMS() throws Exception
	{
		TopLinkSessionsAdapter sessions = AllSCTests.createNewSessions();
		DatabaseSessionAdapter session = sessions.addDatabaseSessionNamed("MyDatabaseSession", noServerPlatform(), buildOracleDataSource());

		_test_ECP_ServerPlatform_Imp(session);
	}

	public void test_ECP_ServerPlatform1_XML() throws Exception
	{
		TopLinkSessionsAdapter sessions = AllSCTests.createNewSessions();
		DatabaseSessionAdapter session = sessions.addDatabaseSessionNamed("MyDatabaseSession", noServerPlatform(), DataSource.buildXmlDataSource());

		_test_ECP_ServerPlatform_Imp(session);
	}

	public void test_ECP_ServerPlatform2_EIS() throws Exception
	{
		TopLinkSessionsAdapter sessions = AllSCTests.createNewSessions();
		DatabaseSessionAdapter session = sessions.addDatabaseSessionNamed("MyDatabaseSession", noServerPlatform(), buildOracleDataSource());

		_test_ECP_ServerPlatform2_Imp(session);
	}

	public void test_ECP_ServerPlatform2_RDBMS() throws Exception
	{
		TopLinkSessionsAdapter sessions = AllSCTests.createNewSessions();
		DatabaseSessionAdapter session = sessions.addDatabaseSessionNamed("MyDatabaseSession", noServerPlatform(), buildOracleDataSource());

		_test_ECP_ServerPlatform2_Imp(session);
	}

	public void test_ECP_ServerPlatform2_XML() throws Exception
	{
		TopLinkSessionsAdapter sessions = AllSCTests.createNewSessions();
		DatabaseSessionAdapter session = sessions.addDatabaseSessionNamed("MyDatabaseSession", noServerPlatform(), buildOracleDataSource());

		_test_ECP_ServerPlatform2_Imp(session);
	}

	public void test_ECP_ServerPlatform3() throws Exception
	{
		TopLinkSessionsAdapter sessions = AllSCTests.createNewSessions();
		DatabaseSessionAdapter session = sessions.addDatabaseSessionNamed("MyDatabaseSession", noServerPlatform(), buildOracleDataSource());
		ServerPlatformAdapter serverPlatform = session.getServerPlatform();
		DatabaseLoginAdapter login = (DatabaseLoginAdapter) session.getLogin();

		// Default values
		assertTrue (serverPlatform.isNull());
		assertFalse(serverPlatform.getEnableJTA());
		assertTrue (login.databaseDriverIsDriverManager());
		assertFalse(login.databaseDriverIsDataSource());
		assertFalse(session.usesExternalConnectionPooling());
		assertFalse(session.hasJTA());

		// Select a Server Platform
		serverPlatform = session.setServerPlatform(new ServerPlatform(SCPlugin.SERVER_PLATFORM_PREFERENCE_DEFAULT));

		assertTrue(serverPlatform.getEnableJTA());
		assertTrue(session.usesExternalConnectionPooling());
		assertTrue (session.hasJTA());

		// Change Server Platform values
		serverPlatform.setEnableJTA(false);

		// Change to J2EE Data Source
		login.setDatabaseDriverAsDataSource();
		session.updateExternalConnectionPooling(); // Has to be done manually

		assertTrue (session.usesExternalConnectionPooling());
		assertFalse(session.hasJTA());
	}

	public void test_ECP_ServerPlatform4() throws Exception
	{
		TopLinkSessionsAdapter sessions = AllSCTests.createNewSessions();
		DatabaseSessionAdapter session = sessions.addDatabaseSessionNamed("MyDatabaseSession", noServerPlatform(), buildOracleDataSource());
		ServerPlatformAdapter serverPlatform = session.getServerPlatform();
		DatabaseLoginAdapter login = (DatabaseLoginAdapter) session.getLogin();

		// Default values
		assertTrue (serverPlatform.isNull());
		assertFalse(serverPlatform.getEnableJTA());
		assertTrue (login.databaseDriverIsDriverManager());
		assertFalse(login.databaseDriverIsDataSource());
		assertFalse(session.usesExternalConnectionPooling());
		assertFalse(session.hasJTA());

		// Select a Server Platform
		serverPlatform = session.setServerPlatform(new ServerPlatform(SCPlugin.SERVER_PLATFORM_PREFERENCE_DEFAULT));

		assertTrue(serverPlatform.getEnableJTA());
		assertTrue(session.usesExternalConnectionPooling());
		assertTrue (session.hasJTA());

		// Change to J2EE Data Source
		login.setDatabaseDriverAsDataSource();
		session.updateExternalConnectionPooling(); // Has to be done manually

		assertTrue(session.usesExternalConnectionPooling());
		assertTrue(session.hasJTA());

		// Change Server Platform values
		serverPlatform.setEnableJTA(false);

		assertTrue (session.usesExternalConnectionPooling());
		assertFalse(session.hasJTA());
	}

	public void testVerifyProblemMappingProject() throws Exception
	{
		TopLinkSessionsAdapter sessions = AllSCTests.createNewSessions();
		DatabaseSessionAdapter session = sessions.addDatabaseSessionNamed("MyDatabaseSession", noServerPlatform(), buildOracleDataSource());

		// Mapping Project should fail
		assertTrue("Mapping Project - Should have problem",
					  hasProblem(SCProblemsConstants.SESSION_DATABASE_MAPPING_PROJECT, session));

		// Add Primary Mapping Project - XML
		session.addPrimaryProjectXmlNamed("/toplink-dd.xml");

		assertFalse("Contain XML - Should not have problem",
						hasProblem(SCProblemsConstants.SESSION_DATABASE_MAPPING_PROJECT, session));

		// Add additional Mapping Project - XML
		ProjectAdapter project = session.addProjectClassNamed("org.eclipse.persistence.demos.employee.relational.EmployeeProject");

		assertFalse("Contain XML - Should not have problem",
						hasProblem(SCProblemsConstants.SESSION_DATABASE_MAPPING_PROJECT, session));

		// Remove additional Mapping Project - Class
		session.removeProject( project);
		session.addProjectClassNamed("org.eclipse.persistence.tools.workbench.MyClass");

		assertFalse("Contain Class - Should not have problem",
						hasProblem(SCProblemsConstants.SESSION_DATABASE_MAPPING_PROJECT, session));
	}
}
