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
 *     dminsky - initial API and implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.tests.expressions;

import java.util.*;

import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.queries.*;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.*;

/**
 * Test using ExpressionBuilder.literal() (Creating a LiteralExpression) using a ReportQuery.
 * This test is intended to be used to test ? symbols within the literal String.
 * 
 * EL Bug 284884 - Quoted '?' symbol in expression literal causes ArrayIndexOutOfBoundsException 
 * in DatasourceCall.translateQueryString()
 * @author dminsky
 */
public class LiteralSQLExpressionWithQuestionMarkTest extends TestCase {
    
    protected Exception caughtException;
    protected Vector<ReportQueryResult> results;
    protected boolean useBinding;
    protected String literalString;

    public LiteralSQLExpressionWithQuestionMarkTest(String literalString, boolean useBinding) {
        super();
        this.literalString = literalString;
        this.useBinding = useBinding;
        setDescription("LiteralExpression Query with emp.firstName not equal " + literalString + " binding: " + useBinding);
    }
    
    public void test() {
        ExpressionBuilder builder = new ExpressionBuilder(); 
        ReportQuery query = new ReportQuery(Employee.class, builder); 
        query.setShouldBindAllParameters(this.useBinding);
        query.setCacheUsage(query.DoNotCheckCache);
        
        query.addAttribute("id"); 
        query.addArgument("nameParameter", String.class);
        
        Expression expression = builder.get("firstName").notEqual(builder.literal(this.literalString));
        expression = expression.and(builder.get("lastName").equal(builder.getParameter("nameParameter"))); // "Smith"
        expression = expression.and(builder.get("gender").notEqual(builder.literal("'?'"))); // second literal
        
        query.setSelectionCriteria(expression);

        Vector<String> parameters = new Vector<String>();
        parameters.addElement("Smith"); // test 

        try {
            results = (Vector<ReportQueryResult>)getSession().executeQuery(query, parameters);
        } catch (Exception ex) {
            this.caughtException = ex;
        }
    }
    
    public void verify() {
        if (caughtException != null) {
            throw new TestErrorException("Exception occurred executing ReportQuery with literal expression: " + this.literalString, caughtException);
        }
        if (results == null || results.isEmpty()) {
            // expect > 0 query results with lastName = Smith
            throw new TestErrorException("Unexpected error - no ReportQuery results returned");
        }
    }
    
}
