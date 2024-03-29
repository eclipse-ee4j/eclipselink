/*
 * Copyright (c) 1998, 2022 Oracle and/or its affiliates. All rights reserved.
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
import oracle.spatial.geometry.JGeometry;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.expressions.spatial.SpatialExpressionFactory;
import org.eclipse.persistence.expressions.spatial.SpatialParameters;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.testing.framework.TestProblemException;
import org.eclipse.persistence.testing.models.spatial.jgeometry.wrapped.Spatial;
import org.eclipse.persistence.testing.models.spatial.jgeometry.wrapped.WrappedSpatial;
import org.eclipse.persistence.testing.tests.spatial.jgeometry.SQLReader;

import java.util.List;

/**
 * This test requires the following SQL be run prior to running the test suite:
 * CREATE OR REPLACE TYPE MY_GEOMETRY AS OBJECT (id NUMBER, geom MDSYS.SDO_GEOMETRY)
 *
 * Test validating the SpatialExpressionOperator. This makes use of params
 * passed directly in a strings instead of using SpatialOperator to handle the
 * parameter creation through API.
 *
 * These tests pass an expression for the first geometry and bin in a JGeometry
 * as a STRUCT for the second one.
 * SQL samples from C:\oracle\db\10.2\md\demo\examples\eginsert.sql
 * Note: Table re-named from TEST81 to SIMPLE_SPATIAL
 *
 * @author Doug Clarke - douglas.clarke@oracle.com
 */
public class Query_SpatialExpOp_ExpGeom_Tests extends WrappedSpatialTestCase {

    public Query_SpatialExpOp_ExpGeom_Tests(String name){
        super(name);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("Query_SpatialExpOp_ExpGeom_Tests");
        suite.addTest(new Query_SpatialExpOp_ExpGeom_Tests("testSDORelateRectangle"));
        suite.addTest(new Query_SpatialExpOp_ExpGeom_Tests("testSDORelateCircle"));
        suite.addTest(new Query_SpatialExpOp_ExpGeom_Tests("testSDORelateArbitraryLine"));
        suite.addTest(new Query_SpatialExpOp_ExpGeom_Tests("testSDOFilterRectangle"));
        suite.addTest(new Query_SpatialExpOp_ExpGeom_Tests("testSDOFilterRectangleNullParams"));
        suite.addTest(new Query_SpatialExpOp_ExpGeom_Tests("testSDOFilterCircle"));
        suite.addTest(new Query_SpatialExpOp_ExpGeom_Tests("testSDOFilterArbitraryLine"));
        suite.addTest(new Query_SpatialExpOp_ExpGeom_Tests("testSDOWithinDistanceRectangle"));
        suite.addTest(new Query_SpatialExpOp_ExpGeom_Tests("testSDOWithinDistanceRectangleUsingMaxResolution"));
        suite.addTest(new Query_SpatialExpOp_ExpGeom_Tests("testSDOWithinDistanceCircle"));
        suite.addTest(new Query_SpatialExpOp_ExpGeom_Tests("testSDOWithinDistanceArbitraryLine"));
        suite.addTest(new Query_SpatialExpOp_ExpGeom_Tests("testSDOWithinDistanceNullParamsMatchingCircle1004"));
        suite.addTest(new Query_SpatialExpOp_ExpGeom_Tests("testSDOWithinDistanceNullParamsNotMatching"));

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

    /**
     * SDO_RELATE using a dynamic rectangular window with lower left
     * and upper right coordinates of {(1,1), (20,20)}
     */
    public void testSDORelateRectangle() {
        String sql =
            "select GID, GEOMETRY from WRAPPED_SPATIAL ws where mdsys.sdo_relate(ws.geometry.geom, " +
            "mdsys.sdo_geometry(3,null,null, mdsys.sdo_elem_info_array(1,3,3),  " +
            "mdsys.sdo_ordinate_array(1,1, 20, 20)), " +
            "'MASK=ANYINTERACT QUERYTYPE=WINDOW') = 'TRUE' ORDER BY GID";

        SQLReader reader = new SQLReader(session, sql);

        JGeometry rectangle =
            JGeometry.createLinearPolygon(new double[] { 1, 1, 1, 20, 10, 20,
                                                         20, 1, 1, 1 }, 2, 0);

        ReadAllQuery raq = new ReadAllQuery(WrappedSpatial.class);
        ExpressionBuilder eb = raq.getExpressionBuilder();

        String params = "MASK=ANYINTERACT QUERYTYPE=WINDOW";
        Expression selectionCriteria = SpatialExpressionFactory.relate(eb.get("geometry").getField("geom"), rectangle, new SpatialParameters(params));
        raq.setSelectionCriteria(selectionCriteria);
        raq.addAscendingOrdering("id");

        @SuppressWarnings({"unchecked"})
        List<Spatial> results = (List<Spatial>)session.executeQuery(raq);

        String compareResult = reader.compare(results);

        assertNull(compareResult, compareResult);
    }

    /**
     * SDO_RELATE using a with a circle of radius 10 around (0,0)
     */
    public void testSDORelateCircle() {
        String sql =
            "select GID, GEOMETRY from WRAPPED_SPATIAL WS where mdsys.sdo_relate(WS.geometry.geom, " +
            "mdsys.sdo_geometry(3,null,null, " +
            "mdsys.sdo_elem_info_array(1,3,4), " +
            "mdsys.sdo_ordinate_array(-10,0, 0, 10, 10, 0)), " +
            "'MASK=ANYINTERACT QUERYTYPE=WINDOW') = 'TRUE' ORDER BY GID";

        SQLReader reader = new SQLReader(session, sql);

        JGeometry circle = JGeometry.createCircle(-10, 0, 0, 10, 10, 0, 0);

        ReadAllQuery raq = new ReadAllQuery(WrappedSpatial.class);
        ExpressionBuilder eb = raq.getExpressionBuilder();

        String params = "MASK=ANYINTERACT QUERYTYPE=WINDOW";
        Expression selectionCriteria = SpatialExpressionFactory.relate(eb.get("geometry").getField("geom"), circle, new SpatialParameters(params));
        raq.setSelectionCriteria(selectionCriteria);
        raq.addAscendingOrdering("id");

        raq.addAscendingOrdering("id");

        @SuppressWarnings({"unchecked"})
        List<Spatial> results = (List<Spatial>)session.executeQuery(raq);

        String compareResult = reader.compare(results);

        assertNull(compareResult, compareResult);
    }

    /**
     * SDO_RELATE using an arbitrary line string {(10,10), (20, 20), (30, 30), (45,45)}
     */
    public void testSDORelateArbitraryLine() {
        String sql =
            "select GID, GEOMETRY from WRAPPED_SPATIAL WS where mdsys.sdo_relate(" +
            "WS.geometry.geom, mdsys.sdo_geometry(2,null,null, " +
            "mdsys.sdo_elem_info_array(1,2,1), " +
            "mdsys.sdo_ordinate_array(10,10, 20,20, 30,30, 45,45)), " +
            "'MASK=ANYINTERACT QUERYTYPE=WINDOW') = 'TRUE' ORDER BY GID";

        SQLReader reader = new SQLReader(session, sql);

        JGeometry line =
            JGeometry.createLinearLineString(new double[] { 10, 10, 20, 20, 30,
                                                            30, 45, 45 }, 2,
                                             0);

        ReadAllQuery raq = new ReadAllQuery(WrappedSpatial.class);
        ExpressionBuilder eb = raq.getExpressionBuilder();

        String params = "MASK=ANYINTERACT QUERYTYPE=WINDOW";
        Expression selectionCriteria = SpatialExpressionFactory.relate(eb.get("geometry").getField("geom"), line, new SpatialParameters(params));
        raq.setSelectionCriteria(selectionCriteria);

        raq.addAscendingOrdering("id");

        @SuppressWarnings({"unchecked"})
        List<Spatial> results = (List<Spatial>)session.executeQuery(raq);

        String compareResult = reader.compare(results);

        assertNull(compareResult, compareResult);
    }

    /**
     * SDO_Filter using a dynamic rectangular window with lower left
     * and upper right coordinates of {(1,1), (20,20)}
     */
    public void testSDOFilterRectangle() {
        String sql =
            "select GID, GEOMETRY from WRAPPED_SPATIAL WS where mdsys.sdo_filter(" +
            "WS.geometry.geom, mdsys.sdo_geometry(3,null,null, " +
            "mdsys.sdo_elem_info_array(1,3,3), " +
            "mdsys.sdo_ordinate_array(1,1, 20, 20)), " +
            "'QUERYTYPE=WINDOW') = 'TRUE' ORDER BY GID";

        SQLReader reader = new SQLReader(session, sql);

        JGeometry rectangle =
            JGeometry.createLinearPolygon(new double[] { 1, 1, 1, 20, 10, 20,
                                                         20, 1, 1, 1 }, 2, 0);

        ReadAllQuery raq = new ReadAllQuery(WrappedSpatial.class);
        ExpressionBuilder eb = raq.getExpressionBuilder();

        String params = "QUERYTYPE=WINDOW";
        Expression selectionCriteria = SpatialExpressionFactory.filter(eb.get("geometry").getField("geom"), rectangle, new SpatialParameters(params));
        raq.setSelectionCriteria(selectionCriteria);
        raq.addAscendingOrdering("id");

        @SuppressWarnings({"unchecked"})
        List<Spatial> results = (List<Spatial>)session.executeQuery(raq);

        String compareResult = reader.compare(results);

        assertNull(compareResult, compareResult);
    }

    public void testSDOFilterRectangleNullParams() {
        String sql =
            "select GID, GEOMETRY from WRAPPED_SPATIAL WS where mdsys.sdo_filter(" +
            "WS.geometry.geom, mdsys.sdo_geometry(3,null,null, " +
            "mdsys.sdo_elem_info_array(1,3,3), " +
            "mdsys.sdo_ordinate_array(1,1, 20, 20)), " +
            "NULL) = 'TRUE' ORDER BY GID";

        SQLReader reader = new SQLReader(session, sql);

        JGeometry rectangle =
            JGeometry.createLinearPolygon(new double[] { 1, 1, 1, 20, 10, 20,
                                                         20, 1, 1, 1 }, 2, 0);

        ReadAllQuery raq = new ReadAllQuery(WrappedSpatial.class);
        ExpressionBuilder eb = raq.getExpressionBuilder();


        Expression selectionCriteria = SpatialExpressionFactory.filter(eb.get("geometry").getField("geom"), rectangle, null);
        raq.setSelectionCriteria(selectionCriteria);
        raq.addAscendingOrdering("id");

        @SuppressWarnings({"unchecked"})
        List<Spatial> results = (List<Spatial>)session.executeQuery(raq);

        String compareResult = reader.compare(results);

        assertNull(compareResult, compareResult);
    }

    /**
     * SDO_FILTER using a with a circle of radius 10 around (0,0)
     */
    public void testSDOFilterCircle() {
        String sql =
            "select GID, GEOMETRY from WRAPPED_SPATIAL WS where mdsys.sdo_filter(" +
            "WS.geometry.geom, mdsys.sdo_geometry(3,null,null, " +
            "mdsys.sdo_elem_info_array(1,3,4), " +
            "mdsys.sdo_ordinate_array(-10,0, 0, 10, 10, 0)), " +
            "'QUERYTYPE=WINDOW') = 'TRUE' ORDER BY GID";

        SQLReader reader = new SQLReader(session, sql);

        JGeometry circle = JGeometry.createCircle(-10, 0, 0, 10, 10, 0, 0);

        ReadAllQuery raq = new ReadAllQuery(WrappedSpatial.class);
        ExpressionBuilder eb = raq.getExpressionBuilder();

        String params = "QUERYTYPE=WINDOW";
        Expression selectionCriteria = SpatialExpressionFactory.filter(eb.get("geometry").getField("geom"), circle, new SpatialParameters(params));
        raq.setSelectionCriteria(selectionCriteria);
        raq.addAscendingOrdering("id");

        @SuppressWarnings({"unchecked"})
        List<Spatial> results = (List<Spatial>)session.executeQuery(raq);

        String compareResult = reader.compare(results);

        assertNull(compareResult, compareResult);
    }

    /**
     * SDO_FILTER using an arbitrary line string {(10,10), (20, 20), (30, 30), (45,45)}
     */
    public void testSDOFilterArbitraryLine() {
        String sql =
            "select GID, GEOMETRY from WRAPPED_SPATIAL WS where mdsys.sdo_filter(" +
            "WS.geometry.geom, mdsys.sdo_geometry(2,null,null, " +
            "mdsys.sdo_elem_info_array(1,2,1), " +
            "mdsys.sdo_ordinate_array(10,10, 20,20, 30,30, 45,45)), " +
            "'QUERYTYPE=WINDOW') = 'TRUE' ORDER BY GID";

        SQLReader reader = new SQLReader(session, sql);

        JGeometry line =
            JGeometry.createLinearLineString(new double[] { 10, 10, 20, 20, 30,
                                                            30, 45, 45 }, 2,
                                             0);

        ReadAllQuery raq = new ReadAllQuery(WrappedSpatial.class);
        ExpressionBuilder eb = raq.getExpressionBuilder();

        String params = "QUERYTYPE=WINDOW";
        Expression selectionCriteria = SpatialExpressionFactory.filter(eb.get("geometry").getField("geom"), line, new SpatialParameters(params));
        raq.setSelectionCriteria(selectionCriteria);
        raq.addAscendingOrdering("id");

        @SuppressWarnings({"unchecked"})
        List<Spatial> results = (List<Spatial>)session.executeQuery(raq);

        String compareResult = reader.compare(results);

        assertNull(compareResult, compareResult);
    }

    /**
     * SDO_WITHIN_DISTANCE using a dynamic rectangular window with lower left
     * and upper right coordinates of {(1,1), (20,20)}
     */
    public void testSDOWithinDistanceRectangle() {
        String sql =
            "select GID, GEOMETRY from WRAPPED_SPATIAL WS where mdsys.sdo_within_distance(" +
            "WS.geometry.geom, mdsys.sdo_geometry(3,null,null, mdsys.sdo_elem_info_array(1,3,3), " +
            "mdsys.sdo_ordinate_array(1,1, 20, 20)), " +
            "'DISTANCE=10') = 'TRUE' ORDER BY GID";

        SQLReader reader = new SQLReader(session, sql);

        JGeometry rectangle =
            JGeometry.createLinearPolygon(new double[] { 1, 1, 1, 20, 10, 20,
                                                         20, 1, 1, 1 }, 2, 0);

        ReadAllQuery raq = new ReadAllQuery(WrappedSpatial.class);
        ExpressionBuilder eb = raq.getExpressionBuilder();

        String params  = "DISTANCE=10";
        Expression selectionCriteria = SpatialExpressionFactory.withinDistance(eb.get("geometry").getField("geom"), rectangle, new SpatialParameters(params));
        raq.setSelectionCriteria(selectionCriteria);

        raq.addAscendingOrdering("id");

        @SuppressWarnings({"unchecked"})
        List<Spatial> results = (List<Spatial>)session.executeQuery(raq);

        String compareResult = reader.compare(results);

        assertNull(compareResult, compareResult);
    }

    public void testSDOWithinDistanceRectangleUsingMaxResolution() {
        String sql =
            "select GID, GEOMETRY from WRAPPED_SPATIAL WS where mdsys.sdo_within_distance(" +
            "WS.geometry.geom, mdsys.sdo_geometry(3,null,null, mdsys.sdo_elem_info_array(1,3,3), " +
            "mdsys.sdo_ordinate_array(1,1, 20, 20)), " +
            "'DISTANCE=10 MAX_RESOLUTION=5') = 'TRUE' ORDER BY GID";

        SQLReader reader = new SQLReader(session, sql);

        JGeometry rectangle =
            JGeometry.createLinearPolygon(new double[] { 1, 1, 1, 20, 10, 20,
                                                         20, 1, 1, 1 }, 2, 0);

        ReadAllQuery raq = new ReadAllQuery(WrappedSpatial.class);
        ExpressionBuilder eb = raq.getExpressionBuilder();

        String params  = "DISTANCE=10 MAX_RESOLUTION=5";
        Expression selectionCriteria = SpatialExpressionFactory.withinDistance(eb.get("geometry").getField("geom"), rectangle, new SpatialParameters(params));
        raq.setSelectionCriteria(selectionCriteria);
        raq.addAscendingOrdering("id");

        @SuppressWarnings({"unchecked"})
        List<Spatial> results = (List<Spatial>)session.executeQuery(raq);

        String compareResult = reader.compare(results);

        assertNull(compareResult, compareResult);
    }

    /**
     * SDO_WITHIN_DISTANCE using a with a circle of radius 10 around (0,0)
     */
    public void testSDOWithinDistanceCircle() {
        String sql =
            "select GID, GEOMETRY from WRAPPED_SPATIAL WS where " + "mdsys.sdo_within_distance(WS.geometry.geom, " +
            "mdsys.sdo_geometry(3,null,null, " +
            "mdsys.sdo_elem_info_array(1,3,4), " +
            "mdsys.sdo_ordinate_array(-10,0, 0, 10, 10, 0)), " +
            "'DISTANCE=10') = 'TRUE' ORDER BY GID";

        SQLReader reader = new SQLReader(session, sql);

        JGeometry circle = JGeometry.createCircle(-10, 0, 0, 10, 10, 0, 0);

        ReadAllQuery raq = new ReadAllQuery(WrappedSpatial.class);
        ExpressionBuilder eb = raq.getExpressionBuilder();

        String params  = "DISTANCE=10";
        Expression selectionCriteria = SpatialExpressionFactory.withinDistance(eb.get("geometry").getField("geom"), circle, new SpatialParameters(params));
        raq.setSelectionCriteria(selectionCriteria);
        raq.addAscendingOrdering("id");

        @SuppressWarnings({"unchecked"})
        List<Spatial> results = (List<Spatial>)session.executeQuery(raq);

        String compareResult = reader.compare(results);

        assertNull(compareResult, compareResult);
    }

    /**
     * SDO_WITHIN_DISTANCE using an arbitrary line string {(10,10), (20, 20), (30, 30), (45,45)}
     */
    public void testSDOWithinDistanceArbitraryLine() {
        String sql =
            "select GID, GEOMETRY from WRAPPED_SPATIAL WS where " + "mdsys.sdo_within_distance(WS.geometry.geom, " +
            "mdsys.sdo_geometry(2,null,null, " +
            "mdsys.sdo_elem_info_array(1,2,1), " +
            "mdsys.sdo_ordinate_array(10,10, 20,20, 30,30, 45,45)), " +
            "'DISTANCE=10') = 'TRUE' ORDER BY GID";

        SQLReader reader = new SQLReader(session, sql);

        JGeometry line =
            JGeometry.createLinearLineString(new double[] { 10, 10, 20, 20, 30,
                                                            30, 45, 45 }, 2,
                                             0);

        ReadAllQuery raq = new ReadAllQuery(WrappedSpatial.class);
        ExpressionBuilder eb = raq.getExpressionBuilder();

        String params  = "DISTANCE=10";
        Expression selectionCriteria = SpatialExpressionFactory.withinDistance(eb.get("geometry").getField("geom"), line, new SpatialParameters(params));
        raq.setSelectionCriteria(selectionCriteria);
        raq.addAscendingOrdering("id");

        @SuppressWarnings({"unchecked"})
        List<Spatial> results = (List<Spatial>)session.executeQuery(raq);

        String compareResult = reader.compare(results);

        assertNull(compareResult, compareResult);
    }

    /**
     * SDO_WITHIN_DISTANCE with NULL params matching a known circle geometry (1004)
     */
    public void testSDOWithinDistanceNullParamsMatchingCircle1004() {
        String sql =
            "select GID, GEOMETRY from WRAPPED_SPATIAL WS where " + "mdsys.sdo_within_distance(WS.geometry.geom, mdsys.sdo_geometry(3, " +
            "NULL, null, mdsys.sdo_elem_info_array(1,3,4), " +
            "mdsys.sdo_ordinate_array(1, 0, 0, 1, 0, -1)), " +
            "NULL) = 'TRUE' ORDER BY GID";

        SQLReader reader = new SQLReader(session, sql);

        JGeometry circle = JGeometry.createCircle(1, 0, 0, 1, 0, -1, 0);

        ReadAllQuery raq = new ReadAllQuery(WrappedSpatial.class);
        ExpressionBuilder eb = raq.getExpressionBuilder();

        String params  = "";
        Expression selectionCriteria = SpatialExpressionFactory.withinDistance(eb.get("geometry").getField("geom"), circle, new SpatialParameters(params));
        raq.setSelectionCriteria(selectionCriteria);

        raq.addAscendingOrdering("id");

        @SuppressWarnings({"unchecked"})
        List<Spatial> results = (List<Spatial>)session.executeQuery(raq);

        String compareResult = reader.compare(results);

        assertNull(compareResult, compareResult);
    }

    /**
     * SDO_WITHIN_DISTANCE with NULL params not matching existing
     */
    public void testSDOWithinDistanceNullParamsNotMatching() {
        String sql =
            "select GID, GEOMETRY from WRAPPED_SPATIAL WS where " + "mdsys.sdo_within_distance(WS.geometry.geom, mdsys.sdo_geometry(3, " +
            "NULL, null, mdsys.sdo_elem_info_array(1,3,4), " +
            "mdsys.sdo_ordinate_array(10, 0, 0, 10, 0, -10)), " +
            "NULL) = 'TRUE' ORDER BY GID";

        SQLReader reader = new SQLReader(session, sql);

        JGeometry circle = JGeometry.createCircle(10, 0, 0, 10, 0, -10, 0);

        ReadAllQuery raq = new ReadAllQuery(WrappedSpatial.class);
        ExpressionBuilder eb = raq.getExpressionBuilder();

        String params  = "";
        Expression selectionCriteria = SpatialExpressionFactory.withinDistance(eb.get("geometry").getField("geom"), circle, new SpatialParameters(params));
        raq.setSelectionCriteria(selectionCriteria);

        raq.addAscendingOrdering("id");

        @SuppressWarnings({"unchecked"})
        List<Spatial> results = (List<Spatial>)session.executeQuery(raq);

        String compareResult = reader.compare(results);

        assertNull(compareResult, compareResult);
    }
}
