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
package org.eclipse.persistence.testing.tests.queries;

import java.util.List;
import java.util.Vector;

import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.ScrollableCursor;

import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.testing.models.employee.domain.Employee;

/**
 * Test using a ScrollableCursor with a simple joined attribute.
 * Bug 351509 - Null Pointer Exception when using ScrollableCursor on a OneToMany Mapping 
 */
public class ScrollableCursorJoinedAttributeTest extends TestCase {
    
    protected List cursoredResults;
    protected Exception caughtException;
    
    public ScrollableCursorJoinedAttributeTest() {
        super();
        setDescription("Scrollable Cursor Test incorporating a joined attribute");
    }
    
    public void test() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        
        cursoredResults = new Vector();
        ReadAllQuery cursoredQuery = new ReadAllQuery(Employee.class);
        cursoredQuery.useScrollableCursor();
        cursoredQuery.addJoinedAttribute(cursoredQuery.getExpressionBuilder().anyOfAllowingNone("phoneNumbers"));
        cursoredQuery.addOrdering(cursoredQuery.getExpressionBuilder().get("id"));
        
        try {
            ScrollableCursor cursor = (ScrollableCursor)getSession().executeQuery(cursoredQuery);
            while (cursor.hasNext()) {
                Object result = cursor.next();
                cursoredResults.add(result);
            }
            cursor.close();
        } catch (Exception e) {
            caughtException = e;
        }
    }
    
    public void verify() {
        if (caughtException != null) {
            throwError("Cursored query should not result in an exception", caughtException);
        }
        assertNotSame("Test data for cursored results should be nonzero", 0, cursoredResults.size());
    }
    
    public void reset() {
        this.cursoredResults = null;
    }

}
