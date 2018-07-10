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

import org.eclipse.persistence.descriptors.invalidation.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.*;

/**
 * Superclass for CacheExpiry tests.
 * Provides a framework which allows cache expiry values to be reset to their original
 * values in the reset method for Employee and Address.  Also provides a convience method
 * to set cache values.
 */
public class CacheExpiryTest extends AutoVerifyTestCase {

    protected CacheInvalidationPolicy employeeCacheExpiryPolicy;
    protected CacheInvalidationPolicy addressCacheExpiryPolicy;

    public void setup() {
        employeeCacheExpiryPolicy = getSession().getDescriptor(Employee.class).getCacheInvalidationPolicy();
        addressCacheExpiryPolicy = getSession().getDescriptor(Address.class).getCacheInvalidationPolicy();

        getSession().getIdentityMapAccessor().initializeIdentityMaps();

        beginTransaction();
    }

    public void reset() {
        rollbackTransaction();
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        getSession().getDescriptor(Employee.class).setCacheInvalidationPolicy(employeeCacheExpiryPolicy);
        getSession().getDescriptor(Address.class).setCacheInvalidationPolicy(addressCacheExpiryPolicy);
    }

}
