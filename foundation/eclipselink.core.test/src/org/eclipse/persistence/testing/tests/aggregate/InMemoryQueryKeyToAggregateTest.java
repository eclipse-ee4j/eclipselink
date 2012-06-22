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

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.mappings.querykeys.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.aggregate.GolfClub;
import org.eclipse.persistence.testing.models.aggregate.GolfClubShaft;

/**
 * Bug 3995468
 * Ensure that an in-memory query works when a DirectQueryKey that maps to an attribute defined in
 * an attribute works.
 */
public class InMemoryQueryKeyToAggregateTest extends AutoVerifyTestCase {
    protected boolean shouldLoadObjectsIntoMemory = false;
    protected GolfClub club = null;
    protected GolfClub originalClub = null;

    public InMemoryQueryKeyToAggregateTest(boolean shouldLoadObjectsIntoMemory) {
        setName("InMemoryQueryKeyToAggregateTest - load into memory = " + shouldLoadObjectsIntoMemory);
        setDescription("Ensure that an in-memory query works when a DirectQueryKey that maps to an attribute defined in an attribute works.");
        this.shouldLoadObjectsIntoMemory = shouldLoadObjectsIntoMemory;
    }

    public void setup() {
        ClassDescriptor descriptor = getSession().getClassDescriptor(GolfClub.class);
        descriptor.addDirectQueryKey("stiffness", "GOLF_CLUB.SHAFT_STIFFNESS");
        DirectQueryKey key = (DirectQueryKey)descriptor.getQueryKeyNamed("stiffness");
        key.initialize(descriptor);
        beginTransaction();
        UnitOfWork uow = getSession().acquireUnitOfWork();
        originalClub = (GolfClub)uow.registerObject(new GolfClub());
        GolfClubShaft shaft = new GolfClubShaft();
        shaft.setStiffnessRating("R");
        originalClub.setShaft(shaft);
        uow.commit();

        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    public void test() {
        UnitOfWork uow = getSession().acquireUnitOfWork();
        if (shouldLoadObjectsIntoMemory) {
            uow.readAllObjects(GolfClub.class);
        }
        ReadObjectQuery query = new ReadObjectQuery(GolfClub.class);
        query.conformResultsInUnitOfWork();
        ExpressionBuilder clubs = new ExpressionBuilder();
        Expression exp = clubs.get("stiffness").equal("R");
        query.setSelectionCriteria(exp);
        club = (GolfClub)uow.executeQuery(query);
    }

    public void verify() {
        if ((club == null) || !(club.getId().equals(originalClub.getId()))) {
            throw new TestErrorException("Executing an in-memory query with a query key to an aggregate returns the wrong result.");
        }
    }

    public void reset() {
        rollbackTransaction();
        ClassDescriptor descriptor = getSession().getClassDescriptor(GolfClub.class);
        descriptor.getQueryKeys().remove("stiffness");
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }
}
