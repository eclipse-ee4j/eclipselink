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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.aggregate;

import java.util.Collection;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.sessions.UnitOfWork;

import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.testing.models.aggregate.GolfClub;
import org.eclipse.persistence.testing.models.aggregate.GolfClubShaft;

/**
 * Test querying on a manual QueryKey defined in an aggregate object
 * EL Bug 326977
 */
public class QueryKeyInAggregateTest extends TestCase {
    
    protected boolean conforming;
    protected Exception exception;
    protected Collection results;
    protected GolfClub originalClub;
    protected GolfClub originalClub2;
    
    public QueryKeyInAggregateTest(boolean shouldConform) {
        super();
        this.conforming = shouldConform;
        setName(getName() + " conforming: " + shouldConform);
        setDescription("Test querying on a manual QueryKey defined in an aggregate object");
    }
    
    public void setup() {
        UnitOfWork uow = getSession().acquireUnitOfWork();
        
        originalClub = (GolfClub)uow.registerObject(new GolfClub());
        GolfClubShaft shaft = new GolfClubShaft();
        shaft.setStiffnessRating("LX");
        originalClub.setShaft(shaft);
        
        originalClub2 = (GolfClub)uow.registerObject(new GolfClub());
        GolfClubShaft shaft2 = new GolfClubShaft();
        shaft2.setStiffnessRating("LX");
        originalClub2.setShaft(shaft2);
        uow.commit();

        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }
    
    public void test() {
        UnitOfWork uow = getSession().acquireUnitOfWork();
        
        if (conforming) {
            // create an object to be included in the conforming query
            GolfClub uncommittedClub = (GolfClub)uow.registerObject(new GolfClub());
            GolfClubShaft shaft = new GolfClubShaft();
            shaft.setStiffnessRating("LX");
            uncommittedClub.setShaft(shaft);
        }
        
        ReadAllQuery query = new ReadAllQuery(GolfClub.class);
        ExpressionBuilder builder = query.getExpressionBuilder();
        // using manual 'flexibility' query key
        Expression expression = builder.get("shaft").get("flexibility").equal("LX");
        query.setSelectionCriteria(expression);
        
        if (conforming) {
            query.conformResultsInUnitOfWork();
        }
        
        try {
            results = (Collection<GolfClub>) uow.executeQuery(query);
        } catch (Exception e) {
            this.exception = e;
        } finally {
            uow.release();
        }

    }
    
    public void verify() {
        if (exception != null) {
            throwError("An exception occurred whilst executing the query: " + exception);
        } else if (results == null || results.isEmpty()) {
            throwError("The query results were null or empty: " + results);
        } 
        
        int expectedSize;
        if (conforming) {
            expectedSize = 3;
        } else {
            expectedSize = 2;
        }
        if (results.size() != expectedSize) {
            throwError("Incorrect results size: " + results.size() + " " + results);
        }
    }
    
    public void reset() {
        UnitOfWork uow = getSession().acquireUnitOfWork();
        uow.deleteObject(originalClub);
        uow.deleteObject(originalClub2);
        uow.commit();
        this.originalClub = null;
        this.originalClub2 = null;
        this.exception = null;
        this.results = null;
    }

}
