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
