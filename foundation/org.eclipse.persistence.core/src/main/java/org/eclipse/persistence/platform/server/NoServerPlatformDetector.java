/*
 * Copyright (c) 2015, 2021 Oracle and/or its affiliates. All rights reserved.
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
