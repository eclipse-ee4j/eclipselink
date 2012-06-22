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
package org.eclipse.persistence.testing.tests.spatial.jgeometry.wrapped;

import java.util.List;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestSuite;


import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.testing.framework.TestProblemException;
import org.eclipse.persistence.testing.models.spatial.jgeometry.wrapped.Spatial;
import org.eclipse.persistence.testing.models.spatial.jgeometry.wrapped.WrappedSpatial;
import org.eclipse.persistence.testing.tests.spatial.jgeometry.SQLReader;

/**
 * This test requires the following SQL be run prior to running the test suite:
 * CREATE OR REPLACE TYPE MY_GEOMETRY AS OBJECT (id NUMBER, geom MDSYS.SDO_GEOMETRY)
 *
 * Query tests that do not involve using a spatial operator in the selection
 * criteria.
 */
public class Query_OrderedHint extends WrappedSpatialTestCase {

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
                    WrappedSpatialTestCase.repopulate(getSession(), true);
                } catch (Exception e){
                    throw new TestProblemException("Could not setup JGeometry test model. Note: This model requires you to run the following CREATE OR REPLACE TYPE MY_GEOMETRY AS OBJECT (id NUMBER, geom MDSYS.SDO_GEOMETRY): ", e);
                }
            }

            protected void tearDown() {
            }
        };
    }

    public void testReadAll() throws Exception {
        String sql = 
            "SELECT /*+ ORDERED */ GID, GEOMETRY FROM WRAPPED_SPATIAL WS ORDER BY GID";
        SQLReader reader = new SQLReader(session, sql);

        ReadAllQuery raq = new ReadAllQuery(WrappedSpatial.class);
        raq.addAscendingOrdering("id");
        raq.setHintString("/*+ ORDERED */ ");

        List<Spatial> results = (List<Spatial>)session.executeQuery(raq);

        String compareResult = reader.compare(results);

        assertNull(compareResult, compareResult);
    }
}
