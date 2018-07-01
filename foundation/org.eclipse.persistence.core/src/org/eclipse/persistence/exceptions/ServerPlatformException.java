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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.exceptions;

import org.eclipse.persistence.exceptions.i18n.ExceptionMessageGenerator;

public class ServerPlatformException extends EclipseLinkException {

    public static final int SERVER_PLATFORM_CLASS_NOT_FOUND = 63001;
    public static final int INVALID_SERVER_PLATFORM_CLASS = 63002;

    public ServerPlatformException() {
        super();
    }

    protected ServerPlatformException(String theMessage) {
        super(theMessage);
    }

    protected ServerPlatformException(String message, Throwable internalException) {
        super(message, internalException);
    }

    public static ServerPlatformException serverPlatformClassNotFound(String className, Throwable t) {
        Object[] args = { className };
        ServerPlatformException sessionLoaderException = new ServerPlatformException(
                ExceptionMessageGenerator.buildMessage(ServerPlatformException.class, SERVER_PLATFORM_CLASS_NOT_FOUND, args), t);
        sessionLoaderException.setErrorCode(SERVER_PLATFORM_CLASS_NOT_FOUND);
        return sessionLoaderException;
    }

    public static ServerPlatformException invalidServerPlatformClass(String className, Throwable t) {
        Object[] args = { className };
        ServerPlatformException sessionLoaderException = new ServerPlatformException(
                ExceptionMessageGenerator.buildMessage(ServerPlatformException.class, INVALID_SERVER_PLATFORM_CLASS, args), t);
        sessionLoaderException.setErrorCode(INVALID_SERVER_PLATFORM_CLASS);
        return sessionLoaderException;
    }
}
