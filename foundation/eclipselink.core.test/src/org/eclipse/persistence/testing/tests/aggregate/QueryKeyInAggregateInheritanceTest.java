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
 ******************************************************************************/
package org.eclipse.persistence.testing.tests.aggregate;

import java.util.Collection;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.sessions.UnitOfWork;

import org.eclipse.persistence.testing.framework.TestCase;

import org.eclipse.persistence.testing.models.aggregate.Car;
import org.eclipse.persistence.testing.models.aggregate.Transport;

/**
 * Test performing an in memory query on a query key inherited by an
 * aggregate object in an inheritance hierarchy.
 * EL Bug 326977
 */
public class QueryKeyInAggregateInheritanceTest extends TestCase {

    protected Collection<Transport> results;
    
    public QueryKeyInAggregateInheritanceTest() {
        super();
        setDescription("Conforming query on a QueryKey defined on an aggregate object with an inheritance hierarchy");
    }
    
    public void test() {
        UnitOfWork uow = getSession().acquireUnitOfWork();

        Car example = new Car();
        example.setMake("Audi");
        example.setModel("TT");
        example.setColour("Azure");
        example.setCapacity(2);
        
        Transport transport = new Transport();
        transport.setVehicle(example);
        
        uow.registerObject(transport);
        
        ReadAllQuery query = new ReadAllQuery(Transport.class);
        ExpressionBuilder builder = query.getExpressionBuilder();
        Expression expression = builder.get("vehicle").get("colour").equal("Azure");
        query.setSelectionCriteria(expression);
        query.conformResultsInUnitOfWork();
        
        results = (Collection)uow.executeQuery(query);
    }
    
    public void verify() {
        if (results == null || results.isEmpty()) {
            throwError("No results were returned");
        } else if (results.size() != 1) {
            throwError("Incorrect results size: " + results);
        }
    }
    
    public void reset() {
        this.results = null;
    }
    
}
