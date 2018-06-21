/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.tools.workbench.utility;

/**
 * ExceptionListeners are notified by an ExceptionBroadcaster of any
 * [uncaught] exceptions that are thrown.
 */
public interface ExceptionListener {

    /**
     * The specified exception was thrown [and not caught]
     * during the execution of the specified thread.
     */
    void exceptionThrown(Thread thread, Throwable exception);

    ExceptionListener NULL_INSTANCE =
        new ExceptionListener() {
            public void exceptionThrown(Thread thread, Throwable exception) {
                // do nothing
            }
            public String toString() {
                return "NullExceptionListener";
            }
        };

    ExceptionListener DEFAULT_INSTANCE =
        new ExceptionListener() {
            public void exceptionThrown(Thread thread, Throwable exception) {
                // let the exception do what comes naturally
                exception.printStackTrace();
            }
            public String toString() {
                return "DefaultExceptionListener";
            }
        };

}
