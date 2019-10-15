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
package org.eclipse.persistence.testing.tests.identitymaps.cacheinvalidation;

import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.descriptors.invalidation.*;

public class EmployeeTimeToLiveTestModel extends EmployeeCacheExpiryTestModel {
    public EmployeeTimeToLiveTestModel() {
        setDescription("Test CRUD operations on the Employee model with non expiring Time To Live Cache Expiry.");
        setName("Employee Time To Live Expiry Test Model");
    }

    public void setup() {
        super.setup();
        getSession().getDescriptor(Employee.class).setCacheInvalidationPolicy(new TimeToLiveCacheInvalidationPolicy(10000000));
        getSession().getDescriptor(Address.class).setCacheInvalidationPolicy(new TimeToLiveCacheInvalidationPolicy(10000000));
        getSession().getDescriptor(Project.class).setCacheInvalidationPolicy(new TimeToLiveCacheInvalidationPolicy(10000000));
        getSession().getDescriptor(PhoneNumber.class).setCacheInvalidationPolicy(new TimeToLiveCacheInvalidationPolicy(10000000));
        getSession().getDescriptor(LargeProject.class).setCacheInvalidationPolicy(new TimeToLiveCacheInvalidationPolicy(10000000));
        getSession().getDescriptor(SmallProject.class).setCacheInvalidationPolicy(new TimeToLiveCacheInvalidationPolicy(10000000));
    }
}
