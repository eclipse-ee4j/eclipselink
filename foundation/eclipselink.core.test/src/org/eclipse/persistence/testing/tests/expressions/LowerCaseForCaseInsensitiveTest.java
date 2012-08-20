/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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

import java.util.Vector;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.testing.models.employee.domain.Employee;

/**
 * Test case-insensitive expressions querying with lower case by default.
 * Using Expression.setUpperCaseForIgnoreCase(false);
 * Bug 384223
 */
public class LowerCaseForCaseInsensitiveTest extends TestCase {

    protected String operation;
    public static final String LikeIgnoreCase = "LikeIgnoreCase";
    public static final String EqualsIgnoreCase = "EqualsIgnoreCase";
    public static final String ContainsSubstringIgnoringCase = "ContainsSubstringIgnoringCase";
    
    protected boolean previousUppercaseDefaultValue = Expression.shouldUseUpperCaseForIgnoreCase;
    protected Employee employee;
    protected String sharpS = "\u00df";
    protected String uUmlaut = "\u00fc";

    public LowerCaseForCaseInsensitiveTest(String operation) {
        super();
        this.operation = operation;
        setName(getName() + " : " + operation);
        setDescription("Test default lowercase for case-insensitive expressions");
    }
    
    public void setup() {
        // set the Expression default upper case value to lower case
        Expression.shouldUseUpperCaseForIgnoreCase = false;
        UnitOfWork uow = getSession().acquireUnitOfWork();
        employee = new Employee();
        Employee employeeClone = (Employee)uow.registerObject(employee);
        employeeClone.setFirstName("Heinz");
        employeeClone.setLastName("S\u00fc\u00dfig");
        employeeClone.setMale();
        uow.commit();
    }
    
    public void test() {
        if (operation == null) {
            fail("Test invoked with a null operation");
        } else if (operation.equals(LikeIgnoreCase)) {
            testLikeIgnoreCase(false);
            testLikeIgnoreCase(true);
        } else if (operation.equals(EqualsIgnoreCase)) {
            testEqualsIgnoreCase(false);
            testEqualsIgnoreCase(true);
        } else if (operation.equals(ContainsSubstringIgnoringCase)) {
            testContainsSubstringIgnoringCase(false);
            testContainsSubstringIgnoringCase(true);
        } else {
            fail("Test invoked with invalid operation: " + operation);
        }
    }
    
    public void testEqualsIgnoreCase(boolean checkCacheOnly) {
        String value = "s" + uUmlaut + sharpS + "IG";
        ReadAllQuery query = new ReadAllQuery(Employee.class);
        if (checkCacheOnly) {
            query.checkCacheOnly();
        }
        query.setSelectionCriteria(new ExpressionBuilder().get("lastName").equalsIgnoreCase(value));
        
        Vector<Employee> results = (Vector<Employee>) getSession().executeQuery(query);
        assertEquals("One result expected (checkCacheOnly=" + checkCacheOnly + ")", 1, results.size());
        assertTrue("Persisted employee expected in results (checkCacheOnly=" + checkCacheOnly + ")", results.contains(employee));
    }
    
    public void testLikeIgnoreCase(boolean checkCacheOnly) {
        String value = "S" + uUmlaut + sharpS + "ig";
        ReadAllQuery query = new ReadAllQuery(Employee.class);
        if (checkCacheOnly) {
            query.checkCacheOnly();
        }
        query.setSelectionCriteria(new ExpressionBuilder().get("lastName").likeIgnoreCase(value));
        
        Vector<Employee> results = (Vector<Employee>) getSession().executeQuery(query);
        assertEquals("One result expected (checkCacheOnly=" + checkCacheOnly + ")", 1, results.size());
        assertTrue("Persisted employee expected in results (checkCacheOnly=" + checkCacheOnly + ")", results.contains(employee));        
    }
    
    public void testContainsSubstringIgnoringCase(boolean checkCacheOnly) {
        String value = uUmlaut + sharpS;
        ReadAllQuery query = new ReadAllQuery(Employee.class);
        if (checkCacheOnly) {
            query.checkCacheOnly();
        }
        query.setSelectionCriteria(new ExpressionBuilder().get("lastName").containsSubstringIgnoringCase(value));
        
        Vector<Employee> results = (Vector<Employee>) getSession().executeQuery(query);
        assertEquals("One result expected (checkCacheOnly=" + checkCacheOnly + ")", 1, results.size());
        assertTrue("Persisted employee expected in results (checkCacheOnly=" + checkCacheOnly + ")", results.contains(employee));        
    }
    
    public void reset() {
        // set the Expression default upper case value to the default
        Expression.shouldUseUpperCaseForIgnoreCase = previousUppercaseDefaultValue;
        
        UnitOfWork uow = getSession().acquireUnitOfWork();
        uow.deleteObject(uow.registerObject(employee));
        uow.commit();
    }
    
}
