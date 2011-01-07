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
package org.eclipse.persistence.testing.tests.performance.reading;

import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.testing.models.performance.Address;
import org.eclipse.persistence.testing.tests.performance.PerformanceTest;

/**
 * This tests the performance of huge read-all queries.
 * Its purpose is to compare the test result with previous release/label results.
 * It also provides a useful test for profiling performance.
 */
public class ReadAllHugeCacheAddressTest extends PerformanceTest {
    public ReadAllHugeCacheAddressTest() {
        // Needs to run for a long time.
        setTestRunTime(100000);
        setDescription("This tests the performance of huge read-all queries.");
    }

    public void setup() {
        super.setup();
        // Add 10000 address.
        for (int index = 0; index < 100; index++) {
            UnitOfWork uow = getSession().acquireUnitOfWork();
            for (int index2 = 0; index2 < 100; index2++) {
                Address address = new Address();
                address.setCity("NewCity");
                address.setCountry("Canada");
                address.setStreet("123 Bank");
                address.setProvince("ONT");
                uow.registerObject(address);
            }
            uow.commit();
        }

        // Fully load the cache.
        allObjects = getSession().readAllObjects(Address.class);
    }

    /**
     * Read employee and clear the cache, test database read.
     */
    public void test() throws Exception {
        super.test();
        getSession().readAllObjects(Address.class);
    }

    public void reset() {
        UnitOfWork uow = getSession().acquireUnitOfWork();
        uow.executeNonSelectingCall(new SQLCall("Delete from ADDRESS where CITY = 'NewCity'"));
        uow.commit();
    }
}
