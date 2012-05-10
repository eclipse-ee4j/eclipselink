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
package org.eclipse.persistence.testing.tests.queries;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.ScrollableCursor;

import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.testing.models.employee.domain.Employee;
import org.eclipse.persistence.testing.models.employee.domain.PhoneNumber;

/**
 * Test using a ScrollableCursor with a simple joined attribute.
 * Bug 361860 - Using ScrollableCursor with 1:M joining produces incorrect results 
 */
public class ScrollableCursorJoiningVerificationTest extends TestCase {
    
    protected List<Employee> forwardCursoredResults;
    protected List<Employee> reverseCursoredResults;
    protected List<Employee> nonCursoredResults;
    
    public ScrollableCursorJoiningVerificationTest() {
        super();
        setDescription("ScrollableCursor test with joining, verifying query results");
    }
    
    public void test() {
        // non-cursored results
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        
        ReadAllQuery nonCursoredQuery = new ReadAllQuery(Employee.class);
        nonCursoredQuery.dontCheckCache();
        nonCursoredQuery.addJoinedAttribute(nonCursoredQuery.getExpressionBuilder().anyOfAllowingNone("phoneNumbers"));
        nonCursoredQuery.addOrdering(nonCursoredQuery.getExpressionBuilder().get("id"));
        
        nonCursoredResults = (List)getSession().executeQuery(nonCursoredQuery);
        
        // forward cursored results
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        
        forwardCursoredResults = new ArrayList<Employee>();
        ReadAllQuery cursoredQuery = new ReadAllQuery(Employee.class);
        nonCursoredQuery.dontCheckCache();
        cursoredQuery.useScrollableCursor();
        cursoredQuery.addJoinedAttribute(cursoredQuery.getExpressionBuilder().anyOfAllowingNone("phoneNumbers"));
        cursoredQuery.addOrdering(cursoredQuery.getExpressionBuilder().get("id"));
        
        ScrollableCursor cursor = (ScrollableCursor)getSession().executeQuery(cursoredQuery);
        while (cursor.hasNext()) {
            Employee result = (Employee)cursor.next();
            forwardCursoredResults.add(result);
        }
        
        // reverse cursored results - use the same cursor
        reverseCursoredResults = new ArrayList();
        while (cursor.hasPrevious()) {
            Employee result = (Employee)cursor.previous();
            reverseCursoredResults.add(result);
        }
        
        cursor.close();
    }
    
    public void verify() {
        assertNotSame("Test data for non-cursored results should be nonzero", 0, nonCursoredResults.size());
        assertNotSame("Test data for cursored results should be nonzero", 0, forwardCursoredResults.size());
        assertNotSame("Test data for reverse-cursored results should be nonzero", 0, reverseCursoredResults.size());
        
        assertSame("Cursored results should be the same size as non-cursored results", nonCursoredResults.size(), forwardCursoredResults.size());
        assertSame("Reverse cursored results should be the same size as non-cursored results", nonCursoredResults.size(), reverseCursoredResults.size());
        
        if (compareEmployeeLists(nonCursoredResults, forwardCursoredResults) == false) {
            failNotEquals("Cursored and non-cursored results should be equal", nonCursoredResults, forwardCursoredResults);
        }
        
        // reverse the "reversed" cursored list to compare with the non cursored results 
        List reversedReverseCursoredResults = new ArrayList(reverseCursoredResults);
        Collections.reverse(reversedReverseCursoredResults);
        
        if (compareEmployeeLists(nonCursoredResults, reversedReverseCursoredResults) == false) {
            assertEquals("Reverse cursored and non-cursored results should be equal", nonCursoredResults, reversedReverseCursoredResults);
        }
    }
    
    public boolean compareEmployeeLists(List<Employee> expectedList, List<Employee> actualList) {
        for (int i = 0; i < expectedList.size(); i++) {
            Employee expectedEmployee = expectedList.get(i);
            Employee actualEmployee = actualList.get(i);
            if (!expectedEmployee.getId().equals(actualEmployee.getId()) || 
                    !expectedEmployee.getFirstName().equals(actualEmployee.getFirstName()) ||
                    !expectedEmployee.getLastName().equals(actualEmployee.getLastName())) {
                return false;
            }
            // cannot be sure that phone numbers are ordered within the list
            for (PhoneNumber expectedPhoneNumber : (Vector<PhoneNumber>) expectedEmployee.getPhoneNumbers()) {
                boolean phoneInList = false;
                for (int j = 0; j < actualEmployee.getPhoneNumbers().size(); j++) {
                    PhoneNumber actualPhoneNumber = (PhoneNumber) actualEmployee.getPhoneNumbers().get(j);
                    if (expectedPhoneNumber.getAreaCode().equals(actualPhoneNumber.getAreaCode()) &&
                            expectedPhoneNumber.getNumber().equals(actualPhoneNumber.getNumber()) &&
                            expectedPhoneNumber.getType().equals(actualPhoneNumber.getType())) {
                        phoneInList = true;
                        break;
                    }
                }
                if (phoneInList == false) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public void reset() {
        this.forwardCursoredResults = null;
        this.nonCursoredResults = null;
    }

}
