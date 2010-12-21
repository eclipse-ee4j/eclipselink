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
package org.eclipse.persistence.testing.tests.jpql;

import java.util.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.testing.models.employee.domain.*;

public class SimpleNotMemberOfTest extends org.eclipse.persistence.testing.tests.jpql.JPQLTestCase {
    public void setup() {

        /*        ExpressionBuilder emp = new ExpressionBuilder();
                ExpressionBuilder proj = new ExpressionBuilder();
                ReportQuery subQuery = new ReportQuery(Project.class,proj);
                subQuery.addAttribute("id");
                subQuery.setSelectionCriteria(proj.equal(emp.anyOf("projects")));
                Expression exp = emp.notExists(subQuery);*/
        ExpressionBuilder projects = new ExpressionBuilder();
        ExpressionBuilder managed = new ExpressionBuilder();

        Expression subQueryWhereClause = projects.equal(managed.get("manager").anyOf("managedEmployees"));

        ReportQuery subQuery = new ReportQuery(Employee.class, managed);
        subQuery.setSelectionCriteria(subQueryWhereClause);

        Expression whereClause = projects.get("teamLeader").exists(subQuery);

        ReadAllQuery raq = new ReadAllQuery();
        raq.setReferenceClass(LargeProject.class);
        raq.setSelectionCriteria(whereClause);
        Object o = getSession().executeQuery(raq);

        // System.out.println("o = " + o);

        Vector allProjects = getSomeProjects();
        Vector selectedProjects = new Vector();
        Project currentProject;
        for (int i = 0; i < allProjects.size(); i++) {
            currentProject = (Project)allProjects.elementAt(i);
            if (shouldIncludeProject(currentProject)) {
                selectedProjects.addElement(currentProject);
            }
        }

        String ejbqlString = "SELECT OBJECT(proj) FROM Employee emp, Project proj " + " WHERE  (proj.teamLeader NOT MEMBER OF emp.manager.managedEmployees) " + "AND (emp.lastName = \"Chan\")";

        setEjbqlString(ejbqlString);
        setOriginalOject(selectedProjects);

        super.setup();
    }

    public boolean shouldIncludeProject(Project someProject) {
        return (someProject.getName().equals("Enterprise System") || someProject.getName().equals("Problem Reporting System") || someProject.getName().equals("Sales System"));
    }
}
