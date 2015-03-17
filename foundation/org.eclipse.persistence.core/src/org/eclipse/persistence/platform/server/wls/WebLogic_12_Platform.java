/*******************************************************************************
 * Copyright (c) 2015 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation
 ******************************************************************************/
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

    private static final class ContextHelper {

        /**
         * Instance of CIC (Component Invocation Context) or null if not
         * initialized.
         */
        private Object cicManagerInstance;
        private Method getCurrentCicMethod;
        private Method getPartitionIdMethod;
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
            try {
                if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()) {
                    return AccessController.doPrivileged(new PrivilegedAction<String>() {
                        @Override
                        public String run() {
                            try {
                                final Object cicInstance = PrivilegedAccessHelper.invokeMethod(getCurrentCicMethod, cicManagerInstance);
                                return (String) PrivilegedAccessHelper.invokeMethod(getPartitionIdMethod, cicInstance);
                            } catch (IllegalAccessException | InvocationTargetException ex) {
                                AbstractSessionLog.getLog().logThrowable(SessionLog.WARNING, null, ex);
                                return "UNKNOWN";
                            }
                        }
                    });
                } else {
                    final Object cicInstance = PrivilegedAccessHelper.invokeMethod(getCurrentCicMethod, cicManagerInstance);
                    return (String) PrivilegedAccessHelper.invokeMethod(getPartitionIdMethod, cicInstance);
                }
            } catch (IllegalAccessException | InvocationTargetException ex) {
                AbstractSessionLog.getLog().logThrowable(SessionLog.WARNING, null, ex);
                return "UNKNOWN";
            }
        }
    }
}
