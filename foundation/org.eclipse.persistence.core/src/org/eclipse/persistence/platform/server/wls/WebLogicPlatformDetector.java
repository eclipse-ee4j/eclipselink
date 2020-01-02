/*
 * Copyright (c) 2015, 2020 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2015 IBM Corporation. All rights reserved.
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
//     01/05/2015 Rick Curtis, Andrei Ilitchev
//       - 455683: Automatically detect target server
package org.eclipse.persistence.platform.server.wls;

import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;

import org.eclipse.persistence.config.TargetServer;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.platform.server.ServerPlatformDetector;

public class WebLogicPlatformDetector implements ServerPlatformDetector {

    @Override
    public String checkPlatform() {
        String platform = null;
        String serverNameAndVersion;
        if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()) {
            serverNameAndVersion = AccessController.doPrivileged(new PrivilegedAction<String>() {
                @Override
                public String run() {
                    return getServerNameAndVersionInternal();
                }
            });
        } else {
            serverNameAndVersion = getServerNameAndVersionInternal();
        }
        if (serverNameAndVersion != null) {
            int idx = serverNameAndVersion.indexOf('.');
            try {
                int version = Integer.valueOf(serverNameAndVersion.substring(0, idx));
                if (version >= 12) {
                    platform = TargetServer.WebLogic_12;
                } else {
                    switch (version) {
                        case 11:
                        case 10:
                            platform = TargetServer.WebLogic_10;
                            break;
                        case 9:
                            platform = TargetServer.WebLogic_9;
                            break;
                        default:
                            platform = TargetServer.WebLogic;
                    }
                }
            } catch (NumberFormatException nfe) {
                // default fallback
                platform = TargetServer.WebLogic;
            }
        }
        return platform;
    }

    /**
     * A private worker method that must be wrapped in a doPriv block if the
     * security manager is enabled.
     *
     * @return The server name and version String. null otherwise.
     */
    private String getServerNameAndVersionInternal() {
        try {
            Class versionCls = Class.forName("weblogic.version");
            Method method = versionCls.getMethod("getReleaseBuildVersion");
            return (String) method.invoke(null);
        } catch (Throwable t) {
            //ignore
        }
        return null;
    }
}
