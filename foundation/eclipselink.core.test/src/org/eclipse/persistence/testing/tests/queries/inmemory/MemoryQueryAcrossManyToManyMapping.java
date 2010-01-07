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
package org.eclipse.persistence.testing.tests.queries.inmemory;

import java.util.Vector;
import java.util.Enumeration;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.expressions.*;

public class MemoryQueryAcrossManyToManyMapping extends TestCase {
    protected Employee employee;
    protected ReadAllQuery queryAll;
    protected ReadAllQuery queryObjects;
    protected ReadObjectQuery queryObject;
    protected java.util.Vector allEmployees;
    protected java.util.Vector employees;

    public MemoryQueryAcrossManyToManyMapping() {
        super();
    }

    public void reset() {
        //clear the cache.
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    /**
     * This is  still in the works.
     */
    public void setup() {
        allEmployees = new Vector();
        employees = new Vector();
        queryAll = new ReadAllQuery();
        queryAll.setReferenceClass(Employee.class);
        queryAll.setSelectionCriteria(new ExpressionBuilder().anyOf("projects").get("name").equal("Enterprise"));
        getSession().removeQuery("getAllEmployees");
        getSession().addQuery("getAllEmployees", queryAll);
        allEmployees = (Vector)getSession().executeQuery("getAllEmployees");
        for (Enumeration enumtr = allEmployees.elements(); enumtr.hasMoreElements();) {
            Vector projects = ((Employee)enumtr.nextElement()).getProjects();
            employees.addAll(projects);
            //trigger all the value holders of projects
        }
    }

    public void test() {
        //all the employees with project names greater than Amagedon should be
        //in the cache right now.
        queryObjects = new ReadAllQuery();
        queryObjects.setReferenceClass(Employee.class);
        queryObjects.checkCacheOnly();//read from cache only

        ExpressionBuilder bldr = new ExpressionBuilder();
        Expression exp = bldr.anyOf("projects").get("name").greaterThan("Ammaggedon");
        queryObjects.setSelectionCriteria(exp);
        employees = (Vector)getSession().executeQuery(queryObjects);

    }

}
