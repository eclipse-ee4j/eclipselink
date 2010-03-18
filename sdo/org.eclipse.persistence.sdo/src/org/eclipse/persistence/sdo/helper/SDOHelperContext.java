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
 *     Oracle  - initial API and implementation from Oracle TopLink
 *     dmccann - Nov. 7/2008 - Added delegate key logic from AbstractHelperDelegator
 ******************************************************************************/  
package org.eclipse.persistence.sdo.helper;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.management.AttributeChangeNotification;
import javax.management.MBeanServer;
import javax.management.Notification;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.eclipse.persistence.exceptions.SDOException;
import org.eclipse.persistence.internal.helper.ClassConstants;
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

/**
 * <b>Purpose:</b>
 * <ul>
 * <li>This class represents a local HelperContext.  The global 
 * HelperContext can be accessed as HelperProvider.getDefaultContext().</li>
 * </ul>
 * <b>Responsibilities:</b>
 * <ul>
 * <li>Provide access to instances of helper objects.</li>
 * <li>Provide an OSGi compatible HelperContext (when the constructor that takes
 * a ClassLoader is used).</li>
 * </ul>
 *
 * @since Oracle TopLink 11.1.1.0.0
 */
public class SDOHelperContext implements HelperContext {
    protected CopyHelper copyHelper;
    protected DataFactory dataFactory;
    protected DataHelper dataHelper;
    protected EqualityHelper equalityHelper;
    protected XMLHelper xmlHelper;
    protected TypeHelper typeHelper;
    protected XSDHelper xsdHelper;
    private String identifier;

    // Each application will have its own helper context - it is assumed that application 
    // names/loaders are unique within each active server instance
    private static ConcurrentHashMap<Object, ConcurrentHashMap<String, HelperContext>> helperContexts = new ConcurrentHashMap<Object, ConcurrentHashMap<String, HelperContext>>();
    private static WeakHashMap<ClassLoader, WeakReference<HelperContext>> userSetHelperContexts = new WeakHashMap<ClassLoader, WeakReference<HelperContext>>();
    
    private static String OC4J_CLASSLOADER_NAME = "oracle";
    private static String WLS_CLASSLOADER_NAME = "weblogic";
    private static String GLOBAL_HELPER_IDENTIFIER = "";
    
    // For WebLogic
    private static MBeanServer wlsMBeanServer = null;
    private static ObjectName wlsThreadPoolRuntime = null;
    private static final String WLS_ENV_CONTEXT_LOOKUP = "java:comp/env/jmx/runtime";
    private static final String WLS_CONTEXT_LOOKUP = "java:comp/jmx/runtime";
    private static final String WLS_RUNTIME_SERVICE = "RuntimeService";    
    private static final String WLS_SERVICE_KEY = "com.bea:Name=RuntimeService,Type=weblogic.management.mbeanservers.runtime.RuntimeServiceMBean";    
    private static final String WLS_APP_RUNTIMES = "ApplicationRuntimes";    
    private static final String WLS_SERVER_RUNTIME = "ServerRuntime";    
    private static final String WLS_THREADPOOL_RUNTIME = "ThreadPoolRuntime";
    private static final String WLS_EXECUTE_THREAD = "ExecuteThread";
    private static final String WLS_MBEAN_SERVER = "MBeanServer";
    private static final String WLS_EXECUTE_THREAD_GET_METHOD_NAME = "getExecuteThread";
    private static final String WLS_APPLICATION_NAME = "ApplicationName";
    private static final String WLS_APPLICATION_NAME_GET_METHOD_NAME = "getApplicationName";
    private static final String WLS_ACTIVE_VERSION_STATE = "ActiveVersionState";
    private static final Class[] PARAMETER_TYPES = {};

    /**
     * Create a local HelperContext.  The current thread's context ClassLoader
     * will be used to find static instance classes.  In OSGi environments the 
     * construct that takes a ClassLoader parameter should be used instead.
     */
    public SDOHelperContext() {
        this(Thread.currentThread().getContextClassLoader());
    }

    /**
     * Create a local HelperContext with the given identifier.  The current 
     * thread's context ClassLoader will be used to find static instance 
     * classes.  In OSGi environments the construct that takes a ClassLoader 
     * parameter should be used instead.
     * 
     * @param identifier The unique label for this HelperContext.
     */
    public SDOHelperContext(String identifier) {
        this(identifier, Thread.currentThread().getContextClassLoader());
    }

    /**
     * Create a local HelperContext.  This constructor should be used in OSGi 
     * environments.
     * 
     * @param aClassLoader This class loader will be used to find static 
     * instance classes.
     */
    public SDOHelperContext(ClassLoader aClassLoader) {
        super();
        this.identifier = this.GLOBAL_HELPER_IDENTIFIER;
        initialize(aClassLoader);
    }

    /**
     * Create a local HelperContext with the given identifier.  This constructor
     * should be used in OSGi environments.
     * 
     * @param identifier The unique label for this HelperContext.
     * @param aClassLoader This class loader will be used to find static 
     * instance classes.
     */
    public SDOHelperContext(String identifier, ClassLoader aClassLoader) {
        super();
        this.identifier = identifier;
        initialize(aClassLoader);
    }

    protected void initialize(ClassLoader aClassLoader)  {
        copyHelper = new SDOCopyHelper(this);
        dataFactory = new SDODataFactoryDelegate(this);
        dataHelper = new SDODataHelper(this);
        equalityHelper = new SDOEqualityHelper(this);
        xmlHelper = new SDOXMLHelperDelegate(this, aClassLoader);
        typeHelper = new SDOTypeHelperDelegate(this);
        xsdHelper = new SDOXSDHelperDelegate(this);        
    }
    
    /**
     * Reset the Type,XML and XSD helper instances.
     */
    public void reset() {
        ((SDOTypeHelper)getTypeHelper()).reset();
        ((SDOXMLHelper)getXMLHelper()).reset();
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
     * Put a ClassLoader/HelperContext key/value pair in the Thread HelperContext 
     * map.  If Thread.currentThread().getContextClassLoader() == key during 
     * getHelperContext() call then the HelperContext (value) will be returned.
     * This method will overwrite an existing entry in the map with the same
     * ClassLoader key.
     * 
     * @param key class loader
     * @param value helper context
     */
    public static void putHelperContext(ClassLoader key, HelperContext value) {
        if (key == null || value == null) {
            return;
        }
        userSetHelperContexts.put(key, new WeakReference<HelperContext>(value));
    }
    
    /**
     * INTERNAL:
     * Retrieve the HelperContext for a given ClassLoader from the Thread 
     * HelperContext map.
     * 
     * @param key class loader
     * @return HelperContext for the given key if key exists in the map, otherwise null
     */
    private static HelperContext getHelperContext(ClassLoader key) {
        if (key == null) {
            return null;
        }
        WeakReference<HelperContext> wRef = userSetHelperContexts.get(key);
        if (wRef == null) {
            return null;
        }
        return wRef.get();
    }
    
    /**
     * INTERNAL:
     * Remove a ClassLoader/HelperContext key/value pair from the Thread 
     * HelperContext map.
     * 
     * @param key class loader
     */
    public static void removeHelperContext(ClassLoader key) {
        if (key == null) {
            return;
        }
        userSetHelperContexts.remove(key);
    }
    
    /**
     * INTERNAL: 
     * Return the helper context for a given key.  The key will either
     * be a ClassLoader or a String (representing an application name).
     * A new context will be created and put in the map if none exists 
     * for the given key.
     * 
     * The key is assumed to be non-null -  getDelegateKey should always
     * return either a string representing the application name (for WLS)
     * or a class loader.  This is relevant since 'putIfAbsent' will 
     * throw a null pointer exception if the key is null.   
     */
    public static HelperContext getHelperContext() {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        // check the map for contextClassLoader and return it if it exists
        HelperContext hCtx = getHelperContext(contextClassLoader);
        if (hCtx != null) {
            return hCtx;
        }
        return getHelperContext(GLOBAL_HELPER_IDENTIFIER);
    }

    /**
     * Return the local helper context with the given identifier, or create
     * one if it does not already exist.
     */
    public static HelperContext getHelperContext(String identifier) {
        ConcurrentMap<String, HelperContext> contextMap = getContextMap();
        HelperContext helperContext = contextMap.get(identifier);
        if (null == helperContext) {
            helperContext = new SDOHelperContext(identifier);
            HelperContext existingContext = contextMap.putIfAbsent(identifier, helperContext);
            if(existingContext != null) {
                helperContext = existingContext;
            }
        }
        return helperContext;
    }

    /**
     * Return the local helper context with the given identifier, or create
     * one if it does not already exist.
     */
    public static HelperContext getHelperContext(String identifier, ClassLoader classLoader) {
        ConcurrentMap<String, HelperContext> contextMap = getContextMap();
        HelperContext helperContext = contextMap.get(identifier);
        if (null == helperContext) {
            helperContext = new SDOHelperContext(identifier, classLoader);
            HelperContext existingContext = contextMap.putIfAbsent(identifier, helperContext);
            if(existingContext != null) {
                helperContext = existingContext;
            }
        }
        return helperContext;
    }

    /**
     * Returns the map of helper contexts, keyed on Identifier, for the current application
     * @return
     */
    static ConcurrentMap<String, HelperContext> getContextMap() {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        Object key = getDelegateMapKey(contextClassLoader);
        ConcurrentHashMap<String, HelperContext> contextMap = helperContexts.get(key);
        if(null == contextMap) {
            contextMap = new ConcurrentHashMap<String, HelperContext>();
            ConcurrentHashMap existingMap = helperContexts.putIfAbsent(key, contextMap);
            if(existingMap != null) {
                contextMap = existingMap;
            }
            if (key.getClass() == ClassConstants.STRING) {
                helperContexts.put(contextClassLoader, contextMap);
            }
        }
        addNotificationListener(key);
        return contextMap;
    }

    /**
     * Replaces the provided helper context in the map of identifiers to helper contexts for
     * this application. ctx.getIdentifier() will be used to obtain identifier 
     */
    public static void putHelperContext(HelperContext ctx) {
        String identifier = ((SDOHelperContext) ctx).getIdentifier();
        if(GLOBAL_HELPER_IDENTIFIER.equals(identifier)) {
            // The global HelperContext cannot be replaced
            return;
        }
        getContextMap().put(identifier, ctx);
    }

    /**
     * ADVANCED:
     * Remove the HelperContext for the application associated with a
     * given key, if it exists in the map.
     */
    private static void resetHelperContext(Object key) {
        HelperContext hCtx = helperContexts.get(key).get(GLOBAL_HELPER_IDENTIFIER);
        if (hCtx != null) {
            helperContexts.remove(key);        
        }
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
    private static Object getDelegateMapKey(ClassLoader classLoader) {
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
                    // ExecuteThread returns null
                    if (delegateKey == null) {
                        delegateKey = classLoader;
                    }
                } catch (Exception e) {
                    throw SDOException.errorInvokingWLSMethodReflectively(WLS_APPLICATION_NAME_GET_METHOD_NAME, WLS_EXECUTE_THREAD, e);
                }
            }
        }        
        return delegateKey;
    }

    /**
     * Lazy load the WebLogic MBeanServer instance.
     * 
     * @return
     */
    private static MBeanServer getWLSMBeanServer() {
        if (wlsMBeanServer == null) {
            Context weblogicContext = null;
            try {
                weblogicContext = new InitialContext();
                try {
                    // The lookup string used depends on the context from which this class is being 
                    // accessed, i.e. servlet, EJB, etc.  Try java:comp/env lookup
                    wlsMBeanServer = (MBeanServer) weblogicContext.lookup(WLS_ENV_CONTEXT_LOOKUP);
                } catch (NamingException e) {
                    // Lookup failed - try java:comp
                    try {
                        wlsMBeanServer = (MBeanServer) weblogicContext.lookup(WLS_CONTEXT_LOOKUP);
                    } catch (NamingException ne) {
                        throw SDOException.errorPerformingWLSLookup(WLS_MBEAN_SERVER, ne);
                    }
                }
            } catch (NamingException nex) {
                throw SDOException.errorCreatingWLSInitialContext(nex);
            }
        }
        return wlsMBeanServer;
    }
    
    /**
     * INTERNAL:
     * This convenience method will look up a WebLogic execute thread from the runtime 
     * MBean tree.  The execute thread contains application information.  This code 
     * will use the name of the current thread to lookup the corresponding ExecuteThread.
     * The ExecuteThread will allow us to obtain the application name (and version, etc).
     * 
     * @return application name or null if the name cannot be obtained
     */
    private static Object getExecuteThread() {
        if (getWLSMBeanServer() != null) {
            // Lazy load the ThreadPoolRuntime instance
            if (wlsThreadPoolRuntime == null) {
                ObjectName service = null;
                ObjectName serverRuntime = null;
                try {
                    service = new ObjectName(WLS_SERVICE_KEY);
                } catch (Exception x) {
                    throw SDOException.errorGettingWLSObjectName(WLS_RUNTIME_SERVICE + " [" + WLS_SERVICE_KEY + "]", x);
                }
                try {
                    serverRuntime = (ObjectName) wlsMBeanServer.getAttribute(service, WLS_SERVER_RUNTIME);
                } catch (Exception x) {
                    throw SDOException.errorGettingWLSObjectName(WLS_SERVER_RUNTIME, x);
                }
                try {
                    wlsThreadPoolRuntime = (ObjectName) wlsMBeanServer.getAttribute(serverRuntime, WLS_THREADPOOL_RUNTIME);
                } catch (Exception x) {
                    throw SDOException.errorGettingWLSObjectName(WLS_THREADPOOL_RUNTIME, x);
                }
            }
            try {
                return wlsMBeanServer.invoke(wlsThreadPoolRuntime, WLS_EXECUTE_THREAD_GET_METHOD_NAME, new Object[] { Thread.currentThread().getName() }, new String[] { String.class.getName() });
            } catch (Exception x) {
                throw SDOException.errorInvokingWLSMethodReflectively(WLS_EXECUTE_THREAD_GET_METHOD_NAME, WLS_THREADPOOL_RUNTIME, x);
            }
        }
        return null;
    }
    
    /**
     * INTERNAL:
     * Adds a notification listener to the ApplicationRuntimeMBean instance with "ApplicationName" 
     * attribute equals to 'applicationName'.  The listener will handle application re-deployment.
     * If any errors occur, we will fail silently, i.e. the listener will not be added.
     *  
     * @param mapKey
     */
    private static void addNotificationListener(Object mapKey) {
        if (Thread.currentThread().getContextClassLoader().getClass().getName().contains(WLS_CLASSLOADER_NAME) && getWLSMBeanServer() != null) {
            try {
                ObjectName service = new ObjectName(WLS_SERVICE_KEY);
                ObjectName serverRuntime = (ObjectName) wlsMBeanServer.getAttribute(service, WLS_SERVER_RUNTIME);
                ObjectName[] appRuntimes = (ObjectName[]) wlsMBeanServer.getAttribute(serverRuntime, WLS_APP_RUNTIMES);
                for (int i=0; i < appRuntimes.length; i++) {
                    try {
                        ObjectName appRuntime = appRuntimes[i];
                        Object appName = wlsMBeanServer.getAttribute(appRuntime, WLS_APPLICATION_NAME);
                        if (appName != null && appName.toString().equals(mapKey)) {
                            wlsMBeanServer.addNotificationListener(appRuntime, new MyNotificationListener(appName.toString()), null, null);
                            break;
                        }
                    } catch (Exception ex) {}
                }
            } catch (Exception x) {}
        }
    }

    /**
     * INTERNAL:
     * Inner class used to catch application re-deployment.  Upon notification of this event,
     * the helper context for the given application will be removed from the helper context
     * to application map.
     */
    private static class MyNotificationListener implements NotificationListener {
        Object mapKey;
        
        public MyNotificationListener(Object mapKey) {
            this.mapKey = mapKey;
        }
        
        public void handleNotification(Notification notification, Object handback) {
            if (notification instanceof AttributeChangeNotification) {
                try {
                    AttributeChangeNotification acn = (AttributeChangeNotification) notification;
                    if (acn.getAttributeName().equals(WLS_ACTIVE_VERSION_STATE)) {
                        if (acn.getNewValue().equals(new Integer(0))) {
                            resetHelperContext(mapKey);
                        }
                    }
                } catch (Exception x) {}
            }
        }
    }

    /**
     * ADVANCED
     * Promote this helper context to be the default or global one.
     * This will completely replace the existing default context including
     * all types and properties defined. 
     */
    public void makeDefaultContext() {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        Object key = getDelegateMapKey(contextClassLoader);
        ConcurrentHashMap<String, HelperContext> contexts = helperContexts.get(key);
        if(contexts == null) {
            contexts = new ConcurrentHashMap<String, HelperContext>();
            ConcurrentHashMap<String, HelperContext> existingContexts = helperContexts.putIfAbsent(key, contexts);
            if(existingContexts != null) {
                contexts = existingContexts;
            }
        }
        this.identifier = GLOBAL_HELPER_IDENTIFIER;
        contexts.put(GLOBAL_HELPER_IDENTIFIER, this);
    }
    public String getIdentifier() {
        return this.identifier;
    }
}
