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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
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
