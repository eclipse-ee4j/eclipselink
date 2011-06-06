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
package org.eclipse.persistence.testing.tests.identitymaps.cache;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.internal.identitymaps.*;

/**
 * <P>
 * <B>Purpose</B>: To test the LRU algorithm in the Cache Identity Map.<P>
 *
 * <B>Motivation</B>: The Cache Identity Map is an integral part of TopLink, and therefore the LRU
 * algorithms must work properly to ensure that the proper objects are in the cache, and in their
 * proper spots within the cache.<P>
 *
 * <B>Design</B>: This test suite tests overflowing the cache to see if size constraints are
 * maintained, as well as testing insertion, and deletion and updating at the front, end, and
 * middle of the cache.<P>
 *
 * <B>Responsibilities</B>: .<P>
 *
 * <B>Features Used</B>:
 * <UL>
 *     <LI>CacheIdentityMap
 * </UL>
 *
 * <B>Paths Covered</B>:
 * <UL>
 *        <LI>Inserts elements exceeding the specified maximum size for the cache.
 *        <LI>Inserts an element and verifies that the state of the cache is maintained.
 *        <LI>Deletes the first element and verifies that the state of the cache is maintained.
 *        <LI>Deletes the last element and verifies that the state of the cache is maintained.
 *        <LI>Deletes a middle element and verifies that the state of the cache is maintained.
 *        <LI>Updates the first element and verifies that the state of the cache is maintained.
 *        <LI>Updates the last element and verifies that the state of the cache is maintained.
 *        <LI>Updates a middle element and verifies that the state of the cache is maintained.
 * </UL>
 *
 * @author Rick Barkhouse
 */
public class CacheIdentityMapTestSuite extends TestSuite {
    public CacheIdentityMapTestSuite() {
        setDescription("This suite thoroughly tests the functionality of the cache identity maps.");
        CacheIdentityMap cache = new CacheIdentityMap(10);
        addTest(getInsertOverflowTestSuite(cache));
        addTest(getInsertTestSuite(cache));
        addTest(getSmallCacheTestSuite(new CacheIdentityMap(2)));
    }

    public void addTests() {
        CacheIdentityMap cache = new CacheIdentityMap(10);

    }

    public TestSuite getInsertOverflowTestSuite(CacheIdentityMap cache) {
        TestSuite suite = new TestSuite();
        suite.setName("CacheIdentityMapInsertOverflowTestSuite");
        suite.setDescription("This suite tests the the insertion of a number of elements into the cache which exceed the maximum size of the cache.");

        suite.addTest(new InsertOverflowTest(cache));

        return suite;
    }

    public TestSuite getInsertTestSuite(CacheIdentityMap cache) {
        TestSuite suite = new TestSuite();
        suite.setName("CacheIdentityMapInsertTestSuite");
        suite.setDescription("This suite tests the the insertion of an element into the cache.");

        suite.addTest(new InsertTest(cache));

        return suite;
    }

    public TestSuite getSmallCacheTestSuite(CacheIdentityMap cache) {
        TestSuite suite = new TestSuite();
        suite.setName("SmallCacheIdentityMapTestSuite");
        suite.setDescription("This suite tests the functionality of a small cache.");

        suite.addTest(new CustomDeleteTest(cache));

        return suite;
    }
}
