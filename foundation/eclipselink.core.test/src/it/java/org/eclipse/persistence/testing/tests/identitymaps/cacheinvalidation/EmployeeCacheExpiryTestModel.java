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

import org.eclipse.persistence.descriptors.invalidation.*;
import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.testing.tests.employee.EmployeeBasicTestModel;

/**
 * Test the Employee model with TimeToLiveCacheExpiry or DailyCacheExpiry and objects that will not expire.
 */
public class EmployeeCacheExpiryTestModel extends EmployeeBasicTestModel {

    protected CacheInvalidationPolicy employeeCacheExpiryPolicy;
    protected CacheInvalidationPolicy addressCacheExpiryPolicy;
    protected CacheInvalidationPolicy projectCacheExpiryPolicy;
    protected CacheInvalidationPolicy phoneNumberCacheExpiryPolicy;
    protected CacheInvalidationPolicy largeProjectCacheExpiryPolicy;
    protected CacheInvalidationPolicy smallProjectCacheExpiryPolicy;

    public EmployeeCacheExpiryTestModel() {
        setDescription("An employee model which tests CRUD operations with a TimeToLiveExpiry policy where nothing expires.");
    }

    public void setup() {
        super.setup();
        employeeCacheExpiryPolicy = getSession().getDescriptor(Employee.class).getCacheInvalidationPolicy();
        addressCacheExpiryPolicy = getSession().getDescriptor(Address.class).getCacheInvalidationPolicy();
        projectCacheExpiryPolicy = getSession().getDescriptor(Project.class).getCacheInvalidationPolicy();
        phoneNumberCacheExpiryPolicy = getSession().getDescriptor(PhoneNumber.class).getCacheInvalidationPolicy();
        largeProjectCacheExpiryPolicy = getSession().getDescriptor(LargeProject.class).getCacheInvalidationPolicy();
        smallProjectCacheExpiryPolicy = getSession().getDescriptor(SmallProject.class).getCacheInvalidationPolicy();
    }

    public void reset() {
        super.reset();
        getSession().getDescriptor(Employee.class).setCacheInvalidationPolicy(employeeCacheExpiryPolicy);
        getSession().getDescriptor(Address.class).setCacheInvalidationPolicy(addressCacheExpiryPolicy);
        getSession().getDescriptor(Project.class).setCacheInvalidationPolicy(projectCacheExpiryPolicy);
        getSession().getDescriptor(PhoneNumber.class).setCacheInvalidationPolicy(phoneNumberCacheExpiryPolicy);
        getSession().getDescriptor(LargeProject.class).setCacheInvalidationPolicy(largeProjectCacheExpiryPolicy);
        getSession().getDescriptor(SmallProject.class).setCacheInvalidationPolicy(smallProjectCacheExpiryPolicy);
    }
}
