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
* dmccann - June 4/2008 - 1.0M8 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.sdo.helper.delegates;

import java.lang.reflect.Method;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.sdo.SDOConstants;

import commonj.sdo.helper.HelperContext;
import commonj.sdo.impl.HelperProvider;

/**
 * Abstract class for SDO helper delegators.  Delegates will shared on an application
 * basis.  This class will return the key to be used to store/retrieve the delegates
 * for a given application. 
 */
public abstract class AbstractHelperDelegator {
    // Hold the context containing all helpers so that we can preserve inter-helper relationships
    protected HelperContext aHelperContext;
    
    private static String OC4J_CLASSLOADER_NAME = "oracle";
    private static String WLS_CLASSLOADER_NAME = "weblogic";
    
    // For WebLogic
    private MBeanServer wlsMBeanServer = null;
    private ObjectName wlsThreadPoolRuntime = null;
    private static final String WLS_ENV_CONTEXT_LOOKUP = "java:comp/env/jmx/runtime";
    private static final String WLS_CONTEXT_LOOKUP = "java:comp/jmx/runtime";
    private static final String WLS_SERVICE_KEY = "com.bea:Name=RuntimeService,Type=weblogic.management.mbeanservers.runtime.RuntimeServiceMBean";    
    private static final String WLS_SERVER_RUNTIME = "ServerRuntime";    
    private static final String WLS_THREADPOOL_RUNTIME = "ThreadPoolRuntime";
    private static final String WLS_EXECUTE_THREAD_GET_METHOD_NAME = "getExecuteThread";
    private static final String WLS_APPLICATION_NAME_GET_METHOD_NAME = "getApplicationName";
    private static final Class[] PARAMETER_TYPES = {};
    
    
    /**
     * INTERNAL:
     * This method will return the key to be used to store/retrieve the delegates
     * for a given application.
     * 
     * OC4J classLoader levels: 
     *      0 - APP.web (servlet/jsp) or APP.wrapper (ejb)
     *      1 - APP.root (parent for helperContext)
     *      2 - default.root
     *      3 - system.root
     *      4 - oc4j.10.1.3 (remote EJB) or org.eclipse.persistence:11.1.1.0.0
     *      5 - api:1.4.0
     *      6 - jre.extension:0.0.0
     *      7 - jre.bootstrap:1.5.0_07 (with various J2SE versions)
     * 
     * @return Application classloader for OC4J, application name for WebLogic, 
     *         otherwise Thread.currentThread().getContextClassLoader()
     */
    protected Object getDelegateMapKey() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String classLoaderName = classLoader.getClass().getName();

        // Default to Thread.currentThread().getContextClassLoader()
        Object delegateKey = classLoader;

        // Delegates in OC4J server will be keyed on classloader  
        if (classLoaderName.startsWith(OC4J_CLASSLOADER_NAME)) {
            // Check to see if we are running in a Servlet container or a local EJB container
            if ((classLoader.getParent() != null) //
                    && ((classLoader.toString().indexOf(SDOConstants.CLASSLOADER_WEB_FRAGMENT) != -1) //
                    ||  (classLoader.toString().indexOf(SDOConstants.CLASSLOADER_EJB_FRAGMENT) != -1))) {
                classLoader = classLoader.getParent();
            }
            delegateKey = classLoader;
        // Delegates in WebLogic server will be keyed on application name
        } else if (classLoaderName.contains(WLS_CLASSLOADER_NAME)) {
            Object executeThread = getExecuteThread();
            if (executeThread != null) {
                try {
                    Method getMethod = PrivilegedAccessHelper.getPublicMethod(executeThread.getClass(), WLS_APPLICATION_NAME_GET_METHOD_NAME, PARAMETER_TYPES, false);
                    delegateKey = PrivilegedAccessHelper.invokeMethod(getMethod, executeThread);
                } catch (Exception e) {}
            }
        }        
        return delegateKey;
    }
    
    /**
     * Return the helperContext that this instance is associated with.
     * This context contains all helpers. If null, the default context
     * is returned. 
     * 
     * @return set helper context or, if null, the default context
     * @see commonj.sdo.helper.HelperContext
     * @see commonj.sdo.impl.HelperProvider
     */
    public HelperContext getHelperContext() {
        if (null == aHelperContext) {
            aHelperContext = HelperProvider.getDefaultContext();
        }
        return aHelperContext;
    }

    /**
     * INTERNAL:
     * Set the helperContext that this instance is associated with.  This context 
     * will contain all helpers, so inter-helper relationships are preserved.
     *
     * @param helperContext
     * @see commonj.sdo.helper.HelperContext
     */
    public void setHelperContext(HelperContext helperContext) {
        aHelperContext = helperContext;
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
    private Object getExecuteThread() {
        // Lazy load the MBeanServer instance
        if (wlsMBeanServer == null) {
            Context weblogicContext = null;
            try {
                weblogicContext = new InitialContext();
                try {
                    // The lookup string used depends on the context from which this class is being 
                    // accessed, i.e. servlet, EJB, etc.
                    // Try java:comp/env lookup
                    wlsMBeanServer = (MBeanServer) weblogicContext.lookup(WLS_ENV_CONTEXT_LOOKUP);
                } catch (NamingException e) {
                    // Lookup failed - try java:comp
                    try {
                        wlsMBeanServer = (MBeanServer) weblogicContext.lookup(WLS_CONTEXT_LOOKUP);
                    } catch (NamingException ne) {}
                }
            } catch (NamingException nex) {}
        }
        
        if (wlsMBeanServer != null) {
            // Lazy load the ThreadPoolRuntime instance
            if (wlsThreadPoolRuntime == null) {
                try {
                    ObjectName service = new ObjectName(WLS_SERVICE_KEY);
                    ObjectName serverRuntime = (ObjectName) wlsMBeanServer.getAttribute(service, WLS_SERVER_RUNTIME);
                    wlsThreadPoolRuntime = (ObjectName) wlsMBeanServer.getAttribute(serverRuntime, WLS_THREADPOOL_RUNTIME);
                } catch (Exception x) {}
            }
            if (wlsThreadPoolRuntime != null) {
                try {
                    return wlsMBeanServer.invoke(wlsThreadPoolRuntime, WLS_EXECUTE_THREAD_GET_METHOD_NAME, new Object[] { Thread.currentThread().getName() }, new String[] { String.class.getName() });
                } catch (Exception e) {}
            }
        }
        return null;
    }
}