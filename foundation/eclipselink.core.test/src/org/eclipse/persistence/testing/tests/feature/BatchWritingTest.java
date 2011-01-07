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
package org.eclipse.persistence.testing.tests.feature;

import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.internal.databaseaccess.DatabaseAccessor;
import org.eclipse.persistence.testing.models.employee.domain.Address;

/**
 * This tests the use of batch writing for a large number of inserts.
 */
public class BatchWritingTest extends TransactionalTestCase {
    protected static int NUM_INSERTS = 200;

    public BatchWritingTest() {
        setDescription("Tests a large number of inserts using Batch Writing");
    }

    public void test() {
        Address address;
        UnitOfWork uow = getSession().acquireUnitOfWork();
        for (int i = 0; i < NUM_INSERTS; i++) {
            address = new Address();
            address.setCity("city" + i);
            address.setProvince("province" + i);
            uow.registerObject(address);
        }
        uow.commit();
        //a little hack to force the SQL to go to the Database
        ((DatabaseAccessor)uow.getParent().getAccessor()).getActiveBatchWritingMechanism().executeBatchedStatements(uow.getParent());
    }
}
