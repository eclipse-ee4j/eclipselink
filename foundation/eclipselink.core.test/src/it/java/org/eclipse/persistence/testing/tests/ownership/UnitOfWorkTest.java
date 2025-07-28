/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.ownership;

import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.framework.WriteObjectTest;
import org.eclipse.persistence.testing.models.ownership.ObjectA;
import org.eclipse.persistence.testing.models.ownership.ObjectB;
import org.eclipse.persistence.testing.models.ownership.ObjectC;
import org.eclipse.persistence.testing.models.ownership.ObjectD;
import org.eclipse.persistence.testing.models.ownership.ObjectE;

import java.util.Enumeration;
import java.util.Vector;

/**
 * <p>
 * <b>Purpose</b>: This test checks to see if the Unit of Work functions with the private-owned mappings
 * <p>
 * <b>Motivation </b>: This test was written to test a new feature: the UOW.
 *
 */
public class UnitOfWorkTest extends WriteObjectTest {
    public Object unitOfWorkWorkingCopy;
    public UnitOfWork unitOfWork;
    public UnitOfWork nestedUnitOfWork;

    public UnitOfWorkTest() {
        super();
    }

    public UnitOfWorkTest(Object originalObject) {
        super(originalObject);
    }

    public void changeObjectC(ObjectC objectC) {
        ObjectD objectD = (ObjectD)objectC.getOneToOne().getValue();
        Vector objectECollection = (Vector)objectD.getOneToMany().getValue();
        objectECollection.remove(objectECollection.get(0));

        objectECollection.add(new ObjectE(objectD));
    }

    protected void changeUnitOfWorkWorkingCopy() {
        ObjectA objectA = (ObjectA)this.unitOfWorkWorkingCopy;
        ObjectB objectB = (ObjectB)objectA.getOneToOne().getValue();
        Vector objectCCollection = (Vector)objectB.getOneToMany().getValue();

        //change object C D and E
        for (Enumeration enumtr = objectCCollection.elements(); enumtr.hasMoreElements();) {
            ObjectC objectC = (ObjectC)enumtr.nextElement();
            this.changeObjectC(objectC);
        }

        objectCCollection.add(ObjectC.example4(objectB));
        objectCCollection.add(ObjectC.example5(objectB));
    }

    @Override
    protected void setup() {
        super.setup();

        // Acquire first unit of work
        this.unitOfWork = getSession().acquireUnitOfWork();

        this.unitOfWorkWorkingCopy = this.unitOfWork.registerObject(this.objectToBeWritten);
    }

    @Override
    protected void test() {
        changeUnitOfWorkWorkingCopy();
        // Use the original session for comparision
        if (!compareObjects(this.originalObject, this.objectToBeWritten)) {
            throw new TestErrorException("The original object was changed through changing the clone.");
        }
        this.unitOfWork.commit();
    }

    /**
     * Verify if the objects match completely through allowing the session to use the descriptors.
     * This will compare the objects and all of their privately owned parts.
     */
    @Override
    protected void verify() {
        if (!(compareObjects(this.unitOfWorkWorkingCopy, this.objectToBeWritten))) {
            throw new TestErrorException("The object in the unit of work has not been commited properly to its parent");
        }

        super.verify();
    }
}
