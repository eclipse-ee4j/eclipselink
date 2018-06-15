/*
 * Copyright (c) 2015, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.identitymaps.cacheinvalidation;

import org.eclipse.persistence.config.CacheIsolationType;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.IdentityMapAccessor;
import org.eclipse.persistence.sessions.server.ClientSession;
import org.eclipse.persistence.sessions.server.ServerSession;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestExecutor;
import org.eclipse.persistence.testing.models.employee.domain.Employee;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Bug #471743
 * Tests the invalidate all from IdentityMapAccessor.
 */
public class InvalidateAllExternalChangeTest extends AutoVerifyTestCase {
    private TestExecutor executor;
    private ServerSession serverSession;
    private ReadObjectQuery query;
    private BigDecimal originalEmployeeAddressId;
    private String originalEmployeeName;
    private DatabaseLogin login;

    public void reset() {
        executor.resetSession();
    }

    protected void setup() {
        executor = getExecutor();
        executor.swapServerSession();
        serverSession = (ServerSession) getSession();
        serverSession.getProject().setHasIsolatedClasses(true); // IsolatedClientSession is required

        ClassDescriptor descriptor = serverSession.getDescriptor(Employee.class);
        descriptor.setCacheIsolation(CacheIsolationType.PROTECTED);
    }

    public void test() throws Exception {
        ClientSession clientSession = serverSession.acquireClientSession();
        Employee initialEmployee = (Employee) clientSession.executeQuery(new ReadObjectQuery(Employee.class));
        assertNotNull("An Employee object could not be retrieved", initialEmployee);
        originalEmployeeAddressId = initialEmployee.getAddressId();

        query = new ReadObjectQuery(Employee.class);
        Expression expression = new ExpressionBuilder().get("address").get("id").equal(originalEmployeeAddressId);
        query.setSelectionCriteria(expression);

        Employee originalEmployee = (Employee) clientSession.executeQuery(query);
        assertNotNull("The Employee object could not be retrieved", originalEmployee);
        originalEmployeeName = originalEmployee.getFirstName();

        IdentityMapAccessor identityMapAccessor = serverSession.getIdentityMapAccessor();
        identityMapAccessor.clearQueryCache();
        identityMapAccessor.invalidateAll();

        login = clientSession.getLogin();
        try (Connection connection = DriverManager.getConnection(login.getDatabaseURL(), login.getUserName(), login.getPassword())) {
            connection.prepareStatement("update EMPLOYEE set F_NAME = 'Updated' where ADDR_ID = " + originalEmployeeAddressId).execute();
        }
    }

    protected void verify() {
        ClientSession clientSession = serverSession.acquireClientSession();
        Employee modifiedEmployee = (Employee) clientSession.executeQuery(query);
        try {
            assertNotSame("Modified Employee was not loaded from the database", originalEmployeeName, modifiedEmployee.getFirstName());
        } finally {
            try (Connection connection = DriverManager.getConnection(login.getDatabaseURL(), login.getUserName(), login.getPassword())) {
                connection.prepareStatement("update EMPLOYEE set F_NAME = '" + originalEmployeeName + "' where ADDR_ID = " + originalEmployeeAddressId).execute();
            } catch (Exception x) {
                fail("Could not revert employee name:\n" + Helper.printStackTraceToString(x));
            }
        }
    }
}
