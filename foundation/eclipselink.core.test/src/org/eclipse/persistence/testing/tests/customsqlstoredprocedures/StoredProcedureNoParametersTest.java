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

import org.eclipse.persistence.queries.StoredProcedureCall;
import org.eclipse.persistence.testing.framework.*;

public class StoredProcedureNoParametersTest extends TestCase {

    public StoredProcedureNoParametersTest() {
        super();
    }

    public void setup() {
        //right now only the stored procedure is set up in SQLServer
        if (!getSession().getPlatform().isSybase() && !getSession().getPlatform().isSQLAnywhere()) {
            throw new TestWarningException("This test intended for Sybase and SQLAnywhere");
        }
        getAbstractSession().beginTransaction();
    }

    public void test() {
        StoredProcedureCall call = new StoredProcedureCall();
        call.setProcedureName("Rise_All_Salaries");
        getSession().executeNonSelectingCall(call);
    }

    public void reset() {
        if(getAbstractSession().isInTransaction()) {
            getAbstractSession().rollbackTransaction();
        }
    }
}
