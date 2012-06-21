/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Vikram Bhatia - initial API and implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.tests.queries.optimization;

import java.util.Collection;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.insurance.Address;

/**
 * Test query.addJoinedAttribute(Expression) when multiple expressions are added and 
 * each expression is created using a new ExpressionBuilder.
 */
public class ComplexJoinedAttributeQueryTest extends TestCase {
    private Address queryResult = null;
    public ComplexJoinedAttributeQueryTest() {
        setDescription("Complex Joined Attribute Query Test");
    }
    
    public void test() {
        ReadAllQuery query = new ReadAllQuery(Address.class);
        Expression zipCodeExpression = new ExpressionBuilder().get("zipCode").equal("28150");
        Expression ssnExpression = new ExpressionBuilder().get("policyHolder").get("ssn").equal(1111);
        Expression expression = zipCodeExpression.or(ssnExpression);
        
        Expression policyExpression = new ExpressionBuilder().get("policyHolder").anyOf("policies").get("policyNumber").equal(200);
        Expression phoneExpression = new ExpressionBuilder().get("policyHolder").anyOf("phones").get("areaCode").equal(123);
        
        expression = expression.and(policyExpression).and(phoneExpression);
        query.setSelectionCriteria(expression);
        
        query.addJoinedAttribute(new ExpressionBuilder().get("policyHolder"));
        query.addJoinedAttribute(new ExpressionBuilder().get("policyHolder").anyOf("policies"));
        query.addJoinedAttribute(new ExpressionBuilder().get("policyHolder").anyOf("phones"));
        
        Collection<Address> result = (Collection<Address>)getSession().executeQuery(query);
        if (result != null && result.size() == 1) {
            queryResult = (Address)result.toArray()[0];
        }
    }
    
    public void verify() {
        if (queryResult == null) { 
            throw new TestErrorException("Address not found.");
        }
    }
}

