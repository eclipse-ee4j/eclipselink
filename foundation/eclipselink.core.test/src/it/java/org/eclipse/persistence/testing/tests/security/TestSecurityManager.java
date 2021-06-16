/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
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
