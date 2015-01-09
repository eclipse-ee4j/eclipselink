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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.PropertiesHandler;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.platform.server.was.WebSpherePlatformDetector;
import org.eclipse.persistence.platform.server.wls.WebLogicPlatformDetector;

/**
 * A utility class to interact with ServerPlatforms.
 */
public class ServerPlatformUtils {
    private static final List<ServerPlatformDetector> PLATFORMS = new ArrayList<ServerPlatformDetector>(Arrays.asList(new ServerPlatformDetector[] {
            new NoServerPlatformDetector(), new WebSpherePlatformDetector(), new WebLogicPlatformDetector() }));

    private static final String UNKNOWN_MARKER = "UNKNOWN";
    private static String SERVER_PLATFORM_CLS;

    /**
     * @return The target-server class string that represents platform that is
     *         currently running. Return null if unknown.
     */
    public static String detectServerPlatform(AbstractSession session) {
        if (SERVER_PLATFORM_CLS == null) {
            for (ServerPlatformDetector server : PLATFORMS) {
                String res = server.checkPlatform();
                if (res != null) {
                    // Convert property values to platform class
                    SERVER_PLATFORM_CLS = PropertiesHandler.getPropertyValue(PersistenceUnitProperties.TARGET_SERVER, res);
                    break;
                }
            }
            // If we didn't detect a server, use the unknown marker so we don't
            // continually try to detect the platform.
            if (SERVER_PLATFORM_CLS == null) {
                SERVER_PLATFORM_CLS = UNKNOWN_MARKER;
            }
        }

        if (SERVER_PLATFORM_CLS == UNKNOWN_MARKER) {
            return null;
        } else {
            session.log(SessionLog.INFO, SessionLog.SERVER, "detect_server_platform", SERVER_PLATFORM_CLS);
            return SERVER_PLATFORM_CLS;
        }

    }

}
