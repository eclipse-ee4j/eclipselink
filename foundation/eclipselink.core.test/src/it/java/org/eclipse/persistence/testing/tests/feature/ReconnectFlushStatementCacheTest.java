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
package org.eclipse.persistence.testing.tests.feature;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.employee.domain.Employee;
import org.eclipse.persistence.testing.models.employee.relational.EmployeeProject;

import java.util.Vector;

/**
 * Bug 3046465 - Ensure that the statement cache gets flushed when reconnecting to the database
 */
public class ReconnectFlushStatementCacheTest extends AutoVerifyTestCase {
    protected DatabaseSession session = null;
    protected Exception reestablishErrorException = null;
    protected Exception logoutErrorException = null;

    public ReconnectFlushStatementCacheTest() {
        setDescription("Ensure the statement cache gets flushed on a reconnect.");
    }

    @Override
    public void setup() {
        // Disconnected the current session and log-on a temporary one
        ((org.eclipse.persistence.sessions.DatabaseSession)getSession()).logout();
        DatabaseLogin login = (DatabaseLogin)(getSession().getLogin().clone());

        // any project that uses sequencing will do
        org.eclipse.persistence.sessions.Project project = new EmployeeProject();
        project.setLogin(login);
        session = project.createDatabaseSession();
        session.setSessionLog(getSession().getSessionLog());
        session.login();
    }

    @Override
    public void test() {
        // Set binding and caching to ensure we encouter the error
        session.getLogin().bindAllParameters();
        session.getLogin().cacheAllStatements();

        ExpressionBuilder builder = new ExpressionBuilder();
        ReadAllQuery query = new ReadAllQuery(Employee.class, builder);
        Expression exp = builder.get("firstName").equal(builder.getParameter("NAME"));
        query.setSelectionCriteria(exp);
        query.addArgument("NAME");

        Vector params = new Vector();
        params.add("Bob");
        Vector result = (Vector)session.executeQuery(query, params);

        ((AbstractSession)session).getAccessor().closeConnection();

        ((AbstractSession)session).getAccessor().reestablishConnection((AbstractSession)session);

        try {
            // this query will fail if the cache is not flushed
            result = (Vector)session.executeQuery(query, params);
        } catch (Exception exception) {
            // catch Exception (instead of something more specific) because different databases may throw different exceptions.
            reestablishErrorException = exception;
        }

        // ensure logout properly closes the statements
        session.logout();
        session.login();
        try {
            // this query will fail if the cache is not flushed
            result = (Vector)session.executeQuery(query, params);
        } catch (Exception exception) {
            // catch Exception (instead of something more specific) because different databases may throw different exceptions.
            logoutErrorException = exception;
        }
    }

    @Override
    public void verify() {
        if (reestablishErrorException != null) {
            throw new TestErrorException("The statement cache was not properly flushed when a connection was reestablished.");
        }
        if (logoutErrorException != null) {
            throw new TestErrorException("The statement cache was not properly flushed when a connection was disconnected.");
        }
    }

    @Override
    public void reset() {
        session.logout();
        // Must reconnect the previous session.
        ((org.eclipse.persistence.sessions.DatabaseSession)getSession()).login();
    }
}
