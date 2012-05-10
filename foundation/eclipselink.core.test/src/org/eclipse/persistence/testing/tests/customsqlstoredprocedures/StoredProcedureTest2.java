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

import java.util.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.testing.framework.*;

public class StoredProcedureTest2 extends TestCase {
    DatabaseRecord row;
    int id = 1;
    boolean shouldBindAllParameters;

    public StoredProcedureTest2() {
        this(true);
    }

    public StoredProcedureTest2(boolean shouldBindAllParameters) {
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
        call.setProcedureName("StoredProcedure_InOutput2");
        call.addNamedInOutputArgument("P_ID");
        call.addNamedOutputArgument("P_ID2", "P_ID2", Integer.class);

        DataReadQuery query = new DataReadQuery();
        query.setShouldBindAllParameters(shouldBindAllParameters);
        query.setCall(call);
        query.addArgument("P_ID");

        Vector args = new Vector(1);
        args.addElement(new Integer(id));

        row = (DatabaseRecord)((Vector)getSession().executeQuery(query, args)).firstElement();
    }

    public void verify() {
        int id1 = ((Number)row.get("P_ID")).intValue();
        int id2 = ((Number)row.get("P_ID2")).intValue();
        if (id1 != id) {
            throw new TestErrorException("Invalid value P_ID = " + id1 + "; should be " + id);
        }
        if (id2 != (id * 2)) {
            throw new TestErrorException("Invalid value P_ID2 = " + id2 + "; should be " + (id * 2));
        }
    }
}
