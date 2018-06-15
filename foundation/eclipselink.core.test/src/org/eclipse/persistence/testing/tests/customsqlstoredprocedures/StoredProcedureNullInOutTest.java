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
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.testing.framework.*;

public class StoredProcedureNullInOutTest extends TestCase {
    Exception exception;
    boolean shouldBindAllParameters;

    public StoredProcedureNullInOutTest() {
        this(true);
    }

    public StoredProcedureNullInOutTest(boolean shouldBindAllParameters) {
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
        StoredProcedureCall call = new StoredProcedureCall();
        call.setProcedureName("StoredProcedure_InOut_Out_In");
        call.addNamedInOutputArgument("P_INOUT");
        call.addNamedOutputArgument("P_OUT");
        call.addNamedArgument("P_IN");

        DataReadQuery query = new DataReadQuery();
        query.setShouldBindAllParameters(shouldBindAllParameters);
        query.setCall(call);
        query.addArgument("P_IN");
        query.addArgument("P_INOUT");

        Vector args = new Vector(2);
        args.addElement(null);
        args.addElement(null);

        try {
            getSession().executeQuery(query, args);
            exception = null;
        } catch (Exception ex) {
            exception = ex;
        }
    }

    public void verify() {
        if (exception != null) {
            throw new TestErrorException("StoredProcedure_InOut_Out_In(null, null) has failed", exception);
        }
    }
}
