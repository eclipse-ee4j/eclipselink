/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.jpql;

//CR#3844 - class created on April 10, 2002

/* EJBQL finder does not work with an inheritance hierarchy.In a model with inheritance,
an EJBQL finder executed on a base class gives an error regarding invalid table
in context of a subclass table.

UsingEmployee demo, the following EJBQL is executed on the Project class:
"SELECT OBJECT(project) FROM Project project WHERE project.name = ?1");
with the value "Swirly Dirly", which is the name of the LargeProject
(a subclass of the Project class).
The following Exception is thrown:

EXCEPTION DESCRIPTION: The field [DatabaseField(LPROJECT.PROJ_ID)] in this
expression has an invalid table in this context. */

// Domain imports
import java.util.Vector;

import org.eclipse.persistence.testing.models.employee.domain.*;

//TopLink imports
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.expressions.*;

public class ComplexInheritanceTest extends org.eclipse.persistence.testing.tests.jpql.JPQLTestCase {
    public ComplexInheritanceTest() {
        super();
        setDescription("Test that EJBQL finder works across inheritance hierarchy");
    }

    public void setup() {
        //
        // Set up query, using query framework, to return a Project object which will be compared
        // against the Project object which is returned by the EJBQL query
        String projectName = null;

        getAbstractSession().addAlias("ProjectBaseClass", getSession().getDescriptor(Project.class));
        Project project = null;
        Vector projects = getSomeProjects();
        for(int i = 0; i < projects.size(); i++) {
            project = (Project)projects.elementAt(i);
            if(project instanceof LargeProject) {
                break;
            }
        }
        projectName = project.getName();
        ReadObjectQuery roq = new ReadObjectQuery();
        ExpressionBuilder eb = new ExpressionBuilder();
        Expression whereClause = eb.get("name").equal(projectName);
        roq.setSelectionCriteria(whereClause);
        roq.setReferenceClass(LargeProject.class);
        LargeProject proj = (LargeProject)getSession().executeQuery(roq);

        //Set Project object which will be compared against the one returned by EJBQL
        if(proj == null) {
            // null OriginalObject should be deemed equal to an empty Vector - but comparator
            // returns returns false.
            // Substitute null with an empty Vector to get the through this comparator limitation.
            setOriginalOject(new Vector(0));
        } else {
            setOriginalOject(proj);
        }

        //Set criteria for EJBQL and call super-class method to construct the EJBQL query
        String ejbql = "SELECT OBJECT(project) FROM ProjectBaseClass project WHERE project.name = \"" + projectName + "\"";

        this.setEjbqlString(ejbql);
        this.setReferenceClass(LargeProject.class);
    }

    public void reset() {
        // Null out instance variables that were set in this test
        getAbstractSession().getAliasDescriptors().remove("ProjectBaseClass");
        super.reset();
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }
}
