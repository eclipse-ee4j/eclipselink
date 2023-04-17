/*
 * Copyright (c) 1998, 2023 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle  - initial API and implementation from Oracle TopLink
//     dmccann - Nov. 7/2008 - Added delegate key logic from AbstractHelperDelegator
//     Dmitry Kornilov - 2014/11/25 - ApplicationAccessWLS fixed to reflect changes in WLS
package org.eclipse.persistence.sdo.helper;

import java.io.File;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.management.AttributeChangeNotification;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.Notification;
import javax.management.NotificationFilterSupport;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.eclipse.persistence.exceptions.SDOException;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.identitymaps.CacheIdentityMap;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedGetMethod;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOResolvable;
import org.eclipse.persistence.sdo.SDOSystemProperties;
import org.eclipse.persistence.sdo.helper.delegates.SDODataFactoryDelegate;
import org.eclipse.persistence.sdo.helper.delegates.SDOTypeHelperDelegate;
import org.eclipse.persistence.sdo.helper.delegates.SDOTypeHelperDelegate.SDOWrapperTypeId;
import org.eclipse.persistence.sdo.helper.delegates.SDOXMLHelperDelegate;
import org.eclipse.persistence.sdo.helper.delegates.SDOXSDHelperDelegate;
import org.eclipse.persistence.sdo.types.SDOWrapperType;

import commonj.sdo.Type;
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

    private static final Logger LOGGER = Logger.getLogger(SDOHelperContext.class.getName());

    protected CopyHelper copyHelper;
    protected DataFactory dataFactory;
    protected DataHelper dataHelper;
    protected EqualityHelper equalityHelper;
    protected XMLHelper xmlHelper;
    protected TypeHelper typeHelper;
    protected XSDHelper xsdHelper;
    private String identifier;
    private Map<String, Object> properties;
    private boolean isStrictTypeCheckingEnabled = PrivilegedAccessHelper.getSystemPropertyBoolean(
            SDOSystemProperties.SDO_STRICT_TYPE_CHECKING_PROPERTY_NAME, true);

    /**
     * Property controls strictness of {@link Type#getInstanceClass()} type checking.
     *
     * <p>
     * See {@link #isStrictTypeCheckingEnabled()} for more details.
     * By this property, the initial value can be changed.
     * Default value is <code>true</code>.
     * </p>
     *
     * @deprecated
     * @see org.eclipse.persistence.sdo.SDOSystemProperties.SDO_STRICT_TYPE_CHECKING_PROPERTY_NAME
     * Moved to {@link org.eclipse.persistence.sdo.SDOSystemProperties}.     *
     */
    @Deprecated
    public static final String STRICT_TYPE_CHECKING_PROPERTY_NAME = SDOSystemProperties.SDO_STRICT_TYPE_CHECKING_PROPERTY_NAME;

    private static int helperContextsMaxSize = Integer.parseInt(PrivilegedAccessHelper.getSystemProperty(SDOSystemProperties.SDO_HELPER_CONTEXTS_MAX_SIZE, "1000000"));

    // Each application will have its own helper context - it is assumed that application
    // names/loaders are unique within each active server instance
    private static ConcurrentHashMap<Object, CacheIdentityMap> helperContexts = new ConcurrentHashMap<Object, CacheIdentityMap>();
    // Each application will have a Map of alias' to identifiers
    private static ConcurrentHashMap<Object, ConcurrentHashMap<String, String>> aliasMap = new ConcurrentHashMap<Object, ConcurrentHashMap<String, String>>();
    // Each application could have separate HelperContextResolver
    private static final ConcurrentHashMap<Object, HelperContextResolver> HELPER_CONTEXT_RESOLVERS = new ConcurrentHashMap<>();
    // allow users to set their own classloader to context map pairs
    private static WeakHashMap<ClassLoader, WeakHashMap<String, WeakReference<HelperContext>>> userSetHelperContexts = new WeakHashMap<ClassLoader, WeakHashMap<String, WeakReference<HelperContext>>>();
    // keep a map of application names to application class loaders to handle redeploy
    private static ConcurrentHashMap<String, ClassLoader> appNameToClassLoaderMap = new ConcurrentHashMap<String, ClassLoader>();
    // keep a map of application name to the wrapper types for that application
    private static final ConcurrentHashMap<String,Map<SDOWrapperTypeId,SDOWrapperType>> SDO_WRAPPER_TYPES = new ConcurrentHashMap<>();

    // Application server identifiers
    private static String WLS_CLASSLOADER_NAME = "weblogic";
    private static String WAS_CLASSLOADER_NAME = "com.ibm.ws";
    private static String JBOSS_CLASSLOADER_NAME = "jboss";
    private static String GLOBAL_HELPER_IDENTIFIER = "";
    private static final int WLS_IDENTIFIER = 0;
    private static final int JBOSS_IDENTIFIER = 1;

    // Common
    private static final int COUNTER_LIMIT = 20;

    // For WebLogic
    private static volatile ApplicationAccessWLS applicationAccessWLS = null;
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
    private static final String WLS_APPLICATION_VERSION = "ApplicationVersion";
    private static final String WLS_APPLICATION_NAME_GET_METHOD_NAME = "getApplicationName";
    private static final String WLS_ACTIVE_VERSION_STATE = "ActiveVersionState";
    private static final Class<?>[] WLS_PARAMETER_TYPES = {};

    // For WebSphere
    private static final String WAS_NEWLINE = "\n";
    private static final String WAS_APP_COLON = "[app:";
    private static final String WAS_CLOSE_BRACKET = "]";

    // For JBoss
    private static MBeanServer jbossMBeanServer = null;
    private static String JBOSS_SERVICE_CONTROLLER = "jboss.system:service=ServiceController";
    private static String JBOSS_TYPE_STOP = "org.jboss.system.ServiceMBean.stop";
    private static String JBOSS_ID_KEY = "id";
    private static final String JBOSS_DEFAULT_DOMAIN_NAME = "jboss";
    private static final String JBOSS_VFSZIP = "vfszip:";
    private static final String JBOSS_VFSFILE = "vfsfile:";
    private static final String JBOSS_EAR = ".ear";
    private static final String JBOSS_JAR = ".jar";
    private static final String JBOSS_WAR = ".war";
    private static final int JBOSS_VFSZIP_OFFSET = JBOSS_VFSZIP.length();
    private static final int JBOSS_VFSFILE_OFFSET = JBOSS_VFSFILE.length();
    private static final int JBOSS_EAR_OFFSET = JBOSS_EAR.length();
    private static final int JBOSS_TRIM_COUNT = 2;  // for stripping off the remaining '/}' chars

    // allow users to provide application info
    private static ApplicationResolver appResolver;
    private static boolean isAppResolverSet = false;

    /**
     * Default strategy for HelperContext creation.
     * Singleton.
     */
    private static final HelperContextResolver DEFAULT_HCR = new DefaultHelperContextResolver();

    /**
     * ADVANCED:
     * Used to set an ApplicationResolver instance that will be used to retrieve
     * info pertaining to a given application, such as the application name, in
     * the case where our logic fails.
     *
     * This method can be called once and only once per active server instance.
     *
     * @param aResolver the ApplicationResolver instance that will be used to retrieve
     *                  info pertaining to a given application.  Note that null is
     *                  considered a valid set operation.
     * @throws SDOException if more than one call is made to this method
     *         in an active server instance.
     */
    public static void setApplicationResolver(ApplicationResolver aResolver) {
        // we only allow one set operation per running server instance
        if (isApplicationResolverSet()) {
            throw SDOException.attemptToResetApplicationResolver();
        }
        appResolver = aResolver;
        isAppResolverSet = true;
    }

    /**
     * Indicates if a call to setApplicationResolver has been made.
     *
     * @return true if a prior call to setApplicationResolver has
     *         been made, false otherwise
     */
    public static boolean isApplicationResolverSet() {
        return isAppResolverSet;
    }

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
        this.identifier = GLOBAL_HELPER_IDENTIFIER;
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
    @Override
    public CopyHelper getCopyHelper() {
        return copyHelper;
    }

    /**
     * Return the DataFactory instance for this helper context.
     */
    @Override
    public DataFactory getDataFactory() {
        return dataFactory;
    }

    /**
     * Return the DataHelper instance for this helper context.
     */
    @Override
    public DataHelper getDataHelper() {
        return dataHelper;
    }

    /**
     * Return the EqualityHelper instance for this helper context.
     */
    @Override
    public EqualityHelper getEqualityHelper() {
        return equalityHelper;
    }

    /**
     * Return the TypeHelper instance for this helper context.
     */
    @Override
    public TypeHelper getTypeHelper() {
        return typeHelper;
    }

    /**
     * Return the XMLHelper instance for this helper context.
     */
    @Override
    public XMLHelper getXMLHelper() {
        return xmlHelper;
    }

    /**
     * Return the XSDHelper instance for this helper context.
     */
    @Override
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
        WeakHashMap<String, WeakReference<HelperContext>> currentMap = userSetHelperContexts.get(key);
        if(currentMap == null) {
            currentMap = new WeakHashMap<String, WeakReference<HelperContext>>();
            userSetHelperContexts.put(key, currentMap);
        }
        currentMap.put(((SDOHelperContext) value).getIdentifier(), new WeakReference(value));
    }

    /**
     * INTERNAL:
     * Retrieve the HelperContext for a given ClassLoader from the Thread
     * HelperContext map.
     *
     * @param key class loader
     * @return HelperContext for the given key if key exists in the map, otherwise null
     */
    private static HelperContext getUserSetHelperContext(String identifier, ClassLoader key) {
        if (key == null) {
            return null;
        }
        WeakHashMap<String, WeakReference<HelperContext>> currentMap = userSetHelperContexts.get(key);
        if(currentMap == null) {
            return null;
        }
        WeakReference<HelperContext> ref = currentMap.get(identifier);
        if(ref == null) {
            return null;
        }
        return ref.get();
    }

    /**
     * INTERNAL:
     * Remove a ClassLoader/HelperContext key/value pair from the Thread
     * HelperContext map. If there are multiple local helper contexts associated
     * with this ClassLoader, they will all be removed from the map.
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
     * INTERNAL
     * @param identifier the specific identifier of the HelperContext to be removed. "" for a Global helper
     * @param key the ClassLoader associated with the HelperContext to be removed
     */
    public static void removeHelperContext(String identifier, ClassLoader key) {
        if(key == null) {
            return;
        }
        WeakHashMap<String, WeakReference<HelperContext>> currentMap = userSetHelperContexts.get(key);
        if(currentMap != null) {
            currentMap.remove(identifier);
        }
    }

    /**
     * INTERNAL:
     * Return the helper context for a given key.  The key will either
     * be a ClassLoader or a String (representing an application name).
     * A new context will be created and put in the map if none exists
     * for the given key.
     *
     * The key is assumed to be non-null -  getDelegateKey should always
     * return either a string representing the application name (for WLS,
     * WAS and JBoss if available) or a class loader.  This is relevant
     * since 'putIfAbsent' will throw a null pointer exception if the
     * key is null.
     */
    public static HelperContext getHelperContext() {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        // check the map for contextClassLoader and return it if it exists
        HelperContext hCtx = getUserSetHelperContext(GLOBAL_HELPER_IDENTIFIER, contextClassLoader);
        if (hCtx != null) {
            return hCtx;
        }
        return getHelperContext(GLOBAL_HELPER_IDENTIFIER);
    }

    /**
     * Return the local helper context associated with the given identifier, or
     * create one if it does not already exist.  If identifier is an alias, the
     * value associated with it in the alias Map will be used as the identifier
     * value.
     *
     * @param identifier the identifier or alias to use for lookup/creation
     * @return HelperContext associated with identifier, or a new HelperContext
     * keyed on identifier if none eixsts
     */
    public static HelperContext getHelperContext(String identifier) {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        return getHelperContext(identifier, contextClassLoader);
    }

    /**
     * Return the local helper context with the given identifier, or create
     * one if it does not already exist.
     */
    public static HelperContext getHelperContext(String identifier, ClassLoader classLoader) {
        // if identifier is an alias, we need the actual id value
        ConcurrentMap<String, String> aliasEntries = getAliasMap();
        if (aliasEntries.containsKey(identifier)) {
            identifier = aliasEntries.get(identifier);
        }
        HelperContext helperContext = getUserSetHelperContext(identifier, classLoader);
        if (helperContext != null) {
            return helperContext;
        }
        CacheIdentityMap contextMap = getContextMap();
        helperContext = (HelperContext)contextMap.get(identifier);
        if (null == helperContext) {
            LOGGER.fine("helperContext not found.");
            helperContext = getHelperContextResolver().getHelperContext(identifier, classLoader);
            HelperContext existingContext = (HelperContext)contextMap.get(identifier);
            if (existingContext != null) {
                LOGGER.fine(String.format("contextMap already has context for id: %s. Existing one will be used.", identifier));
                helperContext = existingContext;
            } else {
                contextMap.put(identifier, helperContext, false, 0);
            }
        }
        return helperContext;
    }

    /**
     * Returns the map of helper contexts, keyed on Identifier, for the current application
     * @return
     */
    static CacheIdentityMap getContextMap() {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        String classLoaderName = contextClassLoader.getClass().getName();
        // get a MapKeyLookupResult instance based on the context loader
        MapKeyLookupResult hCtxMapKey = getContextMapKey(contextClassLoader, classLoaderName);
        // at this point we will have a loader and possibly an application name
        String appName = hCtxMapKey.getApplicationName();
        ClassLoader appLoader = hCtxMapKey.getLoader();
        // we will use the application name as the map key if set; otherwise we use the loader
        Object contextMapKey = appName != null ? appName : appLoader;
        CacheIdentityMap contextMap = helperContexts.get(contextMapKey);

        // handle possible redeploy
        // the following block only applies to WAS - hence the loader name check
        if (contextMap != null && appName != null && classLoaderName.contains(WAS_CLASSLOADER_NAME)) {
            // at this point there is an existing entry in the map - if the context is keyed
            // on application name we need to check to see if a redeployment occurred; in
            // that case the app names will match, but the class loaders will not
            ClassLoader currentAppLoader = appNameToClassLoaderMap.get(appName);
            if (currentAppLoader != null && currentAppLoader != appLoader) {
                // concurrency - use remove(key, value) to ensure we don't remove a newly added entry
                appNameToClassLoaderMap.remove(appName, currentAppLoader);
                helperContexts.remove(appName, contextMap);
                contextMap = null;
            }
        }

        // may need to add a new entry
        if (null == contextMap) {
            contextMap = new CacheIdentityMap(helperContextsMaxSize, null, null, false);
            // use putIfAbsent to avoid concurrent entries in the map
            CacheIdentityMap existingMap = helperContexts.putIfAbsent(contextMapKey, contextMap);
            if (existingMap != null) {
                // if a new entry was just added, use it instead of the one we just created
                contextMap = existingMap;
            } else if (appName != null) {
                // add an appName/appLoader pair to the appNameToClassLoader map
                appNameToClassLoaderMap.put(appName, appLoader);

                if (classLoaderName.contains(WLS_CLASSLOADER_NAME)) {
                    // add a loader/context pair to the helperContexts map to handle case where appName
                    // is no longer available, but the loader from a previous lookup is being used
                    helperContexts.put(appLoader, contextMap);
                    // add a notification listener to handle redeploy
                    addWLSNotificationListener(appName);
                } else if (classLoaderName.contains(JBOSS_CLASSLOADER_NAME)) {
                    // add a notification listener to handle redeploy - the listener will only be added once
                    addJBossNotificationListener();
                }
            }
        }
        return contextMap;
    }

    /**
     * Replaces the provided  helper context in the map of identifiers to
     * helper contexts for this application. ctx.getIdentifier()  will be
     * used to obtain the identifier value. If identifier is a key in the
     * the alias Map, i.e. was previously set as alias, the corresponding
     * entry will be removed from the alias Map.
     *
     * @param ctx the HelperContext to be added to the context Map for
     * the current application
     */
    public static void putHelperContext(HelperContext ctx) {
        String identifier = ((SDOHelperContext) ctx).getIdentifier();
        if (GLOBAL_HELPER_IDENTIFIER.equals(identifier)) {
            // The global HelperContext cannot be replaced
            return;
        }
        getContextMap().put(identifier, ctx, false, 0);
        // identifier may have been an alias at one point
        getAliasMap().remove(identifier);
    }

    /**
     * ADVANCED:
     * Remove the HelperContext for the application associated with a
     * given key, if it exists in the map.
     */
    private static void resetHelperContext(String key) {
        // remove entry from helperContext map
        boolean successHc = removeAppFromMap(helperContexts, key, false);
        // remove app's helperContextResolver
        boolean successHcr = removeAppFromMap(HELPER_CONTEXT_RESOLVERS, key, true);
        if (LOGGER.isLoggable(Level.WARNING) && !successHc && !successHcr) {
            LOGGER.warning("No entries found in maps for application:" + key);
        }

        // remove the appName entry in the appNameToClassLoader map
        appNameToClassLoaderMap.remove(key);
        // remove static SDOWrapperType instances bound to this application
        SDO_WRAPPER_TYPES.remove(key);
        // remove the alias map for this app
        aliasMap.remove(key);
    }

    /**
     * Trying to remove entry for a given app the provided map.
     *
     * @param map from which app value should be removed
     * @param appName application name
     * @param removeDefaultClassloader whether to try removing the default classloader
     * @return true if any removal took place
     */
    private static boolean removeAppFromMap(Map map, String appName, boolean removeDefaultClassloader) {
        boolean result = map.remove(appName) != null;
        // there may be a loader/context pair to remove
        ClassLoader appLoader = appNameToClassLoaderMap.get(appName);
        if (appLoader != null) {
            result = result | map.remove(appLoader) != null;
        } else if (removeDefaultClassloader) {
            // try with Thread ContextClassLoader
            result = result | map.remove(Thread.currentThread().getContextClassLoader()) != null;
        }
        return result;
    }

    /**
     * INTERNAL:
     * Return the key to be used for Map lookups based in the current thread's
     * context loader.  The returned value will be the application name (if
     * available) or the context loader.
     *
     */
    private static Object getMapKey() {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        String classLoaderName = contextClassLoader.getClass().getName();
        // get a MapKeyLookupResult instance based on the context loader
        MapKeyLookupResult hCtxMapKey = getContextMapKey(contextClassLoader, classLoaderName);
        // at this point we will have a loader and possibly an application name
        String appName = hCtxMapKey.getApplicationName();
        ClassLoader appLoader = hCtxMapKey.getLoader();
        // we will use the application name as the map key if set; otherwise we use the loader
        return appName != null ? appName : appLoader;
    }

    /**
     * Bug #506919
     * Retrieves the application name for current {@link ClassLoader}.
     *
     * @param classLoader the ClassLoader to use for searching application name
     * @return the application name
     */
    private static String getApplicationName(ClassLoader classLoader) {
        String classLoaderName = classLoader.getClass().getName();
        // get a MapKeyLookupResult instance based on the context loader
        MapKeyLookupResult hCtxMapKey = getContextMapKey(classLoader, classLoaderName);
        // at this point we will have a loader and possibly an application name
        String applicationName = hCtxMapKey.getApplicationName();
        return applicationName != null? applicationName : "DEFAULT";
    }

    /**
     * INTERNAL:
     * This method will return the MapKeyLookupResult instance to be used to
     * store/retrieve the global helper context for a given application.
     *
     * @return MapKeyLookupResult wrapping the application name for WebLogic and WebSphere, the archive file
     *         name for JBoss - if available; otherwise a MapKeyLookupResult
     *         wrapping Thread.currentThread().getContextClassLoader()
     */
    private static MapKeyLookupResult getContextMapKey(ClassLoader classLoader, String classLoaderName) {
        // Helper contexts in WebLogic server will be keyed on application name if available
        if (classLoaderName.contains(WLS_CLASSLOADER_NAME)) {
            if (null == applicationAccessWLS) {
                synchronized (SDOHelperContext.class) {
                    if (applicationAccessWLS == null) {
                         applicationAccessWLS = new ApplicationAccessWLS();
                    }
                }
            }
            Object appName = applicationAccessWLS.getApplicationName(classLoader);
            if (appName != null) {
                return new MapKeyLookupResult(appName.toString(), classLoader);
            }

            Object executeThread = getExecuteThread();
            if (executeThread != null) {
                try {
                    Method getMethod = PrivilegedAccessHelper.getPublicMethod(executeThread.getClass(), WLS_APPLICATION_NAME_GET_METHOD_NAME, WLS_PARAMETER_TYPES, false);
                    appName = PrivilegedAccessHelper.invokeMethod(getMethod, executeThread);
                } catch (Exception e) {
                    throw SDOException.errorInvokingWLSMethodReflectively(WLS_APPLICATION_NAME_GET_METHOD_NAME, WLS_EXECUTE_THREAD, e);
                }
            }
            // if ExecuteThread is null or doesn't return the app name, attempt
            // to use the user-set ApplicationResolver (if set)
            if (appName == null && appResolver != null) {
                appName = appResolver.getApplicationName();
            }
            // use the application name if set, otherwise key on the class loader
            if (appName != null) {
                return new MapKeyLookupResult(appName.toString(), classLoader);
            }
            // couldn't get the application name, so default to the context loader
            return new MapKeyLookupResult(classLoader);
        }
        // Helper contexts in WebSphere server will be keyed on application name if available
        if (classLoaderName.contains(WAS_CLASSLOADER_NAME)) {
            return getContextMapKeyForWAS(classLoader);
        }
        // Helper contexts in JBoss server will be keyed on archive file name if available
        if (classLoaderName.contains(JBOSS_CLASSLOADER_NAME)) {
            return getContextMapKeyForJBoss(classLoader);
        }
        // at this point we will default to the context loader
        return new MapKeyLookupResult(classLoader);
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
     * This method should only be called when running in an active WLS instance.
     *
     * @param applicationName
     */
    private static void addWLSNotificationListener(String applicationName) {
        try {
            if (getWLSMBeanServer() != null) {
                ObjectName service = new ObjectName(WLS_SERVICE_KEY);
                ObjectName serverRuntime = (ObjectName) wlsMBeanServer.getAttribute(service, WLS_SERVER_RUNTIME);
                ObjectName[] appRuntimes = (ObjectName[]) wlsMBeanServer.getAttribute(serverRuntime, WLS_APP_RUNTIMES);
                for (int i=0; i < appRuntimes.length; i++) {
                    try {
                        ObjectName appRuntime = appRuntimes[i];
                        Object appName = wlsMBeanServer.getAttribute(appRuntime, WLS_APPLICATION_NAME);
                        Object appVersion = wlsMBeanServer.getAttribute(appRuntime, WLS_APPLICATION_VERSION);
                        String appIdentifier = null;
                        if (appName != null) {
                            if (appVersion != null) {
                                appIdentifier = appName.toString() + "#" + appVersion.toString();
                            } else {
                                appIdentifier = appName.toString();
                            }
                            if (appIdentifier != null && appIdentifier.equals(applicationName)) {
                                wlsMBeanServer.addNotificationListener(appRuntime, new MyNotificationListener(applicationName, WLS_IDENTIFIER), null, null);
                                break;
                            }
                        }
                    } catch (Exception ex) {
                        if (LOGGER.isLoggable(Level.FINE)) {
                            LOGGER.fine("Failed to retrieve application name in runtime " + appRuntimes[i].toString() + ":\n" + Helper.printStackTraceToString(ex));
                        }
                    }
                }
            }
        } catch (Exception x) {
            if (LOGGER.isLoggable(Level.WARNING)) {
                LOGGER.warning("Failed to add notification listener for application " + applicationName + ":\n" + Helper.printStackTraceToString(x));
            }
        }
    }

    /**
     * INTERNAL:
     * The listener will handle application re-deployment.  If any errors occur, we will
     * fail silently, i.e. the listener will not be added.  Since we cannot register for
     * notifications for a particular application, and hence recieve notifications for
     * all apps that are redeployed, we only add the listener once - each time we receive
     * notification we will check the map and remove the corresponding entry if it exists.
     *
     * This method should only be called when running in an active JBoss instance.
     *
     */
    private static void addJBossNotificationListener() {
        if (jbossMBeanServer == null) {
            List<MBeanServer> mbeanServers = MBeanServerFactory.findMBeanServer(null);
            for (MBeanServer server : mbeanServers) {
                if (server.getDefaultDomain().equals(JBOSS_DEFAULT_DOMAIN_NAME)) {
                    jbossMBeanServer = server;
                    try {
                        jbossMBeanServer.addNotificationListener(new ObjectName(JBOSS_SERVICE_CONTROLLER), new MyNotificationListener(JBOSS_IDENTIFIER), new MyNotificationFilter(), null);
                    } catch (Exception e) {}
                    break;
                }
            }
        }
    }

    /**
     * Getter for HelperContextResolver
     * @return actual strategy
     */
    public static HelperContextResolver getHelperContextResolver() {
        Object key = getMapKey();
        HelperContextResolver result = HELPER_CONTEXT_RESOLVERS.get(key);
        if (result == null) {
            result = DEFAULT_HCR;
            HELPER_CONTEXT_RESOLVERS.putIfAbsent(key, result);
        }
        return result;
    }

    /**
     * Method allows dynamically change HelperContext creation strategy.
     *
     * @param helperContextResolver on this object {@link HelperContextResolver#getHelperContext(String, ClassLoader)} will be called.
     *                              If it is null - then default strategy will be set.
     */
    public static void setHelperContextResolver(Object helperContextResolver) {
        Object key = getMapKey();
        if (helperContextResolver == null)
            HELPER_CONTEXT_RESOLVERS.put(key, DEFAULT_HCR);
        else
            HELPER_CONTEXT_RESOLVERS.put(key, new ReflectionHelperContextResolver(helperContextResolver));
    }

    /**
     * Method allows dynamically change HelperContext creation strategy.
     *
     * @param helperContextResolver strategy to be used. If it is null - then default strategy will be set.
     */
    public static void setHelperContextResolver(HelperContextResolver helperContextResolver) {
        Object key = getMapKey();
        if (helperContextResolver == null)
            HELPER_CONTEXT_RESOLVERS.put(key, DEFAULT_HCR);
        else
            HELPER_CONTEXT_RESOLVERS.put(key, helperContextResolver);
    }

    /**
     * Removes HelperContextResolver for the current application.
     * Application is resolved based on applicationName or classLoader.
     */
    public static void removeHelerContextResolver() {
        HELPER_CONTEXT_RESOLVERS.remove(getMapKey());
    }

    /**
     * INTERNAL:
     * This class will be handed in as a parameter when adding a JBoss notification listener.
     * The purpose of this class is to restrict notifications to "stop", i.e. we don't
     * care about "destroy", "start" etc.
     *
     */
    static class MyNotificationFilter extends NotificationFilterSupport {
        MyNotificationFilter() {
            super.enableType(JBOSS_TYPE_STOP);
        }
    }

    /**
     * INTERNAL:
     * Inner class used to catch application re-deployment.  Upon notification of this event,
     * the helper context for the given application will be removed from the helper context
     * to application map.  This method will also remove the corresponding entry in the
     * application name to application loader map if necessary.
     *
     * For WebLogic, 'appName' will be set; we will regester a listener for each application
     * name we key a context on, and will only receive a notification if a particular app
     * is redeployed.  Upon notification we will remove the context entry for 'appName'.
     *
     * For JBoss, 'appName' will not be set; we will only register the listener once,
     * and each time we're notified of an application redeployment, we will reset the
     * associated helper context if one exists.
     *
     */
    private static class MyNotificationListener implements NotificationListener {
        int server;
        String appName;

        /**
         * This is the default constructor - typically used when running in an
         * actove JBoss instance.
         *
         * @param server
         */
        public MyNotificationListener(int server) {
            this.server = server;
        }

        /**
         * This constructor will set 'appName' - typically used when running in
         * an active WebLogic instance.
         *
         * @param appName
         * @param server
         */
        public MyNotificationListener(String appName, int server) {
            this.server = server;
            this.appName = appName;
        }

        @Override
        public void handleNotification(Notification notification, Object handback) {
            switch (server) {
            case 0: {  // handle WebLogic notification
                if (notification instanceof AttributeChangeNotification) {
                    AttributeChangeNotification acn = (AttributeChangeNotification) notification;
                    if (acn.getAttributeName().equals(WLS_ACTIVE_VERSION_STATE)) {
                        if (acn.getNewValue().equals(0)) {
                            resetHelperContext(appName);
                        }
                    }
                }
                break;
            }
            case 1: {  // handle JBoss notification
                // assumes 'user data' is an ObjectName containing an 'id' property which indicates the archive file name
                appName = getApplicationNameFromJBossClassLoader(((ObjectName) notification.getUserData()).getKeyProperty(JBOSS_ID_KEY));
                // we receive notifications for all service stops in JBoss;  only call reset if necessary
                if (helperContexts.containsKey(appName)) {
                    resetHelperContext(appName);
                }
                break;
            }
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
        MapKeyLookupResult hCtxMapKey = getContextMapKey(contextClassLoader, contextClassLoader.getClass().getName());

        String appName = hCtxMapKey.getApplicationName();
        ClassLoader appLoader = hCtxMapKey.getLoader();
        Object contextMapKey = appName != null ? appName : appLoader;

        CacheIdentityMap contexts = helperContexts.get(contextMapKey);
        if (contexts == null) {
            contexts = new CacheIdentityMap(helperContextsMaxSize, null, null, false);
            CacheIdentityMap existingContexts = helperContexts.putIfAbsent(contextMapKey, contexts);
            if (existingContexts != null) {
                contexts = existingContexts;
            } else if (appName != null) {
                appNameToClassLoaderMap.put(appName, appLoader);
            }
        }
        this.identifier = GLOBAL_HELPER_IDENTIFIER;
        contexts.put(GLOBAL_HELPER_IDENTIFIER, this, false, 0);
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
     * Attempt to return a MapKeyLookupResult instance wrapping the application name and
     * application loader based on a given WAS classloader.  Here we will traverse up the
     * loader hierarchy looking for the top-most application loader.
     *
     * For WAS, the application loader's toString (and those of it's children) will
     * contain "[app:".
     *
     * @param loader
     * @return a MapKeyLookupResult instance wrapping application name/loader if
     *         successfully retrieved (i.e. at least one loader exists in the
     *         hierarchy with toString containing "[app:"), or a MapKeyLookupResult
     *         instance wrapping the given loader if not found
     */
    private static MapKeyLookupResult getContextMapKeyForWAS(ClassLoader loader) {
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
            return new MapKeyLookupResult(applicationName, applicationLoader);

        }
        // at this point we don't know the application name so the loader will be the key
        return new MapKeyLookupResult(applicationLoader);
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
     * 1 - A given toString may contain both .ear and .jar, or .ear and .war, i.e.
     *     "{vfszip:/.../xxx.ear/.../xxx.war/}".  In this case we want to return
     *     xxx.ear as the application name.
     * 2 - A given toString will end in '/}'.
     * 3 - A toString containing the application name will have one of "vfszip:" or "vfsfile:".
     *
     * @param loaderToString the toString of the loader being processed
     * @return application name (archive file name) if successfully retrieved (i.e. loader
     *         exists in the hierarchy with toString containing "vfszip:" or "vfsfile:")
     *         or null
     */
    private static String getApplicationNameFromJBossClassLoader(String loaderToString) {
        String appNameSegment = null;
        int idx;
        // handle "vfszip:<archive-file-name>"
        if ((idx = loaderToString.indexOf(JBOSS_VFSZIP)) != -1) {
            appNameSegment = loaderToString.substring(idx + JBOSS_VFSZIP_OFFSET, loaderToString.length() - JBOSS_TRIM_COUNT);
            // handle case where the string contains both .ear and .war (remove the .war portion)
            if ((appNameSegment.indexOf(JBOSS_WAR) != -1) && (appNameSegment.indexOf(JBOSS_EAR) != -1)) {
                appNameSegment = appNameSegment.substring(0, appNameSegment.indexOf(JBOSS_EAR) + JBOSS_EAR_OFFSET);
            }
            // handle case where the string contains both .ear and .jar (remove the .jar portion)
            else if ((appNameSegment.indexOf(JBOSS_JAR) != -1) && (appNameSegment.indexOf(JBOSS_EAR) != -1)) {
                appNameSegment = appNameSegment.substring(0, appNameSegment.indexOf(JBOSS_EAR) + JBOSS_EAR_OFFSET);
            }
        }
        // handle "vfsfile:<archive-file-name>"
        else if ((idx = loaderToString.indexOf(JBOSS_VFSFILE)) != -1) {
            appNameSegment = loaderToString.substring(idx + JBOSS_VFSFILE_OFFSET, loaderToString.length() - JBOSS_TRIM_COUNT);
        }
        return appNameSegment != null ? new File(appNameSegment).getName() : null;
    }

    /**
     * Attempt to return a MapKeyLookupResult instance wrapping the archive file name and
     * application loader based on a given JBoss classloader.  Here we will traverse up the
     * loader hierarchy looking for the top-most application loader.
     *
     * @param loader
     * @return a MapKeyLookupResult instance wrapping archive file name/loader if
     *         successfully retrieved (i.e. at least one loader exists in the
     *         hierarchy with toString containing containing "vfszip:" or "vfsfile:"),
     *         or a MapKeyLookupResult instance wrapping the given loader if not found
     */
    private static MapKeyLookupResult getContextMapKeyForJBoss(ClassLoader loader) {
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
                archiveFileName = getApplicationNameFromJBossClassLoader(applicationLoader.toString());
                break;
            }
            // move up and try again
            loader = parent;
            i++;
        }
        // if we found the archive file name, use it as the key
        if (archiveFileName != null) {
            return new MapKeyLookupResult(archiveFileName, applicationLoader);
        }
        // at this point we don't know the archive file name so the loader will be the key
        return new MapKeyLookupResult(applicationLoader);
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

    /**
     * Return true if a HelperContext corresponding to this identifier or alias
     * already exists, else false.  If identifer is an alias, the corresponding
     * value in the alias Map will be used as the identifier for the lookup.
     *
     * @param identifier the alias or identifier used to lookup a helper context
     * @return true if an entry exists in the helper context map for identifier (or
     * the associated identifier value if identifier is an alias), false otherwise.
     */
    public static boolean hasHelperContext(String identifier) {
        String id = identifier;
        Object appKey = getMapKey();
        // if identifier is an alias, we need the actual id value
        ConcurrentMap<String, String> aliasEntries = getAliasMap(appKey);
        if (aliasEntries.containsKey(identifier)) {
            id = aliasEntries.get(identifier);
        }
        // now check the Map of user set identifiers to helperContexts
        WeakHashMap<String, WeakReference<HelperContext>> userSetMap = userSetHelperContexts.get(appKey);
        if (userSetMap != null && userSetMap.containsKey(id)) {
            return true;
        }

        // lastly, check the Map of identifiers to helperContexts
        CacheIdentityMap contextMap = helperContexts.get(appKey);
        return (contextMap != null && contextMap.containsKey(id));
    }

    /**
     * Add an alias to identifier pair to the alias Map for the current
     * application.
     *
     * @param identifier assumed to be a key in the helper context Map
     * @param alias the alias to be associated with identifier
     */
    public static void addAlias(String identifier, String alias) {
        getAliasMap().put(alias, identifier);
    }

    /**
     * INTERNAL:
     * Returns the map of alias' to identifiers for the current application.
     *
     * @return Map of alias' to identifiers for the current application
     */
    private static ConcurrentMap<String, String> getAliasMap() {
        return getAliasMap(getMapKey());
    }

    /**
     * INTERNAL:
     * Returns the map of alias' to identifiers for the current application.
     *
     * @param mapKey application name or classloader used to lookup the alias map
     * @return Map of alias' to identifiers for the current application
     */
    private static ConcurrentMap<String, String> getAliasMap(Object mapKey) {
        ConcurrentHashMap<String, String> alias = aliasMap.get(mapKey);

        // may need to add a new entry
        if (null == alias) {
            alias = new ConcurrentHashMap<String, String>();
            // use putIfAbsent to avoid concurrent entries in the map
            ConcurrentHashMap<String, String> existingMap = aliasMap.putIfAbsent(mapKey, alias);
            if (existingMap != null) {
                // if a new entry was just added, use it instead of the one we just created
                alias = existingMap;
            }
        }
        return alias;
    }

    /**
     * Lazily initialize the Map of user properties.
     */
    private Map<String, Object> getProperties() {
        if (properties == null) {
            properties = new HashMap<String, Object>();
        }
        return properties;
    }

    /**
     * Add a name/value pair to the properties Map.  If name is
     * null, nothing will be done. If value is null, the entry
     * in the Map will be removed (if an entry exists for name).
     *
     * @param name the name of the property
     * @param value the value of the property
     */
    public void setProperty(String name, Object value) {
        // if the key is null there is nothing to do
        if (name == null) {
            return;
        }
        // if value is null, remove the entry
        if (value == null) {
            getProperties().remove(name);
        } else {
            // put the name/value pair in the map
            getProperties().put(name, value);
        }
    }

    /**
     * Return the value stored in the properties Map for a given
     * name, or null if an entry for name does not exist.
     *
     * @param name the name of the property to be returned
     * @return the value associated with name, or null
     */
    public Object getProperty(String name) {
        return getProperties().get(name);
    }

    /**
     * Indicates whether strict type checking is enabled.
     *
     * <p>
     * If strict type checking is enabled then {@link Type#getInstanceClass()}
     * interface is checked whether it contains getters for all the properties
     * of the {@link Type} upon initialization of the {@link Type}.
     * If any getter is missing then the interface is ignored and
     * {@link Type#getInstanceClass()} will return {@code null}.
     * </p>
     *
     * <p>
     * The getters are not checked if the strict type checking is disabled.
     * </p>
     *
     * @return boolean value
     */
    public boolean isStrictTypeCheckingEnabled() {
        return this.isStrictTypeCheckingEnabled;
    }

    /**
     * Controls type checking strictness.
     *
     * See {@link #isStrictTypeCheckingEnabled()} for more details.
     *
     * @param enabled new value ({@code true} to enable the strict validation)
     */
    public void setStrictTypeCheckingEnabled(boolean enabled) {
        this.isStrictTypeCheckingEnabled = enabled;
    }

    /**
     * Strategy for {@link HelperContext} creation.
     *
     * If is not set explicitly the default one is used.
     */
    public interface HelperContextResolver {

        /**
         * Should return instance of {@link HelperContext}.
         *
         * @param id          the unique label for this HelperContext
         * @param classLoader this class loader will be used to find static instance classes
         * @return instance of {@link HelperContext}
         */
        HelperContext getHelperContext(String id, ClassLoader classLoader);
    }

    /**
     * Default implementation of {@link HelperContextResolver}
     */
    private static class DefaultHelperContextResolver implements HelperContextResolver {

        @Override
        public HelperContext getHelperContext(String id, ClassLoader classLoader) {
            LOGGER.fine(String.format("DefaultHelperContextResolver: new HelperContext will be created for id: %s and classLoader: %s",
                    id, classLoader));
            return new SDOHelperContext(id, classLoader);
        }
    }

    /**
     * Implementation of {@link HelperContextResolver} which uses reflection to get {@link HelperContext} instance.
     */
    private static class ReflectionHelperContextResolver implements HelperContextResolver {

        private Method method;
        private Object target;

        /**
         * 'getHelperContext' method will be called on the provided target object.
         *
         * @param target object which has 'getHelperContext(String, Classloader)' method. In case it's not - RuntimeException will be thrown
         */
        public ReflectionHelperContextResolver(Object target) {
            this.target = target;
            try {
                this.method = findMethod(target.getClass(), "getHelperContext", new Class<?>[]{String.class, ClassLoader.class});
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
                throw new IllegalStateException(e);
            }
        }

        private Method findMethod(Class<?> clazz, String methodName, Class<?>[] params) throws java.security.PrivilegedActionException, NoSuchMethodException {
            if (PrivilegedAccessHelper.shouldUsePrivilegedAccess())
                return AccessController.doPrivileged(new PrivilegedGetMethod(clazz, methodName, params, true));
            return PrivilegedAccessHelper.getMethod(clazz, methodName, params, true);
        }

        /**
         * Calling 'getHelperContext' method on provided target object
         *
         * @param id          the unique label for this HelperContext
         * @param classLoader this class loader will be used to find static instance classes
         * @return in case if something goes wrong with method invocation and Exception is thrown - default implementation of
         * {@link HelperContextResolver} will be called
         */
        @Override
        public HelperContext getHelperContext(String id, ClassLoader classLoader) {
            try {
                return (HelperContext) method.invoke(target, id, classLoader);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
            }
            return DEFAULT_HCR.getHelperContext(id, classLoader);
        }
    }

    /**
     * Returns the {@link SDOWrapperType} instances for current application
     */
    public static Map<SDOWrapperTypeId,SDOWrapperType> getWrapperTypes() {
        return SDO_WRAPPER_TYPES.get(getApplicationName(Thread.currentThread().getContextClassLoader()));
    }

    /**
     * Replaces the {@link SDOWrapperType} instances for current application with the ones passed as an argument
     *
     * @param wrapperTypes the SDOWrapperType instances to use for current application
     * @return SDOWrapperType instances configured for current application
     */
    public static Map<SDOWrapperTypeId,SDOWrapperType> putWrapperTypes(Map<SDOWrapperTypeId,SDOWrapperType> wrapperTypes) {
        return SDO_WRAPPER_TYPES.put(getApplicationName(Thread.currentThread().getContextClassLoader()), wrapperTypes);
    }
}
