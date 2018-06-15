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

import java.util.List;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.testing.framework.TestProblemException;
import org.eclipse.persistence.testing.models.spatial.jgeometry.SimpleSpatial;
import org.eclipse.persistence.testing.models.spatial.jgeometry.wrapped.Spatial;


/**
 * Query tests that do not involve using a spatial operator in the selection
 * criteria.
 */
public class Query_OrderedHint extends SimpleSpatialTestCase {

    public Query_OrderedHint(String name){
        super(name);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("Query_OrderedHint");
        suite.addTest(new Query_OrderedHint("testReadAll"));

        return new TestSetup(suite) {
            protected void setUp(){
                try{
                    SimpleSpatialTestCase.repopulate(getSession(), true);
                } catch (Exception e){
                    throw new TestProblemException("Could not setup JGeometry test model", e);
                }
            }

            protected void tearDown() {
            }
        };
    }

    public void testReadAll() throws Exception {
        String sql = "select GID, GEOMETRY from SIMPLE_SPATIAL ORDER BY GID";
        SQLReader reader = new SQLReader(session, sql);

        ReadAllQuery raq = new ReadAllQuery(SimpleSpatial.class);
        raq.addAscendingOrdering("id");
        raq.setHintString("/*+ ORDERED */ ");

        List<Spatial> results =
            (List<Spatial>)session.executeQuery(raq);

        String compareResult = reader.compare(results);

        assertNull(compareResult, compareResult);
    }
}
