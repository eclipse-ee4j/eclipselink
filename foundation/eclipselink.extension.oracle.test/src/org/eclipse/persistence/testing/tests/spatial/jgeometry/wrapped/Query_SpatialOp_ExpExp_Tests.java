/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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


import oracle.spatial.geometry.JGeometry;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.expressions.spatial.SpatialExpressionFactory;
import org.eclipse.persistence.expressions.spatial.SpatialParameters;
import org.eclipse.persistence.expressions.spatial.SpatialParameters.Mask;
import org.eclipse.persistence.expressions.spatial.SpatialParameters.QueryType;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestProblemException;
import org.eclipse.persistence.testing.models.spatial.jgeometry.wrapped.Spatial;
import org.eclipse.persistence.testing.models.spatial.jgeometry.wrapped.WrappedSpatial;
import org.eclipse.persistence.testing.tests.spatial.jgeometry.SQLReader;

/**
 * This test requires the following SQL be run prior to running the test suite:
 * CREATE OR REPLACE TYPE MY_GEOMETRY AS OBJECT (id NUMBER, geom MDSYS.SDO_GEOMETRY)
 *
 * SQL samples from C:\oracle\db\10.2\md\demo\examples\eginsert.sql
 * Note: Table re-named from TEST81 to SIMPLE_SPATIAL
 */
public class Query_SpatialOp_ExpExp_Tests extends WrappedSpatialTestCase {

    public Query_SpatialOp_ExpExp_Tests(String name){
        super(name);
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("Query_SpatialOp_ExpExp_Tests");
        suite.addTest(new Query_SpatialOp_ExpExp_Tests("testSDORelateRectangle"));
        suite.addTest(new Query_SpatialOp_ExpExp_Tests("testSDORelateCircle"));
        suite.addTest(new Query_SpatialOp_ExpExp_Tests("testSDORelateArbitraryLine"));
        suite.addTest(new Query_SpatialOp_ExpExp_Tests("testSDOFilterRectangle"));
        suite.addTest(new Query_SpatialOp_ExpExp_Tests("testSDOFilterRectangleNullParams"));
        suite.addTest(new Query_SpatialOp_ExpExp_Tests("testSDOFilterCircle"));
        suite.addTest(new Query_SpatialOp_ExpExp_Tests("testSDOFilterArbitraryLine"));
        suite.addTest(new Query_SpatialOp_ExpExp_Tests("testSDOWithinDistanceRectangle"));
        suite.addTest(new Query_SpatialOp_ExpExp_Tests("testSDOWithinDistanceRectangleUsingMaxResolution"));
        suite.addTest(new Query_SpatialOp_ExpExp_Tests("testSDOWithinDistanceCircle"));
        suite.addTest(new Query_SpatialOp_ExpExp_Tests("testSDOWithinDistanceArbitraryLine"));
        suite.addTest(new Query_SpatialOp_ExpExp_Tests("testSDOWithinDistanceNullParamsMatchingCircle1004"));
        suite.addTest(new Query_SpatialOp_ExpExp_Tests("testSDOWithinDistanceNullParamsNotMatching"));

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

    public void setUp() throws Exception {
        super.setUp();
        session.executeNonSelectingSQL("DELETE FROM WRAPPED_SPATIAL WS WHERE GID = 6666");
    }

    public void tearDown() throws Exception {
        session.executeNonSelectingSQL("DELETE FROM WRAPPED_SPATIAL WS WHERE GID = 6666");
        super.tearDown();
    }

    private void populateTestGeometry(JGeometry g) {
        UnitOfWork uow = session.acquireUnitOfWork();

        uow.registerNewObject(new WrappedSpatial(6666, 0, g));

        uow.commit();
    }

    /**
     * SDO_RELATE using a dynamic rectangular window with lower left
     * and upper right coordinates of {(1,1), (20,20)}
     */
    public void testSDORelateRectangle() throws Exception {

        String sql = 
            "select GID, GEOMETRY FROM WRAPPED_SPATIAL WS where mdsys.sdo_relate(ws.geometry.geom, " + 
            "mdsys.sdo_geometry(3,null,null, mdsys.sdo_elem_info_array(1,3,3),  " + 
            "mdsys.sdo_ordinate_array(1,1, 20, 20)), " + 
            "'MASK=ANYINTERACT QUERYTYPE=WINDOW') = 'TRUE' ORDER BY GID";

        SQLReader reader = new SQLReader(session, sql);


        populateTestGeometry(JGeometry.createLinearPolygon(new double[] { 1, 1, 
                                                                          1, 
                                                                          20, 
                                                                          10, 
                                                                          20, 
                                                                          20, 
                                                                          1, 1, 
                                                                          1 }, 
                                                           2, 0));

        ReadAllQuery raq = new ReadAllQuery(WrappedSpatial.class);
        ExpressionBuilder eb = raq.getExpressionBuilder();
//        Expression geom1 = SpatialOperator.getExpression("geometry.geom", eb);

        ExpressionBuilder eb2 = new ExpressionBuilder(WrappedSpatial.class);
/*        Expression geom2 = SpatialOperator.getExpression("geometry.geom", eb2);

        SpatialOperator relate = 
            SpatialOperator.relate().setMask(Mask.ANYINTERACT).setQueryType(QueryType.WINDOW);

        raq.setSelectionCriteria(relate.buildExpression(geom1, 
                                                        geom2).and(eb.get("id").notEqual(6666).and(eb2.get("id").equal(6666))));
*/
        SpatialParameters parameters = new SpatialParameters();
        parameters.setMask(Mask.ANYINTERACT).setQueryType(QueryType.WINDOW);
        Expression selectionCriteria = SpatialExpressionFactory.relate(eb.get("geometry").getField("geom"), eb2.get("geometry").getField("geom"), parameters);
        selectionCriteria = selectionCriteria.and(eb.get("id").notEqual(6666).and(eb2.get("id").equal(6666)));
        raq.setSelectionCriteria(selectionCriteria);
        raq.addAscendingOrdering("id");

        List<Spatial> results = (List<Spatial>)session.executeQuery(raq);

        String compareResult = reader.compare(results);

        assertNull(compareResult, compareResult);
    }

    /**
     * SDO_RELATE using a with a circle of radius 10 around (0,0)
     */
    public void testSDORelateCircle() throws Exception {
        String sql = 
            "select GID, GEOMETRY FROM WRAPPED_SPATIAL WS where mdsys.sdo_relate(ws.geometry.geom, " + 
            "mdsys.sdo_geometry(3,null,null, " + 
            "mdsys.sdo_elem_info_array(1,3,4), " + 
            "mdsys.sdo_ordinate_array(-10,0, 0, 10, 10, 0)), " + 
            "'MASK=ANYINTERACT QUERYTYPE=WINDOW') = 'TRUE' ORDER BY GID";

        SQLReader reader = new SQLReader(session, sql);

        populateTestGeometry(JGeometry.createCircle(-10, 0, 0, 10, 10, 0, 0));

        ReadAllQuery raq = new ReadAllQuery(WrappedSpatial.class);
        ExpressionBuilder eb = raq.getExpressionBuilder();
     //   Expression geom1 = SpatialOperator.getExpression("geometry.geom", eb);

        ExpressionBuilder eb2 = new ExpressionBuilder(WrappedSpatial.class);
  /*      Expression geom2 = SpatialOperator.getExpression("geometry.geom", eb2);

        SpatialOperator relate = 
            SpatialOperator.relate().setMask(Mask.ANYINTERACT).setQueryType(QueryType.WINDOW);

        raq.setSelectionCriteria(relate.buildExpression(geom1, 
                                                        geom2).and(eb.get("id").notEqual(6666).and(eb2.get("id").equal(6666))));
*/
        SpatialParameters parameters = new SpatialParameters();
        parameters.setMask(Mask.ANYINTERACT).setQueryType(QueryType.WINDOW);
        Expression selectionCriteria = SpatialExpressionFactory.relate(eb.get("geometry").getField("geom"), eb2.get("geometry").getField("geom"), parameters);
        selectionCriteria = selectionCriteria.and(eb.get("id").notEqual(6666).and(eb2.get("id").equal(6666)));
        raq.setSelectionCriteria(selectionCriteria);
        
        raq.addAscendingOrdering("id");

        List<Spatial> results = (List<Spatial>)session.executeQuery(raq);

        String compareResult = reader.compare(results);

        assertNull(compareResult, compareResult);
    }

    /**
     * SDO_RELATE using an arbitrary line string {(10,10), (20, 20), (30, 30), (45,45)}
     */
    public void testSDORelateArbitraryLine() throws Exception {
        String sql = 
            "select GID, GEOMETRY FROM WRAPPED_SPATIAL WS where mdsys.sdo_relate(" + 
            "ws.geometry.geom, mdsys.sdo_geometry(2,null,null, " + 
            "mdsys.sdo_elem_info_array(1,2,1), " + 
            "mdsys.sdo_ordinate_array(10,10, 20,20, 30,30, 45,45)), " + 
            "'MASK=ANYINTERACT QUERYTYPE=WINDOW') = 'TRUE' ORDER BY GID";

        SQLReader reader = new SQLReader(session, sql);

        populateTestGeometry(JGeometry.createLinearLineString(new double[] { 10, 
                                                                             10, 
                                                                             20, 
                                                                             20, 
                                                                             30, 
                                                                             30, 
                                                                             45, 
                                                                             45 }, 
                                                              2, 0));

        ReadAllQuery raq = new ReadAllQuery(WrappedSpatial.class);
        ExpressionBuilder eb = raq.getExpressionBuilder();
     //   Expression geom1 = SpatialOperator.getExpression("geometry.geom", eb);

        ExpressionBuilder eb2 = new ExpressionBuilder(WrappedSpatial.class);
    /*    Expression geom2 = SpatialOperator.getExpression("geometry.geom", eb2);

        SpatialOperator relate = 
            SpatialOperator.relate().setMask(Mask.ANYINTERACT).setQueryType(QueryType.WINDOW);

        raq.setSelectionCriteria(relate.buildExpression(geom1, 
                                                        geom2).and(eb.get("id").notEqual(6666).and(eb2.get("id").equal(6666))));
 */
        SpatialParameters parameters = new SpatialParameters();
        parameters.setMask(Mask.ANYINTERACT).setQueryType(QueryType.WINDOW);
        Expression selectionCriteria = SpatialExpressionFactory.relate(eb.get("geometry").getField("geom"), eb2.get("geometry").getField("geom"), parameters);
        selectionCriteria = selectionCriteria.and(eb.get("id").notEqual(6666).and(eb2.get("id").equal(6666)));
        raq.setSelectionCriteria(selectionCriteria);
        raq.addAscendingOrdering("id");

        List<Spatial> results = (List<Spatial>)session.executeQuery(raq);

        String compareResult = reader.compare(results);

        assertNull(compareResult, compareResult);
    }

    /**
     * SDO_Filter using a dynamic rectangular window with lower left
     * and upper right coordinates of {(1,1), (20,20)}
     */
    public void testSDOFilterRectangle() throws Exception {
        String sql = 
            "select GID, GEOMETRY FROM WRAPPED_SPATIAL WS where mdsys.sdo_filter(" + 
            "ws.geometry.geom, mdsys.sdo_geometry(3,null,null, " + 
            "mdsys.sdo_elem_info_array(1,3,3), " + 
            "mdsys.sdo_ordinate_array(1,1, 20, 20)), " + 
            "'QUERYTYPE=WINDOW') = 'TRUE' ORDER BY GID";

        SQLReader reader = new SQLReader(session, sql);

        populateTestGeometry(JGeometry.createLinearPolygon(new double[] { 1, 1, 
                                                                          1, 
                                                                          20, 
                                                                          10, 
                                                                          20, 
                                                                          20, 
                                                                          1, 1, 
                                                                          1 }, 
                                                           2, 0));

        ReadAllQuery raq = new ReadAllQuery(WrappedSpatial.class);
        ExpressionBuilder eb = raq.getExpressionBuilder();
   //     Expression geom1 = SpatialOperator.getExpression("geometry.geom", eb);

        ExpressionBuilder eb2 = new ExpressionBuilder(WrappedSpatial.class);
 /*       Expression geom2 = SpatialOperator.getExpression("geometry.geom", eb2);

        SpatialOperator filter = 
            SpatialOperator.filter().setQueryType(QueryType.WINDOW);

        raq.setSelectionCriteria(filter.buildExpression(geom1, 
                                                        geom2).and(eb.get("id").notEqual(6666).and(eb2.get("id").equal(6666))));
*/
        SpatialParameters parameters = new SpatialParameters();
        parameters.setQueryType(QueryType.WINDOW);
        Expression selectionCriteria = SpatialExpressionFactory.filter(eb.get("geometry").getField("geom"), eb2.get("geometry").getField("geom"), parameters);
        selectionCriteria = selectionCriteria.and(eb.get("id").notEqual(6666).and(eb2.get("id").equal(6666)));
        raq.setSelectionCriteria(selectionCriteria);
        
        raq.addAscendingOrdering("id");

        List<Spatial> results = (List<Spatial>)session.executeQuery(raq);

        String compareResult = reader.compare(results);

        assertNull(compareResult, compareResult);
    }

    public void testSDOFilterRectangleNullParams() throws Exception {
        String sql = 
            "select GID, GEOMETRY FROM WRAPPED_SPATIAL WS where mdsys.sdo_filter(" + 
            "ws.geometry.geom, mdsys.sdo_geometry(3,null,null, " + 
            "mdsys.sdo_elem_info_array(1,3,3), " + 
            "mdsys.sdo_ordinate_array(1,1, 20, 20)), " + 
            "NULL) = 'TRUE' ORDER BY GID";

        SQLReader reader = new SQLReader(session, sql);

        populateTestGeometry(JGeometry.createLinearPolygon(new double[] { 1, 1, 
                                                                          1, 
                                                                          20, 
                                                                          10, 
                                                                          20, 
                                                                          20, 
                                                                          1, 1, 
                                                                          1 }, 
                                                           2, 0));

        ReadAllQuery raq = new ReadAllQuery(WrappedSpatial.class);
        ExpressionBuilder eb = raq.getExpressionBuilder();
     //   Expression geom1 = SpatialOperator.getExpression("geometry.geom", eb);

        ExpressionBuilder eb2 = new ExpressionBuilder(WrappedSpatial.class);
   /*     Expression geom2 = SpatialOperator.getExpression("geometry.geom", eb2);

        SpatialOperator filter = SpatialOperator.filter();

        raq.setSelectionCriteria(filter.buildExpression(geom1, 
                                                        geom2).and(eb.get("id").notEqual(6666).and(eb2.get("id").equal(6666))));
*/
        
        Expression selectionCriteria = SpatialExpressionFactory.filter(eb.get("geometry").getField("geom"), eb2.get("geometry").getField("geom"), new SpatialParameters(""));
        selectionCriteria = selectionCriteria.and(eb.get("id").notEqual(6666).and(eb2.get("id").equal(6666)));
        raq.setSelectionCriteria(selectionCriteria);

        raq.addAscendingOrdering("id");

        List<Spatial> results = (List<Spatial>)session.executeQuery(raq);

        String compareResult = reader.compare(results);

        assertNull(compareResult, compareResult);
    }

    /**
     * SDO_FILTER using a with a circle of radius 10 around (0,0)
     */
    public void testSDOFilterCircle() throws Exception {
        String sql = 
            "select GID, GEOMETRY FROM WRAPPED_SPATIAL WS where mdsys.sdo_filter(" + 
            "ws.geometry.geom, mdsys.sdo_geometry(3,null,null, " + 
            "mdsys.sdo_elem_info_array(1,3,4), " + 
            "mdsys.sdo_ordinate_array(-10,0, 0, 10, 10, 0)), " + 
            "'QUERYTYPE=WINDOW') = 'TRUE' ORDER BY GID";

        SQLReader reader = new SQLReader(session, sql);

        populateTestGeometry(JGeometry.createCircle(-10, 0, 0, 10, 10, 0, 0));

        ReadAllQuery raq = new ReadAllQuery(WrappedSpatial.class);
        ExpressionBuilder eb = raq.getExpressionBuilder();
     //   Expression geom1 = SpatialOperator.getExpression("geometry.geom", eb);

        ExpressionBuilder eb2 = new ExpressionBuilder(WrappedSpatial.class);
    /*    Expression geom2 = SpatialOperator.getExpression("geometry.geom", eb2);

        SpatialOperator filter = 
            SpatialOperator.filter().setQueryType(QueryType.WINDOW);

        raq.setSelectionCriteria(filter.buildExpression(geom1, 
                                                        geom2).and(eb.get("id").notEqual(6666).and(eb2.get("id").equal(6666))));
 */
        
        SpatialParameters parameters = new SpatialParameters();
        parameters.setQueryType(QueryType.WINDOW);
        Expression selectionCriteria = SpatialExpressionFactory.filter(eb.get("geometry").getField("geom"), eb2.get("geometry").getField("geom"), parameters);
        selectionCriteria = selectionCriteria.and(eb.get("id").notEqual(6666).and(eb2.get("id").equal(6666)));
        raq.setSelectionCriteria(selectionCriteria);

        raq.addAscendingOrdering("id");

        List<Spatial> results = (List<Spatial>)session.executeQuery(raq);

        String compareResult = reader.compare(results);

        assertNull(compareResult, compareResult);
    }

    /**
     * SDO_FILTER using an arbitrary line string {(10,10), (20, 20), (30, 30), (45,45)}
     */
    public void testSDOFilterArbitraryLine() throws Exception {
        String sql = 
            "select GID, GEOMETRY FROM WRAPPED_SPATIAL WS where mdsys.sdo_filter(" + 
            "ws.geometry.geom, mdsys.sdo_geometry(2,null,null, " + 
            "mdsys.sdo_elem_info_array(1,2,1), " + 
            "mdsys.sdo_ordinate_array(10,10, 20,20, 30,30, 45,45)), " + 
            "'QUERYTYPE=WINDOW') = 'TRUE' ORDER BY GID";

        SQLReader reader = new SQLReader(session, sql);

        populateTestGeometry(JGeometry.createLinearLineString(new double[] { 10, 
                                                                             10, 
                                                                             20, 
                                                                             20, 
                                                                             30, 
                                                                             30, 
                                                                             45, 
                                                                             45 }, 
                                                              2, 0));

        ReadAllQuery raq = new ReadAllQuery(WrappedSpatial.class);
        ExpressionBuilder eb = raq.getExpressionBuilder();
 //       Expression geom1 = SpatialOperator.getExpression("geometry.geom", eb);

        ExpressionBuilder eb2 = new ExpressionBuilder(WrappedSpatial.class);
   /*     Expression geom2 = SpatialOperator.getExpression("geometry.geom", eb2);

        SpatialOperator filter = 
            SpatialOperator.filter().setQueryType(QueryType.WINDOW);

        raq.setSelectionCriteria(filter.buildExpression(geom1, 
                                                        geom2).and(eb.get("id").notEqual(6666).and(eb2.get("id").equal(6666))));
 */
        SpatialParameters parameters = new SpatialParameters();
        parameters.setQueryType(QueryType.WINDOW);
        Expression selectionCriteria = SpatialExpressionFactory.filter(eb.get("geometry").getField("geom"), eb2.get("geometry").getField("geom"), parameters);
        selectionCriteria = selectionCriteria.and(eb.get("id").notEqual(6666).and(eb2.get("id").equal(6666)));
        raq.setSelectionCriteria(selectionCriteria);
        
        raq.addAscendingOrdering("id");

        List<Spatial> results = (List<Spatial>)session.executeQuery(raq);

        String compareResult = reader.compare(results);

        assertNull(compareResult, compareResult);
    }

    /**
     * SDO_WITHIN_DISTANCE using a dynamic rectangular window with lower left
     * and upper right coordinates of {(1,1), (20,20)}
     */
    public void testSDOWithinDistanceRectangle() throws Exception {
        String sql = 
            "select GID, GEOMETRY FROM WRAPPED_SPATIAL WS where mdsys.sdo_within_distance(" + 
            "ws.geometry.geom, mdsys.sdo_geometry(3,null,null, mdsys.sdo_elem_info_array(1,3,3), " + 
            "mdsys.sdo_ordinate_array(1,1, 20, 20)), " + 
            "'DISTANCE=10') = 'TRUE' ORDER BY GID";

        SQLReader reader = new SQLReader(session, sql);

        populateTestGeometry(JGeometry.createLinearPolygon(new double[] { 1, 1, 
                                                                          1, 
                                                                          20, 
                                                                          10, 
                                                                          20, 
                                                                          20, 
                                                                          1, 1, 
                                                                          1 }, 
                                                           2, 0));

        ReadAllQuery raq = new ReadAllQuery(WrappedSpatial.class);
        ExpressionBuilder eb = raq.getExpressionBuilder();
    //    Expression geom1 = SpatialOperator.getExpression("geometry.geom", eb);

        ExpressionBuilder eb2 = new ExpressionBuilder(WrappedSpatial.class);
   /*     Expression geom2 = SpatialOperator.getExpression("geometry.geom", eb2);

        SpatialOperator wd = SpatialOperator.withinDistance().setDistance(10d);

        raq.setSelectionCriteria(wd.buildExpression(geom1, 
                                                    geom2).and(eb.get("id").notEqual(6666).and(eb2.get("id").equal(6666))));
     */
        SpatialParameters parameters = new SpatialParameters();
     parameters.setDistance(10d);
     Expression selectionCriteria = SpatialExpressionFactory.withinDistance(eb.get("geometry").getField("geom"), eb2.get("geometry").getField("geom"), parameters);
     selectionCriteria = selectionCriteria.and(eb.get("id").notEqual(6666).and(eb2.get("id").equal(6666)));
     raq.setSelectionCriteria(selectionCriteria);
        raq.addAscendingOrdering("id");

        List<Spatial> results = (List<Spatial>)session.executeQuery(raq);

        String compareResult = reader.compare(results);

        assertNull(compareResult, compareResult);
    }

    public void testSDOWithinDistanceRectangleUsingMaxResolution() throws Exception {
        String sql = 
            "select GID, GEOMETRY FROM WRAPPED_SPATIAL WS where mdsys.sdo_within_distance(" + 
            "ws.geometry.geom, mdsys.sdo_geometry(3,null,null, mdsys.sdo_elem_info_array(1,3,3), " + 
            "mdsys.sdo_ordinate_array(1,1, 20, 20)), " + 
            "'DISTANCE=10 MAX_RESOLUTION=5') = 'TRUE' ORDER BY GID";

        SQLReader reader = new SQLReader(session, sql);

        populateTestGeometry(JGeometry.createLinearPolygon(new double[] { 1, 1, 
                                                                          1, 
                                                                          20, 
                                                                          10, 
                                                                          20, 
                                                                          20, 
                                                                          1, 1, 
                                                                          1 }, 
                                                           2, 0));

        ReadAllQuery raq = new ReadAllQuery(WrappedSpatial.class);
        ExpressionBuilder eb = raq.getExpressionBuilder();
 //       Expression geom1 = SpatialOperator.getExpression("geometry.geom", eb);

        ExpressionBuilder eb2 = new ExpressionBuilder(WrappedSpatial.class);
 /*       Expression geom2 = SpatialOperator.getExpression("geometry.geom", eb2);

        SpatialOperator wd = 
            SpatialOperator.withinDistance().setDistance(10d).setMaxResolution(5);

        raq.setSelectionCriteria(wd.buildExpression(geom1, 
                                                    geom2).and(eb.get("id").notEqual(6666).and(eb2.get("id").equal(6666))));
  */
        
        SpatialParameters parameters = new SpatialParameters();
        parameters.setDistance(10d).setMaxResolution(5);
        Expression selectionCriteria = SpatialExpressionFactory.withinDistance(eb.get("geometry").getField("geom"), eb2.get("geometry").getField("geom"), parameters);
        selectionCriteria = selectionCriteria.and(eb.get("id").notEqual(6666).and(eb2.get("id").equal(6666)));
        raq.setSelectionCriteria(selectionCriteria);
        
        raq.addAscendingOrdering("id");

        List<Spatial> results = (List<Spatial>)session.executeQuery(raq);

        String compareResult = reader.compare(results);

        assertNull(compareResult, compareResult);
    }

    /**
     * SDO_WITHIN_DISTANCE using a with a circle of radius 10 around (0,0)
     */
    public void testSDOWithinDistanceCircle() throws Exception {
        String sql = 
            "select GID, GEOMETRY FROM WRAPPED_SPATIAL WS where " + "mdsys.sdo_within_distance(ws.geometry.geom, " + 
            "mdsys.sdo_geometry(3,null,null, " + 
            "mdsys.sdo_elem_info_array(1,3,4), " + 
            "mdsys.sdo_ordinate_array(-10,0, 0, 10, 10, 0)), " + 
            "'DISTANCE=10') = 'TRUE' ORDER BY GID";

        SQLReader reader = new SQLReader(session, sql);

        populateTestGeometry(JGeometry.createCircle(-10, 0, 0, 10, 10, 0, 0));

        ReadAllQuery raq = new ReadAllQuery(WrappedSpatial.class);
        ExpressionBuilder eb = raq.getExpressionBuilder();
      //  Expression geom1 = SpatialOperator.getExpression("geometry.geom", eb);

        ExpressionBuilder eb2 = new ExpressionBuilder(WrappedSpatial.class);
      /*  Expression geom2 = SpatialOperator.getExpression("geometry.geom", eb2);

        SpatialOperator wd = SpatialOperator.withinDistance().setDistance(10d);

        raq.setSelectionCriteria(wd.buildExpression(geom1, 
                                                    geom2).and(eb.get("id").notEqual(6666).and(eb2.get("id").equal(6666))));
        */
        
        SpatialParameters parameters = new SpatialParameters();
        parameters.setDistance(10d);
        Expression selectionCriteria = SpatialExpressionFactory.withinDistance(eb.get("geometry").getField("geom"), eb2.get("geometry").getField("geom"), parameters);
        selectionCriteria = selectionCriteria.and(eb.get("id").notEqual(6666).and(eb2.get("id").equal(6666)));
        raq.setSelectionCriteria(selectionCriteria);
        
        raq.addAscendingOrdering("id");

        List<Spatial> results = (List<Spatial>)session.executeQuery(raq);

        String compareResult = reader.compare(results);

        assertNull(compareResult, compareResult);
    }

    /**
     * SDO_WITHIN_DISTANCE using an arbitrary line string {(10,10), (20, 20), (30, 30), (45,45)}
     */
    public void testSDOWithinDistanceArbitraryLine() throws Exception {
        String sql = 
            "select GID, GEOMETRY FROM WRAPPED_SPATIAL WS where " + "mdsys.sdo_within_distance(ws.geometry.geom, " + 
            "mdsys.sdo_geometry(2,null,null, " + 
            "mdsys.sdo_elem_info_array(1,2,1), " + 
            "mdsys.sdo_ordinate_array(10,10, 20,20, 30,30, 45,45)), " + 
            "'DISTANCE=10') = 'TRUE' ORDER BY GID";

        SQLReader reader = new SQLReader(session, sql);

        populateTestGeometry(JGeometry.createLinearLineString(new double[] { 10, 
                                                                             10, 
                                                                             20, 
                                                                             20, 
                                                                             30, 
                                                                             30, 
                                                                             45, 
                                                                             45 }, 
                                                              2, 0));

        ReadAllQuery raq = new ReadAllQuery(WrappedSpatial.class);
        ExpressionBuilder eb = raq.getExpressionBuilder();
    //    Expression geom1 = SpatialOperator.getExpression("geometry.geom", eb);

        ExpressionBuilder eb2 = new ExpressionBuilder(WrappedSpatial.class);
      /*  Expression geom2 = SpatialOperator.getExpression("geometry.geom", eb2);

        SpatialOperator wd = SpatialOperator.withinDistance().setDistance(10d);

        raq.setSelectionCriteria(wd.buildExpression(geom1, 
                                                    geom2).and(eb.get("id").notEqual(6666).and(eb2.get("id").equal(6666))));

*/
        
        SpatialParameters parameters = new SpatialParameters();
        parameters.setDistance(10d);
        Expression selectionCriteria = SpatialExpressionFactory.withinDistance(eb.get("geometry").getField("geom"), eb2.get("geometry").getField("geom"), parameters);
        selectionCriteria = selectionCriteria.and(eb.get("id").notEqual(6666).and(eb2.get("id").equal(6666)));
        raq.setSelectionCriteria(selectionCriteria);
        raq.addAscendingOrdering("id");

        List<Spatial> results = (List<Spatial>)session.executeQuery(raq);

        String compareResult = reader.compare(results);

        assertNull(compareResult, compareResult);
    }

    /**
     * SDO_WITHIN_DISTANCE with NULL params matching a known circle geometry (1004)
     */
    public void testSDOWithinDistanceNullParamsMatchingCircle1004() throws Exception {
        String sql = 
            "select GID, GEOMETRY FROM WRAPPED_SPATIAL WS where " + "mdsys.sdo_within_distance(ws.geometry.geom, mdsys.sdo_geometry(3, " + 
            "NULL, null, mdsys.sdo_elem_info_array(1,3,4), " + 
            "mdsys.sdo_ordinate_array(1, 0, 0, 1, 0, -1)), " + 
            "NULL) = 'TRUE' ORDER BY GID";

        SQLReader reader = new SQLReader(session, sql);

        populateTestGeometry(JGeometry.createCircle(1, 0, 0, 1, 0, -1, 0));

        ReadAllQuery raq = new ReadAllQuery(WrappedSpatial.class);
        ExpressionBuilder eb = raq.getExpressionBuilder();
        //Expression geom1 = SpatialOperator.getExpression("geometry.geom", eb);

        ExpressionBuilder eb2 = new ExpressionBuilder(WrappedSpatial.class);
       /* Expression geom2 = SpatialOperator.getExpression("geometry.geom", eb2);

        SpatialOperator wd = SpatialOperator.withinDistance();

        raq.setSelectionCriteria(wd.buildExpression(geom1, 
                                                    geom2).and(eb.get("id").notEqual(6666).and(eb2.get("id").equal(6666))));
 */
        
        Expression selectionCriteria = SpatialExpressionFactory.withinDistance(eb.get("geometry").getField("geom"), eb2.get("geometry").getField("geom"), new SpatialParameters(""));
        selectionCriteria = selectionCriteria.and(eb.get("id").notEqual(6666).and(eb2.get("id").equal(6666)));
        raq.setSelectionCriteria(selectionCriteria);
        
        raq.addAscendingOrdering("id");

        List<Spatial> results = (List<Spatial>)session.executeQuery(raq);

        String compareResult = reader.compare(results);

        assertNull(compareResult, compareResult);
    }

    /**
     * SDO_WITHIN_DISTANCE with NULL params not matching existing
     */
    public void testSDOWithinDistanceNullParamsNotMatching() throws Exception {
        String sql = 
            "select GID, GEOMETRY FROM WRAPPED_SPATIAL WS where " + "mdsys.sdo_within_distance(ws.geometry.geom, mdsys.sdo_geometry(3, " + 
            "NULL, null, mdsys.sdo_elem_info_array(1,3,4), " + 
            "mdsys.sdo_ordinate_array(10, 0, 0, 10, 0, -10)), " + 
            "NULL) = 'TRUE' ORDER BY GID";

        SQLReader reader = new SQLReader(session, sql);

        populateTestGeometry(JGeometry.createCircle(10, 0, 0, 10, 0, -10, 0));

        ReadAllQuery raq = new ReadAllQuery(WrappedSpatial.class);
        ExpressionBuilder eb = raq.getExpressionBuilder();
     //   Expression geom1 = SpatialOperator.getExpression("geometry.geom", eb);

        ExpressionBuilder eb2 = new ExpressionBuilder(WrappedSpatial.class);
    /*    Expression geom2 = SpatialOperator.getExpression("geometry.geom", eb2);

        SpatialOperator wd = SpatialOperator.withinDistance();

        raq.setSelectionCriteria(wd.buildExpression(geom1, 
                                                    geom2).and(eb.get("id").notEqual(6666).and(eb2.get("id").equal(6666))));
*/
        
        Expression selectionCriteria = SpatialExpressionFactory.withinDistance(eb.get("geometry").getField("geom"), eb2.get("geometry").getField("geom"), new SpatialParameters(""));
        selectionCriteria = selectionCriteria.and(eb.get("id").notEqual(6666).and(eb2.get("id").equal(6666)));
        raq.setSelectionCriteria(selectionCriteria);
        
        raq.addAscendingOrdering("id");

        List<Spatial> results = (List<Spatial>)session.executeQuery(raq);

        String compareResult = reader.compare(results);

        assertNull(compareResult, compareResult);
    }
}
