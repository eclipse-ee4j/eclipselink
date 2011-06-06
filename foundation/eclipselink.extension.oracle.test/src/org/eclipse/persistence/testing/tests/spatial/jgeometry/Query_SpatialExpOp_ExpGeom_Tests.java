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
package org.eclipse.persistence.testing.tests.spatial.jgeometry;

import java.util.List;

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
import org.eclipse.persistence.testing.models.spatial.jgeometry.SimpleSpatial;
import org.eclipse.persistence.testing.models.spatial.jgeometry.wrapped.Spatial;


/**
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
public class Query_SpatialExpOp_ExpGeom_Tests extends SimpleSpatialTestCase {

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
                    SimpleSpatialTestCase.repopulate(getSession(), true);
                } catch (Exception e){
                    throw new TestProblemException("Could not setup JGeometry test model", e);
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
    public void testSDORelateRectangle() throws Exception {
        String sql = 
            "select GID, GEOMETRY from SIMPLE_SPATIAL where mdsys.sdo_relate(geometry, " + 
            "mdsys.sdo_geometry(3,null,null, mdsys.sdo_elem_info_array(1,3,3),  " + 
            "mdsys.sdo_ordinate_array(1,1, 20, 20)), " + 
            "'MASK=ANYINTERACT QUERYTYPE=WINDOW') = 'TRUE' ORDER BY GID";

        SQLReader reader = new SQLReader(session, sql);

        JGeometry rectangle = 
            JGeometry.createLinearPolygon(new double[] { 1, 1, 1, 20, 10, 20, 
                                                         20, 1, 1, 1 }, 2, 0);

        ReadAllQuery raq = new ReadAllQuery(SimpleSpatial.class);
        ExpressionBuilder eb = raq.getExpressionBuilder();

        Expression selectionCriteria = SpatialExpressionFactory.relate(eb.get("geometry"), rectangle, new SpatialParameters("MASK=ANYINTERACT QUERYTYPE=WINDOW"));
        raq.setSelectionCriteria(selectionCriteria);
        raq.addAscendingOrdering("id");
        
        raq.addAscendingOrdering("id");

        List<Spatial> results = 
            (List<Spatial>)session.executeQuery(raq);

        String compareResult = reader.compare(results);

        assertNull(compareResult, compareResult);
    }

    /**
     * SDO_RELATE using a with a circle of radius 10 around (0,0)
     */
    public void testSDORelateCircle() throws Exception {
        String sql = 
            "select GID, GEOMETRY from SIMPLE_SPATIAL where mdsys.sdo_relate(geometry, " + 
            "mdsys.sdo_geometry(3,null,null, " + 
            "mdsys.sdo_elem_info_array(1,3,4), " + 
            "mdsys.sdo_ordinate_array(-10,0, 0, 10, 10, 0)), " + 
            "'MASK=ANYINTERACT QUERYTYPE=WINDOW') = 'TRUE' ORDER BY GID";

        SQLReader reader = new SQLReader(session, sql);

        JGeometry circle = JGeometry.createCircle(-10, 0, 0, 10, 10, 0, 0);

        ReadAllQuery raq = new ReadAllQuery(SimpleSpatial.class);
        ExpressionBuilder eb = raq.getExpressionBuilder();
      
        Expression selectionCriteria = SpatialExpressionFactory.relate(eb.get("geometry"), circle, new SpatialParameters("MASK=ANYINTERACT QUERYTYPE=WINDOW"));
        raq.setSelectionCriteria(selectionCriteria);
        raq.addAscendingOrdering("id");

        List<Spatial> results = 
            (List<Spatial>)session.executeQuery(raq);

        String compareResult = reader.compare(results);

        assertNull(compareResult, compareResult);
    }

    /**
     * SDO_RELATE using an arbitrary line string {(10,10), (20, 20), (30, 30), (45,45)}
     */
    public void testSDORelateArbitraryLine() throws Exception {
        String sql = 
            "select GID, GEOMETRY from SIMPLE_SPATIAL where mdsys.sdo_relate(" + 
            "geometry, mdsys.sdo_geometry(2,null,null, " + 
            "mdsys.sdo_elem_info_array(1,2,1), " + 
            "mdsys.sdo_ordinate_array(10,10, 20,20, 30,30, 45,45)), " + 
            "'MASK=ANYINTERACT QUERYTYPE=WINDOW') = 'TRUE' ORDER BY GID";

        SQLReader reader = new SQLReader(session, sql);

        JGeometry line = 
            JGeometry.createLinearLineString(new double[] { 10, 10, 20, 20, 30, 
                                                            30, 45, 45 }, 2, 
                                             0);

        ReadAllQuery raq = new ReadAllQuery(SimpleSpatial.class);
        ExpressionBuilder eb = raq.getExpressionBuilder();
        
        Expression selectionCriteria = SpatialExpressionFactory.relate(eb.get("geometry"), line, new SpatialParameters("MASK=ANYINTERACT QUERYTYPE=WINDOW"));
        raq.setSelectionCriteria(selectionCriteria);
        
        raq.addAscendingOrdering("id");

        List<Spatial> results = 
            (List<Spatial>)session.executeQuery(raq);

        String compareResult = reader.compare(results);

        assertNull(compareResult, compareResult);
    }

    /**
     * SDO_Filter using a dynamic rectangular window with lower left
     * and upper right coordinates of {(1,1), (20,20)}
     */
    public void testSDOFilterRectangle() throws Exception {
        String sql = 
            "select GID, GEOMETRY from SIMPLE_SPATIAL where mdsys.sdo_filter(" + 
            "geometry, mdsys.sdo_geometry(3,null,null, " + 
            "mdsys.sdo_elem_info_array(1,3,3), " + 
            "mdsys.sdo_ordinate_array(1,1, 20, 20)), " + 
            "'QUERYTYPE=WINDOW') = 'TRUE' ORDER BY GID";

        SQLReader reader = new SQLReader(session, sql);

        JGeometry rectangle = 
            JGeometry.createLinearPolygon(new double[] { 1, 1, 1, 20, 10, 20, 
                                                         20, 1, 1, 1 }, 2, 0);

        ReadAllQuery raq = new ReadAllQuery(SimpleSpatial.class);
        ExpressionBuilder eb = raq.getExpressionBuilder();

        Expression selectionCriteria = SpatialExpressionFactory.filter(eb.get("geometry"), rectangle, new SpatialParameters("QUERYTYPE=WINDOW"));
        raq.setSelectionCriteria(selectionCriteria);     
        raq.addAscendingOrdering("id");

        List<Spatial> results = 
            (List<Spatial>)session.executeQuery(raq);

        String compareResult = reader.compare(results);

        assertNull(compareResult, compareResult);
    }

    public void testSDOFilterRectangleNullParams() throws Exception {
        String sql = 
            "select GID, GEOMETRY from SIMPLE_SPATIAL where mdsys.sdo_filter(" + 
            "geometry, mdsys.sdo_geometry(3,null,null, " + 
            "mdsys.sdo_elem_info_array(1,3,3), " + 
            "mdsys.sdo_ordinate_array(1,1, 20, 20)), " + 
            "NULL) = 'TRUE' ORDER BY GID";

        SQLReader reader = new SQLReader(session, sql);

        JGeometry rectangle = 
            JGeometry.createLinearPolygon(new double[] { 1, 1, 1, 20, 10, 20, 
                                                         20, 1, 1, 1 }, 2, 0);

        ReadAllQuery raq = new ReadAllQuery(SimpleSpatial.class);
        ExpressionBuilder eb = raq.getExpressionBuilder();

        Expression selectionCriteria = SpatialExpressionFactory.filter(eb.get("geometry"), rectangle, null);
        raq.setSelectionCriteria(selectionCriteria);     
        raq.addAscendingOrdering("id");

        List<Spatial> results = 
            (List<Spatial>)session.executeQuery(raq);

        String compareResult = reader.compare(results);

        assertNull(compareResult, compareResult);
    }

    /**
     * SDO_FILTER using a with a circle of radius 10 around (0,0)
     */
    public void testSDOFilterCircle() throws Exception {
        String sql = 
            "select GID, GEOMETRY from SIMPLE_SPATIAL where mdsys.sdo_filter(" + 
            "geometry, mdsys.sdo_geometry(3,null,null, " + 
            "mdsys.sdo_elem_info_array(1,3,4), " + 
            "mdsys.sdo_ordinate_array(-10,0, 0, 10, 10, 0)), " + 
            "'QUERYTYPE=WINDOW') = 'TRUE' ORDER BY GID";

        SQLReader reader = new SQLReader(session, sql);

        JGeometry circle = JGeometry.createCircle(-10, 0, 0, 10, 10, 0, 0);

        ReadAllQuery raq = new ReadAllQuery(SimpleSpatial.class);
        ExpressionBuilder eb = raq.getExpressionBuilder();

        Expression selectionCriteria = SpatialExpressionFactory.filter(eb.get("geometry"), circle, new SpatialParameters("QUERYTYPE=WINDOW"));
        raq.setSelectionCriteria(selectionCriteria);     
        raq.addAscendingOrdering("id");

        List<Spatial> results = 
            (List<Spatial>)session.executeQuery(raq);

        String compareResult = reader.compare(results);

        assertNull(compareResult, compareResult);
    }

    /**
     * SDO_FILTER using an arbitrary line string {(10,10), (20, 20), (30, 30), (45,45)}
     */
    public void testSDOFilterArbitraryLine() throws Exception {
        String sql = 
            "select GID, GEOMETRY from SIMPLE_SPATIAL where mdsys.sdo_filter(" + 
            "geometry, mdsys.sdo_geometry(2,null,null, " + 
            "mdsys.sdo_elem_info_array(1,2,1), " + 
            "mdsys.sdo_ordinate_array(10,10, 20,20, 30,30, 45,45)), " + 
            "'QUERYTYPE=WINDOW') = 'TRUE' ORDER BY GID";

        SQLReader reader = new SQLReader(session, sql);

        JGeometry line = 
            JGeometry.createLinearLineString(new double[] { 10, 10, 20, 20, 30, 
                                                            30, 45, 45 }, 2, 
                                             0);

        ReadAllQuery raq = new ReadAllQuery(SimpleSpatial.class);
        ExpressionBuilder eb = raq.getExpressionBuilder();
        
        Expression selectionCriteria = SpatialExpressionFactory.filter(eb.get("geometry"), line, new SpatialParameters("QUERYTYPE=WINDOW"));
        raq.setSelectionCriteria(selectionCriteria);     
        raq.addAscendingOrdering("id");

        List<Spatial> results = 
            (List<Spatial>)session.executeQuery(raq);

        String compareResult = reader.compare(results);

        assertNull(compareResult, compareResult);
    }

    /**
     * SDO_WITHIN_DISTANCE using a dynamic rectangular window with lower left
     * and upper right coordinates of {(1,1), (20,20)}
     */
    public void testSDOWithinDistanceRectangle() throws Exception {
        String sql = 
            "select GID, GEOMETRY from SIMPLE_SPATIAL where mdsys.sdo_within_distance(" + 
            "geometry, mdsys.sdo_geometry(3,null,null, mdsys.sdo_elem_info_array(1,3,3), " + 
            "mdsys.sdo_ordinate_array(1,1, 20, 20)), " + 
            "'DISTANCE=10') = 'TRUE' ORDER BY GID";

        SQLReader reader = new SQLReader(session, sql);

        JGeometry rectangle = 
            JGeometry.createLinearPolygon(new double[] { 1, 1, 1, 20, 10, 20, 
                                                         20, 1, 1, 1 }, 2, 0);

        ReadAllQuery raq = new ReadAllQuery(SimpleSpatial.class);
        ExpressionBuilder eb = raq.getExpressionBuilder();

        Expression selectionCriteria = SpatialExpressionFactory.withinDistance(eb.get("geometry"), rectangle, new SpatialParameters("DISTANCE=10"));
        raq.setSelectionCriteria(selectionCriteria);     
        raq.addAscendingOrdering("id");

        List<Spatial> results = 
            (List<Spatial>)session.executeQuery(raq);

        String compareResult = reader.compare(results);

        assertNull(compareResult, compareResult);
    }

    public void testSDOWithinDistanceRectangleUsingMaxResolution() throws Exception {
        String sql = 
            "select GID, GEOMETRY from SIMPLE_SPATIAL where mdsys.sdo_within_distance(" + 
            "geometry, mdsys.sdo_geometry(3,null,null, mdsys.sdo_elem_info_array(1,3,3), " + 
            "mdsys.sdo_ordinate_array(1,1, 20, 20)), " + 
            "'DISTANCE=10 MAX_RESOLUTION=5') = 'TRUE' ORDER BY GID";

        SQLReader reader = new SQLReader(session, sql);

        JGeometry rectangle = 
            JGeometry.createLinearPolygon(new double[] { 1, 1, 1, 20, 10, 20, 
                                                         20, 1, 1, 1 }, 2, 0);

        ReadAllQuery raq = new ReadAllQuery(SimpleSpatial.class);
        ExpressionBuilder eb = raq.getExpressionBuilder();

        Expression selectionCriteria = SpatialExpressionFactory.withinDistance(eb.get("geometry"), rectangle, new SpatialParameters("DISTANCE=10 MAX_RESOLUTION=5"));
        raq.setSelectionCriteria(selectionCriteria); 
        raq.addAscendingOrdering("id");

        List<Spatial> results = 
            (List<Spatial>)session.executeQuery(raq);

        String compareResult = reader.compare(results);

        assertNull(compareResult, compareResult);
    }

    /**
     * SDO_WITHIN_DISTANCE using a with a circle of radius 10 around (0,0)
     */
    public void testSDOWithinDistanceCircle() throws Exception {
        String sql = 
            "select GID, GEOMETRY from SIMPLE_SPATIAL where " + "mdsys.sdo_within_distance(geometry, " + 
            "mdsys.sdo_geometry(3,null,null, " + 
            "mdsys.sdo_elem_info_array(1,3,4), " + 
            "mdsys.sdo_ordinate_array(-10,0, 0, 10, 10, 0)), " + 
            "'DISTANCE=10') = 'TRUE' ORDER BY GID";

        SQLReader reader = new SQLReader(session, sql);

        JGeometry circle = JGeometry.createCircle(-10, 0, 0, 10, 10, 0, 0);

        ReadAllQuery raq = new ReadAllQuery(SimpleSpatial.class);
        ExpressionBuilder eb = raq.getExpressionBuilder();

        Expression selectionCriteria = SpatialExpressionFactory.withinDistance(eb.get("geometry"), circle, new SpatialParameters("DISTANCE=10"));
        raq.setSelectionCriteria(selectionCriteria); 
        raq.addAscendingOrdering("id");

        List<Spatial> results = 
            (List<Spatial>)session.executeQuery(raq);

        String compareResult = reader.compare(results);

        assertNull(compareResult, compareResult);
    }

    /**
     * SDO_WITHIN_DISTANCE using an arbitrary line string {(10,10), (20, 20), (30, 30), (45,45)}
     */
    public void testSDOWithinDistanceArbitraryLine() throws Exception {
        String sql = 
            "select GID, GEOMETRY from SIMPLE_SPATIAL where " + "mdsys.sdo_within_distance(geometry, " + 
            "mdsys.sdo_geometry(2,null,null, " + 
            "mdsys.sdo_elem_info_array(1,2,1), " + 
            "mdsys.sdo_ordinate_array(10,10, 20,20, 30,30, 45,45)), " + 
            "'DISTANCE=10') = 'TRUE' ORDER BY GID";

        SQLReader reader = new SQLReader(session, sql);

        JGeometry line = 
            JGeometry.createLinearLineString(new double[] { 10, 10, 20, 20, 30, 
                                                            30, 45, 45 }, 2, 
                                             0);

        ReadAllQuery raq = new ReadAllQuery(SimpleSpatial.class);
        ExpressionBuilder eb = raq.getExpressionBuilder();

        Expression selectionCriteria = SpatialExpressionFactory.withinDistance(eb.get("geometry"), line, new SpatialParameters("DISTANCE=10"));
        raq.setSelectionCriteria(selectionCriteria); 
        raq.addAscendingOrdering("id");

        List<Spatial> results = 
            (List<Spatial>)session.executeQuery(raq);

        String compareResult = reader.compare(results);

        assertNull(compareResult, compareResult);
    }

    /**
     * SDO_WITHIN_DISTANCE with NULL params matching a known circle geometry (1004)
     */
    public void testSDOWithinDistanceNullParamsMatchingCircle1004() throws Exception {
        String sql = 
            "select GID, GEOMETRY from SIMPLE_SPATIAL where " + "mdsys.sdo_within_distance(geometry, mdsys.sdo_geometry(3, " + 
            "NULL, null, mdsys.sdo_elem_info_array(1,3,4), " + 
            "mdsys.sdo_ordinate_array(1, 0, 0, 1, 0, -1)), " + 
            "NULL) = 'TRUE' ORDER BY GID";

        SQLReader reader = new SQLReader(session, sql);

        JGeometry circle = JGeometry.createCircle(1, 0, 0, 1, 0, -1, 0);

        ReadAllQuery raq = new ReadAllQuery(SimpleSpatial.class);
        ExpressionBuilder eb = raq.getExpressionBuilder();

        Expression selectionCriteria = SpatialExpressionFactory.withinDistance(eb.get("geometry"), circle, new SpatialParameters(""));
        raq.setSelectionCriteria(selectionCriteria); 
        raq.addAscendingOrdering("id");

        List<Spatial> results = 
            (List<Spatial>)session.executeQuery(raq);

        String compareResult = reader.compare(results);

        assertNull(compareResult, compareResult);
    }

    /**
     * SDO_WITHIN_DISTANCE with NULL params not matching existing
     */
    public void testSDOWithinDistanceNullParamsNotMatching() throws Exception {
        String sql = 
            "select GID, GEOMETRY from SIMPLE_SPATIAL where " + "mdsys.sdo_within_distance(geometry, mdsys.sdo_geometry(3, " + 
            "NULL, null, mdsys.sdo_elem_info_array(1,3,4), " + 
            "mdsys.sdo_ordinate_array(10, 0, 0, 10, 0, -10)), " + 
            "NULL) = 'TRUE' ORDER BY GID";

        SQLReader reader = new SQLReader(session, sql);

        JGeometry circle = JGeometry.createCircle(10, 0, 0, 10, 0, -10, 0);

        ReadAllQuery raq = new ReadAllQuery(SimpleSpatial.class);
        ExpressionBuilder eb = raq.getExpressionBuilder();

        Expression selectionCriteria = SpatialExpressionFactory.withinDistance(eb.get("geometry"), circle, new SpatialParameters(""));
        raq.setSelectionCriteria(selectionCriteria); 
        raq.addAscendingOrdering("id");

        List<Spatial> results = 
            (List<Spatial>)session.executeQuery(raq);

        String compareResult = reader.compare(results);

        assertNull(compareResult, compareResult);
    }
}
