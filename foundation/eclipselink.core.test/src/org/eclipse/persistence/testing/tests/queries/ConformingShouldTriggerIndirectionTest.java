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
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.testing.models.employee.domain.*;

/**
 * Test for bug 3568141: CONFORMING QUERY DOESN'T RETURN CORRECT ROWS WHEN INDIRECTION IS USED
 * <p>
 * We decoupled the in-memory query indirection policy from conforming, making
 * it impossible for a user to set shouldTriggerIndirection() on a conforming
 * query.
 * <p>
 * This broke a lot of customers, so we had to undo what we did.
 *
 *  @author  smcritch
 */
public class ConformingShouldTriggerIndirectionTest extends AutoVerifyTestCase {
    public ConformingShouldTriggerIndirectionTest() {
    }

    public void test() throws Exception {
        UnitOfWork uow = getSession().acquireUnitOfWork();

        ExpressionBuilder proj = new ExpressionBuilder();
        Expression outOfOttawaProjectsExp = proj.get("teamLeader").get("address").get("city").notEqual("Ottawa");

        ReadAllQuery originalQuery = new ReadAllQuery(Project.class);
        originalQuery.setSelectionCriteria(outOfOttawaProjectsExp);

        Vector outOfOttawaProjects = (Vector)uow.executeQuery(originalQuery);

        ExpressionBuilder emp = new ExpressionBuilder();
        Expression ottawaEmpsExp = emp.get("address").get("city").equal("Ottawa");

        ReadAllQuery employeeQuery = new ReadAllQuery(Employee.class);
        employeeQuery.setSelectionCriteria(ottawaEmpsExp);

        Vector ottawaEmps = (Vector)uow.executeQuery(employeeQuery);

        Project conformingProject = (Project)outOfOttawaProjects.elementAt(0);
        Employee conformingEmp = (Employee)ottawaEmps.elementAt(0);

        // Now this project will no longer conform.
        // The crux is that this project's teamleader's address was changed
        // without triggering all valueholders.  Hence the default heuristic
        // conforming would assume that project has not changed relative to the
        // query.
        // The 2 levels of indirection were needed because setting an attribute
        // (changing an object) automatically triggers one level of indirection.
        conformingProject.setTeamLeader(conformingEmp);

        // Now execute the original query again as a conforming query.  This
        // time the project that was changed should get dropped from the result.
        ReadAllQuery conformingQuery = originalQuery;
        conformingQuery.setInMemoryQueryIndirectionPolicy(new InMemoryQueryIndirectionPolicy(InMemoryQueryIndirectionPolicy.SHOULD_TRIGGER_INDIRECTION));
        conformingQuery.conformResultsInUnitOfWork();

        Vector conformingProjects = (Vector)uow.executeQuery(conformingQuery);

        if ((conformingProjects.size() + 1) != outOfOttawaProjects.size()) {
            throw new TestErrorException("The result was not conformed.  Original: " + outOfOttawaProjects + " Conforming: " + conformingProjects);
        }
    }
}
