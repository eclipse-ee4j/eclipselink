/*
 * Copyright (c) 2015, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation
package org.eclipse.persistence.platform.server.wls;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.sessions.DatabaseSession;

public class WebLogic_12_Platform extends WebLogic_10_Platform {

    private static final ContextHelper ctxHelper = ContextHelper.getContextHelper();

    public WebLogic_12_Platform(DatabaseSession newDatabaseSession) {
        super(newDatabaseSession);
    }

    @Override
    public boolean usesPartitions() {
        return ctxHelper != null;
    }

    @Override
    public String getPartitionID() {
        return ctxHelper != null ? ctxHelper.getPartitionID() : super.getPartitionID();
    }

    public String getPartitionName() {
        return ctxHelper != null ? ctxHelper.getPartitionName() : "GLOBAL";
    }

    public boolean isGlobalRuntime() {
        return ctxHelper == null || ctxHelper.isGlobalRuntime();
    }

    private static final class ContextHelper {

        /**
         * Instance of CIC (Component Invocation Context) or null if not
         * initialized.
         */
        private Object cicManagerInstance;
        private Method getCurrentCicMethod;
        private Method getPartitionIdMethod;
        private Method getPartitionNameMethod;
        private Method isGlobalRuntimeMethod;
        private static final Class cicManagerClass;
        private static volatile ContextHelper instance;
        private static final String CIC_MANAGER_RESOURCE_NAME = "META-INF/services/weblogic.invocation.ComponentInvocationContextManager";
        private static final String CIC_MANAGER_CLASS_NAME = "weblogic.invocation.ComponentInvocationContextManager";

        static {
            if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()) {
                cicManagerClass = AccessController.doPrivileged(new PrivilegedAction<Class>() {
                    @Override
                    public Class run() {
                        return getCicManagerClass(CIC_MANAGER_RESOURCE_NAME, CIC_MANAGER_CLASS_NAME);
                    }
                });
            } else {
                cicManagerClass = getCicManagerClass(CIC_MANAGER_RESOURCE_NAME, CIC_MANAGER_CLASS_NAME);
            }
        }

        private static Class getCicManagerClass(String cicManagerResourceName, String cicManagerClassName) {
            try {
                if (WebLogic_12_Platform.class.getClassLoader().getResource(cicManagerResourceName) != null) {
                    return PrivilegedAccessHelper.getClassForName(cicManagerClassName);
                } else {
                    return null;
                }
            } catch (ClassNotFoundException cnfe) {
                return null;
            }
        }

        private ContextHelper(final Class managerClass, final String contextClassName) {
            if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()) {
                AccessController.doPrivileged(new PrivilegedAction<Void>() {
                    @Override
                    public Void run() {
                        initialize(managerClass, contextClassName);
                        return null;
                    }
                });
            } else {
                initialize(managerClass, contextClassName);
            }
        }

        private void initialize(final Class managerClass, final String contextClassName) {
            try {
                // Get component invocation manager
                final Method getInstance = PrivilegedAccessHelper.getDeclaredMethod(managerClass, "getInstance", new Class[]{});
                cicManagerInstance = PrivilegedAccessHelper.invokeMethod(getInstance, managerClass);
                // Get component invocation context
                getCurrentCicMethod = PrivilegedAccessHelper.getMethod(managerClass, "getCurrentComponentInvocationContext", new Class[]{}, true);
                final Class cicClass = PrivilegedAccessHelper.getClassForName(contextClassName);
                getPartitionIdMethod = PrivilegedAccessHelper.getDeclaredMethod(cicClass, "getPartitionId", new Class[]{});
                getPartitionNameMethod = PrivilegedAccessHelper.getDeclaredMethod(cicClass, "getPartitionName", new Class[]{});
                isGlobalRuntimeMethod = PrivilegedAccessHelper.getDeclaredMethod(cicClass, "isGlobalRuntime", new Class[]{});
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | ClassNotFoundException ex) {
                AbstractSessionLog.getLog().logThrowable(SessionLog.WARNING, null, ex);
            }
        }

        static ContextHelper getContextHelper() {
            if (cicManagerClass == null) {
                return null;
            }
            if (instance == null) {
                synchronized (ContextHelper.class) {
                    if (instance == null) {
                        instance = new ContextHelper(cicManagerClass, "weblogic.invocation.ComponentInvocationContext");
                    }
                }
            }
            return instance;
        }

        /**
         * Gets partition ID. Calls cicInstance.getPartitionIdMethod().
         */
        String getPartitionID() {
            return getStringFromMethod(getPartitionIdMethod);
        }

        /**
         * Gets partition name. Calls cicInstance.getPartitionNameMethod().
         */
        String getPartitionName() {
            return getStringFromMethod(getPartitionNameMethod);
        }

        /**
         * Calls the method passed against cicInstance returning the result as
         * String.
         *
         * @param methodToCall
         *            The cicInstance method to call
         * @return the method result as String
         */
        private String getStringFromMethod(final Method methodToCall) {
            try {
                if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()) {
                    return AccessController.doPrivileged(new PrivilegedAction<String>() {
                        @Override
                        public String run() {
                            try {
                                final Object cicInstance = PrivilegedAccessHelper.invokeMethod(getCurrentCicMethod, cicManagerInstance);
                                return (String) PrivilegedAccessHelper.invokeMethod(methodToCall, cicInstance);
                            } catch (ReflectiveOperationException ex) {
                                AbstractSessionLog.getLog().logThrowable(SessionLog.WARNING, null, ex);
                                return "UNKNOWN";
                            }
                        }
                    });
                } else {
                    final Object cicInstance = PrivilegedAccessHelper.invokeMethod(getCurrentCicMethod, cicManagerInstance);
                    return (String) PrivilegedAccessHelper.invokeMethod(methodToCall, cicInstance);
                }
            } catch (ReflectiveOperationException ex) {
                AbstractSessionLog.getLog().logThrowable(SessionLog.WARNING, null, ex);
                return "UNKNOWN";
            }
        }

        /**
         * Returns whether code is running globally as opposed to running in
         * partition. Calls cicInstance.isGlobalRuntime().
         */
        boolean isGlobalRuntime() {
            try {
                if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()) {
                    return AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
                        @Override
                        public Boolean run() {
                            try {
                                final Object cicInstance = PrivilegedAccessHelper.invokeMethod(getCurrentCicMethod, cicManagerInstance);
                                return (Boolean) PrivilegedAccessHelper.invokeMethod(isGlobalRuntimeMethod, cicInstance);
                            } catch (ReflectiveOperationException ex) {
                                AbstractSessionLog.getLog().logThrowable(SessionLog.WARNING, null, ex);
                                return true;
                            }
                        }
                    });
                } else {
                    final Object cicInstance = PrivilegedAccessHelper.invokeMethod(getCurrentCicMethod, cicManagerInstance);
                    return (boolean) PrivilegedAccessHelper.invokeMethod(isGlobalRuntimeMethod, cicInstance);
                }
            } catch (ReflectiveOperationException ex) {
                AbstractSessionLog.getLog().logThrowable(SessionLog.WARNING, null, ex);
                return true;
            }
        }
    }
}
