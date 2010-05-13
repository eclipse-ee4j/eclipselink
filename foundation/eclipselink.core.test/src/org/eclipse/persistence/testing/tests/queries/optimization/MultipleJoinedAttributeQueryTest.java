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
 *     Vikram Bhatia - initial API and implementation
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.queries.optimization;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.sessions.UnitOfWork;

import org.eclipse.persistence.testing.models.vehicle.CarOwner;
import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;

/**
 * Test select list with multiple joined attributes specified in an order
 */
public class MultipleJoinedAttributeQueryTest extends TestCase {
    private CarOwner owner = null;
    
    public MultipleJoinedAttributeQueryTest() {
        setDescription("Test select list with multiple joined attributes specified in an order.");
    }

    public void test() {
        UnitOfWork uow = getSession().acquireUnitOfWork();
        
        ReadObjectQuery query = new ReadObjectQuery(CarOwner.class);
        Expression rootExpression = query.getExpressionBuilder();
        
        // Add joined attribute car.engineType.
        Expression expression = rootExpression.getAllowingNull("car");
        query.addJoinedAttribute(expression);
        expression = expression.getAllowingNull("engineType");
        query.addJoinedAttribute(expression);
               
        // Add joined attribute lastCar.fuelType.
        expression = rootExpression.getAllowingNull("lastCar");
        query.addJoinedAttribute(expression);
        expression = expression.get("fuelType");
        query.addJoinedAttribute(expression);

        // Add joined attribute car.fuelType.
        expression = rootExpression.getAllowingNull("car");
        query.addJoinedAttribute(expression);
        expression = expression.get("fuelType");
        query.addJoinedAttribute(expression);
        
        owner = (CarOwner)uow.executeQuery(query);
    }
    
    public void verify() {
        if (owner == null || owner.getCar() == null || owner.getCar().getFuelType() == null) { 
            throw new TestErrorException("CarOwner or Car or FuelType is not found.");
        }
        
        if (!owner.getCar().getFuelType().getFuelDescription().equals("Petrol")) {
            throw new TestErrorException("Car Fuel Type is expected to be Petrol, but found " + owner.getCar().getFuelType().getFuelDescription());
        }
            
    }
}
