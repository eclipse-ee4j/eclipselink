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
package org.eclipse.persistence.testing.tests.feature;

import java.util.*;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.testing.models.employee.relational.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.internal.databaseaccess.*;

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
        params.addElement("Bob");
        Vector result = (Vector)session.executeQuery(query, params);

        ((DatabaseAccessor)((AbstractSession)session).getAccessor()).closeConnection();

        ((DatabaseAccessor)((AbstractSession)session).getAccessor()).reestablishConnection((AbstractSession)session);

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

    public void verify() {
        if (reestablishErrorException != null) {
            throw new TestErrorException("The statement cache was not properly flushed when a connection was reestablished.");
        }
        if (logoutErrorException != null) {
            throw new TestErrorException("The statement cache was not properly flushed when a connection was disconnected.");
        }
    }

    public void reset() {
        session.logout();
        // Must reconnect the previous session.
        ((org.eclipse.persistence.sessions.DatabaseSession)getSession()).login();
    }
}
