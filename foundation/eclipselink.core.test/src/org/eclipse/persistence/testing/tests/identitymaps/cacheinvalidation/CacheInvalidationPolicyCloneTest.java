/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     dminsky - initial API and implementation
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.identitymaps.cacheinvalidation;

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
            DailyCacheInvalidationPolicy policy = new DailyCacheInvalidationPolicy();
            DailyCacheInvalidationPolicy policyClone = (DailyCacheInvalidationPolicy)policy.clone();
            
            assertNotNull("Clone should not be null", policyClone);
            assertEquals("Clone should be of the same type", policy.getClass(), policyClone.getClass());
            assertEquals("Clone's expiry time should be the same", policy.getExpiryTime(), policyClone.getExpiryTime());
        } else if (policyClassToTest.equals (NoExpiryCacheInvalidationPolicy.class)) {
            NoExpiryCacheInvalidationPolicy policy = new NoExpiryCacheInvalidationPolicy();
            NoExpiryCacheInvalidationPolicy policyClone = (NoExpiryCacheInvalidationPolicy)policy.clone();
            
            assertNotNull("Clone should not be null", policyClone);
            assertEquals("Clone should be of the same type", policy.getClass(), policyClone.getClass());
        } else if (policyClassToTest.equals(TimeToLiveCacheInvalidationPolicy.class)) {
            TimeToLiveCacheInvalidationPolicy policy = new TimeToLiveCacheInvalidationPolicy();
            TimeToLiveCacheInvalidationPolicy policyClone = (TimeToLiveCacheInvalidationPolicy)policy.clone();
            
            assertNotNull("Clone should not be null", policyClone);
            assertEquals("Clone should be of the same type", policy.getClass(), policyClone.getClass());
            assertEquals("Clone's TTL should be the same", policy.getTimeToLive(), policyClone.getTimeToLive());
        } else {
            throwError("Invalid policy class: " + this.policyClassToTest);
        }
    }
    
}
