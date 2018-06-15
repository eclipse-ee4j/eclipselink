/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.security;

import java.lang.reflect.ReflectPermission;
import java.security.Permission;

//This is an assisting class for SecurityException tests.  This is a lazy yet efficient way to
//trigger SecurityException since triggering the real SecurityException is too much involved.
public class TestSecurityManager extends SecurityManager {

    public static boolean TRIGGER_EX = true;

    public TestSecurityManager() {
        super();
    }

    @Override
    public void checkPackageAccess(String pkg) {
        if (TRIGGER_EX && "java.lang.reflect".equals(pkg)) {
            for (StackTraceElement ste : Thread.currentThread().getStackTrace()) {
                if (ste.getClassName().startsWith("org.eclipse.persistence.testing.tests.security") && "test".equals(ste.getMethodName())) {
                    throw new SecurityException("Dummy SecurityException test");
                }
            }
        }
    }

    public void checkPermission(Permission perm) {
        // don't throw an error, so reset can reset security manager.
        if (perm instanceof ReflectPermission && "suppressAccessChecks".equals(perm.getName())) {
            for (StackTraceElement ste : Thread.currentThread().getStackTrace()) {
                if (ste.getClassName().startsWith("org.eclipse.persistence.testing.tests.security") && "test".equals(ste.getMethodName())) {
                    throw new SecurityException("Dummy SecurityException test");
                }
            }
        }
    }
}
