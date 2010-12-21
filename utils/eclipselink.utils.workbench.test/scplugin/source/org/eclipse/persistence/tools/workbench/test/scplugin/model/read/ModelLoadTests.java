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
package org.eclipse.persistence.tools.workbench.test.scplugin.model.read;

import java.io.File;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.internal.sessions.factories.model.log.DefaultSessionLogConfig;
import org.eclipse.persistence.internal.sessions.factories.model.log.LogConfig;
import org.eclipse.persistence.internal.sessions.factories.model.login.DatabaseLoginConfig;
import org.eclipse.persistence.internal.sessions.factories.model.login.LoginConfig;
import org.eclipse.persistence.internal.sessions.factories.model.session.DatabaseSessionConfig;
import org.eclipse.persistence.tools.workbench.test.scplugin.adapter.TopLinkSessionsAdapter;
import org.eclipse.persistence.tools.workbench.test.utility.TestTools;
import org.eclipse.persistence.tools.workbench.utility.io.FileTools;


/**
 * Load RDBMS platform Database sessions from to the model config.
 */
public class ModelLoadTests extends TestCase {

	private TopLinkSessionsAdapter topLinkSessions;
	private String path;

	public ModelLoadTests( String name) {
		super( name);
	}
	
	protected void setUp() throws Exception {

	}
	
	protected void tearDown() throws Exception {
		TestTools.clear(this);
		super.tearDown();
	}

	public void testCreatingTopLinkSessionsAdapter() throws Exception {

		// First read - DatabaseSession
		File file = FileTools.resourceFile("/SessionsXMLTestModel/XMLSchemaSessions.xml", getClass());
		path = file.getPath();

		topLinkSessions = new TopLinkSessionsAdapter( path, false);
		topLinkSessions.toString();
	
		DatabaseSessionConfig session = ( DatabaseSessionConfig)topLinkSessions.sessionConfigNamed( "SC-EmployeeTest");
		assertNotNull( session);
		
		this.validateDatabaseSession( session);

		// Second read - DatabaseSession
		path = FileTools.resourceFile("/SessionsXMLTestModel/XMLSchemaSessions.xml", getClass()).getPath();

		topLinkSessions = new TopLinkSessionsAdapter( path, false);
		topLinkSessions.toString();
	
		session = ( DatabaseSessionConfig)topLinkSessions.sessionConfigNamed( "SC-EmployeeTest");
		assertNotNull( session);
		this.validateDatabaseSession( session);
	}
		
	private void validateDatabaseSession( DatabaseSessionConfig session) {

//		assertEquals( session.getRemoteCommandManagerConfig().getCommandsConfig().getCacheSync(), false);

		LogConfig log = session.getLogConfig();
		String logFile = (( DefaultSessionLogConfig)log).getFilename(); 
		assertEquals( "EmployeeSessions.log", logFile);

		String sessionName = session.getName();
		assertEquals( "SC-EmployeeTest", sessionName);
// @deprecated
//		assertNotNull( session.getProjectClass());
//		assertNotNull( session.getProjectXML());
			
		LoginConfig login = session.getLoginConfig();
		String driverName = (( DatabaseLoginConfig)login).getDriverClass();
		assertEquals( "oracle.jdbc.driver.OracleDriver", driverName);
	}
	
	public static Test suite() {
		return new TestSuite( ModelLoadTests.class);
	}
}
