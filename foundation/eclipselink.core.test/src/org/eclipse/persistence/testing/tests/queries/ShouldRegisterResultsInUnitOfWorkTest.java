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
package org.eclipse.persistence.testing.tests.queries;

import java.util.Vector;
import java.util.Enumeration;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.descriptors.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.*;

/**
 * <b>Purpose:</b>Tests conform without registering with a wrapper policy.
 * @author Stephen McRitchie
 * @bug 2612601
 */
public class ShouldRegisterResultsInUnitOfWorkTest extends ConformResultsInUnitOfWorkTest {
    Employee newEmp;
    Employee deletedEmp;
    Employee registeredEmp;
    boolean descriptorSetting;

    public ShouldRegisterResultsInUnitOfWorkTest(boolean descriptorSetting) {
        setShouldUseWrapperPolicy(true);
        this.descriptorSetting = descriptorSetting;
        if (descriptorSetting) {
            setName("DescriptorShouldRegisterResultsInUnitOfWorkTest");
        }
    }

    public void buildConformQuery() {
        conformedQuery = new ReadAllQuery(Employee.class);
        conformedQuery.conformResultsInUnitOfWork();
        if (!descriptorSetting) {
            conformedQuery.setShouldRegisterResultsInUnitOfWork(false);
        }
    }

    public void prepareTest() {
        // load in a deleted object, and 1 registered object.
        registeredEmp = (Employee)unitOfWork.readObject(Employee.class);
        ReadObjectQuery query = new ReadObjectQuery(Employee.class);
        ExpressionBuilder builder = new ExpressionBuilder();
        query.setSelectionCriteria(builder.notEqual(registeredEmp));
        deletedEmp = (Employee)unitOfWork.executeQuery(query);
        unitOfWork.deleteObject(deletedEmp);

        if (descriptorSetting) {
            getSession().getDescriptor(Employee.class).setShouldRegisterResultsInUnitOfWork(false);
        }
    }

    public void test() {
        result = unitOfWork.executeQuery(conformedQuery);
    }

    public void verify() {
        try {
            // Check that no employees were registered and put in the UOW cache.
            Vector registeredEmployees = (unitOfWork).getIdentityMapAccessor().getAllFromIdentityMap(null, Employee.class, null, null);
            if (registeredEmployees.size() != 2) {
                throw new TestErrorException("Should be only two employees registered in UOW cache, not: " + registeredEmployees.size());
            }
            Vector employees = (Vector)result;
            if (employees.size() != 11) {
                throw new TestErrorException("11 employees should be returned by the query, not: " + employees.size());
            }
            WrapperPolicy policy = getSession().getDescriptor(Employee.class).getWrapperPolicy();
            UnitOfWorkImpl uow = (UnitOfWorkImpl)unitOfWork;
            Object unwrappedRegistered = policy.unwrapObject(registeredEmp, uow);
            Object unwrappedDeleted = policy.unwrapObject(deletedEmp, uow);
            for (Enumeration enumtr = employees.elements(); enumtr.hasMoreElements();) {
                Object next = policy.unwrapObject(enumtr.nextElement(), uow);
                if (unwrappedRegistered == next) {
                    registeredEmp = null;
                } else if (unwrappedDeleted == next) {
                    deletedEmp = null;
                }
            }

            // Check that unwrapping triggered all the objects to be cloned.
            registeredEmployees = (unitOfWork).getIdentityMapAccessor().getAllFromIdentityMap(null, Employee.class, null, null);
            if (registeredEmployees.size() != 12) {
                throw new TestErrorException("Should now be 12 employees registered in UOW cache, not: " + registeredEmployees.size());
            }
            if (registeredEmp != null) {
                throw new TestErrorException("The registered employee was not included in the result.");
            } else if (deletedEmp == null) {
                throw new TestErrorException("The deleted employee was included in the result.");
            }
        } finally {
            unitOfWork.release();
            newEmp = null;
            registeredEmp = null;
            deletedEmp = null;
        }
    }

    public void reset() {
        if (descriptorSetting) {
            getSession().getDescriptor(Employee.class).setShouldRegisterResultsInUnitOfWork(true);
        }
        super.reset();
    }
}
