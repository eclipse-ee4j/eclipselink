/*******************************************************************************
 * Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Dmitry Kornilov - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.sdo.helper;

import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * INTERNAL:
 *
 * This class using WLS classed to get unique application ID.
 * WLS classes are accessed using reflection. It tries to use ComponentInvocationContext (CIC) first, if no success it
 * tries to use ApplicationAccess.
 *
 * @author Dmitry Kornilov
 * @since EclipseLink 2.6.0
 */
public class ApplicationAccessWLS {
    private static final Logger LOGGER = Logger.getLogger(ApplicationAccessWLS.class.getName());

    /** ApplicationID cache **/
    private final Map<ClassLoader, String> appNames = Collections.synchronizedMap(new WeakHashMap<ClassLoader, String>());

    /** Instance of ApplicationAccess or null if not initialized **/
    private Object applicationAccessInstance;

    private Method getApplicationNameMethod;
    private Method getApplicationVersionMethod;

    /** Instance of CIC (Component Invocation Context) or null if not initialized **/
    private Object cicManagerInstance;
    
    private Method getCurrentCicMethod;
    private Method getApplicationIdMethod;

    /**
     * Create and initialize.
     */
    public ApplicationAccessWLS() {
        try {
            // Try initializing using CIC
            initUsingCic();
            LOGGER.fine("ApplicationAccessWLS initialized using ComponentInvocationContext.");
        } catch (Exception e) {
            LOGGER.log(Level.FINE, "Error initializing ApplicationAccessWLS using ComponentInvocationContext. " +
                    "Trying to initialize it using ApplicationAccess.", e);

            cicManagerInstance = null;
            try {
                // Init using CIC failed, try to init using ApplicationAccess
                initUsingApplicationAccess();
                LOGGER.fine("ApplicationAccessWLS initialized using ApplicationAccess.");
            } catch (Exception ex) {
                LOGGER.log(Level.FINE, "Error initializing ApplicationAccessWLS using ApplicationAccess.", ex);
                applicationAccessInstance = null;
            }
        }
    }

    /**
     * Gets a unique application name.
     *
     * @param classLoader   the class loader
     * @return unique application name.
     */
    public String getApplicationName(ClassLoader classLoader) {
        if (appNames.containsKey(classLoader)) {
            return appNames.get(classLoader);
        } else {
            synchronized (appNames) {
                if (appNames.containsKey(classLoader)) {
                    return appNames.get(classLoader);
                } else {
                    final String appName = getApplicationNameInternal(classLoader);
                    appNames.put(classLoader, appName);
                    return appName;
                }
            }
        }
    }

    /**
     * Internal method to get a unique application name.
     * Uses CIC if possible. If not using ApplicationAccess.
     *
     * @param classLoader   the class loader
     * @return unique application name.
     */
    private String getApplicationNameInternal(ClassLoader classLoader) {
        // If CIC initialization was successful use CIC
        if (cicManagerInstance != null) {
            try {
                return getAppNameUsingCic();
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "ApplicationAccessWLS.getApplicationName error in getAppNameUsingCic.", e);
                return null;
            }
        }

        // If CIC initialization was not successful and ApplicationAccess initialization was successful
        // use ApplicationAccess
        if (applicationAccessInstance != null) {
            try {
                return getAppNameUsingApplicationAccess(classLoader);
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "ApplicationAccessWLS.getApplicationName error in getAppNameUsingApplicationAccess.", e);
                return null;
            }
        }

        // Both CIC and ApplicationAccess are failed to initialize
        LOGGER.fine("ApplicationAccessWLS: null applicationName returned.");
        return null;
    }

    /**
     * Initializes CIC.
     */
    protected void initUsingCic() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // Get component invocation manager
        final Class cicManagerClass = PrivilegedAccessHelper.getClassForName("weblogic.invocation.ComponentInvocationContextManager");
        final Method getInstance = PrivilegedAccessHelper.getDeclaredMethod(cicManagerClass, "getInstance", new Class[] {});
        cicManagerInstance = PrivilegedAccessHelper.invokeMethod(getInstance, cicManagerClass);

        // Get component invocation context
        getCurrentCicMethod = PrivilegedAccessHelper.getMethod(cicManagerClass, "getCurrentComponentInvocationContext", new Class[] {}, true);

        final Class cicClass = PrivilegedAccessHelper.getClassForName("weblogic.invocation.ComponentInvocationContext");
        getApplicationIdMethod = PrivilegedAccessHelper.getDeclaredMethod(cicClass, "getApplicationId", new Class[] {});
    }

    /**
     * Initializes ApplicationAccess.
     */
    protected void initUsingApplicationAccess() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final Class applicationAccessClass = PrivilegedAccessHelper.getClassForName("weblogic.application.ApplicationAccess");
        final Method getApplicationAccessMethod = PrivilegedAccessHelper.getDeclaredMethod(applicationAccessClass, "getApplicationAccess", new Class[] {});
        applicationAccessInstance = PrivilegedAccessHelper.invokeMethod(getApplicationAccessMethod, applicationAccessClass);

        final Class [] methodParameterTypes = new Class[] {ClassLoader.class};
        getApplicationNameMethod = PrivilegedAccessHelper.getMethod(applicationAccessClass, "getApplicationName", methodParameterTypes, true);
        getApplicationVersionMethod = PrivilegedAccessHelper.getMethod(applicationAccessClass, "getApplicationVersion", methodParameterTypes, true);
    }

    /**
     * Gets unique application name using CIC. Calls cicInstance.getApplicationIdMethod().
     */
    private String getAppNameUsingCic() throws InvocationTargetException, IllegalAccessException {
        final Object cicInstance = PrivilegedAccessHelper.invokeMethod(getCurrentCicMethod, cicManagerInstance);
        return (String) PrivilegedAccessHelper.invokeMethod(getApplicationIdMethod, cicInstance);
    }

    /**
     * Gets unique application name using ApplicationAccess. Calls its getApplicationName() and getApplicationVersion methods.
     */
    private String getAppNameUsingApplicationAccess(ClassLoader classLoader) throws InvocationTargetException, IllegalAccessException {
        final Object[] parameters = new Object[] {classLoader};
        final String appName = (String) PrivilegedAccessHelper.invokeMethod(getApplicationNameMethod, applicationAccessInstance, parameters);
        if (appName != null) {
            final String appVersion = (String) PrivilegedAccessHelper.invokeMethod(getApplicationVersionMethod, applicationAccessInstance, parameters);
            if (appVersion != null) {
                return appName + "#" + appVersion;
            }
        }

        return appName;
    }
}
