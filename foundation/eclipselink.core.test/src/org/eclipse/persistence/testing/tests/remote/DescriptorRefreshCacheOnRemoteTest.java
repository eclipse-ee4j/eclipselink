/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
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
