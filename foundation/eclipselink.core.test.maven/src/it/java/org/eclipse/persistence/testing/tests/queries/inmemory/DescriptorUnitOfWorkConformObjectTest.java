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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.queries.inmemory;

import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.testing.models.employee.domain.*;

/**
 * Tests queries using ShouldAlwaysConformResultsInUnitOfWork on the Employee descriptor to ensure
 *  conforming works using ReadObjectQuery.  This test uses the uow from UnitOfWorkConformObjectTest.
 *  Created as a generic test for bug#2679958
 */
public class DescriptorUnitOfWorkConformObjectTest extends UnitOfWorkConformObjectTest {
    protected boolean shouldAlwaysConformResultsInUnitOfWork;
    protected ClassDescriptor employeeDescriptor;

    public DescriptorUnitOfWorkConformObjectTest(ReadObjectQuery query, boolean size) {
        super(query, size);
        setDescription("Tests that the query is done on the unit of work changes when " + "ShouldAlwaysConformResultsInUnitOfWork is set on the descriptor.");
    }

    public void reset() {
        //reset the ShouldAlwaysConformResultsInUnitOfWork setting on the descriptor
        employeeDescriptor.setShouldAlwaysConformResultsInUnitOfWork(shouldAlwaysConformResultsInUnitOfWork);
        super.reset();
    }

    protected void setup() {
        employeeDescriptor = getSession().getClassDescriptor(Employee.class);
        shouldAlwaysConformResultsInUnitOfWork = employeeDescriptor.shouldAlwaysConformResultsInUnitOfWork();

        employeeDescriptor.setShouldAlwaysConformResultsInUnitOfWork(true);

        super.setup();
    }
}
