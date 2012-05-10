/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.customsqlstoredprocedures;

import java.sql.Types;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.testing.framework.*;

/**
 * Test calling a procedure that return a custom PLSQL type, ARRAY.
 */
public class StoredProcedureARRAYTest extends TestCase {
    public void setup() {
        if (!(getSession().getPlatform().isOracle())) {
            throw new TestWarningException("This test can only be run in Oracle");
        }
    }

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
