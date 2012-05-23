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

import java.math.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.mappings.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.testing.models.aggregate.GolfClub;
import org.eclipse.persistence.testing.models.aggregate.GolfClubShaft;
import org.eclipse.persistence.testing.models.aggregate.Manufacturer;

/**
 * Bug 3710297
 * Ensure when joining is set on a one-to-one mapping and a one-one-mapping to the same class with
 * the same name exists on an aggregate, the join is correctly handled and the correct object
 * is returned from a query.
 */
public class JoinInAggregateAndOwnerTest extends TestCase {
    protected int oldJoinFetch;
    protected BigDecimal clubId = null;
    protected boolean initialWriteFailed = false;
    protected boolean joinFailed = false;

    public JoinInAggregateAndOwnerTest() {
        setDescription("Test joining on an attribute that exists both on an aggregate and its parent.");
    }

    public void setup() {
        ClassDescriptor descriptor = getSession().getClassDescriptor(GolfClub.class);
        OneToOneMapping mapping = (OneToOneMapping)descriptor.getMappingForAttributeName("manufacturer");
        oldJoinFetch = mapping.getJoinFetch();
        mapping.useInnerJoinFetch();
        descriptor.reInitializeJoinedAttributes();
        beginTransaction();
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        UnitOfWork uow = getSession().acquireUnitOfWork();

        Manufacturer taylorMade = new Manufacturer();
        taylorMade.setName("Taylor Made");

        GolfClubShaft shaft = new GolfClubShaft();
        shaft.setManufacturer(taylorMade);
        shaft.setStiffnessRating("R90");

        GolfClub club = new GolfClub();
        club.setManufacturer(taylorMade);
        club.setShaft(shaft);
        uow.registerObject(club);

        uow.commit();

        clubId = club.getId();
    }

    public void test() {
        UnitOfWork uow = getSession().acquireUnitOfWork();
        ExpressionBuilder clubs = new ExpressionBuilder();
        Expression exp = clubs.get("id").equal(clubId);
        GolfClub club = (GolfClub)uow.readObject(GolfClub.class, exp);
        if (!(club.getManufacturer().getName() == club.getShaft().getManufacturer().getName())) {
            initialWriteFailed = true;
        }
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setName("True Temper");
        club.getShaft().setManufacturer(manufacturer);
        uow.commit();
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        club = (GolfClub)getSession().readObject(GolfClub.class, exp);
        if (club.getManufacturer().getName() == club.getShaft().getManufacturer().getName()) {
            joinFailed = true;
        }
    }

    public void verify() {
        if (initialWriteFailed) {
            throw new TestErrorException("Aggregate attribute should be equal to parent attribute and it is not.");
        }
        if (joinFailed) {
            throw new TestErrorException("Aggregate attribute should be not equal to parent attribute and it is.");
        }
    }

    public void reset() {
        rollbackTransaction();
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        ClassDescriptor descriptor = getSession().getClassDescriptor(GolfClub.class);
        OneToOneMapping mapping = (OneToOneMapping)descriptor.getMappingForAttributeName("manufacturer");
        mapping.setJoinFetch(oldJoinFetch);
        descriptor.reInitializeJoinedAttributes();
    }
}
