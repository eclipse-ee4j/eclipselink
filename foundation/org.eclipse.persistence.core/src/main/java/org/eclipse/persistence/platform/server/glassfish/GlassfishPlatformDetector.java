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
