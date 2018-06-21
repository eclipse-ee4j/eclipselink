/*
 * Copyright (c) 2016, 2018 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2016 IBM Corporation. All rights reserved.
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
//     01/05/2015 Rick Curtis
//       - 455683: Automatically detect target server
//     08/26/2016 Will Dazey
//       - 499869: Update WAS server detection
package org.eclipse.persistence.platform.server.was;

import java.security.AccessController;
import java.security.PrivilegedAction;

import org.eclipse.persistence.config.TargetServer;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.platform.server.ServerPlatformDetector;

public class WebSpherePlatformDetector implements ServerPlatformDetector {
    private static final String[] LIBERTY_PROPS = new String[] { "server.config.dir", "server.output.dir" };
    private static final String LIBERTY_PROFILE_INFO_INT = "com.ibm.websphere.config.mbeans.FeatureListMBean";
    private static final String FULL_PROFILE_WAS_DIR_CLS = "com.ibm.websphere.product.WASDirectory";

    @Override
    public String checkPlatform() {
        if (isLiberty()) {
            return TargetServer.WebSphere_Liberty;
        }
        if (isFullProfile()) {
            return TargetServer.WebSphere_7;
        }
        return null;
    }

    private boolean isLiberty() {
        if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()) {
            return AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
                @Override
                public Boolean run() {
                    return isLibertyInternal();
                }
            });
        } else {
            return isLibertyInternal();
        }
    }

    private boolean isFullProfile() {
        if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()) {
            return AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
                @Override
                public Boolean run() {
                    return isFullProfileInternal();
                }
            });
        } else {
            return isFullProfileInternal();
        }
    }

    /**
     * A private worker method that must be wrapped in a doPriv block if the
     * security manager is enabled.
     *
     * @return true if running Liberty, false otherwise.
     */
    private Boolean isLibertyInternal() {
       return checkProperties(LIBERTY_PROPS) || checkClassLoader(LIBERTY_PROFILE_INFO_INT);
    }

    /**
     * A private worker method that must be wrapped in a doPriv block if the
     * security manager is enabled.
     *
     * @return true if running full profile, false otherwise.
     */
    private Boolean isFullProfileInternal() {
        return checkClassLoader(FULL_PROFILE_WAS_DIR_CLS);
    }

    private Boolean checkProperties(String [] props) {
        for (String prop : props) {
            if (System.getProperty(prop) == null) {
                return Boolean.FALSE;
            }
        }
        return Boolean.TRUE;
    }

    private Boolean checkClassLoader(String className) {
        try {
            ClassLoader loader = WebSpherePlatformDetector.class.getClassLoader();
            Class<?> cls = loader.loadClass(className);
            if (cls != null) {
                return Boolean.TRUE;
            }
        } catch (Throwable t) { }
        return Boolean.FALSE;
    }
}
