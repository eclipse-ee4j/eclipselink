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
package org.eclipse.persistence.testing.models.bigbad;

import java.util.*;

import java.io.StringWriter;
import java.math.*;

import org.eclipse.persistence.indirection.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.testing.framework.*;

/**
 * Define the project, create tables and populate for the big-bad model.
 */
public class BigBadSystem extends TestSystem {

    public BigBadSystem() {
        project = new BigBadProject();
    }

    public void addDescriptors(DatabaseSession session) {
        if (project == null) {
            project = new BigBadProject();
        }
        session.addDescriptors(project);
    }

    public void createTables(DatabaseSession session) {
        (new BigBadTableCreator()).replaceTables(session);
    }

    /**
     * Write 100 big bad instances with generated data.
     */
    public void populate(DatabaseSession session) {
        session.getLogin().useStringBinding(100);

        BigBadReferenceData referenceData = new BigBadReferenceData();
        referenceData.id = new BigDecimal(12345);
        referenceData.data = "some-reference-data";

        for (int index = 0; index < 25; index++) {
            BigBadObject bigBadObject = new BigBadObject();
            // Ensure some id overlap.
            bigBadObject.id01 = 100;
            bigBadObject.id02 = Math.max(index, 15);
            bigBadObject.id03 = Math.max(index, 15);
            bigBadObject.id04 = Math.max(index, 15);
            bigBadObject.id05 = Math.max(index, 15);
            bigBadObject.id06 = Math.max(index, 15);
            bigBadObject.id07 = Math.max(index, 15);
            bigBadObject.id08 = index;
            bigBadObject.id09 = index;
            bigBadObject.id10 = index;

            // Arg' the thin driver silently trims the data if the total
            // of the bound fields is > 4k.
            bigBadObject.blob = new byte[1000];
            bigBadObject.serializedBlob = new Vector(50);
            for (int count = 0; count < 50; count++) {
                bigBadObject.serializedBlob.add(new BigDecimal(count));
            }

            StringWriter stream = new StringWriter();
            for (int size = 0; size < 100; size++) {
                stream.write("abcde");
            }
            bigBadObject.largeString01 = stream.toString();
            bigBadObject.largeString02 = stream.toString();
            bigBadObject.largeString03 = stream.toString();

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

            bigBadObject.ref01 = new ValueHolder(referenceData);
            bigBadObject.ref02 = new ValueHolder(referenceData);
            bigBadObject.ref03 = new ValueHolder(referenceData);
            bigBadObject.ref04 = new ValueHolder(referenceData);
            bigBadObject.ref05 = new ValueHolder(referenceData);
            bigBadObject.ref06 = new ValueHolder(referenceData);
            bigBadObject.ref07 = new ValueHolder(referenceData);
            bigBadObject.ref08 = new ValueHolder(referenceData);
            bigBadObject.ref09 = new ValueHolder(referenceData);
            bigBadObject.ref10 = new ValueHolder(referenceData);

            BigBadAggregate agg = new BigBadAggregate();
            agg.number = new BigDecimal(12345);
            agg.string = "hello world";
            bigBadObject.agg01 = agg;
            bigBadObject.agg02 = agg;
            bigBadObject.agg03 = agg;
            bigBadObject.agg04 = agg;
            bigBadObject.agg05 = agg;

            session.writeObject(bigBadObject);
        }
    }
}
