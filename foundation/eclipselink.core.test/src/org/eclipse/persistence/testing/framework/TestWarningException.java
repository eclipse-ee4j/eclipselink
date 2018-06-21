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
package org.eclipse.persistence.testing.framework;


/**
 * <p>
 * <b>Purpose</b>: This exception is raised for the test cases where the test case technically passes
 * but something need to be changed to get it completely passes.
 */
public class TestWarningException extends TestException {
    public TestWarningException(String message) {
        super(message);
    }

    public TestWarningException(String theMessage, Throwable internalException) {
            super(theMessage, internalException);
    }
}
