/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     dminsky - initial API and implementation
package org.eclipse.persistence.testing.tests.aggregate;

import org.eclipse.persistence.exceptions.EclipseLinkException;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.sessions.UnitOfWork;

import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.testing.models.aggregate.Child;
import org.eclipse.persistence.testing.models.aggregate.Parent;

/**
 * Test aggregate relationships with a OneToManyMapping
 * EL bug 332080
 */
public class AggregateRelationshipsOneToManyTestCase extends TestCase {

    protected Parent originalParent;
    protected Parent readParent;

    public AggregateRelationshipsOneToManyTestCase() {
        super();
        setDescription("AggregateRelationships: test OneToManyMapping");
    }

    public void setup() {
        UnitOfWork uow = getSession().acquireUnitOfWork();

        originalParent = new Parent();
        Child child1 = new Child();
        Child child2 = new Child();
        Child child3 = new Child();
        originalParent.addChild(child1);
        originalParent.addChild(child2);
        originalParent.addChild(child3);

        uow.registerObject(originalParent);
        uow.commit();
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
    }

    public void test() {
        int id = originalParent.getId();
        try {
            readParent = (Parent) getSession().readObject(
                Parent.class,
                new ExpressionBuilder().get("id").equal(id));
        } catch (EclipseLinkException exception) {
            throwError("An exception occurred whilst reading back a Parent object with id " + id, exception);
        }
    }

    public void verify() {
        assertNotNull("Parent read back should not be null", readParent);
        compareObjects(originalParent, readParent);
        assertEquals(originalParent.getAggregate().getChildren().size(), readParent.getAggregate().getChildren().size());
    }

    public void reset() {
        UnitOfWork uow = getSession().acquireUnitOfWork();
        uow.deleteAllObjects(originalParent.getAggregate().getChildren());
        uow.deleteObject(originalParent);
        uow.commit();
    }

}
