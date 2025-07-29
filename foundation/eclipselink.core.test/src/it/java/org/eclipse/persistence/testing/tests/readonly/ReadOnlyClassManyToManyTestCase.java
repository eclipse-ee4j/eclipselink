/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.readonly;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.mapping.Employee;
import org.eclipse.persistence.testing.models.mapping.Phone;

/**
 * <p>
 * <b>Purpose</b>: Test updating a non-read-only object which has a reference to a read-only
 * object which refers back to the non-read-only object.
 * <p>
 * <b>Responsibilities</b>:
 * <ul>
 * <li> Verify that modifications to the read-only objects do not get written to the database.
 * </ul>
 */
public class ReadOnlyClassManyToManyTestCase extends AutoVerifyTestCase {
    public Employee originalEmployee;
    UnitOfWork uow;
    Phone originalPhone;
    String origAreaCode;

    public ReadOnlyClassManyToManyTestCase() {
        super();
    }

    @Override
    public void reset() {
        originalPhone.areaCode = origAreaCode;
        rollbackTransaction();
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
    }

    @Override
    protected void setup() {
        beginTransaction();
        originalEmployee = (Employee)getSession().readObject(Employee.class);
        originalPhone = (Phone)originalEmployee.getPhoneNumbers().get(0);
        origAreaCode = originalPhone.areaCode;

        uow = getSession().acquireUnitOfWork();
        uow.addReadOnlyClass(Phone.class);
        Employee cloneEmp = (Employee)uow.registerObject(originalEmployee);

        // Change the one of the Employee's Phones and one of the Shipments.
        ((Phone)cloneEmp.getPhoneNumbers().get(0)).setAreaCode("000");
    }

    @Override
    protected void test() {
        uow.commit();
    }

    @Override
    protected void verify() {
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        // Get the version from the database and compare to the original.
        ExpressionBuilder expBuilder = new ExpressionBuilder();
        Expression exp = expBuilder.get("id").equal(originalPhone.id);
        Phone dbPhone = (Phone)getSession().readObject(Phone.class, exp);
        if (!origAreaCode.equals(dbPhone.areaCode)) {
            throw new TestErrorException("We succeed in changing a read-only objects in a logical 1:M implemented with a M:M mapping. This is very bad!");
        }
    }
}
