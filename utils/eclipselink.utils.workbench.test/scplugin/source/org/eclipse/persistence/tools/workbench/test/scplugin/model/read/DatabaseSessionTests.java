/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.tools.workbench.test.scplugin.model.read;

import java.io.File;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.DatabaseLoginAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.DatabaseSessionAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.DefaultSessionLogAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.LogAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.LoginAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.TopLinkSessionsAdapter;
import org.eclipse.persistence.tools.workbench.test.scplugin.AllSCTests;
import org.eclipse.persistence.tools.workbench.test.utility.TestTools;
import org.eclipse.persistence.tools.workbench.utility.io.FileTools;


public class DatabaseSessionTests extends TestCase {

	private String xmlFileName;
	private File xmlFileLocation;
	private TopLinkSessionsAdapter topLinkSessions;
	private DatabaseSessionAdapter databaseSession;
	
	public DatabaseSessionTests( String name) {
		
		super( name);
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		
		xmlFileLocation = FileTools.resourceFile("/SessionsXMLTestModel/XMLSchemaSessions.xml", getClass());

//		topLinkSessions = new TopLinkSessionsAdapter( xmlFileLocation.getPath(), false);
		topLinkSessions = AllSCTests.loadSessions(xmlFileLocation);

		assertNotNull( topLinkSessions);
		
		databaseSession = getDatabaseSessionNamed( "SC-EmployeeTest");
	}
	
	protected void tearDown() throws Exception {
		TestTools.clear(this);
		super.tearDown();
	}

	public void testDatabaseSession() {
		
		validateLoadedSession( databaseSession);
		
		databaseSession.addProjectClassNamed( "org.eclipse.persistence.tools.workbench.test.models.employee.SmallProject");
		databaseSession.addProjectXmlNamed( "aProject.xml");
		// Log
		DefaultSessionLogAdapter currentLog = ( DefaultSessionLogAdapter)databaseSession.getLog();
		currentLog.setLogLevel( "warning");
		// Change the log type
		databaseSession.setJavaLogging();
		// Login
		DatabaseLoginAdapter currentLogin = ( DatabaseLoginAdapter)databaseSession.getLogin();
		currentLogin.setDatabaseDriverAsDataSource();
		currentLogin.setDataSourceName( "J2EE.Data.Source");

		validateDatabaseSession( databaseSession);
	}
	private void validateDatabaseSession( DatabaseSessionAdapter session) {
		
		assertNotNull(session.getLog());

	}
	
	private void validateLoadedSession( DatabaseSessionAdapter session) {

		LogAdapter log = session.getLog();
		String logFile = (( DefaultSessionLogAdapter)log).getFileName(); 
		assertEquals( "EmployeeSessions.log", logFile);

		assertNotNull(session.getPrimaryProjectName());
		assertNotNull(session.additionalProjectNames());
			
		LoginAdapter login = session.getLogin();
		String driverName = (( DatabaseLoginAdapter)login).getDriverClassName();
		assertEquals( "oracle.jdbc.driver.OracleDriver", driverName);
	}
	
	private DatabaseSessionAdapter getDatabaseSessionNamed( String name) {

		return ( DatabaseSessionAdapter)topLinkSessions.sessionNamed( name);
		
	}
	
	public static Test suite() {
		TestTools.setUpJUnitThreadContextClassLoader();
		return new TestSuite( DatabaseSessionTests.class);
	}
}
