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
 *     10/20/2008-1.1M4 Michael O'Brien 
 *       - 248748: Add WebLogic 10.3 specific JMX MBean attributes and functions
 *       see <link>http://wiki.eclipse.org/EclipseLink/DesignDocs/248748</link>
 *     11/06/2008-1.1M5 Michael O'Brien 
 *       - 248746: Add getModuleName() implementation and new getApplicationName()
 *     05/07/2009-1.1.1 Dave Brosius 
 *       - 265755: [PATCH] Set application name correctly 
 ******************************************************************************/  
package org.eclipse.persistence.platform.server.wls;

import java.lang.reflect.Method;
import java.security.AccessController;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedMethodInvoker;
import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.services.mbean.MBeanDevelopmentServices;
import org.eclipse.persistence.services.weblogic.MBeanWebLogicRuntimeServices;
import org.eclipse.persistence.sessions.DatabaseSession;

/**
 * PUBLIC:
 *
 * This is the concrete subclass responsible for representing WebLogic 10 specific behavior.
 * This includes WebLogic 10.3 behavior.
 */
public class WebLogic_10_Platform extends WebLogic_9_Platform {
    // see http://e-docs.bea.com/wls/docs90/jmx/accessWLS.html#1119237
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
    private static final String JMX_REGISTRATION_PREFIX = "TopLink:Name=";
    // Secondary override properties can be set to disable MBean registration
    /** This System property "eclipselink.register.dev.mbean" when set to true will enable registration/unregistration of the DevelopmentServices MBean */
    public static final String JMX_REGISTER_DEV_MBEAN_PROPERTY = "eclipselink.register.dev.mbean";
    /** This System property "eclipselink.register.run.mbean" when set to true will enable registration/unregistration of the RuntimeServices MBean */    
    public static final String JMX_REGISTER_RUN_MBEAN_PROPERTY = "eclipselink.register.run.mbean";
    /** This persistence.xml or sessions.xml property is used to override the moduleName */
    public static final String WEBLOGIC_MODULENAME_PROPERTY = "eclipselink.weblogic.moduleName"; 
    /** This persistence.xml or sessions.xml property is used to override the applicationName */
    public static final String WEBLOGIC_APPLICATIONNAME_PROPERTY = "eclipselink.weblogic.applicationName"; 
    // Any value such as true will turn on the MBean
    protected boolean shouldRegisterDevelopmentBean = System.getProperty(JMX_REGISTER_DEV_MBEAN_PROPERTY) != null;
    protected boolean shouldRegisterRuntimeBean = System.getProperty(JMX_REGISTER_RUN_MBEAN_PROPERTY) != null;

    /**
     * The following constants and attributes are used during reflective API calls
     */
    /** Cache the WebLogic MBeanServer for performance */
    private MBeanServer wlsMBeanServer = null;
    /** Cache the WebLogic ThreadPoolRuntime for performance */    
    private ObjectName wlsThreadPoolRuntime = null;
    /** The JMX context when running as a module */
    private static final String WLS_MODULE_ENV_CONTEXT_LOOKUP = "java:comp/env/jmx/runtime";
    /** The JMX context when running as a non-module */
    private static final String WLS_NON_MODULE_CONTEXT_LOOKUP = "java:comp/jmx/runtime";
    
    private static final String WLS_SERVICE_KEY = "com.bea:Name=RuntimeService,Type=weblogic.management.mbeanservers.runtime.RuntimeServiceMBean";    
    private static final String WLS_SERVER_RUNTIME = "ServerRuntime";    
    private static final String WLS_THREADPOOL_RUNTIME = "ThreadPoolRuntime";
    private static final String WLS_EXECUTE_THREAD_GET_METHOD_NAME = "getExecuteThread";
    // see http://home.bea.com/internal/docs/wiki/p/view/jee/appinfothread
    private static final String WLS_APPLICATION_NAME_GET_METHOD_NAME = "getApplicationName";
    private static final String WLS_MODULE_NAME_GET_METHOD_NAME = "getModuleName";    
    /** Search String in WebLogic ClassLoader for the application:persistence_unit name */
    private static final String WLS_CLASSLOADER_APPLICATION_PU_SEARCH_STRING_PREFIX = "annotation: ";
    
    /** moduleName determination is available during MBean registration only */
    private String moduleName = null;

    /** applicationName determination is available during MBean registration only */
    private String applicationName = null;
    
    /** cache the RuntimeServices MBean */
    private MBeanWebLogicRuntimeServices runtimeServicesMBean = null;    
    /**
     * INTERNAL:
     * Default Constructor: All behavior for the default constructor is inherited
     */
    public WebLogic_10_Platform(DatabaseSession newDatabaseSession) {
        super(newDatabaseSession);
    }

    /**
     * INTERNAL: 
     * serverSpecificRegisterMBean(): Server specific implementation of the
     * creation and deployment of the JMX MBean to provide runtime services for my
     * databaseSession.
     *
     * @return void
     * @see #isRuntimeServicesEnabled()
     * @see #disableRuntimeServices()
     * @see #registerMBean()
     */
    public void serverSpecificRegisterMBean() {
        // get and cache module and application name during registration
        initializeApplicationNameAndModuleName();
        MBeanServer mBeanServerRuntime = null;      
        ObjectName name = null;      
        String sessionName = getMBeanSessionName();
        Context initialContext = null;        
        if (null != sessionName && (shouldRegisterDevelopmentBean || shouldRegisterRuntimeBean)) {
            try {
                initialContext = new InitialContext();
                mBeanServerRuntime = (MBeanServer) initialContext.lookup(JMX_JNDI_RUNTIME_REGISTER);
                // Attempt to register new mBean with the server
                if (shouldRegisterDevelopmentBean) {
                    try {
                        name = new ObjectName(JMX_REGISTRATION_PREFIX + "Development-" + sessionName + ",Type=Configuration");
                    } catch (MalformedObjectNameException mne) {
                        AbstractSessionLog.getLog().log(SessionLog.WARNING, "problem_registering_mbean", mne);
                    } catch (Exception exception) {
                        AbstractSessionLog.getLog().log(SessionLog.WARNING, "problem_registering_mbean", exception);
                    }

                    MBeanDevelopmentServices mbean = new MBeanDevelopmentServices(getDatabaseSession());
                    ObjectInstance info = null;
                    try {
                        info = mBeanServerRuntime.registerMBean(mbean, name);
                    } catch(InstanceAlreadyExistsException iaee) {
                        AbstractSessionLog.getLog().log(SessionLog.WARNING, "problem_registering_mbean", iaee);
                    } catch (MBeanRegistrationException registrationProblem) {
                        AbstractSessionLog.getLog().log(SessionLog.WARNING, "problem_registering_mbean", registrationProblem);
                    } catch (Exception e) {
                        AbstractSessionLog.getLog().log(SessionLog.WARNING, "problem_registering_mbean", e);
                    }
                    AbstractSessionLog.getLog().log(SessionLog.FINEST, "registered_mbean", info);
                }

                if (shouldRegisterRuntimeBean) {
                    try {
                        name = new ObjectName(JMX_REGISTRATION_PREFIX + "Session(" + sessionName + ")");                        
                    } catch (MalformedObjectNameException mne) {
                        AbstractSessionLog.getLog().log(SessionLog.WARNING, "problem_registering_mbean", mne);
                    } catch (Exception exception) {
                        AbstractSessionLog.getLog().log(SessionLog.WARNING, "problem_registering_mbean", exception);
                    }
                    
                    MBeanWebLogicRuntimeServices runtimeServices = new MBeanWebLogicRuntimeServices(getDatabaseSession());                    
                    ObjectInstance runtimeInstance = null;
                    try {
                        runtimeInstance = mBeanServerRuntime.registerMBean(runtimeServices, name);
                        runtimeServicesMBean = runtimeServices;
                    } catch(InstanceAlreadyExistsException iaee) {
                        AbstractSessionLog.getLog().log(SessionLog.WARNING, "problem_registering_mbean", iaee);
                    } catch (MBeanRegistrationException registrationProblem) {
                        AbstractSessionLog.getLog().log(SessionLog.WARNING, "problem_registering_mbean", registrationProblem);
                    } catch (Exception e) {
                        AbstractSessionLog.getLog().log(SessionLog.WARNING, "problem_registering_mbean", e);
                    }
                    AbstractSessionLog.getLog().log(SessionLog.FINEST, "registered_mbean", runtimeInstance);          
                }
            } catch (NamingException ne) {
                AbstractSessionLog.getLog().log(SessionLog.WARNING, "failed_to_find_mbean_server", ne);
            } catch (Exception exception) {
                AbstractSessionLog.getLog().log(SessionLog.WARNING, "problem_registering_mbean", exception);
            } finally {
                // close the context
                // see http://forums.bea.com/thread.jspa?threadID=600004445
                // see http://e-docs.bea.com/wls/docs81/jndi/jndi.html#471919
                // see http://e-docs.bea.com/wls/docs100/jndi/jndi.html#wp467275
                try {
                    mBeanServerRuntime = null;
                    if(null != initialContext) {
                        initialContext.close();
                    }
                } catch (NamingException ne) {
                    // exceptions on context close will be ignored, the context will be GC'd                   
                }
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
        MBeanServer mBeanServerRuntime = null;      
        ObjectName name = null;      
        String sessionName = getMBeanSessionName();
        Context initialContext = null;        
        if (null != sessionName && (shouldRegisterDevelopmentBean || shouldRegisterRuntimeBean)) {
            try {
                initialContext = new InitialContext();
                mBeanServerRuntime = (MBeanServer) initialContext.lookup(JMX_JNDI_RUNTIME_UNREGISTER);
                // Attempt to register new mBean with the server
                if (shouldRegisterDevelopmentBean) {
                    try {
                        name = new ObjectName(JMX_REGISTRATION_PREFIX + "Development_" + sessionName + ",Type=Configuration");
                    } catch (MalformedObjectNameException mne) {
                        AbstractSessionLog.getLog().log(SessionLog.WARNING, "problem_unregistering_mbean", mne);
                    } catch (Exception exception) {
                        AbstractSessionLog.getLog().log(SessionLog.WARNING, "problem_unregistering_mbean", exception);
                    }

                    AbstractSessionLog.getLog().log(SessionLog.FINEST, "unregistering_mbean", name);
                    try {
                        mBeanServerRuntime.unregisterMBean(name);
                    } catch(InstanceNotFoundException inf) {
                        AbstractSessionLog.getLog().log(SessionLog.WARNING, "problem_unregistering_mbean", inf);
                    } catch (MBeanRegistrationException mbre) {
                        AbstractSessionLog.getLog().log(SessionLog.WARNING, "problem_unregistering_mbean", mbre);                        
                    }                                        
                }

                if (shouldRegisterRuntimeBean) {
                    try {                        
                        name = new ObjectName(JMX_REGISTRATION_PREFIX + "Session(" + sessionName + ")");                        
                    } catch (MalformedObjectNameException mne) {
                        AbstractSessionLog.getLog().log(SessionLog.WARNING, "problem_unregistering_mbean", mne);
                    } catch (Exception exception) {
                        AbstractSessionLog.getLog().log(SessionLog.WARNING, "problem_unregistering_mbean", exception);
                    }

                    AbstractSessionLog.getLog().log(SessionLog.FINEST, "unregistering_mbean", name);
                    try {
                        mBeanServerRuntime.unregisterMBean(name);
                    } catch(InstanceNotFoundException inf) {
                        AbstractSessionLog.getLog().log(SessionLog.WARNING, "problem_unregistering_mbean", inf);
                    } catch (MBeanRegistrationException registrationProblem) {
                        AbstractSessionLog.getLog().log(SessionLog.WARNING, "problem_unregistering_mbean", registrationProblem);
                    }                              
                }
            } catch (NamingException ne) {
                AbstractSessionLog.getLog().log(SessionLog.WARNING, "failed_to_find_mbean_server", ne);
            } catch (Exception exception) {
                // Trap a possible WebLogic specific [weblogic.management.NoAccessRuntimeException]
                AbstractSessionLog.getLog().log(SessionLog.WARNING, "problem_unregistering_mbean", exception);
            } finally {
                // de reference the mbean
                runtimeServicesMBean = null;
                // close the context
                // see http://forums.bea.com/thread.jspa?threadID=600004445
                // see http://e-docs.bea.com/wls/docs81/jndi/jndi.html#471919
                // see http://e-docs.bea.com/wls/docs100/jndi/jndi.html#wp467275
                try {
                    mBeanServerRuntime = null;
                    if(null != initialContext) {
                        initialContext.close();
                    }
                } catch (NamingException ne) {
                    // exceptions on context close will be ignored, the context will be GC'd
                }
            }
        }
    }

    /**
     * INTERNAL: 
     * getModuleName(): Answer the name of the context-root of the application that this session is associated with.
     * Answer "unknown" if there is no module name available.
     * Default behavior is to return "unknown" - we override this behavior here for WebLogic.
     * 
     * There are 4 levels of implementation.
     * 1) use the property override weblogic.moduleName, or
     * 2) perform a reflective weblogic.work.executeThreadRuntime.getModuleName() call (build 10.3+), or
     * 3) extract the moduleName:persistence_unit from the weblogic classloader string representation (build 10.3), or
     * 3) defer to superclass - usually return "unknown"
     *
     * @return String moduleName
     */
    public String getModuleName() {        
        return this.moduleName;
    }
    
    /**
     * INTERNAL: 
     * getApplicationName(): Answer the name of the module (EAR name) that this session is associated with.
     * Answer "unknown" if there is no application name available.
     * Default behavior is to return "unknown" - we override this behavior here for WebLogic.
     * 
     * There are 4 levels of implementation.
     * 1) use the property override weblogic.applicationName, or
     * 2) perform a reflective weblogic.work.executeThreadRuntime.getApplicationName() call (build 10.3+), or
     * 3) extract the moduleName:persistence_unit from the weblogic classloader string representation (build 10.3), or
     * 3) defer to superclass - usually return "unknown"
     *
     * @return String applicationName
     */
    public String getApplicationName() {
        return this.applicationName;
    }
    
    /**
     * INTERNAL:
     * Get the applicationName and moduleName from the runtime WebLogic MBean reflectively
     * @return
     */
    private void initializeApplicationNameAndModuleName() {
        // Get property from persistence.xml or sessions.xml
        String jpaModuleName = (String)getDatabaseSession().getProperty(WEBLOGIC_MODULENAME_PROPERTY);
        String jpaApplicationName = (String)getDatabaseSession().getProperty(WEBLOGIC_APPLICATIONNAME_PROPERTY);      
        
        if (jpaModuleName != null) {
            this.moduleName = jpaModuleName;
        } else {
        	jpaModuleName = getNameFromWeblogic(WLS_MODULE_NAME_GET_METHOD_NAME);
            
            // If we are running a version of WebLogic 10.3 that does not support ExecuteThreadRuntime (from 10.3+) then use the ClassLoader                    
            if(null != jpaModuleName && jpaModuleName.indexOf("@") != -1) {
                this.moduleName = jpaModuleName.substring(jpaModuleName.indexOf("@") + 1);
            } else {
                this.moduleName = jpaModuleName;
            }
        }

        if (jpaApplicationName != null) {
            this.applicationName = jpaApplicationName;
        } else {
        	jpaApplicationName = getNameFromWeblogic(WLS_APPLICATION_NAME_GET_METHOD_NAME);

            // defer to the superclass implementation            
            if(null == jpaApplicationName) {
            	jpaApplicationName = super.getApplicationName();
             }
            
            // If we are running a version of WebLogic 10.3 that does not support ExecuteThreadRuntime (from 10.3+) then use the ClassLoader                    
            if(null != jpaApplicationName && jpaApplicationName.indexOf("@") > -1) {
                this.applicationName = jpaApplicationName.substring(jpaApplicationName.indexOf("@") + 1);
            } else {
                this.applicationName = jpaApplicationName;                
            }            
        }
        
        // Final check for null values
        if(null == this.applicationName) {
            this.applicationName = DEFAULT_SERVER_NAME_AND_VERSION;
        }
        if(null == this.moduleName) {
            this.moduleName = DEFAULT_SERVER_NAME_AND_VERSION;
        }
        AbstractSessionLog.getLog().log(SessionLog.FINEST, "mbean_get_application_name", 
                getDatabaseSession().getName(), this.applicationName);
        AbstractSessionLog.getLog().log(SessionLog.FINEST, "mbean_get_module_name", 
                getDatabaseSession().getName(), this.moduleName);
    }

    /**
     * INTERNAL:
     * This method will return the application|module name for WebLogic.
     * If the call to executeThread on the MBean fails - return the current classloader
     * Thread.currentThread().getContextClassLoader() 
     * 
     * ER 248746: Use reflection to obtain the application name (EJB, Web or MDB module)
     * Get either a String containing the module/applicationName or a WebLogic classLoader that contains the module/applicationName in the format...
     * weblogic.utils.classloaders.ChangeAwareClassLoader@19bb43f finder: weblogic.utils.classloaders.CodeGenClassFinder@ab7c2e annotation: org.eclipse.persistence.example.jpa.server.weblogic.enterpriseEAR@enterprise
     * If the getExecuteThread call failed, use the classloader string representation as backup.
     * If the classloader is not in the correct format, defer to superclass.
	 *
     * @return String module|application Name from WLS
     */
    private String getNameFromWeblogic(String getMethodName) {
        Object classLoaderOrString = null;//this.getDatabaseSession().getPlatform().getConversionManager().getLoader();
        Object executeThread = getExecuteThreadFromMBean();
        
        if (executeThread != null) {
            try {
                // perform a reflective public java.lang.String
                // weblogic.work.ExecuteThreadRuntime.<getMethodName>
                Method getMethod = PrivilegedAccessHelper.getPublicMethod(executeThread.getClass(), getMethodName, new Class[] {}, false);
                if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()) {
                    AccessController.doPrivileged(new PrivilegedMethodInvoker(getMethod, executeThread, (Object[]) null));                    
                } else {
                    classLoaderOrString = PrivilegedAccessHelper.invokeMethod(getMethod, executeThread);
                }
                
                if(classLoaderOrString instanceof ClassLoader) {
                    // If we are running a version of WebLogic 10.3 that does not support ExecuteThreadRuntime (from 10.3+) then use the ClassLoader                    
                    String jpaModuleNameRoot = ((ClassLoader)classLoaderOrString).toString();
                    classLoaderOrString = jpaModuleNameRoot.substring(jpaModuleNameRoot.indexOf(
                            WLS_CLASSLOADER_APPLICATION_PU_SEARCH_STRING_PREFIX) + 
                            WLS_CLASSLOADER_APPLICATION_PU_SEARCH_STRING_PREFIX.length());
                }        
            } catch (Exception ex) { // catch all Illegal*Exception and PrivilegedActionException
                /*
                 * If the reflective call to ExecuteThreadRuntime failed for
                 * this an older version of WebLogic 10.3 failed, use the
                 * classloader as a backup method
                 */
                AbstractSessionLog.getLog().log(SessionLog.WARNING,  "problem_with_reflective_weblogic_call_mbean", ex, getMethodName);
            }
        }
        return (String)classLoaderOrString;
    }

    /**
     * INTERNAL:
     * This convenience method will look up a WebLogic execute thread from the runtime 
     * MBean tree.  The execute thread contains application information.  This code 
     * will use the name of the current thread to lookup the corresponding ExecuteThread.
     * The ExecuteThread will allow us to obtain the application name (and version, etc).
     * 
     * Note that the MBeanServer and ThreadPoolRuntime instances will be cached for 
     * performance.
     * 
     * @return application name or null if the name cannot be obtained
     */
    private Object getExecuteThreadFromMBean() {
        // Lazy load the MBeanServer instance
        if (wlsMBeanServer == null) {
            Context initialContext = null;
            try {
                initialContext = new InitialContext();
                try {
                    // The lookup string used depends on the context from which this class is being accessed, i.e. servlet, EJB, etc.
                    // In this case we do not know at runtime whether we are running as a module or not - so we try both lookups
                    // Try java:comp/env lookup  - normally used when the app is a module
                    wlsMBeanServer = (MBeanServer) initialContext.lookup(WLS_MODULE_ENV_CONTEXT_LOOKUP);
                } catch (NamingException e) {
                    // Lookup failed - try java:comp - this is the case when the application is not a module
                    try {
                        wlsMBeanServer = (MBeanServer) initialContext.lookup(WLS_NON_MODULE_CONTEXT_LOOKUP);
                    } catch (NamingException ne) {
                        /*
                         * If the MBeanServer lookup failed, continue and use the classloader as a backup method
                         */
                        AbstractSessionLog.getLog().log(SessionLog.WARNING, "jmx_mbean_runtime_services_mbeanserver_lookup_failed", ne);
                    }
                }
                
                
            } catch (NamingException nex) {
                AbstractSessionLog.getLog().log(SessionLog.WARNING, "problem_unregistering_mbean", nex);
            } finally {
                // close the context
                try {
                    if(null != initialContext) {
                        initialContext.close();
                    }
                } catch (NamingException ne) {
                    // exceptions on context close will be ignored, the context will be garbage collected
                }
            }
        }
        // Now that we know that the MBeanServer has been initialized - we use it
        // Initialize the threadPoolRuntime and get the executeThreadRuntime
        //this.getDatabaseSession().getPlatform().getConversionManager().getLoader();
        if (wlsMBeanServer != null) {
            // Lazy load the ThreadPoolRuntime instance
            if (wlsThreadPoolRuntime == null) {
                try {
                    ObjectName service = new ObjectName(WLS_SERVICE_KEY);
                    ObjectName serverRuntime = (ObjectName) wlsMBeanServer.getAttribute(service, WLS_SERVER_RUNTIME);
                    wlsThreadPoolRuntime = (ObjectName) wlsMBeanServer.getAttribute(serverRuntime, WLS_THREADPOOL_RUNTIME);
                } catch (Exception ex) {
                    AbstractSessionLog.getLog().log(SessionLog.WARNING, "jmx_mbean_runtime_services_threadpool_initialize_failed", ex);                                        
                }
            }
            // Get the executeThreadRuntimeObject
            if (wlsThreadPoolRuntime != null) {
                try {
                    // Perform a reflective getExecuteThread()
                    return wlsMBeanServer.invoke(wlsThreadPoolRuntime, 
                            WLS_EXECUTE_THREAD_GET_METHOD_NAME, 
                            new Object[] { Thread.currentThread().getName() }, new String[] { String.class.getName() });
                } catch (Exception ex) {
                    /*
                     * If the reflective call to get the executeThreadRuntime object failed on the MBean because
                     * this an older version of WebLogic 10.3, continue and use the classloader as a backup method
                     */
                    AbstractSessionLog.getLog().log(SessionLog.WARNING,
                            "jmx_mbean_runtime_services_get_executethreadruntime_object_failed", ex);
                }
            }
        }
        return null;
    }
    
    /**
     * Remove JMX reserved characters from the session name
     * @param aSession
     * @return
     */
    private String getMBeanSessionName() {
        // Check for a valid session - should never occur though        
        if(null != getDatabaseSession() && null != getDatabaseSession().getName()) {
            // remove any JMX reserved characters when the session name is file:/drive:/directory
            return getDatabaseSession().getName().replaceAll("[=,:]", "_");
        } else {
            AbstractSessionLog.getLog().log(SessionLog.WARNING, "session_key_for_mbean_name_is_null");
            return null;
        }
    }
    
    /**
     * Return the method for the WebLogic JDBC connection wrapper vendorConnection.
     * WLS 10.3.4.0 added a getVendorConnectionSafe that does not invalidate the connection,
     * so use this if available.
     */
    protected Method getVendorConnectionMethod() {
        if ((this.vendorConnectionMethod == null) && (!getWebLogicConnectionClass().equals(void.class))) {
            try {
                this.vendorConnectionMethod = PrivilegedAccessHelper.getDeclaredMethod(getWebLogicConnectionClass(), "getVendorConnectionSafe", new Class[0]);
            } catch (NoSuchMethodException not1034) {
                try {
                    this.vendorConnectionMethod = PrivilegedAccessHelper.getDeclaredMethod(getWebLogicConnectionClass(), "getVendorConnection", new Class[0]);
                } catch (NoSuchMethodException exception) {
                    getDatabaseSession().getSessionLog().logThrowable(SessionLog.WARNING, exception);
                }
            }
        }

        return this.vendorConnectionMethod;
    }
    
}
