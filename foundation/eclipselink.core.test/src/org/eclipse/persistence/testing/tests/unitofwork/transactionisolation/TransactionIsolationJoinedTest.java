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
package org.eclipse.persistence.testing.tests.unitofwork.transactionisolation;

import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.models.employee.domain.Address;
import org.eclipse.persistence.testing.models.employee.domain.Employee;


/**
 * Tests the Session read refactoring / reading through the write connection
 * properly feature.
 * <p>
 * Merge test to handle the case of joined attributes.  In this case the
 * valueholder is considered triggered from the start.  The problem is that
 * valueFromRow would have put a UOW in the wrapped valueholder.  I.e. we are
 * never supposed to trigger transactional valueholders but in this case it
 * already is triggered.
 *  @author  smcritch
 */
public class TransactionIsolationJoinedTest extends AutoVerifyTestCase {
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

        ReadObjectQuery query = new ReadObjectQuery(Employee.class);
        query.addJoinedAttribute("address");
        Employee employeeClone = (Employee)unitOfWork.executeQuery(query);

        // Just need the pk to reset() later.
        original = employeeClone;
        originalFirstName = original.getFirstName();
        employeeClone.setFirstName("elle");

        Address addressClone = employeeClone.getAddress();
        Address originalAddress = (Address)getSession().readObject(addressClone);

        unitOfWork.commit();
        unitOfWork = null;

        // Because the address was triggered, it should get merged into the old
        // original.
        Employee newOriginal = (Employee)getSession().getIdentityMapAccessor().getFromIdentityMap(employeeClone);
        strongAssert(newOriginal != employeeClone, "Somehow the employee clone was merged into the shared cache.");

        Address newAddress = (Address)getSession().getIdentityMapAccessor().getFromIdentityMap(addressClone);
        strongAssert(newAddress == originalAddress, "Identity was lost on address accross the 1-1");
    }
}
