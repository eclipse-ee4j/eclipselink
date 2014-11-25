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

    /** Instance of ApplicationAccess or null if not initialized **/
    private Object applicationAccessInstance;

    private Method getApplicationNameMethod;
    private Method getApplicationVersionMethod;

    /** Instance of CIC (Component Invocation Context) or null if not initialized **/
    private Object cicInstance;

    private Method getApplicationId;

    /**
     * Create and initialize.
     */
    public ApplicationAccessWLS() {
        try {
            // Try initializing using CIC
            initUsingCic();
        } catch (Exception e) {
            cicInstance = null;
            try {
                // Init using CIC failed, try to init using ApplicationAccess
                initUsingApplicationAccess();
            } catch (Exception ex) {
                applicationAccessInstance = null;
            }
        }
    }

    /**
     * Gets a unique application name. Uses CIC if possible. If not using ApplicationAccess.
     *
     * @param classLoader   the class loader
     * @return unique application name.
     */
    public String getApplicationName(ClassLoader classLoader) {
        // If CIC initialization was successful use CIC
        if (cicInstance != null) {
            try {
                return getAppNameUsingCic();
            } catch (Exception e) {
                return null;
            }
        }

        // If CIC initialization was not successful and ApplicationAccess initialization was successful
        // use ApplicationAccess
        if (applicationAccessInstance != null) {
            try {
                return getAppNameUsingApplicationAccess(classLoader);
            } catch (Exception e) {
                return null;
            }
        }

        // Both CIC and ApplicationAccess are failed to initialize
        return null;
    }

    /**
     * Initializes CIC.
     */
    protected void initUsingCic() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // Get component invocation manager
        final Class cicManagerClass = PrivilegedAccessHelper.getClassForName("weblogic.invocation.ComponentInvocationContextManager");
        final Method getInstance = PrivilegedAccessHelper.getDeclaredMethod(cicManagerClass, "getInstance", new Class[] {});
        final Object cicManager = PrivilegedAccessHelper.invokeMethod(getInstance, cicManagerClass);

        // Get component invocation context
        final Method getCurrentCic = PrivilegedAccessHelper.getMethod(cicManagerClass, "getCurrentComponentInvocationContext", new Class[] {}, true);
        cicInstance = PrivilegedAccessHelper.invokeMethod(getCurrentCic, cicManager);

        // Get getApplicationId method
        getApplicationId = PrivilegedAccessHelper.getDeclaredMethod(cicInstance.getClass(), "getApplicationId", new Class[] {});
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
     * Gets unique application name using CIC. Calls cicInstance.getApplicationId().
     */
    private String getAppNameUsingCic() throws InvocationTargetException, IllegalAccessException {
        return (String) PrivilegedAccessHelper.invokeMethod(getApplicationId, cicInstance);
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
