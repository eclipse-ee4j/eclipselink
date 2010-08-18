/*******************************************************************************
 * Copyright (c) 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 *     Mark Wolochuk - Bug 321041 ConcurrentModificationException on getFromIdentityMap() fix
 ******************************************************************************/ 
package org.eclipse.persistence.testing.tests.identitymaps;

import java.util.ConcurrentModificationException;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.employee.domain.Employee;

/**
 * Test for bug fix 321041 - EclipseLink throws ConcurrentModificationException when triggering lazy load from conforming query
 * @author tware
 *
 */
public class TriggerValueHoldersSelfReferencingOneToOneTest extends AutoVerifyTestCase{

    protected UnitOfWork uow = null;
    protected ConcurrentModificationException exception = null;
    
    public void setup(){
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        uow = getSession().acquireUnitOfWork();
        
        
        // preload the UnitOfWork identity map with 2 Employees which have different managers
        ExpressionBuilder emp = new ExpressionBuilder();
        Expression queryExp = emp.get("firstName").equal("Charles").and(emp.get("lastName").equal("Chanley"));
        uow.readObject(Employee.class, queryExp);
        
        emp = new ExpressionBuilder();
        queryExp = emp.get("firstName").equal("Marcus").and(emp.get("lastName").equal("Saunders"));
        uow.readObject(Employee.class, queryExp);
    }
    
    public void test(){
        
        // We query for both Employees here because it is impossible to tell which order
        // keys will be returned from the identity map in
        // This bug only occurs when the first key returned is non-conforming and
        // a future key must be looked up
        ExpressionBuilder emp = new ExpressionBuilder();
        Expression queryExp = emp.get("manager").get("firstName").equal("Bob");
        ReadObjectQuery query = new ReadObjectQuery(Employee.class, queryExp);
        query.conformResultsInUnitOfWork();
        query.getInMemoryQueryIndirectionPolicy().triggerIndirection();
        uow.executeQuery(query);
        
        emp = new ExpressionBuilder();
        queryExp = emp.get("manager").get("firstName").equal("John");
        query = new ReadObjectQuery(Employee.class, queryExp);
        query.conformResultsInUnitOfWork();
        query.getInMemoryQueryIndirectionPolicy().triggerIndirection();
        uow.executeQuery(query);
    }
    
    public void verify(){
        if (exception != null){
            throw new TestErrorException("A ConcurrentModificationException was thrown when using a Self Referencing Relationship and " +
                     "a TriggerIndirection query policy.");
        }
    }
    
    public void reset(){
        uow = null;
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }
    
}
