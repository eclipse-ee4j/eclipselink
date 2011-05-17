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
 *     
 *     05/28/2008-1.0M8 Andrei Ilitchev 
 *        - 224964: Provide support for Proxy Authentication through JPA.
 *        Added updateConnectionPolicy method to support EXCLUSIVE_CONNECTION property.
 *        Some methods, like setSessionEventListener called from deploy still used predeploy properties,
 *        that meant it was impossible to set listener through createEMF property in SE case with an agent - fixed that.
 *        Also if creating / closing the same emSetupImpl many times (24 in my case) "java.lang.OutOfMemoryError: PermGen space" resulted:
 *        partially fixed partially worked around this - see a big comment in predeploy method.
 *     12/23/2008-1.1M5 Michael O'Brien 
 *        - 253701: add persistenceInitializationHelper field used by undeploy() to clear the JavaSECMPInitializer
 *     10/14/2009-2.0      Michael O'Brien 
 *        - 266912: add Metamodel instance field as part of the JPA 2.0 implementation
 *     10/21/2009-2.0 Guy Pelletier 
 *       - 290567: mappedbyid support incomplete
 *     cdelahun - Bug 214534: changes to allow JMSPublishingTransportManager configuration through properties
 *     05/14/2010-2.1 Guy Pelletier 
 *       - 253083: Add support for dynamic persistence using ORM.xml/eclipselink-orm.xml
 *     04/01/2011-2.3 Guy Pelletier 
 *       - 337323: Multi-tenant with shared schema support (part 2)
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa;

import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.util.*;
import java.io.FileWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URLDecoder;

import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.ManagedType;
import javax.persistence.metamodel.Metamodel;
import javax.persistence.spi.PersistenceUnitInfo;
import javax.persistence.spi.ClassTransformer;
import javax.persistence.PersistenceException;
import javax.persistence.ValidationMode;

import org.eclipse.persistence.internal.databaseaccess.DatasourcePlatform;
import org.eclipse.persistence.internal.descriptors.OptimisticLockingPolicy;
import org.eclipse.persistence.internal.descriptors.OptimisticLockingPolicy.LockOnChange;
import org.eclipse.persistence.internal.jpa.weaving.PersistenceWeaver;
import org.eclipse.persistence.internal.jpa.weaving.TransformerFactory;
import org.eclipse.persistence.sessions.JNDIConnector;
import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.logging.DefaultSessionLog;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedClassForName;
import org.eclipse.persistence.internal.security.PrivilegedGetDeclaredField;
import org.eclipse.persistence.internal.security.PrivilegedGetDeclaredMethod;
import org.eclipse.persistence.internal.security.PrivilegedMethodInvoker;        
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.DatabaseSessionImpl;
import org.eclipse.persistence.internal.sessions.PropertiesHandler;
import org.eclipse.persistence.sequencing.Sequence;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.sessions.server.ConnectionPolicy;
import org.eclipse.persistence.sessions.server.ConnectionPool;
import org.eclipse.persistence.sessions.server.ExternalConnectionPool;
import org.eclipse.persistence.sessions.server.ReadConnectionPool;
import org.eclipse.persistence.sessions.server.ServerSession;
import org.eclipse.persistence.internal.jpa.metadata.MetadataHelper;
import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;
import org.eclipse.persistence.internal.jpa.metadata.MetadataProcessor;
import org.eclipse.persistence.internal.jpa.metadata.MetadataProject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAsmFactory;
import org.eclipse.persistence.internal.jpa.metamodel.MetamodelImpl;
import org.eclipse.persistence.sessions.broker.SessionBroker;
import org.eclipse.persistence.sessions.coordination.RemoteCommandManager;
import org.eclipse.persistence.sessions.coordination.TransportManager;
import org.eclipse.persistence.sessions.coordination.jms.JMSTopicTransportManager;
import org.eclipse.persistence.sessions.coordination.jms.JMSPublishingTransportManager;
import org.eclipse.persistence.sessions.coordination.rmi.RMITransportManager;
import org.eclipse.persistence.sessions.factories.SessionManager;
import org.eclipse.persistence.sessions.factories.XMLSessionConfigLoader;
import org.eclipse.persistence.annotations.IdValidation;
import org.eclipse.persistence.config.BatchWriting;
import org.eclipse.persistence.config.CacheCoordinationProtocol;
import org.eclipse.persistence.config.DescriptorCustomizer;
import org.eclipse.persistence.config.ExclusiveConnectionMode;
import org.eclipse.persistence.config.LoggerType;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.config.ProfilerType;
import org.eclipse.persistence.config.SessionCustomizer;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.partitioning.PartitioningPolicy;
import org.eclipse.persistence.platform.server.CustomServerPlatform;
import org.eclipse.persistence.platform.server.ServerPlatform;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.helper.JPAClassLoaderHolder;
import org.eclipse.persistence.internal.helper.JPAConversionManager;
import javax.persistence.spi.PersistenceUnitTransactionType;

import org.eclipse.persistence.internal.jpa.deployment.PersistenceUnitProcessor;
import org.eclipse.persistence.internal.jpa.deployment.BeanValidationInitializationHelper;
import org.eclipse.persistence.internal.jpa.deployment.SEPersistenceUnitInfo;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.security.PrivilegedNewInstanceFromClass;
import org.eclipse.persistence.internal.jpa.jdbc.DataSourceImpl;
import org.eclipse.persistence.internal.security.SecurableObjectHolder;
import org.eclipse.persistence.jpa.metadata.MetadataSource;
import org.eclipse.persistence.jpa.metadata.XMLMetadataSource;
import org.eclipse.persistence.platform.database.converters.StructConverter;
import org.eclipse.persistence.platform.database.partitioning.DataPartitioningCallback;
import org.eclipse.persistence.platform.server.ServerPlatformBase;
import org.eclipse.persistence.tools.profiler.PerformanceMonitor;
import org.eclipse.persistence.tools.profiler.PerformanceProfiler;
import org.eclipse.persistence.tools.profiler.QueryMonitor;
import org.eclipse.persistence.tools.schemaframework.SchemaManager;

import static org.eclipse.persistence.internal.jpa.EntityManagerFactoryProvider.*;

/**
 * INTERNAL:
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
     * 
     */

    // this name should uniquely identify the persistence unit
    protected String persistenceUnitUniqueName;
    // session name should uniquely identify the session
    protected String sessionName;

    protected MetadataProcessor processor = null;
    /** Holds a reference to the weaver class transformer so it can be cleared after login. */
    protected PersistenceWeaver weaver = null;
    protected PersistenceUnitInfo persistenceUnitInfo = null;
    // count a number of open factories that use this object.
    protected int factoryCount = 0;
    protected DatabaseSessionImpl session = null;
    // true if predeploy called by createContainerEntityManagerFactory; false - createEntityManagerFactory
    protected boolean isInContainerMode = false;
    protected boolean isSessionLoadedFromSessionsXML=false;
    // indicates whether weaving was used on the first run through predeploy (in STATE_INITIAL)
    protected Boolean enableWeaving = null;
    // indicates that classes have already been woven
    protected boolean isWeavingStatic = false;
    // used by static weaving
    protected StaticWeaveInfo staticWeaveInfo;
    protected SecurableObjectHolder securableObjectHolder = new SecurableObjectHolder();

    // 266912: Criteria API and Metamodel API (See Ch 5 of the JPA 2.0 Specification)
    /** Reference to the Metamodel for this deployment and session. 
     * Please use the accessor and not the instance variable directly*/
    private Metamodel metaModel;     
    
    protected List<StructConverter> structConverters = null;
    // factoryCount==0; session==null
    public static final String STATE_INITIAL        = "Initial";
    
    // session != null
    public static final String STATE_PREDEPLOYED    = "Predeployed";
    
    // factoryCount>0; session != null; session stored in SessionManager
    // for compositeMember factoryCount is always 0; session is never stored in SessionBroker
    public static final String STATE_DEPLOYED       = "Deployed";
    
    // factoryCount==0; session==null
    public static final String STATE_PREDEPLOY_FAILED="PredeployFailed";
    
    // factoryCount>0; session != null
    // for compositeMember factoryCount is always 0
    public static final String STATE_DEPLOY_FAILED  = "DeployFailed";
    
    // factoryCount==0; session==null
    public static final String STATE_UNDEPLOYED     = "Undeployed";

    // factoryCount==0; session==null
    // only composite member persistence unit can be in this state
    public static final String STATE_HALF_PREDEPLOYED_COMPOSITE_MEMBER = "HalfPredeployedCompositeMember";
    /**
     *     Initial -----> HalfPredeployedCompositeMember -----> PredeployFailed
     *                    |        ^                   |
     *                    V------->|                   V
     *                                                Predeployed
     */

    protected String state = STATE_INITIAL;

    /**
     *     Initial -----> PredeployFailed
     *           |         |
     *           V         V
     *         Predeployed --> DeployFailed
     *           |         |        |
     *           V         V        V
     *         Deployed -> Undeployed
     *                                  
     */
    
    
    public static final String ERROR_LOADING_XML_FILE = "error_loading_xml_file";
    public static final String EXCEPTION_LOADING_ENTITY_CLASS = "exception_loading_entity_class";

    /*
     * Properties used to generate sessionName if none is provided.
     */
    public static String[] connectionPropertyNames = {
        PersistenceUnitProperties.TRANSACTION_TYPE,
        PersistenceUnitProperties.JTA_DATASOURCE,
        PersistenceUnitProperties.NON_JTA_DATASOURCE,
        PersistenceUnitProperties.JDBC_URL,
        PersistenceUnitProperties.JDBC_USER,
    };
    
    /*
     * Composite, not null only if it's a composite member.
     */
    protected EntityManagerSetupImpl compositeEmSetupImpl;

    /*
     * Composite members, not null only if it's a composite.
     */
    protected Set<EntityManagerSetupImpl> compositeMemberEmSetupImpls;
    
    /*
     * In HalfPredeployedCompositeMember predeploy method called several times,
     * each call uses mode, then updating it before returning.
     * So mode value could be viewed as a substate of HalfPredeployedCompositeMember state.
     * The mode is required for staging of processing OR metadata for composite members:
     * each processing stage should be completed for ALL composite members before
     * any one of then could proceed to the next processing stage. 
     */
    PersistenceUnitProcessor.Mode mode;
    
    boolean throwExceptionOnFail;
    boolean weaveChangeTracking;
    boolean weaveLazy;
    boolean weaveEager;
    boolean weaveFetchGroups;
    boolean weaveInternal;
    
    public EntityManagerSetupImpl(String persistenceUnitUniqueName, String sessionName) {
        this.persistenceUnitUniqueName = persistenceUnitUniqueName;
        this.sessionName = sessionName;
    }
    
    public EntityManagerSetupImpl() {
        this("", "");
    }
    
    /*
     * Return session name if specified.
     * Otherwise build one from the connection properties names and values.
     * Note that specifying value "" in properties causes 
     * the property value specified in PersistenceUnitInfo to be ignored.
     * Never returns null.
     */
    public static String getOrBuildSessionName(Map properties, PersistenceUnitInfo puInfo, String persistenceUnitUniqueName) {
        // if SESSION_NAME is specified in either properties or puInfo properties - use it as session name (unless it's an empty String).
        String sessionName = (String)properties.get(PersistenceUnitProperties.SESSION_NAME);
        if (sessionName == null) {
            sessionName = (String)puInfo.getProperties().get(PersistenceUnitProperties.SESSION_NAME);
        }
        // Specifying empty String in properties allows to remove SESSION_NAME specified in puInfo properties.
        if(sessionName != null && sessionName.length() > 0) {
            return sessionName;
        }

        if (isComposite(puInfo)) {
            // Composite doesn't use connection properties.
            return persistenceUnitUniqueName;
        } else {
            // In case no SESSION_NAME specified (or empty String) - build one
            // by concatenating persistenceUnitUniqueName and suffix build of connection properties' names and values.
            return persistenceUnitUniqueName + buildSessionNameSuffixFromConnectionProperties(properties);
        }
    }
        
    protected static String buildSessionNameSuffixFromConnectionProperties(Map properties) {        
        String suffix = "";
        for(int i=0; i < connectionPropertyNames.length; i++) {
            String name = connectionPropertyNames[i];
            Object value = properties.get(name);
            if(value != null) {
                String strValue = null;
                if(value instanceof String) {
                    strValue = (String)value;
                } else {
                    if(value instanceof javax.sql.DataSource) {
                        // value of JTA_DATASOURCE / NON_JTA_DATASOURCE may be a DataSource (we would prefer DataSource name)
                        strValue = Integer.toString(System.identityHashCode(value));
                    } else if(value instanceof PersistenceUnitTransactionType) {
                        strValue = value.toString();
                    }
                }
                // don't set an empty String
                if(strValue.length() > 0) {
                    suffix += "_" + Helper.getShortClassName(name) + "=" + strValue;
                }
            }
        }
        return suffix;
    }
    
    /*
     * Should only be called when emSetupImpl created during SE initialization is set into a new EMF.
     * emSetupImpl must be in PREDEPLOYED state.
     */
    public void changeSessionName(String newSessionName) {
        if(!session.getName().equals(newSessionName)) {
            session.log(SessionLog.FINEST, SessionLog.PROPERTIES, "session_name_change", new Object[]{getPersistenceUnitInfo().getPersistenceUnitName(), session.getName(), newSessionName});
            sessionName = newSessionName;
            session.setName(newSessionName);
        }
    }
    
    /**
     * This method can be used to ensure the session represented by emSetupImpl
     * is removed from the SessionManager.
     */
    protected void removeSessionFromGlobalSessionManager() {
        if (session != null){
            try {
                if(session.isConnected()) {
                    session.logout();
                }
            } finally {
                SessionManager.getManager().getSessions().remove(session.getName());
            }
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
     * @param additionalProperties added to persistence unit properties for updateServerSession overriding existing properties.
     *              In JSE case it allows to alter properties in main (as opposed to preMain where preDeploy is called).
     * @return An EntityManagerFactory to be used by the Container to obtain EntityManagers
     */
    public DatabaseSessionImpl deploy(ClassLoader realClassLoader, Map additionalProperties) {
        if (state != STATE_PREDEPLOYED && state != STATE_DEPLOYED) {
            if (mustBeCompositeMember()) {
                throw new PersistenceException(EntityManagerSetupException.compositeMemberCannotBeUsedStandalone(persistenceUnitInfo.getPersistenceUnitName()));
            }
            throw new PersistenceException(EntityManagerSetupException.cannotDeployWithoutPredeploy(persistenceUnitInfo.getPersistenceUnitName(), state));
        }
        // state is PREDEPLOYED or DEPLOYED
        session.log(SessionLog.FINEST, SessionLog.PROPERTIES, "deploy_begin", new Object[]{getPersistenceUnitInfo().getPersistenceUnitName(), session.getName(), state, factoryCount});
        
        try {
            Map deployProperties = mergeMaps(additionalProperties, persistenceUnitInfo.getProperties());
            translateOldProperties(deployProperties, session);
            if (isComposite()) {
                updateCompositeMembersProperties(deployProperties);
            }
            
            if (state == STATE_PREDEPLOYED) {
                synchronized (session) {
                    if (state == STATE_PREDEPLOYED) {
                        try {
                            if (!isSessionLoadedFromSessionsXML) {
                                if (isComposite()) {
                                    deployCompositeMembers(deployProperties, realClassLoader);
                                } else {
                                    // listeners and queries require the real classes and are therefore built during deploy using the realClassLoader
                                    processor.setClassLoader(realClassLoader);
                                    processor.createDynamicClasses();
                                    
                                    // The project is initially created using class names rather than classes.  This call will make the conversion.
                                    // If the session was loaded from sessions.xml this will also convert the descriptor classes to the correct class loader.
                                    session.getProject().convertClassNamesToClasses(realClassLoader);
                                    
                                    processor.addEntityListeners();
                                    if (!isCompositeMember()) {
                                        addBeanValidationListeners(deployProperties, realClassLoader);
                                    }
                                    processor.addNamedQueries();
                                    
                                    // Process the customizers last.
                                    processor.processCustomizers();
                                    
                                    structConverters = processor.getStructConverters();
                                }
                                
                                processor = null;
                            } else {
                                // The project is initially created using class names rather than classes.  This call will make the conversion.
                                // If the session was loaded from sessions.xml this will also convert the descriptor classes to the correct class loader.
                                session.getProject().convertClassNamesToClasses(realClassLoader);
                            }
                    
                            initSession();
                    
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
            if (!isCompositeMember()) {
                if (!session.isConnected()) {
                    synchronized (session) {
                        if(!session.isConnected()) {
                            session.setProperties(deployProperties);
                            updateSession(deployProperties, realClassLoader);
                            if (isValidationOnly(deployProperties, false)) {
                                /**
                                 * for 324213 we could add a session.loginAndDetectDatasource() call 
                                 * before calling initializeDescriptors when validation-only is True
                                 * to avoid a native sequence exception on a generic DatabasePlatform 
                                 * by auto-detecting the correct DB platform.
                                 * However, this would introduce a DB login when validation is on 
                                 * - in opposition to the functionality of the property (to only validate)
                                 */
                                session.initializeDescriptors();
                            } else {
                                if (isSessionLoadedFromSessionsXML) {
                                    if (!session.isConnected()) {
                                        session.login();
                                    }
                                } else {
                                    login(session, deployProperties);
                                }
                                if (!isSessionLoadedFromSessionsXML) {
                                    addStructConverters();
                                }
                                generateDDL(deployProperties);
                            }
                        }
                    }
                }
                // 266912: Initialize the Metamodel, a login should have already occurred.
                try {
                    this.getMetamodel();
                } catch (Exception e) {
                    session.log(SessionLog.FINEST, SessionLog.METAMODEL, "metamodel_init_failed", new Object[]{e.getMessage()});
                }
            }
            // Clear the weaver's reference to meta-data information, as it is held by the class loader and will never gc.
            if (this.weaver != null) {
                this.weaver.clear();
                this.weaver = null;
            }            
            
            return session;
        } catch (Exception exception) {
            PersistenceException persistenceException = null;
            if (exception instanceof PersistenceException) {
                persistenceException = (PersistenceException)exception;
            } else {
                persistenceException = new PersistenceException(exception);
            }
            session.logThrowable(SessionLog.SEVERE, SessionLog.EJB, exception);
            throw persistenceException;
        } finally {
            session.log(SessionLog.FINEST, SessionLog.PROPERTIES, "deploy_end", new Object[]{getPersistenceUnitInfo().getPersistenceUnitName(), session.getName(), state, factoryCount});
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
    public void addStructConverters(){
        if (this.compositeMemberEmSetupImpls == null) {
            for (StructConverter structConverter : structConverters){
                if (session.getPlatform().getTypeConverters().get(structConverter.getJavaType()) != null){
                    throw ValidationException.twoStructConvertersAddedForSameClass(structConverter.getJavaType().getName());
                }
                session.getPlatform().addStructConverter(structConverter);
            }
        } else {
            // composite
            for(EntityManagerSetupImpl compositeMemberEmSetupImpl : this.compositeMemberEmSetupImpls) {
                if (!compositeMemberEmSetupImpl.structConverters.isEmpty()) {
                    String compositeMemberPuName = compositeMemberEmSetupImpl.getPersistenceUnitInfo().getPersistenceUnitName();
                    // debug output added to make it easier to navigate the log because the method is called outside of composite member deploy
                    compositeMemberEmSetupImpl.session.log(SessionLog.FINEST, SessionLog.PROPERTIES, "composite_member_begin_call", new Object[]{"addStructConverters", compositeMemberPuName, state});
                    compositeMemberEmSetupImpl.addStructConverters();
                    compositeMemberEmSetupImpl.session.log(SessionLog.FINEST, SessionLog.PROPERTIES, "composite_member_end_call", new Object[]{"addStructConverters", compositeMemberPuName, state});
                }
            }
        }
    }

    /**
     * Assign a CMP3Policy to each descriptor, and sets the OptimisticLockingPolicy's LockOnChangeMode if applicable.
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
            OptimisticLockingPolicy olp = descriptor.getOptimisticLockingPolicy();
            if (olp != null && olp.getLockOnChangeMode() == null){
                olp.setLockOnChangeMode(LockOnChange.OWNING);
            }
        }

        // TODO: Look into setting a CMPPolicy on the MappedSuperclass descriptors.
        // Will require some tweaking however to ensure the primary key fields are
        // set/initialized correctly. Currently rely on the descriptor initialized
        // object builder which is not available to mapped superclass descriptors.
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
     * Checks for partitioning properties.
     */  
    protected void updatePartitioning(Map m, ClassLoader loader) {
        // Partitioning
        String partitioning = getConfigPropertyAsStringLogDebug(PersistenceUnitProperties.PARTITIONING, m, this.session);
        if (partitioning != null) {
            PartitioningPolicy partitioningPolicy = this.session.getProject().getPartitioningPolicy(partitioning);
            if (partitioningPolicy == null) {
                throw DescriptorException.missingPartitioningPolicy(partitioning, null, null);
            }
            this.session.setPartitioningPolicy(partitioningPolicy);
        }
        
        String callbackClassName = getConfigPropertyAsStringLogDebug(PersistenceUnitProperties.PARTITIONING_CALLBACK, m, this.session);
        if (callbackClassName != null) {
            Class cls = findClassForProperty(callbackClassName, PersistenceUnitProperties.PARTITIONING_CALLBACK, loader);
            DataPartitioningCallback callback = null;
            try {
                Constructor constructor = cls.getConstructor();
                callback = (DataPartitioningCallback)constructor.newInstance();
            } catch (Exception exception) {
                throw EntityManagerSetupException.failedToInstantiateServerPlatform(callbackClassName, PersistenceUnitProperties.PARTITIONING_CALLBACK, exception);
            }
            this.session.getLogin().setPartitioningCallback(callback);
        }
    }
    
    /**
     * Update loggers and settings for the singleton logger and the session logger. 
     * @param persistenceProperties the properties map
     * @param serverPlatformChanged the boolean that denotes a serverPlatform change in the session.
     */
    protected void updateLoggers(Map persistenceProperties, boolean serverPlatformChanged, ClassLoader loader) {
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
    
    /**
     * Check for the PROFILER persistence or system property and set the Session's profiler.
     * This can also set the QueryMonitor.
     */
    protected void updateProfiler(Map persistenceProperties,ClassLoader loader) {
        // This must use config property as the profiler is not in the PropertiesHandler and requires
        // supporting generic profiler classes.
        String newProfilerClassName = getConfigPropertyAsStringLogDebug(PersistenceUnitProperties.PROFILER, persistenceProperties, session);

        if (newProfilerClassName == null) {
               ((ServerPlatformBase)session.getServerPlatform()).configureProfiler(session);
        } else {

            if (newProfilerClassName.equals(ProfilerType.NoProfiler)) {
                session.setProfiler(null);
                return;
            }
            if (newProfilerClassName.equals(ProfilerType.QueryMonitor)) {
                session.setProfiler(null);
                QueryMonitor.shouldMonitor=true;
                return;
            }
            if (newProfilerClassName.equals(ProfilerType.PerformanceProfiler)) {
                session.setProfiler(new PerformanceProfiler());
                return;
            }
            if (newProfilerClassName.equals(ProfilerType.PerformanceMonitor)) {
                session.setProfiler(new PerformanceMonitor());
                return;
            }
            
            String originalProfilerClassNamer = null;
            if (session.getProfiler() != null) {
                originalProfilerClassNamer = session.getProfiler().getClass().getName();
                if (originalProfilerClassNamer.equals(newProfilerClassName)) {
                    return;
                }
            }
            
            // New profiler - create the new instance and set it.
            try {
                Class newProfilerClass = findClassForProperty(newProfilerClassName, PersistenceUnitProperties.PROFILER, loader);
                SessionProfiler sessionProfiler = (SessionProfiler)buildObjectForClass(newProfilerClass, SessionProfiler.class);
                if (sessionProfiler != null) {
                    session.setProfiler(sessionProfiler);
                } else {
                    session.handleException(ValidationException.invalidProfilerClass(newProfilerClassName));
                }
            } catch (IllegalAccessException e) {
                session.handleException(ValidationException.cannotInstantiateProfilerClass(newProfilerClassName,e));
            } catch (PrivilegedActionException e) {
                session.handleException(ValidationException.cannotInstantiateProfilerClass(newProfilerClassName,e));
            } catch (InstantiationException e) {
                session.handleException(ValidationException.cannotInstantiateProfilerClass(newProfilerClassName,e));
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
    
    public DatabaseSessionImpl getSession(){
        return session;
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

        String defaultTypeName = (String)typeMap.remove(PersistenceUnitProperties.DEFAULT);
        if (defaultTypeName != null) {
            // Always use the EclipseLink class loader, otherwise can have loader/redeployment issues.
            Class defaultType = findClassForProperty(defaultTypeName, PersistenceUnitProperties.CACHE_TYPE_DEFAULT, getClass().getClassLoader());
            session.getProject().setDefaultIdentityMapClass(defaultType);
        }
        
        String value = null;
        try {
            String defaultSizeString = (String)sizeMap.remove(PersistenceUnitProperties.DEFAULT);
            if (defaultSizeString != null) {
                value = defaultSizeString;
                int defaultSize = Integer.parseInt(defaultSizeString);
                session.getProject().setDefaultIdentityMapSize(defaultSize);
            }
            
            String defaultSharedString = (String)sharedMap.remove(PersistenceUnitProperties.DEFAULT);
            if (defaultSharedString != null) {
                boolean defaultShared = Boolean.parseBoolean(defaultSharedString);
                session.getProject().setDefaultIsIsolated(!defaultShared);
            }
            
            Iterator it = session.getDescriptors().values().iterator();
            while (it.hasNext() && (!typeMap.isEmpty() || !sizeMap.isEmpty() || !sharedMap.isEmpty())) {
                ClassDescriptor descriptor = (ClassDescriptor)it.next();
                
                if (descriptor.isDescriptorTypeAggregate()) {
                    continue;
                }
                
                String entityName = descriptor.getAlias();
                String className = descriptor.getJavaClass().getName();
                String name;
                
                name = entityName;
                String typeName = (String)typeMap.remove(name);
                if( typeName == null) {
                    name = className;
                    typeName = (String)typeMap.remove(name);
                }
                if (typeName != null) {
                    Class type = findClassForProperty(typeName, PersistenceUnitProperties.CACHE_TYPE_ + name, getClass().getClassLoader());
                    descriptor.setIdentityMapClass(type);
                }
    
                name = entityName;
                String sizeString = (String)sizeMap.remove(name);
                if (sizeString == null) {
                    name = className;
                    sizeString = (String)sizeMap.remove(name);
                }
                if (sizeString != null) {
                    value = sizeString;
                    int size = Integer.parseInt(sizeString);
                    descriptor.setIdentityMapSize(size);
                }
    
                name = entityName;
                String sharedString = (String)sharedMap.remove(name);
                if (sharedString == null) {
                    name = className;
                    sharedString = (String)sharedMap.remove(name);
                }
                if (sharedString != null) {
                    boolean shared = Boolean.parseBoolean(sharedString);
                    descriptor.setIsIsolated(!shared);
                }
            }
        } catch (NumberFormatException exception) {
            this.session.handleException(ValidationException.invalidValueForProperty(value, PersistenceUnitProperties.CACHE_SIZE_, exception));
        }
    }
    
    /**
     * Process all properties under "eclipselink.connection-pool.".
     * This allows for named connection pools.
     * It also processes "read", "write", "default"  and "sequence" connection pools.
     */
    protected void updateConnectionSettings(ServerSession serverSession, Map properties) {
        Map<String, Object> connectionsMap = PropertiesHandler.getPrefixValuesLogDebug(PersistenceUnitProperties.CONNECTION_POOL, properties, serverSession);
        if (connectionsMap.isEmpty()) {
            return;
        }
        for (Map.Entry<String, Object> entry : connectionsMap.entrySet()) {
            String poolName = "default";
            String attribute = null;
            try {
                if (entry.getKey().indexOf(".") == -1) {
                    attribute = entry.getKey();
                } else {
                    poolName = entry.getKey().substring(0, entry.getKey().indexOf("."));
                    attribute = entry.getKey().substring(entry.getKey().indexOf(".") + 1, entry.getKey().length());
                }
                ConnectionPool pool = null;
                if (poolName.equals("write")) {
                    poolName = "default";
                }
                if (poolName.equals("read")) {
                    pool = serverSession.getReadConnectionPool();
                    // By default there is no connection pool, so if the default, create a new one.
                    if ((pool == null) || (pool == serverSession.getDefaultConnectionPool())) {
                        if (this.session.getLogin().shouldUseExternalConnectionPooling()) {
                            pool = new ExternalConnectionPool(poolName, serverSession.getLogin(), serverSession);                            
                        } else {
                            pool = new ConnectionPool(poolName, serverSession.getLogin(), serverSession);
                        }
                        serverSession.setReadConnectionPool(pool);
                    }
                } else if (poolName.equals("sequence")) {
                    pool = this.session.getSequencingControl().getConnectionPool();
                    if (pool == null) {
                        if (this.session.getLogin().shouldUseExternalConnectionPooling()) {
                            pool = new ExternalConnectionPool(poolName, serverSession.getLogin(), serverSession);                            
                        } else {
                            pool = new ConnectionPool(poolName, serverSession.getLogin(), serverSession);
                        }
                        this.session.getSequencingControl().setConnectionPool(pool);
                    }
                } else {
                    pool = serverSession.getConnectionPool(poolName);
                    if (pool == null) {
                        if (this.session.getLogin().shouldUseExternalConnectionPooling()) {
                            pool = new ExternalConnectionPool(poolName, serverSession.getLogin(), serverSession);                            
                        } else {
                            pool = new ConnectionPool(poolName, serverSession.getLogin(), serverSession);
                        }
                        serverSession.addConnectionPool(pool);
                    }
                }
                if (attribute.equals(PersistenceUnitProperties.CONNECTION_POOL_INITIAL)) {
                    pool.setInitialNumberOfConnections(Integer.parseInt((String)entry.getValue()));
                } else if (attribute.equals(PersistenceUnitProperties.CONNECTION_POOL_MIN)) {
                    pool.setMinNumberOfConnections(Integer.parseInt((String)entry.getValue()));
                } else if (attribute.equals(PersistenceUnitProperties.CONNECTION_POOL_MAX)) {
                    pool.setMaxNumberOfConnections(Integer.parseInt((String)entry.getValue()));
                } else if (attribute.equals(PersistenceUnitProperties.CONNECTION_POOL_URL)) {
                    pool.setLogin(pool.getLogin().clone());
                    ((DatabaseLogin)pool.getLogin()).setURL((String)entry.getValue());
                } else if (attribute.equals(PersistenceUnitProperties.CONNECTION_POOL_NON_JTA_DATA_SOURCE)) {
                    pool.setLogin(pool.getLogin().clone());
                    ((DatabaseLogin)pool.getLogin()).useDataSource((String)entry.getValue());
                } else if (attribute.equals(PersistenceUnitProperties.CONNECTION_POOL_JTA_DATA_SOURCE)) {
                    pool.setLogin(pool.getLogin().clone());
                    ((DatabaseLogin)pool.getLogin()).useDataSource((String)entry.getValue());
                } else if (attribute.equals(PersistenceUnitProperties.CONNECTION_POOL_USER)) {
                    pool.setLogin(pool.getLogin().clone());
                    ((DatabaseLogin)pool.getLogin()).setUserName((String)entry.getValue());
                } else if (attribute.equals(PersistenceUnitProperties.CONNECTION_POOL_PASSWORD)) {
                    pool.setLogin(pool.getLogin().clone());
                    ((DatabaseLogin)pool.getLogin()).setPassword((String)entry.getValue());
                } else if (attribute.equals(PersistenceUnitProperties.CONNECTION_POOL_WAIT)) {
                    pool.setWaitTimeout(Integer.parseInt((String)entry.getValue()));
                } else if (attribute.equals(PersistenceUnitProperties.CONNECTION_POOL_FAILOVER)) {
                    String failoverPools = (String)entry.getValue();
                    if ((failoverPools.indexOf(',') != -1) || (failoverPools.indexOf(' ') != -1)) {
                        StringTokenizer tokenizer = new StringTokenizer(failoverPools, " ,");
                        while (tokenizer.hasMoreTokens()) {
                            pool.addFailoverConnectionPool(tokenizer.nextToken());
                        }
                    } else {
                        pool.addFailoverConnectionPool((String)entry.getValue());
                    }
                } else if (poolName.equals("read") && attribute.equals(PersistenceUnitProperties.CONNECTION_POOL_SHARED)) {
                    boolean shared = Boolean.parseBoolean((String)entry.getValue());
                    if (shared) {
                        ReadConnectionPool readPool = new ReadConnectionPool(poolName, serverSession.getLogin(), serverSession);
                        readPool.setInitialNumberOfConnections(pool.getInitialNumberOfConnections());
                        readPool.setMinNumberOfConnections(pool.getMinNumberOfConnections());
                        readPool.setMaxNumberOfConnections(pool.getMaxNumberOfConnections());
                        readPool.setWaitTimeout(pool.getWaitTimeout());
                        readPool.setLogin(pool.getLogin());
                        serverSession.setReadConnectionPool(readPool);
                    }
                }
            } catch (RuntimeException exception) {
                this.session.handleException(ValidationException.invalidValueForProperty(entry.getValue(), entry.getKey(), exception));
            }
        }
    }
    
    protected void updateConnectionPolicy(ServerSession serverSession, Map m) {
        String isLazyString = getConfigPropertyAsStringLogDebug(PersistenceUnitProperties.EXCLUSIVE_CONNECTION_IS_LAZY, m, session);
        if(isLazyString != null) {
            serverSession.getDefaultConnectionPolicy().setIsLazy(Boolean.parseBoolean(isLazyString));
        }
        ConnectionPolicy.ExclusiveMode exclusiveMode = getConnectionPolicyExclusiveModeFromProperties(m, session, true);
        if(exclusiveMode != null) {
            serverSession.getDefaultConnectionPolicy().setExclusiveMode(exclusiveMode);
        }
    }

    public static ConnectionPolicy.ExclusiveMode getConnectionPolicyExclusiveModeFromProperties(Map m, AbstractSession abstractSession, boolean useSystemAsDefault) {
        String exclusiveConnectionModeString = PropertiesHandler.getPropertyValueLogDebug(PersistenceUnitProperties.EXCLUSIVE_CONNECTION_MODE, m, abstractSession, useSystemAsDefault);
        if(exclusiveConnectionModeString != null) {
            if(exclusiveConnectionModeString == ExclusiveConnectionMode.Isolated) {
                return ConnectionPolicy.ExclusiveMode.Isolated;
            } else if(exclusiveConnectionModeString == ExclusiveConnectionMode.Always) {
                return ConnectionPolicy.ExclusiveMode.Always;
            } else {
                return ConnectionPolicy.ExclusiveMode.Transactional;
            }
        } else {
            return null;
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
        ClassLoader privateClassLoader = null;
        if (state == STATE_DEPLOY_FAILED || state == STATE_UNDEPLOYED) {
            throw new PersistenceException(EntityManagerSetupException.cannotPredeploy(persistenceUnitInfo.getPersistenceUnitName(), state));
        }
        if (state == STATE_PREDEPLOYED || state == STATE_DEPLOYED) {
            session.log(SessionLog.FINEST, SessionLog.PROPERTIES, "predeploy_begin", new Object[]{getPersistenceUnitInfo().getPersistenceUnitName(), session.getName(), state, factoryCount});
            factoryCount++;
            session.log(SessionLog.FINEST, SessionLog.PROPERTIES, "predeploy_end", new Object[]{getPersistenceUnitInfo().getPersistenceUnitName(), session.getName(), state, factoryCount});
            return null;
        } else if (state == STATE_INITIAL) {
            persistenceUnitInfo = info;
            if (!isCompositeMember()) {
                if (mustBeCompositeMember(persistenceUnitInfo)) {
                    if (this.staticWeaveInfo == null) {
                        return null;
                    } else {
                        // predeploy is used for static weaving
                        throw new PersistenceException(EntityManagerSetupException.compositeMemberCannotBeUsedStandalone(persistenceUnitInfo.getPersistenceUnitName()));
                    }
                }
            }
        } else if (state == STATE_HALF_PREDEPLOYED_COMPOSITE_MEMBER) {
            session.log(SessionLog.FINEST, SessionLog.PROPERTIES, "predeploy_begin", new Object[]{getPersistenceUnitInfo().getPersistenceUnitName(), session.getName(), state + " " + mode, factoryCount});
        }
        
        // state is INITIAL or PREDEPLOY_FAILED or STATE_HALF_PREDEPLOYED_COMPOSITE_MEMBER
        try {
            // properties not used in STATE_HALF_PREDEPLOYED_COMPOSITE_MEMBER
            Map predeployProperties = null;
            // composite can't be in STATE_HALF_PREDEPLOYED_COMPOSITE_MEMBER
            boolean isComposite = false;
            if(state != STATE_HALF_PREDEPLOYED_COMPOSITE_MEMBER) {
                predeployProperties = mergeMaps(extendedProperties, persistenceUnitInfo.getProperties());
                // Translate old properties.
                // This should be done before using properties (i.e. ServerPlatform).
                translateOldProperties(predeployProperties, null);
                
                String sessionsXMLStr = (String)predeployProperties.get(PersistenceUnitProperties.SESSIONS_XML);
                if (sessionsXMLStr != null) {
                    isSessionLoadedFromSessionsXML = true;
                }
                
                // Create session (it needs to be done before initializing ServerPlatform and logging).
                // If a sessions-xml is used this will get replaced later, but is required for logging.
                isComposite = isComposite(persistenceUnitInfo);
                if (isComposite) {
                    if (isSessionLoadedFromSessionsXML) {
                        throw new PersistenceException(EntityManagerSetupException.compositeIncompatibleWithSessionsXml(persistenceUnitInfo.getPersistenceUnitName()));
                    }
                    session = new SessionBroker();
                    ((SessionBroker)session).setShouldUseDescriptorAliases(true);
                } else {
                    session = new ServerSession(new Project(new DatabaseLogin()));
                }
                session.setName(this.sessionName);

                if (this.compositeEmSetupImpl == null) {
                    // session name and ServerPlatform must be set prior to setting the loggers.
                    ClassLoader realClassLoader = persistenceUnitInfo.getClassLoader();
                    if (this.staticWeaveInfo == null) {
                        updateServerPlatform(predeployProperties, realClassLoader);
                        // Update loggers and settings for the singleton logger and the session logger.
                        updateLoggers(predeployProperties, true, realClassLoader);
                        // Get the temporary classLoader based on the platform
                        JPAClassLoaderHolder privateClassLoaderHolder = session.getServerPlatform().getNewTempClassLoader(persistenceUnitInfo);
                        privateClassLoader = privateClassLoaderHolder.getClassLoader();
                        
                        //Update performance profiler
                        updateProfiler(predeployProperties,realClassLoader);
                    } else {
                        // predeploy is used for static weaving
                        Writer writer = this.staticWeaveInfo.getLogWriter(); 
                        if (writer != null) {
                            ((DefaultSessionLog)session.getSessionLog()).setWriter(writer);
                        }
                        session.setLogLevel(this.staticWeaveInfo.getLogLevel());
                        privateClassLoader = persistenceUnitInfo.getNewTempClassLoader();
                    }
                } else {
                    // composite member
                    session.setSessionLog(this.compositeEmSetupImpl.session.getSessionLog());
                    session.setProfiler(this.compositeEmSetupImpl.session.getProfiler());
                    privateClassLoader = persistenceUnitInfo.getNewTempClassLoader();
                }
                
                // Cannot start logging until session and log and initialized, so log start of predeploy here.
                session.log(SessionLog.FINEST, SessionLog.PROPERTIES, "predeploy_begin", new Object[]{getPersistenceUnitInfo().getPersistenceUnitName(), session.getName(), state, factoryCount});
    
                if (isSessionLoadedFromSessionsXML) {
                    // Loading session from sessions-xml.
                    String tempSessionName = sessionName;
                    if (isCompositeMember()) {
                        // composite member session name is always the same as puName
                        // need the session name specified in properties to read correct session from sessions.xml
                        tempSessionName = (String)predeployProperties.get(PersistenceUnitProperties.SESSION_NAME);
                    }
                    session.log(SessionLog.FINEST, SessionLog.PROPERTIES, "loading_session_xml", sessionsXMLStr, tempSessionName);
                    if (tempSessionName == null) {
                        throw new PersistenceException(EntityManagerSetupException.sessionNameNeedBeSpecified(persistenceUnitInfo.getPersistenceUnitName(), sessionsXMLStr));
                    }                
                    XMLSessionConfigLoader xmlLoader = new XMLSessionConfigLoader(sessionsXMLStr);
                    // Do not register the session with the SessionManager at this point, create temporary session using a local SessionManager and private class loader.
                    // This allows for the project to be accessed without loading any of the classes to allow weaving.
                    // Note that this method assigns sessionName to session.
                    Session tempSession = new SessionManager().getSession(xmlLoader, tempSessionName, privateClassLoader, false, false);
                    // Load path of sessions-xml resource before throwing error so user knows which sessions-xml file was found (may be multiple).
                    session.log(SessionLog.FINEST, SessionLog.PROPERTIES, "sessions_xml_path_where_session_load_from", xmlLoader.getSessionName(), xmlLoader.getResourcePath());
                    if (tempSession == null) {
                        throw new PersistenceException(ValidationException.noSessionFound(sessionName, sessionsXMLStr));
                    }
                    // Currently the session must be either a ServerSession or a SessionBroker, cannot be just a DatabaseSessionImpl. 
                    if (tempSession.isServerSession() || tempSession.isSessionBroker()) {
                       session = (DatabaseSessionImpl) tempSession;
                       if (tempSessionName != sessionName) {
                           // set back the original session name
                           session.setName(sessionName);
                       }
                    } else {
                        throw new PersistenceException(EntityManagerSetupException.sessionLoadedFromSessionsXMLMustBeServerSession(persistenceUnitInfo.getPersistenceUnitName(), (String)predeployProperties.get(PersistenceUnitProperties.SESSIONS_XML), tempSession));
                    }
                    // Must now reset logging and server-platform on the loaded session.
                    // ServerPlatform must be set prior to setting the loggers.
                    updateServerPlatform(predeployProperties, privateClassLoader);
                    // Update loggers and settings for the singleton logger and the session logger.
                    updateLoggers(predeployProperties, true, privateClassLoader);
                }
                
                warnOldProperties(predeployProperties, session);
                session.getPlatform().setConversionManager(new JPAConversionManager());
                
                if (this.staticWeaveInfo == null) {
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
                    
                    // this flag is used to disable work done as a result of the LAZY hint on OneToOne and ManyToOne mappings
                    if(state == STATE_INITIAL) {
                        if (compositeEmSetupImpl == null) {
                            if(null == enableWeaving) {                    
                                enableWeaving = Boolean.TRUE;
                            }
                            isWeavingStatic = false;
                            String weaving = getConfigPropertyAsString(PersistenceUnitProperties.WEAVING, predeployProperties);
                            if (weaving != null && weaving.equalsIgnoreCase("false")) {
                                enableWeaving = Boolean.FALSE;
                            }else if (weaving != null && weaving.equalsIgnoreCase("static")) {
                                isWeavingStatic = true;
                            }
                        } else {
                            // composite member
                            // no weaving for composite forces no weaving for members
                            if (!compositeEmSetupImpl.enableWeaving) {
                                enableWeaving = Boolean.FALSE;
                            } else {
                                if(null == enableWeaving) {                    
                                    enableWeaving = Boolean.TRUE;
                                }
                                String weaving = getConfigPropertyAsString(PersistenceUnitProperties.WEAVING, predeployProperties);
                                if (weaving != null && weaving.equalsIgnoreCase("false")) {
                                    enableWeaving = Boolean.FALSE;
                                }
                            }
                            // static weaving is dictated by composite
                            isWeavingStatic = compositeEmSetupImpl.isWeavingStatic;
                        }
                    }
                
                    if (compositeEmSetupImpl == null) {
                        throwExceptionOnFail = "true".equalsIgnoreCase(
                                EntityManagerFactoryProvider.getConfigPropertyAsStringLogDebug(PersistenceUnitProperties.THROW_EXCEPTIONS, predeployProperties, "true", session));
                    } else {
                        // composite member
                        throwExceptionOnFail = compositeEmSetupImpl.throwExceptionOnFail; 
                    }
                } else {
                    // predeploy is used for static weaving
                    enableWeaving = Boolean.TRUE;
                }
                
                weaveChangeTracking = false;
                weaveLazy = false;
                weaveEager = false;
                weaveFetchGroups = false;
                weaveInternal = false;
                if (enableWeaving) {
                    weaveChangeTracking = "true".equalsIgnoreCase(EntityManagerFactoryProvider.getConfigPropertyAsStringLogDebug(PersistenceUnitProperties.WEAVING_CHANGE_TRACKING, predeployProperties, "true", session));
                    weaveLazy = "true".equalsIgnoreCase(EntityManagerFactoryProvider.getConfigPropertyAsStringLogDebug(PersistenceUnitProperties.WEAVING_LAZY, predeployProperties, "true", session));
                    weaveEager = "true".equalsIgnoreCase(EntityManagerFactoryProvider.getConfigPropertyAsStringLogDebug(PersistenceUnitProperties.WEAVING_EAGER, predeployProperties, "false", session));
                    weaveFetchGroups = "true".equalsIgnoreCase(EntityManagerFactoryProvider.getConfigPropertyAsStringLogDebug(PersistenceUnitProperties.WEAVING_FETCHGROUPS, predeployProperties, "true", session));
                    weaveInternal = "true".equalsIgnoreCase(EntityManagerFactoryProvider.getConfigPropertyAsStringLogDebug(PersistenceUnitProperties.WEAVING_INTERNAL, predeployProperties, "true", session));
                }
            }
            if (!isSessionLoadedFromSessionsXML ) {
                if (isComposite) {
                    predeployCompositeMembers(predeployProperties, privateClassLoader);
                } else {
                    MetadataProcessor compositeProcessor = null;
                    if (compositeEmSetupImpl == null) {
                        mode = PersistenceUnitProcessor.Mode.ALL;
                    } else {
                        // composite member
                        if (state != STATE_HALF_PREDEPLOYED_COMPOSITE_MEMBER) {
                            state = STATE_HALF_PREDEPLOYED_COMPOSITE_MEMBER;
                            mode = PersistenceUnitProcessor.Mode.COMPOSITE_MEMBER_INITIAL;
                        }
                        compositeProcessor = compositeEmSetupImpl.processor;
                    }
                    
                    if (mode == PersistenceUnitProcessor.Mode.ALL || mode == PersistenceUnitProcessor.Mode.COMPOSITE_MEMBER_INITIAL) {
                        boolean usesMultitenantSharedEmf = "true".equalsIgnoreCase(EntityManagerFactoryProvider.getConfigPropertyAsStringLogDebug(PersistenceUnitProperties.MULTITENANT_SHARED_EMF, predeployProperties, "false", session));
    
                        // Create an instance of MetadataProcessor for specified persistence unit info
                        processor = new MetadataProcessor(persistenceUnitInfo, session, privateClassLoader, weaveLazy, weaveEager, weaveFetchGroups, usesMultitenantSharedEmf, predeployProperties, compositeProcessor);
                        
                        //need to use the real classloader to create the repository class
                        updateMetadataRepository(predeployProperties, persistenceUnitInfo.getClassLoader()); 
        
                        //bug:299926 - Case insensitive table / column matching with native SQL queries
                        EntityManagerSetupImpl.updateCaseSensitivitySettings(predeployProperties, processor.getProject(), session);
                    }
                    
                    // Process the Object/relational metadata from XML and annotations.
                    PersistenceUnitProcessor.processORMetadata(processor, throwExceptionOnFail, mode);
                    
                    if (mode == PersistenceUnitProcessor.Mode.COMPOSITE_MEMBER_INITIAL) {
                        mode = PersistenceUnitProcessor.Mode.COMPOSITE_MEMBER_MIDDLE;
                        session.log(SessionLog.FINEST, SessionLog.PROPERTIES, "predeploy_end", new Object[]{getPersistenceUnitInfo().getPersistenceUnitName(), session.getName(), state + " " + mode , factoryCount});
                        return null;
                    } else if (mode == PersistenceUnitProcessor.Mode.COMPOSITE_MEMBER_MIDDLE) {
                        mode = PersistenceUnitProcessor.Mode.COMPOSITE_MEMBER_FINAL;
                        session.log(SessionLog.FINEST, SessionLog.PROPERTIES, "predeploy_end", new Object[]{getPersistenceUnitInfo().getPersistenceUnitName(), session.getName(), state + " " + mode , factoryCount});
                        return null;
                    }
                    // mode == PersistenceUnitProcessor.Mode.ALL || mode == PersistenceUnitProcessor.Mode.COMPOSITE_MEMBER_FINAL
                    // clear mode and proceed
                    mode = null;
    
                    if (session.getIntegrityChecker().hasErrors()){
                        session.handleException(new IntegrityException(session.getIntegrityChecker()));
                    }
    
                    // The transformer is capable of altering domain classes to handle a LAZY hint for OneToOne mappings.  It will only
                    // be returned if we we are mean to process these mappings
                    if (enableWeaving) {                
                        // build a list of entities the persistence unit represented by this EntityManagerSetupImpl will use
                        Collection entities = PersistenceUnitProcessor.buildEntityList(processor, privateClassLoader);
                        this.weaver = TransformerFactory.createTransformerAndModifyProject(session, entities, privateClassLoader, weaveLazy, weaveChangeTracking, weaveFetchGroups, weaveInternal);
                    }
                }
            } else {
                // The transformer is capable of altering domain classes to handle a LAZY hint for OneToOne mappings.  It will only
                // be returned if we we are meant to process these mappings.
                if (enableWeaving) {
                    // If deploying from a sessions-xml it is still desirable to allow the classes to be weaved.                
                    // build a list of entities the persistence unit represented by this EntityManagerSetupImpl will use
                    Collection persistenceClasses = new ArrayList();

                    MetadataAsmFactory factory = new MetadataAsmFactory(new MetadataLogger(session), privateClassLoader);
                    for (Iterator iterator = session.getProject().getDescriptors().keySet().iterator(); iterator.hasNext(); ) {
                        persistenceClasses.add(factory.getMetadataClass(((Class)iterator.next()).getName()));
                    }
                    this.weaver = TransformerFactory.createTransformerAndModifyProject(session, persistenceClasses, privateClassLoader, weaveLazy, weaveChangeTracking, weaveFetchGroups, weaveInternal);
                }
            }
            
            // composite member never has a factory - it is predeployed by the composite.
            if (!isCompositeMember()) {
                // factoryCount is not incremented only in case of a first call to preDeploy
                // in non-container mode: this call is not associated with a factory
                // but rather done by JavaSECMPInitializer.callPredeploy (typically in preMain).
                if(state != STATE_INITIAL || this.isInContainerMode()) {
                    factoryCount++;
                }
            }
            state = STATE_PREDEPLOYED;
            session.log(SessionLog.FINEST, SessionLog.PROPERTIES, "predeploy_end", new Object[]{getPersistenceUnitInfo().getPersistenceUnitName(), session.getName(), state, factoryCount});
            //gf3146: if static weaving is used, we should not return a transformer.  Transformer should still be created though as it modifies descriptors 
            if (isWeavingStatic) {
                return null;
            } else {
                return this.weaver;
            }
        } catch (RuntimeException ex) {
            state = STATE_PREDEPLOY_FAILED;
            session.log(SessionLog.FINEST, SessionLog.PROPERTIES, "predeploy_end", new Object[]{getPersistenceUnitInfo().getPersistenceUnitName(), session.getName(), state, factoryCount});
            session = null;
            mode = null;
            throw new PersistenceException(EntityManagerSetupException.predeployFailed(persistenceUnitInfo.getPersistenceUnitName(), ex));
        }
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
        String validationOnlyString = getConfigPropertyAsStringLogDebug(PersistenceUnitProperties.VALIDATION_ONLY_PROPERTY, m, session);
        if (validationOnlyString != null) {
            return Boolean.parseBoolean(validationOnlyString);
        } else {
            return false;
        }
    }
    
    /**
     * Return if the session should be deployed and connected during the creation of the EtntiyManagerFactory,
     * or if it should be deferred until createEntityManager().
     * The default is to defer, but is only validating, or can be configured to deploy upfront to avoid hanging the
     * application at runtime.
     */
    public boolean shouldGetSessionOnCreateFactory(Map m) {
        m = mergeWithExistingMap(m);
        if (isValidationOnly(m, false)) {
            return true;
        }

        String deployString = getConfigPropertyAsStringLogDebug(PersistenceUnitProperties.DEPLOY_ON_STARTUP, m, this.session);
        if (deployString != null) {
            return Boolean.parseBoolean(deployString);
        } else {
            return false;
        }
    }
    
    protected Map mergeWithExistingMap(Map m) {
        if(persistenceUnitInfo != null) {
            return mergeMaps(m, persistenceUnitInfo.getProperties());
        } else {
            return m;
        }
    }

    public boolean isInContainerMode(){
        return isInContainerMode;
    }

    /**
     * Configure cache coordination using properties.
     */
    protected void updateCacheCoordination(Map m, ClassLoader loader) {
        String protocol = getConfigPropertyAsStringLogDebug(PersistenceUnitProperties.COORDINATION_PROTOCOL, m, this.session);
        
        if (protocol != null) {
            RemoteCommandManager rcm = new RemoteCommandManager(this.session);        
            if (protocol.equalsIgnoreCase(CacheCoordinationProtocol.JMS) || protocol.equalsIgnoreCase(CacheCoordinationProtocol.JMSPublishing)) {
                JMSPublishingTransportManager transport = null;
                if (protocol.equalsIgnoreCase(CacheCoordinationProtocol.JMS)){
                     transport = new JMSTopicTransportManager(rcm);
                } else {
                    transport = new JMSPublishingTransportManager(rcm);
                }
                rcm.setTransportManager(transport);
                
                String host = getConfigPropertyAsStringLogDebug(PersistenceUnitProperties.COORDINATION_JMS_HOST, m, this.session);
                if (host != null) {
                    transport.setTopicHostUrl(host);
                }
                String topic = getConfigPropertyAsStringLogDebug(PersistenceUnitProperties.COORDINATION_JMS_TOPIC, m, this.session);
                if (topic != null) {
                    transport.setTopicName(topic);
                }
                String factory = getConfigPropertyAsStringLogDebug(PersistenceUnitProperties.COORDINATION_JMS_FACTORY, m, this.session);
                if (factory != null) {
                    transport.setTopicConnectionFactoryName(factory);
                }
                
                String reuse_publisher = getConfigPropertyAsStringLogDebug(PersistenceUnitProperties.COORDINATION_JMS_REUSE_PUBLISHER, m, this.session);
                if (reuse_publisher != null) {
                    transport.setShouldReuseJMSTopicPublisher(reuse_publisher.equalsIgnoreCase("true"));
                }
                
            } else if (protocol.equalsIgnoreCase(CacheCoordinationProtocol.RMI) || protocol.equalsIgnoreCase(CacheCoordinationProtocol.RMIIIOP)) {
                if (protocol.equalsIgnoreCase(CacheCoordinationProtocol.RMIIIOP)){
                    ((RMITransportManager)rcm.getTransportManager()).setIsRMIOverIIOP(true);
               }
                // Default protocol.
                String delay = getConfigPropertyAsStringLogDebug(PersistenceUnitProperties.COORDINATION_RMI_ANNOUNCEMENT_DELAY, m, this.session);
                if (delay != null) {
                    rcm.getDiscoveryManager().setAnnouncementDelay(Integer.parseInt(delay));
                }
                String multicast = getConfigPropertyAsStringLogDebug(PersistenceUnitProperties.COORDINATION_RMI_MULTICAST_GROUP, m, this.session);
                if (multicast != null) {
                    rcm.getDiscoveryManager().setMulticastGroupAddress(multicast);
                }
                String port = getConfigPropertyAsStringLogDebug(PersistenceUnitProperties.COORDINATION_RMI_MULTICAST_GROUP_PORT, m, this.session);
                if (port != null) {
                    rcm.getDiscoveryManager().setMulticastPort(Integer.parseInt(port));
                }
                String timeToLive = getConfigPropertyAsStringLogDebug(PersistenceUnitProperties.COORDINATION_RMI_PACKET_TIME_TO_LIVE, m, this.session);
                if (timeToLive != null) {
                    rcm.getDiscoveryManager().setPacketTimeToLive(Integer.parseInt(timeToLive));
                }
                String url = getConfigPropertyAsStringLogDebug(PersistenceUnitProperties.COORDINATION_RMI_URL, m, this.session);
                if (url != null) {
                    rcm.setUrl(url);
                }
            } else {
                Class transportClass = findClassForProperty(protocol, PersistenceUnitProperties.COORDINATION_PROTOCOL, loader);
                try {
                    rcm.setTransportManager((TransportManager)transportClass.newInstance());
                } catch (Exception invalid) {
                    // TODO: ex
                    throw new RuntimeException(invalid);
                }
            }
            String naming = getConfigPropertyAsStringLogDebug(PersistenceUnitProperties.COORDINATION_NAMING_SERVICE, m, this.session);
            if (naming != null) {
                if (naming.equalsIgnoreCase("jndi")) {
                    rcm.getTransportManager().setNamingServiceType(TransportManager.JNDI_NAMING_SERVICE);
                } else if (naming.equalsIgnoreCase("rmi")) {
                    rcm.getTransportManager().setNamingServiceType(TransportManager.REGISTRY_NAMING_SERVICE);
                }
            }
            String user = getConfigPropertyAsStringLogDebug(PersistenceUnitProperties.COORDINATION_JNDI_USER, m, this.session);
            if (user != null) {
                rcm.getTransportManager().setUserName(user);
            }
            String password = getConfigPropertyAsStringLogDebug(PersistenceUnitProperties.COORDINATION_JNDI_PASSWORD, m, this.session);
            if (password != null) {
                rcm.getTransportManager().setPassword(password);
            }
            String context = getConfigPropertyAsStringLogDebug(PersistenceUnitProperties.COORDINATION_JNDI_CONTEXT, m, this.session);
            if (context != null) {
                rcm.getTransportManager().setInitialContextFactoryName(context);
            }
            String removeOnError = getConfigPropertyAsStringLogDebug(PersistenceUnitProperties.COORDINATION_REMOVE_CONNECTION, m, this.session);
            if (removeOnError != null) {
                rcm.getTransportManager().setShouldRemoveConnectionOnError(removeOnError.equalsIgnoreCase("true"));
            }
            String asynch = getConfigPropertyAsStringLogDebug(PersistenceUnitProperties.COORDINATION_ASYNCH, m, this.session);
            if (asynch != null) {
                rcm.setShouldPropagateAsynchronously(asynch.equalsIgnoreCase("true"));
            }
            String channel = getConfigPropertyAsStringLogDebug(PersistenceUnitProperties.COORDINATION_CHANNEL, m, this.session);
            if (channel != null) {
                rcm.setChannel(channel);
            }
            this.session.setCommandManager(rcm);
            this.session.setShouldPropagateChanges(true);
        }
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

        String eclipselinkPlatform = PropertiesHandler.getPropertyValueLogDebug(PersistenceUnitProperties.TARGET_DATABASE, m, session);
        if (eclipselinkPlatform != null) {
            login.setPlatformClassName(eclipselinkPlatform, persistenceUnitInfo.getClassLoader());
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

        // mainDatasource is guaranteed to be non null - TODO: No it is not, if they did not set one it is null, should raise error, not null-pointer.
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

        if(this.session.isServerSession()) {
            // set readLogin
            if (readDatasource != null) {
                DatasourceLogin readLogin = login.clone();
                readLogin.dontUseExternalTransactionController();
                JNDIConnector jndiConnector;
                if (readDatasource instanceof DataSourceImpl) {
                    //Bug5209363  Pass in the datasource name instead of the dummy datasource
                    jndiConnector = new JNDIConnector(((DataSourceImpl)readDatasource).getName());
                } else {
                    jndiConnector = new JNDIConnector(readDatasource);                    
                }
                readLogin.setConnector(jndiConnector);
                ((ServerSession)this.session).setReadConnectionPool(readLogin);
            }
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
            if(((String)datasource).length() > 0) {
                // Create a dummy DataSource that will throw an exception on access
                return new DataSourceImpl((String)datasource, null, null, null);
            } else {
                // allow an empty string data source property passed to createEMF to cancel data source specified in persistence.xml
                return null;
            }
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
        //Login info might be already set with sessions.xml and could be overridden by session customizer after this
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

    /**
     * Configure the internal connection pooling parameters.
     * By default if nothing is configured a default shared (exclusive) read/write pool is used with 32 min/max connections and 1 initial.
     */
    @SuppressWarnings("deprecation")
    protected void updatePools(ServerSession serverSession, Map m) {
        String value = null;
        String property = null;
        try {
            // Configure default/write connection pool.
            // Sizes are irrelevant for external connection pool
            if (!serverSession.getDefaultConnectionPool().getLogin().shouldUseExternalConnectionPooling()) {
                // CONNECTION and WRITE_CONNECTION properties both configure the default pool (mean the same thing, but WRITE normally used with READ).
                property = PersistenceUnitProperties.JDBC_CONNECTIONS_MIN;
                value = getConfigPropertyAsStringLogDebug(property, m, serverSession);
                if (value != null) {
                    serverSession.getDefaultConnectionPool().setMinNumberOfConnections(Integer.parseInt(value));
                }
                property = PersistenceUnitProperties.JDBC_CONNECTIONS_MAX;
                value = getConfigPropertyAsStringLogDebug(property, m, serverSession);
                if (value != null) {
                    serverSession.getDefaultConnectionPool().setMaxNumberOfConnections(Integer.parseInt(value));
                }
                property = PersistenceUnitProperties.JDBC_CONNECTIONS_INITIAL;
                value = getConfigPropertyAsStringLogDebug(property, m, serverSession);
                if (value != null) {
                    serverSession.getDefaultConnectionPool().setInitialNumberOfConnections(Integer.parseInt(value));
                }
                property = PersistenceUnitProperties.JDBC_WRITE_CONNECTIONS_MIN;
                value = getConfigPropertyAsStringLogDebug(property, m, serverSession);
                if (value != null) {
                    serverSession.getDefaultConnectionPool().setMinNumberOfConnections(Integer.parseInt(value));
                }
                property = PersistenceUnitProperties.JDBC_WRITE_CONNECTIONS_MAX;
                value = getConfigPropertyAsStringLogDebug(property, m, serverSession);
                if (value != null) {
                    serverSession.getDefaultConnectionPool().setMaxNumberOfConnections(Integer.parseInt(value));
                }
                property = PersistenceUnitProperties.JDBC_WRITE_CONNECTIONS_INITIAL;
                value = getConfigPropertyAsStringLogDebug(property, m, serverSession);
                if (value != null) {
                    serverSession.getDefaultConnectionPool().setInitialNumberOfConnections(Integer.parseInt(value));
                }
            }
            
            // Configure read connection pool if set.
            // Sizes and shared option are irrelevant for external connection pool
            if (!serverSession.getReadConnectionPool().getLogin().shouldUseExternalConnectionPooling()) {
                String shared = getConfigPropertyAsStringLogDebug(PersistenceUnitProperties.JDBC_READ_CONNECTIONS_SHARED, m, serverSession);
                boolean isShared = false;
                if (shared != null) {
                    isShared = Boolean.parseBoolean(shared);
                }            
                ConnectionPool pool = null;
                if (isShared) {
                    pool = new ReadConnectionPool("read", serverSession.getReadConnectionPool().getLogin(), serverSession);
                } else {
                    pool = new ConnectionPool("read", serverSession.getReadConnectionPool().getLogin(), serverSession);
                }
                String min = getConfigPropertyAsStringLogDebug(PersistenceUnitProperties.JDBC_READ_CONNECTIONS_MIN, m, serverSession);
                if (min != null) {
                    value = min;
                    property = PersistenceUnitProperties.JDBC_READ_CONNECTIONS_MIN;
                    pool.setMinNumberOfConnections(Integer.parseInt(min));
                }
                String max = getConfigPropertyAsStringLogDebug(PersistenceUnitProperties.JDBC_READ_CONNECTIONS_MAX, m, serverSession);
                if (max != null) {
                    value = max;
                    property = PersistenceUnitProperties.JDBC_READ_CONNECTIONS_MAX;
                    pool.setMaxNumberOfConnections(Integer.parseInt(max));
                }
                String initial = getConfigPropertyAsStringLogDebug(PersistenceUnitProperties.JDBC_READ_CONNECTIONS_INITIAL, m, serverSession);
                if (initial != null) {
                    value = initial;
                    property = PersistenceUnitProperties.JDBC_READ_CONNECTIONS_INITIAL;
                    pool.setInitialNumberOfConnections(Integer.parseInt(initial));
                }
                // Only set the read pool if they configured it, otherwise use default shared read/write.
                if (isShared || (min != null) || (max != null) || (initial != null)) {
                    serverSession.setReadConnectionPool(pool);
                }
                String wait = getConfigPropertyAsStringLogDebug(PersistenceUnitProperties.JDBC_CONNECTIONS_WAIT, m, serverSession);
                if (wait != null) {
                    value = wait;
                    property = PersistenceUnitProperties.JDBC_CONNECTIONS_WAIT;
                    serverSession.getDefaultConnectionPool().setWaitTimeout(Integer.parseInt(wait));
                    pool.setWaitTimeout(Integer.parseInt(wait));
                }
            }
            
            // Configure sequence connection pool if set.
            String sequence = getConfigPropertyAsStringLogDebug(PersistenceUnitProperties.JDBC_SEQUENCE_CONNECTION_POOL, m, serverSession);
            if (sequence != null) {
                serverSession.getSequencingControl().setShouldUseSeparateConnection(Boolean.parseBoolean(sequence));
            }
            String sequenceDataSource = getConfigPropertyAsStringLogDebug(PersistenceUnitProperties.JDBC_SEQUENCE_CONNECTION_POOL_DATASOURCE, m, serverSession);
            if (sequenceDataSource != null) {
                DatasourceLogin login = this.session.getLogin().clone();
                login.dontUseExternalTransactionController();
                JNDIConnector jndiConnector = new JNDIConnector(sequenceDataSource);
                login.setConnector(jndiConnector);
                serverSession.getSequencingControl().setLogin(login);
            }        
            // Sizes and shared option are irrelevant for external connection pool
            if (!serverSession.getReadConnectionPool().getLogin().shouldUseExternalConnectionPooling()) {
                value = getConfigPropertyAsStringLogDebug(PersistenceUnitProperties.JDBC_SEQUENCE_CONNECTION_POOL_MIN, m, serverSession);
                if (value != null) {
                    property = PersistenceUnitProperties.JDBC_SEQUENCE_CONNECTION_POOL_MIN;
                    serverSession.getSequencingControl().setMinPoolSize(Integer.parseInt(value));
                }
                value = getConfigPropertyAsStringLogDebug(PersistenceUnitProperties.JDBC_SEQUENCE_CONNECTION_POOL_MAX, m, serverSession);
                if (value != null) {
                    property = PersistenceUnitProperties.JDBC_SEQUENCE_CONNECTION_POOL_MAX;
                    serverSession.getSequencingControl().setMaxPoolSize(Integer.parseInt(value));
                }
                value = getConfigPropertyAsStringLogDebug(PersistenceUnitProperties.JDBC_SEQUENCE_CONNECTION_POOL_INITIAL, m, serverSession);
                if (value != null) {
                    property = PersistenceUnitProperties.JDBC_SEQUENCE_CONNECTION_POOL_INITIAL;
                    serverSession.getSequencingControl().setInitialPoolSize(Integer.parseInt(value));
                }
            }
        } catch (NumberFormatException exception) {
            serverSession.handleException(ValidationException.invalidValueForProperty(value, property, exception));
        }
    }
    
    /**
     * Normally when a property is missing nothing should be applied to the session.
     * However there are several session attributes that defaulted in EJB3 to the values
     * different from EclipseLink defaults.
     * This function applies defaults for such properties and registers the session.
     * All other session-related properties are applied in updateServerSession.
     * Note that updateSession may be called several times on the same session
     * (before login), but initSession is called just once - before the first call
     * to updateSession.
     * @param properties the persistence unit properties.
     */
    protected void initSession() {
        assignCMP3Policy();

        if(!isCompositeMember()) {
            // Register session that has been created earlier.
            addSessionToGlobalSessionManager();
        }
    }

    /**
     * Make any changes to our ServerSession that can be made after it is created.
     */
    protected void updateSession(Map m, ClassLoader loader) {
        if (session == null || session.isConnected()) {
            return;
        }

        // In deploy ServerPlatform could've changed which will affect the loggers.
        boolean serverPlatformChanged = updateServerPlatform(m, loader);

        if (!session.hasBroker()) {
            updateLoggers(m, serverPlatformChanged, loader);        
            updateProfiler(m,loader);
        }

        if(session.isBroker()) {
            PersistenceUnitTransactionType transactionType = persistenceUnitInfo.getTransactionType();
            //bug 5867753: find and override the transaction type using properties
            String transTypeString = getConfigPropertyAsStringLogDebug(PersistenceUnitProperties.TRANSACTION_TYPE, m, session);
            if (transTypeString != null) {
                transactionType = PersistenceUnitTransactionType.valueOf(transTypeString);
            }
            session.getLogin().setUsesExternalTransactionController(transactionType == PersistenceUnitTransactionType.JTA);
        } else {
            String shouldBindString = getConfigPropertyAsStringLogDebug(PersistenceUnitProperties.JDBC_BIND_PARAMETERS, m, session);
            if (shouldBindString != null) {
                session.getPlatform().setShouldBindAllParameters(Boolean.parseBoolean(shouldBindString));
            }
            updateLogins(m);
        }
        if (!session.getLogin().shouldUseExternalTransactionController()) {
            session.getServerPlatform().disableJTA();
        }
        
        if(session.isServerSession()) {
            updatePools((ServerSession)session, m);
            updateConnectionSettings((ServerSession)session, m);
            if (!isSessionLoadedFromSessionsXML) {
                updateDescriptorCacheSettings(m, loader);
            }
            updateConnectionPolicy((ServerSession)session, m);
        }
        
        if(session.isBroker()) {
            if (this.compositeMemberEmSetupImpls != null) {
                // composite
                Map compositeMemberMapOfProperties = (Map)getConfigProperty(PersistenceUnitProperties.COMPOSITE_UNIT_PROPERTIES, m);
                for(EntityManagerSetupImpl compositeMemberEmSetupImpl : this.compositeMemberEmSetupImpls) {
                    // the properties guaranteed to be non-null after updateCompositeMemberProperties call
                    String compositeMemberPuName = compositeMemberEmSetupImpl.getPersistenceUnitInfo().getPersistenceUnitName();
                    Map compositeMemberProperties = (Map)compositeMemberMapOfProperties.get(compositeMemberPuName);
                    // debug output added to make it easier to navigate the log because the method is called outside of composite member deploy
                    compositeMemberEmSetupImpl.session.log(SessionLog.FINEST, SessionLog.PROPERTIES, "composite_member_begin_call", new Object[]{"updateSession", compositeMemberPuName, state});
                    compositeMemberEmSetupImpl.updateSession(compositeMemberProperties, loader);
                    compositeMemberEmSetupImpl.session.log(SessionLog.FINEST, SessionLog.PROPERTIES, "composite_member_end_call", new Object[]{"updateSession", compositeMemberPuName, state});
                }
            }
            setSessionEventListener(m, loader);
            setExceptionHandler(m, loader);

            updateAllowZeroIdSetting(m);
            updateCacheCoordination(m, loader);
            processSessionCustomizer(m, loader);
        } else {
            setSessionEventListener(m, loader);
            setExceptionHandler(m, loader);

            updateBatchWritingSetting(m);
    
            updateNativeSQLSetting(m);
            updateSQLCastSetting(m);
            updateUppercaseSetting(m);
            updateCacheStatementSettings(m);
            updateTemporalMutableSetting(m);
            updateTableCreationSettings(m);
            if (!session.hasBroker()) {
                updateAllowZeroIdSetting(m);
            }
            updateIdValidation(m);
            updatePessimisticLockTimeout(m);
            updateQueryTimeout(m);
            if (!session.hasBroker()) {
                updateCacheCoordination(m, loader);
            }
            updatePartitioning(m, loader);
            
            // Customizers should be processed last
            processDescriptorCustomizers(m, loader);
            processSessionCustomizer(m, loader);
            
            setDescriptorNamedQueries(m);
        }
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
        String connectionString = getConfigPropertyAsStringLogDebug(PersistenceUnitProperties.LOGGING_CONNECTION, m, session);
        if (connectionString != null) {
            log.setShouldPrintConnection(Boolean.parseBoolean(sessionString));
        }
        String exString = getConfigPropertyAsStringLogDebug(PersistenceUnitProperties.LOGGING_EXCEPTIONS, m, session);
        if (exString != null) {
            log.setShouldLogExceptionStackTrace(Boolean.parseBoolean(exString));
        }
        String shouldDisplayData = getConfigPropertyAsStringLogDebug(PersistenceUnitProperties.LOGGING_PARAMETERS, m, session);
        if (shouldDisplayData != null) {
            log.setShouldDisplayData(Boolean.parseBoolean(shouldDisplayData));
        }
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
            String customizerClassName = (String)entry.getValue();
            
            ClassDescriptor descriptor = session.getDescriptorForAlias(name);
            if (descriptor == null) {
                try {
                    Class javaClass = findClass(name, loader);
                    descriptor = session.getDescriptor(javaClass);
                } catch (Exception ex) {
                    throw EntityManagerSetupException.failedWhileProcessingProperty(PersistenceUnitProperties.DESCRIPTOR_CUSTOMIZER_ + name, customizerClassName, ex);
                }
            }
            if (descriptor != null) {
                Class customizerClass = findClassForProperty(customizerClassName, PersistenceUnitProperties.DESCRIPTOR_CUSTOMIZER_ + name, loader);
                try {
                    DescriptorCustomizer customizer = (DescriptorCustomizer)customizerClass.newInstance();
                    customizer.customize(descriptor);
                } catch (Exception ex) {
                    throw EntityManagerSetupException.failedWhileProcessingProperty(PersistenceUnitProperties.DESCRIPTOR_CUSTOMIZER_ + name, customizerClassName, ex);
                }
            } else {
                throw EntityManagerSetupException.failedWhileProcessingProperty(PersistenceUnitProperties.DESCRIPTOR_CUSTOMIZER_ + name, customizerClassName, null);                
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
    
    public boolean isHalfPredeployedCompositeMember() {
        return state == STATE_HALF_PREDEPLOYED_COMPOSITE_MEMBER;
    }
    
    public String getPersistenceUnitUniqueName() {
        return this.persistenceUnitUniqueName;
    }
    
    public int getFactoryCount() {
        return factoryCount;
    }

    public String getSessionName() {
        return this.sessionName;
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
        session.log(SessionLog.FINEST, SessionLog.PROPERTIES, "undeploy_begin", new Object[]{getPersistenceUnitInfo().getPersistenceUnitName(), session.getName(), state, factoryCount});
        try {
            factoryCount--;
            if(factoryCount > 0) {
                return;
            }
            synchronized (EntityManagerFactoryProvider.emSetupImpls) {
                state = STATE_UNDEPLOYED;
                removeSessionFromGlobalSessionManager();
                // remove undeployed emSetupImpl from the map
                EntityManagerFactoryProvider.emSetupImpls.remove(session.getName());
            }
        } finally {
            session.log(SessionLog.FINEST, SessionLog.PROPERTIES, "undeploy_end", new Object[]{getPersistenceUnitInfo().getPersistenceUnitName(), session.getName(), state, factoryCount});
            if(state == STATE_UNDEPLOYED) {
                session = null;
            }
        }
    }    
    
    /**
     * Allow customized session event listener to be added into session.
     * The method needs to be called in deploy stage.
     */
    protected void setSessionEventListener(Map m, ClassLoader loader){
        //Set event listener if it has been specified.
        String sessionEventListenerClassName = EntityManagerFactoryProvider.getConfigPropertyAsStringLogDebug(PersistenceUnitProperties.SESSION_EVENT_LISTENER_CLASS, m, session);
        if(sessionEventListenerClassName!=null){
            Class sessionEventListenerClass = findClassForProperty(sessionEventListenerClassName,PersistenceUnitProperties.SESSION_EVENT_LISTENER_CLASS, loader);
            try {
                SessionEventListener sessionEventListener = (SessionEventListener)buildObjectForClass(sessionEventListenerClass, SessionEventListener.class);
                if(sessionEventListener!=null){
                    session.getEventManager().addListener(sessionEventListener);
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
    protected void setExceptionHandler(Map m, ClassLoader loader){
        //Set exception handler if it was specified.
        String exceptionHandlerClassName = EntityManagerFactoryProvider.getConfigPropertyAsStringLogDebug(PersistenceUnitProperties.EXCEPTION_HANDLER_CLASS, m, session);
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
        String batchWritingSettingString = PropertiesHandler.getPropertyValueLogDebug(PersistenceUnitProperties.BATCH_WRITING, persistenceProperties, this.session);
        if (batchWritingSettingString != null) {
             this.session.getPlatform().setUsesBatchWriting(batchWritingSettingString != BatchWriting.None);
             if (batchWritingSettingString == BatchWriting.JDBC) {
                 this.session.getPlatform().setUsesJDBCBatchWriting(true);
                 this.session.getPlatform().setUsesNativeBatchWriting(false);
             } else if (batchWritingSettingString == BatchWriting.Buffered) {
                 this.session.getPlatform().setUsesJDBCBatchWriting(false);
                 this.session.getPlatform().setUsesNativeBatchWriting(false);
             } else if (batchWritingSettingString == BatchWriting.OracleJDBC) {
                 this.session.getPlatform().setUsesNativeBatchWriting(true);
                 this.session.getPlatform().setUsesJDBCBatchWriting(true);
             }
        }
        // Set batch size.
        String sizeString = EntityManagerFactoryProvider.getConfigPropertyAsStringLogDebug(PersistenceUnitProperties.BATCH_WRITING_SIZE, persistenceProperties, this.session);
        if (sizeString != null) {
            try {
                this.session.getPlatform().setMaxBatchWritingSize(Integer.parseInt(sizeString));
            } catch (NumberFormatException invalid) {
                session.handleException(ValidationException.invalidValueForProperty(sizeString, PersistenceUnitProperties.BATCH_WRITING_SIZE, invalid));
            }
        }
    }

    /**
     * Load the Metadata Repository for Extensibility
     */
    protected void updateMetadataRepository(Map m, ClassLoader loader){
        String repository = EntityManagerFactoryProvider.getConfigPropertyAsStringLogDebug(PersistenceUnitProperties.METADATA_SOURCE, m, null, session);
        if (repository!=null) {
            if (repository.equalsIgnoreCase("XML")) {
                processor.setMetadataSource(new XMLMetadataSource());
            } else {
                Class transportClass = findClassForProperty(repository, PersistenceUnitProperties.METADATA_SOURCE, loader);
                try {
                    processor.setMetadataSource((MetadataSource)transportClass.newInstance());
                } catch (Exception invalid) {
                    session.handleException(EntityManagerSetupException.failedToInstantiateProperty(repository, PersistenceUnitProperties.METADATA_SOURCE,invalid));
                }
            }
        }
    }

    /**
     * Enable or disable the capability of Native SQL function.  
     * The method needs to be called in deploy stage.
     */
    protected void updateNativeSQLSetting(Map m){
        //Set Native SQL flag if it was specified.
        String nativeSQLString = EntityManagerFactoryProvider.getConfigPropertyAsStringLogDebug(PersistenceUnitProperties.NATIVE_SQL, m, session);
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
     * Enable or disable SQL casting.
     */
    protected void updateSQLCastSetting(Map m) {
        //Set Native SQL flag if it was specified.
        String sqlCastString = EntityManagerFactoryProvider.getConfigPropertyAsStringLogDebug(PersistenceUnitProperties.SQL_CAST, m, session);
        if (sqlCastString != null) {
           if (sqlCastString.equalsIgnoreCase("true")) {
                 session.getProject().getLogin().getPlatform().setIsCastRequired(true);
           } else if (sqlCastString.equalsIgnoreCase("false")) {
               session.getProject().getLogin().getPlatform().setIsCastRequired(false);
           } else {
                 session.handleException(ValidationException.invalidBooleanValueForProperty(sqlCastString, PersistenceUnitProperties.SQL_CAST));
           }
        }
    }
    
    /**
     * Enable or disable forcing field names to uppercase.  
     * The method needs to be called in deploy stage.
     */
    protected void updateUppercaseSetting(Map m){
        //Set Native SQL flag if it was specified.
        String uppercaseString = EntityManagerFactoryProvider.getConfigPropertyAsStringLogDebug(PersistenceUnitProperties.NATIVE_QUERY_UPPERCASE_COLUMNS, m, session);
        if (uppercaseString != null) {
           if (uppercaseString.equalsIgnoreCase("true") ){
               this.session.getProject().getLogin().setShouldForceFieldNamesToUpperCase(true);
           } else if (uppercaseString.equalsIgnoreCase("false")) {
               this.session.getProject().getLogin().setShouldForceFieldNamesToUpperCase(false);
           } else {
               this.session.handleException(ValidationException.invalidBooleanValueForProperty(uppercaseString, PersistenceUnitProperties.NATIVE_QUERY_UPPERCASE_COLUMNS));
           }
        }
    }

    /**

    /**
     * Enable or disable forcing field names to be case insensitive.  Implementation of case insensitive column handling relies on setting
     * both sides to uppercase (the column names from annotations/xml as well as what is returned from the JDBC/statement)
     * The method needs to be called in deploy stage.  
     */
    public static void updateCaseSensitivitySettings(Map m, MetadataProject project, AbstractSession session){
        //Set Native SQL flag if it was specified.
        String insensitiveString = EntityManagerFactoryProvider.getConfigPropertyAsStringLogDebug(PersistenceUnitProperties.UPPERCASE_COLUMN_NAMES, m, "false", session);
        if (insensitiveString != null) {
           if (insensitiveString.equalsIgnoreCase("true")) {
               project.setShouldForceFieldNamesToUpperCase(true);
               session.getProject().getLogin().setShouldForceFieldNamesToUpperCase(true);
           } else if (insensitiveString.equalsIgnoreCase("false")) {
               project.setShouldForceFieldNamesToUpperCase(false);
           } else {
               session.handleException(ValidationException.invalidBooleanValueForProperty(insensitiveString, PersistenceUnitProperties.UPPERCASE_COLUMN_NAMES));
           }
        }
    }
    
    /**
     * Update the default pessimistic lock timeout value. 
     * @param persistenceProperties the properties map
     */
    protected void updatePessimisticLockTimeout(Map persistenceProperties) {
        String pessimisticLockTimeout = EntityManagerFactoryProvider.getConfigPropertyAsStringLogDebug(PersistenceUnitProperties.PESSIMISTIC_LOCK_TIMEOUT, persistenceProperties, session);

        if (pessimisticLockTimeout != null) {
            try {
                session.setPessimisticLockTimeoutDefault(Integer.parseInt(pessimisticLockTimeout));
            } catch (NumberFormatException invalid) {
                session.handleException(ValidationException.invalidValueForProperty(pessimisticLockTimeout, PersistenceUnitProperties.PESSIMISTIC_LOCK_TIMEOUT, invalid));
            }
        }
    }
    
    /**
     * Enable or disable statements cached, update statements cache size. 
     * The method needs to be called in deploy stage. 
     */
    protected void updateCacheStatementSettings(Map m){
        // Cache statements if flag was specified.
        String statmentsNeedBeCached = EntityManagerFactoryProvider.getConfigPropertyAsStringLogDebug(PersistenceUnitProperties.CACHE_STATEMENTS, m, session);
        if (statmentsNeedBeCached!=null) {
            if (statmentsNeedBeCached.equalsIgnoreCase("true")) {
                if (session.isServerSession() && ((ServerSession)session).getConnectionPools().isEmpty()){
                    session.log(SessionLog.WARNING, SessionLog.PROPERTIES, "persistence_unit_ignores_statments_cache_setting", new Object[]{null});
                 } else {
                     session.getProject().getLogin().setShouldCacheAllStatements(true);
                 }
            } else if (statmentsNeedBeCached.equalsIgnoreCase("false")) {
                session.getProject().getLogin().setShouldCacheAllStatements(false);
            } else {
                session.handleException(ValidationException.invalidBooleanValueForEnableStatmentsCached(statmentsNeedBeCached));
            }
        }
        
        // Set statement cache size if specified.
        String cacheStatementsSize = EntityManagerFactoryProvider.getConfigPropertyAsStringLogDebug(PersistenceUnitProperties.CACHE_STATEMENTS_SIZE, m, session);
        if (cacheStatementsSize!=null) {
            try {
                session.getProject().getLogin().setStatementCacheSize(Integer.parseInt(cacheStatementsSize));
            } catch (NumberFormatException e) {
                session.handleException(ValidationException.invalidCacheStatementsSize(cacheStatementsSize,e.getMessage()));
            }
        }
    }

    /**
     * Enable or disable default allowing 0 as an id. 
     */
    @SuppressWarnings("deprecation")
    protected void updateAllowZeroIdSetting(Map m) {
        String allowZero = EntityManagerFactoryProvider.getConfigPropertyAsStringLogDebug(PersistenceUnitProperties.ALLOW_ZERO_ID, m, this.session);
        if (allowZero != null) {
            if (allowZero.equalsIgnoreCase("true")) {
               Helper.isZeroValidPrimaryKey = true;
            } else if (allowZero.equalsIgnoreCase("false")) {
                Helper.isZeroValidPrimaryKey = false;
            } else {
                session.handleException(ValidationException.invalidBooleanValueForProperty(allowZero, PersistenceUnitProperties.ALLOW_ZERO_ID));
            }
        }
    }

    /**
     * Enable or disable default allowing 0 as an id. 
     */
    protected void updateIdValidation(Map m) {
        String idValidationString = EntityManagerFactoryProvider.getConfigPropertyAsStringLogDebug(PersistenceUnitProperties.ID_VALIDATION, m, session);
        if (idValidationString != null) {
            session.getProject().setDefaultIdValidation(IdValidation.valueOf(idValidationString));
        }
    }
    
    /**
     * sets the TABLE_CREATION_SUFFIX property on the session's project to be applied to all table creation statements (DDL)
     */
    protected void updateTableCreationSettings(Map m) {
        String tableCreationSuffix = EntityManagerFactoryProvider.getConfigPropertyAsStringLogDebug(PersistenceUnitProperties.TABLE_CREATION_SUFFIX, m, session);
        if (tableCreationSuffix != null && tableCreationSuffix.length()>0) {
            session.getPlatform().setTableCreationSuffix(tableCreationSuffix);
        }
    }

    /**
     * Enable or disable default temporal mutable setting. 
     * The method needs to be called in deploy stage. 
     */
    protected void updateTemporalMutableSetting(Map m) {
        // Cache statements if flag was specified.
        String temporalMutable = EntityManagerFactoryProvider.getConfigPropertyAsStringLogDebug(PersistenceUnitProperties.TEMPORAL_MUTABLE, m, session);
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
    protected void setDescriptorNamedQueries(Map m) {
        // Copy named queries to session if the flag has been specified.
        String addNamedQueriesString  = EntityManagerFactoryProvider.getConfigPropertyAsStringLogDebug(PersistenceUnitProperties.INCLUDE_DESCRIPTOR_QUERIES, m, session);
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

    private void updateQueryTimeout(Map persistenceProperties) {
        String timeout = EntityManagerFactoryProvider.getConfigPropertyAsStringLogDebug(PersistenceUnitProperties.QUERY_TIMEOUT, persistenceProperties, session);

        try {
            if (timeout != null) {
                session.setQueryTimeoutDefault(Integer.parseInt(timeout));
            }
        } catch (NumberFormatException exception) {
            this.session.handleException(ValidationException.invalidValueForProperty(timeout, PersistenceUnitProperties.QUERY_TIMEOUT, exception));
        }
    }

    /**
     * If Bean Validation is enabled, bootstraps Bean Validation on descriptors.
     * @param puProperties merged properties for this persistence unit
     */
    private void addBeanValidationListeners(Map puProperties, ClassLoader appClassLoader) {
        ValidationMode validationMode = getValidationMode(persistenceUnitInfo, puProperties);
        if (validationMode == ValidationMode.AUTO || validationMode == ValidationMode.CALLBACK) {
            // BeanValidationInitializationHelper contains static reference to javax.validation.* classes. We need to support
            // environment where these classes are not available.
            // To guard against some vms that eagerly resolve, reflectively load class to prevent any static reference to it
            String helperClassName = "org.eclipse.persistence.internal.jpa.deployment.BeanValidationInitializationHelper$BeanValidationInitializationHelperImpl";
            ClassLoader eclipseLinkClassLoader = EntityManagerSetupImpl.class.getClassLoader();
            Class helperClass;
            try {
                if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()) {
                    helperClass = (Class) AccessController.doPrivileged(
                            new PrivilegedClassForName(helperClassName, true, eclipseLinkClassLoader));
                } else {
                    helperClass = PrivilegedAccessHelper.getClassForName(helperClassName, true, eclipseLinkClassLoader);
                }
                BeanValidationInitializationHelper beanValidationInitializationHelper = (BeanValidationInitializationHelper)helperClass.newInstance();
                beanValidationInitializationHelper.bootstrapBeanValidation(puProperties, session, processor.getProject(), appClassLoader);
            } catch (Throwable e) {  //Catching Throwable to catch any linkage errors on vms that resolve eagerly
                if (validationMode == ValidationMode.CALLBACK) {
                    throw PersistenceUnitLoadingException.exceptionObtainingRequiredBeanValidatorFactory(e);
                } // else validationMode == ValidationMode.AUTO. Log a message, Ignore the exception
                // TODO use proper message string, i18n.
                session.logMessage("Could not initialize Validation Factory. Encountered following exception: " + e);
            }
        }
    }

    /**
     * Validation mode from information in persistence.xml and properties specified at EMF creation
     * @param persitenceUnitInfo PersitenceUnitInfo instance for this persistence unit
     * @param puProperties merged properties for this persistence unit
     * @return validation mode
     */
    private static ValidationMode getValidationMode(PersistenceUnitInfo persitenceUnitInfo, Map puProperties) {
        ValidationMode validationMode = null;
        // Initialize with value in persitence.xml
        // Using reflection to call getValidationMode to prevent blowing up while we are running in JPA 1.0 environment
        // (This would be all JavaEE5 appservers) where PersistenceUnitInfo does not implement method getValidationMode().
        try {
            Method method = null;
            if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()) {
                method = (Method) AccessController.doPrivileged(new PrivilegedGetDeclaredMethod(PersistenceUnitInfo.class, "getValidationMode", null));
                validationMode = (ValidationMode) AccessController.doPrivileged(new PrivilegedMethodInvoker(method, persitenceUnitInfo));
            } else {
                method = PrivilegedAccessHelper.getDeclaredMethod(PersistenceUnitInfo.class, "getValidationMode", null);
                validationMode = (ValidationMode) PrivilegedAccessHelper.invokeMethod(method, persitenceUnitInfo, null);
            }
        } catch (Throwable exception) {
            // We are running in JavaEE5 environment. Catch and swallow any exceptions and return null.
        }

        if(validationMode == null) {
            // Default to AUTO as specified in JPA spec.
            validationMode = ValidationMode.AUTO;
        }
        //Check if overridden at emf creation
        String validationModeAtEMFCreation = (String) puProperties.get(PersistenceUnitProperties.VALIDATION_MODE);
        if(validationModeAtEMFCreation != null) {
            //User would get IllegalArgumentException if he has specified invalid mode
            validationMode = ValidationMode.valueOf(validationModeAtEMFCreation);
        }
        return validationMode;
    }

    /**
     * INTERNAL:
     * Return an instance of Metamodel interface for access to the
     * metamodel of the persistence unit.
     * @return Metamodel instance
     * @since Java Persistence 2.0
     */
    public Metamodel getMetamodel() {
        // perform lazy initialisation
        Metamodel tempMetaModel = null;
        if(null == metaModel) {
            // 338837: verify that the collection is not empty - this would mean entities did not make it into the search path
            tempMetaModel = new MetamodelImpl(this);
            // If the canonical metamodel classes exist, initialize them
            initializeCanonicalMetamodel(tempMetaModel);
            // set variable after init has executed without exception
            metaModel = tempMetaModel;
        }
        return metaModel;
    }

    /**
     * INTERNAL:
     * Initialize the Canonical Metamodel classes generated by EclipseLink
     * @since Java Persistence 2.0 
     */
    protected void initializeCanonicalMetamodel(Metamodel model) {
        // 338837: verify that the collection is not empty - this would mean entities did not make it into the search path
        if(null == model.getManagedTypes() || model.getManagedTypes().isEmpty()) {
            getSession().log(SessionLog.FINER, SessionLog.METAMODEL, "metamodel_type_collection_empty");
        }
        for (ManagedType manType : model.getManagedTypes()) {
            boolean classInitialized = false;
            String className = MetadataHelper.getQualifiedCanonicalName(manType.getJavaType().getName(), getSession());
            try {                
                Class clazz = (Class)this.getSession().getDatasourcePlatform().convertObject(className, ClassConstants.CLASS);
                classInitialized=true;
                this.getSession().log(SessionLog.FINER, SessionLog.METAMODEL, "metamodel_canonical_model_class_found", className);                
                String fieldName = "";
                for(Object attribute : manType.getDeclaredAttributes()) { 
                    try {
                        fieldName = ((Attribute)attribute).getName();
                        if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                          ((Field)AccessController.doPrivileged(new PrivilegedGetDeclaredField(clazz, fieldName, false))).set(clazz, attribute);
                         } else {
                            PrivilegedAccessHelper.getDeclaredField(clazz, fieldName, false).set(clazz, attribute);
                         }  
                    } catch (NoSuchFieldException nsfe) {
                        // Ignore fields missing in canonical model (dclarke bug 346106)
                    } catch (Exception e) {
                       ValidationException v = ValidationException.invalidFieldForClass(fieldName, clazz);
                       v.setInternalException(e);
                       throw v;
                    }
                }
            } catch (ConversionException exception){
            }
            if (!classInitialized) {
                getSession().log(SessionLog.FINER, SessionLog.METAMODEL, "metamodel_canonical_model_class_not_found", className);
            }
        }
    }    
    
    /**
     * INTERNAL:
     * Convenience function to allow us to reset the Metamodel 
     * in the possible case that we want to regenerate it.
     * This function is outside of the JPA 2.0 specification.
     * @param aMetamodel
     * @since Java Persistence 2.0 
     */
    public void setMetamodel(Metamodel aMetamodel) {
        this.metaModel = aMetamodel;
    }
    
    public boolean mustBeCompositeMember() {
        return mustBeCompositeMember(this.persistenceUnitInfo);
    }
    
    public boolean isCompositeMember() {
        return this.compositeEmSetupImpl != null;
    }
    
    public boolean isComposite() {
        return this.compositeMemberEmSetupImpls != null;
    }
    
    public static boolean mustBeCompositeMember(PersistenceUnitInfo puInfo) {
        String mustBeCompositeMemberStr = PropertiesHandler.getPropertyValue(PersistenceUnitProperties.COMPOSITE_UNIT_MEMBER, puInfo.getProperties(), false);
        if(mustBeCompositeMemberStr != null) {
            return mustBeCompositeMemberStr.equals("true");
        } else {
            return false;
        }
    }
    public static boolean isComposite(PersistenceUnitInfo puInfo) {
        String isCompositeString = PropertiesHandler.getPropertyValue(PersistenceUnitProperties.COMPOSITE_UNIT, puInfo.getProperties(), false);
        if(isCompositeString != null) {
            return isCompositeString.equals("true");
        } else {
            return false;
        }
    }
    
    public void setCompositeEmSetupImpl(EntityManagerSetupImpl compositeEmSetupImpl) {
        this.compositeEmSetupImpl = compositeEmSetupImpl;
    }

    public EntityManagerSetupImpl getCompositeEmSetupImpl() {
        return this.compositeEmSetupImpl;
    }
    
    // predeploy method will be used for static weaving
    public void setStaticWeaveInfo(StaticWeaveInfo staticWeaveInfo) {
        this.staticWeaveInfo = staticWeaveInfo;
    }

    protected void predeployCompositeMembers(Map predeployProperties, ClassLoader tempClassLoader) {
        // get all puInfos found in jar-files specified in composite's persistence.xml
        // all these puInfos are not composite because composites are recursively "taken apart", too. 
        Set<SEPersistenceUnitInfo> compositeMemberPuInfos = getCompositeMemberPuInfoSet(persistenceUnitInfo, predeployProperties);
        // makes sure each member has a non-null property, overrides where required properties with composite's predeploy properties.
        updateCompositeMembersProperties(compositeMemberPuInfos, predeployProperties);
        // Don't log these properties - may contain passwords. The properties will be logged by contained persistence units.
        Map compositeMemberMapOfProperties = (Map)getConfigProperty(PersistenceUnitProperties.COMPOSITE_UNIT_PROPERTIES, predeployProperties);
        this.compositeMemberEmSetupImpls = new HashSet(compositeMemberPuInfos.size());
        this.processor = new MetadataProcessor();
        if (enableWeaving) {
            this.weaver = new PersistenceWeaver(this.session, new HashMap());
        }
        
        // create containedEmSetupImpls and predeploy them for the first time.
        // predeploy divided in three stages (modes):
        // all composite members should complete a stage before any of them can move to the next one.
        for (SEPersistenceUnitInfo compositeMemberPuInfo : compositeMemberPuInfos) {
            // set composite's temporary classloader
            compositeMemberPuInfo.setNewTempClassLoader(tempClassLoader);
            String containedPuName = compositeMemberPuInfo.getPersistenceUnitName();
            EntityManagerSetupImpl containedEmSetupImpl = new EntityManagerSetupImpl(containedPuName, containedPuName);
            // set composite
            containedEmSetupImpl.setCompositeEmSetupImpl(this);
            // non-null only in case predeploy is used for static weaving
            containedEmSetupImpl.setStaticWeaveInfo(this.staticWeaveInfo);
            // the properties guaranteed to be non-null after updateCompositeMemberProperties call
            Map compositeMemberProperties = (Map)compositeMemberMapOfProperties.get(containedPuName);
            containedEmSetupImpl.predeploy(compositeMemberPuInfo, compositeMemberProperties);
            // reset temporary classloader back to the original
            compositeMemberPuInfo.setNewTempClassLoader(compositeMemberPuInfo.getClassLoader());
            this.compositeMemberEmSetupImpls.add(containedEmSetupImpl);
        }
        
        // after the first loop containedEmSetupImpls are in HalfPredeployed state, 
        // mode = COMPOSITE_MEMBER_MIDDLE mode
        for(EntityManagerSetupImpl containedEmSetupImpl : this.compositeMemberEmSetupImpls) {
            // properties not used, puInfo already set
            containedEmSetupImpl.predeploy(null, null);
        }

        // after the second loop containedEmSetupImpls are still in HalfPredeployed state, 
        // mode = COMPOSITE_MEMBER_FINAL mode
        for(EntityManagerSetupImpl containedEmSetupImpl : this.compositeMemberEmSetupImpls) {
            // properties not used, puInfo already set
            PersistenceWeaver containedWeaver = (PersistenceWeaver)containedEmSetupImpl.predeploy(null, null);
            // containedEmSetupImpl is finally in Predeployed state.
            // if both composite and composite member weavings enabled copy class details from member's weaver to composite's one.
            if(enableWeaving && containedWeaver != null) {
                this.weaver.getClassDetailsMap().putAll(containedWeaver.getClassDetailsMap());
            }
        }

        if(enableWeaving && this.weaver.getClassDetailsMap().isEmpty()) {
            this.weaver = null;
        }
    }

    protected void deployCompositeMembers(Map deployProperties, ClassLoader realClassLoader) {
        // Don't log these properties - may contain passwords. The properties will be logged by contained persistence units.
        Map compositeMemberMapOfProperties = (Map)getConfigProperty(PersistenceUnitProperties.COMPOSITE_UNIT_PROPERTIES, deployProperties);
        for(EntityManagerSetupImpl compositeMemberEmSetupImpl : this.compositeMemberEmSetupImpls) {
            // the properties guaranteed to be non-null after updateCompositeMemberProperties call
            Map compositeMemberProperties = (Map)compositeMemberMapOfProperties.get(compositeMemberEmSetupImpl.getPersistenceUnitInfo().getPersistenceUnitName());
            compositeMemberEmSetupImpl.deploy(realClassLoader, compositeMemberProperties);
            AbstractSession containedSession = compositeMemberEmSetupImpl.getSession(); 
            ((SessionBroker)session).registerSession(containedSession.getName(), containedSession);
        }
    }

    /*
     * Overide composite member properties' map with a new one, which
     * has (possibly empty but non-null) properties for each composite member,
     * for required properties overrides values with those from composite properties. 
     */
    protected void updateCompositeMembersProperties(Map compositeProperties) {
        Set<SEPersistenceUnitInfo> compositePuInfos = new HashSet(compositeMemberEmSetupImpls.size());
        for (EntityManagerSetupImpl compositeMemberEmSetupImpl : compositeMemberEmSetupImpls) {
            compositePuInfos.add((SEPersistenceUnitInfo)compositeMemberEmSetupImpl.persistenceUnitInfo);
        }
        updateCompositeMembersProperties(compositePuInfos, compositeProperties);
    }

    /*
     * Overide composite member properties' map with a new one, which
     * has (possibly empty but non-null) properties for each composite member,
     * for required properties overrides values with those from composite properties. 
     * Parameter compositePuInfo indicates whether compositeMemberPreoperties should be merged (overriding) with its puInfo properties
     * (false for predeploy, true for deploy).
     */
    protected void updateCompositeMembersProperties(Set<SEPersistenceUnitInfo> compositePuInfos, Map compositeProperties) {
        // Don't log these properties - may contain passwords. The properties will be logged by contained persistence units.
        Map compositeMemberMapOfProperties = (Map)getConfigProperty(PersistenceUnitProperties.COMPOSITE_UNIT_PROPERTIES, compositeProperties);
        Map newCompositeMemberMapOfProperties;
        if (compositeMemberMapOfProperties == null) {
            newCompositeMemberMapOfProperties = new HashMap(compositePuInfos.size());
        } else {
            // Don't alter user-supplied properties' map - create a copy instead
            newCompositeMemberMapOfProperties = new HashMap(compositeMemberMapOfProperties);
        }
        
        for (SEPersistenceUnitInfo compositePuInfo : compositePuInfos) {
            String compositeMemberPuName = compositePuInfo.getPersistenceUnitName();
            Map compositeMemberProperties = (Map)newCompositeMemberMapOfProperties.get(compositeMemberPuName);
            Map newCompositeMemberProperties;
            if (compositeMemberProperties == null) {
                newCompositeMemberProperties = new HashMap();
            } else {
                // Don't alter user-supplied properties - create a copy instead
                newCompositeMemberProperties = new HashMap(compositeMemberProperties);
            }
            overrideMemberProperties(newCompositeMemberProperties, compositeProperties);
            newCompositeMemberProperties = mergeMaps(newCompositeMemberProperties, compositePuInfo.getProperties());
            translateOldProperties(newCompositeMemberProperties, session);
            newCompositeMemberMapOfProperties.put(compositeMemberPuName, newCompositeMemberProperties);
        }
        
        // set the new COMPOSITE_PROPERTIES into compositeProperties
        compositeProperties.put(PersistenceUnitProperties.COMPOSITE_UNIT_PROPERTIES, newCompositeMemberMapOfProperties);
    }

    protected void generateDDL(Map props) {
        if (this.compositeMemberEmSetupImpls == null) {
            boolean createTables = false, shouldDropFirst = false;
           
            String ddlGeneration = getConfigPropertyAsString(PersistenceUnitProperties.DDL_GENERATION, props, PersistenceUnitProperties.NONE);
            ddlGeneration = ddlGeneration.toLowerCase();
            if(ddlGeneration.equals(PersistenceUnitProperties.NONE)) {
                return;
            }
    
            if(ddlGeneration.equals(PersistenceUnitProperties.CREATE_ONLY) || 
                ddlGeneration.equals(PersistenceUnitProperties.DROP_AND_CREATE)) {
                createTables = true;
                if(ddlGeneration.equals(PersistenceUnitProperties.DROP_AND_CREATE)) {
                    shouldDropFirst = true;
                }
            } 
            
            if (createTables) {
                String ddlGenerationMode = getConfigPropertyAsString(PersistenceUnitProperties.DDL_GENERATION_MODE, props, PersistenceUnitProperties.DEFAULT_DDL_GENERATION_MODE);
                // Optimize for cases where the value is explicitly set to NONE 
                if (ddlGenerationMode.equals(PersistenceUnitProperties.NONE)) {                
                    return;
                }
    
                if (isCompositeMember()) {
                    // debug output added to make it easier to navigate the log because the method is called outside of composite member deploy
                    session.log(SessionLog.FINEST, SessionLog.PROPERTIES, "composite_member_begin_call", new Object[]{"generateDDL", persistenceUnitInfo.getPersistenceUnitName(), state});
                }
                SchemaManager mgr = new SchemaManager(session);
                
                if (ddlGenerationMode.equals(PersistenceUnitProperties.DDL_DATABASE_GENERATION) || ddlGenerationMode.equals(PersistenceUnitProperties.DDL_BOTH_GENERATION)) {
                    writeDDLToDatabase(mgr, shouldDropFirst);                
                }
    
                if (ddlGenerationMode.equals(PersistenceUnitProperties.DDL_SQL_SCRIPT_GENERATION)|| ddlGenerationMode.equals(PersistenceUnitProperties.DDL_BOTH_GENERATION)) {
                    String appLocation = getConfigPropertyAsString(PersistenceUnitProperties.APP_LOCATION, props, PersistenceUnitProperties.DEFAULT_APP_LOCATION);
                    String createDDLJdbc = getConfigPropertyAsString(PersistenceUnitProperties.CREATE_JDBC_DDL_FILE, props, PersistenceUnitProperties.DEFAULT_CREATE_JDBC_FILE_NAME);
                    String dropDDLJdbc = getConfigPropertyAsString(PersistenceUnitProperties.DROP_JDBC_DDL_FILE, props,  PersistenceUnitProperties.DEFAULT_DROP_JDBC_FILE_NAME);
                    writeDDLsToFiles(mgr, appLocation,  createDDLJdbc,  dropDDLJdbc);                
                }
                if (isCompositeMember()) {
                    // debug output added to make it easier to navigate the log because the method is called outside of composite member deploy
                    session.log(SessionLog.FINEST, SessionLog.PROPERTIES, "composite_member_end_call", new Object[]{"generateDDL", persistenceUnitInfo.getPersistenceUnitName(), state});
                }
            }
        } else {
            // composite
            Map compositeMemberMapOfProperties = (Map)getConfigProperty(PersistenceUnitProperties.COMPOSITE_UNIT_PROPERTIES, props);
            for(EntityManagerSetupImpl compositeMemberEmSetupImpl : this.compositeMemberEmSetupImpls) {
                // the properties guaranteed to be non-null after updateCompositeMemberProperties call
                Map compositeMemberProperties = (Map)compositeMemberMapOfProperties.get(compositeMemberEmSetupImpl.getPersistenceUnitInfo().getPersistenceUnitName());
                compositeMemberEmSetupImpl.generateDDL(compositeMemberProperties);
            }
        }
    }
    
    /*
     * For required properties overrides values with those from composite properties. 
     */
    protected static void overrideMemberProperties(Map memberProperties, Map compositeProperties) {
        String transactionTypeProp =  (String)compositeProperties.get(PersistenceUnitProperties.TRANSACTION_TYPE);
        if (transactionTypeProp != null) {
            memberProperties.put(PersistenceUnitProperties.TRANSACTION_TYPE, transactionTypeProp);
        } else {
            memberProperties.remove(PersistenceUnitProperties.TRANSACTION_TYPE);
        }

        String serverPlatformProp =  (String)compositeProperties.get(PersistenceUnitProperties.TARGET_SERVER);
        if (serverPlatformProp != null) {
            memberProperties.put(PersistenceUnitProperties.TARGET_SERVER, serverPlatformProp);
        } else {
            memberProperties.remove(PersistenceUnitProperties.TARGET_SERVER);
        }
    }
    
    /*
     * If a member is composite then add its members instead.
     * All members' puNames must be unique.
     * Return a Map of composite member SEPersistenceUnitInfo keyed by persistence unit name.
     */
    protected static Map<String, SEPersistenceUnitInfo> getCompositeMemberPuInfoMap(PersistenceUnitInfo puInfo, Map predeployProperties) {
        Set<SEPersistenceUnitInfo> memeberPuInfoSet = PersistenceUnitProcessor.getPersistenceUnits(puInfo.getClassLoader(), predeployProperties, puInfo.getJarFileUrls());
        HashMap<String, SEPersistenceUnitInfo> memberPuInfoMap = new HashMap(memeberPuInfoSet.size());        
        for (SEPersistenceUnitInfo memberPuInfo : memeberPuInfoSet) {
            // override transaction type with composite's transaction type
            memberPuInfo.setTransactionType(puInfo.getTransactionType());
            // override properties that should be overridden by composit's properties
            overrideMemberProperties(memberPuInfo.getProperties(), puInfo.getProperties());
            if (isComposite(memberPuInfo)) {
                Map<String, SEPersistenceUnitInfo> containedMemberPuInfoMap = getCompositeMemberPuInfoMap(memberPuInfo, memberPuInfo.getProperties());
                Iterator<Map.Entry<String, SEPersistenceUnitInfo>> it = containedMemberPuInfoMap.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<String, SEPersistenceUnitInfo> entry = it.next();
                    String containedMemberPuName = entry.getKey();
                    SEPersistenceUnitInfo containedMemberPuInfo = entry.getValue();
                    SEPersistenceUnitInfo anotherMemeberPuInfo = memberPuInfoMap.get(containedMemberPuName); 
                    if (anotherMemeberPuInfo == null) {
                        memberPuInfoMap.put(containedMemberPuName, containedMemberPuInfo);
                    } else {
                        throwPersistenceUnitNameAlreadyInUseException(containedMemberPuName, containedMemberPuInfo, anotherMemeberPuInfo);
                    }
                }
            } else {
                String memberPuName = memberPuInfo.getPersistenceUnitName();
                SEPersistenceUnitInfo anotherMemeberPuInfo = memberPuInfoMap.get(memberPuName); 
                if (anotherMemeberPuInfo == null) {
                    memberPuInfoMap.put(memberPuName, memberPuInfo);
                } else {
                    throwPersistenceUnitNameAlreadyInUseException(memberPuName, memberPuInfo, anotherMemeberPuInfo);
                }
            }
        }
        return memberPuInfoMap;
    }

    /*
     * If a member is composite then add its members instead.
     * All members' puNames must be unique.
     * Return a Set of composite member SEPersistenceUnitInfo.
     */
    protected static Set<SEPersistenceUnitInfo> getCompositeMemberPuInfoSet(PersistenceUnitInfo puInfo, Map predeployProperties) {
        return new HashSet(getCompositeMemberPuInfoMap(puInfo, predeployProperties).values());
    }
    
    public static void throwPersistenceUnitNameAlreadyInUseException(String puName, PersistenceUnitInfo newPuInfo, PersistenceUnitInfo exsitingPuInfo) { 
        String puUrl;
        String anotherPuUrl;
        try {
            puUrl = URLDecoder.decode(newPuInfo.getPersistenceUnitRootUrl().toString(), "UTF8");
            anotherPuUrl = URLDecoder.decode(exsitingPuInfo.getPersistenceUnitRootUrl().toString(), "UTF8");
        } catch (UnsupportedEncodingException e) {
            puUrl = newPuInfo.getPersistenceUnitRootUrl().toString();
            anotherPuUrl = exsitingPuInfo.getPersistenceUnitRootUrl().toString();
        }
        throw PersistenceUnitLoadingException.persistenceUnitNameAlreadyInUse(puName, puUrl, anotherPuUrl);
    }
}
