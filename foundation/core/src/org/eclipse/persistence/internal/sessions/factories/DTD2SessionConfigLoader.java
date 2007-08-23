/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.sessions.factories;

import org.w3c.dom.*;

import java.security.AccessController;
import java.util.Vector;
import java.lang.reflect.*;
import org.eclipse.persistence.exceptions.SessionLoaderException;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedGetMethod;
import org.eclipse.persistence.internal.security.PrivilegedMethodInvoker;
import org.eclipse.persistence.internal.sessions.factories.model.*;
import org.eclipse.persistence.internal.sessions.factories.model.csm.*;
import org.eclipse.persistence.internal.sessions.factories.model.rcm.*;
import org.eclipse.persistence.internal.sessions.factories.model.rcm.command.*;
import org.eclipse.persistence.internal.sessions.factories.model.log.*;
import org.eclipse.persistence.internal.sessions.factories.model.pool.*;
import org.eclipse.persistence.internal.sessions.factories.model.login.*;
import org.eclipse.persistence.internal.sessions.factories.model.event.*;
import org.eclipse.persistence.internal.sessions.factories.model.project.*;
import org.eclipse.persistence.internal.sessions.factories.model.session.*;
import org.eclipse.persistence.internal.sessions.factories.model.platform.*;
import org.eclipse.persistence.internal.sessions.factories.model.property.*;
import org.eclipse.persistence.internal.sessions.factories.model.clustering.*;
import org.eclipse.persistence.internal.sessions.factories.model.transport.*;
import org.eclipse.persistence.internal.sessions.factories.model.transport.discovery.*;
import org.eclipse.persistence.internal.sessions.factories.model.transport.naming.*;

/**
 * INTERNAL:
 *
 * <p><b>Purpose</b>: Provide a mechanism for loading configuration XML files,
 * as classpath resources, into a model. These provides backwards
 * compatibility so that the MW may display 9.0.4 session xml files.
 *
 * Processing is done reflectively based on the name of the XML Tag
 *
 * @author Guy Pelletier
 */
public class DTD2SessionConfigLoader {
    private String m_onConnectionError;
    private boolean m_useSequenceConnectionPool;
    private String m_nonJTSConnectionURL;
    private String m_nonJTSDatasource;

    /**
     * INTERNAL:
     */
    public DTD2SessionConfigLoader() {
        m_onConnectionError = null;
        m_useSequenceConnectionPool = false;
        m_nonJTSConnectionURL = null;
        m_nonJTSDatasource = null;
    }

    /**
     * INTERNAL:
     */
    public String convertNodeToMethodName(Node node) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("process_");
        buffer.append(node.getNodeName().replace('-', '_'));
        buffer.append("_Tag");
        return buffer.toString();
    }

    /**
     * INTERNAL:
     */
    public SessionConfigs load(Document document) {
        SessionConfigs eclipseLinkSessions = new SessionConfigs();

        String publicId = document.getDoctype().getPublicId();
        String version = "unknown";

        if (publicId.equals(PersistenceEntityResolver.doctTypeId40)) {
            version = "4.0";
        } else if (publicId.equals(PersistenceEntityResolver.doctTypeId45)) {
            version = "4.5";
        } else if (publicId.equals(WASPersistenceEntityResolver.docTypeId_903)) {
            version = "9.0.3";
        } else if (publicId.equals(PersistenceEntityResolver.doctTypeId904) || publicId.equals(WASPersistenceEntityResolver.docTypeId_904)) {
            version = "9.0.4";
        }

        eclipseLinkSessions.setVersion(version);

        NodeListElementEnumerator list = new NodeListElementEnumerator(document.getChildNodes());
        process_toplink_configuration_Tag(list.nextNode(), eclipseLinkSessions);

        return eclipseLinkSessions;
    }

    /**
     * INTERNAL:
     */
    protected void process_toplink_configuration_Tag(Node node, SessionConfigs tlSessions) {
        ObjectHolder sessionConfigHolder = new ObjectHolder();
        NodeListElementEnumerator list = new NodeListElementEnumerator(node.getChildNodes());

        while (list.hasMoreNodes()) {
            Node childNode = list.nextNode();

            try {
                Class[] args = { Node.class, ObjectHolder.class };
                if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                    Method method = (Method)AccessController.doPrivileged(new PrivilegedGetMethod(getClass(), convertNodeToMethodName(childNode), args, false));
                    Object[] objectList = { childNode, sessionConfigHolder };
                    AccessController.doPrivileged(new PrivilegedMethodInvoker(method, this, objectList));
                }else{
                    Method method = PrivilegedAccessHelper.getMethod(getClass(), convertNodeToMethodName(childNode), args, false);
                    Object[] objectList = { childNode, sessionConfigHolder };
                    method.invoke(this, objectList);
                    
                }
            } catch (Exception exception) {
                throw SessionLoaderException.unkownTagAtNode(childNode.getNodeName(), node.getNodeName(), exception);
            }

            tlSessions.addSessionConfig((SessionConfig)sessionConfigHolder.getObject());
        }
    }

    /**
    * INTERNAL:
    */
    public void process_cache_synchronization_manager_Tag(Node node, ObjectHolder holder) {
        ObjectHolder csmHolder = new ObjectHolder();
        csmHolder.setObject(new CacheSynchronizationManagerConfig());
        processChildrenNodes(node, csmHolder);

        ((SessionConfig)holder.getObject()).setCacheSynchronizationManagerConfig((CacheSynchronizationManagerConfig)csmHolder.getObject());
    }

    /**
     * INTERNAL:
     */
    public void process_connection_pool_Tag(Node node, ObjectHolder holder) {
        ServerSessionConfig serverSessionConfig = (ServerSessionConfig)holder.getObject();

        if (serverSessionConfig.getPoolsConfig() == null) {
            serverSessionConfig.setPoolsConfig(new PoolsConfig());
        }

        ObjectHolder poolHolder = new ObjectHolder();
        NodeListElementEnumerator list = new NodeListElementEnumerator(node.getChildNodes());

        boolean isReadConnectionPool = false;

        // process the is_read_connection_pool tag
        if (Boolean.valueOf(list.nextNode().getFirstChild().getNodeValue()).booleanValue()) {
            isReadConnectionPool = true;

            // Read connection pool may have already been initialized in process_login_Tag
            ReadConnectionPoolConfig readPool = serverSessionConfig.getPoolsConfig().getReadConnectionPoolConfig();
            if (readPool == null) {
                poolHolder.setObject(new ReadConnectionPoolConfig());
            } else {
                poolHolder.setObject(readPool);
            }
        } else {
            poolHolder.setObject(new ConnectionPoolConfig());
        }

        processChildrenNodes(list, node, poolHolder);

        // finally set it
        if (isReadConnectionPool) {
            serverSessionConfig.getPoolsConfig().setReadConnectionPoolConfig((ReadConnectionPoolConfig)poolHolder.getObject());
        } else {
            serverSessionConfig.getPoolsConfig().addConnectionPoolConfig((ConnectionPoolConfig)poolHolder.getObject());
        }
    }

    /**
     * INTERNAL:
     */
    public ConnectionPoolConfig process_is_read_connection_pool_Tag(Node node, ObjectHolder holder) {
        ConnectionPoolConfig poolConfig;

        if (Boolean.valueOf(node.getFirstChild().getNodeValue()).booleanValue()) {
            poolConfig = new ReadConnectionPoolConfig();
            ((PoolsConfig)holder.getObject()).setReadConnectionPoolConfig((ReadConnectionPoolConfig)poolConfig);
        } else {
            poolConfig = new ConnectionPoolConfig();
            ((PoolsConfig)holder.getObject()).getConnectionPoolConfigs().add(poolConfig);
        }

        return poolConfig;
    }

    /**
    * INTERNAL:
    */
    public void process_max_connections_Tag(Node node, ObjectHolder holder) {
        ((ConnectionPoolConfig)holder.getObject()).setMaxConnections(Integer.valueOf(node.getFirstChild().getNodeValue()));
    }

    /**
     * INTERNAL:
     */
    public void process_min_connections_Tag(Node node, ObjectHolder holder) {
        ((ConnectionPoolConfig)holder.getObject()).setMinConnections(Integer.valueOf(node.getFirstChild().getNodeValue()));
    }

    /**
     * INTERNAL:
     */
    public void process_login_Tag(Node node, ObjectHolder holder) {
        Object object = holder.getObject();

        ObjectHolder loginConfigHolder = new ObjectHolder();

        // Connection pool config may already have a database login config
        if (object instanceof ConnectionPoolConfig && (((ConnectionPoolConfig)object).getLoginConfig() != null)) {
            loginConfigHolder.setObject(((ConnectionPoolConfig)object).getLoginConfig());
        } else {
            loginConfigHolder.setObject(new DatabaseLoginConfig());
        }

        processChildrenNodes(node, loginConfigHolder);

        if (object instanceof DatabaseSessionConfig) {
            ((DatabaseSessionConfig)object).setLoginConfig((LoginConfig)loginConfigHolder.getObject());

            if (object instanceof ServerSessionConfig) {
                ServerSessionConfig serverSessionConfig = (ServerSessionConfig)object;

                if (m_useSequenceConnectionPool) {
                    serverSessionConfig.setPoolsConfig(new PoolsConfig());
                    serverSessionConfig.getPoolsConfig().setSequenceConnectionPoolConfig(new ConnectionPoolConfig());
                    m_useSequenceConnectionPool = false;// blank it out, that way the next session doesn't pick it up
                }

                if ((m_nonJTSConnectionURL != null) || (m_nonJTSDatasource != null)) {
                    // Check if we hit the above creation of the pools config
                    if (serverSessionConfig.getPoolsConfig() == null) {
                        serverSessionConfig.setPoolsConfig(new PoolsConfig());
                    }

                    // Create a new login config to be set on a ReadConnectionPoolConfig
                    DatabaseLoginConfig loginConfig = new DatabaseLoginConfig();

                    if (m_nonJTSDatasource != null) {
                        loginConfig.setDatasource(m_nonJTSDatasource);
                        loginConfig.setExternalTransactionController(false);
                    } else {
                        loginConfig.setConnectionURL(m_nonJTSConnectionURL);
                    }

                    ReadConnectionPoolConfig readPool = new ReadConnectionPoolConfig();
                    readPool.setLoginConfig(loginConfig);
                    serverSessionConfig.getPoolsConfig().setReadConnectionPoolConfig(readPool);
                }
            }
        } else if (object instanceof ConnectionPoolConfig) {
            ((ConnectionPoolConfig)object).setLoginConfig((LoginConfig)loginConfigHolder.getObject());
        }

        // Blank out the flag values to ensure they are not picked up by
        // consecutive sessions
        m_useSequenceConnectionPool = false;
        m_nonJTSDatasource = null;
        m_nonJTSConnectionURL = null;
    }

    /**
     * INTERNAL:
     */
    public void process_license_path_Tag(Node node, ObjectHolder holder) {
    }

    /**
     * INTERNAL:
     */
    public void process_datasource_Tag(Node node, ObjectHolder holder) {
        ((DatabaseLoginConfig)holder.getObject()).setDatasource(node.getFirstChild().getNodeValue());
    }

    /**
     * INTERNAL:
     */
    public void process_session_broker_Tag(Node node, ObjectHolder holder) {
        holder.setObject(new SessionBrokerConfig());
        processChildrenNodes(node, holder);
    }

    /**
     * INTERNAL:
     */
    public void process_name_Tag(Node node, ObjectHolder holder) {
        Object object = holder.getObject();
        String name = node.getFirstChild().getNodeValue();

        if (object instanceof ConnectionPoolConfig) {
            ((ConnectionPoolConfig)object).setName(name);
        } else if (object instanceof SessionBrokerConfig) {
            ((SessionBrokerConfig)object).setName(name);
        }
    }

    /**
     * INTERNAL:
     */
    public void process_session_Tag(Node node, ObjectHolder holder) {
        NodeListElementEnumerator list = new NodeListElementEnumerator(node.getChildNodes());

        // Store the session name
        String sessionName = list.nextNode().getFirstChild().getNodeValue();

        // Store the project node
        Node projectNode = list.nextNode();
        String projectNodeName = projectNode.getNodeName();
        ProjectConfig projectConfig;

        if (projectNodeName.equals("project-xml")) {
            projectConfig = new ProjectXMLConfig();
        } else {
            projectConfig = new ProjectClassConfig();
        }

        projectConfig.setProjectString(projectNode.getFirstChild().getNodeValue());

        // Process the session type
        String sessionType = (new NodeListElementEnumerator(list.nextNode().getChildNodes())).nextNode().getNodeName();
        SessionConfig sessionConfig = (sessionType.equals("database-session")) ? new DatabaseSessionConfig() : new ServerSessionConfig();

        // Set the stored name
        ((DatabaseSessionConfig)sessionConfig).setName(sessionName);

        // Set the stored project config
        ((DatabaseSessionConfig)sessionConfig).setPrimaryProject(projectConfig);

        // Set it on the holder
        holder.setObject(sessionConfig);

        // Process the remainder of the tags
        processChildrenNodes(list, node, holder);
    }

    /**
     * INTERNAL:
     */
    public void process_uses_byte_array_binding_Tag(Node node, ObjectHolder holder) {
        ((DatabaseLoginConfig)holder.getObject()).setByteArrayBinding(Boolean.valueOf(node.getFirstChild().getNodeValue()).booleanValue());
    }

    /**
     * INTERNAL:
     */
    public void process_uses_string_binding_Tag(Node node, ObjectHolder holder) {
        ((DatabaseLoginConfig)holder.getObject()).setStringBinding(Boolean.valueOf(node.getFirstChild().getNodeValue()).booleanValue());
    }

    /**
     * INTERNAL:
     */
    public void process_uses_streams_for_binding_Tag(Node node, ObjectHolder holder) {
        ((DatabaseLoginConfig)holder.getObject()).setStreamsForBinding(Boolean.valueOf(node.getFirstChild().getNodeValue()).booleanValue());
    }

    /**
     * INTERNAL:
     */
    public void process_should_force_field_names_to_uppercase_Tag(Node node, ObjectHolder holder) {
        ((DatabaseLoginConfig)holder.getObject()).setForceFieldNamesToUppercase(Boolean.valueOf(node.getFirstChild().getNodeValue()).booleanValue());
    }

    /**
     * INTERNAL:
     */
    public void process_should_optimize_data_conversion_Tag(Node node, ObjectHolder holder) {
        ((DatabaseLoginConfig)holder.getObject()).setOptimizeDataConversion(Boolean.valueOf(node.getFirstChild().getNodeValue()).booleanValue());
    }

    /**
     * INTERNAL:
     */
    public void process_uses_native_sql_Tag(Node node, ObjectHolder holder) {
        ((DatabaseLoginConfig)holder.getObject()).setNativeSQL(Boolean.valueOf(node.getFirstChild().getNodeValue()).booleanValue());
    }

    /**
     * INTERNAL:
     */
    public void process_should_trim_strings_Tag(Node node, ObjectHolder holder) {
        ((DatabaseLoginConfig)holder.getObject()).setTrimStrings(Boolean.valueOf(node.getFirstChild().getNodeValue()).booleanValue());
    }

    /**
     * INTERNAL:
     */
    public void process_uses_batch_writing_Tag(Node node, ObjectHolder holder) {
        ((DatabaseLoginConfig)holder.getObject()).setBatchWriting(Boolean.valueOf(node.getFirstChild().getNodeValue()).booleanValue());
    }

    /**
     * INTERNAL:
     */
    public void process_uses_jdbc20_batch_writing_Tag(Node node, ObjectHolder holder) {
        ((DatabaseLoginConfig)holder.getObject()).setJdbcBatchWriting(Boolean.valueOf(node.getFirstChild().getNodeValue()).booleanValue());
    }

    /**
     * INTERNAL:
     */
    public void process_uses_external_transaction_controller_Tag(Node node, ObjectHolder holder) {
        ((DatabaseLoginConfig)holder.getObject()).setExternalTransactionController(Boolean.valueOf(node.getFirstChild().getNodeValue()).booleanValue());
    }

    /**
     * INTERNAL:
     */
    public void process_uses_external_connection_pool_Tag(Node node, ObjectHolder holder) {
        ((DatabaseLoginConfig)holder.getObject()).setExternalConnectionPooling(Boolean.valueOf(node.getFirstChild().getNodeValue()).booleanValue());
    }

    /**
     * INTERNAL:
     */
    public void process_should_cache_all_statements_Tag(Node node, ObjectHolder holder) {
        ((DatabaseLoginConfig)holder.getObject()).setCacheAllStatements(Boolean.valueOf(node.getFirstChild().getNodeValue()).booleanValue());
    }

    /**
     * INTERNAL:
     */
    public void process_should_bind_all_parameters_Tag(Node node, ObjectHolder holder) {
        ((DatabaseLoginConfig)holder.getObject()).setBindAllParameters(Boolean.valueOf(node.getFirstChild().getNodeValue()).booleanValue());
    }

    /**
     * INTERNAL:
     */
    public void process_connection_spec_class_Tag(Node node, ObjectHolder holder) {
        ((EISLoginConfig)holder.getObject()).setConnectionSpecClass(node.getFirstChild().getNodeValue());
    }

    /**
     * INTERNAL:
     */
    public void process_connection_factory_url_Tag(Node node, ObjectHolder holder) {
        ((EISLoginConfig)holder.getObject()).setConnectionFactoryURL(node.getFirstChild().getNodeValue());
    }

    /**
     * INTERNAL:
     */
    public void process_uses_native_sequencing_Tag(Node node, ObjectHolder holder) {
        ((DatabaseLoginConfig)holder.getObject()).setNativeSequencing(Boolean.valueOf(node.getFirstChild().getNodeValue()).booleanValue());
    }

    /**
     * INTERNAL:
     */
    public void process_enable_logging_Tag(Node node, ObjectHolder holder) {
        if (Boolean.valueOf(node.getFirstChild().getNodeValue()).booleanValue()) {
            DefaultSessionLogConfig logConfig = new DefaultSessionLogConfig();
            logConfig.setLogLevel("finer");
            ((SessionConfig)holder.getObject()).setLogConfig(logConfig);
        }
    }

    /**
     * INTERNAL:
     */
    public void process_driver_class_Tag(Node node, ObjectHolder holder) {
        ((DatabaseLoginConfig)holder.getObject()).setDriverClass(node.getFirstChild().getNodeValue());
    }

    /**
     * INTERNAL:
     */
    public void process_event_listener_class_Tag(Node node, ObjectHolder holder) {
        SessionConfig sessionConfig = (SessionConfig)holder.getObject();

        if (sessionConfig.getSessionEventManagerConfig() == null) {
            sessionConfig.setSessionEventManagerConfig(new SessionEventManagerConfig());
        }

        sessionConfig.getSessionEventManagerConfig().addSessionEventListener(node.getFirstChild().getNodeValue());
    }

    /**
     * INTERNAL:
     */
    public void process_user_name_Tag(Node node, ObjectHolder holder) {
        Object object = holder.getObject();
        String username = node.getFirstChild().getNodeValue();

        if (object instanceof LoginConfig) {
            ((LoginConfig)object).setUsername(username);
        } else if (object instanceof JNDINamingServiceConfig) {
            ((JNDINamingServiceConfig)object).setUsername(username);
        }
    }

    /**
     * INTERNAL:
     */
    public void process_password_Tag(Node node, ObjectHolder holder) {
        Object object = holder.getObject();
        String password = node.getFirstChild().getNodeValue();

        if (object instanceof LoginConfig) {
            // Always assume the password is encrypted.
            ((LoginConfig)object).setEncryptedPassword(password);
        } else if (object instanceof JNDINamingServiceConfig) {
            // Always assume the password is encrypted
            ((JNDINamingServiceConfig)object).setEncryptedPassword(password);
        }
    }

    /**
     * INTERNAL:
     */
    public void process_encrypted_password_Tag(Node node, ObjectHolder holder) {
        process_password_Tag(node, holder);
    }

    /**
     * INTERNAL:
     */
    public void process_encryption_class_name_Tag(Node node, ObjectHolder holder) {
        Object object = holder.getObject();
        String encryptionClass = node.getFirstChild().getNodeValue();

        if (object instanceof LoginConfig) {
            ((LoginConfig)object).setEncryptionClass(encryptionClass);
        } else if (object instanceof JNDINamingServiceConfig) {
            ((JNDINamingServiceConfig)object).setEncryptionClass(encryptionClass);
        }
    }

    /**
     * INTERNAL:
     */
    public void process_sequence_counter_field_Tag(Node node, ObjectHolder holder) {
        ((DatabaseLoginConfig)holder.getObject()).setSequenceCounterField(node.getFirstChild().getNodeValue());
    }

    /**
     * INTERNAL:
     */
    public void process_sequence_name_field_Tag(Node node, ObjectHolder holder) {
        ((DatabaseLoginConfig)holder.getObject()).setSequenceNameField(node.getFirstChild().getNodeValue());
    }

    /**
     * INTERNAL:
     */
    public void process_sequence_table_Tag(Node node, ObjectHolder holder) {
        ((DatabaseLoginConfig)holder.getObject()).setSequenceTable(node.getFirstChild().getNodeValue());
    }

    /**
     * INTERNAL:
     */
    public void process_connection_url_Tag(Node node, ObjectHolder holder) {
        ((DatabaseLoginConfig)holder.getObject()).setConnectionURL(node.getFirstChild().getNodeValue());
    }

    /**
     * INTERNAL:
     */
    public void process_non_jts_connection_url_Tag(Node node, ObjectHolder holder) {
        m_nonJTSConnectionURL = node.getFirstChild().getNodeValue();
    }

    /**
     * INTERNAL:
     */
    public void process_non_jts_datasource_Tag(Node node, ObjectHolder holder) {
        m_nonJTSDatasource = node.getFirstChild().getNodeValue();
    }

    /**
     * INTERNAL:
     * Assume we are dealing with a ServerSession at this point
     */
    public void process_uses_sequence_connection_pool_Tag(Node node, ObjectHolder holder) {
        // The holder here will hold a loginConfig. Set a flag and once the login
        // is done processing its children, check the flag and set it on the
        // sequenceConnectionPoolConfig on the ServerSessionConfig if true.
        m_useSequenceConnectionPool = true;
    }

    /**
     * INTERNAL:
     */
    public void process_session_name_Tag(Node node, ObjectHolder holder) {
        ((SessionBrokerConfig)holder.getObject()).addSessionName(node.getFirstChild().getNodeValue());
    }

    /**
     * INTERNAL:
     */
    public void process_is_asynchronous_Tag(Node node, ObjectHolder holder) {
        ((CacheSynchronizationManagerConfig)holder.getObject()).setIsAsynchronous(true);
    }

    /**
     * INTERNAL:
     */
    public void process_multicast_group_address_Tag(Node node, ObjectHolder holder) {
        Object object = holder.getObject();
        String multicastGroupAddress = node.getFirstChild().getNodeValue();

        if (object instanceof CacheSynchronizationManagerConfig) {
            CacheSynchronizationManagerConfig csmConfig = (CacheSynchronizationManagerConfig)object;
            csmConfig.getClusteringServiceConfig().setMulticastGroupAddress(multicastGroupAddress);
        } else if (object instanceof DiscoveryConfig) {
            ((DiscoveryConfig)object).setMulticastGroupAddress(multicastGroupAddress);
        }
    }

    /**
     * INTERNAL:
     */
    public void process_should_remove_connection_on_error_Tag(Node node, ObjectHolder holder) {
        boolean value = Boolean.valueOf(node.getFirstChild().getNodeValue()).booleanValue();
        ((CacheSynchronizationManagerConfig)holder.getObject()).setRemoveConnectionOnError(value);
    }

    /**
     * INTERNAL:
     */
    public void process_jms_topic_Tag(Node node, ObjectHolder holder) {
        JMSTopicTransportManagerConfig transportConfig = new JMSTopicTransportManagerConfig();
        transportConfig.setOnConnectionError(m_onConnectionError);
        m_onConnectionError = null;// blank it out
        holder.setObject(transportConfig);
        processChildrenNodes(node, holder);
    }

    /**
     * INTERNAL:
     */
    public void process_jms_topic_connection_factory_name_Tag(Node node, ObjectHolder holder) {
        Object object = holder.getObject();
        String connectionFactoryName = node.getFirstChild().getNodeValue();

        if (object instanceof CacheSynchronizationManagerConfig) {
            CacheSynchronizationManagerConfig csmConfig = (CacheSynchronizationManagerConfig)object;

            // otherwise ignore it. Can only be set on this clustering service
            if (csmConfig.getClusteringServiceConfig() instanceof JMSClusteringConfig) {
                ((JMSClusteringConfig)csmConfig.getClusteringServiceConfig()).setJMSTopicConnectionFactoryName(connectionFactoryName);
            }
        } else if (object instanceof JMSTopicTransportManagerConfig) {
            ((JMSTopicTransportManagerConfig)object).setTopicConnectionFactoryName(connectionFactoryName);
        }
    }

    /**
     * INTERNAL:
     */
    public void process_jms_topic_name_Tag(Node node, ObjectHolder holder) {
        Object object = holder.getObject();
        String topicName = node.getFirstChild().getNodeValue();

        if (object instanceof CacheSynchronizationManagerConfig) {
            CacheSynchronizationManagerConfig csmConfig = (CacheSynchronizationManagerConfig)object;

            // otherwise ignore it. Can only be set on this clustering service
            if (csmConfig.getClusteringServiceConfig() instanceof JMSClusteringConfig) {
                ((JMSClusteringConfig)csmConfig.getClusteringServiceConfig()).setJMSTopicName(topicName);
            }
        } else if (object instanceof JMSTopicTransportManagerConfig) {
            ((JMSTopicTransportManagerConfig)object).setTopicName(topicName);
        }
    }

    /**
     * INTERNAL:
     */
    public void process_jndi_user_name_Tag(Node node, ObjectHolder holder) {
        CacheSynchronizationManagerConfig csm = (CacheSynchronizationManagerConfig)holder.getObject();
        ((JNDIClusteringServiceConfig)csm.getClusteringServiceConfig()).setJNDIUsername(node.getFirstChild().getNodeValue());
    }

    /**
     * INTERNAL:
     */
    public void process_jndi_password_Tag(Node node, ObjectHolder holder) {
        CacheSynchronizationManagerConfig csm = (CacheSynchronizationManagerConfig)holder.getObject();
        ((JNDIClusteringServiceConfig)csm.getClusteringServiceConfig()).setJNDIPassword(node.getFirstChild().getNodeValue());
    }

    /**
     * INTERNAL:
     */
    public void process_naming_service_initial_context_factory_name_Tag(Node node, ObjectHolder holder) {
        CacheSynchronizationManagerConfig csm = (CacheSynchronizationManagerConfig)holder.getObject();
        ((JNDIClusteringServiceConfig)csm.getClusteringServiceConfig()).setNamingServiceInitialContextFactoryName(node.getFirstChild().getNodeValue());
    }

    /**
     * INTERNAL:
     */
    public void process_naming_service_url_Tag(Node node, ObjectHolder holder) {
        CacheSynchronizationManagerConfig csm = (CacheSynchronizationManagerConfig)holder.getObject();
        csm.getClusteringServiceConfig().setNamingServiceURL(node.getFirstChild().getNodeValue());
    }

    /**
     * INTERNAL:
     */
    public void process_sequence_preallocation_size_Tag(Node node, ObjectHolder holder) {
        ((DatabaseLoginConfig)holder.getObject()).setSequencePreallocationSize(Integer.valueOf(node.getFirstChild().getNodeValue()));
    }

    /**
     * INTERNAL:
     */
    public void process_multicast_port_Tag(Node node, ObjectHolder holder) {
        Object object = holder.getObject();
        Integer portNumber = Integer.valueOf(node.getFirstChild().getNodeValue());

        if (object instanceof CacheSynchronizationManagerConfig) {
            CacheSynchronizationManagerConfig csmConfig = (CacheSynchronizationManagerConfig)object;
            csmConfig.getClusteringServiceConfig().setMulticastPort(portNumber);
        } else if (object instanceof DiscoveryConfig) {
            ((DiscoveryConfig)object).setMulticastPort(portNumber.intValue());
        }
    }

    /**
     * INTERNAL:
     */
    public void process_packet_time_to_live_Tag(Node node, ObjectHolder holder) {
        Object object = holder.getObject();
        Integer timeToLive = Integer.valueOf(node.getFirstChild().getNodeValue());

        if (object instanceof CacheSynchronizationManagerConfig) {
            CacheSynchronizationManagerConfig csmConfig = (CacheSynchronizationManagerConfig)object;
            csmConfig.getClusteringServiceConfig().setPacketTimeToLive(timeToLive);
        } else if (object instanceof DiscoveryConfig) {
            ((DiscoveryConfig)object).setPacketTimeToLive(timeToLive.intValue());
        }
    }

    /**
     * INTERNAL:
     */
    public void process_clustering_service_Tag(Node node, ObjectHolder holder) {
        String serviceClass = node.getFirstChild().getNodeValue();
        CacheSynchronizationManagerConfig csmConfig = (CacheSynchronizationManagerConfig)holder.getObject();

        if (serviceClass.equals("org.eclipse.persistence.sessions.remote.rmi.RMIClusteringService")) {
            csmConfig.setClusteringServiceConfig(new RMIClusteringConfig());
        } else if (serviceClass.equals("org.eclipse.persistence.sessions.remote.rmi.RMIJNDIClusteringService")) {
            csmConfig.setClusteringServiceConfig(new RMIJNDIClusteringConfig());
        } else if (serviceClass.equals("org.eclipse.persistence.sessions.remote.rmi.wls.WLSClusteringService")) {
            csmConfig.setClusteringServiceConfig(new WLSClusteringConfig());
        } else if (serviceClass.equals("org.eclipse.persistence.sessions.remote.rmi.iiop.RMIJNDIClusteringService")) {
            csmConfig.setClusteringServiceConfig(new RMIIIOPJNDIClusteringConfig());
        } else if (serviceClass.equals("org.eclipse.persistence.sessions.remote.jms.JMSClusteringService")) {
            csmConfig.setClusteringServiceConfig(new JMSClusteringConfig());
        } else if (serviceClass.equals("org.eclipse.persistence.sessions.remote.corba.sun.CORBAJNDIClusteringService")) {
            csmConfig.setClusteringServiceConfig(new SunCORBAJNDIClusteringConfig());
        } else {
            // Default to Sun corba if unsupported protocol used.
            csmConfig.setClusteringServiceConfig(new SunCORBAJNDIClusteringConfig());
        }
    }

    /**
     * INTERNAL:
     */
    public void process_platform_class_Tag(Node node, ObjectHolder holder) {
        ((LoginConfig)holder.getObject()).setPlatformClass(node.getFirstChild().getNodeValue());
    }

    /**
     * INTERNAL:
     */
    public void process_type_Tag(Node node, ObjectHolder holder) {
        if (node.getFirstChild().getNodeValue().equals("org.eclipse.persistence.eis.EISLogin")) {
            holder.setObject(new EISLoginConfig());
        }

        // else we already set a DatabaseLoginConfig object on the holder
    }

    /**
     * INTERNAL:
     */
    public void process_external_transaction_controller_class_Tag(Node node, ObjectHolder holder) {
        CustomServerPlatformConfig platformConfig = new CustomServerPlatformConfig();
        platformConfig.setEnableJTA(true);
        platformConfig.setExternalTransactionControllerClass(node.getFirstChild().getNodeValue());
        ((SessionConfig)holder.getObject()).setServerPlatformConfig(platformConfig);
    }

    /**
     * INTERNAL:
     */
    public void process_exception_handler_class_Tag(Node node, ObjectHolder holder) {
        ((SessionConfig)holder.getObject()).setExceptionHandlerClass(node.getFirstChild().getNodeValue());
    }

    /**
     * INTERNAL:
     */
    public void process_profiler_class_Tag(Node node, ObjectHolder holder) {
        ((SessionConfig)holder.getObject()).setProfiler(node.getFirstChild().getNodeValue());
    }

    /**
     * INTERNAL:
     */
    public void process_logging_options_Tag(Node node, ObjectHolder holder) {
        processChildrenNodes(node, holder);
    }

    /**
     * INTERNAL:
     */
    public void process_java_Tag(Node node, ObjectHolder holder) {
        holder.setObject(new JavaLogConfig());
    }

    /**
     * INTERNAL:
     */
    public void process_toplink_Tag(Node node, ObjectHolder holder) {
        holder.setObject(new DefaultSessionLogConfig());
        processChildrenNodes(node, holder);
    }

    /**
     * INTERNAL:
     */
    public void process_log_type_Tag(Node node, ObjectHolder holder) {
        ObjectHolder logHolder = new ObjectHolder();
        processChildrenNodes(node, logHolder);
        ((SessionConfig)holder.getObject()).setLogConfig((LogConfig)logHolder.getObject());
    }

    /**
     * INTERNAL:
     */
    public void process_log_level_Tag(Node node, ObjectHolder holder) {
        NodeListElementEnumerator list = new NodeListElementEnumerator(node.getChildNodes());
        ((DefaultSessionLogConfig)holder.getObject()).setLogLevel(list.nextNode().getNodeName());
    }

    /**
     * INTERNAL:
     */
    public void process_file_name_Tag(Node node, ObjectHolder holder) {
        ((DefaultSessionLogConfig)holder.getObject()).setFilename(String.valueOf(node.getFirstChild().getNodeValue()));
    }

    /**
     * INTERNAL:
     */
    public void process_log_debug_Tag(Node node, ObjectHolder holder) {
    }

    /**
     * INTERNAL:
     */
    public void process_log_exceptions_Tag(Node node, ObjectHolder holder) {
    }

    /**
     * INTERNAL:
     */
    public void process_log_exception_stacktrace_Tag(Node node, ObjectHolder holder) {
    }

    /**
     * INTERNAL:
     */
    public void process_print_thread_Tag(Node node, ObjectHolder holder) {
    }

    /**
     * INTERNAL:
     */
    public void process_print_session_Tag(Node node, ObjectHolder holder) {
    }

    /**
     * INTERNAL:
     */
    public void process_print_connection_Tag(Node node, ObjectHolder holder) {
    }

    /**
     * INTERNAL:
     */
    public void process_print_date_Tag(Node node, ObjectHolder holder) {
    }

    /**
     * INTERNAL:
     */
    public void process_remote_command_Tag(Node node, ObjectHolder holder) {
        ObjectHolder rcmHolder = new ObjectHolder();
        rcmHolder.setObject(new RemoteCommandManagerConfig());
        processChildrenNodes(node, rcmHolder);

        ((SessionConfig)holder.getObject()).setRemoteCommandManagerConfig((RemoteCommandManagerConfig)rcmHolder.getObject());
    }

    /**
     * INTERNAL:
     */
    public void process_channel_Tag(Node node, ObjectHolder holder) {
        ((RemoteCommandManagerConfig)holder.getObject()).setChannel(node.getFirstChild().getNodeValue());
    }

    /**
     * INTERNAL:
     */
    public void process_commands_Tag(Node node, ObjectHolder holder) {
        ObjectHolder commandsConfigHolder = new ObjectHolder();
        commandsConfigHolder.setObject(new CommandsConfig());
        processChildrenNodes(node, commandsConfigHolder);

        ((RemoteCommandManagerConfig)holder.getObject()).setCommandsConfig((CommandsConfig)commandsConfigHolder.getObject());
    }

    /**
     * INTERNAL:
     */
    public void process_cache_sync_Tag(Node node, ObjectHolder holder) {
        ((CommandsConfig)holder.getObject()).setCacheSync(true);
    }

    /**
     * INTERNAL:
     */
    public void process_transport_Tag(Node node, ObjectHolder holder) {
        ObjectHolder transportHolder = new ObjectHolder();
        processChildrenNodes(node, transportHolder);

        ((RemoteCommandManagerConfig)holder.getObject()).setTransportManagerConfig((TransportManagerConfig)transportHolder.getObject());
    }

    /**
     * INTERNAL:
     */
    public void process_on_connection_error_Tag(Node node, ObjectHolder holder) {
        // Set the string value to this global. The appropriate transport manager
        // config will pick up its value.
        m_onConnectionError = node.getFirstChild().getNodeValue();
    }

    /**
     * INTERNAL:
     */
    public void process_topic_host_url_Tag(Node node, ObjectHolder holder) {
        ((JMSTopicTransportManagerConfig)holder.getObject()).setTopicHostURL(node.getFirstChild().getNodeValue());
    }

    /**
     * INTERNAL:
     */
    public void process_topic_connection_factory_name_Tag(Node node, ObjectHolder holder) {
        ((JMSTopicTransportManagerConfig)holder.getObject()).setTopicConnectionFactoryName(node.getFirstChild().getNodeValue());
    }

    /**
     * INTERNAL:
     */
    public void process_topic_name_Tag(Node node, ObjectHolder holder) {
        ((JMSTopicTransportManagerConfig)holder.getObject()).setTopicName(node.getFirstChild().getNodeValue());
    }

    /**
     * INTERNAL:
     */
    public void process_transport_class_Tag(Node node, ObjectHolder holder) {
        UserDefinedTransportManagerConfig transportConfig = new UserDefinedTransportManagerConfig();
        transportConfig.setOnConnectionError(m_onConnectionError);
        m_onConnectionError = null;// blank it out
        transportConfig.setTransportClass(node.getFirstChild().getNodeValue());

        holder.setObject(transportConfig);
    }

    /**
     * INTERNAL:
     */
    public void process_rmi_Tag(Node node, ObjectHolder holder) {
        RMITransportManagerConfig transportConfig = new RMITransportManagerConfig();
        transportConfig.setOnConnectionError(m_onConnectionError);
        m_onConnectionError = null;// blank it out
        holder.setObject(transportConfig);
        processChildrenNodes(node, holder);
    }

    /**
     * INTERNAL:
     */
    public void process_send_mode_Tag(Node node, ObjectHolder holder) {
        ((RMITransportManagerConfig)holder.getObject()).setSendMode(node.getFirstChild().getNodeValue());
    }

    /**
     * INTERNAL:
     */
    public void process_discovery_Tag(Node node, ObjectHolder holder) {
        ObjectHolder discoveryConfigHolder = new ObjectHolder();
        discoveryConfigHolder.setObject(new DiscoveryConfig());
        processChildrenNodes(node, discoveryConfigHolder);

        ((RMITransportManagerConfig)holder.getObject()).setDiscoveryConfig((DiscoveryConfig)discoveryConfigHolder.getObject());
    }

    /**
     * INTERNAL:
     */
    public void process_announcement_delay_Tag(Node node, ObjectHolder holder) {
        ((DiscoveryConfig)holder.getObject()).setAnnouncementDelay(Integer.parseInt(node.getFirstChild().getNodeValue()));
    }

    /**
     * INTERNAL:
     */
    public void process_jndi_naming_service_Tag(Node node, ObjectHolder holder) {
        ObjectHolder namingHolder = new ObjectHolder();
        namingHolder.setObject(new JNDINamingServiceConfig());
        processChildrenNodes(node, namingHolder);

        JNDINamingServiceConfig namingConfig = (JNDINamingServiceConfig)namingHolder.getObject();
        Object object = holder.getObject();

        if (object instanceof RMITransportManagerConfig) {
            ((RMITransportManagerConfig)object).setJNDINamingServiceConfig(namingConfig);
        } else if (object instanceof JMSTopicTransportManagerConfig) {
            ((JMSTopicTransportManagerConfig)object).setJNDINamingServiceConfig(namingConfig);
        }
    }

    /**
     * INTERNAL:
     */
    public void process_url_Tag(Node node, ObjectHolder holder) {
        Object object = holder.getObject();
        String url = node.getFirstChild().getNodeValue();

        if (object instanceof JNDINamingServiceConfig) {
            ((JNDINamingServiceConfig)object).setURL(url);
        } else if (object instanceof RMIRegistryNamingServiceConfig) {
            ((RMIRegistryNamingServiceConfig)object).setURL(url);
        }
    }

    /**
     * INTERNAL:
     */
    public void process_initial_context_factory_name_Tag(Node node, ObjectHolder holder) {
        ((JNDINamingServiceConfig)holder.getObject()).setInitialContextFactoryName(node.getFirstChild().getNodeValue());
    }

    /**
     * INTERNAL:
     */
    public void process_property_Tag(Node node, ObjectHolder holder) {
        JNDINamingServiceConfig namingConfig = (JNDINamingServiceConfig)holder.getObject();

        if (namingConfig.getPropertyConfigs() == null) {
            namingConfig.setPropertyConfigs(new Vector());
        }

        PropertyConfig propertyConfig = new PropertyConfig();
        propertyConfig.setName(node.getAttributes().getNamedItem("name").getNodeValue());
        propertyConfig.setValue(node.getAttributes().getNamedItem("value").getNodeValue());

        namingConfig.getPropertyConfigs().add(propertyConfig);
    }

    /**
     * INTERNAL:
     */
    public void process_rmi_registry_naming_service_Tag(Node node, ObjectHolder holder) {
        ObjectHolder namingHolder = new ObjectHolder();
        namingHolder.setObject(new RMIRegistryNamingServiceConfig());
        processChildrenNodes(node, namingHolder);

        RMIRegistryNamingServiceConfig namingConfig = (RMIRegistryNamingServiceConfig)namingHolder.getObject();
        ((RMITransportManagerConfig)holder.getObject()).setRMIRegistryNamingServiceConfig(namingConfig);
    }

    /**
     * INTERNAL:
     */
    protected void processChildrenNodes(Node node, ObjectHolder objectHolder) {
        NodeListElementEnumerator list = new NodeListElementEnumerator(node.getChildNodes());

        while (list.hasMoreNodes()) {
            Node childNode = list.nextNode();

            try {
                if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                    Class[] args = { Node.class, ObjectHolder.class };
                    Method method = (Method)AccessController.doPrivileged(new PrivilegedGetMethod(getClass(), convertNodeToMethodName(childNode), args, false));
                    Object[] objectList = { childNode, objectHolder };
                    AccessController.doPrivileged(new PrivilegedMethodInvoker(method, this, objectList));
                }else{
                    Class[] args = { Node.class, ObjectHolder.class };
                    Method method = PrivilegedAccessHelper.getMethod(getClass(), convertNodeToMethodName(childNode), args, false);
                    Object[] objectList = { childNode, objectHolder };
                    method.invoke(this, objectList);
                    
                }
            } catch (Exception exception) {
                throw SessionLoaderException.unkownTagAtNode(childNode.getNodeName(), node.getNodeName(), exception);
            }
        }
    }

    /**
     * INTERNAL:
     */
    protected void processChildrenNodes(NodeListElementEnumerator list, Node originalNode, ObjectHolder objectHolder) {
        while (list.hasMoreNodes()) {
            Node childNode = list.nextNode();

            try {
                if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                    Class[] args = { Node.class, ObjectHolder.class };
                    Method method = (Method)AccessController.doPrivileged(new PrivilegedGetMethod(getClass(), convertNodeToMethodName(childNode), args, false));
                    Object[] objectList = { childNode, objectHolder };
                    AccessController.doPrivileged(new PrivilegedMethodInvoker(method, this, objectList));
                }else{
                    Class[] args = { Node.class, ObjectHolder.class };
                    Method method = PrivilegedAccessHelper.getMethod(getClass(), convertNodeToMethodName(childNode), args, false);
                    Object[] objectList = { childNode, objectHolder };
                    method.invoke(this, objectList);
                }
            } catch (Exception exception) {
                throw SessionLoaderException.unkownTagAtNode(childNode.getNodeName(), originalNode.getNodeName(), exception);
            }
        }
    }

    /**
       * INTERNAL:
       */
    public class ObjectHolder {
        public Object object;

        public void setObject(Object object) {
            this.object = object;
        }

        public Object getObject() {
            return this.object;
        }
    }
}