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
//     dminsky - initial API and implementation
package org.eclipse.persistence.testing.tests.queries;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.DescriptorQueryManager;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.internal.databaseaccess.DatasourceCall;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.employee.domain.Employee;

import java.util.ArrayList;
import java.util.List;

/**
 * Test to ensure that DescriptorQueryManager's cached update calls do not have their
 * 'query' reference set. This is to prevent a memory leak from occurring.
 * @author dminsky
 */
public class CachedUpdateCallsQueryClearTest extends TestCase {

    public CachedUpdateCallsQueryClearTest() {
        setDescription("Test that the DescriptorQueryManager's cached update calls query attribute is de-referenced");
    }

    @Override
    public void setup() {
        getDatabaseSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        getDatabaseSession().beginTransaction();
    }

    @Override
    public void test() {
        UnitOfWork uow = getDatabaseSession().acquireUnitOfWork();
        Employee employee = (Employee) uow.readObject(
            Employee.class,
            new ExpressionBuilder().get("lastName").equal("Smitty"));
        assertNotNull(employee);
        employee.setLastName("Archibald");
        uow.commit();
    }

    @Override
    public void verify() {
        ClassDescriptor descriptor = getSession().getDescriptor(Employee.class);
        DescriptorQueryManager descriptorQueryManager = descriptor.getDescriptorQueryManager();
        // in the update transaction in test(), the lastName and the version fields are updated
        List<DatabaseField> fields = new ArrayList<>(2);
        fields.add(descriptor.getMappingForAttributeName("lastName").getField());
        fields.add(descriptor.getOptimisticLockingPolicy().getWriteLockField());

        List<DatasourceCall> cachedUpdateCalls = descriptorQueryManager.getCachedUpdateCalls(fields);
        assertNotNull(cachedUpdateCalls);
        assertFalse(cachedUpdateCalls.isEmpty());

        for (DatasourceCall call : cachedUpdateCalls) {
            // calls should not cache a query
            if (call.getQuery() != null) {
                throw new TestErrorException("DatasourceCall's query is not null: " + call);
            }
        }
    }

    @Override
    public void reset() {
        getDatabaseSession().rollbackTransaction();
        getDatabaseSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

}
