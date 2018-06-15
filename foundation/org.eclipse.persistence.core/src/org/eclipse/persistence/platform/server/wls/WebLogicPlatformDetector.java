/*
 * Copyright (c) 2015, 2018 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2015 IBM Corporation, Oracle. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
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
            switch (serverNameAndVersion.substring(0, idx)) {
                case "12":
                    platform = TargetServer.WebLogic_12;
                    break;
                case "11":
                case "10":
                    platform = TargetServer.WebLogic_10;
                    break;
                case "9":
                    platform = TargetServer.WebLogic_9;
                    break;
                default:
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
            String loaderStr = WebLogicPlatformDetector.class.getClassLoader().getClass().getName();
            if (loaderStr.contains("weblogic")) {
                Class versionCls = Class.forName("weblogic.version");
                Method method = versionCls.getMethod("getReleaseBuildVersion");
                return (String) method.invoke(null);
            }
        } catch (Throwable t) {
            //ignore
        }
        return null;
    }
}
