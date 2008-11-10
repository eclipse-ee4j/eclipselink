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
 *     Oracle  - initial API and implementation from Oracle TopLink
 *     dmccann - Nov. 7/2008 - Added delegate key logic from AbstractHelperDelegator
 ******************************************************************************/  

/**
 * INTERNAL:
 * <b>Purpose:</b>
 * <ul><li>This class is a reference implementation of an instantiable {@link Sequence commonj.sdo.HelperContext}.</li>
 * </ul>
 * <b>Responsibilities:</b>
 * <ul>
 * <li>Provide access to a instances of helper objects.</li>
 * </ul>
 * <p/>
 * Use this class in place of the default HelperProvider.DefaultHelperContext implementation.
 * <br/>
 *
 * @since Oracle TopLink 11.1.1.0.0
 */
package org.eclipse.persistence.sdo.helper;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.WeakHashMap;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOResolvable;
import org.eclipse.persistence.sdo.helper.delegates.SDODataFactoryDelegate;
import org.eclipse.persistence.sdo.helper.delegates.SDOTypeHelperDelegate;
import org.eclipse.persistence.sdo.helper.delegates.SDOXMLHelperDelegate;
import org.eclipse.persistence.sdo.helper.delegates.SDOXSDHelperDelegate;
import commonj.sdo.helper.CopyHelper;
import commonj.sdo.helper.DataFactory;
import commonj.sdo.helper.DataHelper;
import commonj.sdo.helper.EqualityHelper;
import commonj.sdo.helper.HelperContext;
import commonj.sdo.helper.TypeHelper;
import commonj.sdo.helper.XMLHelper;
import commonj.sdo.helper.XSDHelper;
import commonj.sdo.impl.ExternalizableDelegator;

public class SDOHelperContext implements HelperContext {
    private CopyHelper copyHelper;
    private DataFactory dataFactory;
    private DataHelper dataHelper;
    private EqualityHelper equalityHelper;
    private XMLHelper xmlHelper;
    private TypeHelper typeHelper;
    private XSDHelper xsdHelper;

    // Each application will have its own helper context - it is assumed that application 
    // names/loaders are unique within each active server instance
    private static Map<Object, HelperContext> helperContexts = new WeakHashMap<Object, HelperContext>();
    
    private static String OC4J_CLASSLOADER_NAME = "oracle";
    private static String WLS_CLASSLOADER_NAME = "weblogic";
    
    // For WebLogic
    private static MBeanServer wlsMBeanServer = null;
    private static ObjectName wlsThreadPoolRuntime = null;
    private static final String WLS_ENV_CONTEXT_LOOKUP = "java:comp/env/jmx/runtime";
    private static final String WLS_CONTEXT_LOOKUP = "java:comp/jmx/runtime";
    private static final String WLS_SERVICE_KEY = "com.bea:Name=RuntimeService,Type=weblogic.management.mbeanservers.runtime.RuntimeServiceMBean";    
    private static final String WLS_SERVER_RUNTIME = "ServerRuntime";    
    private static final String WLS_THREADPOOL_RUNTIME = "ThreadPoolRuntime";
    private static final String WLS_EXECUTE_THREAD_GET_METHOD_NAME = "getExecuteThread";
    private static final String WLS_APPLICATION_NAME_GET_METHOD_NAME = "getApplicationName";
    private static final Class[] PARAMETER_TYPES = {};

    public SDOHelperContext() {
        this(Thread.currentThread().getContextClassLoader());
    }

    public SDOHelperContext(ClassLoader aClassLoader) {
        super();
        copyHelper = new SDOCopyHelper(this);
        dataFactory = new SDODataFactoryDelegate(this);
        dataHelper = new SDODataHelper(this);
        equalityHelper = new SDOEqualityHelper(this);
        xmlHelper = new SDOXMLHelperDelegate(this, aClassLoader);
        typeHelper = new SDOTypeHelperDelegate(this);
        xsdHelper = new SDOXSDHelperDelegate(this);
    }

    public void reset() {
        ((SDOXMLHelper)getXMLHelper()).reset();
        ((SDOTypeHelper)getTypeHelper()).reset();
        ((SDOXSDHelper)getXSDHelper()).reset();
    }

    public CopyHelper getCopyHelper() {
        return copyHelper;
    }

    public DataFactory getDataFactory() {
        return dataFactory;
    }

    public DataHelper getDataHelper() {
        return dataHelper;
    }

    public EqualityHelper getEqualityHelper() {
        return equalityHelper;
    }

    public TypeHelper getTypeHelper() {
        return typeHelper;
    }

    public XMLHelper getXMLHelper() {
        return xmlHelper;
    }

    public XSDHelper getXSDHelper() {
        return xsdHelper;
    }

    public ExternalizableDelegator.Resolvable createResolvable() {
        return new SDOResolvable(this);
    }

    public ExternalizableDelegator.Resolvable createResolvable(Object target) {
        return new SDOResolvable(target, this);
    }
    
    /**
     * INTERNAL: 
     * Return the helper context for a given key.  The key will either
     * be a ClassLoader or a String (representing an application name).
     * A new context will be created and put in the map if none exists 
     * for the given key.
     */
    public static HelperContext getHelperContext() {
        Object key = getDelegateMapKey();
        HelperContext hCtx = helperContexts.get(key);
        if (hCtx == null) {
            hCtx = new SDOHelperContext();
            helperContexts.put(key, hCtx);
        }
        return hCtx;
    }
    
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
    private static Object getDelegateMapKey() {
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
    private static Object getExecuteThread() {
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