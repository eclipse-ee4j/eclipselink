/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 *     cdelahun - Bug 214534: changes for JMSPublishingTransportManager configuration via session.xml
 ******************************************************************************/  
package org.eclipse.persistence.internal.sessions.factories;

import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.util.*;
import java.lang.reflect.Constructor;

import org.eclipse.persistence.config.SessionCustomizer;
import org.eclipse.persistence.eis.*;
//import org.eclipse.persistence.eis.adapters.xmlfile.XMLFileSequence;
import org.eclipse.persistence.internal.helper.ConversionManager;
import org.eclipse.persistence.oxm.XMLLogin;
import org.eclipse.persistence.logging.*;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.sessions.coordination.*;
import org.eclipse.persistence.sessions.coordination.rmi.RMITransportManager;
import org.eclipse.persistence.sessions.coordination.jms.JMSPublishingTransportManager;
import org.eclipse.persistence.sessions.coordination.jms.JMSTopicTransportManager;
import org.eclipse.persistence.sessions.coordination.corba.sun.SunCORBATransportManager;
import org.eclipse.persistence.sessions.server.*;
import org.eclipse.persistence.internal.databaseaccess.DatabasePlatform;
import org.eclipse.persistence.internal.databaseaccess.Platform;
import org.eclipse.persistence.platform.database.converters.StructConverter;
import org.eclipse.persistence.platform.server.ServerPlatform;
import org.eclipse.persistence.platform.server.NoServerPlatform;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.Login;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.DatasourceLogin;
import org.eclipse.persistence.sessions.SessionEventListener;
import org.eclipse.persistence.sequencing.Sequence;
import org.eclipse.persistence.sequencing.DefaultSequence;
import org.eclipse.persistence.sequencing.NativeSequence;
import org.eclipse.persistence.sequencing.TableSequence;
import org.eclipse.persistence.sequencing.UnaryTableSequence;
import org.eclipse.persistence.sessions.JNDIConnector;
import org.eclipse.persistence.sessions.broker.SessionBroker;
import org.eclipse.persistence.sessions.factories.XMLProjectReader;
import org.eclipse.persistence.tools.profiler.*;
import org.eclipse.persistence.internal.databaseaccess.DatasourcePlatform;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedGetConstructorFor;
import org.eclipse.persistence.internal.security.PrivilegedInvokeConstructor;
import org.eclipse.persistence.internal.security.PrivilegedNewInstanceFromClass;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.DatabaseSessionImpl;
import org.eclipse.persistence.internal.sessions.factories.model.*;
import org.eclipse.persistence.internal.sessions.factories.model.log.*;
import org.eclipse.persistence.internal.sessions.factories.model.pool.*;
import org.eclipse.persistence.internal.sessions.factories.model.rcm.*;
import org.eclipse.persistence.internal.sessions.factories.model.rcm.command.*;
import org.eclipse.persistence.internal.sessions.factories.model.login.*;
import org.eclipse.persistence.internal.sessions.factories.model.event.*;
import org.eclipse.persistence.internal.sessions.factories.model.project.*;
import org.eclipse.persistence.internal.sessions.factories.model.sequencing.*;
import org.eclipse.persistence.internal.sessions.factories.model.session.*;
import org.eclipse.persistence.internal.sessions.factories.model.platform.*;
import org.eclipse.persistence.internal.sessions.factories.model.property.*;
import org.eclipse.persistence.internal.sessions.factories.model.transport.*;
import org.eclipse.persistence.internal.sessions.factories.model.transport.naming.*;
import org.eclipse.persistence.internal.sessions.factories.model.transport.discovery.*;

/**
 * INTERNAL:
 * Builds EclipseLink Sessions from the XML Session Config model.
 * Model classes that are not built, are processed only.
 *
 * @author Guy Pelletier
 * @version 1.0
 * @date November 18, 2003
 */
public class SessionsFactory {
	protected Map m_sessions;
    protected Map m_logLevels;
    protected ClassLoader m_classLoader;

    /**
     * INTERNAL:
     */
    public SessionsFactory() {
        m_logLevels = new HashMap();
        m_logLevels.put("off", Integer.valueOf(SessionLog.OFF));
        m_logLevels.put("severe", Integer.valueOf(SessionLog.SEVERE));
        m_logLevels.put("warning", Integer.valueOf(SessionLog.WARNING));
        m_logLevels.put("info", Integer.valueOf(SessionLog.INFO));
        m_logLevels.put("config", Integer.valueOf(SessionLog.CONFIG));
        m_logLevels.put("fine", Integer.valueOf(SessionLog.FINE));
        m_logLevels.put("finer", Integer.valueOf(SessionLog.FINER));
        m_logLevels.put("finest", Integer.valueOf(SessionLog.FINEST));
        m_logLevels.put("all", Integer.valueOf(SessionLog.ALL));
    }

    /**
     * INTERNAL:
     * To build EclipseLink sessions, users must call this method with a
     * SessionConfigs object returned from an OX read in the
     * XMLSessionsConfigLoader
     */
    public Map buildSessionConfigs(SessionConfigs eclipseLinkSessions, ClassLoader classLoader) {
        m_sessions = new HashMap();
        m_classLoader = classLoader;
        Vector sessionBrokerConfigs = new Vector();
        Enumeration e = eclipseLinkSessions.getSessionConfigs().elements();

        while (e.hasMoreElements()) {
            SessionConfig sessionConfig = (SessionConfig)e.nextElement();

            if (sessionConfig instanceof SessionBrokerConfig) {
                // Hold all the session brokers till all the sessions have been built
                sessionBrokerConfigs.add(sessionConfig);
            } else {
                AbstractSession session = buildSession(sessionConfig);
                session.getDatasourcePlatform().getConversionManager().setLoader(classLoader);
                processSessionCustomizer(sessionConfig, session);
                m_sessions.put(session.getName(), session);
            }
        }

        // All the sessions have been built now so we can process the Session Brokers
        Enumeration ee = sessionBrokerConfigs.elements();

        while (ee.hasMoreElements()) {
            SessionBrokerConfig sessionBrokerConfig = (SessionBrokerConfig)ee.nextElement();
            SessionBroker sessionBroker = buildSessionBrokerConfig(sessionBrokerConfig);
            sessionBroker.getDatasourcePlatform().getConversionManager().setLoader(classLoader);
            processSessionCustomizer(sessionBrokerConfig, sessionBroker);
            m_sessions.put(sessionBroker.getName(), sessionBroker);
        }

        return m_sessions;
    }

    /**
     * INTERNAL:
     * Process the user inputed session customizer class. Will be run at the
     * end of the session build process
     */
    protected void processSessionCustomizer(SessionConfig sessionConfig, AbstractSession session) {
        // Session customizer - MUST BE THE LAST THING TO PROCESS
        String sessionCustomizerClassName = sessionConfig.getSessionCustomizerClass();
        if (sessionCustomizerClassName != null) {
            try {
                Class sessionCustomizerClass = m_classLoader.loadClass(sessionCustomizerClassName);
                if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                    ((SessionCustomizer)AccessController.doPrivileged(new PrivilegedNewInstanceFromClass(sessionCustomizerClass))).customize(session);
                }else{
                    ((SessionCustomizer)PrivilegedAccessHelper.newInstanceFromClass(sessionCustomizerClass)).customize(session);
                }
            } catch (Throwable exception) {
                throw SessionLoaderException.failedToLoadTag("session-customizer-class", sessionCustomizerClassName, exception);
            }
        }
    }

    /**
     * INTERNAL:
     * Build the correct session based on the session config type
     */
    protected AbstractSession buildSession(SessionConfig sessionConfig) {
        if (sessionConfig instanceof ServerSessionConfig) {
            return buildServerSessionConfig((ServerSessionConfig)sessionConfig);
        } else {// if (sessionConfig instanceof DatabaseSessionConfig) {
            return buildDatabaseSessionConfig((DatabaseSessionConfig)sessionConfig);
        }
    }

    /**
     * INTERNAL:
     * Wrapped by the getSession() call, therefore, config can't be null at this
     * point.
     */
    protected AbstractSession buildDatabaseSessionConfig(DatabaseSessionConfig databaseSessionConfig) {
        // Create a new database session (null means use login from deployment xml if there is one)
        DatabaseSessionImpl databaseSession = createSession(databaseSessionConfig, null);

        // Login - may overwrite the previous login (expected behavior)
        Login login = buildLogin(databaseSessionConfig.getLoginConfig());
        if (login != null) {
            databaseSession.setLogin(login);
        }

        // Common processing since ServerSessions inherit from DatabaseSession
        processDatabaseSessionConfig(databaseSessionConfig, databaseSession);

        // Process the common elements in SessionConfig
        processSessionConfig(databaseSessionConfig, databaseSession);

        return databaseSession;
    }

    /**
     * INTERNAL
     * Process a DatabaseSessionConfig object.
     */
    protected void processDatabaseSessionConfig(DatabaseSessionConfig sessionConfig, AbstractSession session) {
    }

    /**
     * INTERNAL:
     * Builds a server server from the given ServerSessionConfig.
     */
    protected AbstractSession buildServerSessionConfig(ServerSessionConfig serverSessionConfig) {
        // For server sessions we should build the login first, that way we can 
        // initialize the server session with the login (if there is one)
        Login login = buildLogin(serverSessionConfig.getLoginConfig());

        // Create a new server session
        ServerSession serverSession = (ServerSession)createSession(serverSessionConfig, login);

        // Common processing since ServerSessions inherit from DatabaseSession
        processDatabaseSessionConfig(serverSessionConfig, serverSession);

        // Process pools config
        processPoolsConfig(serverSessionConfig.getPoolsConfig(), serverSession);

        // Process connection policy config
        processConnectionPolicyConfig(serverSessionConfig.getConnectionPolicyConfig(), serverSession);

        // Process the common elements in SessionConfig
        processSessionConfig(serverSessionConfig, serverSession);

        return serverSession;
    }

    /**
     * INTERNAL:
     * Return a DatabaseSession object from it's config object using either
     * the project classes or project XML files.
     */
    protected DatabaseSessionImpl createSession(DatabaseSessionConfig sessionConfig, Login login) {
        Project primaryProject;

        if (sessionConfig.getPrimaryProject() != null) {
            primaryProject = loadProjectConfig(sessionConfig.getPrimaryProject());
        } else {
            primaryProject = new Project();// Build a session from an empty project
        }

        prepareProjectLogin(primaryProject, login);
        DatabaseSessionImpl sessionToReturn = getSession(sessionConfig, primaryProject);

        // Append descriptors from all subsequent project.xml and project classes 
        // to the mainProject  
        if (sessionConfig.getAdditionalProjects() != null) {
            Enumeration additionalProjects = sessionConfig.getAdditionalProjects().elements();

            while (additionalProjects.hasMoreElements()) {
                Project subProject = loadProjectConfig((ProjectConfig)additionalProjects.nextElement());
                primaryProject.addDescriptors(subProject, sessionToReturn);
            }
        }

        return sessionToReturn;
    }

    /**
     * INTERNAL:
     * Return the correct session type from the sessionConfig
     */
    protected void prepareProjectLogin(Project project, Login login) {
        if (login != null) {
            project.setLogin(login);
        } else if (project.getDatasourceLogin() == null) {
            // dummy login that needs to be set, otherwise session creation will fail
            project.setLogin(new DatabaseLogin());
        } else {
            // we read a login from the deployment xml of java, don't overwrite
        }
    }

    /**
     * INTERNAL:
     * Return the correct session type from the sessionConfig
     */
    protected DatabaseSessionImpl getSession(SessionConfig sessionConfig, Project project) {
        if (sessionConfig instanceof ServerSessionConfig) {
            return (ServerSession)project.createServerSession();
        } else {
            return (DatabaseSessionImpl)project.createDatabaseSession();
        }
    }

    /**
     * INTERNAL:
     * Load a projectConfig from the session.xml file. This method will determine
     * the proper loading scheme, that is, for a class or xml project.
     */
    protected Project loadProjectConfig(ProjectConfig projectConfig) {
        Project project = null;
        String projectString = projectConfig.getProjectString().trim();

        if (projectConfig.isProjectClassConfig()) {
            try {
                if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                    project = (Project) AccessController.doPrivileged(new PrivilegedNewInstanceFromClass(m_classLoader.loadClass(projectString)));
                }else{
                    project = (Project) PrivilegedAccessHelper.newInstanceFromClass(m_classLoader.loadClass(projectString));
                }
            } catch (Throwable exception) {
                throw SessionLoaderException.failedToLoadProjectClass(projectString, exception);
            }
        } else {
            try {
                project = XMLProjectReader.read(projectString, m_classLoader);
            } catch (ValidationException validationException) {
                if (validationException.getErrorCode() == ValidationException.PROJECT_XML_NOT_FOUND) {
                    try {
                        project = XMLProjectReader.read(projectString);
                    } catch (Exception e) {
                        throw SessionLoaderException.failedToLoadProjectXml(projectString, validationException);
                    }
                } else {
                    throw SessionLoaderException.failedToLoadProjectXml(projectString, validationException);
                }
            }
        }

        return project;
    }

    /**
     * INTERNAL:
     * Build the correct login based on the login config type
     */
    protected Login buildLogin(LoginConfig loginConfig) {
        if (loginConfig instanceof EISLoginConfig) {
            return buildEISLoginConfig((EISLoginConfig)loginConfig);
        } else if (loginConfig instanceof XMLLoginConfig) {
            return buildXMLLoginConfig((XMLLoginConfig)loginConfig);
        } else if (loginConfig instanceof DatabaseLoginConfig) {
            return buildDatabaseLoginConfig((DatabaseLoginConfig)loginConfig);
        } else {
            return null;
        }
    }

    /**
     * INTERNAL:
     * Wrapped by the getLogin() call, therefore, config can't be null at this
     * point.
     */
    protected Login buildEISLoginConfig(EISLoginConfig eisLoginConfig) {
        EISLogin eisLogin = new EISLogin();

        // Connection Spec
        String specClassName = eisLoginConfig.getConnectionSpecClass();
        if (specClassName != null) {
            try {
                Class specClass = m_classLoader.loadClass(specClassName);
                EISConnectionSpec spec = null;
                if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                    spec = (EISConnectionSpec)AccessController.doPrivileged(new PrivilegedNewInstanceFromClass(specClass));
                }else{
                    spec = (EISConnectionSpec)PrivilegedAccessHelper.newInstanceFromClass(specClass);
                }
                eisLogin.setConnectionSpec(spec);
            } catch (Exception exception) {
                throw SessionLoaderException.failedToLoadTag("connection-spec-class", specClassName, exception);
            }
        }

        // Connection Factory URL, setConnectionFactoryURL checks for null
        eisLogin.setConnectionFactoryURL(eisLoginConfig.getConnectionFactoryURL());

        // Process the common elements in LoginConfig
        processLoginConfig(eisLoginConfig, eisLogin);

        // Finally, return the newly created EISLogin
        return eisLogin;
    }

    /**
      * INTERNAL:
      * Wrapped by the getLogin() call, therefore, config can't be null at this
      * point.
      */
    protected Login buildXMLLoginConfig(XMLLoginConfig xmlLoginConfig) {
        XMLLogin xmlLogin = new XMLLogin();

        // Process the common elements in LoginConfig
        processLoginConfig(xmlLoginConfig, xmlLogin);

        // Finally, return the newly created XMLLogin
        return xmlLogin;
    }

    /**
     * INTERNAL:
     * Build a DatabaseLogin for the given Session
     * Wrapped by the getLogin() call, therefore, config can't be null at this
     * point.
     */
    protected Login buildDatabaseLoginConfig(DatabaseLoginConfig databaseLoginConfig) {
        DatabaseLogin databaseLogin = new DatabaseLogin();

        // Driver class
        String driverClassName = databaseLoginConfig.getDriverClass();
        if (driverClassName != null) {
            try {
                Class driverClass = m_classLoader.loadClass(driverClassName);
                databaseLogin.setDriverClass(driverClass);
            } catch (Exception exception) {
                throw SessionLoaderException.failedToLoadTag("driver-class", driverClassName, exception);
            }
        }

        // Connection URL
        String connectionString = databaseLoginConfig.getConnectionURL();
        if (connectionString != null) {
            databaseLogin.setConnectionString(connectionString);
        }

        // Datasource
        String datasourceName = databaseLoginConfig.getDatasource();
        if (datasourceName != null) {
            try {
                JNDIConnector jndiConnector = new JNDIConnector(new javax.naming.InitialContext(), datasourceName);
                jndiConnector.setLookupType(databaseLoginConfig.getLookupType().intValue());
                databaseLogin.setConnector(jndiConnector);
            } catch (Exception exception) {
                throw SessionLoaderException.failedToLoadTag("datasource", datasourceName, exception);
            }
        }

        // Bind all parameters - XML Schema default is false
        databaseLogin.setShouldBindAllParameters(databaseLoginConfig.getBindAllParameters());

        // Cache all statements - XML Schema default is false
        databaseLogin.setShouldCacheAllStatements(databaseLoginConfig.getCacheAllStatements());

        // Byte array binding - XML Schema default is true
        databaseLogin.setUsesByteArrayBinding(databaseLoginConfig.getByteArrayBinding());

        // String binding - XML Schema default is false
        databaseLogin.setUsesStringBinding(databaseLoginConfig.getStringBinding());

        // Stream binding - XML Schema default is false
        databaseLogin.setUsesStreamsForBinding(databaseLoginConfig.getStreamsForBinding());

        // Force field to uppper case - XML Schema default is false
        databaseLogin.setShouldForceFieldNamesToUpperCase(databaseLoginConfig.getForceFieldNamesToUppercase());

        // Optimize data conversion - XML Schema default is true
        databaseLogin.setShouldOptimizeDataConversion(databaseLoginConfig.getOptimizeDataConversion());

        // Trim strings - XML Schema default is true
        databaseLogin.setShouldTrimStrings(databaseLoginConfig.getTrimStrings());

        // Batch writing - XML Schema default is false
        databaseLogin.setUsesBatchWriting(databaseLoginConfig.getBatchWriting());

        // JDBC 2.0 batch writing - XML Schema default is true
        databaseLogin.setUsesJDBCBatchWriting(databaseLoginConfig.getJdbcBatchWriting());

        // Max batch writing size - XML Schema default is 32000
        Integer maxBatchWritingSize = databaseLoginConfig.getMaxBatchWritingSize();
        if (maxBatchWritingSize != null) {
            databaseLogin.setMaxBatchWritingSize(maxBatchWritingSize.intValue());
        }

        // Native SQL - XML Schema default is false
        databaseLogin.setUsesNativeSQL(databaseLoginConfig.getNativeSQL());

        // Process the common elements in LoginConfig
        processLoginConfig(databaseLoginConfig, databaseLogin);

        processStructConverterConfig(databaseLoginConfig.getStructConverterConfig(), databaseLogin);
        
        if (databaseLoginConfig.isConnectionHealthValidatedOnError() != null){
            databaseLogin.setConnectionHealthValidatedOnError(databaseLoginConfig.isConnectionHealthValidatedOnError());
        }
        if (databaseLoginConfig.getQueryRetryAttemptCount() != null){
            databaseLogin.setQueryRetryAttemptCount(databaseLoginConfig.getQueryRetryAttemptCount());
        }
        if (databaseLoginConfig.getDelayBetweenConnectionAttempts() != null){
            databaseLogin.setDelayBetweenConnectionAttempts(databaseLoginConfig.getDelayBetweenConnectionAttempts());
        }
        if (databaseLoginConfig.getPingSQL() != null){
            databaseLogin.setPingSQL(databaseLoginConfig.getPingSQL());
        }
        
        // Finally, return the newly created DatabaseLogin
        return databaseLogin;
    }

    /**
     * INTERNAL:
     */
    protected void processStructConverterConfig(StructConverterConfig converterClassConfig, DatabaseLogin login) {
        if (converterClassConfig != null) {
            Platform platform = login.getDatasourcePlatform();
            if (platform instanceof DatabasePlatform){
                Iterator i = converterClassConfig.getStructConverterClasses().iterator();
    
                while (i.hasNext()) {
                    String converterClassName = (String)i.next();
                    try {
                        Class converterClass = m_classLoader.loadClass(converterClassName);
                        StructConverter converter = null;
                        if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                            try{
                                converter = (StructConverter)AccessController.doPrivileged(new PrivilegedNewInstanceFromClass(converterClass));
                            }catch (PrivilegedActionException ex){
                                throw (Exception)ex.getCause();
                            }
                        }else{
                            converter = (StructConverter)PrivilegedAccessHelper.newInstanceFromClass(converterClass);
                        }
                        ((DatabasePlatform)platform).addStructConverter(converter);
                    } catch (Exception exception) {
                        throw SessionLoaderException.failedToLoadTag("struct-converter", converterClassName, exception);
                    }
                }
            }
        }
    }
    
    /**
     * INTERNAL:
     * Process the common elements of a Login.
     */
    protected void processLoginConfig(LoginConfig loginConfig, DatasourceLogin login) {
        // Platform class
        String platformClassName = loginConfig.getPlatformClass();
        if (platformClassName != null) {
            try {
                Class platformClass = m_classLoader.loadClass(platformClassName);
                if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                    login.usePlatform((DatasourcePlatform)AccessController.doPrivileged(new PrivilegedNewInstanceFromClass(platformClass)));
                }else{
                    login.usePlatform((DatasourcePlatform)PrivilegedAccessHelper.newInstanceFromClass(platformClass));
                }
            } catch (Exception exception) {
                throw SessionLoaderException.failedToLoadTag("platform-class", platformClassName, exception);
            }
        }

        // Table qualifier
        String tableQualifier = loginConfig.getTableQualifier();
        if (tableQualifier != null) {
            login.setTableQualifier(tableQualifier);
        }

        // Username - setUserName checks for null
        login.setUserName(loginConfig.getUsername());

        // Encryption class (must be set before the password)
        // XML Schema default is org.eclipse.persistence.internal.security.JCEEncryptor
        login.setEncryptionClassName(loginConfig.getEncryptionClass());

        // Password is encrypted on the model - setEncryptedPassword checks for null
        login.setEncryptedPassword(loginConfig.getEncryptedPassword());

        // External connection pool - XML Schema default is false
        login.setUsesExternalConnectionPooling(loginConfig.getExternalConnectionPooling());

        // External transaction controller - XML Schema default is false
        login.setUsesExternalTransactionController(loginConfig.getExternalTransactionController());

        // Sequencing - XML Schema default is null
        if (loginConfig.getSequencingConfig() != null) {
            if (loginConfig.getSequencingConfig().getDefaultSequenceConfig() != null) {
                Sequence sequence = buildSequence(loginConfig.getSequencingConfig().getDefaultSequenceConfig());
                login.setDefaultSequence(sequence);
            }

            if ((loginConfig.getSequencingConfig().getSequenceConfigs() != null) && !loginConfig.getSequencingConfig().getSequenceConfigs().isEmpty()) {
                Enumeration eSequenceConfigs = loginConfig.getSequencingConfig().getSequenceConfigs().elements();

                while (eSequenceConfigs.hasMoreElements()) {
                    Sequence sequence = buildSequence((SequenceConfig)eSequenceConfigs.nextElement());
                    login.addSequence(sequence);
                }
            }
        }

        // Properties (assumes they are all valid) 
        if (loginConfig.getPropertyConfigs() != null) {
            Enumeration e = loginConfig.getPropertyConfigs().elements();

            while (e.hasMoreElements()) {
                PropertyConfig propertyConfig = (PropertyConfig)e.nextElement();
                login.getProperties().put(propertyConfig.getName(), propertyConfig.getValue());
            }
        }
    }

    /**
     * INTERNAL:
     * Process the PoolsConfig object.
     */
    protected void processPoolsConfig(PoolsConfig poolsConfig, ServerSession serverSession) {
        if (poolsConfig != null) {
            // Read connection pool
            ReadConnectionPoolConfig readConnectionPoolConfig = poolsConfig.getReadConnectionPoolConfig();
            if (readConnectionPoolConfig != null) {
                serverSession.setReadConnectionPool(buildReadConnectionPoolConfig(readConnectionPoolConfig, serverSession));
            }

            // Write connection pool
            ConnectionPoolConfig writeConnectionPoolConfig = poolsConfig.getWriteConnectionPoolConfig();
            if (writeConnectionPoolConfig != null) {
                serverSession.addConnectionPool(buildConnectionPoolConfig(writeConnectionPoolConfig, serverSession));
            }

            // Sequence connection pool
            ConnectionPoolConfig sequenceConnectionPoolConfig = poolsConfig.getSequenceConnectionPoolConfig();
            if (sequenceConnectionPoolConfig != null) {
                processSequenceConnectionPoolConfig(sequenceConnectionPoolConfig, serverSession);
            }

            // Connection pools
            Enumeration e = poolsConfig.getConnectionPoolConfigs().elements();
            while (e.hasMoreElements()) {
                ConnectionPoolConfig connectionPoolConfig = (ConnectionPoolConfig)e.nextElement();
                serverSession.addConnectionPool(buildConnectionPoolConfig(connectionPoolConfig, serverSession));
            }
        }
    }

    /**
     * INTERNAL:
     * Process a SequenceConnectionPoolConfig object.
     */
    protected void processSequenceConnectionPoolConfig(ConnectionPoolConfig poolConfig, ServerSession serverSession) {
        // Set the Sequence connection pool flag to true
        serverSession.getSequencingControl().setShouldUseSeparateConnection(true);

        // Max connections
        Integer maxConnections = poolConfig.getMaxConnections();
        if (maxConnections != null) {
            serverSession.getSequencingControl().setMaxPoolSize(maxConnections.intValue());
        }

        // Min connections
        Integer minConnections = poolConfig.getMinConnections();
        if (minConnections != null) {
            serverSession.getSequencingControl().setMinPoolSize(minConnections.intValue());
        }

        // Name - no need to process
    }

    /**
     * INTERNAL:
     * Process a ServerPlatformConfig object.
     */
    protected void processServerPlatformConfig(ServerPlatformConfig platformConfig, ServerPlatform platform) {
        // Enable runtime services - XML schema default is true
        if (!platformConfig.getEnableRuntimeServices()) {
            platform.disableRuntimeServices();
        }

        // Enable JTA - XML schema default is true
        if (!platformConfig.getEnableJTA()) {
            platform.disableJTA();
        }
    }

    /**
     * INTERNAL:
     * Build a connection pool from the config to store on the server session.
     */
    protected ConnectionPool buildConnectionPoolConfig(ConnectionPoolConfig poolConfig, ServerSession serverSession) {
        ConnectionPool connectionPool = new ConnectionPool();

        // Process the common elements in ConnectionPool
        processConnectionPoolConfig(poolConfig, connectionPool, serverSession);

        return connectionPool;
    }

    /**
     * INTERNAL:
     */
    protected ServerPlatform buildCustomServerPlatformConfig(CustomServerPlatformConfig platformConfig, DatabaseSessionImpl session) {
        ServerPlatform platform;

        // Server class - XML schema default is org.eclipse.persistence.platform.server.CustomServerPlatform
        String serverClassName = platformConfig.getServerClassName();
        try {
            Class serverClass = m_classLoader.loadClass(serverClassName);
            if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                Constructor constructor = (Constructor)AccessController.doPrivileged(new PrivilegedGetConstructorFor(serverClass, new Class[] { org.eclipse.persistence.sessions.DatabaseSession.class }, false));
                platform = (ServerPlatform)AccessController.doPrivileged(new PrivilegedInvokeConstructor(constructor, new Object[] { session }));
            }else{
                Constructor constructor = PrivilegedAccessHelper.getConstructorFor(serverClass, new Class[] { org.eclipse.persistence.sessions.DatabaseSession.class }, false);
                platform = (ServerPlatform)PrivilegedAccessHelper.invokeConstructor(constructor, new Object[] { session });
            }
        } catch (Throwable e) {
            throw SessionLoaderException.failedToLoadTag("server-class", serverClassName, e);
        }

        // External transaction controller class
        String externalTransactionControllerClass = platformConfig.getExternalTransactionControllerClass();
        if (externalTransactionControllerClass != null) {
            try {
                platform.setExternalTransactionControllerClass(m_classLoader.loadClass(externalTransactionControllerClass));
            } catch (Exception exception) {
                throw SessionLoaderException.failedToLoadTag("external-transaction-controller-class", externalTransactionControllerClass, exception);
            }
        }

        return platform;
    }

    /**
     * INTERNAL:
     * Build a read connection pool from the config to store on the server session.
     */
    protected ConnectionPool buildReadConnectionPoolConfig(ReadConnectionPoolConfig poolConfig, ServerSession serverSession) {
        // Exclusive tag - XML Schema default is false
        ConnectionPool connectionPool = (poolConfig.getExclusive()) ? new ConnectionPool() : new ReadConnectionPool();

        // Process the common elements in ConnectionPool
        processConnectionPoolConfig(poolConfig, connectionPool, serverSession);

        return connectionPool;
    }

    /**
     * INTERNAL:
     * Process the common elements from a ConnectionPoolConfig
     */
    protected void processConnectionPolicyConfig(ConnectionPolicyConfig connectionPolicyConfig, ServerSession serverSession) {
        if (connectionPolicyConfig != null) {
            ConnectionPolicy connectionPolicy = serverSession.getDefaultConnectionPolicy();
            if(connectionPolicyConfig.getUseExclusiveConnection()) {
                connectionPolicy.setExclusiveMode(ConnectionPolicy.ExclusiveMode.Isolated);
            } else {
                connectionPolicy.setExclusiveMode(ConnectionPolicy.ExclusiveMode.Transactional);
            }
            connectionPolicy.setIsLazy(connectionPolicyConfig.getLazy());
        }
    }

    /**
     * INTERNAL:
     * Process the common elements from a ConnectionPoolConfig
     */
    protected void processConnectionPoolConfig(ConnectionPoolConfig poolConfig, ConnectionPool connectionPool, AbstractSession session) {
        // Login - if null, set it to the same as the session login
        Login login = buildLogin(poolConfig.getLoginConfig());
        if (login != null) {
            connectionPool.setLogin(login);
        } else {
            connectionPool.setLogin(session.getDatasourceLogin());
        }

        // Name
        connectionPool.setName(poolConfig.getName());

        // Max connections
        Integer maxConnections = poolConfig.getMaxConnections();
        if (maxConnections != null) {
            connectionPool.setMaxNumberOfConnections(maxConnections.intValue());
        }

        // Min connections
        Integer minConnections = poolConfig.getMinConnections();
        if (minConnections != null) {
            connectionPool.setMinNumberOfConnections(minConnections.intValue());
        }
    }

    /**
     * INTERNAL:
     * Process the common elements from a SessionConfig.
     */
    protected void processSessionConfig(SessionConfig sessionConfig, AbstractSession session) {
        // Name
        session.setName(sessionConfig.getName().trim());
        
        // Session Event Manager
        processSessionEventManagerConfig(sessionConfig.getSessionEventManagerConfig(), session);

        //server platform
        ((DatabaseSessionImpl)session).setServerPlatform(buildServerPlatformConfig(sessionConfig.getServerPlatformConfig(), (DatabaseSessionImpl)session));

        // Session Log - BUG# 3442865, don't set the log if it is null
        SessionLog log = buildSessionLog(sessionConfig.getLogConfig(), session);
        if (log != null) {
            session.setSessionLog(log);
        }
        
        // Remote command manager    
        buildRemoteCommandManagerConfig(sessionConfig.getRemoteCommandManagerConfig(), session);

        // Profiler - XML Schema default is null
        if (sessionConfig.getProfiler() != null) {
            if (sessionConfig.getProfiler().equals("eclipselink")) {
                session.setProfiler(new PerformanceProfiler());
            }
        }

        // Exception handler
        String exceptionHandlerClassName = sessionConfig.getExceptionHandlerClass();
        if (exceptionHandlerClassName != null) {
            try {
                Class exceptionHandlerClass = m_classLoader.loadClass(exceptionHandlerClassName);
                if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                    session.setExceptionHandler((ExceptionHandler)AccessController.doPrivileged(new PrivilegedNewInstanceFromClass(exceptionHandlerClass)));
                }else{
                    session.setExceptionHandler((ExceptionHandler)PrivilegedAccessHelper.newInstanceFromClass(exceptionHandlerClass));
                }
            } catch (Throwable e) {
                throw SessionLoaderException.failedToLoadTag("exception-handler-class", exceptionHandlerClassName, e);
            }
        }

        // Session customizer will be processed in the buildSessions method.
        // Ensures it is run last.
    }

    /**
     * INTERNAL: Build the appropriate server platform
     */
    protected ServerPlatform buildServerPlatformConfig(ServerPlatformConfig platformConfig, DatabaseSessionImpl session) {
        if (platformConfig == null) {
            return new NoServerPlatform(session);
        }

        // Build the server platform, the config model knows which to build.
        ServerPlatform platform;

        if (platformConfig instanceof CustomServerPlatformConfig) {
            platform = buildCustomServerPlatformConfig((CustomServerPlatformConfig)platformConfig, session);
        } else {
            // A supported platform so instantiate an object of its type.
            String serverClassName = platformConfig.getServerClassName();
            if (platformConfig.isSupported()) {
                try {
                    Class serverClass = m_classLoader.loadClass(serverClassName);
                    if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                        Constructor constructor = (Constructor) AccessController.doPrivileged(new PrivilegedGetConstructorFor(serverClass, new Class[] { DatabaseSession.class }, false));
                        platform = (ServerPlatform)AccessController.doPrivileged(new PrivilegedInvokeConstructor(constructor, new Object[] { session }));
                    } else {
                        Constructor constructor = PrivilegedAccessHelper.getConstructorFor(serverClass, new Class[] { DatabaseSession.class }, false);
                        platform = (ServerPlatform)PrivilegedAccessHelper.invokeConstructor(constructor, new Object[] { session });
                    }
                } catch (Throwable e) {
                    try {
                        Class serverClass = getClass().getClassLoader().loadClass(serverClassName);
                        if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                            Constructor constructor = (Constructor) AccessController.doPrivileged(new PrivilegedGetConstructorFor(serverClass, new Class[] { DatabaseSession.class }, false));
                            platform = (ServerPlatform)AccessController.doPrivileged(new PrivilegedInvokeConstructor(constructor, new Object[] { session }));
                        } else {
                            Constructor constructor = PrivilegedAccessHelper.getConstructorFor(serverClass, new Class[] { DatabaseSession.class }, false);
                            platform = (ServerPlatform)PrivilegedAccessHelper.invokeConstructor(constructor, new Object[] { session });
                        }
                    } catch (Throwable ignore) {
                        // Ignore, throw first error.
                    }
                    throw SessionLoaderException.failedToParseXML("Server platform class is invalid: " + serverClassName, e);
                }
            } else {
                throw SessionLoaderException.serverPlatformNoLongerSupported(serverClassName);
            }
        }

        // Process the common elements in ServerPlatformConfig 
        processServerPlatformConfig(platformConfig, platform);
        return platform;
    }

    /**
     * INTERNAL:
     */
    protected void buildRemoteCommandManagerConfig(RemoteCommandManagerConfig rcmConfig, AbstractSession session) {
        if (rcmConfig != null) {
            RemoteCommandManager rcm = new RemoteCommandManager(session);

            // Commands
            processCommandsConfig(rcmConfig.getCommandsConfig(), rcm);

            // Transport Manager - will set the built TransportManager on the given 
            // Remote command manager that is passed in
            buildTransportManager(rcmConfig.getTransportManagerConfig(), rcm);

            // Channel - XML Schema default is TopLinkCommandChannel
            rcm.setChannel(rcmConfig.getChannel());
        }
    }

    /**
     * INTERNAL:
     */
    protected void buildTransportManager(TransportManagerConfig tmConfig, RemoteCommandManager rcm) {
        if (tmConfig instanceof RMITransportManagerConfig) {
            buildRMITransportManagerConfig((RMITransportManagerConfig)tmConfig, rcm);
        } else if (tmConfig instanceof RMIIIOPTransportManagerConfig) {
            buildRMIIIOPTransportManagerConfig((RMIIIOPTransportManagerConfig)tmConfig, rcm);
        }  else if (tmConfig instanceof JMSTopicTransportManagerConfig) {
            buildJMSTopicTransportManagerConfig((JMSTopicTransportManagerConfig)tmConfig, rcm);
        } else if (tmConfig instanceof JMSPublishingTransportManagerConfig) {
            buildJMSPublishingTransportManagerConfig((JMSPublishingTransportManagerConfig)tmConfig, rcm);
        } else if (tmConfig instanceof Oc4jJGroupsTransportManagerConfig) {
            buildOc4jJGroupsTransportManagerConfig((Oc4jJGroupsTransportManagerConfig)tmConfig, rcm);
        } else if (tmConfig instanceof SunCORBATransportManagerConfig) {
            buildSunCORBATransportManagerConfig((SunCORBATransportManagerConfig)tmConfig, rcm);
        } else if (tmConfig instanceof UserDefinedTransportManagerConfig) {
            buildUserDefinedTransportManagerConfig((UserDefinedTransportManagerConfig)tmConfig, rcm);
        }
    }

    /**
     * INTERNAL:
     */
    protected void buildRMITransportManagerConfig(RMITransportManagerConfig tmConfig, RemoteCommandManager rcm) {
        RMITransportManager tm = new RMITransportManager(rcm);

        // Set the transport manager. This will initialize the DiscoveryManager
        // This needs to be done before we process the DiscoveryConfig.
        rcm.setTransportManager(tm);

        // Discovery
        DiscoveryConfig discoveryConfig = tmConfig.getDiscoveryConfig();
        if (discoveryConfig != null) {
            processDiscoveryConfig(discoveryConfig, rcm.getDiscoveryManager());
        }

        if (tmConfig.getJNDINamingServiceConfig() != null) {
            // JNDI naming service
            tm.setNamingServiceType(TransportManager.JNDI_NAMING_SERVICE);
            processJNDINamingServiceConfig(tmConfig.getJNDINamingServiceConfig(), tm);
        } else if (tmConfig.getRMIRegistryNamingServiceConfig() != null) {
            // RMI registry naming service
            tm.setNamingServiceType(TransportManager.REGISTRY_NAMING_SERVICE);
            processRMIRegistryNamingServiceConfig(tmConfig.getRMIRegistryNamingServiceConfig(), tm);
        }

        tm.setIsRMIOverIIOP(tmConfig instanceof RMIIIOPTransportManagerConfig);

        // Send mode - Can only be Asynchronous (true) or Synchronous (false), validated by the schema
        // XML Schema default is Asynchronous
        rcm.setShouldPropagateAsynchronously(tmConfig.getSendMode().equals("Asynchronous"));

        // Process the common elements in TransportManagerConfig
        processTransportManagerConfig(tmConfig, tm);
    }

    /**
     * INTERNAL:
     * Builds a Sequence from the given SequenceConfig.
     */
    protected Sequence buildSequence(SequenceConfig sequenceConfig) {
        if (sequenceConfig == null) {
            return null;
        }

        String name = sequenceConfig.getName();
        int size = sequenceConfig.getPreallocationSize().intValue();

        if (sequenceConfig instanceof DefaultSequenceConfig) {
            return new DefaultSequence(name, size);
        } else if (sequenceConfig instanceof NativeSequenceConfig) {
            return new NativeSequence(name, size);
        } else if (sequenceConfig instanceof TableSequenceConfig) {
            TableSequenceConfig tsc = (TableSequenceConfig)sequenceConfig;
            return new TableSequence(name, size, tsc.getTable(), tsc.getNameField(), tsc.getCounterField());
        } else if (sequenceConfig instanceof UnaryTableSequenceConfig) {
            UnaryTableSequenceConfig utsc = (UnaryTableSequenceConfig)sequenceConfig;
            return new UnaryTableSequence(name, size, utsc.getCounterField());
        } else if (sequenceConfig instanceof XMLFileSequenceConfig) {
            try {
                // Can no longer reference class directly as in a different project.
                Class xmlClass = Class.forName("org.eclipse.persistence.eis.adapters.xmlfile.XMLFileSequence");
                Sequence sequence = (Sequence)xmlClass.newInstance();
                sequence.setName(name);
                sequence.setInitialValue(size);
                return sequence;
            } catch (Exception missing) {
                return null;
            }
        } else {
            // Unknown SequenceConfig subclass - should never happen
            return null;
        }
    }

    /**
     * INTERNAL:
     * Left this in for now since in the future we may add more IIOP specific
     * configurations?
     */
    protected void buildRMIIIOPTransportManagerConfig(RMIIIOPTransportManagerConfig tmConfig, RemoteCommandManager rcm) {
        buildRMITransportManagerConfig(tmConfig, rcm);
    }

    /**
     * INTERNAL:
     */
    protected void buildJMSTopicTransportManagerConfig(JMSTopicTransportManagerConfig tmConfig, RemoteCommandManager rcm) {
        JMSTopicTransportManager tm = new JMSTopicTransportManager(rcm);

        // Set the transport manager. This will initialize the DiscoveryManager
        rcm.setTransportManager(tm);
        
        processJMSTransportManagerConfig(tmConfig, tm);
    }
    
    /**
     * INTERNAL:
     */
    protected void buildJMSPublishingTransportManagerConfig(JMSPublishingTransportManagerConfig tmConfig, RemoteCommandManager rcm) {
        JMSPublishingTransportManager tm = new JMSPublishingTransportManager(rcm);

        // Set the transport manager. This will initialize the DiscoveryManager
        rcm.setTransportManager(tm);
        processJMSTransportManagerConfig(tmConfig, tm);
    }
    
    /**
     * INTERNAL:
     * Common JMS configuration
     */
    protected void processJMSTransportManagerConfig(JMSPublishingTransportManagerConfig tmConfig, JMSPublishingTransportManager tm ){
        // JNDI naming service
        if (tmConfig.getJNDINamingServiceConfig() != null) {
            tm.setNamingServiceType(TransportManager.JNDI_NAMING_SERVICE);
            processJNDINamingServiceConfig(tmConfig.getJNDINamingServiceConfig(), tm);
        }

        // Topic host URL
        String topicHostURL = tmConfig.getTopicHostURL();
        if (topicHostURL != null) {
            tm.setTopicHostUrl(topicHostURL);
        }

        // Topic connection factory name - XML Schema default is jms/TopLinkTopicConnectionFactory
        tm.setTopicConnectionFactoryName(tmConfig.getTopicConnectionFactoryName());

        // Topic name - XML Schema default is jms/TopLinkTopic
        tm.setTopicName(tmConfig.getTopicName());

        // Process the common elements in TransportManagerConfig
        processTransportManagerConfig(tmConfig, tm);
    }

    /**
     * INTERNAL:
     */
    protected void buildOc4jJGroupsTransportManagerConfig(Oc4jJGroupsTransportManagerConfig tmConfig, RemoteCommandManager rcm) {
        TransportManager tm = null;
        try {
            Class tmClass = m_classLoader.loadClass(tmConfig.getTransportManagerClassName());
            if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                Constructor constructor = (Constructor)AccessController.doPrivileged(new PrivilegedGetConstructorFor(tmClass, new Class[] { RemoteCommandManager.class, boolean.class, String.class }, false));
                tm = (TransportManager)AccessController.doPrivileged(new PrivilegedInvokeConstructor(constructor, new Object[] { rcm, tmConfig.useSingleThreadedNotification(), tmConfig.getTopicName() }));
            }else{
                Constructor constructor = PrivilegedAccessHelper.getConstructorFor(tmClass, new Class[] { RemoteCommandManager.class, boolean.class, String.class }, false);
                tm = (TransportManager)PrivilegedAccessHelper.invokeConstructor(constructor, new Object[] { rcm, tmConfig.useSingleThreadedNotification(), tmConfig.getTopicName() });
            }
        } catch (Throwable e) {
            throw SessionLoaderException.failedToParseXML("Oc4jJGroupsTransportManager class is invalid: " + tmConfig.getTransportManagerClassName(), e);
        }

        // Set the transport manager. This will initialize the DiscoveryManager
        rcm.setTransportManager(tm);

        // Process the common elements in TransportManagerConfig
        processTransportManagerConfig(tmConfig, tm);
    }

    /**
     * INTERNAL:
     */
    protected void buildUserDefinedTransportManagerConfig(UserDefinedTransportManagerConfig tmConfig, RemoteCommandManager rcm) {
        TransportManager tm = null;

        // Transport class
        String transportManagerClassName = tmConfig.getTransportClass();
        if (transportManagerClassName != null) {
            try {
                Class transportManagerClass = m_classLoader.loadClass(transportManagerClassName);
                if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                    tm = (TransportManager)AccessController.doPrivileged(new PrivilegedNewInstanceFromClass(transportManagerClass));
                }else{
                    tm = (TransportManager)PrivilegedAccessHelper.newInstanceFromClass(transportManagerClass);
                }
            } catch (Throwable exception) {
                throw SessionLoaderException.failedToLoadTag("transport-class", transportManagerClassName, exception);
            }

            // Set the transport manager. This will initialize the DiscoveryManager
            rcm.setTransportManager(tm);

            // Process the common elements in TransportManagerConfig
            processTransportManagerConfig(tmConfig, tm);
        }
    }

    /**
     * INTERNAL:
     */
    protected void processJNDINamingServiceConfig(JNDINamingServiceConfig namingConfig, TransportManager tm) {
        // URL
        String url = namingConfig.getURL();
        if (url != null) {
            tm.getRemoteCommandManager().setUrl(url);
        }

        // Username - XML Schema default is admin
        tm.setUserName(namingConfig.getUsername());

        // Encryption class - XML Schema default is org.eclipse.persistence.internal.security.JCEEncryptor
        tm.setEncryptionClassName(namingConfig.getEncryptionClass());

        // Password - XML Schema default is password
        tm.setEncryptedPassword(namingConfig.getEncryptedPassword());

        // Initial context factory name - XML Schema is weblogic.jndi.WLInitialContextFactory
        tm.setInitialContextFactoryName(namingConfig.getInitialContextFactoryName());

        // Properties (assumes they are all valid)
        Enumeration e = namingConfig.getPropertyConfigs().elements();

        while (e.hasMoreElements()) {
            PropertyConfig propertyConfig = (PropertyConfig)e.nextElement();
            tm.getRemoteContextProperties().put(propertyConfig.getName(), propertyConfig.getValue());
        }
    }

    /**
     * INTERNAL:
     */
    protected void processRMIRegistryNamingServiceConfig(RMIRegistryNamingServiceConfig namingConfig, TransportManager tm) {
        // URL
        tm.getRemoteCommandManager().setUrl(namingConfig.getURL());
    }

    /**
     * INTERNAL:
     */
    protected void processDiscoveryConfig(DiscoveryConfig discoveryConfig, DiscoveryManager discoveryManager) {
        // Multicast group address - XML Schema default is 226.10.12.64
        discoveryManager.setMulticastGroupAddress(discoveryConfig.getMulticastGroupAddress());

        // Mutlicast port - XML Schema default is 3121
        discoveryManager.setMulticastPort(discoveryConfig.getMulticastPort());

        // Announcement delay - XML Schema default is 1000
        discoveryManager.setAnnouncementDelay(discoveryConfig.getAnnouncementDelay());

        //Packet time-to-live - XML Schema default is 2
        discoveryManager.setPacketTimeToLive(discoveryConfig.getPacketTimeToLive());
    }

    /**
     * INTERNAL:
     */
    protected void processTransportManagerConfig(TransportManagerConfig tmConfig, TransportManager tm) {
        // On connection error - Can only be DiscardConnection (true) or 
        // KeepConnection (false), validated by the schema
        // XML Schema default is DiscardConnection
        tm.setShouldRemoveConnectionOnError(tmConfig.getOnConnectionError().equals("DiscardConnection"));
    }

    /**
     * INTERNAL:
     */
    protected void processSessionEventManagerConfig(SessionEventManagerConfig sessionEventManagerConfig, AbstractSession session) {
        if (sessionEventManagerConfig != null) {
            Enumeration e = sessionEventManagerConfig.getSessionEventListeners().elements();

            while (e.hasMoreElements()) {
                String listenerClassName = (String)e.nextElement();

                try {
                    Class listenerClass = m_classLoader.loadClass(listenerClassName);
                    if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                        session.getEventManager().addListener((SessionEventListener)AccessController.doPrivileged(new PrivilegedNewInstanceFromClass(listenerClass)));
                    }else{
                        
                    }
                    session.getEventManager().addListener((SessionEventListener)PrivilegedAccessHelper.newInstanceFromClass(listenerClass));
                } catch (Exception exception) {
                    throw SessionLoaderException.failedToLoadTag("event-listener-class", listenerClassName, exception);
                }
            }
        }
    }

    /**
     * INTERNAL:
     */
    protected SessionLog buildSessionLog(LogConfig logConfig, AbstractSession session) {
        if (logConfig instanceof JavaLogConfig) {
            return buildJavaLogConfig((JavaLogConfig)logConfig, session);
        } else if (logConfig instanceof DefaultSessionLogConfig) {
            return buildDefaultSessionLogConfig((DefaultSessionLogConfig)logConfig);
        } else if (logConfig instanceof ServerLogConfig) {
            return buildServerLogConfig((ServerLogConfig)logConfig, session);
        } else {
            return null;
        }
    }

    /**
     * INTERNAL:
     */
    protected SessionLog buildJavaLogConfig(JavaLogConfig javaLogConfig, AbstractSession session) {
        SessionLog javaLog = null;
        try {
            // use ConversionManager to avoid loading the JDK 1.4 class unless it is needed.
            ConversionManager conversionManager = new ConversionManager();
            conversionManager.setLoader(getClass().getClassLoader());
            javaLog = (SessionLog)((Class)conversionManager.convertObject("org.eclipse.persistence.logging.JavaLog", Class.class)).newInstance();
            javaLog.setSession(session);
        } catch (Exception exception) {
            throw ValidationException.unableToLoadClass("org.eclipse.persistence.logging.JavaLog", exception);
        }

        // Process the common elements from LogConfig
        processLogConfig(javaLogConfig, javaLog);

        return javaLog;
    }

    /**
     * INTERNAL:
     * Wrapped by the getSessionLog() call, therefore, config can't be null at
     * this point.
     */
    protected SessionLog buildDefaultSessionLogConfig(DefaultSessionLogConfig defaultSessionLogConfig) {
        DefaultSessionLog defaultSessionLog = new DefaultSessionLog();

        // Log level - XML Schema default is info
        defaultSessionLog.setLevel(((Integer)m_logLevels.get(defaultSessionLogConfig.getLogLevel())).intValue());

        // Filename - setWriter will handle nulls
        defaultSessionLog.setWriter(defaultSessionLogConfig.getFilename());

        // Process the common elements from LogConfig
        processLogConfig(defaultSessionLogConfig, defaultSessionLog);

        return defaultSessionLog;
    }

    /**
     * INTERNAL:
     */
    protected SessionLog buildServerLogConfig(ServerLogConfig serverLogConfig, AbstractSession session) {
        SessionLog serverLog = ((DatabaseSessionImpl)session).getServerPlatform().getServerLog();

        return serverLog;
    }

    /**
     * INTERNAL:
     */
    protected void processLogConfig(LogConfig logConfig, SessionLog log) {
        if (logConfig.getLoggingOptions() != null) {
            if (logConfig.getLoggingOptions().getShouldLogExceptionStackTrace() != null) {
                log.setShouldLogExceptionStackTrace(logConfig.getLoggingOptions().getShouldLogExceptionStackTrace().booleanValue());
            }
            if (logConfig.getLoggingOptions().getShouldPrintConnection() != null) {
                log.setShouldPrintConnection(logConfig.getLoggingOptions().getShouldPrintConnection().booleanValue());
            }
            if (logConfig.getLoggingOptions().getShouldPrintDate() != null) {
                log.setShouldPrintDate(logConfig.getLoggingOptions().getShouldPrintDate().booleanValue());
            }
            if (logConfig.getLoggingOptions().getShouldPrintSession() != null) {
                log.setShouldPrintSession(logConfig.getLoggingOptions().getShouldPrintSession().booleanValue());
            }
            if (logConfig.getLoggingOptions().getShouldPrintThread() != null) {
                log.setShouldPrintThread(logConfig.getLoggingOptions().getShouldPrintThread().booleanValue());
            }
        }
    }


    /**
     * INTERNAL:
     * Builds a Sun CORBA transport manager with the given remote command manager
     */
    protected void buildSunCORBATransportManagerConfig(SunCORBATransportManagerConfig tmConfig, RemoteCommandManager rcm) {
        SunCORBATransportManager tm = new SunCORBATransportManager(rcm);

        // Set the transport manager. This will initialize the DiscoveryManager
        rcm.setTransportManager(tm);

        // Process the common elements in TransportManagerConfig
        processTransportManagerConfig(tmConfig, tm);
    }

    /**
     * INTERNAL:
     */
    protected void processCommandsConfig(CommandsConfig commandsConfig, RemoteCommandManager rcm) {
        if (commandsConfig != null) {
            // cache-sync - XML Schema default is false
            ((AbstractSession)rcm.getCommandProcessor()).setShouldPropagateChanges(commandsConfig.getCacheSync());
        }
    }

    /**
     * INTERNAL:
     * Builds a session broker from the given SessionBrokerConfig.
     */
    protected SessionBroker buildSessionBrokerConfig(SessionBrokerConfig sessionBrokerConfig) {
        SessionBroker sessionBroker = new SessionBroker();

        // Session names
        Enumeration sessionNames = sessionBrokerConfig.getSessionNames().elements();

        while (sessionNames.hasMoreElements()) {
            // Register the sessions
            String sessionName = (String)sessionNames.nextElement();
            sessionBroker.registerSession(sessionName, (AbstractSession)m_sessions.get(sessionName));
        }

        // Process the common elements in SessionConfig
        processSessionConfig(sessionBrokerConfig, sessionBroker);

        return sessionBroker;
    }
}
