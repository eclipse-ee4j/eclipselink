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
package org.eclipse.persistence.platform.server.was;

import java.security.AccessController;
import java.security.PrivilegedAction;

import org.eclipse.persistence.config.TargetServer;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.platform.server.ServerPlatformDetector;

public class WebSpherePlatformDetector implements ServerPlatformDetector {
    private static final String[] LIBERTY_PROPS = new String[] { "server.config.dir", "server.output.dir" };
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
        for (String prop : LIBERTY_PROPS) {
            if (System.getProperty(prop) == null) {
                return Boolean.FALSE;
            }
        }
        return Boolean.TRUE;
    }

    /**
     * A private worker method that must be wrapped in a doPriv block if the
     * security manager is enabled.
     *
     * @return true if running full profile, false otherwise.
     */
    private Boolean isFullProfileInternal() {
        try {
            ClassLoader loader = WebSpherePlatformDetector.class.getClassLoader();
            Class<?> cls = loader.loadClass(FULL_PROFILE_WAS_DIR_CLS);
            Object instance = cls.newInstance();
            if (instance != null) {
                return Boolean.TRUE;
            }
        } catch (Throwable t) {
        }

        return Boolean.FALSE;
    }

}
