/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
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
