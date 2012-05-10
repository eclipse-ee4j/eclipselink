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
package org.eclipse.persistence.tools.workbench.test.scplugin.model.write;

import java.io.File;
import java.io.IOException;
import java.util.Vector;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.internal.sessions.factories.model.log.DefaultSessionLogConfig;
import org.eclipse.persistence.internal.sessions.factories.model.login.DatabaseLoginConfig;
import org.eclipse.persistence.internal.sessions.factories.model.login.EISLoginConfig;
import org.eclipse.persistence.internal.sessions.factories.model.property.PropertyConfig;
import org.eclipse.persistence.internal.sessions.factories.model.rcm.RemoteCommandManagerConfig;
import org.eclipse.persistence.internal.sessions.factories.model.session.DatabaseSessionConfig;
import org.eclipse.persistence.internal.sessions.factories.model.session.ServerSessionConfig;
import org.eclipse.persistence.internal.sessions.factories.model.session.SessionConfig;
import org.eclipse.persistence.internal.sessions.factories.model.transport.JMSTopicTransportManagerConfig;
import org.eclipse.persistence.internal.sessions.factories.model.transport.naming.JNDINamingServiceConfig;
import org.eclipse.persistence.tools.workbench.test.scplugin.adapter.TopLinkSessionsAdapter;
import org.eclipse.persistence.tools.workbench.test.utility.TestTools;

public class DatabaseSessionWriteTest extends TestCase {

	private TopLinkSessionsAdapter topLinkSessions;
	private SessionConfig session;
	private String path;
	
	public DatabaseSessionWriteTest( String name) {
		super( name);
	}
	
	public void testBasicDatabaseSession() throws IOException {
		String name = "BasicDatabaseSessionWriteTest";
		session = new DatabaseSessionConfig();
		
		populateBasicDatabaseSession(( DatabaseSessionConfig)session, name);
		
		this.saveSession( session, name);
	}
	
	public void testDatabaseSessionWithLogin() throws IOException {
		String name = "DatabaseSessionWithLoginWriteTest";
		session = new DatabaseSessionConfig();
		
		populateBasicDatabaseSession(( DatabaseSessionConfig)session, name);
		
		this.saveSession( session, name);
	}

	public void testDatabaseSession() throws IOException {
		String name = "DatabaseSessionWriteTest";
		session = new DatabaseSessionConfig();
		
		populateBasicDatabaseSession(( DatabaseSessionConfig)session, name);
		
		this.saveSession( session, name);
	}

	public void testServerSessionConfigManager() {
		session = new ServerSessionConfig();

	}
	/**
	 * BasicDatabaseSession without Event Listener Classes.
	 */
	protected void populateBasicDatabaseSession( DatabaseSessionConfig session, String name) {

		session.setName( name);
		
		Vector projects = new Vector();
		projects.add( "myProject");

// @deprecated
//		session.setProjectClass( projects);
	}

	protected void populateDatabaseSessionWithLogin( DatabaseSessionConfig session, String name) {

		session.setName( name);
		//Login
		DatabaseLoginConfig loginConfig = new DatabaseLoginConfig();
		loginConfig.setPlatformClass("org.eclipse.persistence.OraclePlatform");
		session.setLoginConfig( loginConfig);

	}
	
	protected void populateEisServerSession( ServerSessionConfig session, String name) {

		session.setName( name);
		//Login
		EISLoginConfig loginConfig = new EISLoginConfig();
		loginConfig.setConnectionSpecClass("org.eclipse.persistence.eis.EISPlatform");
		session.setLoginConfig( loginConfig);

		// Remote command manager
		RemoteCommandManagerConfig rcmConfig = new RemoteCommandManagerConfig();
		rcmConfig.setChannel("new_channel");

		// Transport Manager
		JMSTopicTransportManagerConfig transportConfig = new JMSTopicTransportManagerConfig();
		transportConfig.setOnConnectionError("KeepConnection");
		transportConfig.setTopicHostURL("ormi://jms_topic_host");
		transportConfig.setTopicConnectionFactoryName("test-topic-connection-factory-name");
		transportConfig.setTopicName("test-topic-name");

		// Naming
		JNDINamingServiceConfig namingConfig = new JNDINamingServiceConfig();
		namingConfig.setURL("new_jndi_url");
		namingConfig.setUsername("guy");
		namingConfig.setPassword("password");
		namingConfig.setInitialContextFactoryName("new_initial_context_factory_name");
        
		Vector props = new Vector();
		PropertyConfig one = new PropertyConfig();
		one.setName("name1");
		one.setValue("value1");
		props.add(one);
		PropertyConfig two = new PropertyConfig();
		two.setName("name2");
		two.setValue("value2");
		props.add(two);
		namingConfig.setPropertyConfigs(props);

		transportConfig.setJNDINamingServiceConfig(namingConfig);    

		rcmConfig.setTransportManagerConfig(transportConfig);
		session.setRemoteCommandManagerConfig(rcmConfig);    
   
//   		//Read Pool
//		ReadConnectionPoolConfig readPoolConfig = new ReadConnectionPoolConfig();
//		readPoolConfig.setName( "ReadConnectionPool");
//		readPoolConfig.setExclusive( false);
//		readPoolConfig.setNonJTSDatasource( "nonJTSDatasource");
//		readPoolConfig.setNonJTSConnectionURL( "non_JTS_Connection");
//		
//		EISLoginConfig poolLoginConfig = new EISLoginConfig();
//		poolLoginConfig.setConnectionFactoryURL(" login_Connection_Factory");
//		readPoolConfig.setLoginConfig( poolLoginConfig);
//
//		//Write Pool
//		ConnectionPoolConfig writePoolConfig = new ConnectionPoolConfig();
//		writePoolConfig.setName( "WriteConnectionPool");
//		writePoolConfig.setLoginConfig( poolLoginConfig);
//
//		//Pools
//		PoolsConfig poolsConfig = new PoolsConfig();
//		poolsConfig.setReadConnectionPoolConfig( readPoolConfig);
//		Vector pools =  new Vector();
//		pools.add( writePoolConfig);
//		poolsConfig.setConnectionPoolConfigs( pools);
//
//		session.setPoolsConfig( poolsConfig);
	}
		
	
	protected void populateDatabaseSession( DatabaseSessionConfig dbSessionConfig, String name) {

		dbSessionConfig.setName( name);
    
		// Exception handler class
		dbSessionConfig.setExceptionHandlerClass("handlerClass");

		// External transaction controller
//		dbSessionConfig.setExternalTransactionControllerClass("externalTransactionController");

		// Log config
		DefaultSessionLogConfig logConfig = new DefaultSessionLogConfig();
		logConfig.setLogLevel("severe");
		logConfig.setFilename("logfile");
		dbSessionConfig.setLogConfig(logConfig);

		// Login
		DatabaseLoginConfig loginConfig = new DatabaseLoginConfig();
		loginConfig.setBatchWriting(true);
		loginConfig.setBindAllParameters(true);
		loginConfig.setByteArrayBinding(false);
		loginConfig.setCacheAllStatements(false);
		loginConfig.setConnectionURL("jdbc:oracle:thin:@otl-ora8infmx73:1521:toplinkj");
		loginConfig.setDriverClass("oracle.jdbc.driver.OracleDriver");
		loginConfig.setExternalConnectionPooling(false);
		loginConfig.setExternalTransactionController(false);
		loginConfig.setForceFieldNamesToUppercase(false);
		loginConfig.setJdbcBatchWriting(false);
		loginConfig.setMaxBatchWritingSize(new Integer(5));
		loginConfig.setNativeSequencing(false);
		loginConfig.setNativeSQL(false);
		loginConfig.setOptimizeDataConversion(true);
		loginConfig.setPassword("password");
		loginConfig.setPlatformClass("platform");
		loginConfig.setSequenceCounterField("SEQ_COUNT");
		loginConfig.setSequenceNameField("SEQ_NAME");
		loginConfig.setSequencePreallocationSize(new Integer(99));
		loginConfig.setSequenceTable("SEQUENCE");
		loginConfig.setStreamsForBinding(false);
		loginConfig.setStringBinding(false);
		loginConfig.setTableQualifier("table:");
		loginConfig.setTrimStrings(true);
		loginConfig.setUsername("tljtest1");
		dbSessionConfig.setLoginConfig(loginConfig);

		// Name
		dbSessionConfig.setName("EmployeeSession");

		// Profiler
		dbSessionConfig.setProfiler( "MyProfiler");

		// Project class
		Vector v = new Vector();
		v.add("org.eclipse.persistence.demos.employee.relational.EmployeeProject");
//		@deprecated
//		dbSessionConfig.setProjectClass(v);

		// Remote command manager
		RemoteCommandManagerConfig rcmConfig = new RemoteCommandManagerConfig();
		rcmConfig.setChannel("new_channel");

		// Transport Manager
		JMSTopicTransportManagerConfig transportConfig = new JMSTopicTransportManagerConfig();
		transportConfig.setOnConnectionError("KeepConnection");
		transportConfig.setTopicHostURL("ormi://jms_topic_host");
		transportConfig.setTopicConnectionFactoryName("test-topic-connection-factory-name");
		transportConfig.setTopicName("test-topic-name");

		// Naming
		JNDINamingServiceConfig namingConfig = new JNDINamingServiceConfig();
		namingConfig.setURL("new_jndi_url");
		namingConfig.setUsername("guy");
		namingConfig.setPassword("password");
		namingConfig.setInitialContextFactoryName("new_initial_context_factory_name");
        
		Vector props = new Vector();
		PropertyConfig one = new PropertyConfig();
		one.setName("name1");
		one.setValue("value1");
		props.add(one);
		PropertyConfig two = new PropertyConfig();
		two.setName("name2");
		two.setValue("value2");
		props.add(two);
		namingConfig.setPropertyConfigs(props);

		transportConfig.setJNDINamingServiceConfig(namingConfig);    

		rcmConfig.setTransportManagerConfig(transportConfig);
		dbSessionConfig.setRemoteCommandManagerConfig(rcmConfig);    
    
		// Session customizer
		dbSessionConfig.setSessionCustomizerClass("sessionCustomizer");

	}

	protected File buildFile( String name) {
		return new File( path + name + ".xml");
	}	

	public static Test suite() {
		return new TestSuite( DatabaseSessionWriteTest.class);
	}
	
	protected void setUp() throws Exception {
		
		path = "/temp/";

		topLinkSessions = new TopLinkSessionsAdapter( path, true);
	}
	
	protected void tearDown() throws Exception {
		TestTools.clear(this);
		super.tearDown();
	}

	protected void saveSession( SessionConfig session, String name) throws IOException {
		
		topLinkSessions.addSessionConfig( session);
		topLinkSessions.save( buildFile( name));
		topLinkSessions.removeSessionConfig( session);
	}
}
