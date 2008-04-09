/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
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
package org.eclipse.persistence.internal.jpa;

import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.util.*;
import java.io.FileWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;

import javax.persistence.spi.PersistenceUnitInfo;
import javax.persistence.spi.ClassTransformer;
import javax.persistence.PersistenceException;

import org.eclipse.persistence.jpa.config.PersistenceUnitProperties;
import org.eclipse.persistence.jpa.config.BatchWriting;
import org.eclipse.persistence.internal.databaseaccess.DatasourcePlatform;
import org.eclipse.persistence.internal.weaving.TransformerFactory;
import org.eclipse.persistence.sessions.JNDIConnector;
import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedClassForName;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.sequencing.Sequence;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.sessions.server.ReadConnectionPool;
import org.eclipse.persistence.sessions.server.ServerSession;
import org.eclipse.persistence.internal.jpa.metadata.MetadataProcessor;
import org.eclipse.persistence.sessions.factories.SessionManager;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.platform.server.CustomServerPlatform;
import org.eclipse.persistence.platform.server.ServerPlatform;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.internal.helper.JPAConversionManager;
import javax.persistence.spi.PersistenceUnitTransactionType;
import org.eclipse.persistence.internal.jpa.deployment.PersistenceUnitProcessor;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.security.PrivilegedNewInstanceFromClass;
import org.eclipse.persistence.internal.sessions.factories.XMLSessionConfigLoader;
import org.eclipse.persistence.jpa.config.LoggerType;
import org.eclipse.persistence.internal.jpa.jdbc.DataSourceImpl;
import org.eclipse.persistence.internal.sessions.factories.DescriptorCustomizer;
import org.eclipse.persistence.internal.sessions.factories.SessionCustomizer;
import org.eclipse.persistence.internal.security.SecurableObjectHolder;
import org.eclipse.persistence.queries.EJBQLPlaceHolderQuery;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.platform.database.converters.StructConverter;
import org.eclipse.persistence.platform.server.ServerPlatformBase;
import org.eclipse.persistence.jpa.config.ProfilerType;
import org.eclipse.persistence.tools.profiler.QueryMonitor;


import static org.eclipse.persistence.internal.jpa.EntityManagerFactoryProvider.*;

/**
 * INTERNAL:
 * An EclipseLink specific implementer of the EntityManagerInitializer interface.
 * This class handles deployment of a persistence unit.
 * In predeploy the meta-data is processed and weaver transformer is returned to allow weaving of the persistent classes.
 * In deploy the project and session are initialize and registered.
 */
public class EntityManagerSetupImpl {
    /*
     * Design Pattern in use: Builder pattern
     * EntityManagerSetupImpl, MetadataProcessor and MetadataProject
     * play the role of director, builder and product respectively.
     * See processORMetadata which is the factory method.
     */

    protected MetadataProcessor processor = null;
    protected PersistenceUnitInfo persistenceUnitInfo = null;
    protected Map predeployProperties = null;
    // count a number of open factories that use this object.
    protected int factoryCount = 0;
    protected ServerSession session = null;
    // true if predeploy called by createContainerEntityManagerFactory; false - createEntityManagerFactory
    protected boolean isInContainerMode = false;
    protected boolean isSessionLoadedFromSessionsXML=false;
    // indicates whether weaving was used on the first run through predeploy (in STATE_INITIAL)
    protected boolean enableWeaving = false;
    // indicates that classes have already been woven
    protected boolean isWeavingStatic = false;
    protected SecurableObjectHolder securableObjectHolder = new SecurableObjectHolder();

    // factoryCount==0; session==null
    public static final String STATE_INITIAL        = "Initial";
    
    // session != null
    public static final String STATE_PREDEPLOYED    = "Predeployed";
    
    // factoryCount>0; session != null; session stored in SessionManager
    public static final String STATE_DEPLOYED       = "Deployed";
    
    // factoryCount==0; session==null
    public static final String STATE_PREDEPLOY_FAILED="PredeployFailed";
    
    // factoryCount>0; session != null
    public static final String STATE_DEPLOY_FAILED  = "DeployFailed";
    
    // factoryCount==0; session==null
    public static final String STATE_UNDEPLOYED     = "Undeployed";

    protected String state = STATE_INITIAL;

    /**
     *     Initial -----> PredeployFailed
     *           |         |
     *           V         V
     *       |-> Predeployed --> DeployFailed
     *       |   |         |        |
     *       |   V         V        V
     *       | Deployed -> Undeployed-->|
     *       |                          |
     *       |<-------------------------V
     */
    
    
    public static final String ERROR_LOADING_XML_FILE = "error_loading_xml_file";
    public static final String EXCEPTION_LOADING_ENTITY_CLASS = "exception_loading_entity_class";

    /**
     * This method can be used to ensure the session represented by emSetupImpl
     * is removed from the SessionManager.
     */
    protected void removeSessionFromGlobalSessionManager() {
        if (session != null){
            if(session.isConnected()) {
                session.logout();
            }
            SessionManager.getManager().getSessions().remove(session.getName());
        }
    }
    
    
    /**
     * Deploy a persistence session and return an EntityManagerFactory.
     * 
     * Deployment takes a session that was partially created in the predeploy call and makes it whole.
     * 
     * This means doing any configuration that requires the real class definitions for the entities.  In
     * the predeploy phase we were in a stage where we were not let allowed to load the real classes.
     * 
     * Deploy could be called several times - but only the first call does the actual deploying -
     * additional calls allow to update session properties (in case the session is not connected).
     * 
     * Note that there is no need to synchronize deploy method - it doesn't alter factoryCount
     * and while deploy is executed no other method can alter the current state
     * (predeploy call would just increment factoryCount; undeploy call would not drop factoryCount to 0).
     * However precautions should be taken to handle concurrent calls to deploy, because those may
     * alter the current state or connect the session.
     * 
     * @param realClassLoader The class loader that was used to load the entity classes. This loader
     *               will be maintained for the lifespan of the loaded classes.
     * @param additionalProperties added to predeployProperties for updateServerSession overriding existing properties.
     *              In JSE case it allows to alter properties in main (as opposed to preMain where preDeploy is called).
     * @return An EntityManagerFactory to be used by the Container to obtain EntityManagers
     */
    public ServerSession deploy(ClassLoader realClassLoader, Map additionalProperties) {
        if(state != STATE_PREDEPLOYED && state != STATE_DEPLOYED) {
            throw new PersistenceException(EntityManagerSetupException.cannotDeployWithoutPredeploy(persistenceUnitInfo.getPersistenceUnitName(), state));
        }
        // state is PREDEPLOYED or DEPLOYED
        session.log(SessionLog.FINEST, SessionLog.PROPERTIES, "deploy_begin", new Object[]{getPersistenceUnitInfo().getPersistenceUnitName(), state, factoryCount});
        List<StructConverter> structConverters = null;
        try {                        
            Map deployProperties = mergeMaps(additionalProperties, predeployProperties);
            translateOldProperties(deployProperties, session);
            
            if(state == STATE_PREDEPLOYED) {
                synchronized(session) {
                    if(state == STATE_PREDEPLOYED) {
                        try {
                            // The project is initially created using class names rather than classes.  This call will make the conversion.
                            // If the session was loaded from sessions.xml this will also converter the descriptor classes to the correct class loader.
                            session.getProject().convertClassNamesToClasses(realClassLoader);

                            if (!isSessionLoadedFromSessionsXML) {
                                    // listeners and queries require the real classes and are therefore built during deploy using the realClassLoader
                                    processor.setClassLoader(realClassLoader);
                                    processor.addEntityListeners();
                                    processor.addNamedQueries();
                                    
                                    // Process the customizers last.
                                    processor.processCustomizers();
                                    
                                    structConverters = processor.getStructConverters();
                                    
                                    processor = null;
                            }
                    
                            initServerSession(deployProperties);
                    
                            if (session.getIntegrityChecker().hasErrors()){
                                session.handleException(new IntegrityException(session.getIntegrityChecker()));
                            }
                    
                            session.getDatasourcePlatform().getConversionManager().setLoader(realClassLoader);
                            state = STATE_DEPLOYED;
                        } catch (RuntimeException ex) {
                            state = STATE_DEPLOY_FAILED;
                            // session not discarded here only because it will be used in undeploy method for debug logging.
                            throw new PersistenceException(EntityManagerSetupException.deployFailed(persistenceUnitInfo.getPersistenceUnitName(), ex));
                        }
                    }
                }
            }
            // state is DEPLOYED
            if(!session.isConnected()) {
                synchronized(session) {
                    if(!session.isConnected()) {
                        session.setProperties(deployProperties);
                        updateServerSession(deployProperties, realClassLoader);
                        if(isValidationOnly(deployProperties, false)) {
                            session.initializeDescriptors();
                        } else {
                            if(isSessionLoadedFromSessionsXML) {
                                if (!session.isConnected()) {
                                    session.login();
                                }
                            }else{
                                login(session, deployProperties);
                            }
                            if (!isSessionLoadedFromSessionsXML) {
                                addStructConverters(session, structConverters);
                            }
                            generateDDL(session, deployProperties);
                        }
                    }
                }
            List queries = session.getEjbqlPlaceHolderQueries();
            for (Iterator iterator = queries.iterator(); iterator.hasNext();) {
                EJBQLPlaceHolderQuery existingQuery = (EJBQLPlaceHolderQuery)iterator.next();
                DatabaseQuery databaseQuery = existingQuery.processEjbQLQuery(session);
                session.addQuery(databaseQuery.getName(), databaseQuery);
            }
            queries.clear();
            }
            return session;
        } catch (IllegalArgumentException illegalArgumentException) {
            throw new javax.persistence.PersistenceException(illegalArgumentException);
        } catch (org.eclipse.persistence.exceptions.ValidationException exception) {
            throw new javax.persistence.PersistenceException(exception);
        } finally {
            session.log(SessionLog.FINEST, SessionLog.PROPERTIES, "deploy_end", new Object[]{getPersistenceUnitInfo().getPersistenceUnitName(), state, factoryCount});
        }
    }


    /**
     * Adds descriptors plus sequencing info found on the project to the session.
     */
    protected void addProjectToSession(ServerSession session, Project project) {
        DatasourcePlatform sessionPlatform = (DatasourcePlatform)session.getDatasourceLogin().getDatasourcePlatform();
        DatasourcePlatform projectPlatform = (DatasourcePlatform)project.getDatasourceLogin().getDatasourcePlatform();
        if (!sessionPlatform.hasDefaultSequence() && projectPlatform.hasDefaultSequence()) {
            sessionPlatform.setDefaultSequence(projectPlatform.getDefaultSequence());
        }
        if ((sessionPlatform.getSequences() == null) || sessionPlatform.getSequences().isEmpty()) {
            if ((projectPlatform.getSequences() != null) && !projectPlatform.getSequences().isEmpty()) {
                sessionPlatform.setSequences(projectPlatform.getSequences());
            }
        } else {
            if ((projectPlatform.getSequences() != null) && !projectPlatform.getSequences().isEmpty()) {
                Iterator itProjectSequences = projectPlatform.getSequences().values().iterator();
                while (itProjectSequences.hasNext()) {
                    Sequence sequence = (Sequence)itProjectSequences.next();
                    if (!sessionPlatform.getSequences().containsKey(sequence.getName())) {
                        sessionPlatform.addSequence(sequence);
                    }
                }
            }
        }
        session.addDescriptors(project);
    }
    
    /**
     * Put the given session into the session manager so it can be looked up later
     */
    protected void addSessionToGlobalSessionManager() {
        AbstractSession oldSession = (AbstractSession)SessionManager.getManager().getSessions().get(session.getName());
        if(oldSession != null) {
            throw new PersistenceException(EntityManagerSetupException.attemptedRedeployWithoutClose(session.getName()));
        }
        SessionManager.getManager().addSession(session);
    }

    /**
     * Add the StructConverters that were specified by annotation on the DatabasePlatform
     * This method must be called after the DatabasePlatform has been detected
     * @param session
     * @param structConverters
     */
    public void addStructConverters(Session session, List<StructConverter> structConverters){
        for (StructConverter structConverter : structConverters){
            if (session.getPlatform().getTypeConverters().get(structConverter.getJavaType()) != null){
                throw ValidationException.twoStructConvertersAddedForSameClass(structConverter.getJavaType().getName());
            }
            session.getPlatform().addStructConverter(structConverter);
        }
    }

    /**
     * Assign a CMP3Policy to each descriptor
     */
    protected void assignCMP3Policy() {
        // all descriptors assigned CMP3Policy
        Project project = session.getProject();
        for (Iterator iterator = project.getDescriptors().values().iterator(); iterator.hasNext();){
            //bug:4406101  changed class cast to base class, which is used in projects generated from 904 xml
            ClassDescriptor descriptor = (ClassDescriptor)iterator.next();
            
            if(descriptor.getCMPPolicy() == null) {
                descriptor.setCMPPolicy(new CMP3Policy());
            }
        }
    }

    /**
     * Updates the EclipseLink ServerPlatform class for use with this platform.
     * @return true if the ServerPlatform has changed.
     */  
    protected boolean updateServerPlatform(Map m, ClassLoader loader) {
        String serverPlatformClassName = PropertiesHandler.getPropertyValueLogDebug(PersistenceUnitProperties.TARGET_SERVER, m, session);
        if(serverPlatformClassName == null) {
            // property is not specified - nothing to do.
            return false;
        }

        // originalServerPlatform is always non-null - Session's constructor sets serverPlatform to NoServerPlatform
        ServerPlatform originalServerPlatform = session.getServerPlatform();
        String originalServerPlatformClassName = originalServerPlatform.getClass().getName();
        if(originalServerPlatformClassName.equals(serverPlatformClassName)) {
            // nothing to do - use the same value as before
            return false;
        }

        // the new serverPlatform
        ServerPlatform serverPlatform = null;
        // New platform - create the new instance and set it.
        Class cls = findClassForProperty(serverPlatformClassName, PersistenceUnitProperties.TARGET_SERVER, loader);
        try {
            Constructor constructor = cls.getConstructor(new Class[]{org.eclipse.persistence.sessions.DatabaseSession.class});
            serverPlatform = (ServerPlatform)constructor.newInstance(new Object[]{session});
        } catch (Exception ex) {
            if(ExternalTransactionController.class.isAssignableFrom(cls)) {
                // the new serverPlatform is CustomServerPlatform, cls is its ExternalTransactionController class
                if(originalServerPlatform.getClass().equals(CustomServerPlatform.class)) {
                    // both originalServerPlatform and the new serverPlatform are Custom,
                    // just set externalTransactionController class (if necessary) into
                    // originalServerPlatform
                    CustomServerPlatform originalCustomServerPlatform = (CustomServerPlatform)originalServerPlatform;
                    if(cls.equals(originalCustomServerPlatform.getExternalTransactionControllerClass())) {
                        // externalTransactionController classes are the same - nothing to do
                    } else {
                        originalCustomServerPlatform.setExternalTransactionControllerClass(cls);
                    }
                } else {
                    // originalServerPlatform is not custom - need a new one.
                    CustomServerPlatform customServerPlatform = new CustomServerPlatform(session);
                    customServerPlatform.setExternalTransactionControllerClass(cls);
                    serverPlatform = customServerPlatform;
                }
             } else {
                 throw EntityManagerSetupException.failedToInstantiateServerPlatform(serverPlatformClassName, PersistenceUnitProperties.TARGET_SERVER, ex);
             }
         }
 
        if (serverPlatform != null){
            session.setServerPlatform(serverPlatform);
            return true;
        }    
        return false;
    }

    /**
     * Update loggers and settings for the singleton logger and the session logger. 
     * @param persistenceProperties the properties map
     * @param serverPlatformChanged the boolean that denotes a serverPlatform change in the session.
     * @param sessionNameChanged the boolean that denotes a sessionNameChanged change in the session.
     */
    protected void updateLoggers(Map persistenceProperties, boolean serverPlatformChanged, boolean sessionNameChanged, ClassLoader loader) {
        // Logger(SessionLog type) can be specified by the logger property or ServerPlatform.getServerLog().
        // The logger property has a higher priority to ServerPlatform.getServerLog().
        String loggerClassName = PropertiesHandler.getPropertyValueLogDebug(PersistenceUnitProperties.LOGGING_LOGGER, persistenceProperties, session);

        // The sessionLog instance should be different from the singletonLog because they have 
        // different state.
        SessionLog singletonLog = null, sessionLog = null;
        if (loggerClassName != null) {
            SessionLog currentLog = session.getSessionLog();
            if(loggerClassName.equals(LoggerType.ServerLogger)){
                ServerPlatform serverPlatform = session.getServerPlatform();
                singletonLog = serverPlatform.getServerLog();
                sessionLog = serverPlatform.getServerLog();
            } else if (!currentLog.getClass().getName().equals(loggerClassName)) {
                // Logger class was specified and it's not what's already there.
                Class sessionLogClass = findClassForProperty(loggerClassName, PersistenceUnitProperties.LOGGING_LOGGER, loader);
                try {
                    singletonLog = (SessionLog)sessionLogClass.newInstance();
                    sessionLog = (SessionLog)sessionLogClass.newInstance();
                } catch (Exception ex) {
                    throw EntityManagerSetupException.failedToInstantiateLogger(loggerClassName, PersistenceUnitProperties.LOGGING_LOGGER, ex);
                }
            }
        } else if (serverPlatformChanged) {
            ServerPlatform serverPlatform = session.getServerPlatform();
            singletonLog = serverPlatform.getServerLog();
            sessionLog = serverPlatform.getServerLog();
        }
        
        // Don't change default loggers if the new loggers have not been created.
        if (singletonLog != null && sessionLog != null) {
            AbstractSessionLog.setLog(singletonLog);
            session.setSessionLog(sessionLog);
        } else if (sessionNameChanged) {
            // In JavaLog this will result in logger name changes,
            // but won't affect DefaultSessionLog.
            // Note, that the session hasn't change, only its name.
            session.getSessionLog().setSession(session);
        }

        // Bug5389828.  Update the logging settings for the singleton logger.
        initOrUpdateLogging(persistenceProperties, AbstractSessionLog.getLog());
        initOrUpdateLogging(persistenceProperties, session.getSessionLog());
        // Set logging file.
        String loggingFileString = (String)persistenceProperties.get(PersistenceUnitProperties.LOGGING_FILE);
        if (loggingFileString != null) {
            if (!loggingFileString.trim().equals("")) {
                try {
                    if (sessionLog!=null){
                        if (sessionLog instanceof AbstractSessionLog) {
                            FileOutputStream fos = new FileOutputStream(loggingFileString);
                           ((AbstractSessionLog)sessionLog).setWriter(fos);
                        } else {
                            FileWriter fw = new FileWriter(loggingFileString); 
                            sessionLog.setWriter(fw);
                        }
                    }
                } catch (IOException e) {
                    session.handleException(ValidationException.invalidLoggingFile(loggingFileString,e));
                }
            } else {
                session.handleException(ValidationException.invalidLoggingFile());
            }
        }
    }
    
    protected void updateProfiler(Map persistenceProperties,ClassLoader loader){
        String newProfilerClassName = PropertiesHandler.getPropertyValueLogDebug(PersistenceUnitProperties.PROFILER, persistenceProperties, session);

        if(newProfilerClassName == null){
               ((ServerPlatformBase)session.getServerPlatform()).configureProfiler(session);
        }else{

            if(newProfilerClassName.equals(ProfilerType.NoProfiler)){
                session.setProfiler(null);
                return;
            }
            if(newProfilerClassName.equals(ProfilerType.QueryMonitor)){
                session.setProfiler(null);
                QueryMonitor.shouldMonitor=true;
                return;
            }
            
            String originalProfilerClassNamer = null;
            if(session.getProfiler()!=null){
                originalProfilerClassNamer = session.getProfiler().getClass().getName();
                if(originalProfilerClassNamer.equals(newProfilerClassName)){
                    return;
                }
            }
            
            // New profiler - create the new instance and set it.
            try {
                Class newProfilerClass = findClassForProperty(newProfilerClassName,PersistenceUnitProperties.PROFILER, loader);
                SessionProfiler sessionProfiler = (SessionProfiler)buildObjectForClass(newProfilerClass, SessionProfiler.class);
                if(sessionProfiler!=null){
                    session.setProfiler(sessionProfiler);
                } else {
                    session.handleException(ValidationException.invalidProfilerClass(newProfilerClassName));
                }
            } catch (IllegalAccessException e) {
                session.handleException(ValidationException.cannotInstantiateSessionEventListenerClass(newProfilerClassName,e));
            } catch (PrivilegedActionException e) {
                session.handleException(ValidationException.cannotInstantiateSessionEventListenerClass(newProfilerClassName,e));
            } catch (InstantiationException e) {
                session.handleException(ValidationException.cannotInstantiateSessionEventListenerClass(newProfilerClassName,e));
            }
        }
    }
    
    
    protected static Class findClass(String className, ClassLoader loader) throws ClassNotFoundException, PrivilegedActionException {
        if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
            return (Class)AccessController.doPrivileged(new PrivilegedClassForName(className, true, loader));
        } else {
            return org.eclipse.persistence.internal.security.PrivilegedAccessHelper.getClassForName(className, true, loader);
        }
    }
    
    protected static Class findClassForProperty(String className, String propertyName, ClassLoader loader) {
        try {
            return findClass(className, loader);
        } catch (PrivilegedActionException exception1) {
            throw EntityManagerSetupException.classNotFoundForProperty(className, propertyName, exception1.getException());
        } catch (ClassNotFoundException exception2) {
            throw EntityManagerSetupException.classNotFoundForProperty(className, propertyName, exception2);
        }
    }
    
    /**
     * This method will be used to validate the specified class and return it's instance.
     */
    protected static Object buildObjectForClass(Class clazz, Class mustBeImplementedInterface) throws IllegalAccessException, PrivilegedActionException,InstantiationException {
        if(clazz!=null && Helper.classImplementsInterface(clazz,mustBeImplementedInterface)){
            if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                return AccessController.doPrivileged(new PrivilegedNewInstanceFromClass(clazz));
            } else {
                return PrivilegedAccessHelper.newInstanceFromClass(clazz);
            }
        } else {
            return null;
        }
    }
    
    protected void updateDescriptorCacheSettings(Map m, ClassLoader loader) {
        Map typeMap = PropertiesHandler.getPrefixValuesLogDebug(PersistenceUnitProperties.CACHE_TYPE_, m, session);
        Map sizeMap = PropertiesHandler.getPrefixValuesLogDebug(PersistenceUnitProperties.CACHE_SIZE_, m, session);
        Map sharedMap = PropertiesHandler.getPrefixValuesLogDebug(PersistenceUnitProperties.CACHE_SHARED_, m, session);
        if(typeMap.isEmpty() && sizeMap.isEmpty() && sharedMap.isEmpty()) {
            return;
        }

        boolean hasDefault = false;
        
        String defaultTypeName = (String)typeMap.remove(PersistenceUnitProperties.DEFAULT);
        Class defaultType = null;
        if(defaultTypeName != null) {
            defaultType = findClassForProperty(defaultTypeName, PersistenceUnitProperties.CACHE_TYPE_DEFAULT, loader);
            hasDefault = true;
        }
        
        String defaultSizeString = (String)sizeMap.remove(PersistenceUnitProperties.DEFAULT);
        Integer defaultSize = null;
        if(defaultSizeString != null) {
            defaultSize = Integer.parseInt(defaultSizeString);
            hasDefault = true;
        }
        
        String defaultSharedString = (String)sharedMap.remove(PersistenceUnitProperties.DEFAULT);
        Boolean defaultShared = null;
        if(defaultSharedString != null) {
            defaultShared = Boolean.parseBoolean(defaultSharedString);
            hasDefault = true;
        }
        
        Iterator it = session.getDescriptors().values().iterator();
        while (it.hasNext() && (hasDefault || !typeMap.isEmpty() || !sizeMap.isEmpty() || !sharedMap.isEmpty())) {
            ClassDescriptor descriptor = (ClassDescriptor)it.next();
            
            if(descriptor.isAggregateDescriptor() || descriptor.isAggregateCollectionDescriptor()) {
                continue;
            }
            
            String entityName = descriptor.getAlias();
            String className = descriptor.getJavaClass().getName();
            String name;
            
            Class type = defaultType;
            name = entityName;
            String typeName = (String)typeMap.remove(name);
            if(typeName == null) {
                name = className;
                typeName = (String)typeMap.remove(name);
            }
            if(typeName != null) {
                type = findClassForProperty(typeName, PersistenceUnitProperties.CACHE_TYPE_ + name, loader);
            }
            if(type != null) {
                descriptor.setIdentityMapClass(type);
            }

            Integer size = defaultSize;
            name = entityName;
            String sizeString = (String)sizeMap.remove(name);
            if(sizeString == null) {
                name = className;
                sizeString = (String)sizeMap.remove(name);
            }
            if(sizeString != null) {
                size = Integer.parseInt(sizeString);
            }
            if(size != null) {
                descriptor.setIdentityMapSize(size.intValue());
            }

            Boolean shared = defaultShared;
            name = entityName;
            String sharedString = (String)sharedMap.remove(name);
            if(sharedString == null) {
                name = className;
                sharedString = (String)sharedMap.remove(name);
            }
            if(sharedString != null) {
                shared = Boolean.parseBoolean(sharedString);
            }
            if(shared != null) {
                descriptor.setIsIsolated(!shared.booleanValue());
            }
        }
    }

    /**
     * Perform any steps necessary prior to actual deployment.  This includes any steps in the session
     * creation that do not require the real loaded domain classes.
     * 
     * The first call to this method caches persistenceUnitInfo which is reused in the following calls.
     * 
     * Note that in JSE case factoryCount is NOT incremented on the very first call 
     * (by JavaSECMPInitializer.callPredeploy, typically in preMain).
     * That provides 1 to 1 correspondence between factoryCount and the number of open factories.
     * 
     * In case factoryCount > 0 the method just increments factoryCount.
     * factory == 0 triggers creation of a new session.
     * 
     * This method and undeploy - the only methods altering factoryCount - should be synchronized.
     *
     * @return A transformer (which may be null) that should be plugged into the proper
     *         classloader to allow classes to be transformed as they get loaded.
     * @see #deploy(ClassLoader, Map)
     */
    public synchronized ClassTransformer predeploy(PersistenceUnitInfo info, Map extendedProperties) {
        if (state == STATE_DEPLOY_FAILED) {
            throw new PersistenceException(EntityManagerSetupException.cannotPredeploy(persistenceUnitInfo.getPersistenceUnitName(), state));
        }
        if (state == STATE_PREDEPLOYED || state == STATE_DEPLOYED) {
            session.log(SessionLog.FINEST, SessionLog.PROPERTIES, "predeploy_begin", new Object[]{getPersistenceUnitInfo().getPersistenceUnitName(), state, factoryCount});
            factoryCount++;
            session.log(SessionLog.FINEST, SessionLog.PROPERTIES, "predeploy_end", new Object[]{getPersistenceUnitInfo().getPersistenceUnitName(), state, factoryCount});
            return null;
        } else if(state == STATE_INITIAL || state == STATE_UNDEPLOYED) {
            persistenceUnitInfo = info;
        }
        // state is INITIAL, PREDEPLOY_FAILED or UNDEPLOYED
        try {
            // Initialize the privateClassLoader right away.
            ClassLoader privateClassLoader = persistenceUnitInfo.getNewTempClassLoader();
            predeployProperties = mergeMaps(extendedProperties, persistenceUnitInfo.getProperties());    
            // Translate old properties.
            // This should be done before using properties (i.e. ServerPlatform).
            translateOldProperties(predeployProperties, null);

            String sessionsXMLStr = (String)predeployProperties.get(PersistenceUnitProperties.SESSIONS_XML);
            String sessionNameStr = (String)predeployProperties.get(PersistenceUnitProperties.SESSION_NAME);            
            if (sessionsXMLStr != null) {
                isSessionLoadedFromSessionsXML = true;
            }
            
            // Create server session (it needs to be done before initializing ServerPlatform and logging).
            // If a sessions-xml is used this will get replaced later, but is required for logging.
            session = new ServerSession(new Project(new DatabaseLogin()));            
            // ServerSession name and ServerPlatform must be set prior to setting the loggers.
            setServerSessionName(predeployProperties);
            ClassLoader realClassLoader = persistenceUnitInfo.getClassLoader();
            updateServerPlatform(predeployProperties, realClassLoader);
            // Update loggers and settings for the singleton logger and the session logger.
            updateLoggers(predeployProperties, true, false, realClassLoader);
            
            //Update performance profiler
            updateProfiler(predeployProperties,realClassLoader);
            
            // Cannot start logging until session and log and initialized, so log start of predeploy here.
            session.log(SessionLog.FINEST, SessionLog.PROPERTIES, "predeploy_begin", new Object[]{getPersistenceUnitInfo().getPersistenceUnitName(), state, factoryCount});
            
            if (isSessionLoadedFromSessionsXML) {
                // Loading session from sessions-xml.
                session.log(SessionLog.FINEST, SessionLog.PROPERTIES, "loading_session_xml", sessionsXMLStr, sessionNameStr);
                if (sessionNameStr == null) {
                    throw new PersistenceException(EntityManagerSetupException.sessionNameNeedBeSpecified(info.getPersistenceUnitName(), sessionsXMLStr));
                }                
                XMLSessionConfigLoader xmlLoader = new XMLSessionConfigLoader(sessionsXMLStr);
                // Do not register the session with the SessionManager at this point, create tempory session using a local SessionManager and private class loader.
                // This allows for the project to be accessed without loading any of the class to allow weaving.
                Session tempSession = new SessionManager().getSession(xmlLoader, sessionNameStr, privateClassLoader, false, false);
                // Load path of sessions-xml resource before throwing error so user knows which sessions-xml file was found (may be multiple).
                session.log(SessionLog.FINEST, SessionLog.PROPERTIES, "sessions_xml_path_where_session_load_from", xmlLoader.getSessionName(), xmlLoader.getResourcePath());
                if (tempSession == null) {
                    throw new PersistenceException(ValidationException.noSessionFound(sessionNameStr, sessionsXMLStr));
                }
                if (tempSession.isServerSession()) {
                   session = (ServerSession) tempSession;
                } else {
                    throw new PersistenceException(EntityManagerSetupException.sessionLoadedFromSessionsXMLMustBeServerSession(info.getPersistenceUnitName(), (String)predeployProperties.get(PersistenceUnitProperties.SESSIONS_XML), tempSession));
                }
                // Must now reset logging and server-platform on the loaded session.
                // ServerSession name and ServerPlatform must be set prior to setting the loggers.
                setServerSessionName(predeployProperties);
                updateServerPlatform(predeployProperties, privateClassLoader);
                // Update loggers and settings for the singleton logger and the session logger.
                updateLoggers(predeployProperties, true, false, privateClassLoader);
            }
            
            warnOldProperties(predeployProperties, session);
            session.getPlatform().setConversionManager(new JPAConversionManager());
        
            PersistenceUnitTransactionType transactionType=null;
            //bug 5867753: find and override the transaction type
            String transTypeString = getConfigPropertyAsStringLogDebug(PersistenceUnitProperties.TRANSACTION_TYPE, predeployProperties, session);
            if ( transTypeString != null ){
                transactionType=PersistenceUnitTransactionType.valueOf(transTypeString);
            } else if (persistenceUnitInfo!=null){
                transactionType=persistenceUnitInfo.getTransactionType();
            }
            
            if (!isValidationOnly(predeployProperties, false) && persistenceUnitInfo != null && transactionType == PersistenceUnitTransactionType.JTA) {
                if (predeployProperties.get(PersistenceUnitProperties.JTA_DATASOURCE) == null && persistenceUnitInfo.getJtaDataSource() == null) {
                    throw new PersistenceException(EntityManagerSetupException.jtaPersistenceUnitInfoMissingJtaDataSource(persistenceUnitInfo.getPersistenceUnitName()));
                }
            }
            
            // this flag is used to disable work done as a result of the LAZY hint on OneToOne mappings
            if(state == STATE_INITIAL || state == STATE_UNDEPLOYED) {
                enableWeaving = true;
                isWeavingStatic = false;
                String weaving = getConfigPropertyAsString(PersistenceUnitProperties.WEAVING);
                if (weaving != null && weaving.equalsIgnoreCase("false")) {
                    enableWeaving = false;
                }else if (weaving != null && weaving.equalsIgnoreCase("static")) {
                    isWeavingStatic = true;
                }
            }
            
            boolean throwExceptionOnFail = "true".equalsIgnoreCase(
                    EntityManagerFactoryProvider.getConfigPropertyAsStringLogDebug(PersistenceUnitProperties.TOPLINK_ORM_THROW_EXCEPTIONS, predeployProperties, "true", session));                
    
            ClassTransformer transformer = null;
            
            boolean weaveChangeTracking = false;
            boolean weaveLazy = false;
            boolean weaveFetchGroups = false;
            boolean weaveInternal = false;
            if (enableWeaving) {
                weaveChangeTracking = "true".equalsIgnoreCase(EntityManagerFactoryProvider.getConfigPropertyAsStringLogDebug(PersistenceUnitProperties.WEAVING_CHANGE_TRACKING, predeployProperties, "true", session));
                weaveLazy = "true".equalsIgnoreCase(EntityManagerFactoryProvider.getConfigPropertyAsStringLogDebug(PersistenceUnitProperties.WEAVING_LAZY, predeployProperties, "true", session));
                weaveFetchGroups = "true".equalsIgnoreCase(EntityManagerFactoryProvider.getConfigPropertyAsStringLogDebug(PersistenceUnitProperties.WEAVING_FETCHGROUPS, predeployProperties, "true", session));
                weaveInternal = "true".equalsIgnoreCase(EntityManagerFactoryProvider.getConfigPropertyAsStringLogDebug(PersistenceUnitProperties.WEAVING_INTERNAL, predeployProperties, "true", session));
            }
            if (!isSessionLoadedFromSessionsXML ) {
                // Create an instance of MetadataProcessor for specified persistence unit info
                processor = new MetadataProcessor(persistenceUnitInfo, session, privateClassLoader, enableWeaving);
                
                // Process the Object/relational metadata from XML and annotations.
                PersistenceUnitProcessor.processORMetadata(processor, throwExceptionOnFail);

                if (session.getIntegrityChecker().hasErrors()){
                    session.handleException(new IntegrityException(session.getIntegrityChecker()));
                }

                // The transformer is capable of altering domain classes to handle a LAZY hint for OneToOne mappings.  It will only
                // be returned if we we are mean to process these mappings
                if (enableWeaving) {                
                    // build a list of entities the persistence unit represented by this EntityManagerSetupImpl will use
                    Collection entities = PersistenceUnitProcessor.buildEntityList(processor.getProject(), privateClassLoader);
                    transformer = TransformerFactory.createTransformerAndModifyProject(session, entities, privateClassLoader, weaveLazy, weaveChangeTracking, weaveFetchGroups, weaveInternal);
                }
            } else {
                // The transformer is capable of altering domain classes to handle a LAZY hint for OneToOne mappings.  It will only
                // be returned if we we are meant to process these mappings.
                if (enableWeaving) {
                    // If deploying from a sessions-xml it is still desirable to allow the classes to be weaved.                
                    // build a list of entities the persistence unit represented by this EntityManagerSetupImpl will use
                    Collection persistenceClasses = new ArrayList(session.getProject().getDescriptors().keySet());
                    transformer = TransformerFactory.createTransformerAndModifyProject(session, persistenceClasses, privateClassLoader, weaveLazy, weaveChangeTracking, weaveFetchGroups, weaveInternal);
                }
            }
            // factoryCount is not incremented only in case of a first call to preDeploy
            // in non-container mode: this call is not associated with a factory
            // but rather done by JavaSECMPInitializer.callPredeploy (typically in preMain).
            if((state != STATE_INITIAL && state != STATE_UNDEPLOYED) || this.isInContainerMode()) {
                factoryCount++;
            }
            state = STATE_PREDEPLOYED;
            session.log(SessionLog.FINEST, SessionLog.PROPERTIES, "predeploy_end", new Object[]{getPersistenceUnitInfo().getPersistenceUnitName(), state, factoryCount});
            //gf3146: if static weaving is used, we should not return a transformer.  Transformer should still be created though as it modifies descriptors 
            if (isWeavingStatic) {
                return null;
            } else {
                return transformer;
            }
        } catch (RuntimeException ex) {
            state = STATE_PREDEPLOY_FAILED;
            session = null;
            throw new PersistenceException(EntityManagerSetupException.predeployFailed(persistenceUnitInfo.getPersistenceUnitName(), ex));
        }
    }


    /**
     * Check the provided map for an object with the given key.  If that object is not available, check the
     * System properties.  If it is not available from either location, return the default value.
     */
    public String getConfigPropertyAsString(String propertyKey, String defaultValue){
        return getConfigPropertyAsStringLogDebug(propertyKey, predeployProperties, defaultValue, session);
    }

    public String getConfigPropertyAsString(String propertyKey){
        return getConfigPropertyAsStringLogDebug(propertyKey, predeployProperties, session);
    }

    /**
     * Return the name of the session this SetupImpl is building. The session name is only known at deploy
     * time and if this method is called prior to that, this method will return null.
     */
    public String getDeployedSessionName(){
        return session != null ? session.getName() : null;
    }
    
    public PersistenceUnitInfo getPersistenceUnitInfo(){
        return persistenceUnitInfo;
    }
    
    public boolean isValidationOnly(Map m) {
        return isValidationOnly(m, true);
    }
    
    protected boolean isValidationOnly(Map m, boolean shouldMergeMap) {
        if (shouldMergeMap) {
            m = mergeWithExistingMap(m);
        }
        String validationOnlyString = getConfigPropertyAsStringLogDebug(PersistenceUnitProperties.TOPLINK_VALIDATION_ONLY_PROPERTY, m, session);
        if (validationOnlyString != null) {
            return Boolean.parseBoolean(validationOnlyString);
        } else {
            return false;
        }
    }
    
    public boolean shouldGetSessionOnCreateFactory(Map m) {
        m = mergeWithExistingMap(m);
        return isValidationOnly(m, false);
    }
    
    protected Map mergeWithExistingMap(Map m) {
        if(predeployProperties != null) {
            return mergeMaps(m, predeployProperties);
        } else if(persistenceUnitInfo != null) {
            return mergeMaps(m, persistenceUnitInfo.getProperties());
        } else {
            return m;
        }
    }

    public boolean isInContainerMode(){
        return isInContainerMode;
    }

    /**
     * Override the default login creation method.
     * If persistenceInfo is available, use the information from it to setup the login
     * and possibly to set readConnectionPool.
     */
    protected void updateLogins(Map m){
        DatasourceLogin login = session.getLogin();
    
        // Note: This call does not checked the stored persistenceUnitInfo or extended properties because
        // the map passed into this method should represent the full set of properties we expect to process

        String user = getConfigPropertyAsStringLogDebug(PersistenceUnitProperties.JDBC_USER, m, session);
        String password = getConfigPropertyAsStringLogDebug(PersistenceUnitProperties.JDBC_PASSWORD, m, session);
        if(user != null) {
            login.setUserName(user);
        }
        if (password != null) {
            login.setPassword(securableObjectHolder.getSecurableObject().decryptPassword(password));
        }

        String toplinkPlatform = PropertiesHandler.getPropertyValueLogDebug(PersistenceUnitProperties.TARGET_DATABASE, m, session);
        if (toplinkPlatform != null) {
            login.setPlatformClassName(toplinkPlatform, persistenceUnitInfo.getClassLoader());
        }
        
        PersistenceUnitTransactionType transactionType = persistenceUnitInfo.getTransactionType();
        //bug 5867753: find and override the transaction type using properties
        String transTypeString = getConfigPropertyAsStringLogDebug(PersistenceUnitProperties.TRANSACTION_TYPE, m, session);
        if (transTypeString != null) {
            transactionType = PersistenceUnitTransactionType.valueOf(transTypeString);
        }
        //find the jta datasource
        javax.sql.DataSource jtaDatasource = getDatasourceFromProperties(m, PersistenceUnitProperties.JTA_DATASOURCE, persistenceUnitInfo.getJtaDataSource());

        //find the non jta datasource  
        javax.sql.DataSource nonjtaDatasource = getDatasourceFromProperties(m, PersistenceUnitProperties.NON_JTA_DATASOURCE, persistenceUnitInfo.getNonJtaDataSource());

        if (isValidationOnly(m, false) && transactionType == PersistenceUnitTransactionType.JTA && jtaDatasource == null) {
            updateLoginDefaultConnector(login, m);
            return;
        }
        
        login.setUsesExternalTransactionController(transactionType == PersistenceUnitTransactionType.JTA);

        javax.sql.DataSource mainDatasource = null;
        javax.sql.DataSource readDatasource = null;
        if (login.shouldUseExternalTransactionController()) {
            // JtaDataSource is guaranteed to be non null - otherwise exception would've been thrown earlier
            mainDatasource = jtaDatasource;
            // only define readDatasource if there is jta mainDatasource
            readDatasource = nonjtaDatasource;
        } else {
            // JtaDataSource will be ignored because transactionType is RESOURCE_LOCAL
            if (jtaDatasource != null) {
                session.log(SessionLog.WARNING, SessionLog.TRANSACTION, "resource_local_persistence_init_info_ignores_jta_data_source", persistenceUnitInfo.getPersistenceUnitName());
            }
            if (nonjtaDatasource != null) {
                mainDatasource = nonjtaDatasource;
            } else {
                updateLoginDefaultConnector(login, m);
                return;
            }
        }

        // mainDatasource is guaranteed to be non null
        if (!(login.getConnector() instanceof JNDIConnector)) {
             JNDIConnector jndiConnector;
            if (mainDatasource instanceof DataSourceImpl) {
                //Bug5209363  Pass in the datasource name instead of the dummy datasource
                jndiConnector = new JNDIConnector(((DataSourceImpl)mainDatasource).getName());                
            } else {
                jndiConnector = new JNDIConnector(mainDatasource);                                
            }
            login.setConnector(jndiConnector);
            login.setUsesExternalConnectionPooling(true);
        }

        // set readLogin
        if(readDatasource != null) {
            DatasourceLogin readLogin = (DatasourceLogin)login.clone();
            readLogin.dontUseExternalTransactionController();
            JNDIConnector jndiConnector;
            if (readDatasource instanceof DataSourceImpl) {
                //Bug5209363  Pass in the datasource name instead of the dummy datasource
                jndiConnector = new JNDIConnector(((DataSourceImpl)readDatasource).getName());
            } else {
                jndiConnector = new JNDIConnector(readDatasource);                    
            }
            readLogin.setConnector(jndiConnector);
            session.setReadConnectionPool(readLogin);
        }
        
    }
    
    /**
     * This is used to return either the defaultDatasource or, if one exists, a datasource 
     * defined under the property from the Map m.  This method will build a DataSourceImpl
     * object to hold the url if the property in Map m defines a string instead of a datasource.
     */
    protected javax.sql.DataSource getDatasourceFromProperties(Map m, String property, javax.sql.DataSource defaultDataSource){
        Object datasource = getConfigPropertyLogDebug(property, m, session);
        if ( datasource == null ){
            return defaultDataSource;
        } 
        if ( datasource instanceof String){
            // Create a dummy DataSource that will throw an exception on access
            return new DataSourceImpl((String)datasource, null, null, null);
        }
        if ( !(datasource instanceof javax.sql.DataSource) ){
            //A warning should be enough.  Though an error might be better, the properties passed in could contain anything
            session.log(SessionLog.WARNING, SessionLog.PROPERTIES, "invalid_datasource_property_value", property, datasource);
            return defaultDataSource;
        }
        return (javax.sql.DataSource)datasource;
    }
    
    /**
     * In cases where there is no data source, we will use properties to configure the login for
     * our session.  This method gets those properties and sets them on the login.
     */
    protected void updateLoginDefaultConnector(DatasourceLogin login, Map m){
        //Login info might be already set with sessions.xml and could be overrided by session customizer after this
        //If login has default connector then JDBC properties update(override) the login info
        if ((login.getConnector() instanceof DefaultConnector)) {
            DatabaseLogin dbLogin = (DatabaseLogin)login;
            // Note: This call does not checked the stored persistenceUnitInfo or extended properties because
            // the map passed into this method should represent the full set of properties we expect to process
            String jdbcDriver = getConfigPropertyAsStringLogDebug(PersistenceUnitProperties.JDBC_DRIVER, m, session);
            String connectionString = getConfigPropertyAsStringLogDebug(PersistenceUnitProperties.JDBC_URL, m, session);
            if(connectionString != null) {
                dbLogin.setConnectionString(connectionString);
            }
            if(jdbcDriver != null) {
                dbLogin.setDriverClassName(jdbcDriver);
            }
        }
    }

    protected void updatePools(Map m) {
        // Sizes are irrelevant for external connection pool
        if(!session.getDefaultConnectionPool().getLogin().shouldUseExternalConnectionPooling()) {
            String strWriteMin = getConfigPropertyAsStringLogDebug(PersistenceUnitProperties.JDBC_WRITE_CONNECTIONS_MIN, m, session);
            if(strWriteMin != null) {
                session.getDefaultConnectionPool().setMinNumberOfConnections(Integer.parseInt(strWriteMin));
            }
            String strWriteMax = getConfigPropertyAsStringLogDebug(PersistenceUnitProperties.JDBC_WRITE_CONNECTIONS_MAX, m, session);
            if(strWriteMax != null) {
                session.getDefaultConnectionPool().setMaxNumberOfConnections(Integer.parseInt(strWriteMax));
            }
        }
        
        // Sizes and shared option are irrelevant for external connection pool
        if (!session.getReadConnectionPool().getLogin().shouldUseExternalConnectionPooling()) {
            String strReadMin = getConfigPropertyAsStringLogDebug(PersistenceUnitProperties.JDBC_READ_CONNECTIONS_MIN, m, session);
            if(strReadMin != null) {
                session.getReadConnectionPool().setMinNumberOfConnections(Integer.parseInt(strReadMin));
            }
            String strReadMax = getConfigPropertyAsStringLogDebug(PersistenceUnitProperties.JDBC_READ_CONNECTIONS_MAX, m, session);
            if (strReadMax != null) {
                session.getReadConnectionPool().setMaxNumberOfConnections(Integer.parseInt(strReadMax));
            }
            String strShouldUseShared = getConfigPropertyAsStringLogDebug(PersistenceUnitProperties.JDBC_READ_CONNECTIONS_SHARED, m,session);
            if(strShouldUseShared != null) {
                boolean shouldUseShared = Boolean.parseBoolean(strShouldUseShared);
                boolean sessionUsesShared = session.getReadConnectionPool() instanceof ReadConnectionPool;
                if(shouldUseShared != sessionUsesShared) {
                    Login readLogin = session.getReadConnectionPool().getLogin();
                    int nReadMin = session.getReadConnectionPool().getMinNumberOfConnections();
                    int nReadMax = session.getReadConnectionPool().getMaxNumberOfConnections();
                    if(shouldUseShared) {
                        session.useReadConnectionPool(nReadMin, nReadMax);
                    } else {
                        session.useExclusiveReadConnectionPool(nReadMin, nReadMax);
                    }
                    // keep original readLogin
                    session.getReadConnectionPool().setLogin(readLogin);
                }
            }
        }
    }
    
    /**
     * Normally when a property is missing nothing should be applied to the session.
     * However there are several session attributes that defaulted in EJB3 to the values
     * different from EclipseLink defaults.
     * This function applies defaults for such properties and registers the session.
     * All other session-related properties are applied in updateServerSession.
     * Note that updateServerSession may be called several times on the same session
     * (before login), but initServerSession is called just once - before the first call
     * to updateServerSession.
     * @param properties the persistence unit properties.
     */
    protected void initServerSession(Map properties) {
        assignCMP3Policy();

        // Register session that has been created earlier.
        addSessionToGlobalSessionManager();
    }

    /**
     * Set ServerSession name but do not register the session.
     * The session registration should be done in sync
     * with increment of the deployment counter, as otherwise the
     * undeploy will not behave correctly in case of a more
     * than one predeploy request for the same session name.
     * @param m the combined properties map.
     */
    protected void setServerSessionName(Map m) {
        // use default session name if none is provided
        String name = EntityManagerFactoryProvider.getConfigPropertyAsString(PersistenceUnitProperties.SESSION_NAME, m);
        if(name == null) {
            if (persistenceUnitInfo.getPersistenceUnitRootUrl() != null){
                name = persistenceUnitInfo.getPersistenceUnitRootUrl().toString() + "-" + persistenceUnitInfo.getPersistenceUnitName();
            } else {
                name = persistenceUnitInfo.getPersistenceUnitName();
            }
        } 

        session.setName(name);
    }

    /**
     * Make any changes to our ServerSession that can be made after it is created.
     */
    protected void updateServerSession(Map m, ClassLoader loader) {
        if (session == null || session.isConnected()) {
            return;
        }

        // In deploy Session name and ServerPlatform could've changed which will affect the loggers.
        boolean serverPlatformChanged = updateServerPlatform(m, loader);
        boolean sessionNameChanged = updateSessionName(m);

        updateLoggers(m, serverPlatformChanged, sessionNameChanged, loader);
        
        updateProfiler(m,loader);

        String shouldBindString = getConfigPropertyAsStringLogDebug(PersistenceUnitProperties.JDBC_BIND_PARAMETERS, m, session);
        if (shouldBindString != null) {
            session.getPlatform().setShouldBindAllParameters(Boolean.parseBoolean(shouldBindString));
        }

        updateLogins(m);
        if (!session.getLogin().shouldUseExternalTransactionController()) {
            session.getServerPlatform().disableJTA();
        }
        
        setSessionEventListener(loader);
        setExceptionHandler(loader);

        updatePools(m);
        
        if (!isSessionLoadedFromSessionsXML) {
             updateDescriptorCacheSettings(m, loader);
        }
        
        updateBatchWritingSetting(m);

        updateNativeSQLSetting();
        updateCacheStatementSettings();
        updateTemporalMutableSetting();
        
        // Customizers should be processed last
        processDescriptorCustomizers(m, loader);
        processSessionCustomizer(m, loader);
        
        setDescriptorNamedQueries();        
    }

    /** 
     * This sets the isInContainerMode flag.
     * "true" indicates container case, "false" - SE.
     */ 
    public void setIsInContainerMode(boolean isInContainerMode) {
        this.isInContainerMode = isInContainerMode;
    }

    protected void processSessionCustomizer(Map m, ClassLoader loader) {
        String sessionCustomizerClassName = getConfigPropertyAsStringLogDebug(PersistenceUnitProperties.SESSION_CUSTOMIZER, m, session);
        if (sessionCustomizerClassName == null) {
            return;
        }        
        Class sessionCustomizerClass = findClassForProperty(sessionCustomizerClassName, PersistenceUnitProperties.SESSION_CUSTOMIZER, loader);
        SessionCustomizer sessionCustomizer;
        try {
            sessionCustomizer = (SessionCustomizer)sessionCustomizerClass.newInstance();
            sessionCustomizer.customize(session);
        } catch (Exception ex) {
            throw EntityManagerSetupException.failedWhileProcessingProperty(PersistenceUnitProperties.SESSION_CUSTOMIZER, sessionCustomizerClassName, ex);
        }
    }

    protected void initOrUpdateLogging(Map m, SessionLog log) {
        String logLevelString = PropertiesHandler.getPropertyValueLogDebug(PersistenceUnitProperties.LOGGING_LEVEL, m, session);
        if (logLevelString != null) {
            log.setLevel(AbstractSessionLog.translateStringToLoggingLevel(logLevelString));
        }
        // category-specific logging level
        Map categoryLogLevelMap = PropertiesHandler.getPrefixValuesLogDebug(PersistenceUnitProperties.CATEGORY_LOGGING_LEVEL_, m, session);
        if(!categoryLogLevelMap.isEmpty()) {
            Iterator it = categoryLogLevelMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry entry = (Map.Entry)it.next();
                String category = (String)entry.getKey();
                String value = (String)entry.getValue();
                log.setLevel(AbstractSessionLog.translateStringToLoggingLevel(value), category);
            }
        }
        
        String tsString = getConfigPropertyAsStringLogDebug(PersistenceUnitProperties.LOGGING_TIMESTAMP, m, session);
        if (tsString != null) {
            log.setShouldPrintDate(Boolean.parseBoolean(tsString));
        }
        String threadString = getConfigPropertyAsStringLogDebug(PersistenceUnitProperties.LOGGING_THREAD, m, session);
        if (threadString != null) {
            log.setShouldPrintThread(Boolean.parseBoolean(threadString));
        }
        String sessionString = getConfigPropertyAsStringLogDebug(PersistenceUnitProperties.LOGGING_SESSION, m, session);
        if (sessionString != null) {
            log.setShouldPrintSession(Boolean.parseBoolean(sessionString));
        }
        String exString = getConfigPropertyAsStringLogDebug(PersistenceUnitProperties.LOGGING_EXCEPTIONS, m, session);
        if (exString != null) {
            log.setShouldLogExceptionStackTrace(Boolean.parseBoolean(exString));
        }
    }

    /**
     * Updates server session name if changed.
     * @return true if the name has changed.
     */
    protected boolean updateSessionName(Map m) {
        String newName = getConfigPropertyAsStringLogDebug(PersistenceUnitProperties.SESSION_NAME, m, session);
        if(newName == null || newName.equals(session.getName())) {
            return false;
        }

        removeSessionFromGlobalSessionManager();
        session.setName(newName);
        addSessionToGlobalSessionManager();

        return true;
    }
    
    protected void processDescriptorCustomizers(Map m, ClassLoader loader) {
        Map customizerMap = PropertiesHandler.getPrefixValuesLogDebug(PersistenceUnitProperties.DESCRIPTOR_CUSTOMIZER_, m, session);
        if (customizerMap.isEmpty()) {
            return;
        }

        Iterator it = customizerMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry)it.next();
            String name = (String)entry.getKey();
            
            ClassDescriptor descriptor = session.getDescriptorForAlias(name);
            if (descriptor == null) {
                try {
                    Class javaClass = findClass(name, loader);
                    descriptor = session.getDescriptor(javaClass);
                } catch (Exception ex) {
                    // Ignore exception
                }
            }
            if (descriptor != null) {
                String customizerClassName = (String)entry.getValue();
                Class customizerClass = findClassForProperty(customizerClassName, PersistenceUnitProperties.DESCRIPTOR_CUSTOMIZER_ + name, loader);
                try {
                    DescriptorCustomizer customizer = (DescriptorCustomizer)customizerClass.newInstance();
                    customizer.customize(descriptor);
                } catch (Exception ex) {
                    throw EntityManagerSetupException.failedWhileProcessingProperty(PersistenceUnitProperties.DESCRIPTOR_CUSTOMIZER_ + name, customizerClassName, ex);
                }
            }
        }
    }
    
    public boolean isInitial() {
        return state == STATE_INITIAL;
    }

    public boolean isPredeployed() {
        return state == STATE_PREDEPLOYED;
    }

    public boolean isDeployed() {
        return state == STATE_DEPLOYED;
    }

    public boolean isUndeployed() {
        return state == STATE_UNDEPLOYED;
    }

    public boolean isPredeployFailed() {
        return state == STATE_PREDEPLOY_FAILED;
    }

    public boolean isDeployFailed() {
        return state == STATE_DEPLOY_FAILED;
    }

    public int getFactoryCount() {
        return factoryCount;
    }

    public boolean shouldRedeploy() {
        return state == STATE_UNDEPLOYED || state == STATE_PREDEPLOY_FAILED;
    }
    
    /**
     * Undeploy may be called several times, but only the call that decreases
     * factoryCount to 0 disconnects the session and removes it from the session manager.
     * This method and predeploy - the only methods altering factoryCount - should be synchronized.
     * After undeploy call that turns factoryCount to 0:
     *   session==null;
     *   PREDEPLOYED, DEPLOYED and DEPLOYED_FAILED states change to UNDEPLOYED state.
     */
    public synchronized void undeploy() {
        if (state == STATE_INITIAL || state == STATE_PREDEPLOY_FAILED || state == STATE_UNDEPLOYED) {
            // must already have factoryCount==0 and session==null
            return;
        }
        // state is PREDEPLOYED, DEPLOYED or DEPLOY_FAILED
        session.log(SessionLog.FINEST, SessionLog.PROPERTIES, "undeploy_begin", new Object[]{getPersistenceUnitInfo().getPersistenceUnitName(), state, factoryCount});
        try {
            factoryCount--;
            if(factoryCount > 0) {
                return;
            }
            state = STATE_UNDEPLOYED;
            removeSessionFromGlobalSessionManager();
        } finally {
            session.log(SessionLog.FINEST, SessionLog.PROPERTIES, "undeploy_end", new Object[]{getPersistenceUnitInfo().getPersistenceUnitName(), state, factoryCount});
            if(state == STATE_UNDEPLOYED) {
                session = null;
            }
        }
    }    
    
    /**
     * Allow customized session event listener to be added into session.
     * The method needs to be called in deploy stage.
     */
    protected void setSessionEventListener(ClassLoader loader){
        //Set event listener if it has been specified.
        String sessionEventListenerClassName = EntityManagerFactoryProvider.getConfigPropertyAsStringLogDebug(PersistenceUnitProperties.SESSION_EVENT_LISTENER_CLASS, predeployProperties, session);
        if(sessionEventListenerClassName!=null){
            Class sessionEventListenerClass = findClassForProperty(sessionEventListenerClassName,PersistenceUnitProperties.SESSION_EVENT_LISTENER_CLASS, loader);
            try {
                SessionEventListener sessionEventListener = (SessionEventListener)buildObjectForClass(sessionEventListenerClass, SessionEventListener.class);
                if(sessionEventListener!=null){
                    session.getEventManager().getListeners().add(sessionEventListener);
                } else {
                    session.handleException(ValidationException.invalidSessionEventListenerClass(sessionEventListenerClassName));
                }
            } catch (IllegalAccessException e) {
                session.handleException(ValidationException.cannotInstantiateSessionEventListenerClass(sessionEventListenerClassName,e));
            } catch (PrivilegedActionException e) {
                session.handleException(ValidationException.cannotInstantiateSessionEventListenerClass(sessionEventListenerClassName,e));
            } catch (InstantiationException e) {
                session.handleException(ValidationException.cannotInstantiateSessionEventListenerClass(sessionEventListenerClassName,e));
            }
        }
    }
    
    /**
     * Allow customized exception handler to be added into session.
     * The method needs to be called in deploy and pre-deploy stage.
     */
    protected void setExceptionHandler(ClassLoader loader){
        //Set exception handler if it was specified.
        String exceptionHandlerClassName = EntityManagerFactoryProvider.getConfigPropertyAsStringLogDebug(PersistenceUnitProperties.EXCEPTION_HANDLER_CLASS, predeployProperties, session);
        if(exceptionHandlerClassName!=null){
            Class exceptionHandlerClass = findClassForProperty(exceptionHandlerClassName,PersistenceUnitProperties.EXCEPTION_HANDLER_CLASS, loader);
            try {
                ExceptionHandler exceptionHandler = (ExceptionHandler)buildObjectForClass(exceptionHandlerClass, ExceptionHandler.class);
                if (exceptionHandler!=null){
                    session.setExceptionHandler(exceptionHandler);
                } else {
                    session.handleException(ValidationException.invalidExceptionHandlerClass(exceptionHandlerClassName));
                }
            } catch (IllegalAccessException e) {
                session.handleException(ValidationException.cannotInstantiateExceptionHandlerClass(exceptionHandlerClassName,e));
            } catch (PrivilegedActionException e) {
                session.handleException(ValidationException.cannotInstantiateExceptionHandlerClass(exceptionHandlerClassName,e));
            } catch (InstantiationException e) {
                session.handleException(ValidationException.cannotInstantiateExceptionHandlerClass(exceptionHandlerClassName,e));
            }
        }
    }
    
    /**
     * Update batch writing setting.
     * The method needs to be called in deploy stage.
     */
    protected void updateBatchWritingSetting(Map persistenceProperties) {
        String batchWritingSettingString = PropertiesHandler.getPropertyValueLogDebug(PersistenceUnitProperties.BATCH_WRITING, persistenceProperties, session);
        if (batchWritingSettingString != null) {
             session.getPlatform().setUsesBatchWriting(batchWritingSettingString != BatchWriting.None);
             if (batchWritingSettingString == BatchWriting.JDBC) {
                 session.getPlatform().setUsesJDBCBatchWriting(true);
                 session.getPlatform().setUsesNativeBatchWriting(false);
             } else if (batchWritingSettingString == BatchWriting.Buffered) {
                 session.getPlatform().setUsesJDBCBatchWriting(false);
                 session.getPlatform().setUsesNativeBatchWriting(false);
             } else if (batchWritingSettingString == BatchWriting.OracleJDBC) {
                 session.getPlatform().setUsesNativeBatchWriting(true);
                 session.getPlatform().setUsesJDBCBatchWriting(true);
             }
        }
    }
    
    /**
     * Enable or disable the capability of Native SQL function.  
     * The method needs to be called in deploy stage.
     */
    protected void updateNativeSQLSetting(){
        //Set Native SQL flag if it was specified.
        String nativeSQLString = EntityManagerFactoryProvider.getConfigPropertyAsStringLogDebug(PersistenceUnitProperties.NATIVE_SQL, predeployProperties, session);
        if(nativeSQLString!=null){
           if(nativeSQLString.equalsIgnoreCase("true") ){
                 session.getProject().getLogin().useNativeSQL();
           }else if (nativeSQLString.equalsIgnoreCase("false")){
                 session.getProject().getLogin().dontUseNativeSQL();
           }else{
                 session.handleException(ValidationException.invalidBooleanValueForSettingNativeSQL(nativeSQLString));
           }
        }
    }

    /**
     * Enable or disable statements cached, update statements cache size. 
     * The method needs to be called in deploy stage. 
     */
    protected void updateCacheStatementSettings(){
        // Cache statements if flag was specified.
        String statmentsNeedBeCached = EntityManagerFactoryProvider.getConfigPropertyAsStringLogDebug(PersistenceUnitProperties.CACHE_STATEMENTS, predeployProperties, session);
        if (statmentsNeedBeCached!=null) {
            if (statmentsNeedBeCached.equalsIgnoreCase("true")) {
                if (session.getConnectionPools().size()>0){//And if connection pooling is configured,
                    session.getProject().getLogin().setShouldCacheAllStatements(true);
                 } else {
                    session.log(SessionLog.WARNING, SessionLog.PROPERTIES, "persistence_unit_ignores_statments_cache_setting", new Object[]{null});
                 }
            } else if (statmentsNeedBeCached.equalsIgnoreCase("false")) {
                session.getProject().getLogin().setShouldCacheAllStatements(false);
            } else {
                session.handleException(ValidationException.invalidBooleanValueForEnableStatmentsCached(statmentsNeedBeCached));
            }
        }
        
        // Set statement cache size if specified.
        String cacheStatementsSize = EntityManagerFactoryProvider.getConfigPropertyAsStringLogDebug(PersistenceUnitProperties.CACHE_STATEMENTS_SIZE, predeployProperties, session);
        if (cacheStatementsSize!=null) {
            try {
                session.getProject().getLogin().setStatementCacheSize(Integer.parseInt(cacheStatementsSize));
            } catch (NumberFormatException e) {
                session.handleException(ValidationException.invalidCacheStatementsSize(cacheStatementsSize,e.getMessage()));
            }
        }
    }

    /**
     * Enable or disable default temporal mutable setting. 
     * The method needs to be called in deploy stage. 
     */
    protected void updateTemporalMutableSetting() {
        // Cache statements if flag was specified.
        String temporalMutable = EntityManagerFactoryProvider.getConfigPropertyAsStringLogDebug(PersistenceUnitProperties.TEMPORAL_MUTABLE, predeployProperties, session);
        if (temporalMutable != null) {
            if (temporalMutable.equalsIgnoreCase("true")) {
               session.getProject().setDefaultTemporalMutable(true);
            } else if (temporalMutable.equalsIgnoreCase("false")) {
               session.getProject().setDefaultTemporalMutable(false);
            } else {
                session.handleException(ValidationException.invalidBooleanValueForProperty(temporalMutable, PersistenceUnitProperties.TEMPORAL_MUTABLE));
            }
        }
    }
    
    /**
     * Copy named queries defined in EclipseLink descriptor into the session if it was indicated to do so.
     */
    protected void setDescriptorNamedQueries() {
        // Copy named queries to session if the flag has been specified.
        String addNamedQueriesString  = EntityManagerFactoryProvider.getConfigPropertyAsStringLogDebug(PersistenceUnitProperties.INCLUDE_DESCRIPTOR_QUERIES, predeployProperties, session);
        if (addNamedQueriesString!=null) {
            if (addNamedQueriesString.equalsIgnoreCase("true")) {
                session.copyDescriptorNamedQueries(false);
            } else {
                if (!addNamedQueriesString.equalsIgnoreCase("false")) {
                   session.handleException(ValidationException.invalidBooleanValueForAddingNamedQueries(addNamedQueriesString));
                }
            }
        }
    }
}
