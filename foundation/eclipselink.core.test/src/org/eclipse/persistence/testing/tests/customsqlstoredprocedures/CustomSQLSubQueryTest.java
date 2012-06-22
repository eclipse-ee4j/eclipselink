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
package org.eclipse.persistence.testing.tests.customsqlstoredprocedures;

import java.util.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.Employee;

/**
 * <p>
 * <b>Purpose</b>: Execute a ReadAllQuery which uses a custom SQL subquery on the database.
 * <p>
 * <b>Responsibilities</b>:
 * <ul>
 * <li> Execute the read all query with the subquery and verify no errors occurred.
 * <li> Verify the objects returned match the original number of objects.
 * </ul>
 */
public class CustomSQLSubQueryTest extends AutoVerifyTestCase {
    protected ReadAllQuery query;
    protected int numberOfManagedEmployees;
    protected Object objectsFromDatabase;
    protected Class referenceClass;
    protected Employee someManager;

    public CustomSQLSubQueryTest() {
        referenceClass = Employee.class;
        setName("CustomSQLSubQueryTest(" + referenceClass.getName() + ")");
        setDescription("The test runs a ReadAllQuery with a customSQL subquery to the database.");
    }

    protected void setup() {
        getSession().getIdentityMapAccessor().initializeIdentityMaps();

        // This employee as a manager
        someManager = (Employee)getSession().readObject(referenceClass/*,
            bldr.get("firstName").equal("Sarah").and(bldr.get("lastName").equal("Way"))*/);
        //number of managed employees
        numberOfManagedEmployees = someManager.getManagedEmployees().size();
    }

    protected void test() {
        query = new ReadAllQuery();
        query.setReferenceClass(referenceClass);

        ReportQuery hierarchyQuery = new ReportQuery();
        hierarchyQuery.setReferenceClass(referenceClass);
        // The #employeeId is what would cause the error
        hierarchyQuery.setCall(new SQLCall("SELECT EMP_ID FROM EMPLOYEE WHERE MANAGER_ID=#employeeId"));

        query.setSelectionCriteria(query.getExpressionBuilder().get("id").in(hierarchyQuery));
        // want the argument at the top level query
        query.addArgument("employeeId");
    }

    /**
     * Verify that the query ran successfully, and
     * that the number of objects returned matches the number of managed employees.
     */
    protected void verify() throws Exception {
        Vector params = new Vector();
        params.add(someManager.getId());//numberOfManagedEmployees
        try {
            Vector results = (Vector)getSession().executeQuery(query, params);
            if (!(numberOfManagedEmployees == results.size())) {
                throw new TestErrorException(results.size() + " objects were read from the database, but originially there were, " + numberOfManagedEmployees + ".");
            }
        } catch (org.eclipse.persistence.exceptions.DatabaseException e) {
            throw new TestErrorException("Custom SQL subquery with parameters failed with a DatabaseException.");
        }
    }
}
