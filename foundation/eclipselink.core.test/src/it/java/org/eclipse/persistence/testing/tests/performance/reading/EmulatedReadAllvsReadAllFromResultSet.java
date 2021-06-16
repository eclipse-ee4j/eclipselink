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
package org.eclipse.persistence.testing.tests.performance.reading;

import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.testing.framework.*;

/**
 * This test compares the performance of read all vs reading directly from a result-set.
 */
public class EmulatedReadAllvsReadAllFromResultSet extends ReadAllvsReadAllFromResultSet {

    public void setup() {
        Session session = buildEmulatedSession();

        /*EmulatedConnection connection = (EmulatedConnection)((org.eclipse.persistence.internal.sessions.AbstractSession)session).getAccessor().getConnection();

        ReadAllQuery query = new ReadAllQuery(Address.class);
        getSession().executeQuery(query);
        String sql = query.getSQLString();
        Vector rows = getSession().executeSQL(sql);
        connection.putRows(sql, rows);*/

        getExecutor().swapSession(session);
        super.setup();
    }

    public void reset() throws Exception {
        getExecutor().resetSession();
        super.reset();
    }

    public PerformanceComparisonTestCase readAllFromResultSet() {
        PerformanceComparisonTestCase test = super.readAllFromResultSet();
        test.setAllowableDecrease(20);
        return test;
    }
}
