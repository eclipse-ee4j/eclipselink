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
public class CreateTests extends SimpleSpatialTestCase {

    public CreateTests(String name){
        super(name);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("CreateTests");
        suite.addTest(new CreateTests("testCreate_SRID_0_WithoutBinding"));
        suite.addTest(new CreateTests("testCreate_SRID_0_WithBinding"));
        suite.addTest(new CreateTests("testInsertNullWithBinding"));
        suite.addTest(new CreateTests("testInsertNullWithoutBinding"));

        return new TestSetup(suite) {
            private boolean shouldBindAllParameters;
            protected void setUp(){
                try {
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

    public void testCreate_SRID_0_WithoutBinding() throws Exception {
        getSession().getLogin().setShouldBindAllParameters(false);

        SimpleSpatialTestCase.repopulate(getSession(), false);
    }

    public void testCreate_SRID_0_WithBinding() throws Exception {
        getSession().getLogin().setShouldBindAllParameters(true);

        SimpleSpatialTestCase.repopulate(getSession(), false);
    }

    public void testInsertNullWithBinding() throws Exception {
        getSession().getLogin().setShouldBindAllParameters(true);
        UnitOfWork uow = getSession().acquireUnitOfWork();

        SimpleSpatial ss = (SimpleSpatial)uow.newInstance(SimpleSpatial.class);
        ss.setId(123456789);

        uow.writeChanges();
        uow.release();
    }

    public void testInsertNullWithoutBinding() throws Exception {
        getSession().getLogin().setShouldBindAllParameters(false);
        UnitOfWork uow = getSession().acquireUnitOfWork();

        SimpleSpatial ss = (SimpleSpatial)uow.newInstance(SimpleSpatial.class);
        ss.setId(123456789);

        uow.writeChanges();
        uow.release();
    }
}
