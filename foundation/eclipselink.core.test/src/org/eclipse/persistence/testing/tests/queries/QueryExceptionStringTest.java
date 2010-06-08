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
package org.eclipse.persistence.testing.tests.queries;

import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.testing.models.employee.domain.Employee;
import org.eclipse.persistence.testing.framework.TestErrorException;

/**
 * Tests that the query exception strings contain the query name and
 * domain class, if available.
 *
 * @author Guy Pelletier
 * @date January 21, 2003
 */
public class QueryExceptionStringTest extends MultiNameQueriesTestCase {
    private DatabaseSession session;
    private String exception1;
    private String exception2;
    private String exception3;
    private String exception4;
    private String bogusQueryName = "bogusName";
    private String realQueryName = "namedQuerySameName";

    public QueryExceptionStringTest() {
        setDescription("Testing query error strings");
    }

    public void reset() {
        session.removeQuery(realQueryName);
        session.getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    public void setup() {
        session = (DatabaseSession)getSession();
        session.addQuery(realQueryName, getNamedQueryFirstName());
    }

    public void test() {
        // catch exception, this covers 
        // executeQuery(String, Object)
        // executeQuery(String, Object, Object)
        // executeQuery(String, Object, Object, Object)
        // executeQuery(String, Vector)
        try {
            session.executeQuery(bogusQueryName, new String("Jill"));
        } catch (Exception e) {
            exception1 = e.getMessage();
        }

        // catch exception, this covers
        // executeQuery(String, Class, Object)
        // executeQuery(String, Class, Object, Object)
        // executeQuery(String, Class, Object, Object, Object)
        // executeQuery(String, Class, Vector)
        try {
            session.executeQuery(bogusQueryName, Employee.class, new String("Jill"));
        } catch (Exception e) {
            exception2 = e.getMessage();
        }

        // catch exception, covers
        // executeQuery(String, Class)
        try {
            session.executeQuery(bogusQueryName, Employee.class);
        } catch (Exception e) {
            exception3 = e.getMessage();
        }

        // catch exception, covers
        // executeQuery(String)
        try {
            session.executeQuery(bogusQueryName);
        } catch (Exception e) {
            exception4 = e.getMessage();
        }

        // too bad for these ones ... too late to capture name or class
        // executeQuery(DatabaseQuery)
        // executeQuery(DatabaseQuery, DatabaseRow)
        // executeQuery(DatabaseQuery, Vector)
    }

    public void verify() {
        if (exception1.indexOf(bogusQueryName) == -1) {
            throw new TestErrorException("Test failed on exception1 ... see testcase");
        }

        if (exception2.indexOf(bogusQueryName) == -1) {
            throw new TestErrorException("Test failed on exception2 ... see testcase");
        }

        if (exception3.indexOf(bogusQueryName) == -1) {
            throw new TestErrorException("Test failed on exception3 ... see testcase");
        }

        if (exception4.indexOf(bogusQueryName) == -1) {
            throw new TestErrorException("Test failed on exception4 ... see testcase");
        }
    }
}
