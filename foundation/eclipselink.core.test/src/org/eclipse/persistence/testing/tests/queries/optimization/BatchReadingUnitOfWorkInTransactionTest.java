/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.tests.queries.optimization;

import java.util.*;

import org.eclipse.persistence.annotations.BatchFetchType;
import org.eclipse.persistence.queries.*;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.*;

/**
 *  This testcase verifies the bug fix about batch reading through a UnitOfWork while already in a transaction
 */
public class BatchReadingUnitOfWorkInTransactionTest extends TestCase {
    BatchFetchType batchType;
    public org.eclipse.persistence.sessions.UnitOfWork myUOW;
    private ReadAllQuery myQuery;

    public BatchReadingUnitOfWorkInTransactionTest(BatchFetchType batchType) {
        setDescription("This test verifies that batch reading works correctly in a unit of work while a transaction has already started");
        this.batchType = batchType;
        setName(getName() + batchType);
    }

    public ReadAllQuery getMyQuery() {
        return myQuery;
    }

    public org.eclipse.persistence.sessions.UnitOfWork getMyUOW() {
        return myUOW;
    }

    protected void setMyQuery(ReadAllQuery newValue) {
        this.myQuery = newValue;
    }

    public void setMyUOW(org.eclipse.persistence.sessions.UnitOfWork newValue) {
        this.myUOW = newValue;
    }

    public void setup() {
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        setMyUOW(getSession().acquireUnitOfWork());
        setMyQuery(new ReadAllQuery());
        getMyQuery().setBatchFetchType(batchType);
        getMyQuery().setReferenceClass(Employee.class);
        getMyQuery().addBatchReadAttribute("address");
    }

    public void test() {

        // Need to start a transaction
        getMyUOW().executeNonSelectingCall(new org.eclipse.persistence.queries.SQLCall("UPDATE EMPLOYEE SET L_NAME = 'Jefferson' WHERE (L_NAME = 'Jefferson')"));
        // don't do an update.
        try {
            Vector employees = (Vector)getMyUOW().executeQuery(getMyQuery());
            ((Employee)employees.firstElement()).getAddress().getCity();
            getMyUOW().commit(); // no changes so rollback not required.
        } catch (Exception exception) {
            getMyUOW().release();
        }
    }
}
