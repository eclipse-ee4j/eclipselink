/*
 * Copyright (c) 2015, 2018 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2015, 2018 IBM Corporation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     01/05/2015 Rick Curtis
//       - 455683: Automatically detect target server
package org.eclipse.persistence.platform.server;

import java.security.AccessController;
import java.security.PrivilegedActionException;

import org.eclipse.persistence.config.TargetServer;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedGetClassLoaderForClass;

public class NoServerPlatformDetector implements ServerPlatformDetector {
    private static final String SE_CLASSLOADER_STRING = "sun.misc.Launcher$AppClassLoader";

    @Override
    public String checkPlatform() {
        String loaderStr;
        if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()) {
            try{
                loaderStr = AccessController.doPrivileged(new PrivilegedGetClassLoaderForClass(NoServerPlatformDetector.class)).toString();
            } catch (PrivilegedActionException ex){
                throw (RuntimeException) ex.getCause();
            }
        } else {
            loaderStr = PrivilegedAccessHelper.getClassLoaderForClass(NoServerPlatformDetector.class).toString();
        }

        if (loaderStr != null && loaderStr.startsWith(SE_CLASSLOADER_STRING)) {
            return TargetServer.None;
        }
        return null;
    }

}
