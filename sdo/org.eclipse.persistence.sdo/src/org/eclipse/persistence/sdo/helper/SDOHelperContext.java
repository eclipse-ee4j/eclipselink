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

import java.io.File;
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
    private static ConcurrentHashMap<HelperContextMapKey, ConcurrentHashMap<String, HelperContext>> helperContexts = new ConcurrentHashMap<HelperContextMapKey, ConcurrentHashMap<String, HelperContext>>();
    private static WeakHashMap<ClassLoader, WeakReference<HelperContext>> userSetHelperContexts = new WeakHashMap<ClassLoader, WeakReference<HelperContext>>();

    // Application server identifiers
    private static String OC4J_CLASSLOADER_NAME = "oracle";
    private static String WLS_CLASSLOADER_NAME = "weblogic";
    private static String WAS_CLASSLOADER_NAME = "com.ibm.ws";
    private static String JBOSS_CLASSLOADER_NAME = "jboss";
    private static String GLOBAL_HELPER_IDENTIFIER = "";

    // Common
    private static final int COUNTER_LIMIT = 20;

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
    private static final Class[] WLS_PARAMETER_TYPES = {};

    // For WebSphere
    private static final String WAS_NEWLINE = "\n";
    private static final String WAS_APP_COLON = "[app:";    
    private static final String WAS_CLOSE_BRACKET = "]";
    
    // For JBoss
    private static final String JBOSS_VFSZIP = "vfszip:";
    private static final String JBOSS_VFSFILE = "vfsfile:";
    private static final String JBOSS_EAR = ".ear";
    private static final String JBOSS_WAR = ".war";
    private static final int JBOSS_VFSZIP_OFFSET = JBOSS_VFSZIP.length();
    private static final int JBOSS_VFSFILE_OFFSET = JBOSS_VFSFILE.length();
    private static final int JBOSS_EAR_OFFSET = JBOSS_EAR.length();
    private static final int JBOSS_TRIM_COUNT = 2;  // for stripping off the remaining '/}' chars

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

    /**
     * The underlying helpers for this instance will be instantiated 
     * in this method.
     * 
     * @param aClassLoader
     */
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

    /**
     * Return the CopyHelper instance for this helper context.
     */
    public CopyHelper getCopyHelper() {
        return copyHelper;
    }

    /**
     * Return the DataFactory instance for this helper context.
     */
    public DataFactory getDataFactory() {
        return dataFactory;
    }

    /**
     * Return the DataHelper instance for this helper context.
     */
    public DataHelper getDataHelper() {
        return dataHelper;
    }

    /**
     * Return the EqualityHelper instance for this helper context.
     */
    public EqualityHelper getEqualityHelper() {
        return equalityHelper;
    }

    /**
     * Return the TypeHelper instance for this helper context.
     */
    public TypeHelper getTypeHelper() {
        return typeHelper;
    }

    /**
     * Return the XMLHelper instance for this helper context.
     */
    public XMLHelper getXMLHelper() {
        return xmlHelper;
    }

    /**
     * Return the XSDHelper instance for this helper context.
     */
    public XSDHelper getXSDHelper() {
        return xsdHelper;
    }

    /**
     * Create and return a new ExternalizableDelegator.Resolvable instance based
     * on this helper context.
     * 
     * @return
     */
    public ExternalizableDelegator.Resolvable createResolvable() {
        return new SDOResolvable(this);
    }

    /**
     * Create and return a new ExternalizableDelegator.Resolvable instance based
     * on this helper context and a given target.
     * 
     * @param target
     * @return
     */
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
            if (existingContext != null) {
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
            if (existingContext != null) {
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
        String classLoaderName = contextClassLoader.getClass().getName();
        HelperContextMapKey key = getContextMapKey(contextClassLoader, classLoaderName);
        ConcurrentHashMap<String, HelperContext> contextMap = helperContexts.get(key);       
        // the following block only applies to WAS and JBoss - hence the loader name check
        if (contextMap != null && (classLoaderName.contains(WAS_CLASSLOADER_NAME) || classLoaderName.contains(JBOSS_CLASSLOADER_NAME))) {
            // at this point there is an existing entry in the map - if the context is keyed 
            // on application name we need to check to see if a redeployment occurred; in 
            // that case the app names will match, but the class loaders will not
            if (key.getApplicationName() != null) {
                for (HelperContextMapKey existingKey : helperContexts.keySet()) {
                    // find the existing key
                    if (key.equals(existingKey)) {
                        // compare loaders - if the loaders are not equal, we need to remove the old entry
                        if (!key.areLoadersEqual(existingKey.getLoader())) {
                            // concurrency - use remove(key, value) to ensure we don't remove a newly added entry
                            helperContexts.remove(key, contextMap);
                            contextMap = null;
                        }
                    }
                }
            }            
        }
        // may need to add a new entry
        if (null == contextMap) {
            contextMap = new ConcurrentHashMap<String, HelperContext>();
            // use putIfAbsent to avoid concurrent entries in the map
            ConcurrentHashMap existingMap = helperContexts.putIfAbsent(key, contextMap);
            if (existingMap != null) {
                // if a new entry was just added, use it instead of the one we just created
                contextMap = existingMap;
            }
            if (key.getClass() == ClassConstants.STRING) {
                helperContexts.put(new HelperContextMapKey(contextClassLoader), contextMap);
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
        if (GLOBAL_HELPER_IDENTIFIER.equals(identifier)) {
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
    private static void resetHelperContext(HelperContextMapKey key) {
        HelperContext hCtx = helperContexts.get(key).get(GLOBAL_HELPER_IDENTIFIER);
        if (hCtx != null) {
            helperContexts.remove(key);
        }
    }

    /**
     * INTERNAL:
     * This method will return the HelperContextMapKey instance to be used to 
     * store/retrieve the global helper context for a given application.
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
     * @return HelperContextMapKey wrapping the application classloader for OC4J,
     *         the application name for WebLogic and WebSphere, the archive file 
     *         name for JBoss; otherwise a HelperContextMapKey wrapping 
     *         Thread.currentThread().getContextClassLoader()
     */
    private static HelperContextMapKey getContextMapKey(ClassLoader classLoader, String classLoaderName) {
        // Helper contexts in OC4J server will be keyed on classloader  
        if (classLoaderName.startsWith(OC4J_CLASSLOADER_NAME)) {
            // Check to see if we are running in a Servlet container or a local EJB container
            if ((classLoader.getParent() != null) //
                    && ((classLoader.toString().indexOf(SDOConstants.CLASSLOADER_WEB_FRAGMENT) != -1) //
                    ||  (classLoader.toString().indexOf(SDOConstants.CLASSLOADER_EJB_FRAGMENT) != -1))) {
                classLoader = classLoader.getParent();
            }
            return new HelperContextMapKey(classLoader);
        }
        // Helper contexts in WebLogic server will be keyed on application name
        if (classLoaderName.contains(WLS_CLASSLOADER_NAME)) {
            Object executeThread = getExecuteThread();
            if (executeThread != null) {
                try {
                    Method getMethod = PrivilegedAccessHelper.getPublicMethod(executeThread.getClass(), WLS_APPLICATION_NAME_GET_METHOD_NAME, WLS_PARAMETER_TYPES, false);
                    Object appName = PrivilegedAccessHelper.invokeMethod(getMethod, executeThread);
                    // if ExecuteThread returns null, we will key on loader, otherwise use the application name
                    if (appName != null) {
                        // assumes object returned from getApplicationName method call is a String if non-null
                        return new HelperContextMapKey(appName.toString(), classLoader);
                    }
                } catch (Exception e) {
                    throw SDOException.errorInvokingWLSMethodReflectively(WLS_APPLICATION_NAME_GET_METHOD_NAME, WLS_EXECUTE_THREAD, e);
                }
            }
            // couldn't get the application name, so default to the context loader
            return new HelperContextMapKey(classLoader);
        }            
        // Helper contexts in WebSphere server will be keyed on application name
        if (classLoaderName.contains(WAS_CLASSLOADER_NAME)) {
            return getContextMapKeyForWAS(classLoader);
        }
        // Helper contexts in JBoss server will be keyed on archive file name
        if (classLoaderName.contains(JBOSS_CLASSLOADER_NAME)) {
            return getContextMapKeyForJBoss(classLoader);
        }
        // at this point we will default to the context loader
        return new HelperContextMapKey(classLoader);
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
     * attribute equals to 'mapKey.applicationName'.  The listener will handle application 
     * re-deployment.
     * 
     * If any errors occur, we will fail silently, i.e. the listener will not be added.
     *  
     * @param mapKey
     */
    private static void addNotificationListener(HelperContextMapKey mapKey) {
        if (Thread.currentThread().getContextClassLoader().getClass().getName().contains(WLS_CLASSLOADER_NAME) && getWLSMBeanServer() != null) {
            try {
                ObjectName service = new ObjectName(WLS_SERVICE_KEY);
                ObjectName serverRuntime = (ObjectName) wlsMBeanServer.getAttribute(service, WLS_SERVER_RUNTIME);
                ObjectName[] appRuntimes = (ObjectName[]) wlsMBeanServer.getAttribute(serverRuntime, WLS_APP_RUNTIMES);
                for (int i=0; i < appRuntimes.length; i++) {
                    try {
                        ObjectName appRuntime = appRuntimes[i];
                        Object appName = wlsMBeanServer.getAttribute(appRuntime, WLS_APPLICATION_NAME);
                        if (appName != null && appName.toString().equals(mapKey.getApplicationName())) {
                            wlsMBeanServer.addNotificationListener(appRuntime, new MyNotificationListener(mapKey), null, null);
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
        HelperContextMapKey mapKey;
        
        public MyNotificationListener(HelperContextMapKey mapKey) {
            this.mapKey = mapKey;
        }
        
        public void handleNotification(Notification notification, Object handback) {
            if (notification instanceof AttributeChangeNotification) {
                try {
                    AttributeChangeNotification acn = (AttributeChangeNotification) notification;
                    if (acn.getAttributeName().equals(WLS_ACTIVE_VERSION_STATE)) {
                        if (acn.getNewValue().equals(0)) {
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
        HelperContextMapKey key = getContextMapKey(contextClassLoader, contextClassLoader.getClass().getName());
        ConcurrentHashMap<String, HelperContext> contexts = helperContexts.get(key);
        if (contexts == null) {
            contexts = new ConcurrentHashMap<String, HelperContext>();
            ConcurrentHashMap<String, HelperContext> existingContexts = helperContexts.putIfAbsent(key, contexts);
            if (existingContexts != null) {
                contexts = existingContexts;
            }
        }
        this.identifier = GLOBAL_HELPER_IDENTIFIER;
        contexts.put(GLOBAL_HELPER_IDENTIFIER, this);
    }
    
    /**
     * Attempt to return the WAS application name based on a given class loader.
     * For WAS, the application loader's toString will contain "[app:".
     * 
     * @param loader
     * @return String representing the application name, or null if the loader's toString
     *         doesn't contain "[app:".
     */
    private static String getApplicationNameFromWASClassLoader(final ClassLoader loader) {
        String applicationName = null;
        String loaderString = loader.toString().trim();
        while ((loaderString.startsWith(WAS_NEWLINE)) && (loaderString.length() > 0)) {
            loaderString = loaderString.substring(1).trim();
        }
        String loaderStringLines[] = loaderString.split(WAS_NEWLINE, 2);
        if (loaderStringLines.length > 0) {
            String firstLine = loaderStringLines[0].trim();
            int appPos = firstLine.indexOf(WAS_APP_COLON);
            if ((appPos >= 0) && (appPos + WAS_APP_COLON.length() < firstLine.length())) {
                String appNameSegment = firstLine.substring(appPos + WAS_APP_COLON.length());
                int closingBracketPosition = appNameSegment.indexOf(WAS_CLOSE_BRACKET);
                if (closingBracketPosition > 0) {
                    applicationName = appNameSegment.substring(0, closingBracketPosition);
                } else {
                    applicationName = appNameSegment;
                }
            }
        }
        return applicationName;
    }

    /**
     * Attempt to return a HelperContextMapKey instance wrapping the application name and 
     * application loader based on a given WAS classloader.  Here we will traverse up the
     * loader hierarchy looking for the top-most application loader.  
     * 
     * For WAS, the application loader's toString (and those of it's children) will 
     * contain "[app:".
     * 
     * @param loader
     * @return a HelperContextMapKey instance wrapping application name/loader if 
     *         successfully retrieved (i.e. at least one loader exists in the 
     *         hierarchy with toString containing "[app:"), or a HelperContextMapKey 
     *         instance wrapping the given loader if not found
     */                
    private static HelperContextMapKey getContextMapKeyForWAS(ClassLoader loader) {
        ClassLoader applicationLoader = loader;
        String applicationName = null; 
        // Safety counter to keep from taking too long or looping forever, just in case of some unexpected circumstance.
        int i = 0;
        // iterate up the loader hierarchy looking for the top-level application loader
        while (i < COUNTER_LIMIT) {
            if (wasClassLoaderHasApplicationName(loader)) {
                // current loader has application name info - store it
                applicationLoader = loader;
            }
            final ClassLoader parent = loader.getParent();
            // once we have hit the top we will stop looking
            if (parent == null || parent == loader) {
                // get the application name from the loader we are going to return
                applicationName = getApplicationNameFromWASClassLoader(applicationLoader);
                break;
            }
            // move up and try again
            loader = parent;
            i++;
        }
        // if we found the application name, use it as the key
        if (applicationName != null) {
            return new HelperContextMapKey(applicationName, applicationLoader);
            
        }
        // at this point we don't know the application name so the loader will be the key
        return new HelperContextMapKey(applicationLoader);
    }

    /**
     * Indicates if a given WAS class loader contains a application name.
     * 
     * Assumptions:
     * 1 - The toString of a WAS application loader will contain "[app:".
     * 
     * @param loader
     * @return true if the WAS class loader's toString contains "[app:"; false otherwise
     */                
    private static boolean wasClassLoaderHasApplicationName(ClassLoader loader) {
        String loaderString = loader.toString().trim();
        while ((loaderString.startsWith(WAS_NEWLINE)) && (loaderString.length() > 0)) {
            loaderString = loaderString.substring(1).trim();
        }
        String loaderStringLines[] = loaderString.split(WAS_NEWLINE, 2);
        if (loaderStringLines.length > 0) {
            String firstLine = loaderStringLines[0].trim();
            int appPos = firstLine.indexOf(WAS_APP_COLON);
            if ((appPos >= 0) && (appPos + WAS_APP_COLON.length() < firstLine.length())) {
                return true;
            }
        }
        return false;
    }    
    
    /**
     * Attempt to get the application name (archive file name) based on a given JBoss classloader.
     * 
     * Here is an example toString result of the classloader which loaded the application in JBoss:   
     * BaseClassLoader@1316dd{vfszip:/ade/xidu_j2eev5/oracle/work/utp/resultout/functional/jrf/
     *       jboss-jrfServer/deploy/jrftestapp.jar/}
     * or {vfsfile:/net/stott18.ca.oracle.com/scratch/xidu/view_storage/xidu_j2eev5/work/jboss/
     *       server/default/deploy/testapp.ear/} in exploded deployment
     * war: BaseClassLoader@bfe0e4{vfszip:/ade/xidu_j2eebug/oracle/work/utp/resultout/functional/
     *       jrf/jboss-jrfServer/jrfServer/deploy/jrftestapp.ear/jrftestweb.war/}
     * 
     * Assumptions:
     * 1 - A given toString will only contain one .ear, .jar, or .war EXCEPT in the servlet case, 
     * where the string would have "{vfszip:/.../xxx.ear/.../xxx.war/}".  In this case we want to 
     * return xxx.ear as the application name.
     * 2 - A given toString will end in '/}'.
     * 3 - A toString containing the application name will have one of "vfszip:" or "vfsfile:".
     * 
     * @param loader
     * @return application name (archive file name) if successfully retrieved (i.e. loader 
     *         exists in the hierarchy with toString containing "vfszip:" or "vfsfile:") 
     *         or null
     */                
    private static String getApplicationNameFromJBossClassLoader(ClassLoader loader) {
        String clStr = loader.toString();
        String appNameSegment = null;
        // handle "vfszip:<archive-file-name>"
        if (clStr.indexOf(JBOSS_VFSZIP) != -1) {            
            appNameSegment = clStr.substring(clStr.indexOf(JBOSS_VFSZIP) + JBOSS_VFSZIP_OFFSET, clStr.length() - JBOSS_TRIM_COUNT);
            // handle case where the string contains both .ear and .war (remove the .war portion)
            if ((appNameSegment.indexOf(JBOSS_WAR) != -1) && (appNameSegment.indexOf(JBOSS_EAR) != -1)) {
                appNameSegment = appNameSegment.substring(0, appNameSegment.indexOf(JBOSS_EAR) + JBOSS_EAR_OFFSET);
            }
        // handle "vfsfile:<archive-file-name>"
        } else if (clStr.indexOf(JBOSS_VFSFILE) != -1) {
            appNameSegment = clStr.substring(clStr.indexOf(JBOSS_VFSFILE) + JBOSS_VFSFILE_OFFSET, clStr.length() - JBOSS_TRIM_COUNT);
        }
        if (appNameSegment != null) {
            return new File(appNameSegment).getName();
        }
        return null;
    }    

    /**
     * Attempt to return a HelperContextMapKey instance wrapping the archive file name and 
     * application loader based on a given JBoss classloader.  Here we will traverse up the
     * loader hierarchy looking for the top-most application loader.  
     * 
     * @param loader
     * @return a HelperContextMapKey instance wrapping archive file name/loader if 
     *         successfully retrieved (i.e. at least one loader exists in the 
     *         hierarchy with toString containing containing "vfszip:" or "vfsfile:"), 
     *         or a HelperContextMapKey instance wrapping the given loader if not found
     */                
    private static HelperContextMapKey getContextMapKeyForJBoss(ClassLoader loader) {
        ClassLoader applicationLoader = loader;
        String archiveFileName = null;
        // safety counter to keep from taking too long or looping forever, just in case of some unexpected circumstance
        int i = 0;
        // iterate up the loader hierarchy looking for the top-level application loader
        while (i < COUNTER_LIMIT) {
            if (jBossClassLoaderHasArchiveFileInfo(loader)) {
                // current loader has archive file info - store it
                applicationLoader = loader;
            }
            final ClassLoader parent = loader.getParent();
            // once we have hit the top we will stop looking
            if (parent == null || parent == loader) {
                // get the archive file name from the loader we are going to return
                archiveFileName = getApplicationNameFromJBossClassLoader(applicationLoader);
                break;
            }
            // move up and try again
            loader = parent;
            i++;
        }
        // if we found the archive file name, use it as the key
        if (archiveFileName != null) {
            return new HelperContextMapKey(archiveFileName, applicationLoader);
            
        }
        // at this point we don't know the archive file name so the loader will be the key
        return new HelperContextMapKey(applicationLoader);
    }

    /**
     * Indicates if a given JBoss class loader contains an archive file name; i.e. is an application
     * loader.
     * 
     * Here is an example toString result of the classloader which loaded the application in JBoss:   
     * BaseClassLoader@1316dd{vfszip:/ade/xidu_j2eev5/oracle/work/utp/resultout/functional/jrf/
     *       jboss-jrfServer/deploy/jrftestapp.jar/}
     * or {vfsfile:/net/stott18.ca.oracle.com/scratch/xidu/view_storage/xidu_j2eev5/work/jboss/
     *       server/default/deploy/testapp.ear/} in exploded deployment
     * war: BaseClassLoader@bfe0e4{vfszip:/ade/xidu_j2eebug/oracle/work/utp/resultout/functional/
     *       jrf/jboss-jrfServer/jrfServer/deploy/jrftestapp.ear/jrftestweb.war/}
     * 
     * Assumptions:
     * 1 - The toString of an application loader will have one of "vfszip:" or "vfsfile:".
     * 
     * @param loader
     * @return true if the given JBoss loader has a toString containing "vfszip:" or "vfsfile:");
     *         false otherwise
     */                
    private static boolean jBossClassLoaderHasArchiveFileInfo(ClassLoader loader) {
        // look for "vfszip:<archive-file-name>" or "vfsfile:<archive-file-name>"
        return (loader.toString().indexOf(JBOSS_VFSZIP) != -1 || loader.toString().indexOf(JBOSS_VFSFILE) != -1);            
    }    

    /**
     * Return the unique label for this HelperContext.
     * 
     * @return String representing the unique label for this HelperContext
     */
    public String getIdentifier() {
        return this.identifier;
    }
}
