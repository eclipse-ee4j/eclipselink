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
import org.eclipse.persistence.expressions.spatial.SpatialParameters.Mask;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.testing.framework.TestProblemException;
import org.eclipse.persistence.testing.models.spatial.jgeometry.SimpleSpatial;
import org.eclipse.persistence.testing.models.spatial.jgeometry.wrapped.Spatial;


/**
 * Query tests that do not involve using a spatial operator in the selection
 * criteria.
 */
public class NamedQueryTests extends SimpleSpatialTestCase {
    
    private static final String SESSION_QUERY_NAME = "simple-jgeometry-session-query";
    private static final String DESCRIPTOR_QUERY_NAME = "simple-jgeometry-descriptor-query";
    
    public NamedQueryTests(String name){
        super(name);
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("NamedQueryTests");
        suite.addTest(new NamedQueryTests("testNamedSessionQueryNoBinding"));
        suite.addTest(new NamedQueryTests("testNamedSessionQueryBindAllParameters"));
        suite.addTest(new NamedQueryTests("testNamedSessionQueryBindQueryParameters"));
        suite.addTest(new NamedQueryTests("testDescriptorNamedQueryNoBinding"));
        suite.addTest(new NamedQueryTests("testDescriptorNamedQueryBindAllParameter"));
        suite.addTest(new NamedQueryTests("testDescriptorNamedQueryBindQueryParameter"));

        return new TestSetup(suite) {
            protected void setUp(){
                try{
                    SimpleSpatialTestCase.repopulate(getSession(), true);
                    ReadAllQuery raq = new ReadAllQuery(SimpleSpatial.class);
                    ExpressionBuilder eb = raq.getExpressionBuilder();

                    SpatialParameters parameters = new SpatialParameters();
                    parameters.setQueryType(SpatialParameters.QueryType.WINDOW).setMask(Mask.ANYINTERACT);
                    Expression selectionCriteria = SpatialExpressionFactory.relate(eb.get("geometry"), eb.getParameter("GEOMETRY"), parameters);
                    raq.setSelectionCriteria(selectionCriteria);

                    raq.addAscendingOrdering("id");

                    raq.addArgument("GEOMETRY", JGeometry.class);
                    getSession().addQuery(SESSION_QUERY_NAME, raq);


                    raq = new ReadAllQuery(SimpleSpatial.class);
                    eb = raq.getExpressionBuilder();

                    parameters = new SpatialParameters();
                    parameters.setDistance(10d);
                    selectionCriteria = SpatialExpressionFactory.withinDistance(eb.get("geometry"), eb.getParameter("GEOMETRY"), parameters);
                    raq.setSelectionCriteria(selectionCriteria);
                    raq.addAscendingOrdering("id");
                    raq.addArgument("GEOMETRY", JGeometry.class);

                    getSession().getClassDescriptor(SimpleSpatial.class).getDescriptorQueryManager().addQuery(DESCRIPTOR_QUERY_NAME, raq);
                } catch (Exception e){
                    throw new TestProblemException("Could not setup JGeometry test model", e);
                }
            }

            protected void tearDown() {
                try{
                    getSession().removeQuery(SESSION_QUERY_NAME);
                    getSession().getClassDescriptor(SimpleSpatial.class).getDescriptorQueryManager().removeQuery(DESCRIPTOR_QUERY_NAME);
                }catch (Exception e){
                    throw new TestProblemException("Could not tearDown NamedQueryTests for SimpleSpatialTestCase.", e);
                }
            }
        };
    }

    public void executeNamedSessionQuery() throws Exception {
        String sql = 
            "select GID, GEOMETRY from SIMPLE_SPATIAL where mdsys.sdo_relate(geometry, " + 
            "mdsys.sdo_geometry(3,null,null, mdsys.sdo_elem_info_array(1,3,3),  " + 
            "mdsys.sdo_ordinate_array(1,1, 20, 20)), " + 
            "'MASK=ANYINTERACT QUERYTYPE=WINDOW') = 'TRUE' ORDER BY GID";
        SQLReader reader = new SQLReader(session, sql);
        double[] points = new double[] { 1, 1, 1, 20, 10, 20, 20, 1, 1, 1 };
        JGeometry rectangle = JGeometry.createLinearPolygon(points, 2, 0);

        List<Spatial> results = 
            (List<Spatial>)session.executeQuery(SESSION_QUERY_NAME, rectangle);

        String compareResult = reader.compare(results);
        assertNull(compareResult, compareResult);
    }

    public void testNamedSessionQueryNoBinding() throws Exception {
        getSession().getLogin().getPlatform().setShouldBindAllParameters(false);

        executeNamedSessionQuery();
    }

    public void testNamedSessionQueryBindAllParameters() throws Exception {
        getSession().getLogin().getPlatform().setShouldBindAllParameters(true);

        executeNamedSessionQuery();
    }

    public void testNamedSessionQueryBindQueryParameters() throws Exception {
        getSession().getLogin().getPlatform().setShouldBindAllParameters(false);
        getSession().getQuery(SESSION_QUERY_NAME).bindAllParameters();

        executeNamedSessionQuery();
    }

    public void executeDescriptorNamedQuery() throws Exception {
        String sql = 
            "select GID, GEOMETRY from SIMPLE_SPATIAL where " + "mdsys.sdo_within_distance(geometry, " + 
            "mdsys.sdo_geometry(3,null,null, " + 
            "mdsys.sdo_elem_info_array(1,3,4), " + 
            "mdsys.sdo_ordinate_array(-10,0, 0, 10, 10, 0)), " + 
            "'DISTANCE=10') = 'TRUE' ORDER BY GID";

        SQLReader reader = new SQLReader(session, sql);

        JGeometry circle = JGeometry.createCircle(-10, 0, 0, 10, 10, 0, 0);

        List<Spatial> results = 
            (List<Spatial>)session.executeQuery(DESCRIPTOR_QUERY_NAME, 
                                       SimpleSpatial.class, circle);

        String compareResult = reader.compare(results);

        assertNull(compareResult, compareResult);
    }

    public void testDescriptorNamedQueryNoBinding() throws Exception {
        getSession().getLogin().getPlatform().setShouldBindAllParameters(false);
        
        executeDescriptorNamedQuery();
    }

    public void testDescriptorNamedQueryBindAllParameter() throws Exception {
        getSession().getLogin().getPlatform().setShouldBindAllParameters(true);
        
        executeDescriptorNamedQuery();
    }

    public void testDescriptorNamedQueryBindQueryParameter() throws Exception {
        getSession().getLogin().getPlatform().setShouldBindAllParameters(false);
        getSession().getClassDescriptor(SimpleSpatial.class).getDescriptorQueryManager().getQuery(DESCRIPTOR_QUERY_NAME).bindAllParameters();
        
        executeDescriptorNamedQuery();
    }
}
