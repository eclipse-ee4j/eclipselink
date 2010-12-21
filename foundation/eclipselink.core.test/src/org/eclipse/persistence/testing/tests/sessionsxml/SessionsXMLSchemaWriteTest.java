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
package org.eclipse.persistence.testing.tests.sessionsxml;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Vector;

import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLUnmarshaller;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.internal.sessions.factories.*;
import org.eclipse.persistence.internal.sessions.factories.model.SessionConfigs;
import org.eclipse.persistence.internal.sessions.factories.model.log.DefaultSessionLogConfig;
import org.eclipse.persistence.internal.sessions.factories.model.login.DatabaseLoginConfig;
import org.eclipse.persistence.internal.sessions.factories.model.platform.CustomServerPlatformConfig;
import org.eclipse.persistence.internal.sessions.factories.model.platform.ServerPlatformConfig;
import org.eclipse.persistence.internal.sessions.factories.model.pool.PoolsConfig;
import org.eclipse.persistence.internal.sessions.factories.model.pool.ReadConnectionPoolConfig;
import org.eclipse.persistence.internal.sessions.factories.model.pool.WriteConnectionPoolConfig;
import org.eclipse.persistence.internal.sessions.factories.model.project.ProjectClassConfig;
import org.eclipse.persistence.internal.sessions.factories.model.property.PropertyConfig;
import org.eclipse.persistence.internal.sessions.factories.model.rcm.RemoteCommandManagerConfig;
import org.eclipse.persistence.internal.sessions.factories.model.session.DatabaseSessionConfig;
import org.eclipse.persistence.internal.sessions.factories.model.session.ServerSessionConfig;
import org.eclipse.persistence.internal.sessions.factories.model.transport.JMSTopicTransportManagerConfig;
import org.eclipse.persistence.internal.sessions.factories.model.transport.naming.JNDINamingServiceConfig;

/**
 * Tests the OX writing portion of the XMLSessionConfig model.
 *
 * @author Guy Pelletier
 * @version 1.0
 * @date November 18, 2003
 */
public class SessionsXMLSchemaWriteTest extends AutoVerifyTestCase {
    private File m_resource;
    private DatabaseSessionConfig m_session;

    public SessionsXMLSchemaWriteTest() {
        setDescription("Test the writing portion of the session xml against the XML Schema");
    }

    public void reset() {
    }

    protected void setup() throws Exception {
        m_resource = new File("XMLSchemaWriteTest.xml");

        SessionConfigs sessions = new SessionConfigs();

        DatabaseSessionConfig dbSessionConfig = new DatabaseSessionConfig();

        // Exception handler class
        dbSessionConfig.setExceptionHandlerClass("handlerClass");

        // CustomServerPlatformConfid & External transaction controller
        CustomServerPlatformConfig platformConfig = new CustomServerPlatformConfig();
        platformConfig.setExternalTransactionControllerClass("externalTransactionController");
        dbSessionConfig.setServerPlatformConfig(platformConfig);

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
        loginConfig.setDriverClass("oracle.jdbc.OracleDriver");
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
        loginConfig.setSequenceTable("\"SEQUENCE\"");
        loginConfig.setStreamsForBinding(false);
        loginConfig.setStringBinding(false);
        loginConfig.setTableQualifier("table:");
        loginConfig.setTrimStrings(true);
        loginConfig.setUsername("tljtest1");
        dbSessionConfig.setLoginConfig(loginConfig);

        // Name
        dbSessionConfig.setName("EmployeeSession");

        // Profiler
        dbSessionConfig.setProfiler("toplink");

        // Primary project
        ProjectClassConfig projectConfig = new ProjectClassConfig();
        projectConfig.setProjectString("org.eclipse.persistence.demos.employee.relational.EmployeeProject");
        dbSessionConfig.setPrimaryProject(projectConfig);

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

        sessions.addSessionConfig(dbSessionConfig);

        ServerSessionConfig serverSessionConfig = new ServerSessionConfig();
        serverSessionConfig.setPoolsConfig(new PoolsConfig());
        ReadConnectionPoolConfig readPool = new ReadConnectionPoolConfig();
        readPool.setMaxConnections(new Integer(2));
        readPool.setMinConnections(new Integer(2));
        serverSessionConfig.getPoolsConfig().setReadConnectionPoolConfig(readPool);
        WriteConnectionPoolConfig writePool = new WriteConnectionPoolConfig();
        writePool.setMaxConnections(new Integer(10));
        writePool.setMinConnections(new Integer(5));
        serverSessionConfig.getPoolsConfig().setWriteConnectionPoolConfig(writePool);
        WriteConnectionPoolConfig userPool = new WriteConnectionPoolConfig();
        userPool.setMaxConnections(new Integer(5));
        userPool.setMinConnections(new Integer(0));
        serverSessionConfig.getPoolsConfig().addConnectionPoolConfig(userPool);
        sessions.addSessionConfig(serverSessionConfig);
        FileWriter writer = new FileWriter(m_resource);
        XMLSessionConfigWriter.write(sessions, writer);
        writer.close();
    }

    public void test() {
        try {
            FileReader reader = new FileReader(m_resource);

            // XMLSessionConfigWriter uses the latest version of XMLSessionConfigProject to write;
            // therefore the latest version of XMLSessionConfigWriter should be used for reading, too.
            XMLContext context = new XMLContext(new XMLSessionConfigProject_11_1_1());
            XMLUnmarshaller unmarshaller = context.createUnmarshaller();
            SessionConfigs eclipseLinkSessions = (SessionConfigs)unmarshaller.unmarshal(reader);
            m_session = (DatabaseSessionConfig)eclipseLinkSessions.getSessionConfigs().firstElement();
        } catch (Exception exception) {
            m_session = null;
        }
    }

    protected void verify() {
        if (m_session == null) {
            throw new TestErrorException("The session on read back was null");
        }

        // Name
        check("Name", m_session.getName(), "EmployeeSession");

        // Profiler
        check("Profiler", m_session.getProfiler(), "toplink");

        // Primary project
        if (m_session.getPrimaryProject() != null) {
            check("ProjectClass", m_session.getPrimaryProject().getProjectString(), "org.eclipse.persistence.demos.employee.relational.EmployeeProject");
        } else {
            throw new TestErrorException("ProjectClass was null");
        }

        // Exception handler class
        check("ExceptionHandlerClass", m_session.getExceptionHandlerClass(), "handlerClass");

        // guy
        // Custom server platform config
        if (m_session.getServerPlatformConfig() != null) {
            ServerPlatformConfig platformConfig = m_session.getServerPlatformConfig();
            if (platformConfig instanceof CustomServerPlatformConfig) {
                check("ExternalTransactionControllerClass", ((CustomServerPlatformConfig)platformConfig).getExternalTransactionControllerClass(), "externalTransactionController");
            } else {
                throw new TestErrorException("ServerPlatformConfig not correct type");
            }
        } else {
            throw new TestErrorException("ServerPlatformConfig was null");
        }

        // Session customizer
        check("SessionCustomizer", m_session.getSessionCustomizerClass(), "sessionCustomizer");


        // Log config
        if (m_session.getLogConfig() instanceof DefaultSessionLogConfig) {
            DefaultSessionLogConfig logConfig = (DefaultSessionLogConfig)m_session.getLogConfig();
            check("LogLevel", logConfig.getLogLevel(), "severe");
            check("Filename", logConfig.getFilename(), "logfile");
        } else {
            throw new TestErrorException("LogConfig not correct type");
        }

        // Login
        if (m_session.getLoginConfig() instanceof DatabaseLoginConfig) {
            DatabaseLoginConfig loginConfig = (DatabaseLoginConfig)m_session.getLoginConfig();
            checkBoolean("BatchWriting", loginConfig.getBatchWriting(), true);
            checkBoolean("BindAllParameters", loginConfig.getBindAllParameters(), true);
            checkBoolean("ByteArrayBinding", loginConfig.getByteArrayBinding(), false);
            checkBoolean("CacheAllStatements", loginConfig.getCacheAllStatements(), false);
            check("ConnectionURL", loginConfig.getConnectionURL(), "jdbc:oracle:thin:@otl-ora8infmx73:1521:toplinkj");
            check("DriverClass", loginConfig.getDriverClass(), "oracle.jdbc.OracleDriver");
            checkBoolean("ExternalConnectionPooling", loginConfig.getExternalConnectionPooling(), false);
            checkBoolean("ExternalTransactionController", loginConfig.getExternalTransactionController(), false);
            checkBoolean("ForceFieldNamesToUppercase", loginConfig.getForceFieldNamesToUppercase(), false);
            checkBoolean("JdbcBatchWriting", loginConfig.getJdbcBatchWriting(), false);
            check("MaxBatchWritingSize", loginConfig.getMaxBatchWritingSize(), new Integer(5));
            checkBoolean("NativeSequencing", loginConfig.getNativeSequencing(), false);
            checkBoolean("NativeSQL", loginConfig.getNativeSQL(), false);
            checkBoolean("OptimizeDataConversion", loginConfig.getOptimizeDataConversion(), true);
            check("Password", loginConfig.getPassword(), "password");
            check("PlatformClass", loginConfig.getPlatformClass(), "platform");
            check("SequenceCounterField", loginConfig.getSequenceCounterField(), "SEQ_COUNT");
            check("SequenceNameField", loginConfig.getSequenceNameField(), "SEQ_NAME");
            check("SequencePreallocationSize", loginConfig.getSequencePreallocationSize(), new Integer(99));
            check("SequenceTable", loginConfig.getSequenceTable(), "\"SEQUENCE\"");
            checkBoolean("StreamsForBinding", loginConfig.getStreamsForBinding(), false);
            checkBoolean("StringBinding", loginConfig.getStringBinding(), false);
            check("TableQualifier", loginConfig.getTableQualifier(), "table:");
            checkBoolean("TrimStrings", loginConfig.getTrimStrings(), true);
            check("Username", loginConfig.getUsername(), "tljtest1");
        } else {
            throw new TestErrorException("LogConfig not correct type");
        }

        // Remote command manager
        RemoteCommandManagerConfig rcmConfig = m_session.getRemoteCommandManagerConfig();

        if (rcmConfig == null) {
            throw new TestErrorException("RemoteCommandManagerConfig was null");
        } else {
            // Channel
            check("Channel", rcmConfig.getChannel(), "new_channel");

            if (rcmConfig.getTransportManagerConfig() instanceof JMSTopicTransportManagerConfig) {
                // Transport manager
                JMSTopicTransportManagerConfig transportConfig = (JMSTopicTransportManagerConfig)rcmConfig.getTransportManagerConfig();
                check("OnConnectionError", transportConfig.getOnConnectionError(), "KeepConnection");
                check("TopicHostURL", transportConfig.getTopicHostURL(), "ormi://jms_topic_host");
                check("TopicConnectionFactoryName", transportConfig.getTopicConnectionFactoryName(), "test-topic-connection-factory-name");
                check("TopicName", transportConfig.getTopicName(), "test-topic-name");

                // Naming
                JNDINamingServiceConfig namingConfig = transportConfig.getJNDINamingServiceConfig();

                if (namingConfig == null) {
                    throw new TestErrorException("JNDINamingServiceConfig was null");
                } else {
                    check("Naming URL", namingConfig.getURL(), "new_jndi_url");
                    check("Naming Username", namingConfig.getUsername(), "guy");
                    check("Naming Password", namingConfig.getPassword(), "password");
                    check("InitialContextFactoryName", namingConfig.getInitialContextFactoryName(), "new_initial_context_factory_name");

                    // Properties
                    Vector propertyConfigs = namingConfig.getPropertyConfigs();

                    if (propertyConfigs == null) {
                        throw new TestErrorException("PropertyConfigs were null");
                    } else if (propertyConfigs.size() != 2) {
                        throw new TestErrorException("PropertyConfigs were not the correct size");
                    } else {
                        PropertyConfig one = (PropertyConfig)propertyConfigs.firstElement();
                        check("Property name", one.getName(), "name1");
                        check("Property value", one.getValue(), "value1");

                        PropertyConfig two = (PropertyConfig)propertyConfigs.lastElement();
                        check("Property name", two.getName(), "name2");
                        check("Property value", two.getValue(), "value2");
                    }
                }
            } else {
                throw new TestErrorException("TransportManagerConfig not correct type");
            }
        }
    }

    private void checkBoolean(String name, boolean one, boolean two) {
        if (one != two) {
            throw new TestErrorException("For: " + name + ", expecting: " + one + ", found: " + two);
        }
    }

    private void check(String name, Object one, Object two) {
        if (!one.equals(two)) {
            throw new TestErrorException("For: " + name + ", expecting: " + one + ", found: " + two);
        }
    }
}
