/*
 * Copyright (c) 2015, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     11/20/2015 Lukas Jungmann
//       Bug 482894 - Provide autodetector for Glassfish
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
