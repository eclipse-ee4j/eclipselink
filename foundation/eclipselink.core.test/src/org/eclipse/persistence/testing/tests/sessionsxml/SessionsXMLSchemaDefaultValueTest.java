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

import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.sequencing.NativeSequence;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.sessions.server.ConnectionPool;
import org.eclipse.persistence.sessions.server.ServerSession;
import org.eclipse.persistence.internal.sessions.factories.XMLSessionConfigProject;
import org.eclipse.persistence.internal.sessions.factories.XMLSessionConfigProject_11_1_1;
import org.eclipse.persistence.sessions.factories.SessionManager;
import org.eclipse.persistence.sessions.factories.XMLSessionConfigLoader;


/**
 * Tests that some of the defaulted values from the XML Schema are set properly.
 * BUG #3455771 and 3454653
 *
 * @author Guy Pelletier
 * @revisor Edwin Tang
 * @version 2.0
 * @date December 08, 2004
 */
public class SessionsXMLSchemaDefaultValueTest extends AutoVerifyTestCase {
    DatabaseSession employeeSession;
    ServerSession serverSession;

    public SessionsXMLSchemaDefaultValueTest() {
        setDescription("Test default values from the XML schema.");
    }

    public void reset() {
        if (employeeSession != null && employeeSession.isConnected()) {
            employeeSession.logout(); // If session is logged in, log it out
            SessionManager.getManager().getSessions().remove(employeeSession);
            employeeSession = null;
        }
    }

    public void test() {
        XMLSessionConfigLoader loader = new XMLSessionConfigLoader("org/eclipse/persistence/testing/models/sessionsxml/XMLSchemaSessionNoDefaultedTagsAllowed.xml");

        // don't log in the session
        employeeSession = (DatabaseSession)SessionManager.getManager().getSession(loader, "EmployeeSession", getClass().getClassLoader(), false, true); // refresh the session  
        // don't log in the session
        serverSession = (ServerSession)SessionManager.getManager().getSession(loader, "ServerSession", getClass().getClassLoader(), false, true); // refresh the session  
    }

    protected void verify() {
        if (employeeSession == null) {
            throw new TestErrorException("Employee session is null");
        }

        if (!employeeSession.getDatasourceLogin().getPlatform().getSequenceTableName().equals(XMLSessionConfigProject.SEQUENCE_TABLE_DEFAULT)) {
            throw new TestErrorException("The sequence table had the wrong default value");
        }

        if (!employeeSession.getDatasourceLogin().getPlatform().getSequenceCounterFieldName().equals(XMLSessionConfigProject.SEQUENCE_COUNTER_FIELD_DEFAULT)) {
            throw new TestErrorException("The sequence counter field name had the wrong default value");
        }

        if (!employeeSession.getDatasourceLogin().getPlatform().getSequenceNameFieldName().equals(XMLSessionConfigProject.SEQUENCE_NAME_FIELD_DEFAULT)) {
            throw new TestErrorException("The sequence name field name had the wrong default value");
        }

        // We could check a few others. More can be added later.
        // Most defaults were verified in the debugger at runtime.
        // The three above were explicitely tested since they were the cause of
        // error for BUG 3454653
        if (employeeSession.getDatasourceLogin().getPlatform().getSequencePreallocationSize() != XMLSessionConfigProject.SEQUENCE_PREALLOCATION_SIZE_DEFAULT) {
            throw new TestErrorException("The sequence preallocation size had the wrong default value");
        }

        if ((employeeSession.getDatasourceLogin().getPlatform().getDefaultSequence() instanceof NativeSequence) != XMLSessionConfigProject.NATIVE_SEQUENCING_DEFAULT) {
            throw new TestErrorException("usesNativeSequencing status did not match the default setting");
        }
        if (employeeSession.getDatasourceLogin().getPlatform().getMaxBatchWritingSize() != XMLSessionConfigProject.MAX_BATCH_WRITING_SIZE_DEFAULT) {
            throw new TestErrorException("The max batch writing had the wrong default value");
        }
        if (employeeSession.getDatasourceLogin().getPlatform().usesNativeSQL() != XMLSessionConfigProject.NATIVE_SQL_DEFAULT) {
            throw new TestErrorException("usesNativeSQL status did not match the default setting");
        }
        if (employeeSession.getDatasourceLogin().getPlatform().usesBatchWriting() != XMLSessionConfigProject.BATCH_WRITING_DEFAULT) {
            throw new TestErrorException("usesBatchWriting status did not match the default setting");
        }
        if (employeeSession.getDatasourceLogin().getPlatform().usesJDBCBatchWriting() != XMLSessionConfigProject.JDBC20_BATCH_WRITING_DEFAULT) {
            throw new TestErrorException("usesJDBCBatchWriting status did not match the default setting");
        }
        if (employeeSession.getDatasourceLogin().getPlatform().usesNativeSQL() != XMLSessionConfigProject.NATIVE_SQL_DEFAULT) {
            throw new TestErrorException("usesNativeSQL status did not match the default setting");
        }
        if (employeeSession.getDatasourceLogin().getPlatform().usesStringBinding() != XMLSessionConfigProject.STRING_BINDING_DEFAULT) {
            throw new TestErrorException("usesStringBinding status did not match the default setting");
        }
        if (employeeSession.getDatasourceLogin().getPlatform().usesStreamsForBinding() != XMLSessionConfigProject.STREAMS_FOR_BINDING_DEFAULT) {
            throw new TestErrorException("usesStreamsForBinding status did not match the default setting");
        }
        if (employeeSession.getDatasourceLogin().getPlatform().usesByteArrayBinding() != XMLSessionConfigProject.BYTE_ARRAY_BINDING_DEFAULT) {
            throw new TestErrorException("usesByteArrayBinding status did not match the default setting");
        }
        if (employeeSession.getDatasourceLogin().getPlatform().shouldBindAllParameters() != XMLSessionConfigProject_11_1_1.BIND_ALL_PARAMETERS_DEFAULT) {
            throw new TestErrorException("shouldBindAllParameters status did not match the default setting");
        }
        if (employeeSession.getDatasourceLogin().getPlatform().shouldCacheAllStatements() != XMLSessionConfigProject.CACHE_ALL_STATEMENTS_DEFAULT) {
            throw new TestErrorException("shouldCacheAllStatements status did not match the default setting");
        }
        if (employeeSession.getDatasourceLogin().getPlatform().shouldForceFieldNamesToUpperCase() != XMLSessionConfigProject.FORCE_FIELD_NAMES_TO_UPPERCASE_DEFAULT) {
            throw new TestErrorException("shouldForceFieldNamesToUpperCase status did not match the default setting");
        }
        if (employeeSession.getDatasourceLogin().getPlatform().shouldTrimStrings() != XMLSessionConfigProject.TRIM_STRINGS_DEFAULT) {
            throw new TestErrorException("shouldTrimStrings status did not match the default setting");
        }
        if (employeeSession.getDatasourceLogin().getPlatform().shouldOptimizeDataConversion() != XMLSessionConfigProject.OPTIMIZE_DATA_CONVERSION_DEFAULT) {
            throw new TestErrorException("shouldOptimizeDataConversion status did not match the default setting");
        }

        if (employeeSession.getDatasourceLogin().shouldUseExternalConnectionPooling() != XMLSessionConfigProject.EXTERNAL_CONNECTION_POOL_DEFAULT) {
            throw new TestErrorException("shouldUseExternalConnectionPooling status did not match the default setting");
        }
        if (employeeSession.getDatasourceLogin().shouldUseExternalTransactionController() != XMLSessionConfigProject.EXTERNAL_TRANSACTION_CONTROLLER_DEFAULT) {
            throw new TestErrorException("shouldUseExternalTransactionController status did not match the default setting");
        }

        String logLevel = null;
        switch (employeeSession.getLogLevel()) {
        case (SessionLog.OFF):
            {
                logLevel = "off";
                break;
            }
        case (SessionLog.SEVERE):
            {
                logLevel = "severe";
                break;
            }
        case (SessionLog.WARNING):
            {
                logLevel = "warning";
                break;
            }
        case (SessionLog.INFO):
            {
                logLevel = "info";
                break;
            }
        case (SessionLog.CONFIG):
            logLevel = "config";
            {
                logLevel = "config";
                break;
            }
        case (SessionLog.FINE):
            logLevel = "fine";
            {
                logLevel = "fine";
                break;
            }
        case (SessionLog.FINER):
            logLevel = "finer";
            {
                logLevel = "finer";
                break;
            }
        case (SessionLog.FINEST):
            logLevel = "finest";
            {
                logLevel = "finest";
                break;
            }
        case (SessionLog.ALL):
            logLevel = "all";
            {
                logLevel = "all";
                break;
            }
        }
        if (!logLevel.equals(XMLSessionConfigProject.LOG_LEVEL_DEFAULT)) {
            throw new TestErrorException("The log level had the wrong default value");
        }


        if (serverSession == null) {
            throw new TestErrorException("Server session is null");
        }

        if (serverSession.getCommandManager().shouldPropagateAsynchronously() != XMLSessionConfigProject.IS_ASYNCHRONOUS_DEFAULT) {
            throw new TestErrorException("The initial context factory name had the wrong default value");
        }
        if (!serverSession.getCommandManager().getChannel().equals(XMLSessionConfigProject.CHANNEL_DEFAULT)) {
            throw new TestErrorException("The channel the wrong default value");
        }

        if (!serverSession.getCommandManager().getTransportManager().getUserName().equals(XMLSessionConfigProject.USERNAME_DEFAULT)) {
            throw new TestErrorException("The user name had the wrong default value");
        }
        if (!serverSession.getCommandManager().getTransportManager().getPassword().equals(XMLSessionConfigProject.PASSWORD_DEFAULT)) {
            throw new TestErrorException("The password had the wrong default value");
        }
        if (!serverSession.getCommandManager().getTransportManager().getInitialContextFactoryName().equals(XMLSessionConfigProject.INITIAL_CONTEXT_FACTORY_NAME_DEFAULT)) {
            throw new TestErrorException("The initial context factory name had the wrong default value");
        }
        if (serverSession.getCommandManager().getTransportManager().DEFAULT_REMOVE_CONNECTION_ON_ERROR_MODE != XMLSessionConfigProject.REMOVE_CONNECTION_ON_ERROR_DEFAULT) {
            throw new TestErrorException("The DEFAULT_REMOVE_CONNECTION_ON_ERROR_MODE had the wrong default value");
        }

        if (!serverSession.getCommandManager().getDiscoveryManager().DEFAULT_MULTICAST_GROUP.equals(XMLSessionConfigProject.MULTICAST_GROUP_ADDRESS_DEFAULT)) {
            throw new TestErrorException("The multicast group address had the wrong default value");
        }
        if (serverSession.getCommandManager().getDiscoveryManager().DEFAULT_MULTICAST_PORT != XMLSessionConfigProject.MULTICAST_PORT_DEFAULT) {
            throw new TestErrorException("The multicast port had the wrong default value");
        }
        if (serverSession.getCommandManager().getDiscoveryManager().DEFAULT_ANNOUNCEMENT_DELAY != XMLSessionConfigProject.ANNOUNCEMENT_DELAY_DEFAULT) {
            throw new TestErrorException("The announcement delay had the wrong default value");
        }
        if (serverSession.getCommandManager().getDiscoveryManager().DEFAULT_PACKET_TIME_TO_LIVE != XMLSessionConfigProject.PACKET_TIME_TO_LIVE_DEFAULT) {
            throw new TestErrorException("The packet time to live had the wrong default value");
        }
        /*
		if (serverSession.getServerPlatform().isRuntimeServicesEnabled() != XMLSessionConfigProject.ENABLE_RUNTIME_SERVICES_DEFAULT)
		{
      throw new TestErrorException("isRuntimeServicesEnabled did not match the default setting");
		}
		if (serverSession.getServerPlatform().isJTAEnabled() != XMLSessionConfigProject.ENABLE_JTA_DEFAULT)
		{
      throw new TestErrorException("isJTAEnabled did not match the default setting");
		}
		*/
        ConnectionPool readConnPool = serverSession.getConnectionPool("ReadConnectionPool");
        ConnectionPool writeConnPool = serverSession.getConnectionPool("WriteConnectionPool");
        ConnectionPool seqConnPool = serverSession.getConnectionPool("SequenceConnectionPool");
        /*
		if((readConnPool == null) || (writeConnPool == null) || (seqConnPool == null))
		{
      throw new TestErrorException("Connection Pools were not created correctly");
		}
		if((readConnPool.getMaxNumberOfConnections()!=XMLSessionConfigProject.READ_CONNECTION_POOL_MAX_DEFAULT) ||
			(readConnPool.getMinNumberOfConnections()!=XMLSessionConfigProject.READ_CONNECTION_POOL_MIN_DEFAULT) ||
			(writeConnPool.getMaxNumberOfConnections()!=XMLSessionConfigProject.CONNECTION_POOL_MAX_DEFAULT) ||
			(writeConnPool.getMinNumberOfConnections()!=XMLSessionConfigProject.CONNECTION_POOL_MIN_DEFAULT) ||
			(seqConnPool.getMaxNumberOfConnections()!=XMLSessionConfigProject.CONNECTION_POOL_MAX_DEFAULT) ||
			(seqConnPool.getMinNumberOfConnections()!=XMLSessionConfigProject.CONNECTION_POOL_MIN_DEFAULT))
		{
      throw new TestErrorException("Connection Pools had the wrong default sizes");
		}
		*/
    }
}
