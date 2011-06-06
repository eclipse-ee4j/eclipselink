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

import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.aggregate.RoomSellingPoint;
import org.eclipse.persistence.testing.models.aggregate.House;
import org.eclipse.persistence.testing.models.aggregate.SellingPoint;

/**
 * Test to make sure that the appropriate update is made when an aggregate using inheritance is
 * changed from one subclass to another.
 * @author Tom Ware
 */
public class NestedAggregateCollectionTest extends org.eclipse.persistence.testing.framework.AutoVerifyTestCase {
    private Class cls;
    private String errorMessage = null;
    private UnitOfWork uow = null;
    // that's either Agent or Builder
    private Object object = null;
    private House house = null;

    // Must be Agent or Builder.
    public NestedAggregateCollectionTest(Class cls) {
        super();
        this.cls = cls;
        setName(getName() + AgentBuilderHelper.getNameInBrackets(cls));
    }
    
    public void reset() {
        rollbackTransaction();
    }

    public void setup() {
        beginTransaction();
    }

    public void test() {
        DatabaseSession session = (DatabaseSession)getSession();

        // CR#2896 - TW
        SellingPoint sellingPoint = RoomSellingPoint.example3();
        SellingPoint databaseSellingPoint = null;
        int index = -1;

        // read an agent from the database, insert a selling point.
        readAHouse();
        house.getSellingPoints().add(sellingPoint);
        uow.commit();

        //Now check that the item was correctly inserted.
        readAHouse();
        index = house.getSellingPoints().indexOf(sellingPoint);

        if (index == -1) {
            errorMessage = "Selling point was not added to aggregate collection.";
            return;
        }
        sellingPoint = (SellingPoint)house.getSellingPoints().elementAt(index);
        //(sellingPoint);
        //sellingPoint = databaseSellingPoint;
        sellingPoint.setDescription("Small, dark, and dingy area.");
        //house.getSellingPoints().add(sellingPoint);
        uow.commit();

        //Now check that the item was updated correctly.
        readAHouse();
        index = house.getSellingPoints().indexOf(sellingPoint);
        if (index == -1) {
            errorMessage = "Selling point was not updated in the aggregate collection.";
            return;
        }

        // Now remove the item and commit.
        house.getSellingPoints().remove(index);
        uow.commit();

        // Now check that the item was deleted correctly.
        readAHouse();
        index = house.getSellingPoints().indexOf(sellingPoint);
        if (index != -1) {
            errorMessage = "Selling point was not deleted from the aggregate collection.";
            return;
        }
    }

    public void verify() {
        if (errorMessage != null) {
            throw new TestErrorException(errorMessage);
        }
    }

    private void readAHouse() {
        DatabaseSession session = (DatabaseSession)getSession();
        session.getIdentityMapAccessor().initializeIdentityMaps();
        uow = session.acquireUnitOfWork();
        object = uow.readObject(cls);
        house = (House)AgentBuilderHelper.getHouses(object).get(0);
    }
}
