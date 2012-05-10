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

    public void setup() {
        //right now only the stored procedure is set up in Oracle
        if (!(getSession().getPlatform().isOracle())) {
            throw new TestWarningException("This test can only be run in Oracle");
        }
    }

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
        args.addElement(currentDate);

        row = (DatabaseRecord)((Vector)getSession().executeQuery(query, args)).firstElement();
    }

    //a java.util.Date object should return a java.sql.Timestamp object
    public void verify() {
        if (!(row.get("CURRENT_DATE")).getClass().equals(java.sql.Timestamp.class)) {
            throw new TestErrorException("Timestamp class not returned for java.util.Date");
        }
    }
}
