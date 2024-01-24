/*
 * Copyright (c) 1998, 2024 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.identitymaps;

import java.util.*;
import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.testing.framework.*;

import org.eclipse.persistence.internal.identitymaps.*;
import org.eclipse.persistence.sessions.*;

/**
 * Bug 5840635
 * Ensure that adding a new CacheKey with a null object reference to
 * SoftCacheWeakIdentityMap during a phase of putting objects into the
 * IdentityMap does not result in the existing IdentityMap entries getting removed.
 * @author David Minsky
 */
public class CleanupCacheKeyCorrectnessTest extends TestCase {

    protected Class<? extends IdentityMap> originalIdentityMapClass;
    protected Class<? extends IdentityMap> newIdentityMapClass;
    protected int originalIdentityMapSize;
    protected int newIdentityMapSize;
    protected int numberOfObjectsToCreate;
    protected int objectsNotFoundInIdentityMap;

    public CleanupCacheKeyCorrectnessTest() {
        super();
        setDescription("This test verifies that CacheKeys with null object references aren't removed when calling IdentityMap.put()");
    }

    @Override
    public void setup() {
        originalIdentityMapClass = getSession().getDescriptor(Employee.class).getIdentityMapClass();
        originalIdentityMapSize = getSession().getDescriptor(Employee.class).getIdentityMapSize();

        newIdentityMapClass = SoftCacheWeakIdentityMap.class;
        newIdentityMapSize = 5;

        objectsNotFoundInIdentityMap = 0;
        numberOfObjectsToCreate = newIdentityMapSize * 5;

        getSession().getDescriptor(Employee.class).setIdentityMapClass(newIdentityMapClass);
        getSession().getDescriptor(Employee.class).setIdentityMapSize(newIdentityMapSize);

        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        beginTransaction();
    }

    /**
     * 1. Create and register multiple Employees with a UoW
     * 2. Commit UoW
     * 3. Check that the employees are contained in the parent session's
     *    IdentityMap; If they are not contained, increment the failure count
     */

    @Override
    public void test() {
        List<Employee> employees = new ArrayList<>(numberOfObjectsToCreate);

        UnitOfWork uow = getSession().acquireUnitOfWork();
        for (int i = 0; i < numberOfObjectsToCreate; i++) {
            int identifier = i + 1;
            Employee employee = new Employee();
            employees.add(employee); // add the original to the list for testing later
            employee.setFirstName("Bob");
            employee.setLastName("Jones#" + identifier);
            uow.registerObject(employee);
        }
        uow.commit();

        for (Employee employee : employees) {
            // if the IdentityMap does not contain the employee object, increment failure count
            if (!getSession().getIdentityMapAccessor().containsObjectInIdentityMap(employee)) {
                objectsNotFoundInIdentityMap++;
            }
        }
    }

    @Override
    public void verify() {
        if (objectsNotFoundInIdentityMap > 0) {
            String buffer = "The IdentityMap - size(" +
                    newIdentityMapSize +
                    ") removed " +
                    objectsNotFoundInIdentityMap +
                    " new objects out of " +
                    numberOfObjectsToCreate +
                    " objects added. No objects should have been removed.";
            throw new TestErrorException(buffer);
        }
    }

    @Override
    public void reset() {
        rollbackTransaction();
        getSession().getDescriptor(Employee.class).setIdentityMapClass(originalIdentityMapClass);
        getSession().getDescriptor(Employee.class).setIdentityMapSize(originalIdentityMapSize);
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
    }

}
