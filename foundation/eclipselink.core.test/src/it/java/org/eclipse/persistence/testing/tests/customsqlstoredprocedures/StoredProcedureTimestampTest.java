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

import java.util.Date;
import java.util.Vector;

public class StoredProcedureTimestampTest extends TestCase {

    DatabaseRecord row;
    boolean shouldBindAllParameters;

    public StoredProcedureTimestampTest() {
        this(true);
    }

    public StoredProcedureTimestampTest(boolean shouldBindAllParameters) {
        super();
        //binding is not really necessary for this test
        this.shouldBindAllParameters = shouldBindAllParameters;
        setName(getName() + " bind = " + shouldBindAllParameters);
    }

    @Override
    public void setup() {
        //right now only the stored procedure is set up in Oracle
        if (!(getSession().getPlatform().isOracle())) {
            throw new TestWarningException("This test can only be run in Oracle");
        }
    }

    @Override
    public void test() {
        StoredProcedureCall call = new StoredProcedureCall();
        call.setProcedureName("StoredProcedure_Timestamp");
        call.addNamedInOutputArgument("CURRENT_DATE");

        DataReadQuery query = new DataReadQuery();
        query.setShouldBindAllParameters(shouldBindAllParameters);
        query.setCall(call);
        query.addArgument("CURRENT_DATE");

        //note: using java.util.Date
        Date currentDate = new Date();
        Vector args = new Vector(1);
        args.add(currentDate);

        row = (DatabaseRecord)((Vector)getSession().executeQuery(query, args)).get(0);
    }

    //a java.util.Date object should return a java.sql.Timestamp object
    @Override
    public void verify() {
        if (!(row.get("CURRENT_DATE")).getClass().equals(java.sql.Timestamp.class)) {
            throw new TestErrorException("Timestamp class not returned for java.util.Date");
        }
    }
}
