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
//     tware - test for https://bugs.eclipse.org/bugs/show_bug.cgi?id=260986
package org.eclipse.persistence.testing.tests.queries;

import java.util.Iterator;
import java.util.List;

import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.employee.domain.Employee;

// test for bug 260986
@SuppressWarnings("deprecation")
public class PartialAttributeDistinctOrderByTest extends AutoVerifyTestCase{

    protected Exception exception = null;
    protected List results = null;

    public void test(){
        try{
            ReadAllQuery query = new ReadAllQuery(Employee.class);
            query.dontMaintainCache();
            query.useDistinct();
            query.addPartialAttribute("firstName");
            query.addPartialAttribute("address");
            query.addOrdering(query.getExpressionBuilder().get("firstName"));
            results = (List)getSession().executeQuery(query);
        } catch (Exception exception){
            this.exception = exception;
        }
    }

    public void verify(){
        if (exception != null){
            throw new TestErrorException("Partial Attribute query with joining failed with exception. " + exception.toString());
        }
        if (results.size() != 12){
            throw new TestErrorException("Incorrect number of Employees returned.");
        }
        Iterator i = results.iterator();
        String previousValue = "AAAAA";
        while (i.hasNext()){
            Employee e = (Employee)i.next();
            if (e.getFirstName().compareTo(previousValue) < 0){
                throw new TestErrorException("Order is incorrect between: " + e.getFirstName() + " and: " + previousValue + ".");
            }
            previousValue = e.getFirstName();
        }
    }

}
