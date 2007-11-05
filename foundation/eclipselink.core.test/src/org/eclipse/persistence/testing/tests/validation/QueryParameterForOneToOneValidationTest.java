/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.validation;

import java.util.*;

import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.exceptions.*;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.*;

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

    public QueryParameterForOneToOneValidationTest() {
        super();
        setDescription("Test that an exception is thrown for 1:1 when using a simple parameter instead of a complex object");
    }
    
    public void setup() {
        expectedException = QueryException.incorrectClassForObjectComparison(null, null, null);
    }
    
    public void test() {
        ReadAllQuery query = new ReadAllQuery(PhoneNumber.class);
        ExpressionBuilder builder = query.getExpressionBuilder();
        Expression expression = builder.get("owner").equal(builder.getParameter("the_parameter"));
        query.setSelectionCriteria(expression);
        query.addArgument("the_parameter", Integer.class);   

        try {
            // doesn't matter what id is queried
            Vector params = new Vector();
            params.add(new Integer(9)); 
            
            getSession().executeQuery(query, params);
        } catch (EclipseLinkException te) {
            caughtException = te;
        } catch (Exception e) {
            throw new TestException("Caught exception " + e.toString() + "running test " + getName());
        }
    }


}
