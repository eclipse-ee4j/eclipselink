/*
 * Copyright (c) 2015, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     01/05/2015 Rick Curtis
//       - 455683: Automatically detect target server
//     04/24/2015 Rick Curtis
//       - 465452: Throw ServerPlatformException rather than NPE on create null platformClass
package org.eclipse.persistence.platform.server;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.exceptions.ServerPlatformException;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedClassForName;
import org.eclipse.persistence.internal.security.PrivilegedGetConstructorFor;
import org.eclipse.persistence.internal.security.PrivilegedInvokeConstructor;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.PropertiesHandler;
import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.platform.server.glassfish.GlassfishPlatformDetector;
import org.eclipse.persistence.platform.server.was.WebSpherePlatformDetector;
import org.eclipse.persistence.platform.server.wls.WebLogicPlatformDetector;
import org.eclipse.persistence.sessions.DatabaseSession;

/**
 * A utility class to interact with ServerPlatforms.
 */
public final class ServerPlatformUtils {
    private static final List<ServerPlatformDetector> PLATFORMS = new ArrayList<ServerPlatformDetector>() {{
        add(new NoServerPlatformDetector());
        add(new WebSpherePlatformDetector());
        add(new WebLogicPlatformDetector());
        add(new GlassfishPlatformDetector());
    }};

    private static final String UNKNOWN_MARKER = "UNKNOWN";
    private static String SERVER_PLATFORM_CLS;

    /**
     * @param session
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
            //https://bugs.eclipse.org/bugs/show_bug.cgi?id=476018
            //log message only when we know how loggers were set up
            if (session != null) {
                session.log(SessionLog.FINER, SessionLog.SERVER, "detect_server_platform", SERVER_PLATFORM_CLS);
            } else {
                AbstractSessionLog.getLog().log(SessionLog.FINER, SessionLog.SERVER, "detect_server_platform", SERVER_PLATFORM_CLS);
            }
            return SERVER_PLATFORM_CLS;
        }
    }

    /**
     * Create an instance of {@link ServerPlatform} from parameters passed in.
     *
     * @param session {@link DatabaseSession} which will be passed to the constructor of {@link ServerPlatform}
     * @param platformClass fully qualified name of the {@link ServerPlatform} implementation to initialize
     * @param loader {@link ClassLoader} to look up given platformClass
     *
     * @return initialized instance of {@link ServerPlatform}
     * @throws ServerPlatformException if supplied platformClass is not found, can not be initialized, or is null.
     *
     * @see ServerPlatformBase#ServerPlatformBase(DatabaseSession)
     */
    public static ServerPlatform createServerPlatform(DatabaseSession session, String platformClass, ClassLoader loader) {
        if (platformClass == null) {
            throw ServerPlatformException.invalidServerPlatformClass(null, null);
        }
        Class cls = null;
        try {
            //try the supplied classloader
            cls = findClass(platformClass, loader);
        } catch (ClassNotFoundException | PrivilegedActionException ex) {
            //fallback to EL loader
            ClassLoader cl = ServerPlatformUtils.class.getClassLoader();
            if (loader != cl) {
                try {
                    cls = findClass(platformClass, cl);
                } catch (ClassNotFoundException | PrivilegedActionException ex1) {
                    //ignore, throw first exception
                    throw ServerPlatformException.serverPlatformClassNotFound(platformClass, ex);
                }
            } else {
                throw ServerPlatformException.serverPlatformClassNotFound(platformClass, ex);
            }
        }
        final Class[] paramTypes = new Class[] { DatabaseSession.class };
        final Object[] params = new Object[] { session };
        ServerPlatform platform = null;
        if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()) {
            try {
                Constructor constructor = AccessController.doPrivileged(
                        new PrivilegedGetConstructorFor(cls, paramTypes, false));
                platform = (ServerPlatform) AccessController.doPrivileged(
                        new PrivilegedInvokeConstructor(constructor, params));
            } catch (PrivilegedActionException ex) {
                throw ServerPlatformException.invalidServerPlatformClass(platformClass, ex);
            }
        } else {
            try {
                Constructor constructor = PrivilegedAccessHelper.getConstructorFor(cls, paramTypes, false);
                platform = (ServerPlatform) PrivilegedAccessHelper.invokeConstructor(constructor, params);
            } catch (NoSuchMethodException | IllegalAccessException
                    | InvocationTargetException | InstantiationException ex) {
                throw ServerPlatformException.invalidServerPlatformClass(platformClass, ex);
            }
        }
        return platform;
    }

    private static Class findClass(String className, ClassLoader loader) throws ClassNotFoundException, PrivilegedActionException {
        if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()) {
            return AccessController.doPrivileged(new PrivilegedClassForName(className, false, loader));
        } else {
            return PrivilegedAccessHelper.getClassForName(className, false, loader);
        }
    }
}
