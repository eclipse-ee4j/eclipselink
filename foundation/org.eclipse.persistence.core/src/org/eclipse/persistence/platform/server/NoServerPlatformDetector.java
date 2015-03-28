/*******************************************************************************
 * Copyright (c) 2015 IBM Corporation. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     01/05/2015 Rick Curtis
 *       - 455683: Automatically detect target server
 ******************************************************************************/
package org.eclipse.persistence.platform.server;

import org.eclipse.persistence.config.TargetServer;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;

public class NoServerPlatformDetector implements ServerPlatformDetector {
    private static final String SE_CLASSLOADER_STRING = "sun.misc.Launcher$AppClassLoader";

    @Override
    public String checkPlatform() {
        String loaderStr;
        if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()) {
            loaderStr = PrivilegedAccessHelper.privilegedGetClassLoaderForClass(NoServerPlatformDetector.class).toString();
        } else {
            loaderStr = PrivilegedAccessHelper.getClassLoaderForClass(NoServerPlatformDetector.class).toString();
        }

        if (loaderStr != null && loaderStr.startsWith(SE_CLASSLOADER_STRING)) {
            return TargetServer.None;
        }
        return null;
    }

}
