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
 *     dminsky - added changes to support conforming and dynamically finding an id
 *      
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.validation;

import java.util.*;

import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.exceptions.*;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.PhoneNumber;
import org.eclipse.persistence.testing.models.employee.domain.Employee;
import org.eclipse.persistence.testing.models.employee.domain.EmployeePopulator;

/**
 * Bug 6119707 - A DATABASE QUERY AND IN-MEMORY QUERY PRODUCES A DIFFERENT RESULT
 * Test that querying across a 1:1 mapping using a 'simple type' instead of a complex
 * object in parameters produces an exception. 
 * Needed to support correct query building habits with developers.
 * e.g.
 * Incorrect: (where the_parameter = numeric type)
 *    builder.get("owner").equal(builder.getParameter("the_parameter"));
 * GOOD: (where the_parameter = numeric type)
 *    builder.get("owner").get("id").equal(builder.getParameter("the_parameter"));
 */
public class QueryParameterForOneToOneValidationTest extends ExceptionTest {

    protected boolean shouldConform;

    public QueryParameterForOneToOneValidationTest(boolean shouldConform) {
        super();
        this.shouldConform = shouldConform;
        setName(getName() + " - conforming: " + shouldConform);
        setDescription("Test that an exception is thrown for 1:1 when using a simple parameter instead of a complex object");
    }
    
    public void setup() {
        expectedException = QueryException.incorrectClassForObjectComparison(null, null, null);
    }
    
    public void test() {
        // find a suitable id
    	org.eclipse.persistence.testing.models.employee.domain.Employee empExample = 
            (org.eclipse.persistence.testing.models.employee.domain.Employee)new EmployeePopulator().basicEmployeeExample10();
        ExpressionBuilder testBuilder = new ExpressionBuilder();
        Expression testExpression = testBuilder.get("firstName").equal(empExample.getFirstName());
        testExpression = testExpression.and(testBuilder.get("lastName").equal(empExample.getLastName()));
        Employee testEmp = (Employee)getSession().readObject(Employee.class, testExpression);
        
        if (testEmp == null) {
            throw new TestErrorException("Employee example was not retrieved from DB (null): " + empExample);
        }
        
        int id = testEmp.getId().intValue();
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();

        ReadAllQuery query = new ReadAllQuery(PhoneNumber.class);
        ExpressionBuilder builder = query.getExpressionBuilder();
        Expression expression = builder.get("owner").equal(builder.getParameter("the_parameter"));
        query.setSelectionCriteria(expression);
        query.addArgument("the_parameter", Integer.class);   

        try {
            // doesn't matter what id is queried
            Vector params = new Vector();
            params.add(new Integer(id));
            
            // special case for conforming
            if (this.shouldConform) {
                // enable conforming
                query.setCacheUsage(query.CheckCacheOnly);
                // preload objects into identitymap
                getSession().readAllObjects(PhoneNumber.class);
                getSession().readAllObjects(Employee.class);
            }
            
            getSession().executeQuery(query, params);
        } catch (EclipseLinkException te) {
            caughtException = te;
        } catch (Exception e) {
            throw new TestException("Caught exception " + e.toString() + "running test " + getName());
        }
    }


}
