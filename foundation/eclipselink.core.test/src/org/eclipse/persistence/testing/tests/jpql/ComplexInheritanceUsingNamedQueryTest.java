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

import java.util.Vector;

import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.testing.framework.*;

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
public class ComplexInheritanceUsingNamedQueryTest extends org.eclipse.persistence.testing.tests.jpql.JPQLTestCase {
    protected ClassDescriptor descriptor = null;
    protected String argument = null;
    protected QueryException caughtException = null;

    public ComplexInheritanceUsingNamedQueryTest() {
        super();
        setDescription("Test that EJBQL finder - using a named query - works across inheritance hierarchy");
    }

    public ClassDescriptor getDescriptor() {
        return this.descriptor;
    }

    public void setDescriptor(ClassDescriptor d) {
        this.descriptor = d;
    }

    public void setArgument(String a) {
        this.argument = a;
    }

    public String getArgument() {
        return this.argument;
    }

    public void setup() {
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        Project project = null;
        Vector projects = getSomeProjects();
        for(int i = 0; i < projects.size(); i++) {
            project = (Project)projects.elementAt(i);
            if(project instanceof LargeProject) {
                break;
            }
        }
        setArgument(project.getName());

        //set up query, using query framework, to return a Project object which will be compared
        //against the Project object which is returned by the EJBQL query
        ReadObjectQuery roq = new ReadObjectQuery();
        roq.setReferenceClass(LargeProject.class);
        ExpressionBuilder eb = new ExpressionBuilder();
        Expression whereClause = eb.get("name").equal(getArgument());
        roq.setSelectionCriteria(whereClause);
        Project proj = (Project)getSession().executeQuery(roq);
        if(proj == null) {
            // null OriginalObject should be deemed equal to an empty Vector - but comparator
            // returns returns false.
            // Substitute null with an empty Vector to get the through this comparator limitation.
            setOriginalOject(new Vector(0));
        } else {
            setOriginalOject(proj);
        }

        //register named query with Project descriptor
        setUpSessionWithNamedQuery();
    }

    public void setUpSessionWithNamedQuery() {
        String queryName = "findLargeProjectByNameEJBQL";

        if (!(getSession().containsQuery(queryName))) {
            getAbstractSession().addAlias("ProjectBaseClass", getSession().getDescriptor(Project.class));

            //Named query must be built and registered with the session
            ReadObjectQuery query = new ReadObjectQuery();
            query.setEJBQLString("SELECT OBJECT(project) FROM ProjectBaseClass project WHERE project.name = ?1");
            query.setName(queryName);
            query.addArgument("1");
            query.setReferenceClass(Project.class);

            getSession().addQuery("findLargeProjectByNameEJBQL", query);
        }
    }

    public void test() {
        //Run named query
        try {
            Project returnedProject = (Project)getSession().executeQuery("findLargeProjectByNameEJBQL", getArgument());
            setReturnedObjects(returnedProject);
        } catch (QueryException q) {
            caughtException = q;
        }
    }

    public void verify() throws Exception {
        //Check for specific Exception described in CR#3844
        if (caughtException != null) {
            throw new TestErrorException("Test of EJBQL query against inheritance hierarchy failed when using a named query - .\n" + "This exception thrown while testing case\n" + "----- ComplexInheritanceUsingNamedQueryTest() -----\n" + caughtException.getMessage());
        }

        //Verify that object returned from EJBQL query matches one returned using query framework
        super.verify();
    }

    public void reset() {
        // Remove the named query and alias from the session
        getSession().removeQuery("findLargeProjectByBudgetEJBQL");
        getAbstractSession().getAliasDescriptors().remove("ProjectBaseClass");

        // Null out all instance variables set in this test
        setReturnedObjects(null);
        setOriginalOject(null);

        super.reset();
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }
}
