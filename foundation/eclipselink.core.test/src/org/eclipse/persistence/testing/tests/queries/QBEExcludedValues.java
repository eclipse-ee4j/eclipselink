/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.*;

public class QBEExcludedValues extends TestCase {

    public Employee employee;
    public QueryByExamplePolicy policy;

    public QBEExcludedValues() {
        this.setDescription("To test the Query By Example for values than have been declared to be excluded.");
    }

    public void setup() {
        //This method tests the excluded values list
        employee = new Employee();
        policy = new QueryByExamplePolicy();
        policy.excludeValue(new Integer(-1));
        employee.setFirstName("John");
        employee.setLastName("");
        employee.setSalary(-1);
        //since the emply string and the integer -1 are to be excluded from the query
        //the query should only care about the first name value.
    }

    public void test() {
        ReadObjectQuery query = new ReadObjectQuery();
        query.setReferenceClass(Employee.class);
        query.setExampleObject(employee);
        query.setQueryByExamplePolicy(policy);
        Employee emp = new Employee();
        emp = (Employee)getSession().executeQuery(query);
        if (emp == null) {
            throw (new TestErrorException("Error!"));
        }
    }
}
