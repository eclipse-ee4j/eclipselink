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
 package org.eclipse.persistence.testing.tests.distributedservers;

import org.eclipse.persistence.testing.models.aggregate.Employee1;
import org.eclipse.persistence.testing.models.aggregate.HomeAddress;
import org.eclipse.persistence.testing.models.aggregate.WorkingAddress;

/**
 * Test changing private parts of an object.
 *
 */
public class UpdateChangeObjectTestEmployee1 extends ComplexUpdateTest {

    public UpdateChangeObjectTestEmployee1() {
        super();
    }

    public UpdateChangeObjectTestEmployee1(Employee1 originalObject) {
        super(originalObject);
    }

    protected void changeObject() {
        Employee1 employee = (Employee1)this.workingCopy;
        // Transformation
        if (employee.getAddress() instanceof HomeAddress) {
            employee.setAddress(WorkingAddress.example1());
        }else{
            employee.setAddress(HomeAddress.example1());
        }
    }
}
