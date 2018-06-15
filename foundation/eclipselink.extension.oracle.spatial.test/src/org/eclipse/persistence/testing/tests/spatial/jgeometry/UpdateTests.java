/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.spatial.jgeometry;

import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.queries.UpdateAllQuery;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestProblemException;
import org.eclipse.persistence.testing.models.spatial.jgeometry.SimpleSpatial;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestSuite;


/**
 * SQL samples from C:\oracle\db\10.2\md\demo\examples\eginsert.sql
 * Note: Table re-named from TEST81 to SIMPLE_SPATIAL
 */
public class UpdateTests extends SimpleSpatialTestCase {

    public UpdateTests(String name){
        super(name);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("UpdateTests");
        suite.addTest(new UpdateTests("testUpdateSingleToNull"));
        suite.addTest(new UpdateTests("testUpdateAllToNull"));
        suite.addTest(new UpdateTests("testReplaceExisting"));
        suite.addTest(new UpdateTests("testUpdateNull"));

        return new TestSetup(suite) {
            private boolean shouldBindAllParameters;
            protected void setUp(){
                try{
                    shouldBindAllParameters = getSession().getLogin().getShouldBindAllParameters();
                    SimpleSpatialTestCase.repopulate(getSession(), true);
                } catch (Exception e){
                    throw new TestProblemException("Could not setup JGeometry test model", e);
                }
            }

            protected void tearDown() {
                try {
                    getSession().getLogin().setShouldBindAllParameters(shouldBindAllParameters);
                } catch (Exception e){
                    throw new TestProblemException("Could not clean up JGeometry test model", e);
                }
            }
        };
    }

    public void testUpdateSingleToNull() throws Exception {
        UnitOfWork uow = session.acquireUnitOfWork();

        SimpleSpatial ss =
            (SimpleSpatial)uow.readObject(SimpleSpatial.class, new ExpressionBuilder().get("geometry").notNull());
        assertNotNull("No SimpleSpatial instances found", ss);

        ss.setJGeometry(null);

        uow.writeChanges();
        uow.release();
    }

    public void testUpdateAllToNull() throws Exception {
        session.getLogin().setShouldBindAllParameters(false);
        UnitOfWork uow = session.acquireUnitOfWork();

        UpdateAllQuery uaq = new UpdateAllQuery(SimpleSpatial.class);
        uaq.addUpdate("geometry", (Object)null);
        // BUG: 5102352 - workaround requires selection criteria
        uaq.setSelectionCriteria(uaq.getExpressionBuilder().get("id").notNull());

        try {
            uow.executeQuery(uaq);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

        uow.writeChanges();
        uow.release();
    }

    public void testReplaceExisting() {
        UnitOfWork uow = session.acquireUnitOfWork();

        SimpleSpatial ss =
            (SimpleSpatial)uow.readObject(SimpleSpatial.class, new ExpressionBuilder().get("id").equal(1001));
        assertNotNull("No SimpleSpatial instances found", ss);

        ss.setJGeometry(new SampleGeometries().simplyPolygon());

        uow.writeChanges();
        uow.release();
    }

    public void testUpdateNull() {
        UnitOfWork uow = session.acquireUnitOfWork();

        SimpleSpatial ss =
            (SimpleSpatial)uow.readObject(SimpleSpatial.class, new ExpressionBuilder().get("geometry").isNull());
        assertNotNull("No SimpleSpatial instances found", ss);

        ss.setJGeometry(new SampleGeometries().simplyPolygon());

        uow.writeChanges();
        uow.release();
    }
}
