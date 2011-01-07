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
package org.eclipse.persistence.testing.tests.optimization.queryandsqlcounting.querycache;

import java.util.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.descriptors.invalidation.*;

/**
 * Test the invalidation of query cache results
 * This test is configurable to use three types of invalidation and whether those types
 * of invalidation should cause the results to become invalid
 */
public class QueryCacheInvalidationTest extends NamedQueryQueryCacheTest {
    protected int invalidationPolicyType = 0;
    protected boolean expiringTest = false;
    protected int sleepTime = 0;
    public static final int NO_INVALIDATION = 0;
    public static final int TIME_TO_LIVE_INVALIDATION = 1;
    public static final int DAILY_INVALIDATION = 2;

    public QueryCacheInvalidationTest(int invalidationPolicyType, boolean expiringTest) {
        setName("QueryCacheInvalidationTest invalidationType: " + invalidationPolicyType + " expiring: " + expiringTest);
        setDescription("Test the various cache invalidation polcies on the query cache.");
        this.invalidationPolicyType = invalidationPolicyType;
        this.expiringTest = expiringTest;
    }

    /**
     * This method will build a cache invalidation policy for the query being run depending
     * on the invalidation and expiringTest settings of the query.
     */
    protected CacheInvalidationPolicy getInvalidationPolicy() {
        CacheInvalidationPolicy invalidationPolicy = null;
        if (invalidationPolicyType == NO_INVALIDATION) {
            invalidationPolicy = new NoExpiryCacheInvalidationPolicy();
        } else if (invalidationPolicyType == TIME_TO_LIVE_INVALIDATION) {
            if (expiringTest) {
                invalidationPolicy = new TimeToLiveCacheInvalidationPolicy(0);
                expectedSQLCount = 1;
                sleepTime = 100;
            } else {
                invalidationPolicy = new TimeToLiveCacheInvalidationPolicy(1000000);
            }
        } else {
            if (expiringTest) {
                expectedSQLCount = 1;
                GregorianCalendar calendar = new GregorianCalendar();
                calendar.setTime(new Date(System.currentTimeMillis() + 1000));
                invalidationPolicy = new DailyCacheInvalidationPolicy(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND), calendar.get(Calendar.MILLISECOND));
                sleepTime = 1500;
            } else {
                GregorianCalendar calendar = new GregorianCalendar();
                calendar.setTime(new Date(System.currentTimeMillis() + 1000000));
                invalidationPolicy = new DailyCacheInvalidationPolicy(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND), calendar.get(Calendar.MILLISECOND));

            }
        }
        return invalidationPolicy;
    }

    public ReadQuery getQueryForTest() {
        ReadQuery testQuery = super.getQueryForTest();
        testQuery.setQueryResultsCachePolicy(new QueryResultsCachePolicy(getInvalidationPolicy()));
        return testQuery;
    }

    public void test() {
        try {
            Thread.sleep(sleepTime);// sleep will allow cache to expire
        } catch (Exception exception) {
        }
        super.test();
    }
}
