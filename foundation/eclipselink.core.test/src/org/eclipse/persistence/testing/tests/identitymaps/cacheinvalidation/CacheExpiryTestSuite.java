/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.tests.identitymaps.cacheinvalidation;

import java.util.*;

import org.eclipse.persistence.descriptors.invalidation.*;
import org.eclipse.persistence.tools.schemaframework.PopulationManager;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.*;

public class CacheExpiryTestSuite extends TestSuite {

    public CacheExpiryTestSuite() {
        setDescription("Test the cache expiry feature.");
    }

    public void addTests() {
        addTest(new CacheExpiryAPITest());

        TimeToLiveCacheInvalidationPolicy ttlPolicy = new TimeToLiveCacheInvalidationPolicy(0);
        AutoVerifyTestCase test = new CacheExpiryPolicyTest(ttlPolicy, true);
        test.setDescription("Ensure Objects expire with TIME_TO_LIVE_EXPIRY.");
        test.setName("TIME_TO_LIVE_EXPIRY(0s) - Expire");
        addTest(test);

        ttlPolicy = new TimeToLiveCacheInvalidationPolicy(100000);
        test = new CacheExpiryPolicyTest(ttlPolicy, false);
        test.setDescription("Ensure Objects survive with TIME_TO_LIVE_EXPIRY.");
        test.setName("TIME_TO_LIVE_EXPIRY(100s) - Live");
        addTest(test);

        ttlPolicy = new TimeToLiveCacheInvalidationPolicy(-100000);
        test = new CacheExpiryPolicyTest(ttlPolicy, true);
        test.setDescription("Ensure Objects expire with TIME_TO_LIVE_EXPIRY.");
        test.setName("TIME_TO_LIVE_EXPIRY(-100s) - Expire");
        addTest(test);

        Calendar calendar = new GregorianCalendar();
        calendar.add(GregorianCalendar.MILLISECOND, -1);
        DailyCacheInvalidationPolicy dPolicy = 
            new DailyCacheInvalidationPolicy(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), 
                                             calendar.get(Calendar.SECOND), calendar.get(Calendar.MILLISECOND));
        test = new CacheExpiryPolicyTest(dPolicy, true);
        test.setDescription("Ensure Objects expire with DAILY_EXPIRY.");
        test.setName("DAILY - Expire");
        addTest(test);

        calendar = new GregorianCalendar();
        dPolicy = 
                new DailyCacheInvalidationPolicy(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND), 
                                                 calendar.get(Calendar.MILLISECOND));
        test = new CacheExpiryPolicyTest(dPolicy, true);
        test.setDescription("Ensure Objects expire with DAILY_EXPIRY.");
        test.setName("DAILY_EXPIRY - Expire");
        addTest(test);

        calendar = new GregorianCalendar();
        calendar.add(GregorianCalendar.HOUR_OF_DAY, 1);
        dPolicy = 
                new DailyCacheInvalidationPolicy(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND), 
                                                 calendar.get(Calendar.MILLISECOND));
        test = new CacheExpiryPolicyTest(dPolicy, false);
        test.setDescription("Ensure Objects survive with DAILY_EXPIRY.");
        test.setName("DAILY_EXPIRY - Live");
        addTest(test);

        test = new CacheExpiryPolicyTest(new NoExpiryCacheInvalidationPolicy(), false);
        test.setDescription("Ensure Objects survive with NO_EXPIRY.");
        test.setName("NO_EXPIRY - Live");
        addTest(test);

        long oneDay = 1000 * 60 * 60 * 24; // milliseconds in a day
        long minusOneDay = -1 * oneDay;

        test = new DailyCacheExpiryTest(oneDay + 1000, 0, false);
        test.setDescription("Ensure cache expiry works correctly when expiry is greater than one day away.");
        test.setName("Daily Expiry (+ One Day)");
        addTest(test);

        test = new DailyCacheExpiryTest(minusOneDay - 100, minusOneDay - 1000, true);
        test.setDescription("Ensure cache expiry works correctly when expiry is greater than one day ago.");
        test.setName("Daily Expiry (- One Day)");
        addTest(test);

        test = new DailyCacheExpiryTest(0, (3 * minusOneDay) - 1000, true);
        test.setDescription("Ensure cache expiry works correctly when expiry is many days.");
        test.setName("Daily Expiry (- 3 Days)");
        addTest(test);

        test = new DailyCacheExpiryTest(3 * oneDay, 0, false);
        test.setDescription("Ensure cache expiry works correctly when expiry is many days.");
        test.setName("Daily Expiry (+ 3 Days)");
        addTest(test);

        addTest(new NoExpiryInvalidationTest());

        PopulationManager manager = PopulationManager.getDefaultManager();
        Employee employeeToRead = (Employee)manager.getObject(Employee.class, "0001");

        ReadObjectQuery query = new ReadObjectQuery(employeeToRead);
        query.checkCacheThenDatabase();
        test = new CacheExpiryReadObjectQueryTest(employeeToRead, query, true);
        test.setDescription("Verify Read Object Query with expiry and a query object.");
        test.setName("ReadObjectQuery (query object) Expiry Test");
        addTest(test);

        query = new ReadObjectQuery(employeeToRead);
        query.checkCacheThenDatabase();
        test = new CacheExpiryReadObjectQueryTest(employeeToRead, query, false);
        test.setDescription("Verify Read Object Query which does not expire.");
        test.setName("ReadObjectQuery No Expiry Test");
        addTest(test);

        query = new ReadObjectQuery(Employee.class);
        query.checkCacheThenDatabase();
        test = new CacheExpiryReadObjectQueryTest(employeeToRead, query, false);
        test.setDescription("Verify Read Object Query which does not expire and queries with no selection criteria.");
        test.setName("ReadObjectQuery (no criteria) No Expiry Test");
        addTest(test);

        ExpressionBuilder exactPKBuilder = new ExpressionBuilder();
        Expression exactPK = exactPKBuilder.get("id").equal(employeeToRead.getId());
        query = new ReadObjectQuery(Employee.class, exactPK);
        query.checkCacheThenDatabase();
        test = new CacheExpiryReadObjectQueryTest(employeeToRead, query, false);
        test.setDescription("Verify Read Object Query which does not expire and queries by primary key.");
        test.setName("ReadObjectQuery (exact primary key) No Expiry Test");
        addTest(test);

        ExpressionBuilder inExactPKBuilder = new ExpressionBuilder();
        Expression inExactPK = inExactPKBuilder.get("firstName").equal(employeeToRead.getFirstName());
        inExactPK = inExactPK.and(inExactPKBuilder.get("lastName").equal(employeeToRead.getLastName()));
        inExactPK = inExactPK.and(inExactPKBuilder.get("id").equal(employeeToRead.getId()));
        query = new ReadObjectQuery(Employee.class, inExactPK);
        query.checkCacheThenDatabase();
        test = new CacheExpiryReadObjectQueryTest(employeeToRead, query, false);
        test.setDescription("Verify Read Object Query which does not expire and queries by in exact primary key.");
        test.setName("ReadObjectQuery (in-exact primary key) No Expiry Test");
        addTest(test);

        test = new CacheExpiryReadAllQueryTest(true);
        test.setDescription("Verify Read All Query which does expires.");
        test.setName("ReadAllQuery Expiry Test");
        addTest(test);

        test = new CacheExpiryReadAllQueryTest(false);
        test.setDescription("Verify Read All Query which does not expire.");
        test.setName("ReadAllQuery No Expiry Test");
        addTest(test);

        addTest(new ReadAllQueryCheckCacheOnlyExpiryTest());
        addTest(new ReadObjectCheckCacheOnlyExpiryTest());

        addTest(new RefreshQueryCacheExpiryTest());
        addTest(new RefreshIfNewerVersionTest());

        test = new UpdateQueryChangeExpiryTest(false);
        test.setDescription("Verify expiry time does not get updated when Updates are not set to update expiry.");
        test.setName("Update - do not update expiry test.");
        addTest(test);

        test = new UpdateQueryChangeExpiryTest(true);
        test.setDescription("Verify expiry time gets updated when updates are set to update expiry.");
        test.setName("Update - update expiry test.");
        addTest(test);

        addTest(new ReadAllQueryConformExpiryTest());
        addTest(new ReadObjectQueryConformExpiryTest());
        addTest(new CacheExpiryValueholderTest());
        addTest(new UnitOfWorkExpiredObjectTest());
        addTest(new CacheExpiryUnitOfWorkReadTest());
        addTest(new UnitOfWorkCreateObjectReadTimeTest());
        addTest(new SessionCreateObjectReadTimeTest());
        addTest(new InvalidateClassRecurseOptionTest(true));
        addTest(new InvalidateClassRecurseOptionTest(false));
        addTest(new InvalidateAllTest());
        addTest(new PrimaryKeyQueryInUOWTest());
        // EL bug 276362 - Re-Validate CacheKey before refreshing object graph
        addTest(new UnitOfWorkRefreshAfterInvalidationTest());
    }

}
