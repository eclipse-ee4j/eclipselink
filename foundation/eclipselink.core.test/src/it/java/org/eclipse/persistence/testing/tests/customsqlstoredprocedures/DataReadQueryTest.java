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
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.framework.TestWarningException;

import java.util.Vector;

public class DataReadQueryTest extends TestCase {
    public DataReadQueryTest() {
        super();
    }

    @Override
    public void setup() {
        //right now only the stored procedure is set up in SQLServer
        if (!getSession().getPlatform().isSQLServer()) {
            throw new TestWarningException("This test can only be run in SQLServer.");
        }
    }

    @Override
    public void test() {
        DataReadQuery readQuery = new DataReadQuery();
        StoredProcedureCall call;
        call = new StoredProcedureCall();
        call.setProcedureName("Select_Employee_using_Output");
        call.addNamedArgument("ARG1", "argument");
        call.addNamedOutputArgument("VERSION", "VERSION", java.math.BigDecimal.class);
        readQuery.setCall(call);
        readQuery.addArgument("argument");
        getSession().removeQuery("dblogin");
        getSession().addQuery("dblogin", readQuery);
        Vector args = new Vector(1);
        args.add(1);
        try {
            Vector vResult = (Vector)getSession().executeQuery("dblogin", args);
        } catch (ClassCastException e) {
            throw new TestErrorException("Stored Procedure is returning a Row and the query expects a Vector");
        }

        readQuery = new DataReadQuery();
        call = new StoredProcedureCall();
        call.setProcedureName("Select_Employee_using_Output");
        call.addNamedArgumentValue("ARG1", 1);
        call.addNamedOutputArgument("VERSION", "VERSION", java.math.BigDecimal.class);
        readQuery.setCall(call);
        try {
            Vector vResult = (Vector)getSession().executeQuery(readQuery);
        } catch (ClassCastException e) {
            throw new TestErrorException("Stored Procedure is returning a Row and the query expects a Vector");
        }
    }
}
