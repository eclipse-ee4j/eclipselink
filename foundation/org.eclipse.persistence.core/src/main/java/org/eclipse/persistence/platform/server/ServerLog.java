/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.platform.server;

import java.io.*;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.logging.*;

/**
 * <p>
 * Basic logging class that provides framework for integration with the application
 * server log. This class is used when messages need to be logged through an application
 * server, e.g. OC4J.
 * </p>
 *  @see SessionLog
 *  @see AbstractSessionLog
 *  @see SessionLogEntry
 *  @see org.eclipse.persistence.sessions.Session Session
 */
public class ServerLog extends AbstractSessionLog {

    /**
     * PUBLIC:
     * <p>
     * Create and return a new ServerLog.
     * </p>
     */
    public ServerLog() {
        super();
    }

    /**
     * PUBLIC:
     * <p>
     * Log a SessionLogEntry
     * </p>
     *
     * @param entry SessionLogEntry that holds all the information for a TopLink logging event
     */
    @Override
    public void log(SessionLogEntry entry) {
        if (!shouldLog(entry.getLevel())) {
            return;
        }

        String message = getSupplementDetailString(entry);

        if (entry.hasException()) {
            if (entry.getLevel() == SEVERE) {
                message += Helper.printStackTraceToString(entry.getException());
            } else if (entry.getLevel() <= WARNING) {
                if (shouldLogExceptionStackTrace()) {
                    message += Helper.printStackTraceToString(entry.getException());
                } else {
                    message += entry.getException();
                }
            }
        } else {
            message += formatMessage(entry);
        }

        basicLog(entry.getLevel(), entry.getNameSpace(), message);
    }

    /**
     * <p>
     * Log message to a writer by default.  It needs to be overridden by the subclasses.
     * </p>
     *
     * @param level the log request level
     * @param message the formatted string message
     */
    protected void basicLog(int level, String category, String message) {
        try {
            printPrefixString(level, null);
            getWriter().write(message);
            getWriter().write(Helper.cr());
            getWriter().flush();
        } catch (IOException exception) {
            throw ValidationException.logIOError(exception);
        }
    }
}
