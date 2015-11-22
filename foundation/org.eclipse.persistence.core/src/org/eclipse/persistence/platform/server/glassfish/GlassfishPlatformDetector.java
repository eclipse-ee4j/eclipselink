/*******************************************************************************
 * Copyright (c) 2015 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     11/20/2015 Lukas Jungmann
 *       Bug 482894 - Provide autodetector for Glassfish
 ******************************************************************************/
package org.eclipse.persistence.platform.server.glassfish;

import java.security.AccessController;

import org.eclipse.persistence.config.TargetServer;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedGetSystemProperty;
import org.eclipse.persistence.platform.server.ServerPlatformDetector;

public final class GlassfishPlatformDetector implements ServerPlatformDetector {

    private static final String GF_ROOT_PROP = "com.sun.aas.installRoot";

    @Override
    public String checkPlatform() {
        if (isGlassfish()) {
            return TargetServer.Glassfish;
        }
        return null;
    }

    private boolean isGlassfish() {
        return getGlassfishInstallRoot() != null;
    }

    private String getGlassfishInstallRoot() {
        return PrivilegedAccessHelper.shouldUsePrivilegedAccess()
                ? AccessController.doPrivileged(new PrivilegedGetSystemProperty(GF_ROOT_PROP))
                : System.getProperty(GF_ROOT_PROP);
    }
}
