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
import org.eclipse.persistence.expressions.spatial.SpatialParameters;
import org.eclipse.persistence.expressions.spatial.SpatialParameters.Mask;
import org.eclipse.persistence.expressions.spatial.SpatialExpressionFactory;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.testing.framework.TestProblemException;
import org.eclipse.persistence.testing.models.spatial.jgeometry.SimpleSpatial;
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
public class Query_Basic_Tests extends WrappedSpatialTestCase {
    
    public Query_Basic_Tests(String name){
        super(name);
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("Query_Basic_Tests");
        suite.addTest(new Query_Basic_Tests("testReadAll"));
        suite.addTest(new Query_Basic_Tests("testSDORelateRectangle"));
        suite.addTest(new Query_Basic_Tests("testSDORelateRectangleAndIDGreaterThan2"));
        suite.addTest(new Query_Basic_Tests("testReadNotNullGeometry"));
        suite.addTest(new Query_Basic_Tests("testMyGeometryGeomIsNull"));
        suite.addTest(new Query_Basic_Tests("testGetTableGetField"));

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
        String sql = "select GID, GEOMETRY from WRAPPED_SPATIAL ORDER BY GID";
        SQLReader reader = new SQLReader(session, sql);

        ReadAllQuery raq = new ReadAllQuery(WrappedSpatial.class);
        raq.addAscendingOrdering("id");

        List<Spatial> results = 
            (List<Spatial>)session.executeQuery(raq);
        assertNotNull(results);

        String compareResult = reader.compare(results);

        assertNull(compareResult, compareResult);
    }

    /**
     * SDO_RELATE using a dynamic rectangular window with lower left
     * and upper right coordinates of {(1,1), (20,20)}
     */
    public void testSDORelateRectangle() throws Exception {
        double[] points = new double[] { 1, 1, 1, 20, 10, 20, 20, 1, 1, 1 };
        JGeometry rectangle = JGeometry.createLinearPolygon(points, 2, 0);

        ReadAllQuery raq = new ReadAllQuery(WrappedSpatial.class);
        ExpressionBuilder eb = raq.getExpressionBuilder();
       
        SpatialParameters parameters = new SpatialParameters();
        parameters.setQueryType(SpatialParameters.QueryType.WINDOW).setMask(Mask.ANYINTERACT);
        Expression selectionCriteria = SpatialExpressionFactory.relate(eb.get("geometry").getField("geom"), rectangle, parameters);
        raq.setSelectionCriteria(selectionCriteria);
        raq.addAscendingOrdering("id");

        List<SimpleSpatial> results = 
            (List<SimpleSpatial>)session.executeQuery(raq);

        assertNotNull(results);
    }

    /**
     * SDO_RELATE using a dynamic rectangular window with lower left
     * and upper right coordinates of {(1,1), (20,20)}
     */
    public void testSDORelateRectangleAndIDGreaterThan2() throws Exception {
        double[] points = new double[] { 1, 1, 1, 20, 10, 20, 20, 1, 1, 1 };
        JGeometry rectangle = JGeometry.createLinearPolygon(points, 2, 0);

        ReadAllQuery raq = new ReadAllQuery(WrappedSpatial.class);
        ExpressionBuilder eb = raq.getExpressionBuilder();
        
        SpatialParameters parameters = new SpatialParameters();
        parameters.setQueryType(SpatialParameters.QueryType.WINDOW).setMask(Mask.ANYINTERACT);
        Expression selectionCriteria = SpatialExpressionFactory.relate(eb.get("geometry").getField("geom"), rectangle, parameters);
        selectionCriteria = selectionCriteria.and(eb.get("geometry").getField("id").greaterThan(2));
        raq.setSelectionCriteria(selectionCriteria);
        raq.addAscendingOrdering("id");

        List<SimpleSpatial> results = 
            (List<SimpleSpatial>)session.executeQuery(raq);

        assertNotNull(results);
    }

    /**
     * Comparisons to geometry != NULL always fail.
     */
    public void testReadNotNullGeometry() throws Exception {
        ReadAllQuery roq = new ReadAllQuery(WrappedSpatial.class);
        ExpressionBuilder eb = roq.getExpressionBuilder();

        Expression selectionCriteria = eb.get("geometry").getField("geom").notNull();
        roq.setSelectionCriteria(selectionCriteria);
        
        List<WrappedSpatial> results = 
            (List<WrappedSpatial>)session.executeQuery(roq);

        assertNotNull(results);
        int countAll = DeleteTests.countWrappedSpatial(session);

        assertEquals("More then one found", countAll - 1, results.size());
    }

    public void testMyGeometryGeomIsNull() throws Exception {

        ReadAllQuery roq = new ReadAllQuery(WrappedSpatial.class);
        ExpressionBuilder eb = new ExpressionBuilder(WrappedSpatial.class);

        Expression geomExp = eb.get("geometry").getField("geom");
        
        roq.setSelectionCriteria(geomExp.isNull());

        List<WrappedSpatial> results = 
            (List<WrappedSpatial>)session.executeQuery(roq);

        assertNotNull(results);
        assertEquals(1, results.size());
    }
    
    public void testGetTableGetField(){
        ReadAllQuery roq = new ReadAllQuery(WrappedSpatial.class);
        ExpressionBuilder eb = roq.getExpressionBuilder();
        roq.setSelectionCriteria(eb.getTable("WRAPPED_SPATIAL").getField("GEOMETRY").getField("GEOM").notNull());

        List<SimpleSpatial> results = 
            (List<SimpleSpatial>)session.executeQuery(roq);

        assertNotNull(results);
        int countAll = DeleteTests.countWrappedSpatial(session);

        assertEquals("Incorrect number found", countAll - 1, results.size());
        
    }

}
