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
 *     dminsky - initial API and implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.tests.expressions;

import java.util.*;

import org.eclipse.persistence.mappings.*;
import org.eclipse.persistence.descriptors.*;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.queries.*;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.*;

/**
 * Test using ExpressionBuilder.literal() (Creating a LiteralExpression) using a ReportQuery.
 * The DB field "GENDER" should be printed as a literal, data should be returned for the 
 * ReportQuery gender alias, and an exception should not occur when executing the ReportQuery.
 * Literal usage is: query.addAttribute("sysdate", builder.literal("SYSDATE"));
 * 
 * EL Bug 247076 - LiteralExpression does not print SQL in statement
 * @author dminsky
 */

public class LiteralExpressionTest extends TestCase {
    
    protected EclipseLinkException exception;
    protected Vector<ReportQueryResult> results;

    public LiteralExpressionTest() {
        super();
        setDescription("Test using a LiteralExpression through ExpressionBuilder.literal()");
    }
    
    public void test() {
        ClassDescriptor descriptor = getSession().getDescriptor(Employee.class);
        DatabaseMapping genderMapping = descriptor.getMappingForAttributeName("gender");
        String genderFieldName = genderMapping.getField().getName();

        ReportQuery query = new ReportQuery(Employee.class, new ExpressionBuilder());
        ExpressionBuilder builder = query.getExpressionBuilder();

        query.addAttribute("id", builder.get("id"));
        // Use a literal. Mapped in the DB as 'M' and 'F'
        query.addAttribute("gender", builder.literal(genderFieldName));
        query.addAttribute("firstName", builder.get("firstName"));
        query.addAttribute("lastName", builder.get("lastName"));

        try {
            results = (Vector<ReportQueryResult>)getSession().executeQuery(query);
        } catch (EclipseLinkException ex) {
            this.exception = ex;
        }
    }
    
    public void verify() {
        if (exception != null) {
            throw new TestErrorException("An exception occurred executing a ReportQuery with a literal expression", exception);
        }
        if (results == null || results.isEmpty()) {
            throw new TestErrorException("Unexpected error - no ReportQuery results returned");
        }
        for (int i = 0; i < results.size(); i++) {
            ReportQueryResult rqr = results.get(i);
            Object gender = rqr.get("gender");
            if (gender == null) {
                throw new TestErrorException("ReportQueryResult does not contain entries for 'gender', literal not added");
            }
        }        
    }
    
}
