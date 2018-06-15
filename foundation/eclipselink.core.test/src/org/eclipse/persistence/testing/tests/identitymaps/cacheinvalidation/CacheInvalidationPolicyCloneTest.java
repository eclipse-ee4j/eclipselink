/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     dminsky - initial API and implementation
package org.eclipse.persistence.testing.tests.identitymaps.cacheinvalidation;

import java.util.Calendar;

import org.eclipse.persistence.descriptors.invalidation.CacheInvalidationPolicy;
import org.eclipse.persistence.descriptors.invalidation.DailyCacheInvalidationPolicy;
import org.eclipse.persistence.descriptors.invalidation.NoExpiryCacheInvalidationPolicy;
import org.eclipse.persistence.descriptors.invalidation.TimeToLiveCacheInvalidationPolicy;
import org.eclipse.persistence.internal.helper.Helper;

import org.eclipse.persistence.testing.framework.TestCase;

/**
 * Test the clone() method of each CacheInvalidationPolicy type
 * EL bug 336486
 */
public class CacheInvalidationPolicyCloneTest extends TestCase {

    protected Class policyClassToTest;

    public CacheInvalidationPolicyCloneTest(Class policyClassToTest) {
        super();
        this.policyClassToTest = policyClassToTest;
        setName(getClass().getSimpleName() + " : " + policyClassToTest.getSimpleName());
        setDescription("Test CacheInvalidationPolicy cloning for: " + policyClassToTest.getSimpleName());
    }

    public void test() {
        if (policyClassToTest == null || !Helper.classIsSubclass(policyClassToTest, CacheInvalidationPolicy.class)) {
            throwError("CacheInvalidationPolicy class to test cannot be null");
        }

        if (policyClassToTest.equals(DailyCacheInvalidationPolicy.class)) {
            // expire at 01:02:03.004
            DailyCacheInvalidationPolicy policy = new DailyCacheInvalidationPolicy(1, 2, 3, 4);
            DailyCacheInvalidationPolicy policyClone = (DailyCacheInvalidationPolicy)policy.clone();

            assertNotNull("Clone should not be null", policyClone);
            assertFalse("Clone should not be the same instance", policy == policyClone);
            assertEquals("Clone should be of the same type", policy.getClass(), policyClone.getClass());
            assertEquals("Clone's expiry time should be the same", policy.getExpiryTime().getTimeInMillis(), policyClone.getExpiryTime().getTimeInMillis());
            assertEquals("Clone's expiry time should be 01:02:03.004 - hour", 1, policyClone.getExpiryTime().get(Calendar.HOUR));
            assertEquals("Clone's expiry time should be 01:02:03.004 - minute", 2, policyClone.getExpiryTime().get(Calendar.MINUTE));
            assertEquals("Clone's expiry time should be 01:02:03.004 - second", 3, policyClone.getExpiryTime().get(Calendar.SECOND));
            assertEquals("Clone's expiry time should be 01:02:03.004 - millisecond", 4, policyClone.getExpiryTime().get(Calendar.MILLISECOND));
        } else if (policyClassToTest.equals(NoExpiryCacheInvalidationPolicy.class)) {
            NoExpiryCacheInvalidationPolicy policy = new NoExpiryCacheInvalidationPolicy();
            NoExpiryCacheInvalidationPolicy policyClone = (NoExpiryCacheInvalidationPolicy)policy.clone();

            assertNotNull("Clone should not be null", policyClone);
            assertFalse("Clone should not be the same instance", policy == policyClone);
            assertEquals("Clone should be of the same type", policy.getClass(), policyClone.getClass());
        } else if (policyClassToTest.equals(TimeToLiveCacheInvalidationPolicy.class)) {
            TimeToLiveCacheInvalidationPolicy policy = new TimeToLiveCacheInvalidationPolicy();
            TimeToLiveCacheInvalidationPolicy policyClone = (TimeToLiveCacheInvalidationPolicy)policy.clone();

            assertNotNull("Clone should not be null", policyClone);
            assertFalse("Clone should not be the same instance", policy == policyClone);
            assertEquals("Clone should be of the same type", policy.getClass(), policyClone.getClass());
            assertEquals("Clone's TTL should be the same", policy.getTimeToLive(), policyClone.getTimeToLive());
        } else {
            throwError("Invalid policy class: " + this.policyClassToTest);
        }
    }

}
