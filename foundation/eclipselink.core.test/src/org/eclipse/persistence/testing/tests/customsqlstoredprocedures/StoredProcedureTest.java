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
package org.eclipse.persistence.testing.tests.customsqlstoredprocedures;

import java.util.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.testing.framework.*;

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

    public void setup() {
        // right now only the stored procedure is set up in Oracle
        if (!(getSession().getPlatform().isOracle())) {
            throw new TestWarningException("This test can only be run in Oracle");
        }
    }

    public void test() {
        Integer id = new Integer(12);
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
        args.addElement(id);
        args.addElement(name);

        row = (DatabaseRecord)((Vector)getSession().executeQuery(query, args)).firstElement();

    }

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
