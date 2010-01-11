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
package org.eclipse.persistence.testing.tests.queries.oracle;

import java.util.*;

import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.mapping.*;

/**
 * This class is the root class for most Hierarchical Query tests. The subclasses
 * override the getQuery and expectedResults methods. The test method executes the 
 * query and the verify checks the results against the expected results.
 */
public abstract class HierarchicalQueryTest extends TestCase {
    static String ERROR_1 = "The number of results returned was not the number of results expected.";
    static String ERROR_2 = "The results did not match, missing:";

    public Vector results;

    public HierarchicalQueryTest() {
    }
    
    //Returns a vector of employee names, representing the expected results of the query
    public abstract Vector expectedResults();
    
    //Returns the query to execute
    public abstract ReadAllQuery getQuery();

    public void setup() {
        if (!(getSession().getPlatform().isOracle())) {
            throw new TestWarningException("This test is intended for Oracle databases only");
        }
    }

    public void reset() {
    }

    public void test() {
        try {
            results = (Vector)getSession().executeQuery(getQuery());
        } catch (Exception ex) {
            ex.printStackTrace();
            if ((ex.getMessage().indexOf("missing BY keyword") != -1) || (ex.getMessage().indexOf("cannot have join with CONNECT BY") != -1)) {
                throw new TestWarningException("This test is indended for Oracle 9i and up");
            }
            ex.printStackTrace();
            throw new TestErrorException(ex.getMessage());
        }
    }

    public void verify() {
        Vector expectedResults = expectedResults();
        if (expectedResults.size() != results.size()) {
            throw new TestErrorException(ERROR_1);
        }
        Iterator res = results.iterator();
        while (res.hasNext()) {
            Object next = res.next();
            if (!expectedResults.contains(next)) {
                throw new TestErrorException(ERROR_2 + next);
            }
        }
    }
    
    //Adds an employee and all his managed employees recursivly to the provided vector.
    //This creates a hierarchy rooted at Employee emp
    public void addEmployee(Vector toVector, Employee emp) {
        toVector.addElement(emp);
        Vector managed = emp.getManagedEmployees();
        if (managed != null && !(managed.isEmpty())) {
            for (int i = 0; i < managed.size(); i++) {
                Employee next = (Employee)managed.elementAt(i);
                addEmployee(toVector, next);
            }
        }
    }
}
