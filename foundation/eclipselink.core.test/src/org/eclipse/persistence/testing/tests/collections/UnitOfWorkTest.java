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
package org.eclipse.persistence.testing.tests.collections;

import java.util.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.collections.Diner;
import org.eclipse.persistence.testing.models.collections.Location;
import org.eclipse.persistence.testing.models.collections.Menu;
import org.eclipse.persistence.testing.models.collections.MenuItem;
import org.eclipse.persistence.testing.models.collections.Restaurant;

/**
 * <p>
 * <b>Purpose</b>: This test checks to see if the Unit of Work feature functions correctly
 * within the context of Collections.
 */
public class UnitOfWorkTest extends WriteObjectTest {
    public Object unitOfWorkWorkingCopy;
    public UnitOfWork unitOfWork;

    public UnitOfWorkTest() {
        super();
    }

    public UnitOfWorkTest(Object originalObject) {
        super(originalObject);
    }

    protected void changeUnitOfWorkWorkingCopy() {
        Restaurant restaurant = (Restaurant)this.unitOfWorkWorkingCopy;

        Location location = (Location)restaurant.getLocations().iterator().next();
        restaurant.getLocations().remove(location);

        ((Menu)restaurant.getMenus().get("dinner")).getItems().add(MenuItem.example17((Menu)restaurant.getMenus().get("dinner")));

        Iterator iter = restaurant.getPreferredCustomers().values().iterator();
        Diner aDiner = (Diner)iter.next();
        aDiner.setFirstName(aDiner.getFirstName() + " B.");

        restaurant.getLocations().add(Location.example6());

        String aSlogan = (String)restaurant.getSlogans().iterator().next();
        restaurant.getSlogans().remove(aSlogan);
        restaurant.getSlogans().add("100% Java");

        restaurant.addMenu(new Menu("snack"));
        restaurant.addMenu(new Menu("four oclock tea"));
    }

    protected void setup() {
        super.setup();

        // Acquire first unit of work
        this.unitOfWork = getSession().acquireUnitOfWork();

        this.unitOfWorkWorkingCopy = this.unitOfWork.registerObject(this.objectToBeWritten);
        changeUnitOfWorkWorkingCopy();
        // Use the original session for comparision
        if (!compareObjects(this.originalObject, this.objectToBeWritten)) {
            throw new TestErrorException("The original object was changed through changing the clone.");
        }
    }

    protected void test() {
        this.unitOfWork.commit();
    }

    /**
     * Verify if the objects match completely through allowing the session to use the descriptors.
     * This will compare the objects and all of their privately owned parts.
     */
    protected void verify() {
        getSession().logMessage("working copy     Restaurant: " + this.unitOfWorkWorkingCopy);
        getSession().logMessage("objectToBeWriten Restaurant: " + this.objectToBeWritten);

        if (!(compareObjects(this.unitOfWorkWorkingCopy, this.objectToBeWritten))) {
            throw new TestErrorException("The object in the unit of work has not been commited properly to its parent");
        }

        super.verify();
    }
}
