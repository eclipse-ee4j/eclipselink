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
package org.eclipse.persistence.testing.tests.queries;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.testing.models.employee.domain.Employee;

import java.util.Enumeration;
import java.util.Vector;

public class ConformResultsAcrossOneToManyTest extends org.eclipse.persistence.testing.framework.AutoVerifyTestCase {
    protected Employee employee;
    protected org.eclipse.persistence.sessions.UnitOfWork unitOfWork;
    protected ReadAllQuery queryAllObjects;
    protected ReadAllQuery queryAll;
    protected ReadObjectQuery queryObject;
    protected java.util.Vector employees;
    protected java.util.Vector allPhones;
    protected java.util.Vector allEmployees;
    protected java.util.Vector employeesInCache;

    public ConformResultsAcrossOneToManyTest() {
        super();

    }

    @Override
    public void reset() {
        //clear the cache
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    @Override
    public void setup() {
        allEmployees = new Vector();
        allPhones = new Vector();

        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        unitOfWork = getSession().acquireUnitOfWork();
        queryAll = new ReadAllQuery();
        queryAll.setReferenceClass(Employee.class);
        queryAll.setSelectionCriteria(new ExpressionBuilder().anyOf("phoneNumbers").get("areaCode").equal("613"));
        queryAll.conformResultsInUnitOfWork();
        allEmployees = (Vector)getSession().executeQuery(queryAll);
        for (Enumeration enumtr = allEmployees.elements(); enumtr.hasMoreElements(); ) {
            Vector phones = ((Employee)enumtr.nextElement()).getPhoneNumbers();
            allPhones.addAll(phones);
            //trigger all the value holders of phoneNumbers
        }
    }

    @Override
    public void test() {

        //all the employees with work type phonenumbers should be
        //in the cache right now.

        queryAllObjects = new ReadAllQuery();
        queryAllObjects.setReferenceClass(Employee.class);
        ExpressionBuilder bldr = new ExpressionBuilder();
        Expression exp = bldr.anyOf("phoneNumbers").get("areaCode").equal("613");
        queryAllObjects.setSelectionCriteria(exp);
        employeesInCache = (Vector)getSession().executeQuery(queryAllObjects);
        unitOfWork.release();

    }

    @Override
    public void verify() {
        if (employeesInCache.size() == 13) {
            //?
        }
    }
}
