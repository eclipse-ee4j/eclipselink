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
package org.eclipse.persistence.testing.tests.spatial.jgeometry.wrapped;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.queries.ReportQuery;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestProblemException;
import org.eclipse.persistence.testing.models.spatial.jgeometry.wrapped.WrappedSpatial;

/**
 * This test requires the following SQL be run prior to running the test suite:
 * CREATE OR REPLACE TYPE MY_GEOMETRY AS OBJECT (id NUMBER, geom MDSYS.SDO_GEOMETRY)
 *
 * SQL samples from C:\oracle\db\10.2\md\demo\examples\eginsert.sql
 * Note: Table re-named from TEST81 to SIMPLE_SPATIAL
 */
public class DeleteTests extends WrappedSpatialTestCase {

    public DeleteTests(String name){
        super(name);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("DeleteTests");
        suite.addTest(new DeleteTests("testAnySingleDelete"));

        return new TestSetup(suite) {
            protected void setUp(){
                try{
                    WrappedSpatialTestCase.repopulate(getSession(), true);
                } catch (Exception e){
                    throw new TestProblemException("Could not setup JGeometry test model. Note: This model requires you to run the following CREATE OR REPLACE TYPE MY_GEOMETRY AS OBJECT (id NUMBER, geom MDSYS.SDO_GEOMETRY): ", e);
                }
            }

            protected void tearDown() {
            }
        };
    }

    public void testAnySingleDelete() throws Exception {
        int initialQuantity = countWrappedSpatial(session);

        UnitOfWork uow = session.acquireUnitOfWork();

        WrappedSpatial ss = (WrappedSpatial)uow.readObject(WrappedSpatial.class);
        assertNotNull("No SimpleSpatial found", ss);
        uow.deleteObject(ss);

        uow.writeChanges();
        int afterCount = countWrappedSpatial(uow);

        assertEquals("Number of rows does not match", initialQuantity - 1,
                     afterCount);

        uow.release();
    }

    public static int countWrappedSpatial(Session session) {
        ReportQuery rq =
            new ReportQuery(WrappedSpatial.class, new ExpressionBuilder());

        rq.addCount();

        rq.setShouldReturnSingleValue(true);

        Number value = (Number)session.executeQuery(rq);

        return value.intValue();
    }
}
