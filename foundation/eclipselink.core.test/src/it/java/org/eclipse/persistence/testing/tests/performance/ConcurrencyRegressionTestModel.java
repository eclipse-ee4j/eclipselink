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
package org.eclipse.persistence.testing.tests.performance;

import org.eclipse.persistence.Version;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.server.Server;
import org.eclipse.persistence.testing.framework.TestModel;
import org.eclipse.persistence.testing.models.performance.Address;
import org.eclipse.persistence.testing.models.performance.EmploymentPeriod;
import org.eclipse.persistence.testing.models.performance.toplink.Employee;
import org.eclipse.persistence.testing.models.performance.toplink.EmployeeSystem;
import org.eclipse.persistence.testing.models.performance.toplink.PhoneNumber;
import org.eclipse.persistence.testing.tests.performance.concurrent.AddPhoneCachedConcurrentRegressionTest;
import org.eclipse.persistence.testing.tests.performance.concurrent.ReadAllObjectsConcurrentRegressionTest;
import org.eclipse.persistence.testing.tests.performance.concurrent.ReadAllUOWConcurrentRegressionTest;
import org.eclipse.persistence.testing.tests.performance.concurrent.ReadAnyObjectCachedConcurrentRegressionTest;
import org.eclipse.persistence.testing.tests.performance.concurrent.ReadAnyObjectIsolatedConcurrentRegressionTest;
import org.eclipse.persistence.testing.tests.performance.concurrent.ReadObjectCachedConcurrentRegressionTest;
import org.eclipse.persistence.testing.tests.performance.concurrent.ReadObjectCachedUOWConcurrentRegressionTest;
import org.eclipse.persistence.testing.tests.performance.concurrent.UpdateAnyObjectCachedConcurrentRegressionTest;

/**
 * Concurrency tests the compare the multi-CPU concurrency of various tasks.
 */
public class ConcurrencyRegressionTestModel extends TestModel {
    protected Session oldSession;

    public ConcurrencyRegressionTestModel() {
        setDescription("Multi-CPU concurrency tests.");
    }

    @Override
    public void addRequiredSystems() {
        addRequiredSystem(new EmployeeSystem());
    }

    @Override
    public void addTests() {
        addTest(new ReadObjectCachedConcurrentRegressionTest());
        addTest(new ReadAnyObjectCachedConcurrentRegressionTest());
        addTest(new ReadAnyObjectIsolatedConcurrentRegressionTest());
        addTest(new ReadObjectCachedUOWConcurrentRegressionTest());
        addTest(new ReadAllObjectsConcurrentRegressionTest());
        addTest(new ReadAllUOWConcurrentRegressionTest());
        addTest(new AddPhoneCachedConcurrentRegressionTest());
        addTest(new UpdateAnyObjectCachedConcurrentRegressionTest());
    }

    @Override
    public void setup() {
        for (int j = 0; j < 100; j++) {
            Employee empInsert = new Employee();
            empInsert.setFirstName("Brendan");
            empInsert.setMale();
            empInsert.setLastName("" + j);
            empInsert.setSalary(100000);
            EmploymentPeriod employmentPeriod = new EmploymentPeriod();
            java.sql.Date startDate = Helper.dateFromString("1901-12-31");
            java.sql.Date endDate = Helper.dateFromString("1895-01-01");
            employmentPeriod.setEndDate(endDate);
            employmentPeriod.setStartDate(startDate);
            empInsert.setPeriod(employmentPeriod);
            empInsert.setAddress(new Address());
            empInsert.getAddress().setCity("Nepean");
            empInsert.getAddress().setPostalCode("N5J2N5");
            empInsert.getAddress().setProvince("ON");
            empInsert.getAddress().setStreet("1111 Mountain Blvd. Floor 13, suite " + j);
            empInsert.getAddress().setCountry("Canada");
            empInsert.addPhoneNumber(new PhoneNumber("Work Fax", "613", "2255943"));
            empInsert.addPhoneNumber(new PhoneNumber("Home", "613", "2224599"));
            getDatabaseSession().insertObject(empInsert);
        }

        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        oldSession = getSession();
        Server serverSession = getSession().getProject().createServerSession(32, 32);
        serverSession.useExclusiveReadConnectionPool(32, 32);

        // Enable binding for 10.1.3 runs.
        if (Version.getVersion().contains("10.1.3")) {
            serverSession.getLogin().setShouldBindAllParameters(true);
        }
        serverSession.getLogin().setShouldCacheAllStatements(true);

        // Add a large project to test performance of large projects.
        serverSession.addDescriptors(new org.eclipse.persistence.testing.models.interfaces.InterfaceHashtableProject());
        serverSession.setSessionLog(getSession().getSessionLog());
        serverSession.login();
        getExecutor().setSession(serverSession);
    }

    @Override
    public void reset() {
        if (oldSession != null) {
            getDatabaseSession().logout();
            getExecutor().setSession(oldSession);
        }
    }
}
