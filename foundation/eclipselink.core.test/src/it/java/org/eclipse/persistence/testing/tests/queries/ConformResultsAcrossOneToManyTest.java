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
package org.eclipse.persistence.testing.tests.queries;

import java.util.Vector;
import java.util.Enumeration;

import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.expressions.*;

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

    public void reset() {
        //clear the cache
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

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

    public void verify() {
        if (employeesInCache.size() == 13) {
            //?
        }
    }
}
