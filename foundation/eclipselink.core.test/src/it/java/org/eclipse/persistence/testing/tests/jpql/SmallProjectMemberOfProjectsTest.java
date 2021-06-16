/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
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
