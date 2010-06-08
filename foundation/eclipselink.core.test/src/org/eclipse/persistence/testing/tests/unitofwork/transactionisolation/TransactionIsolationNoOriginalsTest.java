/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.tests.unitofwork.transactionisolation;

import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.models.employee.domain.Employee;


/**
 * Tests the Session read refactoring / reading through the write connection
 * properly feature.
 * <p>
 * This test verifies that originals of objects read by a UnitOfWork in early
 * transaction are not placed in the shared cache until commit/merge time.
 * <p>
 * If the object is not registered in the UnitOfWork, you would think it would
 * be safe to put the original in the shared cache, for if not registered must
 * not have changed it.  However this feature is being quite particular about
 * not putting anything in the shared cache, even if we are pretty sure we can
 * get away with it.  I.e. maybe they executed a direct no select and messed up
 * some rows, even though we haven't registered anything yet.
 * @author  smcritch
 */
public class TransactionIsolationNoOriginalsTest extends AutoVerifyTestCase {
    UnitOfWork unitOfWork;

    protected void setup() throws Exception {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        unitOfWork = getSession().acquireUnitOfWork();
    }

    public void reset() throws Exception {
        if (unitOfWork != null) {
            getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
            unitOfWork.release();
            unitOfWork = null;
        }
    }

    public void test() {
        unitOfWork.beginEarlyTransaction();

        Employee employeeClone = (Employee)unitOfWork.readObject(Employee.class);

        strongAssert(employeeClone != null, "Executing reads in early transaction doesn't work.");

        ReadObjectQuery cacheQuery = new ReadObjectQuery(Employee.class);
        cacheQuery.checkCacheOnly();

        Employee employee = (Employee)getSession().executeQuery(cacheQuery);

        strongAssert(employee == null, "There should be nothing in the shared cache.");
    }
}
