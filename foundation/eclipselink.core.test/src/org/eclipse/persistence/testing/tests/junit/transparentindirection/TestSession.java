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
package org.eclipse.persistence.testing.tests.junit.transparentindirection;

import org.eclipse.persistence.internal.sessions.AbstractRecord;


/**
 * stub Session used for testing (does not actually do anything)
 */
public class TestSession extends org.eclipse.persistence.internal.sessions.AbstractSession {
    // the result to be returned by the executeQuery(DatabaseQuery, DatabaseRecord)
    Object result;

    /**
     * set the object to be returned later
     */
    public TestSession(Object result) {
        this.result = result;
    }

    public Object executeQuery(org.eclipse.persistence.queries.DatabaseQuery query, AbstractRecord row) {
        return result;
    }
}
