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
package org.eclipse.persistence.testing.tests.unitofwork;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.descriptors.OptimisticLockingPolicy;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestWarningException;
import org.eclipse.persistence.testing.models.employee.domain.Employee;


/**
 * <P><B>Purpose</B>:     To make sure that reading an object containing a bidirectional
 *                        relationship with a value holder does not cause an infinite loop when
 *                        using NoIdentityMap.
 *
 * <P><B>Motivation</B>:     This test was written to fix a bug.  When using NoIdentityMap, if an
 *                            Employee (which has a bidirectional relationship with a ValueHolder
 *                            managedEmployees) is read, it checks to see if a managedEmployee is in
 *                            the cache, and since there is no cache, reads the managedEmployee.  When
 *                            the managedEmployee is read, it too checks to see if its manager is in the
 *                            cache, and since there is none, reads its manager, hence causing an
 *                            infinite loop.
 *
 * <P><B>Design</B>:     First, the Employee descriptor is changed to use NoIdentityMap.  Next, a
 *                        Unit Of Work is acquired, and an attempt is made to read and change an Employee.
 *                        After the Employee is read, the Employee descriptor is reset to its original state.
 *
 * <P><B>Responsibilities</B>:     Verify that the above conditions do not cause an infinite loop.
 *
 * <P><B>Features Used</B>:     Unit Of Work, (No) Identity Map
 *
 * <P><B>Paths Covered</B>:     Attempting to read a org.eclipse.persistence.testing.models.employee.domain.Employee covers:
 *        <UL>
 *            <LI><CODE>manager</CODE> 1:1 Mapping
 *            <LI><CODE>managedEmployees</CODE> 1:M Mapping
 *        </UL>
 */
public class NoIMWithValueHolderTest extends AutoVerifyTestCase {
    public UnitOfWork unitOfWork;
    public ClassDescriptor originalEmployeeDescriptor;
    public OptimisticLockingPolicy policy;

    public NoIMWithValueHolderTest() {
        super();
        setDescription("This test case should NOT get into an infinite loop (if you're reading this, it passed).");
    }

    public void reset() {
        if (getSession() instanceof org.eclipse.persistence.sessions.remote.RemoteSession) {
            return;
        }
        getAbstractSession().rollbackTransaction();
        // Reset the original Employee descriptor
        originalEmployeeDescriptor = getSession().getClassDescriptor(Employee.class);
        originalEmployeeDescriptor.useFullIdentityMap();
        originalEmployeeDescriptor.setOptimisticLockingPolicy(this.policy);
        originalEmployeeDescriptor.getQueryManager().checkCacheForDoesExist();
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    public void setup() {
        if (getSession() instanceof org.eclipse.persistence.sessions.remote.RemoteSession) {
            throw new TestWarningException("This test cannot be run through the remote.");
        }
        getAbstractSession().beginTransaction();
        // Save the original Employee descriptor
        originalEmployeeDescriptor = getSession().getClassDescriptor(Employee.class);
        originalEmployeeDescriptor.useNoIdentityMap();
        this.policy = originalEmployeeDescriptor.getOptimisticLockingPolicy();
        originalEmployeeDescriptor.setOptimisticLockingPolicy(null);
        originalEmployeeDescriptor.getQueryManager().assumeExistenceForDoesExist();
    }

    protected void test() {
        // Attempt to read and change an Employee
        unitOfWork = getSession().acquireUnitOfWork();
        Employee employee = (Employee)unitOfWork.readObject(Employee.class);
        employee.setFirstName("Yahoo");
        employee.setLastName("Serious");
        employee.setMale();
        unitOfWork.commit();
    }
}
