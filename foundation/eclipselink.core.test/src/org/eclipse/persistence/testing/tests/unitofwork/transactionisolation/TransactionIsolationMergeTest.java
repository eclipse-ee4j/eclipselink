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
 * While NoOriginals test verifies that no originals are placed in the shared
 * cache until commit time, this test verifies that changes, and an original,
 * are placed in the shared cache after commit.
 * @author  smcritch
 */
public class TransactionIsolationMergeTest extends AutoVerifyTestCase {
    UnitOfWork unitOfWork;
    Employee original;
    String originalFirstName;

    protected void setup() throws Exception {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        unitOfWork = getSession().acquireUnitOfWork();
    }

    public void reset() throws Exception {
        if (unitOfWork != null) {
            unitOfWork.release();
            unitOfWork = null;
        }
        unitOfWork = getSession().acquireUnitOfWork();
        Employee clone = (Employee)unitOfWork.readObject(original);
        clone.setFirstName(originalFirstName);
        unitOfWork.commit();
        unitOfWork = null;
        originalFirstName = null;
        original = null;
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    public void test() {
        unitOfWork.beginEarlyTransaction();

        Employee employeeClone = (Employee)unitOfWork.readObject(Employee.class);

        // Just need the pk to reset() later.
        original = employeeClone;
        originalFirstName = original.getFirstName();

        employeeClone.setFirstName("elle");
        unitOfWork.commit();

        unitOfWork = null;

        ReadObjectQuery cacheQuery = new ReadObjectQuery(Employee.class);
        cacheQuery.checkCacheOnly();
        Employee newOriginal = (Employee)getSession().executeQuery(cacheQuery);

	strongAssert(newOriginal != null,
			"There should be an original in the shared cache.");	
	strongAssert(newOriginal != employeeClone,
			"commit placed a clone into the shared cache.");
    }
}
