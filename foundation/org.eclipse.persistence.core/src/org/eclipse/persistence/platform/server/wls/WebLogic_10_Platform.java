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
 *     06/30/2010-2.1.1 Michael O'Brien 
 *       - 316513: Enable JMX MBean functionality for JBoss, Glassfish and WebSphere in addition to WebLogic
 *       Move JMX MBean generic registration code up from specific platforms
 *       see <link>http://wiki.eclipse.org/EclipseLink/DesignDocs/316513</link>        
 ******************************************************************************/  
package org.eclipse.persistence.platform.server.wls;

import java.lang.reflect.Method;
import java.security.AccessController;

import javax.management.ObjectName;

import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedMethodInvoker;
import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.platform.server.JMXEnabledPlatform;
import org.eclipse.persistence.services.weblogic.MBeanWebLogicRuntimeServices;
import org.eclipse.persistence.sessions.DatabaseSession;

/**
 * PUBLIC:
 *
 * This is the concrete subclass responsible for representing WebLogic 10 specific behavior.
 * This includes WebLogic 10.3 behavior.
 */
public class WebLogic_10_Platform extends WebLogic_9_Platform implements JMXEnabledPlatform {
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
    /** This persistence.xml or sessions.xml property is used to override the moduleName */
    protected static final String SERVER_SPECIFIC_MODULENAME_PROPERTY = "eclipselink.weblogic.moduleName"; 
    /** This persistence.xml or sessions.xml property is used to override the applicationName */
    protected static final String SERVER_SPECIFIC_APPLICATIONNAME_PROPERTY = "eclipselink.weblogic.applicationName"; 

    /**
     * The following constants and attributes are used during reflective API calls
     */
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
    
    /**
     * INTERNAL:
     * Default Constructor: All behavior for the default constructor is inherited
     */
    public WebLogic_10_Platform(DatabaseSession newDatabaseSession) {
        super(newDatabaseSession);
        this.enableRuntimeServices();
        // Create the JMX MBean specific to this platform for later registration
        this.prepareServerSpecificServicesMBean();
    }

    @Override
    public boolean isRuntimeServicesEnabledDefault() {
        return true;
    }
    
    /**
     * INTERNAL: 
     * prepareServerSpecificServicesMBean(): Server specific implementation of the
     * creation and deployment of the JMX MBean to provide runtime services for the
     * databaseSession.
     *
     * Default is to do nothing.
     * Implementing platform classes must override this function and supply
     * the server specific MBean instance for later registration by calling it in the constructor.  
     *
     * @return void
     * @see #isRuntimeServicesEnabled()
     * @see #disableRuntimeServices()
     * @see #registerMBean()
     */
    @Override
    public void prepareServerSpecificServicesMBean() {
        // No check for an existing cached MBean - we will replace it if it exists
        if(shouldRegisterRuntimeBean) {
            this.setRuntimeServicesMBean(new MBeanWebLogicRuntimeServices(getDatabaseSession()));
        }
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
    @Override
    public void serverSpecificRegisterMBean() {
       super.serverSpecificRegisterMBean();
        // get and cache module and application name during registration
        initializeApplicationNameAndModuleName();
    }
    
    /**
     * INTERNAL:
     * Get the applicationName and moduleName from the runtime WebLogic MBean reflectively
     * @return
     */
    private void initializeApplicationNameAndModuleName() {
        // Get property from persistence.xml or sessions.xml
        String jpaModuleName = (String)getDatabaseSession().getProperty(SERVER_SPECIFIC_MODULENAME_PROPERTY);
        String jpaApplicationName = (String)getDatabaseSession().getProperty(SERVER_SPECIFIC_APPLICATIONNAME_PROPERTY);      
        
        if (jpaModuleName != null) {
            setModuleName(jpaModuleName);
        } else {
            jpaModuleName = getModuleOrApplicationName(WLS_MODULE_NAME_GET_METHOD_NAME);
            
            // If we are running a version of WebLogic 10.3 that does not support ExecuteThreadRuntime (from 10.3+) then use the ClassLoader                    
            if(null != jpaModuleName && jpaModuleName.indexOf("@") != -1) {
                setModuleName(jpaModuleName.substring(jpaModuleName.indexOf("@") + 1));
            } else {
                setModuleName(jpaModuleName);
            }
        }

        if (jpaApplicationName != null) {
            setApplicationName(jpaApplicationName);
        } else {
            jpaApplicationName = getModuleOrApplicationName(WLS_APPLICATION_NAME_GET_METHOD_NAME);

            // defer to the superclass implementation            
            if(null == jpaApplicationName) {
                jpaApplicationName = super.getApplicationName();
             }
            
            // If we are running a version of WebLogic 10.3 that does not support ExecuteThreadRuntime (from 10.3+) then use the ClassLoader                    
            if(null != jpaApplicationName && jpaApplicationName.indexOf("@") > -1) {
                setApplicationName(jpaApplicationName.substring(jpaApplicationName.indexOf("@") + 1));
            } else {
                setApplicationName(jpaApplicationName);                
            }            
        }
        
        // TODO: remove: Final check for null values
        if(null == getApplicationName()) {
            setApplicationName(DEFAULT_SERVER_NAME_AND_VERSION);
        }
        if(null == getModuleName()) {
            setModuleName(DEFAULT_SERVER_NAME_AND_VERSION);
        }
        AbstractSessionLog.getLog().log(SessionLog.FINEST, "mbean_get_application_name", 
                getDatabaseSession().getName(), getApplicationName());
        AbstractSessionLog.getLog().log(SessionLog.FINEST, "mbean_get_module_name", 
                getDatabaseSession().getName(), getModuleName());
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
    private String getModuleOrApplicationName(String getMethodName) {
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
        // Initialize the threadPoolRuntime and get the executeThreadRuntime
        //this.getDatabaseSession().getPlatform().getConversionManager().getLoader();
        if (getMBeanServer() != null) {
            // Lazy load the ThreadPoolRuntime instance
            if (wlsThreadPoolRuntime == null) {
                try {
                    ObjectName service = new ObjectName(WLS_SERVICE_KEY);
                    ObjectName serverRuntime = (ObjectName) getMBeanServer().getAttribute(service, WLS_SERVER_RUNTIME);
                    wlsThreadPoolRuntime = (ObjectName) getMBeanServer().getAttribute(serverRuntime, WLS_THREADPOOL_RUNTIME);
                } catch (Exception ex) {
                    AbstractSessionLog.getLog().log(SessionLog.WARNING, "jmx_mbean_runtime_services_threadpool_initialize_failed", ex);                                        
                }
            }
            // Get the executeThreadRuntimeObject
            if (wlsThreadPoolRuntime != null) {
                try {
                    // Perform a reflective getExecuteThread()
                    return getMBeanServer().invoke(wlsThreadPoolRuntime, 
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
