/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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

import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.testing.models.performance.Address;
import org.eclipse.persistence.testing.models.performance.EmploymentPeriod;
import org.eclipse.persistence.testing.models.performance.toplink.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.tests.performance.reading.*;
import org.eclipse.persistence.testing.tests.performance.writing.*;

/**
 * Performance tests that compare the performance of two or more ways of doing something.
 * This allows for the performance difference between two task to be determined and verified.
 * This can be used for analyzing which is the best way to do something,
 * or to verify that usage of a optimization feature continues to provide the expected benefit.
 */
public class PerformanceComparisonModel extends TestModel {
    public PerformanceComparisonModel() {
        setDescription("Performance tests that compare/verify the performance of two or more ways of doing something.");
    }

    public void addRequiredSystems() {
        addRequiredSystem(new EmployeeSystem());
    }

    public void addTests() {
        addTest(getReadingTestSuite());
        addTest(getWritingTestSuite());
    }

    public TestSuite getReadingTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("ReadingTestSuite");
        suite.setDescription("This suite tests reading performance.");

        suite.addTest(new DeferredvsWriteLockTest());
        suite.addTest(new ReadObjectStaticvsDynamicTest());
        suite.addTest(new ReadObjectCachevsDatabaseTest());
        suite.addTest(new ReadObjectvsJoinTest());
        suite.addTest(new ReadAllvsJoinBatchTest());
        suite.addTest(new ReadAllvsBatch1mTest());
        suite.addTest(new ReadAllvsBatch21mTest());
        suite.addTest(new ReadAllvsInMemoryTest());
        suite.addTest(new ReadAllvsConformTest());
        suite.addTest(new ReadAllComplexvsConformTest());
        suite.addTest(new ReadAllvsConformNewUnitOfWorkTest());
        suite.addTest(new ReadObjectvsParameterizedSQLTest());
        suite.addTest(new ReadAllvsCursorTest());
        suite.addTest(new ReadAllStreamvsCursorTest());
        suite.addTest(new ReadAllMaxRowsFirstResultVsRownumFilteringTest());
        suite.addTest(new ReadAllMaxRowsVsRownumFilteringTest());
        suite.addTest(new ReadAllFirstResultVsRownumFilteringTest());
        suite.addTest(new ReadAllStreamvsCursorSizeTest());
        //suite.addTest(new ReadAllvsReadAllFromResultSet());
        //suite.addTest(new EmulatedReadAllvsReadAllFromResultSet());
        suite.addTest(new ReadObjectPreparedvsDynamicTest());
        suite.addTest(new EmulatedReadObjectPreparedvsDynamicTest());

        return suite;
    }

    public TestSuite getWritingTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("WritingTestSuite");
        suite.setDescription("This suite tests uow/writing performance.");

        suite.addTest(new InsertBatchUnitOfWorkComparisonTest());

        return suite;
    }

    public void setup() {
        for (int j = 0; j < 100; j++) {
            Employee empInsert = new Employee();
            empInsert.setFirstName("Brendan");
            empInsert.setMale();
            empInsert.setLastName("" + j + "");
            empInsert.setSalary(100000);
            EmploymentPeriod employmentPeriod = new EmploymentPeriod();
            java.sql.Date startDate = Helper.dateFromString("1901-12-31");
            java.sql.Date endDate = Helper.dateFromString("1895-01-01");
            employmentPeriod.setEndDate(startDate);
            employmentPeriod.setStartDate(endDate);
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
        // Add a large project to test performance of large projects.
        getDatabaseSession().addDescriptors(new org.eclipse.persistence.testing.models.interfaces.InterfaceHashtableProject());
    }
}
