/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.tools.workbench.utility;

/**
 * An ExceptionBroadcaster will notify its listeners when an
 * [uncaught] exception has occurred.
 */
public interface ExceptionBroadcaster {

    /**
     * Broadcast the specified exception to the broadcaster's
     * listeners. The specified thread was executing when the
     * specified exception was thrown.
     */
    void broadcast(Thread thread, Throwable exception);

    /**
     * Add the specified listener to the broadcaster's
     * registered listeners. The listener will be notified
     * of any unhandled exceptions.
     */
    void addExceptionListener(ExceptionListener listener);

    /**
     * Remove the specified listener from the broadcaster's
     * registered listeners.
     */
    void removeExceptionListener(ExceptionListener listener);

}
