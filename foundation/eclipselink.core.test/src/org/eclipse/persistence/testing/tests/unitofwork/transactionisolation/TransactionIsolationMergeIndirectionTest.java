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

import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.models.employee.domain.Address;
import org.eclipse.persistence.testing.models.employee.domain.Employee;


/**
 * Tests the Session read refactoring / reading through the write connection
 * properly feature.
 * <p>
 * Merge test to handle the following advanced scenario:
 * <ul>
 *    <li> begin early transaction and read employee in UOW
 *    <li> trigger address indirection in UOW, do not change it
 *    <li> read address on session into shared cache
 *    <li> commit.  verify that two originals in shared cache pointing to each
 *  other, and no clones places in the shared cache.
 *  </ul>
 *  @author  smcritch
 */
public class TransactionIsolationMergeIndirectionTest extends AutoVerifyTestCase {
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
