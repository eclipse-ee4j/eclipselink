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

import org.eclipse.persistence.sessions.factories.SessionManager;
import org.eclipse.persistence.sessions.SessionProfiler;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sessions.Login;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.DatasourceLogin;
import org.eclipse.persistence.sessions.SessionEventListener;
import org.eclipse.persistence.logging.DefaultSessionLog;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.internal.databaseaccess.DatasourcePlatform;
import org.eclipse.persistence.sessions.JNDIConnector;
import org.eclipse.persistence.eis.*;
import org.eclipse.persistence.sessions.remote.*;
import org.eclipse.persistence.sessions.remote.jms.*;
import org.eclipse.persistence.sessions.coordination.*;
import org.eclipse.persistence.sessions.coordination.jms.JMSTopicTransportManager;
import org.eclipse.persistence.sessions.coordination.rmi.RMITransportManager;
import org.eclipse.persistence.sessions.server.*;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.sessions.factories.XMLProjectReader;
import org.eclipse.persistence.sessions.remote.CacheSynchronizationManager;
import org.eclipse.persistence.internal.localization.ExceptionLocalization;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.logging.*;
import org.eclipse.persistence.Version;
import org.eclipse.persistence.internal.helper.ConversionManager;
import org.eclipse.persistence.platform.server.NoServerPlatform;
import org.eclipse.persistence.platform.xml.XMLParser;
import org.eclipse.persistence.platform.xml.XMLPlatform;
import org.eclipse.persistence.platform.xml.XMLPlatformException;
import org.eclipse.persistence.platform.xml.XMLPlatformFactory;
import org.eclipse.persistence.platform.server.CustomServerPlatform;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.DatabaseSessionImpl;

import org.w3c.dom.*;
import java.io.InputStreamReader;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.*;

import org.xml.sax.*;
import java.lang.reflect.*;

/**
 * <p><b>Purpose</b>: Provide a mechanism for loading configuration XML files, as classpath resources,
 * and processing them.  Processing is done reflectivly based on the name
 * of the XML Tag
 *
 * @since TopLink 4.0
 * @author Gordon Yorke
 * @deprecated As of version 10.1.3. Replaced by XMLSessionConfigLoader
 */
public class XMLLoader {
    protected String resourceName;
    protected ClassLoader classLoader;
    protected Vector exceptionStore;
    protected boolean shouldLoginSession;
    protected SessionManager sessionManager;
    protected boolean shouldRefreshSession;
    protected AbstractSession currentlyBuildingSession;
    // Guy
    protected static final String DEFAULT_RESOURCE_NAME = "sessions.xml";
    protected static final String DEFAULT_RESOURCE_NAME_IN_META_INF = "META-INF/sessions.xml";

    /** This variable will be set to true once the configuration resource file in this loader has been processed */
    protected boolean hasBeenRetreived;

    //Bug2792096  Stores anything that needs to be logged in session log
    protected Vector messageStore;
    protected EntityResolver m_entityResolver;

    /**
     * PUBLIC:
     * This constructor is used when the file resource named 'sessions.xml' will be parsed for configuration.
     */
    public XMLLoader() {
        // Guy
        this(DEFAULT_RESOURCE_NAME);
    }

    /**
     * PUBLIC:
     * This constructor is used when passing in the resource name of the configuration file that should be parsed
     */
    public XMLLoader(String resourceName) {
        this.exceptionStore = new Vector();
        this.resourceName = resourceName;
        this.shouldRefreshSession = false;
        this.shouldLoginSession = true;
        //Bug2792096 Stores anything that needs to be logged in session log
        this.messageStore = new Vector();

        m_entityResolver = new PersistenceEntityResolver();
    }

    /**
     * INTERNAL:
     */
    public Vector getExceptionStore() {
        return this.exceptionStore;
    }

    /**
     * INTERNAL:
     */

    //Bug2792096 Stores anything that needs to be logged in session log
    public Vector getMessageStore() {
        return this.messageStore;
    }

    /**
     * INTERNAL:
     */
    public String getResourceName() {
        return this.resourceName;
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

    public void load(final SessionManager sessionManager, final ClassLoader loader, final boolean shouldLoginSession, final boolean shouldRefreshSession) throws EclipseLinkException {
        if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()) {
            AccessController.doPrivileged(new PrivilegedAction() {
                    public Object run() {
                        loadInternal(sessionManager, loader, shouldLoginSession, shouldRefreshSession);
                        return null;
                    }
                });
        } else {
            loadInternal(sessionManager, loader, shouldLoginSession, shouldRefreshSession);
        }
    }

    /**
     * INTERNAL:
     * This is the method that starts the processing.  It determines if the file resource should be read again
     * or not.
     */
    public void loadInternal(SessionManager sessionManager, ClassLoader loader, boolean shouldLoginSession, boolean shouldRefreshSession) throws EclipseLinkException {
        this.sessionManager = sessionManager;
        this.shouldLoginSession = shouldLoginSession;
        this.shouldRefreshSession = shouldRefreshSession;
        if (this.classLoader != loader) {
            this.classLoader = loader;
            this.hasBeenRetreived = false;
        }
        if (this.hasBeenRetreived && !this.shouldRefreshSession) {
            //We have allready processed this file resource there is no need to process it again.
            return;
        }
        Document document = retreiveDOM(loader);

        //CR4142 Add a null pointer check. 
        if (document != null) {
            processRootTag(document, null, loader);
        }
        if (!getExceptionStore().isEmpty()) {
            throw SessionLoaderException.finalException(getExceptionStore());
        }
    }

    /**
     * INTERNAL:
     * This method instantiates the parser and builds the document.
     * The entity resolver is here to retrieve the schema from our classpath for the parser
     */
    public org.w3c.dom.Document retreiveDOM(ClassLoader loader) throws EclipseLinkException {
        //Retrieve the file resource from the classpath and load as a stream
        java.io.InputStream inStream = loader.getResourceAsStream(this.resourceName);

        //CR 3317
        if (inStream == null) {
            // sessions.xml not found, try META-INF/sessions.xml
            if (this.resourceName.equals(DEFAULT_RESOURCE_NAME)) {
                inStream = loader.getResourceAsStream(DEFAULT_RESOURCE_NAME_IN_META_INF);
            }
            
            if (inStream == null) {
                throw ValidationException.noSessionsXMLFound(this.resourceName);
            }
        }

        //CR4142  Register an error handler with parser to make validation work.  Add SAXException 
        //  to exceptionStore when caught.
        try {
            //Bug2631348  Only UTF-8 is supported
            InputStreamReader inputReader = new InputStreamReader(inStream, "UTF-8");

            // BUG # 2612131: remove dependencies on Xerces
            XMLPlatform xmlPlatform = XMLPlatformFactory.getInstance().getXMLPlatform();
            XMLParser xmlParser = xmlPlatform.newXMLParser();
            if (SessionManager.shouldPerformDTDValidation()) {
                xmlParser.setValidationMode(XMLParser.DTD_VALIDATION);
            }
            xmlParser.setNamespaceAware(false);
            xmlParser.setWhitespacePreserving(false);
            xmlParser.setErrorHandler(new XMLLoaderErrorHandler());
            xmlParser.setEntityResolver(m_entityResolver);

            return xmlParser.parse(inputReader);
        } catch (XMLPlatformException e) {
            // Test if a SAXParseException is contained
            if (e.getInternalException() instanceof SAXParseException) {
                SAXParseException saxParseException = (SAXParseException)e.getInternalException();
                getExceptionStore().add(SessionLoaderException.failedToParseXML(saxParseException.getMessage(), saxParseException.getLineNumber(), saxParseException.getColumnNumber(), saxParseException));
                return null;
            }
            getExceptionStore().add(SessionLoaderException.failedToParseXML(e.getMessage(), e));
            return null;
        } catch (Exception exception) {
            // An IO or other Exception has been thrown
            getExceptionStore().add(SessionLoaderException.nonParseException(exception));
            return null;
        }
    }

    /**
     * INTERNAL:
     * This method is the start the of the reflective recursion to process the document
     * THis method is intended to process the root node\
     */
    public void processRootTag(Node node, ObjectHolder sessionHolder, ClassLoader loader) {
        sessionHolder = new ObjectHolder();
        node = node.getLastChild();
        NodeListElementEnumerator list = new NodeListElementEnumerator(node.getChildNodes());
        while (list.hasMoreNodes()) {
            Node childNode = list.nextNode();
            try {
                Class[] args = { Node.class, ObjectHolder.class };
                java.lang.reflect.Method method = this.getClass().getMethod(convertNodeToMethodName(childNode), args);
                Object[] objectList = { childNode, sessionHolder };
                method.invoke(this, objectList);
            } catch (Exception exception) {
                getExceptionStore().add(SessionLoaderException.unkownTagAtNode(childNode.getNodeName(), node.getNodeName(), exception));
                continue;
            }
            if (getExceptionStore().isEmpty()) {
                // If the session is not allready loaded, then add to session Manager
                if (!sessionManager.getSessions().containsKey(((AbstractSession)sessionHolder.getObject()).getName())) {
                    sessionManager.addSession((AbstractSession)sessionHolder.getObject());
                    ((AbstractSession)sessionHolder.getObject()).getDatasourcePlatform().getConversionManager().setLoader(loader);
                }
            }
        }
    }

    /**
     * INTERNAL:
     */
    public void process_cache_synchronization_manager_Tag(Node node, ObjectHolder sessionHolder) {
        ((AbstractSession)sessionHolder.getObject()).setCacheSynchronizationManager(new CacheSynchronizationManager());
        NodeListElementEnumerator list = new NodeListElementEnumerator(node.getChildNodes());
        while (list.hasMoreNodes()) {
            Node childNode = list.nextNode();
            try {
                Class[] args = { Node.class, ObjectHolder.class };
                java.lang.reflect.Method method = this.getClass().getMethod(convertNodeToMethodName(childNode), args);
                Object[] objectList = { childNode, sessionHolder };
                method.invoke(this, objectList);
            } catch (Exception exception) {
                getExceptionStore().add(SessionLoaderException.unkownTagAtNode(childNode.getNodeName(), node.getNodeName(), exception));
            }
        }
    }

    /**
     * INTERNAL:
     */
    public void process_connection_pool_Tag(Node node, ObjectHolder sessionHolder) {
        ObjectHolder poolHolder = new ObjectHolder();
        NodeListElementEnumerator list = new NodeListElementEnumerator(node.getChildNodes());
        process_is_read_connection_pool_Tag(list.nextNode(), poolHolder);
        String poolName = list.nextNode().getFirstChild().getNodeValue();
        while (list.hasMoreNodes()) {
            Node childNode = list.nextNode();
            try {
                Class[] args = { Node.class, ObjectHolder.class };
                java.lang.reflect.Method method = this.getClass().getMethod(convertNodeToMethodName(childNode), args);
                Object[] objectList = { childNode, poolHolder };
                method.invoke(this, objectList);
            } catch (Exception exception) {
                getExceptionStore().add(SessionLoaderException.unkownTagAtNode(childNode.getNodeName(), node.getNodeName(), exception));
            }
        }
        ((ConnectionPool)poolHolder.getObject()).setName(poolName);
        ((ServerSession)sessionHolder.getObject()).addConnectionPool((ConnectionPool)poolHolder.getObject());
    }

    /**
     * INTERNAL:
     */
    public void process_is_read_connection_pool_Tag(Node node, ObjectHolder poolHolder) {
        ConnectionPool connectionPool = null;
        if (node.getFirstChild().getNodeValue().equals("true")) {
            connectionPool = new ReadConnectionPool();
        } else {
            connectionPool = new ConnectionPool();
        }
        connectionPool.setLogin((DatabaseLogin)this.currentlyBuildingSession.getProject().getDatasourceLogin().clone());
        poolHolder.setObject(connectionPool);
    }

    /**
     * INTERNAL:
     */
    public void process_max_connections_Tag(Node node, ObjectHolder poolHolder) {
        try {
            int maxConnections = Integer.parseInt(node.getFirstChild().getNodeValue());
            ((ConnectionPool)poolHolder.getObject()).setMaxNumberOfConnections(maxConnections);
        } catch (Exception exception) {
            getExceptionStore().add(SessionLoaderException.failedToLoadTag(node.getNodeName(), node.getFirstChild().getNodeValue(), exception));
        }
    }

    /**
     * INTERNAL:
     */
    public void process_min_connections_Tag(Node node, ObjectHolder poolHolder) {
        try {
            int minConnections = Integer.parseInt(node.getFirstChild().getNodeValue());
            ((ConnectionPool)poolHolder.getObject()).setMinNumberOfConnections(minConnections);
        } catch (Exception exception) {
            getExceptionStore().add(SessionLoaderException.failedToLoadTag(node.getNodeName(), node.getFirstChild().getNodeValue(), exception));
        }
    }

    /**
     * INTERNAL:
     */
    public void process_login_Tag(Node node, ObjectHolder parentHolder) {
        ObjectHolder loginHolder = new ObjectHolder();
        if (parentHolder.getObject() instanceof AbstractSession) {
            loginHolder.setObject(((AbstractSession)parentHolder.getObject()).getProject().getDatasourceLogin());
        } else {
            loginHolder.setObject(((ConnectionPool)parentHolder.getObject()).getLogin());
        }
        NodeListElementEnumerator list = new NodeListElementEnumerator(node.getChildNodes());
        while (list.hasMoreNodes()) {
            Node childNode = list.nextNode();
            try {
                Class[] args = { Node.class, ObjectHolder.class };
                java.lang.reflect.Method method = this.getClass().getMethod(convertNodeToMethodName(childNode), args);
                Object[] objectList = { childNode, loginHolder };
                method.invoke(this, objectList);
            } catch (Exception exception) {
                getExceptionStore().add(SessionLoaderException.unkownTagAtNode(childNode.getNodeName(), node.getNodeName(), exception));
            }
        }
        Login login = (Login)loginHolder.getObject();

        // Reset login as it may have changed.
        if (parentHolder.getObject() instanceof AbstractSession) {
            ((AbstractSession)parentHolder.getObject()).getProject().setLogin(login);
            if (parentHolder.getObject() instanceof ServerSession) {
                // As the connection pools have already been created, their logins must be reset.
                ServerSession session = ((ServerSession)parentHolder.getObject());
                Login readLogin = login;
                if (login instanceof DatabaseLogin) {
                    if (session.getDefaultConnectionPool().getLogin() != session.getReadConnectionPool().getLogin()) {
                        if (session.getReadConnectionPool().getLogin() instanceof DatabaseLogin) {
                            DatabaseLogin readDatabaseLogin = (DatabaseLogin)session.getReadConnectionPool().getLogin();
                            DatabaseLogin loginClone = (DatabaseLogin)login.clone();
                            loginClone.setConnector(readDatabaseLogin.getConnector());
                            loginClone.setUsesExternalTransactionController(readDatabaseLogin.shouldUseExternalTransactionController());
                            readLogin = loginClone;
                        }
                    }
                }
                session.getReadConnectionPool().setLogin(readLogin);
                session.getDefaultConnectionPool().setLogin(login);
            }
        } else {
            ((ConnectionPool)parentHolder.getObject()).setLogin(login);
        }
    }

    /**
     * INTERNAL:
     */
    public void process_license_path_Tag(Node node, ObjectHolder loginHolder) {
        //n/a
    }

    /**
     * INTERNAL:
     */
    public void process_datasource_Tag(Node node, ObjectHolder loginHolder) {
        try {
            String sourceName = node.getFirstChild().getNodeValue();
            ((DatabaseLogin)loginHolder.getObject()).setConnector(new JNDIConnector(new javax.naming.InitialContext(), sourceName));
        } catch (Exception exception) {
            getExceptionStore().add(SessionLoaderException.failedToLoadTag(node.getNodeName(), node.getFirstChild().getNodeValue(), exception));
        }
    }

    /**
     * INTERNAL:
     */
    public void process_non_jts_datasource_Tag(Node node, ObjectHolder loginHolder) {
        try {
            String sourceName = node.getFirstChild().getNodeValue();
            JNDIConnector connector = new JNDIConnector(new javax.naming.InitialContext(), sourceName);
            if (this.currentlyBuildingSession instanceof ServerSession) {
                DatabaseLogin login = (DatabaseLogin)((ServerSession)this.currentlyBuildingSession).getReadConnectionPool().getLogin();
                login = (DatabaseLogin)login.clone();
                login.setConnector(connector);
                login.setUsesExternalTransactionController(false);
                ((ServerSession)this.currentlyBuildingSession).getReadConnectionPool().setLogin(login);
            }
        } catch (Exception exception) {
            getExceptionStore().add(SessionLoaderException.failedToLoadTag(node.getNodeName(), node.getFirstChild().getNodeValue(), exception));
        }
    }

    /**
     * INTERNAL:
     */
    public void process_session_broker_Tag(Node node, ObjectHolder sessionHolder) {
        NodeListElementEnumerator list = new NodeListElementEnumerator(node.getChildNodes());
        SessionBrokerPlaceHolder placeHolder = new SessionBrokerPlaceHolder();
        String name = list.nextNode().getFirstChild().getNodeValue();
        placeHolder.setName(name);
        sessionHolder.setObject(placeHolder);
        while (list.hasMoreNodes()) {
            Node childNode = list.nextNode();
            try {
                Class[] args = { Node.class, ObjectHolder.class };
                java.lang.reflect.Method method = this.getClass().getMethod(convertNodeToMethodName(childNode), args);
                Object[] objectList = { childNode, sessionHolder };
                method.invoke(this, objectList);
            } catch (Exception exception) {
                getExceptionStore().add(SessionLoaderException.unkownTagAtNode(childNode.getNodeName(), node.getNodeName(), exception));
            }
        }
    }

    /**
     * INTERNAL:
     */
    public void process_session_Tag(Node node, ObjectHolder sessionHolder) {
        NodeListElementEnumerator list = new NodeListElementEnumerator(node.getChildNodes());
        String name = list.nextNode().getFirstChild().getNodeValue();
        Node projectNode = list.nextNode();
        ObjectHolder projectHolder = new ObjectHolder();

        // process the project tags
        try {
            Class[] args = { Node.class, ObjectHolder.class };
            java.lang.reflect.Method method = this.getClass().getMethod(convertNodeToMethodName(projectNode), args);
            Object[] objectList = { projectNode, projectHolder };
            method.invoke(this, objectList);
        } catch (Exception exception) {
            getExceptionStore().add(SessionLoaderException.unkownTagAtNode(projectNode.getNodeName(), node.getNodeName(), exception));
        }

        // BUG#2669260 if an error occur in the project, it must still be set or a nullpointer will occur.
        if (projectHolder.getObject() == null) {
            projectHolder.setObject(new Project());
        }
        process_session_type_Tag(list.nextNode(), projectHolder);
        // the session type method will create a session and place it in the object holder
        sessionHolder.setObject(projectHolder.getObject());
        this.currentlyBuildingSession = (AbstractSession)projectHolder.getObject();
        this.currentlyBuildingSession.setName(name);
        while (list.hasMoreNodes()) {
            Node childNode = list.nextNode();
            try {
                Class[] args = { Node.class, ObjectHolder.class };
                java.lang.reflect.Method method = this.getClass().getMethod(convertNodeToMethodName(childNode), args);
                Object[] objectList = { childNode, sessionHolder };
                method.invoke(this, objectList);
            } catch (Exception exception) {
                getExceptionStore().add(SessionLoaderException.unkownTagAtNode(childNode.getNodeName(), node.getNodeName(), exception));
            }
        }

        //Bug2792096  Anything stored in messageStore is logged in session log
        if (((AbstractSession)sessionHolder.getObject()).shouldLog(SessionLog.WARNING, null)) {
            for (Enumeration enumtr = getMessageStore().elements(); enumtr.hasMoreElements();) {
                Object ob = enumtr.nextElement();
                if (ob instanceof Throwable) {
                    Throwable ex = (Throwable)enumtr.nextElement();
                    ((AbstractSession)sessionHolder.getObject()).logThrowable(SessionLog.WARNING, null, ex);
                } else {
                    String st = (String)enumtr.nextElement();
                    ((AbstractSession)sessionHolder.getObject()).warning(st, null);
                }
            }
        }
    }

    /**
     * INTERNAL:
     */
    public void process_uses_byte_array_binding_Tag(Node node, ObjectHolder loginHolder) {
        try {
            Boolean bool = Boolean.valueOf(node.getFirstChild().getNodeValue());
            ((DatabaseLogin)loginHolder.getObject()).setUsesByteArrayBinding(bool.booleanValue());
        } catch (Exception exception) {
            getExceptionStore().add(SessionLoaderException.failedToLoadTag(node.getNodeName(), node.getFirstChild().getNodeValue(), exception));
        }
    }

    /**
     * INTERNAL:
     */
    public void process_uses_string_binding_Tag(Node node, ObjectHolder loginHolder) {
        try {
            Boolean bool = Boolean.valueOf(node.getFirstChild().getNodeValue());
            ((DatabaseLogin)loginHolder.getObject()).setUsesStringBinding(bool.booleanValue());
        } catch (Exception exception) {
            getExceptionStore().add(SessionLoaderException.failedToLoadTag(node.getNodeName(), node.getFirstChild().getNodeValue(), exception));
        }
    }

    /**
     * INTERNAL:
     */
    public void process_uses_streams_for_binding_Tag(Node node, ObjectHolder loginHolder) {
        try {
            Boolean bool = Boolean.valueOf(node.getFirstChild().getNodeValue());
            ((DatabaseLogin)loginHolder.getObject()).setUsesStreamsForBinding(bool.booleanValue());
        } catch (Exception exception) {
            getExceptionStore().add(SessionLoaderException.failedToLoadTag(node.getNodeName(), node.getFirstChild().getNodeValue(), exception));
        }
    }

    /**
     * INTERNAL:
     */
    public void process_should_force_field_names_to_uppercase_Tag(Node node, ObjectHolder loginHolder) {
        try {
            Boolean bool = Boolean.valueOf(node.getFirstChild().getNodeValue());
            ((DatabaseLogin)loginHolder.getObject()).setShouldForceFieldNamesToUpperCase(bool.booleanValue());
        } catch (Exception exception) {
            getExceptionStore().add(SessionLoaderException.failedToLoadTag(node.getNodeName(), node.getFirstChild().getNodeValue(), exception));
        }
    }

    /**
     * INTERNAL:
     */
    public void process_should_optimize_data_conversion_Tag(Node node, ObjectHolder loginHolder) {
        try {
            Boolean bool = Boolean.valueOf(node.getFirstChild().getNodeValue());
            ((DatabaseLogin)loginHolder.getObject()).setShouldOptimizeDataConversion(bool.booleanValue());
        } catch (Exception exception) {
            getExceptionStore().add(SessionLoaderException.failedToLoadTag(node.getNodeName(), node.getFirstChild().getNodeValue(), exception));
        }
    }

    /**
     * INTERNAL:
     */
    public void process_uses_native_sql_Tag(Node node, ObjectHolder loginHolder) {
        try {
            Boolean bool = Boolean.valueOf(node.getFirstChild().getNodeValue());
            ((DatabaseLogin)loginHolder.getObject()).setUsesNativeSQL(bool.booleanValue());
        } catch (Exception exception) {
            getExceptionStore().add(SessionLoaderException.failedToLoadTag(node.getNodeName(), node.getFirstChild().getNodeValue(), exception));
        }
    }

    /**
     * INTERNAL:
     */
    public void process_should_trim_strings_Tag(Node node, ObjectHolder loginHolder) {
        try {
            Boolean bool = Boolean.valueOf(node.getFirstChild().getNodeValue());
            ((DatabaseLogin)loginHolder.getObject()).setShouldTrimStrings(bool.booleanValue());
        } catch (Exception exception) {
            getExceptionStore().add(SessionLoaderException.failedToLoadTag(node.getNodeName(), node.getFirstChild().getNodeValue(), exception));
        }
    }

    /**
     * INTERNAL:
     */
    public void process_uses_batch_writing_Tag(Node node, ObjectHolder loginHolder) {
        try {
            Boolean bool = Boolean.valueOf(node.getFirstChild().getNodeValue());
            ((DatabaseLogin)loginHolder.getObject()).setUsesBatchWriting(bool.booleanValue());
        } catch (Exception exception) {
            getExceptionStore().add(SessionLoaderException.failedToLoadTag(node.getNodeName(), node.getFirstChild().getNodeValue(), exception));
        }
    }

    /**
     * INTERNAL:
     */
    public void process_uses_jdbc20_batch_writing_Tag(Node node, ObjectHolder loginHolder) {
        try {
            Boolean bool = Boolean.valueOf(node.getFirstChild().getNodeValue());
            ((DatabaseLogin)loginHolder.getObject()).setUsesJDBCBatchWriting(bool.booleanValue());
        } catch (Exception exception) {
            getExceptionStore().add(SessionLoaderException.failedToLoadTag(node.getNodeName(), node.getFirstChild().getNodeValue(), exception));
        }
    }

    /**
     * INTERNAL:
     */
    public void process_uses_external_transaction_controller_Tag(Node node, ObjectHolder loginHolder) {
        try {
            Boolean bool = Boolean.valueOf(node.getFirstChild().getNodeValue());
            ((DatasourceLogin)loginHolder.getObject()).setUsesExternalTransactionController(bool.booleanValue());
        } catch (Exception exception) {
            getExceptionStore().add(SessionLoaderException.failedToLoadTag(node.getNodeName(), node.getFirstChild().getNodeValue(), exception));
        }
    }

    /**
      * INTERNAL:
     */
    public void process_uses_external_connection_pool_Tag(Node node, ObjectHolder loginHolder) {
        try {
            Boolean bool = Boolean.valueOf(node.getFirstChild().getNodeValue());
            ((DatasourceLogin)loginHolder.getObject()).setUsesExternalConnectionPooling(bool.booleanValue());
        } catch (Exception exception) {
            getExceptionStore().add(SessionLoaderException.failedToLoadTag(node.getNodeName(), node.getFirstChild().getNodeValue(), exception));
        }
    }

    /**
     * INTERNAL:
     */
    public void process_should_cache_all_statements_Tag(Node node, ObjectHolder loginHolder) {
        try {
            Boolean bool = Boolean.valueOf(node.getFirstChild().getNodeValue());
            ((DatabaseLogin)loginHolder.getObject()).setShouldCacheAllStatements(bool.booleanValue());
        } catch (Exception exception) {
            getExceptionStore().add(SessionLoaderException.failedToLoadTag(node.getNodeName(), node.getFirstChild().getNodeValue(), exception));
        }
    }

    /**
     * INTERNAL:
     */
    public void process_should_bind_all_parameters_Tag(Node node, ObjectHolder loginHolder) {
        try {
            Boolean bool = Boolean.valueOf(node.getFirstChild().getNodeValue());
            ((DatabaseLogin)loginHolder.getObject()).setShouldBindAllParameters(bool.booleanValue());
        } catch (Exception exception) {
            getExceptionStore().add(SessionLoaderException.failedToLoadTag(node.getNodeName(), node.getFirstChild().getNodeValue(), exception));
        }
    }

    /**
     * INTERNAL:
     */
    public void process_connection_spec_class_Tag(Node node, ObjectHolder loginHolder) {
        try {
            Class specClass = this.classLoader.loadClass(node.getFirstChild().getNodeValue());
            EISConnectionSpec spec = (EISConnectionSpec)specClass.newInstance();
            ((EISLogin)loginHolder.getObject()).setConnectionSpec(spec);
        } catch (Exception exception) {
            getExceptionStore().add(SessionLoaderException.failedToLoadTag(node.getNodeName(), node.getFirstChild().getNodeValue(), exception));
        }
    }

    /**
     * INTERNAL:
     */
    public void process_connection_factory_url_Tag(Node node, ObjectHolder loginHolder) {
        try {
            ((EISLogin)loginHolder.getObject()).setConnectionFactoryURL((String)node.getFirstChild().getNodeValue());
        } catch (Exception exception) {
            getExceptionStore().add(SessionLoaderException.failedToLoadTag(node.getNodeName(), node.getFirstChild().getNodeValue(), exception));
        }
    }

    /**
     * INTERNAL:
     */
    public void process_uses_native_sequencing_Tag(Node node, ObjectHolder loginHolder) {
        try {
            Boolean bool = Boolean.valueOf(node.getFirstChild().getNodeValue());
            ((DatabaseLogin)loginHolder.getObject()).setUsesNativeSequencing(bool.booleanValue());
        } catch (Exception exception) {
            getExceptionStore().add(SessionLoaderException.failedToLoadTag(node.getNodeName(), node.getFirstChild().getNodeValue(), exception));
        }
    }

    /**
     * INTERNAL:
     * Obsolete.  Map this tag to the new log levels
     */
    public void process_enable_logging_Tag(Node node, ObjectHolder sessionHolder) {
        try {
            Boolean bool = Boolean.valueOf(node.getFirstChild().getNodeValue());
            ((AbstractSession)sessionHolder.getObject()).setShouldLogMessages(bool.booleanValue());
        } catch (Exception exception) {
            getExceptionStore().add(SessionLoaderException.failedToLoadTag(node.getNodeName(), node.getFirstChild().getNodeValue(), exception));
        }
    }

    /**
     * INTERNAL:
     */
    public void process_driver_class_Tag(Node node, ObjectHolder loginHolder) {
        try {
            Class driverClass = this.classLoader.loadClass(node.getFirstChild().getNodeValue());
            ((DatabaseLogin)loginHolder.getObject()).setDriverClass(driverClass);
        } catch (Exception exception) {
            getExceptionStore().add(SessionLoaderException.failedToLoadTag(node.getNodeName(), node.getFirstChild().getNodeValue(), exception));
        }
    }

    /**
     * INTERNAL:
     */
    public void process_event_listener_class_Tag(Node node, ObjectHolder sessionHolder) {
        try {
            Class listenerClass = this.classLoader.loadClass(node.getFirstChild().getNodeValue());
            ((AbstractSession)sessionHolder.getObject()).getEventManager().addListener((SessionEventListener)listenerClass.newInstance());
        } catch (Exception exception) {
            getExceptionStore().add(SessionLoaderException.failedToLoadTag(node.getNodeName(), node.getFirstChild().getNodeValue(), exception));
        }
    }

    /**
     * INTERNAL:
     */
    public void process_session_type_Tag(Node node, ObjectHolder projectHolder) {
        NodeListElementEnumerator list = new NodeListElementEnumerator(node.getChildNodes());
        String sessionType = list.nextNode().getNodeName();

        //CR4130 Do a check on login
        Project project = (Project)projectHolder.getObject();
        if (project.getDatasourceLogin() == null) {
            project.setLogin(new DatabaseLogin());
        }
        if (sessionType.equals("database-session")) {
            projectHolder.setObject(project.createDatabaseSession());
        } else {
            projectHolder.setObject(project.createServerSession());
        }

        //always give a 904 session NoServerPlatform. If an external-transaction-controller-class
        //is specified, this will be changed to org.eclipse.persistence.internal.platform.server.ServerPlatform
        DatabaseSessionImpl databaseSession = (DatabaseSessionImpl)projectHolder.getObject();
        databaseSession.setServerPlatform(new NoServerPlatform(databaseSession));

    }

    /**
     * INTERNAL:
     */
    public void process_user_name_Tag(Node node, ObjectHolder objectHolder) {
        // RCM tag
        if (objectHolder.getObject() instanceof TransportManager) {
            if (node.getFirstChild() != null) {
                ((TransportManager)objectHolder.getObject()).setUserName(node.getFirstChild().getNodeValue());
            } else {
                ((TransportManager)objectHolder.getObject()).setUserName("");
            }
            return;
        }

        if (node.getFirstChild() != null) {
            ((DatasourceLogin)objectHolder.getObject()).setUserName(node.getFirstChild().getNodeValue());
        } else {
            ((DatasourceLogin)objectHolder.getObject()).setUserName("");
        }
    }

    /**
     * INTERNAL:
     */
    public void process_password_Tag(Node node, ObjectHolder objectHolder) {
        // RCM tag
        if (objectHolder.getObject() instanceof TransportManager) {
            if (node.getFirstChild() != null) {
                ((TransportManager)objectHolder.getObject()).setPassword(node.getFirstChild().getNodeValue());
            }
            return;
        }

        if (node.getFirstChild() == null) {
            ((DatasourceLogin)objectHolder.getObject()).setPassword("");
        } else {
            ((DatasourceLogin)objectHolder.getObject()).setPassword(node.getFirstChild().getNodeValue());
        }
    }

    /**
       * INTERNAL:
       */
    public void process_encrypted_password_Tag(Node node, ObjectHolder objectHolder) {
        // RCM tag
        if (objectHolder.getObject() instanceof TransportManager && (node.getFirstChild() != null)) {
            ((TransportManager)objectHolder.getObject()).setEncryptedPassword(node.getFirstChild().getNodeValue());
            return;
        }

        if (node.getFirstChild() == null) {
            ((DatasourceLogin)objectHolder.getObject()).setEncryptedPassword("");
        } else {
            ((DatasourceLogin)objectHolder.getObject()).setEncryptedPassword(node.getFirstChild().getNodeValue());
        }
    }

    /**
       * INTERNAL:
       */
    public void process_encryption_class_name_Tag(Node node, ObjectHolder objectHolder) {
        // RCM tag
        if (objectHolder.getObject() instanceof TransportManager && (node.getFirstChild() != null)) {
            ((TransportManager)objectHolder.getObject()).setEncryptionClassName(node.getFirstChild().getNodeValue());
            return;
        }

        if (node.getFirstChild() != null) {
            ((DatasourceLogin)objectHolder.getObject()).setEncryptionClassName(node.getFirstChild().getNodeValue());
        }
    }

    /**
     * INTERNAL:
     */
    public void process_sequence_counter_field_Tag(Node node, ObjectHolder loginHolder) {
        ((DatabaseLogin)loginHolder.getObject()).setSequenceCounterFieldName(node.getFirstChild().getNodeValue());
    }

    /**
     * INTERNAL:
     */
    public void process_sequence_name_field_Tag(Node node, ObjectHolder loginHolder) {
        ((DatabaseLogin)loginHolder.getObject()).setSequenceNameFieldName(node.getFirstChild().getNodeValue());
    }

    /**
     * INTERNAL:
     */
    public void process_sequence_table_Tag(Node node, ObjectHolder loginHolder) {
        ((DatabaseLogin)loginHolder.getObject()).setSequenceTableName(node.getFirstChild().getNodeValue());
    }

    /**
     * INTERNAL:
     */
    public void process_connection_url_Tag(Node node, ObjectHolder loginHolder) {
        ((DatabaseLogin)loginHolder.getObject()).setConnectionString(node.getFirstChild().getNodeValue());
    }

    /**
     * INTERNAL:
     */
    public void process_non_jts_connection_url_Tag(Node node, ObjectHolder loginHolder) {
        String url = node.getFirstChild().getNodeValue();
        if (this.currentlyBuildingSession instanceof ServerSession) {
            DatabaseLogin login = (DatabaseLogin)((ServerSession)this.currentlyBuildingSession).getReadConnectionPool().getLogin();
            login = (DatabaseLogin)login.clone();
            login.setConnectionString(url);
            ((ServerSession)this.currentlyBuildingSession).getReadConnectionPool().setLogin(login);
        }
    }

    /**
     * INTERNAL:
     */

    //CR3895  Add a new tag for sequence connection pool
    public void process_uses_sequence_connection_pool_Tag(Node node, ObjectHolder loginHolder) {
        try {
            boolean bool = Boolean.valueOf(node.getFirstChild().getNodeValue()).booleanValue();
            if (bool && this.currentlyBuildingSession instanceof ServerSession) {
                ((ServerSession)this.currentlyBuildingSession).getSequencingControl().setShouldUseSeparateConnection(true);
            }
        } catch (Exception exception) {
            getExceptionStore().add(SessionLoaderException.failedToLoadTag(node.getNodeName(), node.getFirstChild().getNodeValue(), exception));
        }
    }

    /**
     * INTERNAL:
     */
    public void process_session_name_Tag(Node node, ObjectHolder placeHolder) {
        ((SessionBrokerPlaceHolder)placeHolder.getObject()).addSessionName(node.getFirstChild().getNodeValue());
    }

    /**
     * INTERNAL:
     */
    public void process_is_asynchronous_Tag(Node node, ObjectHolder sessionHolder) {
        ((AbstractSession)sessionHolder.getObject()).getCacheSynchronizationManager().isAsynchronous();
    }

    /**
     * INTERNAL:
     */
    public void process_multicast_group_address_Tag(Node node, ObjectHolder objectHolder) {
        try {
            String address = node.getFirstChild().getNodeValue();
            if (objectHolder.getObject() instanceof AbstractSession) {
                ((AbstractSession)objectHolder.getObject()).getCacheSynchronizationManager().getClusteringService().setMulticastGroupAddress(address);
            }

            // RCM tag
            if (objectHolder.getObject() instanceof TransportManager) {
                RemoteCommandManager rcm = ((TransportManager)objectHolder.getObject()).getRemoteCommandManager();
                rcm.getDiscoveryManager().setMulticastGroupAddress(address);
            }
        } catch (Exception exception) {
            getExceptionStore().add(SessionLoaderException.failedToLoadTag(node.getNodeName(), node.getFirstChild().getNodeValue(), exception));
        }
    }

    /**
     * INTERNAL:
     */
    public void process_should_remove_connection_on_error_Tag(Node node, ObjectHolder sessionHolder) {
        try {
            Boolean bool = Boolean.valueOf(node.getFirstChild().getNodeValue());
            ((AbstractSession)sessionHolder.getObject()).getCacheSynchronizationManager().setShouldRemoveConnectionOnError(bool.booleanValue());
        } catch (Exception exception) {
            getExceptionStore().add(SessionLoaderException.failedToLoadTag(node.getNodeName(), node.getFirstChild().getNodeValue(), exception));
        }
    }

    /**
     * INTERNAL: RCM tag
     */
    public void process_jms_topic_Tag(Node node, ObjectHolder commandManagerHolder) {
        ObjectHolder transportManagerHolder = new ObjectHolder();
        TransportManager transportMgr = ((CommandManager)commandManagerHolder.getObject()).getTransportManager();
        try {
            boolean oldErrorHandlingState = transportMgr.shouldRemoveConnectionOnError();
            transportMgr = new JMSTopicTransportManager(transportMgr.getRemoteCommandManager());

            // copy previous setting
            transportMgr.setShouldRemoveConnectionOnError(oldErrorHandlingState);
            transportManagerHolder.setObject(transportMgr);

            processChildrenNodes(node, transportManagerHolder);
        } catch (Exception exception) {
            getExceptionStore().add(SessionLoaderException.failedToLoadTag(node.getNodeName(), node.getFirstChild().getNodeValue(), exception));
        }
    }

    /**
     * INTERNAL:
     */
    public void process_jms_topic_connection_factory_name_Tag(Node node, ObjectHolder sessionHolder) {
        try {
            ((JMSClusteringService)((AbstractSession)sessionHolder.getObject()).getCacheSynchronizationManager().getClusteringService()).setTopicConnectionFactoryName(node.getFirstChild().getNodeValue());
        } catch (Exception exception) {
            getExceptionStore().add(SessionLoaderException.failedToLoadTag(node.getNodeName(), node.getFirstChild().getNodeValue(), exception));
        }
    }

    /**
     * INTERNAL:
     */
    public void process_jms_topic_name_Tag(Node node, ObjectHolder sessionHolder) {
        try {
            ((JMSClusteringService)((AbstractSession)sessionHolder.getObject()).getCacheSynchronizationManager().getClusteringService()).setTopicName(node.getFirstChild().getNodeValue());
        } catch (Exception exception) {
            getExceptionStore().add(SessionLoaderException.failedToLoadTag(node.getNodeName(), node.getFirstChild().getNodeValue(), exception));
        }
    }

    /**
     * INTERNAL:
     */
    public void process_jndi_user_name_Tag(Node node, ObjectHolder sessionHolder) {
        try {
            ((AbstractJNDIClusteringService)((AbstractSession)sessionHolder.getObject()).getCacheSynchronizationManager().getClusteringService()).setUserName(node.getFirstChild().getNodeValue());
        } catch (Exception exception) {
            getExceptionStore().add(SessionLoaderException.failedToLoadTag(node.getNodeName(), node.getFirstChild().getNodeValue(), exception));
        }
    }

    /**
     * INTERNAL:
     */
    public void process_jndi_password_Tag(Node node, ObjectHolder sessionHolder) {
        try {
            ((AbstractJNDIClusteringService)((AbstractSession)sessionHolder.getObject()).getCacheSynchronizationManager().getClusteringService()).setPassword(node.getFirstChild().getNodeValue());
        } catch (Exception exception) {
            getExceptionStore().add(SessionLoaderException.failedToLoadTag(node.getNodeName(), node.getFirstChild().getNodeValue(), exception));
        }
    }

    /**
     * INTERNAL:
     */
    public void process_naming_service_initial_context_factory_name_Tag(Node node, ObjectHolder sessionHolder) {
        try {
            ((AbstractJNDIClusteringService)((AbstractSession)sessionHolder.getObject()).getCacheSynchronizationManager().getClusteringService()).setInitialContextFactoryName(node.getFirstChild().getNodeValue());
        } catch (Exception exception) {
            getExceptionStore().add(SessionLoaderException.failedToLoadTag(node.getNodeName(), node.getFirstChild().getNodeValue(), exception));
        }
    }

    /**
     * INTERNAL:
     */
    public void process_naming_service_url_Tag(Node node, ObjectHolder sessionHolder) {
        try {
            // For bug 2700794 make sure the naming service url is set in the clustering service.	
            ((AbstractSession)sessionHolder.getObject()).getCacheSynchronizationManager().getClusteringService().setLocalHostURL(node.getFirstChild().getNodeValue());
        } catch (Exception exception) {
            getExceptionStore().add(SessionLoaderException.failedToLoadTag(node.getNodeName(), node.getFirstChild().getNodeValue(), exception));
        }
    }

    /**
     * INTERNAL:
     */
    public void process_sequence_preallocation_size_Tag(Node node, ObjectHolder loginHolder) {
        try {
            int preallocationSize = Integer.parseInt(node.getFirstChild().getNodeValue());
            ((DatasourceLogin)loginHolder.getObject()).setSequencePreallocationSize(preallocationSize);
        } catch (Exception exception) {
            getExceptionStore().add(SessionLoaderException.failedToLoadTag(node.getNodeName(), node.getFirstChild().getNodeValue(), exception));
        }
    }

    /**
     * INTERNAL:
     */
    public void process_multicast_port_Tag(Node node, ObjectHolder objectHolder) {
        try {
            int portNumber = Integer.parseInt(node.getFirstChild().getNodeValue());

            if (objectHolder.getObject() instanceof AbstractSession) {
                ((AbstractSession)objectHolder.getObject()).getCacheSynchronizationManager().getClusteringService().setMulticastPort(portNumber);
            }

            // RCM tag
            if (objectHolder.getObject() instanceof TransportManager) {
                RemoteCommandManager rcm = ((TransportManager)objectHolder.getObject()).getRemoteCommandManager();
                rcm.getDiscoveryManager().setMulticastPort(portNumber);
            }
        } catch (Exception exception) {
            getExceptionStore().add(SessionLoaderException.failedToLoadTag(node.getNodeName(), node.getFirstChild().getNodeValue(), exception));
        }
    }

    /**
     * INTERNAL:
     */
    public void process_packet_time_to_live_Tag(Node node, ObjectHolder objectHolder) {
        try {
            int timeToLive = Integer.parseInt(node.getFirstChild().getNodeValue());

            if (objectHolder.getObject() instanceof AbstractSession) {
                ((AbstractSession)objectHolder.getObject()).getCacheSynchronizationManager().getClusteringService().setTimeToLive(timeToLive);
            }

            // RCM tag
            if (objectHolder.getObject() instanceof TransportManager) {
                RemoteCommandManager rcm = ((TransportManager)objectHolder.getObject()).getRemoteCommandManager();
                rcm.getDiscoveryManager().setPacketTimeToLive(timeToLive);
            }
        } catch (Exception exception) {
            getExceptionStore().add(SessionLoaderException.failedToLoadTag(node.getNodeName(), node.getFirstChild().getNodeValue(), exception));
        }
    }

    /**
     * INTERNAL:
     */
    public void process_clustering_service_Tag(Node node, ObjectHolder sessionHolder) {
        try {
            Class serviceClass = this.classLoader.loadClass(node.getFirstChild().getNodeValue());
            Class[] params = { org.eclipse.persistence.sessions.Session.class };
            java.lang.reflect.Constructor constructor = serviceClass.getConstructor(params);
            Object[] args = { this.currentlyBuildingSession };
            ((AbstractSession)sessionHolder.getObject()).getCacheSynchronizationManager().setClusteringService((AbstractClusteringService)constructor.newInstance(args));
        } catch (Exception exception) {
            getExceptionStore().add(SessionLoaderException.failedToLoadTag(node.getNodeName(), node.getFirstChild().getNodeValue(), exception));
        }
    }

    /**
     * INTERNAL:
     */
    public void process_platform_class_Tag(Node node, ObjectHolder loginHolder) {
        try {
            //for backward compatibility convert old classes to their new class equivalent
            String nodeValue = node.getFirstChild().getNodeValue();
            if ("org.eclipse.persistence.internal.databaseaccess.AccessPlatform".equals(nodeValue)){
                nodeValue = "org.eclipse.persistence.platform.database.AccessPlatform";
            }else if ("org.eclipse.persistence.internal.databaseaccess.AttunityPlatform".equals(nodeValue)){
                nodeValue = "org.eclipse.persistence.platform.database.AttunityPlatform";
            }else if ("org.eclipse.persistence.internal.databaseaccess.CloudscapePlatform".equals(nodeValue)){
                nodeValue = "org.eclipse.persistence.platform.database.CloudscapePlatform";
            }else if ("org.eclipse.persistence.internal.databaseaccess.DatabasePlatform".equals(nodeValue)){
                nodeValue = "org.eclipse.persistence.platform.database.DatabasePlatform";
            }else if ("org.eclipse.persistence.internal.databaseaccess.DB2MainframePlatform".equals(nodeValue)){
                nodeValue = "org.eclipse.persistence.platform.database.DB2MainframePlatform";
            }else if ("org.eclipse.persistence.internal.databaseaccess.DB2Platform".equals(nodeValue)){
                nodeValue = "org.eclipse.persistence.platform.database.DB2Platform";
            }else if ("org.eclipse.persistence.internal.databaseaccess.DBasePlatform".equals(nodeValue)){
                nodeValue = "org.eclipse.persistence.platform.database.DBasePlatform";
            }else if ("org.eclipse.persistence.internal.databaseaccess.HSQLPlatform".equals(nodeValue)){
                nodeValue = "org.eclipse.persistence.platform.database.HSQLPlatform";
            }else if ("org.eclipse.persistence.internal.databaseaccess.InformixPlatform".equals(nodeValue)){
                nodeValue = "org.eclipse.persistence.platform.database.InformixPlatform";
            }else if ("org.eclipse.persistence.internal.databaseaccess.OraclePlatform".equals(nodeValue)){
                nodeValue = "org.eclipse.persistence.platform.database.oracle.OraclePlatform";
            }else if ("org.eclipse.persistence.internal.databaseaccess.PointBasePlatform".equals(nodeValue)){
                nodeValue = "org.eclipse.persistence.platform.database.PointBasePlatform";
            }else if ("org.eclipse.persistence.internal.databaseaccess.SQLAnyWherePlatform".equals(nodeValue)){
                nodeValue = "org.eclipse.persistence.platform.database.SQLAnyWherePlatform";
            }else if ("org.eclipse.persistence.internal.databaseaccess.SQLServerPlatform".equals(nodeValue)){
                nodeValue = "org.eclipse.persistence.platform.database.SQLServerPlatform";
            }else if ("org.eclipse.persistence.internal.databaseaccess.SybasePlatform".equals(nodeValue)){
                nodeValue = "org.eclipse.persistence.platform.database.SybasePlatform";
            }else if ("org.eclipse.persistence.oraclespecific.Oracle8Platform".equals(nodeValue)){
                nodeValue = "org.eclipse.persistence.platform.database.oracle.Oracle8Platform";
            }else if ("org.eclipse.persistence.oraclespecific.Oracle9Platform".equals(nodeValue)){
                nodeValue = "org.eclipse.persistence.platform.database.oracle.Oracle9Platform";
            }
            Class platformClass = this.classLoader.loadClass(nodeValue);
            ((DatasourceLogin)loginHolder.getObject()).usePlatform((DatasourcePlatform)platformClass.newInstance());
        } catch (Exception exception) {
            getExceptionStore().add(SessionLoaderException.failedToLoadTag(node.getNodeName(), node.getFirstChild().getNodeValue(), exception));
        }
    }

    /**
     * INTERNAL:
     */
    public void process_type_Tag(Node node, ObjectHolder loginHolder) {
        try {
            Class loginClass = this.classLoader.loadClass(node.getFirstChild().getNodeValue());
            Login login = (Login)loginClass.newInstance();
            loginHolder.setObject(login);
        } catch (Exception exception) {
            getExceptionStore().add(SessionLoaderException.failedToLoadTag(node.getNodeName(), node.getFirstChild().getNodeValue(), exception));
        }
    }

    /**
     * INTERNAL:
     */
    public void process_project_class_Tag(Node node, ObjectHolder projectHolder) {
        try {
            String projectName = node.getFirstChild().getNodeValue();
            Class projectClass = this.classLoader.loadClass(projectName);
            projectHolder.setObject(projectClass.newInstance());
        } catch (Throwable exception) {
            getExceptionStore().add(SessionLoaderException.failedToLoadProjectClass(node.getFirstChild().getNodeValue(), exception));
        }
    }

    /**
     * INTERNAL:
     * Check classpath for the project, then the filesystem.
     */
    public void process_project_xml_Tag(Node node, ObjectHolder projectHolder) {
        try {
            Project project = null;
            try {
                project = XMLProjectReader.read(node.getFirstChild().getNodeValue(), this.classLoader);
            } catch (ValidationException validationException) {
                if (validationException.getErrorCode() == ValidationException.PROJECT_XML_NOT_FOUND) {
                    project = XMLProjectReader.read(node.getFirstChild().getNodeValue());
                } else {
                    getExceptionStore().add(SessionLoaderException.failedToLoadProjectXml(node.getFirstChild().getNodeValue(), validationException));
                    return;
                }
            }
            if (project != null) {
                projectHolder.setObject(project);
            } else {
                getExceptionStore().add(SessionLoaderException.couldNotFindProjectXml(node.getFirstChild().getNodeValue()));
            }
        } catch (Throwable exception) {
            getExceptionStore().add(SessionLoaderException.failedToLoadProjectXml(node.getFirstChild().getNodeValue(), exception));
        }
    }

    /**
     * INTERNAL:
     */
    public void process_external_transaction_controller_class_Tag(Node node, ObjectHolder sessionHolder) {
        try {
            Class controllerClass = this.classLoader.loadClass(node.getFirstChild().getNodeValue());

            //changed to define the transaction controller class in the server platform,
            //and define the server platform in the database session
            if (controllerClass != null) {
                DatabaseSessionImpl databaseSession = (DatabaseSessionImpl)sessionHolder.getObject();
                CustomServerPlatform customPlatform = new CustomServerPlatform(databaseSession);
                databaseSession.setServerPlatform(customPlatform);
                customPlatform.setExternalTransactionControllerClass(controllerClass);
            }
        } catch (Throwable exception) {
            getExceptionStore().add(SessionLoaderException.failedToLoadTag(node.getNodeName(), node.getFirstChild().getNodeValue(), exception));
        }
    }

    /**
     * INTERNAL:
     */
    public void process_exception_handler_class_Tag(Node node, ObjectHolder sessionHolder) {
        try {
            Class handlerClass = this.classLoader.loadClass(node.getFirstChild().getNodeValue());
            ((AbstractSession)sessionHolder.getObject()).setExceptionHandler((ExceptionHandler)handlerClass.newInstance());
        } catch (Throwable exception) {
            getExceptionStore().add(SessionLoaderException.failedToLoadTag(node.getNodeName(), node.getFirstChild().getNodeValue(), exception));
        }
    }

    /**
     * INTERNAL:
     */
    public void process_profiler_class_Tag(Node node, ObjectHolder sessionHolder) {
        try {
            Class profilerClass = this.classLoader.loadClass(node.getFirstChild().getNodeValue());
            ((AbstractSession)sessionHolder.getObject()).setProfiler((SessionProfiler)profilerClass.newInstance());
        } catch (Throwable exception) {
            getExceptionStore().add(SessionLoaderException.failedToLoadTag(node.getNodeName(), node.getFirstChild().getNodeValue(), exception));
        }
    }

    /**
     * INTERNAL:
     */
    public void process_logging_options_Tag(Node node, ObjectHolder sessionHolder) {
        processChildrenNodes(node, sessionHolder);
    }

    /**
     * INTERNAL:
     */
    public void process_log_type_Tag(Node node, ObjectHolder sessionHolder) {
        try {
            NodeListElementEnumerator list = new NodeListElementEnumerator(node.getChildNodes());
            Node childNode = list.nextNode();
            String logType = childNode.getNodeName();
            if (logType.equals("toplink")) {
                ((AbstractSession)sessionHolder.getObject()).setSessionLog(new DefaultSessionLog());
                process_toplink_log_Tag(childNode, sessionHolder);
            } else if (logType.equals("java")) {
                SessionLog defaultLog = null;
                if (!Version.isJDK13()) {
                    try {
                        // use ConversionManager to avoid loading the JDK 1.4 class unless it is needed.
                        defaultLog = (SessionLog)((Class)ConversionManager.getDefaultManager().convertObject("org.eclipse.persistence.logging.JavaLog", Class.class)).newInstance();
                        defaultLog.setSession((AbstractSession)sessionHolder.getObject());
                    } catch (Exception exception) {
                        getExceptionStore().add(exception);
                    }
                } else {
                    getExceptionStore().add(ValidationException.featureIsNotAvailableInRunningJDKVersion("Java Log"));
                }
                ((AbstractSession)sessionHolder.getObject()).setSessionLog(defaultLog);
            } else {
                getExceptionStore().add(SessionLoaderException.unexpectedValueOfTag(logType, node.getNodeName()));
            }
        } catch (Throwable exception) {
            getExceptionStore().add(SessionLoaderException.failedToLoadTag(node.getNodeName(), node.getFirstChild().getNodeName(), exception));
        }
    }

    /**
     * INTERNAL:
     */
    public void process_toplink_log_Tag(Node node, ObjectHolder sessionHolder) {
        processChildrenNodes(node, sessionHolder);
    }

    /**
     * INTERNAL:
     */
    public void process_log_level_Tag(Node node, ObjectHolder sessionHolder) {
        try {
            NodeListElementEnumerator list = new NodeListElementEnumerator(node.getChildNodes());
            String logLevel = list.nextNode().getNodeName();
            if (logLevel.equals("off")) {
                ((AbstractSession)sessionHolder.getObject()).getSessionLog().setLevel(SessionLog.OFF);
            } else if (logLevel.equals("severe")) {
                ((AbstractSession)sessionHolder.getObject()).getSessionLog().setLevel(SessionLog.SEVERE);
            } else if (logLevel.equals("warning")) {
                ((AbstractSession)sessionHolder.getObject()).getSessionLog().setLevel(SessionLog.WARNING);
            } else if (logLevel.equals("info")) {
                ((AbstractSession)sessionHolder.getObject()).getSessionLog().setLevel(SessionLog.INFO);
            } else if (logLevel.equals("config")) {
                ((AbstractSession)sessionHolder.getObject()).getSessionLog().setLevel(SessionLog.CONFIG);
            } else if (logLevel.equals("fine")) {
                ((AbstractSession)sessionHolder.getObject()).getSessionLog().setLevel(SessionLog.FINE);
            } else if (logLevel.equals("finer")) {
                ((AbstractSession)sessionHolder.getObject()).getSessionLog().setLevel(SessionLog.FINER);
            } else if (logLevel.equals("finest")) {
                ((AbstractSession)sessionHolder.getObject()).getSessionLog().setLevel(SessionLog.FINEST);
            } else if (logLevel.equals("all")) {
                ((AbstractSession)sessionHolder.getObject()).getSessionLog().setLevel(SessionLog.ALL);
            } else {
                getExceptionStore().add(SessionLoaderException.unexpectedValueOfTag(logLevel, node.getNodeName()));
            }
        } catch (Throwable exception) {
            getExceptionStore().add(SessionLoaderException.failedToLoadTag(node.getNodeName(), node.getFirstChild().getNodeName(), exception));
        }
    }

    /**
     * INTERNAL:
     */
    public void process_file_name_Tag(Node node, ObjectHolder sessionHolder) {
        try {
            String fileName = String.valueOf(node.getFirstChild().getNodeValue());
            ((DefaultSessionLog)((AbstractSession)sessionHolder.getObject()).getSessionLog()).setWriter(fileName);
        } catch (Throwable exception) {
            getExceptionStore().add(SessionLoaderException.failedToLoadTag(node.getNodeName(), node.getFirstChild().getNodeValue(), exception));
        }
    }

    /**
     * INTERNAL:
     * Obsolete.  Map this tag to the new log levels.
     */
    public void process_log_debug_Tag(Node node, ObjectHolder sessionHolder) {
        try {
            Boolean bool = Boolean.valueOf(node.getFirstChild().getNodeValue());
            ((AbstractSession)sessionHolder.getObject()).getSessionLog().setShouldLogDebug(bool.booleanValue());
        } catch (Throwable exception) {
            getExceptionStore().add(SessionLoaderException.failedToLoadTag(node.getNodeName(), node.getFirstChild().getNodeValue(), exception));
        }
    }

    /**
     * INTERNAL:
     * Obsolete.  Map this tag to the new log levels.
     */
    public void process_log_exceptions_Tag(Node node, ObjectHolder sessionHolder) {
        try {
            Boolean bool = Boolean.valueOf(node.getFirstChild().getNodeValue());
            ((AbstractSession)sessionHolder.getObject()).getSessionLog().setShouldLogExceptions(bool.booleanValue());
        } catch (Throwable exception) {
            getExceptionStore().add(SessionLoaderException.failedToLoadTag(node.getNodeName(), node.getFirstChild().getNodeValue(), exception));
        }
    }

    /**
     * INTERNAL:
     */
    public void process_log_exception_stacktrace_Tag(Node node, ObjectHolder sessionHolder) {
        try {
            Boolean bool = Boolean.valueOf(node.getFirstChild().getNodeValue());
            ((AbstractSession)sessionHolder.getObject()).getSessionLog().setShouldLogExceptionStackTrace(bool.booleanValue());
        } catch (Throwable exception) {
            getExceptionStore().add(SessionLoaderException.failedToLoadTag(node.getNodeName(), node.getFirstChild().getNodeValue(), exception));
        }
    }

    /**
     * INTERNAL:
     */
    public void process_print_thread_Tag(Node node, ObjectHolder sessionHolder) {
        try {
            Boolean bool = Boolean.valueOf(node.getFirstChild().getNodeValue());
            ((AbstractSession)sessionHolder.getObject()).getSessionLog().setShouldPrintThread(bool.booleanValue());
        } catch (Throwable exception) {
            getExceptionStore().add(SessionLoaderException.failedToLoadTag(node.getNodeName(), node.getFirstChild().getNodeValue(), exception));
        }
    }

    /**
     * INTERNAL:
     */
    public void process_print_session_Tag(Node node, ObjectHolder sessionHolder) {
        try {
            Boolean bool = Boolean.valueOf(node.getFirstChild().getNodeValue());
            ((AbstractSession)sessionHolder.getObject()).getSessionLog().setShouldPrintSession(bool.booleanValue());
        } catch (Throwable exception) {
            getExceptionStore().add(SessionLoaderException.failedToLoadTag(node.getNodeName(), node.getFirstChild().getNodeValue(), exception));
        }
    }

    /**
     * INTERNAL:
     */
    public void process_print_connection_Tag(Node node, ObjectHolder sessionHolder) {
        try {
            Boolean bool = Boolean.valueOf(node.getFirstChild().getNodeValue());
            ((AbstractSession)sessionHolder.getObject()).getSessionLog().setShouldPrintConnection(bool.booleanValue());
        } catch (Throwable exception) {
            getExceptionStore().add(SessionLoaderException.failedToLoadTag(node.getNodeName(), node.getFirstChild().getNodeValue(), exception));
        }
    }

    /**
     * INTERNAL:
     */
    public void process_print_date_Tag(Node node, ObjectHolder sessionHolder) {
        try {
            Boolean bool = Boolean.valueOf(node.getFirstChild().getNodeValue());
            ((AbstractSession)sessionHolder.getObject()).getSessionLog().setShouldPrintDate(bool.booleanValue());
        } catch (Throwable exception) {
            getExceptionStore().add(SessionLoaderException.failedToLoadTag(node.getNodeName(), node.getFirstChild().getNodeValue(), exception));
        }
    }

    /**
     * INTERNAL: RCM tag
     */
    public void process_remote_command_Tag(Node node, ObjectHolder sessionHolder) {
        ObjectHolder commandManagerHolder = new ObjectHolder();
        commandManagerHolder.setObject(new RemoteCommandManager((AbstractSession)sessionHolder.getObject()));
        processChildrenNodes(node, commandManagerHolder);
    }

    /**
     * INTERNAL: RCM tag
     */
    public void process_channel_Tag(Node node, ObjectHolder commandManagerHolder) {
        ((CommandManager)commandManagerHolder.getObject()).setChannel(node.getFirstChild().getNodeValue());
    }

    /**
     * INTERNAL: RCM tag
     */
    public void process_commands_Tag(Node node, ObjectHolder commandManagerHolder) {
        processChildrenNodes(node, commandManagerHolder);
    }

    /**
     * INTERNAL: RCM tag
     */
    public void process_cache_sync_Tag(Node node, ObjectHolder commandManagerHolder) {
        // turn on cache sync with Session
        ((AbstractSession)((CommandManager)commandManagerHolder.getObject()).getCommandProcessor()).setShouldPropagateChanges(true);
    }

    /**
     * INTERNAL: RCM tag
     */
    public void process_transport_Tag(Node node, ObjectHolder commandManagerHolder) {
        processChildrenNodes(node, commandManagerHolder);
    }

    /**
     * INTERNAL: RCM tag
     */
    public void process_on_connection_error_Tag(Node node, ObjectHolder commandManagerHolder) {
        String onConnectionError = node.getFirstChild().getNodeValue();
        if (onConnectionError.equals("DiscardConnection")) {
            ((CommandManager)commandManagerHolder.getObject()).getTransportManager().setShouldRemoveConnectionOnError(true);
        } else if (onConnectionError.equals("KeepConnection")) {
            ((CommandManager)commandManagerHolder.getObject()).getTransportManager().setShouldRemoveConnectionOnError(false);
        } else {
            getExceptionStore().add(SessionLoaderException.unexpectedValueOfTag(onConnectionError, node.getNodeName()));
        }
    }

    /**
     * INTERNAL: RCM tag
     */
    public void process_topic_host_url_Tag(Node node, ObjectHolder transportManagerHolder) {
        try {
            ((JMSTopicTransportManager)transportManagerHolder.getObject()).setTopicHostUrl(node.getFirstChild().getNodeValue());
        } catch (Exception exception) {
            getExceptionStore().add(SessionLoaderException.failedToLoadTag(node.getNodeName(), node.getFirstChild().getNodeValue(), exception));
        }
    }

    /**
     * INTERNAL: RCM tag
     */
    public void process_topic_connection_factory_name_Tag(Node node, ObjectHolder transportManagerHolder) {
        try {
            ((JMSTopicTransportManager)transportManagerHolder.getObject()).setTopicConnectionFactoryName(node.getFirstChild().getNodeValue());
        } catch (Exception exception) {
            getExceptionStore().add(SessionLoaderException.failedToLoadTag(node.getNodeName(), node.getFirstChild().getNodeValue(), exception));
        }
    }

    /**
     * INTERNAL: RCM tag
     */
    public void process_topic_name_Tag(Node node, ObjectHolder transportManagerHolder) {
        try {
            ((JMSTopicTransportManager)transportManagerHolder.getObject()).setTopicName(node.getFirstChild().getNodeValue());
        } catch (Exception exception) {
            getExceptionStore().add(SessionLoaderException.failedToLoadTag(node.getNodeName(), node.getFirstChild().getNodeValue(), exception));
        }
    }

    /**
     * INTERNAL: RCM tag
     */
    public void process_transport_class_Tag(Node node, ObjectHolder commandManagerHolder) {
        AbstractSession theSession = (AbstractSession)((CommandManager)commandManagerHolder.getObject()).getCommandProcessor();
        String className = node.getFirstChild().getNodeValue();
        try {
            Class transportClass = (Class)theSession.getDatasourcePlatform().getConversionManager().convertObject(className, ClassConstants.CLASS);
            Constructor constructor = transportClass.getDeclaredConstructor(new Class[0]);
            TransportManager transport = (TransportManager)constructor.newInstance((Object[])null);
            theSession.getCommandManager().setTransportManager(transport);

        } catch (Exception exception) {
            getExceptionStore().add(SessionLoaderException.failedToLoadTag(node.getNodeName(), className, exception));
        }
    }

    /**
     * INTERNAL: RCM tag
     */
    public void process_rmi_Tag(Node node, ObjectHolder commandManagerHolder) {
        ObjectHolder transportManagerHolder = new ObjectHolder();
        RemoteCommandManager rcm = (RemoteCommandManager)commandManagerHolder.getObject();

        // 3039025: avoid overwrite the existing RMITransportManager
        if (!(rcm.getTransportManager() instanceof RMITransportManager)) {
            rcm.setTransportManager(new RMITransportManager(rcm));
        }
        transportManagerHolder.setObject(rcm.getTransportManager());

        processChildrenNodes(node, transportManagerHolder);
    }

    /**
     * INTERNAL: RCM tag
     */
    public void process_send_mode_Tag(Node node, ObjectHolder transportManagerHolder) {
        String mode = node.getFirstChild().getNodeValue();
        RemoteCommandManager rcm = ((TransportManager)transportManagerHolder.getObject()).getRemoteCommandManager();

        if (mode.equals("Asynchronous")) {
            rcm.setShouldPropagateAsynchronously(true);
        } else if (mode.equals("Synchronous")) {
            rcm.setShouldPropagateAsynchronously(false);
        } else {
            getExceptionStore().add(SessionLoaderException.unexpectedValueOfTag(mode, node.getNodeName()));
        }
    }

    /**
     * INTERNAL: RCM tag
     */
    public void process_discovery_Tag(Node node, ObjectHolder transportManagerHolder) {
        processChildrenNodes(node, transportManagerHolder);
    }

    /**
     * INTERNAL: RCM tag
     */
    public void process_announcement_delay_Tag(Node node, ObjectHolder transportManagerHolder) {
        RemoteCommandManager rcm = ((TransportManager)transportManagerHolder.getObject()).getRemoteCommandManager();
        int delay = Integer.parseInt(node.getFirstChild().getNodeValue());
        rcm.getDiscoveryManager().setAnnouncementDelay(delay);
    }

    /**
     * INTERNAL: RCM tag
     */
    public void process_jndi_naming_service_Tag(Node node, ObjectHolder transportManagerHolder) {
        ((TransportManager)transportManagerHolder.getObject()).setNamingServiceType(TransportManager.JNDI_NAMING_SERVICE);
        processChildrenNodes(node, transportManagerHolder);
    }

    /**
     * INTERNAL: RCM tag
     */
    public void process_url_Tag(Node node, ObjectHolder transportManagerHolder) {
        RemoteCommandManager rcm = ((TransportManager)transportManagerHolder.getObject()).getRemoteCommandManager();
        rcm.setUrl(node.getFirstChild().getNodeValue());
    }

    /**
     * INTERNAL: RCM tag
     */
    public void process_initial_context_factory_name_Tag(Node node, ObjectHolder transportManagerHolder) {
        ((TransportManager)transportManagerHolder.getObject()).setInitialContextFactoryName(node.getFirstChild().getNodeValue());
    }

    /**
     * INTERNAL: RCM tag
     */
    public void process_property_Tag(Node node, ObjectHolder transportManagerHolder) {
        NamedNodeMap attributes = node.getAttributes();
        Node nameNode = null;
        Node valueNode = null;

        if (attributes != null) {
            nameNode = attributes.getNamedItem("name");
            valueNode = attributes.getNamedItem("value");
        }
        if ((nameNode == null) || (valueNode == null)) {
            getExceptionStore().add(SessionLoaderException.unknownAttributeOfTag(node.getNodeName()));
        } else {
            String name = nameNode.getNodeValue();
            String value = valueNode.getNodeValue();

            ((TransportManager)transportManagerHolder.getObject()).getRemoteContextProperties().put(name, value);
        }
    }

    /**
     * INTERNAL: RCM tag
     */
    public void process_rmi_registry_naming_service_Tag(Node node, ObjectHolder transportManagerHolder) {
        ((TransportManager)transportManagerHolder.getObject()).setNamingServiceType(RMITransportManager.REGISTRY_NAMING_SERVICE);
        processChildrenNodes(node, transportManagerHolder);
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

    /**
     * INTERNAL: helper method to process children nodes of the current node
     */
    protected void processChildrenNodes(Node node, ObjectHolder objectHolder) {
        NodeListElementEnumerator list = new NodeListElementEnumerator(node.getChildNodes());
        while (list.hasMoreNodes()) {
            Node childNode = list.nextNode();
            try {
                Class[] args = { Node.class, ObjectHolder.class };
                java.lang.reflect.Method method = this.getClass().getMethod(convertNodeToMethodName(childNode), args);
                Object[] objectList = { childNode, objectHolder };
                method.invoke(this, objectList);
            } catch (Exception exception) {
                getExceptionStore().add(SessionLoaderException.unkownTagAtNode(childNode.getNodeName(), node.getNodeName(), exception));
            }
        }
    }

    /**
     * INTERNAL:
     * <p><b>Purpose</b>: Provide a mechanism for parse exception handling
     *
     * @see org.xml.sax.ErrorHandler
     * @since TopLink 4.6
     * @author Shannon Chen
     */

    //CR4142 Set an error handler to make validation work.
    public class XMLLoaderErrorHandler implements org.xml.sax.ErrorHandler {
        public void warning(SAXParseException e) throws SAXException {
            //Bug2792096  Warning is stored in messageStore and passed on to session log later.  Parsing continues.
            getMessageStore().add(SessionLoaderException.failedToParseXML(ExceptionLocalization.buildMessage("parsing_warning"), e.getLineNumber(), e.getColumnNumber(), e));
        }

        public void error(SAXParseException e) throws SAXException {
            throw new SAXException(ExceptionLocalization.buildMessage("parsing_error"), e);
        }

        public void fatalError(SAXParseException e) throws SAXException {
            throw new SAXException(ExceptionLocalization.buildMessage("parsing_fatal_error"), e);
        }
    }
}