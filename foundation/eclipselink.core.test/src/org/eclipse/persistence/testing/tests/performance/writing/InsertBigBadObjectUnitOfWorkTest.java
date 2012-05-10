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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.performance.writing;

import java.util.*;
import java.math.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.testing.models.bigbad.*;
import org.eclipse.persistence.testing.tests.performance.PerformanceTest;

/**
 * This tests the performance of unit of work inserts.
 * Its purpose is to compare the test result with previous release/label results.
 * It also provides a useful test for profiling performance.
 */
public class InsertBigBadObjectUnitOfWorkTest extends PerformanceTest {
    public InsertBigBadObjectUnitOfWorkTest() {
        setDescription("This tests the performance of unit of work inserts.");
    }

    /**
     * Insert address and then reset database/cache.
     */
    public void test() throws Exception {
        UnitOfWork uow = getSession().acquireUnitOfWork();

        BigBadObject bigBadObject = new BigBadObject();
        bigBadObject.id01 = 100;
        bigBadObject.id02 = 15;
        bigBadObject.id03 = 15;
        bigBadObject.id04 = 15;
        bigBadObject.id05 = 15;
        bigBadObject.id06 = 15;
        bigBadObject.id07 = 15;
        bigBadObject.id08 = 200;
        bigBadObject.id09 = 200;
        bigBadObject.id10 = 200;

        // Arg' the thin driver silently trims the data if the total
        // of the bound fields is > 4k.
        bigBadObject.blob = new byte[1000];
        bigBadObject.serializedBlob = new ArrayList(50);
        for (int count = 0; count < 50; count++) {
            bigBadObject.serializedBlob.add(new BigDecimal(count));
        }

        bigBadObject.largeString01 = new String(new char[500]);
        bigBadObject.largeString02 = new String(new char[500]);
        bigBadObject.largeString03 = new String(new char[500]);

        bigBadObject.number01 = new BigDecimal(12345);
        bigBadObject.number02 = new BigDecimal(12345);

        bigBadObject.string01 = "this is a story of a lovely lady and 3 girls and a charming man and 3 boys";
        bigBadObject.string02 = "this is a story of a lovely lady and 3 girls and a charming man and 3 boys";
        bigBadObject.string03 = "this is a story of a lovely lady and 3 girls and a charming man and 3 boys";
        bigBadObject.string04 = "this is a story of a lovely lady and 3 girls and a charming man and 3 boys";
        bigBadObject.string05 = "this is a story of a lovely lady and 3 girls and a charming man and 3 boys";
        bigBadObject.string06 = "this is a story of a lovely lady and 3 girls and a charming man and 3 boys";
        bigBadObject.string07 = "this is a story of a lovely lady and 3 girls and a charming man and 3 boys";
        bigBadObject.string08 = "this is a story of a lovely lady and 3 girls and a charming man and 3 boys";
        bigBadObject.string09 = "this is a story of a lovely lady and 3 girls and a charming man and 3 boys";
        bigBadObject.string10 = "this is a story of a lovely lady and 3 girls and a charming man and 3 boys";
        bigBadObject.string11 = "this is a story of a lovely lady and 3 girls and a charming man and 3 boys";
        bigBadObject.string12 = "this is a story of a lovely lady and 3 girls and a charming man and 3 boys";
        bigBadObject.string13 = "this is a story of a lovely lady and 3 girls and a charming man and 3 boys";
        bigBadObject.string14 = "this is a story of a lovely lady and 3 girls and a charming man and 3 boys";
        bigBadObject.string15 = "this is a story of a lovely lady and 3 girls and a charming man and 3 boys";
        bigBadObject.string16 = "this is a story of a lovely lady and 3 girls and a charming man and 3 boys";
        bigBadObject.string17 = "this is a story of a lovely lady and 3 girls and a charming man and 3 boys";
        bigBadObject.string18 = "this is a story of a lovely lady and 3 girls and a charming man and 3 boys";
        bigBadObject.string19 = "this is a story of a lovely lady and 3 girls and a charming man and 3 boys";
        bigBadObject.string20 = "this is a story of a lovely lady and 3 girls and a charming man and 3 boys";

        bigBadObject.calendar01 = Calendar.getInstance();
        bigBadObject.calendar02 = Calendar.getInstance();
        bigBadObject.calendar03 = Calendar.getInstance();
        bigBadObject.calendar04 = Calendar.getInstance();
        bigBadObject.calendar05 = Calendar.getInstance();
        bigBadObject.calendar06 = Calendar.getInstance();
        bigBadObject.calendar07 = Calendar.getInstance();
        bigBadObject.calendar08 = Calendar.getInstance();
        bigBadObject.calendar09 = Calendar.getInstance();
        bigBadObject.calendar10 = Calendar.getInstance();

        bigBadObject.date01 = new java.sql.Date(new java.util.Date().getTime());
        bigBadObject.date02 = new java.sql.Date(new java.util.Date().getTime());
        bigBadObject.date03 = new java.sql.Date(new java.util.Date().getTime());
        bigBadObject.date04 = new java.sql.Date(new java.util.Date().getTime());
        bigBadObject.date05 = new java.sql.Date(new java.util.Date().getTime());
        bigBadObject.date06 = new java.sql.Date(new java.util.Date().getTime());
        bigBadObject.date07 = new java.sql.Date(new java.util.Date().getTime());
        bigBadObject.date08 = new java.sql.Date(new java.util.Date().getTime());
        bigBadObject.date09 = new java.sql.Date(new java.util.Date().getTime());
        bigBadObject.date10 = new java.sql.Date(new java.util.Date().getTime());

        bigBadObject.time01 = new java.sql.Time(new java.util.Date().getTime());
        bigBadObject.time02 = new java.sql.Time(new java.util.Date().getTime());
        bigBadObject.time03 = new java.sql.Time(new java.util.Date().getTime());
        bigBadObject.time04 = new java.sql.Time(new java.util.Date().getTime());
        bigBadObject.time05 = new java.sql.Time(new java.util.Date().getTime());
        bigBadObject.time06 = new java.sql.Time(new java.util.Date().getTime());
        bigBadObject.time07 = new java.sql.Time(new java.util.Date().getTime());
        bigBadObject.time08 = new java.sql.Time(new java.util.Date().getTime());
        bigBadObject.time09 = new java.sql.Time(new java.util.Date().getTime());
        bigBadObject.time10 = new java.sql.Time(new java.util.Date().getTime());

        bigBadObject.timestamp01 = new java.sql.Timestamp(new java.util.Date().getTime());
        bigBadObject.timestamp02 = new java.sql.Timestamp(new java.util.Date().getTime());
        bigBadObject.timestamp03 = new java.sql.Timestamp(new java.util.Date().getTime());
        bigBadObject.timestamp04 = new java.sql.Timestamp(new java.util.Date().getTime());
        bigBadObject.timestamp05 = new java.sql.Timestamp(new java.util.Date().getTime());
        bigBadObject.timestamp06 = new java.sql.Timestamp(new java.util.Date().getTime());
        bigBadObject.timestamp07 = new java.sql.Timestamp(new java.util.Date().getTime());
        bigBadObject.timestamp08 = new java.sql.Timestamp(new java.util.Date().getTime());
        bigBadObject.timestamp09 = new java.sql.Timestamp(new java.util.Date().getTime());
        bigBadObject.timestamp10 = new java.sql.Timestamp(new java.util.Date().getTime());

        BigBadAggregate agg = new BigBadAggregate();
        agg.number = new BigDecimal(12345);
        agg.string = "hello world";
        bigBadObject.agg01 = agg;
        bigBadObject.agg02 = agg;
        bigBadObject.agg03 = agg;
        bigBadObject.agg04 = agg;
        bigBadObject.agg05 = agg;

        uow.registerObject(bigBadObject);

        uow.commit();
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        getSession().executeNonSelectingCall(new SQLCall("Delete from BIG_BAD_OBJ where ID10 = 200"));
    }
}
