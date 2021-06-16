/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.aggregate;

import org.eclipse.persistence.testing.tests.writing.*;
import org.eclipse.persistence.testing.models.aggregate.*;

/**
 * Test adding null to the aggregate collections of an object.
 * References: CR#2378-X.C, CR#2258-S.M.
 */
public class AddNullToAggregateCollectionTest extends ComplexUpdateTest {

    public AddNullToAggregateCollectionTest() {
        super();
        // Compare will fail because null is ignored.
        this.shouldCompareClone = false;
    }

    public AddNullToAggregateCollectionTest(Agent originalObject) {
        super(originalObject);
        // Compare will fail because null is ignored.
        this.shouldCompareClone = false;
        // The original problem was with compareForChange() called by
        // mergeChangesIntoParent().  The following allows the latter to
        // be executed prior to a write to the database, which would
        // just throw a null field exception,
        //preventing the recreation of the problem.
        commitParentUnitOfWork();
    }

    public AddNullToAggregateCollectionTest(Builder originalObject) {
        super(originalObject);
        // Compare will fail because null is ignored.
        this.shouldCompareClone = false;
        // The original problem was with compareForChange() called by
        // mergeChangesIntoParent().  The following allows the latter to
        // be executed prior to a write to the database, which would
        // just throw a null field exception,
        //preventing the recreation of the problem.
        commitParentUnitOfWork();
    }

    protected void changeObject() {
        Object object = this.workingCopy;
        AgentBuilderHelper.getHouses(object).add(null);
        //Test nesting.
        House house = (House)AgentBuilderHelper.getHouses(object).get(0);
        house.getSellingPoints().add(null);
        //CR#2896
        SellingPoint sellingPoint = new RoomSellingPoint();
        sellingPoint.setArea("bathroom");
        sellingPoint.setDescription("clean and functional.");
        house.getSellingPoints().add(sellingPoint);
    }
}
