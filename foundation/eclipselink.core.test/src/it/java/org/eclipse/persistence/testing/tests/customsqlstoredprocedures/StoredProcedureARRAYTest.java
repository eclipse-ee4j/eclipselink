/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.customsqlstoredprocedures;

import org.eclipse.persistence.queries.DataReadQuery;
import org.eclipse.persistence.queries.StoredProcedureCall;
import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.testing.framework.TestWarningException;

import java.sql.Types;

/**
 * Test calling a procedure that return a custom PLSQL type, ARRAY.
 */
public class StoredProcedureARRAYTest extends TestCase {
    @Override
    public void setup() {
        if (!(getSession().getPlatform().isOracle())) {
            throw new TestWarningException("This test can only be run in Oracle");
        }
    }

    @Override
    public void test() {
        StoredProcedureCall call = new StoredProcedureCall();
        call.setProcedureName("StoredProcedure_ARRAY");
        call.addNamedOutputArgument("P_OUT", "P_OUT", Types.ARRAY, "TEST_STRING_ARRAY");

        DataReadQuery query = new DataReadQuery();
        query.setShouldBindAllParameters(true);
        query.setCall(call);

        getSession().executeQuery(query);
    }
}
