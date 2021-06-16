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
package org.eclipse.persistence.testing.tests.remote;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.insurance.*;

public class DescriptorRefreshCacheOnRemoteTest extends ReadObjectTest {
    protected PolicyHolder holder1, holder2;

    public DescriptorRefreshCacheOnRemoteTest() {
        setName("DescriptorRefreshCacheTestOnRemote");
        setDescription("This test case tests remote session refresh cache hit property on descriptor.");
    }

    public void reset() {
        getAbstractSession().rollbackTransaction();
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
    }

    protected void setup() {
        getAbstractSession().beginTransaction();
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        ClassDescriptor holderDescriptor = getSession().getDescriptor(PolicyHolder.class);

        holderDescriptor.alwaysRefreshCacheOnRemote();
        holderDescriptor.disableCacheHitsOnRemote();

    }

    public void test() {
        holder1 = (PolicyHolder)getSession().readObject(PolicyHolder.class);
        holder1.setAddress(null);
        holder1.setFirstName("Yahoo!");
        holder1.setPolicies(null);
        holder2 = (PolicyHolder)getSession().readObject(holder1);
    }

    public void verify() {
        if (holder2.getPolicies() == null || holder2.getAddress() == null || holder2.getFirstName() == "Yahoo!") {
            throw new TestErrorException("Refresh object on remote fails");
        }
    }
}
