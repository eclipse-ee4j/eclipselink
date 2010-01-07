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
import org.eclipse.persistence.testing.models.employee.domain.Address;
import org.eclipse.persistence.testing.models.employee.domain.Employee;


/**
 * Tests the Session read refactoring / reading through the write connection
 * properly feature.
 * <p>
 * An extension to the TransactionIsolationNoOriginalsIndirectionTest which
 * further checks that triggering indirection when in an early transaction will
 * not put anything in the shared cache either.
 * @author  smcritch
 */
public class TransactionIsolationNoOriginalsIndirectionTest extends TransactionIsolationNoOriginalsTest {
    public void test() {
        super.test();

        Employee employeeClone = (Employee)unitOfWork.readObject(Employee.class);

        Address addressClone = employeeClone.getAddress();

        ReadObjectQuery cacheQuery = new ReadObjectQuery(Address.class);
        cacheQuery.checkCacheOnly();
        Address addressOriginal = (Address)getSession().executeQuery(cacheQuery);

        strongAssert(addressOriginal == null, 
                     "There should be nothing in the shared cache after triggering indirection.");
    }
}
