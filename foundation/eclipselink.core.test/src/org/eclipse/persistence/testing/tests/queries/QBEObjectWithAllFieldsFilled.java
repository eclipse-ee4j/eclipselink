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
package org.eclipse.persistence.testing.tests.queries;

import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.expressions.*;

import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.testing.framework.*;

public class QBEObjectWithAllFieldsFilled extends TestCase {
    public Employee employee;
    public QueryByExamplePolicy policy;

    public QBEObjectWithAllFieldsFilled() {
        this.setDescription("To test the Query By Example for a simple Employee with no emply fields.");
    }

    public void setup() {
        //This tests the normal case, with no emply fields:
        employee = new Employee();
        policy = new QueryByExamplePolicy();
        ExpressionBuilder bldr = new ExpressionBuilder();
        Expression exp = bldr.get("firstName").equal("Jim-bob");
        ReadObjectQuery query = new ReadObjectQuery();
        query.setReferenceClass(Employee.class);
        query.setSelectionCriteria(exp);
        employee = (Employee)getSession().executeQuery(query);

    }

    public void test() {
        ReadObjectQuery query = new ReadObjectQuery();
        query.setExampleObject(employee);
        query.setQueryByExamplePolicy(policy);
        Employee emp = new Employee();
        emp = (Employee)getSession().executeQuery(query);
        if (emp != employee) {
            throw (new TestErrorException("Query By Example does not return the correct object."));
        }
    }
}
