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
import org.eclipse.persistence.sessions.DatabaseRecord;
import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.framework.TestWarningException;

import java.util.Vector;

public class StoredProcedureTest extends TestCase {
    DatabaseRecord row;
    boolean shouldBindAllParameters;

    public StoredProcedureTest() {
        this(true);
    }

    public StoredProcedureTest(boolean shouldBindAllParameters) {
        super();
        this.shouldBindAllParameters = shouldBindAllParameters;
        setName(getName() + " bind = " + shouldBindAllParameters);
    }

    @Override
    public void setup() {
        // right now only the stored procedure is set up in Oracle
        if (!(getSession().getPlatform().isOracle())) {
            throw new TestWarningException("This test can only be run in Oracle");
        }
    }

    @Override
    public void test() {
        Integer id = 12;
        String name = "James";

        StoredProcedureCall call = new StoredProcedureCall();
        call.setProcedureName("StoredProcedure_InOutput");
        call.addNamedInOutputArgument("P_EMP_ID");
        call.addNamedInOutputArgument("P_F_NAME");

        DataReadQuery query = new DataReadQuery();
        query.setShouldBindAllParameters(shouldBindAllParameters);
        query.bindAllParameters();
        query.setCall(call);
        query.addArgument("P_EMP_ID");
        query.addArgument("P_F_NAME");

        Vector args = new Vector(2);
        args.add(id);
        args.add(name);

        row = (DatabaseRecord)((Vector)getSession().executeQuery(query, args)).get(0);

    }

    @Override
    public void verify() {
        // Fix for different interpretations of Database type NUMBER on oracle 8 and oracle 9 - tgw
        if (!(((Number)row.get("P_EMP_ID")).intValue() == 12)) {
            throw new TestErrorException("Invalid Field");
        }

        if (!row.get("P_F_NAME").equals("James")) {
            throw new TestErrorException("Invalid Field");
        }
    }
}
