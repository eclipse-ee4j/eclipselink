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


// Java imports
import java.util.*;

// Domain imports
import org.eclipse.persistence.testing.models.employee.domain.*;

//TopLink imports
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.expressions.*;

public class SmallProjectMemberOfProjectsTest extends JPQLTestCase {
    private Vector employeesWithSmallProjects = null;

    public void setup() {
        String ejbqlString = null;

        //query for those employees with SmallProjects using an expression
        ReadAllQuery query = new ReadAllQuery();
        Expression selectionCriteria = new ExpressionBuilder().anyOf("projects").equal(new ExpressionBuilder(SmallProject.class));
        query.setSelectionCriteria(selectionCriteria);
        query.setReferenceClass(Employee.class);

        employeesWithSmallProjects = (Vector)getSession().executeQuery(query);

        setOriginalOject(employeesWithSmallProjects);

        //setup the EJBQL to do the same
        ejbqlString = "SELECT OBJECT(employee) FROM Employee employee, SmallProject sp WHERE ";
        ejbqlString = ejbqlString + "sp MEMBER OF employee.projects";
        setEjbqlString(ejbqlString);
        super.setup();
    }
}
