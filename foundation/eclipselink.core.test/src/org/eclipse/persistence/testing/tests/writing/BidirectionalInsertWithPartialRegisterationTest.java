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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.writing;

import java.util.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.sessions.*;

/**
 * Test insert related objects.
 */
public class BidirectionalInsertWithPartialRegisterationTest extends AutoVerifyTestCase {
    protected boolean usesUnitOfWork;
    protected UnitOfWork unitOfWork;
    protected Vector originalObjects;

    public BidirectionalInsertWithPartialRegisterationTest() {
        super();
    }

    public BidirectionalInsertWithPartialRegisterationTest(boolean usesUnitOfWork) {
        String testName = "BidirectionalInsertWithPartialRegisterationTest(";

        this.usesUnitOfWork = usesUnitOfWork;

        if (usesUnitOfWork) {
            testName = testName + "with UOW";
        }
        testName = testName + ")";
        setName(testName);
        setDescription("This creates a new set of interrelated employees and projects and commits them.");
    }

    public void reset() {
        rollbackTransaction();
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    protected void setup() {
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        beginTransaction();
        originalObjects = new Vector();

        Employee originalBob = new org.eclipse.persistence.testing.models.employee.domain.Employee();
        originalBob.setFirstName("Bob");
        unitOfWork = getSession().acquireUnitOfWork();
        Employee bob = (Employee)unitOfWork.registerObject(originalBob);

        Employee fred = new org.eclipse.persistence.testing.models.employee.domain.Employee();
        fred.setFirstName("Fred");
        LargeProject prs = new org.eclipse.persistence.testing.models.employee.domain.LargeProject();
        prs.setName("PRS");
        prs.setDescription("TOPLink/Smalltalk");
        SmallProject flq = new org.eclipse.persistence.testing.models.employee.domain.SmallProject();
        flq.setName("FLQ");
        flq.setDescription("TOPLink/Java");

        fred.addManagedEmployee(bob);
        fred.addProject(flq);
        fred.addProject(prs);
        bob.addManagedEmployee(fred);
        bob.addProject(flq);
        bob.addProject(prs);
        flq.setTeamLeader(bob);
        prs.setTeamLeader(fred);

        originalObjects.addElement(bob);

    }

    protected void test() {
        unitOfWork.commit();

    }

    protected void verify() {
        getSession().getIdentityMapAccessor().initializeIdentityMaps();

        Vector databaseObjects = new Vector();

        for (Enumeration originalObjectsEnum = this.originalObjects.elements();
                 originalObjectsEnum.hasMoreElements();) {
            databaseObjects.addElement(getSession().readObject(originalObjectsEnum.nextElement()));
        }

        Enumeration originalObjectsEnum = this.originalObjects.elements();
        Enumeration databaseObjectsEnum = databaseObjects.elements();

        while (originalObjectsEnum.hasMoreElements()) {
            if (!compareObjects(originalObjectsEnum.nextElement(), databaseObjectsEnum.nextElement())) {
                throw new TestErrorException("Objects do not match after insert.");
            }
        }
    }
}
