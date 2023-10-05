/*
 * Copyright (c) 2023 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */
package org.eclipse.persistence.testing.tests.unitofwork;

import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.testing.models.employee.domain.Employee;

import java.util.IdentityHashMap;

/**
 * This test is in response to issue 1950: Duplicate objects in UnitOfWorkImpl.primaryKeyToNewObjects
 *
 * When a new object is registered for persist by calling registerNewObjectForPersist
 * the object is potentially added twice to primaryKeyToNewObjects. Once during assignSequenceNumber and
 * then in registerNewObjectClone. This test verifies that the object is contained only once in primaryKeyToNewObjects.
 */
public class UOWPrimaryKeyToNewObjectsDuplicateObjectsTest extends TestCase {
    public UOWPrimaryKeyToNewObjectsDuplicateObjectsTest() {
        setDescription("This test verifies that no duplicates exist in primaryKeyToNewObjects after registering a new object");
    }

    @Override
    public void reset() {
        getAbstractSession().rollbackTransaction();
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    @Override
    public void setup() {
        getAbstractSession().beginTransaction();
    }

    @Override
    public void test() {
        Session session = getSession();
        UnitOfWorkImpl uow = (UnitOfWorkImpl) session.acquireUnitOfWork();
        Employee emp = new Employee();
        emp.setFirstName("UOWPrimaryKeyToNewObjectsDuplicateObjectsTest");
        uow.registerNewObjectForPersist(emp, new IdentityHashMap<>());
        // there should be only one object in primaryKeyToNewObjects
        assertEquals("Unexpected amount of entities in primaryKeyToNewObjects", 1,
                uow.getPrimaryKeyToNewObjects().get(emp.getId()).size());
    }
}