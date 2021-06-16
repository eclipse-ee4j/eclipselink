/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
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
