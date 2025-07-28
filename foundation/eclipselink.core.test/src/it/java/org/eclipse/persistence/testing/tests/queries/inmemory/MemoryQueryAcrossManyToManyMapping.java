/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.queries.inmemory;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.testing.models.employee.domain.Employee;

import java.util.Enumeration;
import java.util.Vector;

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

    @Override
    public void reset() {
        //clear the cache.
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    /**
     * This is  still in the works.
     */
    @Override
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

    @Override
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
