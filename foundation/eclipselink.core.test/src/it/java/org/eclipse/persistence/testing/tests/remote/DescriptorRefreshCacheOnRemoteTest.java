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
package org.eclipse.persistence.testing.tests.remote;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.testing.framework.ReadObjectTest;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.insurance.PolicyHolder;

public class DescriptorRefreshCacheOnRemoteTest extends ReadObjectTest {
    protected PolicyHolder holder1, holder2;

    public DescriptorRefreshCacheOnRemoteTest() {
        setName("DescriptorRefreshCacheTestOnRemote");
        setDescription("This test case tests remote session refresh cache hit property on descriptor.");
    }

    @Override
    public void reset() {
        getAbstractSession().rollbackTransaction();
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
    }

    @Override
    protected void setup() {
        getAbstractSession().beginTransaction();
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        ClassDescriptor holderDescriptor = getSession().getDescriptor(PolicyHolder.class);

        holderDescriptor.alwaysRefreshCacheOnRemote();
        holderDescriptor.disableCacheHitsOnRemote();

    }

    @Override
    public void test() {
        holder1 = (PolicyHolder)getSession().readObject(PolicyHolder.class);
        holder1.setAddress(null);
        holder1.setFirstName("Yahoo!");
        holder1.setPolicies(null);
        holder2 = (PolicyHolder)getSession().readObject(holder1);
    }

    @Override
    public void verify() {
        if (holder2.getPolicies() == null || holder2.getAddress() == null || holder2.getFirstName() == "Yahoo!") {
            throw new TestErrorException("Refresh object on remote fails");
        }
    }
}
