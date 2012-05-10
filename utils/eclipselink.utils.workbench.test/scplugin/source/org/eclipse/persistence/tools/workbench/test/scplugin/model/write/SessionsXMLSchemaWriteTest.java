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

import org.eclipse.persistence.internal.sessions.factories.model.property.PropertyConfig;
import org.eclipse.persistence.internal.sessions.factories.model.rcm.RemoteCommandManagerConfig;
import org.eclipse.persistence.internal.sessions.factories.model.session.DatabaseSessionConfig;
import org.eclipse.persistence.internal.sessions.factories.model.session.SessionConfig;
import org.eclipse.persistence.internal.sessions.factories.model.transport.JMSTopicTransportManagerConfig;
import org.eclipse.persistence.internal.sessions.factories.model.transport.naming.JNDINamingServiceConfig;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.DatabaseSessionAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.RemoteCommandManagerAdapter;
import org.eclipse.persistence.tools.workbench.test.scplugin.adapter.TopLinkSessionsAdapter;
import org.eclipse.persistence.tools.workbench.test.utility.TestTools;

/**
 * Read XMLSchemaSessions.xml, add properties, and write out resulting xml.
 */
public class SessionsXMLSchemaWriteTest extends TestCase {

	private TopLinkSessionsAdapter topLinkSessions;
	private SessionConfig session;
	private String path;

	public void testSessionsXMLSchema() throws IOException {
		String name = "SC-EmployeeTest";
		this.session = this.topLinkSessions.sessionConfigNamed( name);
		
		populateSessionConfig(( DatabaseSessionConfig)this.session);
		
		this.saveSession( this.session);
	}
	
		protected void populateSessionConfig( DatabaseSessionConfig session) {

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
    
//			// Cache synchronization manager
//			CacheSynchronizationManagerConfig csmConfig = new CacheSynchronizationManagerConfig();
//
//			// Clustering service
//			csmConfig.setClusteringServiceConfig(new RMIJNDIClusteringConfig());
//    
//			csmConfig.setIsAsynchronous(true);
//			csmConfig.setJNDIPassword("password");
//			csmConfig.setJNDIUsername("username");
//			csmConfig.setNamingServiceInitialContextFactoryName("initialContextFactoryName");
//			csmConfig.setNamingServiceURL("localhost:1099");
//			csmConfig.setRemoveConnectionOnError(true);
//		
//			// Set to Cache synchronization manager
//			session.setCacheSynchronizationManagerConfig(csmConfig);


		}
		
		protected void populateSessionAdapter( DatabaseSessionAdapter session) {
		
			session.setClusteringToRemoteCommandManager();
		
			RemoteCommandManagerAdapter rcm = session.getRemoteCommandManager();
		
			rcm.setChannel( "my_Channel");
//			rcm.setCacheSync( true);
//			rcm.setRemoveConnectionOnError( true);

		}
	
	public static Test suite() {
		return new TestSuite( SessionsXMLSchemaWriteTest.class);
	}
	
	protected void setUp() throws Exception {
		
		this.path = getClass().getResource( "/SessionsXMLTestModel/XMLSchemaSessions.xml").getPath();

		this.topLinkSessions = new TopLinkSessionsAdapter( this.path, false);
	}
	
	protected File buildFile() {
		return new File( this.path.replaceFirst( ".xml", "") + "_WriteTest.xml" );
	}	

	protected void saveSession( SessionConfig session) throws IOException {
		
		this.topLinkSessions.addSessionConfig( session);
		this.topLinkSessions.save( buildFile());
		this.topLinkSessions.removeSessionConfig( session);
	}

	protected void tearDown() throws Exception {
		TestTools.clear(this);
		super.tearDown();
	}

}
