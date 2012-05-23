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
package org.eclipse.persistence.testing.tests.optimization.queryandsqlcounting;

import org.eclipse.persistence.queries.DataModifyQuery;
import org.eclipse.persistence.sessions.UnitOfWork;

public class BatchWritingFlushInWriteChangesTest extends BatchWritingFlushQueryTest {
    public BatchWritingFlushInWriteChangesTest() {
        EXPECTED_INITIAL_QUERIES = 2;
        setDescription("Test WriteChanges flushes batch statements when using the Dynamic batch writing mechanism.");
    }
    
    public void test() {
        UnitOfWork uow = getSession().acquireUnitOfWork();

        myDataModifyQueryObj1 = new DataModifyQuery("UPDATE EMPLOYEE SET F_NAME = 'Fatima?' WHERE L_NAME = 'Smith'");
        myDataModifyQueryObj2 = new DataModifyQuery("UPDATE EMPLOYEE SET F_NAME = 'Fatima2?' WHERE L_NAME = 'Smith'");
        uow.executeQuery(myDataModifyQueryObj1);
        uow.executeQuery(myDataModifyQueryObj2);
        
        initialSQLStatements = tracker.getSqlStatements().size();
        initialQueries = tracker.getQueries().size();

        //feature to be tested:
        uow.writeChanges();

        secondSQLStatements = tracker.getSqlStatements().size();
        secondQueries = tracker.getQueries().size();

        uow.commit();
    }
}
