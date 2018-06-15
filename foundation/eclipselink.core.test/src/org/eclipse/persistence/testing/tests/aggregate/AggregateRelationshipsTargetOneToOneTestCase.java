/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     dminsky - initial API and implementation
package org.eclipse.persistence.testing.tests.aggregate;

import org.eclipse.persistence.exceptions.EclipseLinkException;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.sessions.UnitOfWork;

import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.testing.models.aggregate.Parent;
import org.eclipse.persistence.testing.models.aggregate.StepChild;

/**
 * Test aggregate relationships with a target OneToOneMapping
 * EL bug 332080
 */
public class AggregateRelationshipsTargetOneToOneTestCase extends TestCase {

    protected Parent originalParent;
    protected Parent readParent;

    public AggregateRelationshipsTargetOneToOneTestCase() {
        super();
        setDescription("AggregateRelationships: test target OneToOneMapping");
    }

    public void setup() {
        UnitOfWork uow = getSession().acquireUnitOfWork();

        originalParent = new Parent();
        StepChild stepChild = new StepChild();
        originalParent.getAggregate().setStepChild(stepChild);
        stepChild.setParent(originalParent);

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
        compareObjects(originalParent.getAggregate().getStepChild(), readParent.getAggregate().getStepChild());
    }

    public void reset() {
        UnitOfWork uow = getSession().acquireUnitOfWork();
        uow.deleteObject(originalParent.getAggregate().getStepChild());
        uow.deleteObject(originalParent);
        uow.commit();
    }

}
