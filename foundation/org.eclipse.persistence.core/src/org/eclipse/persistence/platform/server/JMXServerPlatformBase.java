/*******************************************************************************
 * Copyright (c) 2010, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     06/30/2010-2.1.1 Michael O'Brien 
 *       - 316513: Enable JMX MBean functionality for JBoss, Glassfish and WebSphere in addition to WebLogic
 *       Move JMX MBean generic registration code up from specific platforms
 *       see <link>http://wiki.eclipse.org/EclipseLink/DesignDocs/316513</link>
 *     07/15/2010-2.1.1 Michael O'Brien 
 *       - 316513: registration/deregistration mismatch for MBean Object name reg=- and dereg=_ - no more instance already exists on redeploy
 *     10/18/2010-2.1.2 Michael O'Brien 
 *       - 328006: Refactor WebLogic MBeanServer registration to use active 
 *         WLS com.bea server when multiple instances returned 
 *       see <link>http://wiki.eclipse.org/EclipseLink/DesignDocs/316513#DI_4:_20100624:_Verify_correct_MBeanServer_available_when_running_multiple_MBeanServer_Instances</link>        
 *     01/01/2011-2.2 Michael O'Brien 
 *       - 333160: ModuleName string extraction code does not handle -1 not found index in 3 of 5 cases 
 *     11/01/2011-2.2 Michael O'Brien 
 *       - 333336: findMBeanServer() requires security API AccessController.doPrivileged() 
 *         private run method security block. 
 ******************************************************************************/  
package org.eclipse.persistence.platform.server;

import java.lang.management.ManagementFactory;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.List;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedMethodInvoker;

import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.services.mbean.MBeanDevelopmentServices;
import org.eclipse.persistence.services.mbean.MBeanRuntimeServicesMBean;
import org.eclipse.persistence.sessions.DatabaseSession;

/**
 * PUBLIC:
 * Subclass of org.eclipse.persistence.platform.server.ServerPlatformBase 
 * in support of the JMXEnabledPlatform interface
 * <p>
 * This is the abstract superclass of all platforms for all servers that contain a subclass
 * that implements the JMXEnabledPlatform interface. 
 * Each DatabaseSession
 * contains an instance of the receiver, to help the DatabaseSession determine:
 * <p><ul>
 * <li> Whether or not to enable JTA (external transaction control)
 * <li> How to register/unregister for runtime services (JMX/MBean)
 * <li> Whether or not to enable runtime services
 * </ul><p>
 * Subclasses already exist to provide configurations for Oc4J, WebLogic, JBoss, NetWeaver, GlassFish and WebSphere.
 * <p>
 * If the versioned platform subclass is JMX enabled by EclipseLink (registers MBeans) then that 
 * server platform must implement the JMXEnabledPlatform interface
 * To provide some different behavior than the provided ServerPlatform(s), we recommend
 * subclassing org.eclipse.persistence.platform.server.JMXServerPlatformBase (or a subclass),
 * and overriding:
 * <ul>
 * <li>JMXEnabledPlatform.prepareServerSpecificServicesMBean()
 * </ul>
 * for the desired behavior.
 *
 * @see org.eclipse.persistence.platform.server.ServerPlatformBase
 * @since EclipseLink 2.1.1
 */
public abstract class JMXServerPlatformBase extends ServerPlatformBase {
    /** This JNDI address is for JMX MBean registration */
    private static final String JMX_JNDI_RUNTIME_REGISTER = "java:comp/env/jmx/runtime";
    /* 
     * If the cached MBeanServer is not used, then the unregister jndi address must be used to create a context
     * Note: the context must be explicitly closed after use or we may cache the user and get a
     * weblogic.management.NoAccessRuntimeException when trying to use the associated MBeanServer
     * see http://bugs.eclipse.org/238343
     * see http://e-docs.bea.com/wls/docs100/jndi/jndi.html#wp467275  
     */
    /** This JNDI address is for JMX MBean unregistration */    
    private static final String JMX_JNDI_RUNTIME_UNREGISTER = "java:comp/jmx/runtime";

    /** This is the prefix for all MBeans that are registered with their specific session name appended */
    public static final String JMX_REGISTRATION_PREFIX = "TopLink:Name=";
    /** The default indexed MBeanServer instance to use when multiple MBeanServer instances exist on the platform - usually only in JBoss */
    public static final int JMX_MBEANSERVER_INDEX_DEFAULT_FOR_MULTIPLE_SERVERS = 0;
    /** This persistence.xml or sessions.xml property is used to override the moduleName */
    protected static final String OVERRIDE_JMX_MODULENAME_PROPERTY = "eclipselink.jmx.moduleName"; 
    /** This persistence.xml or sessions.xml property is used to override the applicationName */
    protected static final String OVERRIDE_JMX_APPLICATIONNAME_PROPERTY = "eclipselink.jmx.applicationName"; 

    /**
     * The following constants and attributes are used to determine the module and application name
     * to satisfy the requirements for 248746 where we provide an identifier pair for JMX sessions.
     * Each application can have several modules.
     * 1) Application name - the persistence unit associated with the session (a 1-1 relationship)
     * 2) Module name - the ejb or war jar name (there is a 1-many relationship for module:session(s)) 
     */
    /** Override by subclass: Search String in application server ClassLoader for the application:persistence_unit name */
    private static String APP_SERVER_CLASSLOADER_OVERRIDE_DEFAULT = "postfix,match~should;be]implemented!by`subclass^";
    protected static String APP_SERVER_CLASSLOADER_APPLICATION_PU_SEARCH_STRING_PREFIX = APP_SERVER_CLASSLOADER_OVERRIDE_DEFAULT;
    protected static String APP_SERVER_CLASSLOADER_APPLICATION_PU_SEARCH_STRING_POSTFIX = APP_SERVER_CLASSLOADER_OVERRIDE_DEFAULT;

    /** Override by subclass: Search String in application server session for ejb modules */
    protected static String APP_SERVER_CLASSLOADER_MODULE_EJB_SEARCH_STRING_PREFIX = APP_SERVER_CLASSLOADER_OVERRIDE_DEFAULT;
    /** Override by subclass: Search String in application server session for war modules */
    protected static String APP_SERVER_CLASSLOADER_MODULE_WAR_SEARCH_STRING_PREFIX = APP_SERVER_CLASSLOADER_OVERRIDE_DEFAULT;
    protected static String APP_SERVER_CLASSLOADER_MODULE_EJB_WAR_SEARCH_STRING_POSTFIX = APP_SERVER_CLASSLOADER_OVERRIDE_DEFAULT;    
    
    
    /** Cache the ServerPlatform MBeanServer for performance */
    protected MBeanServer mBeanServer = null;
    
    /** cache the RuntimeServices MBean - during platform construction for JMXEnabledPlatform implementors */
    private MBeanRuntimeServicesMBean runtimeServicesMBean = null;

    /** moduleName determination is available during MBean registration only */
    private String moduleName = null;

    /** applicationName determination is available during MBean registration only */
    private String applicationName = null;
    
    /**
     * INTERNAL:
     * Default Constructor: Initialize so that runtime services and JTA are enabled. 
     */
    public JMXServerPlatformBase(DatabaseSession newDatabaseSession) {
        super(newDatabaseSession);
    }
    
    /**
     * Return the AbstractSession required to log to the non-platform EclipseLink log.
     * Note: The WeblogicEclipseLinkLog  defined for the WebLogic platform currently only supports the following
     * logging categories in the categoryMap HashMap
     * {connection=DebugJpaJdbcJdbc, 
     * cache=DebugJpaDataCache, 
     * transaction=DebugJpaJdbcJdbc, 
     * weaver=DebugJpaEnhance, 
     * query=DebugJpaQuery, 
     * dms=DebugJpaProfile, 
     * sequencing=DebugJpaRuntime, 
     * properties=DebugJpaRuntime, 
     * ejb=DebugJpaRuntime, 
     * jpa_metamodel=DebugJpaMetaData, 
     * sql=DebugJpaJdbcSql, 
     * ejb_or_metadata=DebugJpaMetaData, 
     * event=DebugJpaRuntime, 
     * server=DebugJpaRuntime, 
     * propagation=DebugJpaDataCache}    
     */
    protected AbstractSession getAbstractSession() {
        return ((AbstractSession)getDatabaseSession());
    }
    
    /** 
     * INTERNAL:
     * Return the MBeanServer to be used for MBean registration and deregistration.<br>
     * This MBeanServer reference is lazy loaded and cached on the platform.<br>
     * There are multiple ways of getting the MBeanServer<br>
     * <p>
     * 1) MBeanServerFactory static function - working for 3 of 4 servers  WebSphere, JBoss and Glassfish in a generic way<br>
     *   - JBoss returns 2 MBeanServers in the List - but one of them has a null domain - we don't use that one<br>
     *   - WebLogic may return 2 MBeanServers - in that case we want to register with the one containing the "com.bea" tree
     * 2) ManagementFactory static function - what is the difference in using this one over the one returning a List of servers<br>
     * 3) JNDI lookup<br>
     * 4) Direct server specific native API<br></p>
     * We are using method (1)<br>
     * 
     * @return the JMX specification MBeanServer
     */
    public MBeanServer getMBeanServer() {
    	/**
    	 * This function will attempt to get the MBeanServer via the findMBeanServer spec call.
    	 * 1) If the return list is null we attempt to retrieve the PlatformMBeanServer 
    	 * (if it exists or is enabled in this security context).
    	 * 2) If the list of MBeanServers returned is more than one 
    	 * we get the lowest indexed MBeanServer that does not on a null default domain.
    	 * 3) 333336: we need to wrap JMX calls in doPrivileged blocks
    	 * 4) fail-fast: if there are any issues with JMX - continue - don't block the deploy() 
    	 */
    	// lazy initialize the MBeanServer reference
        if(null == mBeanServer) {
        	List<MBeanServer> mBeanServerList = null;
            try {
            	if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()) {
            		try {
            			mBeanServerList = (List<MBeanServer>) AccessController.doPrivileged(
            				new PrivilegedExceptionAction() {
                				public List<MBeanServer> run() {
                					return MBeanServerFactory.findMBeanServer(null);
                				}
                			}
               			);
            		} catch (PrivilegedActionException pae) {
                        getAbstractSession().log(SessionLog.WARNING, SessionLog.SERVER, 
                                "failed_to_find_mbean_server", "null or empty List returned from privileged MBeanServerFactory.findMBeanServer(null)");
                        Context initialContext = null;
            			try {
            				initialContext = new InitialContext(); // the context should be cached
            				mBeanServer = (MBeanServer) initialContext.lookup(JMX_JNDI_RUNTIME_REGISTER);
                        } catch (NamingException ne) {
                            getAbstractSession().log(SessionLog.WARNING, SessionLog.SERVER, "failed_to_find_mbean_server", ne);
                        }            			
            		}
                } else {
                    mBeanServerList = MBeanServerFactory.findMBeanServer(null);
                }
                // Attempt to get the first MBeanServer we find - usually there is only one - when agentId == null we return a List of them
                if(null == mBeanServer && (null == mBeanServerList || mBeanServerList.isEmpty())) {
                    // Unable to acquire a JMX specification List of MBeanServer instances
                    getAbstractSession().log(SessionLog.WARNING, SessionLog.SERVER, 
                            "failed_to_find_mbean_server", "null or empty List returned from MBeanServerFactory.findMBeanServer(null)");
                    // Try alternate static method
                    if (!PrivilegedAccessHelper.shouldUsePrivilegedAccess()) {
                        mBeanServer = ManagementFactory.getPlatformMBeanServer();
                        if(null == mBeanServer) {
                            getAbstractSession().log(SessionLog.WARNING, SessionLog.SERVER, 
                                "failed_to_find_mbean_server", "null returned from ManagementFactory.getPlatformMBeanServer()");
                        } else {
                            getAbstractSession().log(SessionLog.FINER, SessionLog.SERVER, 
                                    "jmx_mbean_runtime_services_registration_mbeanserver_print",
                                    new Object[]{mBeanServer, mBeanServer.getMBeanCount(), mBeanServer.getDefaultDomain(), 0});
                        }
                    }
                } else {
                    // Use the first MBeanServer by default - there may be multiple domains each with their own MBeanServer
                    mBeanServer = mBeanServerList.get(JMX_MBEANSERVER_INDEX_DEFAULT_FOR_MULTIPLE_SERVERS);
                    if(mBeanServerList.size() > 1) {
                        // There are multiple MBeanServerInstances (usually only JBoss)
                        // 328006: WebLogic may also return multiple instances (we need to register the one containing the com.bea tree)
                        getAbstractSession().log(SessionLog.WARNING, SessionLog.SERVER, 
                                "jmx_mbean_runtime_services_registration_encountered_multiple_mbeanserver_instances",
                                mBeanServerList.size(), JMX_MBEANSERVER_INDEX_DEFAULT_FOR_MULTIPLE_SERVERS, mBeanServer);
                        // IE: for JBoss we need to verify we are using the correct MBean server of the two (default, null)
                        // Check the domain if it is non-null - avoid using this server
                        int index = 0;
                        for(MBeanServer anMBeanServer : mBeanServerList) {
                            getAbstractSession().log(SessionLog.FINER, SessionLog.SERVER, 
                                    "jmx_mbean_runtime_services_registration_mbeanserver_print",
                                    new Object[]{anMBeanServer, anMBeanServer.getMBeanCount(), anMBeanServer.getDefaultDomain(), index});
                            if(null != anMBeanServer.getDefaultDomain()) {
                                mBeanServer = anMBeanServer;
                                getAbstractSession().log(SessionLog.WARNING, SessionLog.SERVER, 
                                        "jmx_mbean_runtime_services_switching_to_alternate_mbeanserver",
                                        mBeanServer, index);                                                                
                            }
                            index++;
                        }
                    } else {
                        // Only a single MBeanServer instance was found
                        getAbstractSession().log(SessionLog.FINER, SessionLog.SERVER, 
                                "jmx_mbean_runtime_services_registration_mbeanserver_print",
                                new Object[]{mBeanServer, mBeanServer.getMBeanCount(), mBeanServer.getDefaultDomain(), 0});
                    }
                }                
            } catch (Exception e) {
                // TODO: Warning required
                e.printStackTrace();
            }
        }
        return mBeanServer;
    } 
    
    /**
     * INTERNAL: serverSpecificRegisterMBean(): Server specific implementation of the
     * creation and deployment of the JMX MBean to provide runtime services for my
     * databaseSession.
     *
     * Default is to do nothing. This should be subclassed if required.
     * For platform classes that override the JMXEnabledPlatform - the services MBean 
     * is created at platform construction for use during MBean registration here.
     *
     * @return void
     * @see #isRuntimeServicesEnabled()
     * @see #disableRuntimeServices()
     * @see #registerMBean()
     */
    public void serverSpecificRegisterMBean() {        
        // get and cache module and application name during registration
        MBeanServer mBeanServerRuntime = getMBeanServer();      
        ObjectName name = null;      
        String sessionName = getMBeanSessionName();
        if (null != sessionName && (shouldRegisterDevelopmentBean || shouldRegisterRuntimeBean)) {
                // Attempt to register new mBean with the server
                if (null != mBeanServerRuntime && shouldRegisterDevelopmentBean) {
                    try {
                        name = new ObjectName(JMX_REGISTRATION_PREFIX + "Development-" + sessionName + ",Type=Configuration");
                    } catch (MalformedObjectNameException mne) {
                        getAbstractSession().log(SessionLog.WARNING, SessionLog.SERVER, "problem_registering_mbean", mne);
                    } catch (Exception exception) {
                        getAbstractSession().log(SessionLog.WARNING, SessionLog.SERVER, "problem_registering_mbean", exception);
                    }

                    // Currently the to be deprecated development MBean is generic to all server platforms
                    MBeanDevelopmentServices developmentMBean = new MBeanDevelopmentServices(getDatabaseSession());
                    ObjectInstance info = null;
                    Object[] args = new Object[2];
                    args[0] = developmentMBean;
                    args[1] = name;
                    try {
                        Method getMethod = PrivilegedAccessHelper.getPublicMethod(MBeanServer.class, 
                                "registerMBean", new Class[] {Object.class, ObjectName.class}, false);
                        if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()) {
                            info = (ObjectInstance) AccessController.doPrivileged(new PrivilegedMethodInvoker(getMethod, mBeanServerRuntime, args));
                        } else {
                            info = mBeanServerRuntime.registerMBean(developmentMBean, name);
                        }
                    } catch(InstanceAlreadyExistsException iaee) {
                        getAbstractSession().log(SessionLog.WARNING, SessionLog.SERVER, "problem_registering_mbean", iaee);
                    } catch (MBeanRegistrationException registrationProblem) {
                        getAbstractSession().log(SessionLog.WARNING, SessionLog.SERVER, "problem_registering_mbean", registrationProblem);
                    } catch (Exception e) {
                        getAbstractSession().log(SessionLog.WARNING, SessionLog.SERVER, "problem_registering_mbean", e);
                    }
                    getAbstractSession().log(SessionLog.FINEST, SessionLog.SERVER, "registered_mbean", info, mBeanServerRuntime);
                }

                if (null != mBeanServerRuntime && shouldRegisterRuntimeBean) {
                    try {
                        name = new ObjectName(JMX_REGISTRATION_PREFIX + "Session(" + sessionName + ")");                        
                    } catch (MalformedObjectNameException mne) {
                        getAbstractSession().log(SessionLog.WARNING, SessionLog.SERVER, "problem_registering_mbean", mne);
                    } catch (Exception exception) {
                        getAbstractSession().log(SessionLog.WARNING, SessionLog.SERVER, "problem_registering_mbean", exception);
                    }
                    
                    ObjectInstance runtimeInstance = null;
                    try {
                        // The cached runtimeServicesMBean is a server platform specific instance
                        Object[] args = new Object[2];
                        args[0] = runtimeServicesMBean;
                        args[1] = name;
                        Method getMethod = PrivilegedAccessHelper.getPublicMethod(MBeanServer.class, 
                                "registerMBean", new Class[] {Object.class, ObjectName.class}, false);
                        if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()) {
                           runtimeInstance = (ObjectInstance) AccessController.doPrivileged(new PrivilegedMethodInvoker(getMethod, mBeanServerRuntime, args));
                        } else {
                            runtimeInstance = mBeanServerRuntime.registerMBean(runtimeServicesMBean, name);
                        }                        
                        setRuntimeServicesMBean(runtimeServicesMBean);
                    } catch(InstanceAlreadyExistsException iaee) {
                        getAbstractSession().log(SessionLog.WARNING, SessionLog.SERVER, "problem_registering_mbean", iaee);
                    } catch (MBeanRegistrationException registrationProblem) {
                        getAbstractSession().log(SessionLog.WARNING, SessionLog.SERVER, "problem_registering_mbean", registrationProblem);
                    } catch (Exception e) {
                        getAbstractSession().log(SessionLog.WARNING, SessionLog.SERVER, "problem_registering_mbean", e);
                    }
                    getAbstractSession().log(SessionLog.FINEST, SessionLog.SERVER, "registered_mbean", runtimeInstance, mBeanServerRuntime);          
                }
        }
    }
    
    /**
     * INTERNAL: 
     * serverSpecificUnregisterMBean(): Server specific implementation of the
     * de-registration of the JMX MBean from its server during session logout.
     *
     * @return void
     * @see #isRuntimeServicesEnabled()
     * @see #disableRuntimeServices()
     */
    public void serverSpecificUnregisterMBean() {
        MBeanServer mBeanServerRuntime = getMBeanServer();      
        ObjectName name = null;      
        String sessionName = getMBeanSessionName();
        if (null != sessionName && (shouldRegisterDevelopmentBean || shouldRegisterRuntimeBean)) {
            if(null == mBeanServerRuntime) {
                getAbstractSession().log(SessionLog.WARNING, SessionLog.SERVER, "jmx_unable_to_unregister_mbean", name);
            } else {
                try {
                    // Attempt to register new mBean with the server
                    if (shouldRegisterDevelopmentBean) {
                        try {
                            name = new ObjectName(JMX_REGISTRATION_PREFIX + "Development-" + sessionName + ",Type=Configuration");                        
                        } catch (MalformedObjectNameException mne) {
                            getAbstractSession().log(SessionLog.WARNING, SessionLog.SERVER, "problem_unregistering_mbean", mne);
                        } catch (Exception exception) {
                            getAbstractSession().log(SessionLog.WARNING, SessionLog.SERVER, "problem_unregistering_mbean", exception);
                        }

                        getAbstractSession().log(SessionLog.FINEST, SessionLog.SERVER, "unregistering_mbean", name, mBeanServerRuntime);
                        Object[] args = new Object[1];
                        args[0] = name;
                        try {
                            Method getMethod = PrivilegedAccessHelper.getPublicMethod(MBeanServer.class, 
                                    "unregisterMBean", new Class[] {ObjectName.class}, false);
                            if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()) {
                                AccessController.doPrivileged(new PrivilegedMethodInvoker(getMethod, mBeanServerRuntime, args));
                            } else {
                                mBeanServerRuntime.unregisterMBean(name);
                            }                            
                            getAbstractSession().log(SessionLog.FINEST, SessionLog.SERVER, "jmx_unregistered_mbean", name, mBeanServerRuntime);
                        } catch(InstanceNotFoundException inf) {
                            getAbstractSession().log(SessionLog.WARNING, SessionLog.SERVER, "problem_unregistering_mbean", inf);
                        } catch (MBeanRegistrationException mbre) {
                            getAbstractSession().log(SessionLog.WARNING, SessionLog.SERVER, "problem_unregistering_mbean", mbre);                        
                        }                                        
                    }

                    if (shouldRegisterRuntimeBean) {
                        try {                        
                            name = new ObjectName(JMX_REGISTRATION_PREFIX + "Session(" + sessionName + ")");                        
                        } catch (MalformedObjectNameException mne) {
                            getAbstractSession().log(SessionLog.WARNING, SessionLog.SERVER, "problem_unregistering_mbean", mne);
                        } catch (Exception exception) {
                            getAbstractSession().log(SessionLog.WARNING, SessionLog.SERVER, "problem_unregistering_mbean", exception);
                        }
                        
                        getAbstractSession().log(SessionLog.FINEST, SessionLog.SERVER, "unregistering_mbean", name, mBeanServerRuntime);
                        Object[] args = new Object[1];
                        args[0] = name;
                        try {
                            Method getMethod = PrivilegedAccessHelper.getPublicMethod(MBeanServer.class, 
                                    "unregisterMBean", new Class[] {ObjectName.class}, false);
                            if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()) {
                                AccessController.doPrivileged(new PrivilegedMethodInvoker(getMethod, mBeanServerRuntime, args));
                            } else {
                                mBeanServerRuntime.unregisterMBean(name);
                            }                            
                            getAbstractSession().log(SessionLog.FINEST, SessionLog.SERVER, "jmx_unregistered_mbean", name, mBeanServerRuntime);                            
                        } catch(InstanceNotFoundException inf) {
                            getAbstractSession().log(SessionLog.WARNING, SessionLog.SERVER, "problem_unregistering_mbean", inf);
                        } catch (MBeanRegistrationException registrationProblem) {
                            getAbstractSession().log(SessionLog.WARNING, SessionLog.SERVER, "problem_unregistering_mbean", registrationProblem);
                        }                              
                    }
                } catch (Exception exception) {
                    getAbstractSession().log(SessionLog.WARNING, SessionLog.SERVER, "problem_unregistering_mbean", exception);
                } finally {
                    // de reference the mbean
                    this.setRuntimeServicesMBean(null);
                }
            }
        }
    }    
    
    /**
     * Remove JMX reserved characters from the session name
     * @param aSession
     * @return
     */
    protected String getMBeanSessionName() {
        // Check for a valid session - should never occur though        
        if(null != getDatabaseSession() && null != getDatabaseSession().getName()) {
            // remove any JMX reserved characters when the session name is file:/drive:/directory
            return getDatabaseSession().getName().replaceAll("[=,:]", "_");
        } else {
            getAbstractSession().log(SessionLog.WARNING, SessionLog.SERVER, "session_key_for_mbean_name_is_null");
            return null;
        }
    }
    
    /**
     * INTERNAL:
     * Return the cached server specific services MBean 
     */
    protected MBeanRuntimeServicesMBean getRuntimeServicesMBean() {
        return runtimeServicesMBean;
    }

    /**
     * INTERNAL:
     * Set the cached server specific services MBean
     * @param runtimeServicesMBean
     */
    protected void setRuntimeServicesMBean(MBeanRuntimeServicesMBean runtimeServicesMBean) {
        this.runtimeServicesMBean = runtimeServicesMBean;
    }

    /**
     * INTERNAL:
     * @param enableDefault
     * @return
     */
    protected String getModuleName(boolean enableDefault) {
        //Object substring = getModuleOrApplicationName();
        if(null == this.moduleName) {
            // Get property from persistence.xml or sessions.xml
            this.moduleName = (String)getDatabaseSession().getProperty(OVERRIDE_JMX_MODULENAME_PROPERTY);
        }        
        // if there is no function or property override then answer "unknown" as a default for platforms that do not implement getModuleName()
        if(null == this.moduleName && enableDefault) {
            this.moduleName = DEFAULT_SERVER_NAME_AND_VERSION;
        }
        return this.moduleName;
    }
    
    /**
     * INTERNAL: 
     * getModuleName(): Answer the name of the context-root of the application that this session is associated with.
     * Answer "unknown" if there is no module name available.
     * Default behavior is to return "unknown" - we override this behavior here for JBoss.
     * 
     * There are 4 levels of implementation.
     * 1) use the property override jboss.moduleName, or
     * 2) perform a reflective jboss.work.executeThreadRuntime.getModuleName() call, or
     * 3) extract the moduleName:persistence_unit from the server specific classloader string representation, or
     * 3) defer to superclass - usually return "unknown"
     *
     * @return String moduleName
     */
    @Override
    public String getModuleName() {
        return getModuleName(true);
    }
    
    /**
     * INTERNAL;
     * @param aName
     */
    protected void setModuleName(String aName) {
        this.moduleName = aName;
    }
    
    /**
     * INTERNAL:
     * Lazy initialize the application name by
     * first checking for a persistence.xml property override and then
     * deferring to a default name in the absence of a platform override of this function
     * @param enableDefault
     * @return
     */
    protected String getApplicationName(boolean enableDefault) {
        //Object substring = getModuleOrApplicationName();
        if(null == this.applicationName) {
            // Get property from persistence.xml or sessions.xml
            this.applicationName = (String)getDatabaseSession().getProperty(OVERRIDE_JMX_APPLICATIONNAME_PROPERTY);
        }
        // if there is no function or property override then answer "unknown" as a default for platforms that do not implement getApplicationName()
        if(null == this.applicationName && enableDefault) {
            this.applicationName = DEFAULT_SERVER_NAME_AND_VERSION;
        }
        return this.applicationName;
    }
    
    /**
     * INTERNAL: 
     * getApplicationName(): Answer the name of the module (EAR name) that this session is associated with.
     * Answer "unknown" if there is no application name available.
     * Default behavior is to return "unknown" 
     * 
     * There are 4 levels of implementation.
     * 1) use the property override weblogic.applicationName, or
     * 2) perform a reflective weblogic.work.executeThreadRuntime.getApplicationName() call, or
     * 3) extract the moduleName:persistence_unit from the application classloader string representation, or
     * 3) defer to this superclass - usually return "unknown"
     *
     * @return String applicationName
     * @see JMXEnabledPlatform 
     */
    public String getApplicationName() {
        return getApplicationName(true);
    }
    
    /**
     * INTERNAL:
     * @param aName
     */
    public void setApplicationName(String aName) {
        this.applicationName = aName;
    }

    /**
     * INTERNAL:
     * Get the applicationName and moduleName from the application server.
     * This function does not use reflective API on the application server, instead it parses
     * the database session name for the module name, and 
     * the classLoader (from the Platform.conversionManager) toString() for the application name.
     * @return
     */
    protected void initializeApplicationNameAndModuleName() {
        // 333160: Fail Fast: we wrap the entire function in a try/catch - even though no exceptions like IOBE should occur - because this initialization should not stop server predeploy in "any" way
        // The database session name is used to get the module name (no reflection required)
        String databaseSessionName = getMBeanSessionName();
        
        // The classLoader toString() is used to get the application name (no reflection required)
        String classLoaderName = getDatabaseSession().getDatasourcePlatform().getConversionManager().getLoader().toString();
        try {
            getAbstractSession().log(SessionLog.FINEST, SessionLog.SERVER, "jmx_mbean_classloader_in_use", 
                "Platform ConversionManager", classLoaderName);
            // Get property from persistence.xml or sessions.xml
            String jpaModuleName = getModuleName(false);
            String jpaApplicationName = getApplicationName(false);     
        
            // Get the name past the matching string if we have a match
            int startIndex = databaseSessionName.indexOf(
                APP_SERVER_CLASSLOADER_MODULE_EJB_SEARCH_STRING_PREFIX);
            if (jpaModuleName == null && startIndex > -1) {
                String subString = databaseSessionName.substring(startIndex + 
                    APP_SERVER_CLASSLOADER_MODULE_EJB_SEARCH_STRING_PREFIX.length());
                if(null == subString) {
                    startIndex = databaseSessionName.indexOf(
                        APP_SERVER_CLASSLOADER_MODULE_WAR_SEARCH_STRING_PREFIX);
                    if(startIndex > -1) {
                        subString = databaseSessionName.substring(startIndex +  
                                APP_SERVER_CLASSLOADER_MODULE_WAR_SEARCH_STRING_PREFIX.length());
                    }
                }            
                // clear terminating characters like ".jar/"
                if(null != subString) {
                    int endIndex = subString.indexOf(APP_SERVER_CLASSLOADER_MODULE_EJB_WAR_SEARCH_STRING_POSTFIX);
                    if(endIndex > -1) {
                        subString = subString.substring(0, endIndex);
                    }
                }
                setModuleName(subString);
            }

            // Get the application name from the ClassLoader, it is also present in the session name
            startIndex = classLoaderName.indexOf(
                APP_SERVER_CLASSLOADER_APPLICATION_PU_SEARCH_STRING_PREFIX);
            if (jpaApplicationName == null && startIndex > -1) {                
                jpaApplicationName = classLoaderName.substring(startIndex + 
                        APP_SERVER_CLASSLOADER_APPLICATION_PU_SEARCH_STRING_PREFIX.length());
                // Clear terminating delimiting characters like "/}
                if(null != jpaApplicationName) {
                    int endIndex = jpaApplicationName.indexOf(APP_SERVER_CLASSLOADER_APPLICATION_PU_SEARCH_STRING_POSTFIX);
                    if(endIndex > -1) {
                        jpaApplicationName = jpaApplicationName.substring(0, endIndex);
                    }
                }
                // replace encodable characters
                if(null != jpaApplicationName) {
                    jpaApplicationName = jpaApplicationName.replaceAll("[=,:] ", "_");
                }
                setApplicationName(jpaApplicationName);
            }
            // Final check for null values - incorporated into the get functions in these logs (null will be converted to "UNKNOWN")
            getAbstractSession().log(SessionLog.FINEST, SessionLog.SERVER, "mbean_get_application_name", 
                getDatabaseSession().getName(), getApplicationName());
            getAbstractSession().log(SessionLog.FINEST, SessionLog.SERVER, "mbean_get_module_name", 
                getDatabaseSession().getName(), getModuleName());
        } catch (Exception e) {
            // 333160: Fail Fast: we wrap the entire function in a try/catch - even though no exceptions like IOBE should occur - because this initialization should not stop server predeploy in "any" way
            // However, we should never arrive here
            getAbstractSession().log(SessionLog.FINEST, SessionLog.SERVER, "mbean_get_application_name", 
                    classLoaderName, "unavailable");
            getAbstractSession().log(SessionLog.FINEST, SessionLog.SERVER, "mbean_get_module_name", 
                    databaseSessionName, "unavailable");
            e.printStackTrace();
        }
    }    
}
