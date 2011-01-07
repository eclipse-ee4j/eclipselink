/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     03/08/2010 Andrei Ilitchev 
 *       Bug 300512 - Add FUNCTION support to extended JPQL
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.jpa.jgeometry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import junit.framework.Test;
import junit.framework.TestSuite;

import oracle.spatial.geometry.JGeometry;

import org.eclipse.persistence.config.QueryHints;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.structconverter.JGeometryTableCreator;
import org.eclipse.persistence.testing.models.jpa.structconverter.SimpleSpatial;
import org.eclipse.persistence.testing.models.spatial.jgeometry.wrapped.Spatial;

import org.eclipse.persistence.testing.framework.TestProblemException;

/**
 * Test validating custom jpql spatial functions.
 * It is conversion to jpql of Query_SpatialExpOp_ExpGeom_Tests. 
 *
 * These tests pass an expression for the first geometry and bin in a JGeometry
 * as a STRUCT for the second one.
 * SQL samples from C:\oracle\db\10.2\md\demo\examples\eginsert.sql
 * Note: Table re-named from TEST81 to JPA_JGEOMETRY
 * fields we renamed from {GID, GEOMETRY} to {ID, JGEOMETRY}
 */
public class SpatialJPQLTestSuite extends JUnitTestCase {

    public static final String STRUCT_CONVERTER_PU = "structConverter";
    
    public boolean supported;
    
    public SpatialJPQLTestSuite() {
        super();
    }

    public SpatialJPQLTestSuite(String name){
        super(name);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("SpatialJPQLTestSuite");
        suite.addTest(new SpatialJPQLTestSuite("testSetup"));
        suite.addTest(new SpatialJPQLTestSuite("testSDORelateRectangle"));
        suite.addTest(new SpatialJPQLTestSuite("testSDORelateRectangle2"));
        suite.addTest(new SpatialJPQLTestSuite("testSDORelateCircle"));
        suite.addTest(new SpatialJPQLTestSuite("testSDORelateArbitraryLine"));
        suite.addTest(new SpatialJPQLTestSuite("testSDOFilterRectangle"));
        suite.addTest(new SpatialJPQLTestSuite("testSDOFilterRectangleNullParams"));
        suite.addTest(new SpatialJPQLTestSuite("testSDOFilterCircle"));
        suite.addTest(new SpatialJPQLTestSuite("testSDOFilterArbitraryLine"));
        suite.addTest(new SpatialJPQLTestSuite("testSDOWithinDistanceRectangle"));
        suite.addTest(new SpatialJPQLTestSuite("testSDOWithinDistanceRectangleUsingMaxResolution"));
        suite.addTest(new SpatialJPQLTestSuite("testSDOWithinDistanceCircle"));
        suite.addTest(new SpatialJPQLTestSuite("testSDOWithinDistanceArbitraryLine"));
        suite.addTest(new SpatialJPQLTestSuite("testSDOWithinDistanceNullParamsMatchingCircle1004"));
        suite.addTest(new SpatialJPQLTestSuite("testSDOWithinDistanceNullParamsNotMatching"));
        suite.addTest(new SpatialJPQLTestSuite("testSdoBinding"));
        return suite;
    }
       
    public void testSetup() {
        supported = getServerSession(STRUCT_CONVERTER_PU).getPlatform().isOracle();
        if(!supported) {
            return;
        }
        clearCache(STRUCT_CONVERTER_PU);
        getServerSession(STRUCT_CONVERTER_PU).executeNonSelectingSQL("DELETE FROM USER_SDO_GEOM_METADATA WHERE TABLE_NAME = 'JPA_JGEOMETRY'");
        new JGeometryTableCreator().replaceTables(JUnitTestCase.getServerSession(STRUCT_CONVERTER_PU));
        getServerSession(STRUCT_CONVERTER_PU).executeNonSelectingSQL("INSERT INTO USER_SDO_GEOM_METADATA(TABLE_NAME, COLUMN_NAME, DIMINFO) VALUES('JPA_JGEOMETRY', 'JGEOMETRY'," +
                " mdsys.sdo_dim_array(mdsys.sdo_dim_element('X', -100, 100, 0.005), mdsys.sdo_dim_element('Y', -100, 100, 0.005)))");

        getServerSession(STRUCT_CONVERTER_PU).executeNonSelectingSQL("CREATE INDEX jpa_test_idx on JPA_JGEOMETRY(jgeometry) indextype is mdsys.spatial_index parameters ('mdsys.sdo_level=5 sdo_numtiles=6')");
        populate();
    }
    
    protected void populate() {
        SampleGeometries samples = new SampleGeometries();
        EntityManager em = createEntityManager(STRUCT_CONVERTER_PU);
        beginTransaction(em);
        try {
            List<SimpleSpatial> simpleSpatials = samples.simpleJpaPopulation();
            for(SimpleSpatial ss : simpleSpatials) {
                em.persist(ss);
            }
            commitTransaction(em);
            assertEquals(simpleSpatials.size(), ((Long)em.createQuery("SELECT COUNT(ss) FROM SimpleSpatial ss").getSingleResult()).intValue());
        } catch (Exception ex) {
            if(isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            throw new TestProblemException("Populate failed", ex);
        } finally {
            closeEntityManager(em);
            clearCache(STRUCT_CONVERTER_PU);
        }
    }
    
    /**
     * SDO_RELATE using a dynamic rectangular window with lower left
     * and upper right coordinates of {(1,1), (20,20)}
     */
    public void testSDORelateRectangle() throws Exception {
        String sql = 
            "select ID, JGEOMETRY from JPA_JGEOMETRY where mdsys.sdo_relate(jgeometry, " + 
            "mdsys.sdo_geometry(3,null,null, mdsys.sdo_elem_info_array(1,3,3),  " + 
            "mdsys.sdo_ordinate_array(1,1, 20, 20)), " + 
            "'MASK=ANYINTERACT QUERYTYPE=WINDOW') = 'TRUE' ORDER BY ID";

        SQLReader reader = new SQLReader(getServerSession(STRUCT_CONVERTER_PU), sql);

        JGeometry rectangle = 
            JGeometry.createLinearPolygon(new double[] { 1, 1, 1, 20, 10, 20, 
                                                         20, 1, 1, 1 }, 2, 0);

        EntityManager em = createEntityManager(STRUCT_CONVERTER_PU);
        Query query = em.createQuery("SELECT ss FROM SimpleSpatial ss WHERE FUNC('mdsys.sdo_RELATE', ss.JGeometry, :otherGeometry, :params) = 'TRUE' ORDER BY ss.id ASC");
        query.setParameter("otherGeometry", rectangle);
        query.setParameter("params", "MASK=ANYINTERACT QUERYTYPE=WINDOW");
        List<Spatial> results = query.getResultList(); 

        String compareResult = reader.compare(results);

        assertNull(compareResult, compareResult);
    }

    /**
     * SDO_RELATE using a dynamic rectangular window with lower left
     * and upper right coordinates of {(1,1), (20,20)}
     * Build jpql string with all mdsys.sdo_ functions found in the control query.
     */
    public void testSDORelateRectangle2() throws Exception {
        String sql = 
            "select ID, JGEOMETRY from JPA_JGEOMETRY where mdsys.sdo_relate(jgeometry, " + 
            "mdsys.sdo_geometry(3,null,null, mdsys.sdo_elem_info_array(1,3,3),  " + 
            "mdsys.sdo_ordinate_array(1,1, 20, 20)), " + 
            "'MASK=ANYINTERACT QUERYTYPE=WINDOW') = 'TRUE' ORDER BY ID";

        SQLReader reader = new SQLReader(getServerSession(STRUCT_CONVERTER_PU), sql);

        String otherGeometry = "FUNC('mdsys.sdo_geometry', 3, null, null, FUNC('mdsys.sdo_elem_info_array', 1, 3, 3), FUNC('mdsys.sdo_ordinate_array', 1, 1, 20, 20))";
        
        EntityManager em = createEntityManager(STRUCT_CONVERTER_PU);
        Query query = em.createQuery("SELECT ss FROM SimpleSpatial ss WHERE FUNC('mdsys.sdo_RELATE', ss.JGeometry, "+otherGeometry+", :params) = 'TRUE' ORDER BY ss.id ASC");
        query.setParameter("params", "MASK=ANYINTERACT QUERYTYPE=WINDOW");
        query.setHint(QueryHints.BIND_PARAMETERS, "false");
        List<Spatial> results = query.getResultList(); 

        String compareResult = reader.compare(results);

        assertNull(compareResult, compareResult);
    }

    /**
     * SDO_RELATE using a with a circle of radius 10 around (0,0)
     */
    public void testSDORelateCircle() throws Exception {
        String sql = 
            "select ID, JGEOMETRY from JPA_JGEOMETRY where mdsys.sdo_relate(jgeometry, " + 
            "mdsys.sdo_geometry(3,null,null, " + 
            "mdsys.sdo_elem_info_array(1,3,4), " + 
            "mdsys.sdo_ordinate_array(-10,0, 0, 10, 10, 0)), " + 
            "'MASK=ANYINTERACT QUERYTYPE=WINDOW') = 'TRUE' ORDER BY ID";

        SQLReader reader = new SQLReader(getServerSession(STRUCT_CONVERTER_PU), sql);

        JGeometry circle = JGeometry.createCircle(-10, 0, 0, 10, 10, 0, 0);

        EntityManager em = createEntityManager(STRUCT_CONVERTER_PU);
        Query query = em.createQuery("SELECT ss FROM SimpleSpatial ss WHERE FUNC('mdsys.sdo_RELATE', ss.JGeometry, :otherGeometry, :params) = 'TRUE' ORDER BY ss.id ASC");
        query.setParameter("otherGeometry", circle);
        query.setParameter("params", "MASK=ANYINTERACT QUERYTYPE=WINDOW");
        List<Spatial> results = query.getResultList(); 

        String compareResult = reader.compare(results);

        assertNull(compareResult, compareResult);
    }

    /**
     * SDO_RELATE using an arbitrary line string {(10,10), (20, 20), (30, 30), (45,45)}
     */
    public void testSDORelateArbitraryLine() throws Exception {
        String sql = 
            "select ID, JGEOMETRY from JPA_JGEOMETRY where mdsys.sdo_relate(" + 
            "jgeometry, mdsys.sdo_geometry(2,null,null, " + 
            "mdsys.sdo_elem_info_array(1,2,1), " + 
            "mdsys.sdo_ordinate_array(10,10, 20,20, 30,30, 45,45)), " + 
            "'MASK=ANYINTERACT QUERYTYPE=WINDOW') = 'TRUE' ORDER BY ID";

        SQLReader reader = new SQLReader(getServerSession(STRUCT_CONVERTER_PU), sql);

        JGeometry line = 
            JGeometry.createLinearLineString(new double[] { 10, 10, 20, 20, 30, 
                                                            30, 45, 45 }, 2, 
                                             0);

        EntityManager em = createEntityManager(STRUCT_CONVERTER_PU);
        Query query = em.createQuery("SELECT ss FROM SimpleSpatial ss WHERE FUNC('mdsys.sdo_RELATE', ss.JGeometry, :otherGeometry, :params) = 'TRUE' ORDER BY ss.id ASC");
        query.setParameter("otherGeometry", line);
        query.setParameter("params", "MASK=ANYINTERACT QUERYTYPE=WINDOW");
        List<Spatial> results = query.getResultList(); 

        String compareResult = reader.compare(results);

        assertNull(compareResult, compareResult);
    }

    /**
     * SDO_Filter using a dynamic rectangular window with lower left
     * and upper right coordinates of {(1,1), (20,20)}
     */
    public void testSDOFilterRectangle() throws Exception {
        String sql = 
            "select ID, JGEOMETRY from JPA_JGEOMETRY where mdsys.sdo_filter(" + 
            "jgeometry, mdsys.sdo_geometry(3,null,null, " + 
            "mdsys.sdo_elem_info_array(1,3,3), " + 
            "mdsys.sdo_ordinate_array(1,1, 20, 20)), " + 
            "'QUERYTYPE=WINDOW') = 'TRUE' ORDER BY ID";

        SQLReader reader = new SQLReader(getServerSession(STRUCT_CONVERTER_PU), sql);

        JGeometry rectangle = 
            JGeometry.createLinearPolygon(new double[] { 1, 1, 1, 20, 10, 20, 
                                                         20, 1, 1, 1 }, 2, 0);

        EntityManager em = createEntityManager(STRUCT_CONVERTER_PU);
        Query query = em.createQuery("SELECT ss FROM SimpleSpatial ss WHERE FUNC('mdsys.sdo_FILTER', ss.JGeometry, :otherGeometry, :params) = 'TRUE' ORDER BY ss.id ASC");
        query.setParameter("otherGeometry", rectangle);
        query.setParameter("params", "QUERYTYPE=WINDOW");
        List<Spatial> results = query.getResultList(); 

        String compareResult = reader.compare(results);

        assertNull(compareResult, compareResult);
    }

    public void testSDOFilterRectangleNullParams() throws Exception {
        String sql = 
            "select ID, JGEOMETRY from JPA_JGEOMETRY where mdsys.sdo_filter(" + 
            "jgeometry, mdsys.sdo_geometry(3,null,null, " + 
            "mdsys.sdo_elem_info_array(1,3,3), " + 
            "mdsys.sdo_ordinate_array(1,1, 20, 20)), " + 
            "NULL) = 'TRUE' ORDER BY ID";

        SQLReader reader = new SQLReader(getServerSession(STRUCT_CONVERTER_PU), sql);

        JGeometry rectangle = 
            JGeometry.createLinearPolygon(new double[] { 1, 1, 1, 20, 10, 20, 
                                                         20, 1, 1, 1 }, 2, 0);

        EntityManager em = createEntityManager(STRUCT_CONVERTER_PU);
        Query query = em.createQuery("SELECT ss FROM SimpleSpatial ss WHERE FUNC('mdsys.sdo_FILTER', ss.JGeometry, :otherGeometry, :params) = 'TRUE' ORDER BY ss.id ASC");
        query.setParameter("otherGeometry", rectangle);
        query.setParameter("params", null);
        List<Spatial> results = query.getResultList(); 

        String compareResult = reader.compare(results);

        assertNull(compareResult, compareResult);
    }

    /**
     * SDO_FILTER using a with a circle of radius 10 around (0,0)
     */
    public void testSDOFilterCircle() throws Exception {
        String sql = 
            "select ID, JGEOMETRY from JPA_JGEOMETRY where mdsys.sdo_filter(" + 
            "jgeometry, mdsys.sdo_geometry(3,null,null, " + 
            "mdsys.sdo_elem_info_array(1,3,4), " + 
            "mdsys.sdo_ordinate_array(-10,0, 0, 10, 10, 0)), " + 
            "'QUERYTYPE=WINDOW') = 'TRUE' ORDER BY ID";

        SQLReader reader = new SQLReader(getServerSession(STRUCT_CONVERTER_PU), sql);

        JGeometry circle = JGeometry.createCircle(-10, 0, 0, 10, 10, 0, 0);

        EntityManager em = createEntityManager(STRUCT_CONVERTER_PU);
        Query query = em.createQuery("SELECT ss FROM SimpleSpatial ss WHERE FUNC('mdsys.sdo_FILTER', ss.JGeometry, :otherGeometry, :params) = 'TRUE' ORDER BY ss.id ASC");
        query.setParameter("otherGeometry", circle);
        query.setParameter("params", "QUERYTYPE=WINDOW");
        List<Spatial> results = query.getResultList(); 

        String compareResult = reader.compare(results);

        assertNull(compareResult, compareResult);
    }

    /**
     * SDO_FILTER using an arbitrary line string {(10,10), (20, 20), (30, 30), (45,45)}
     */
    public void testSDOFilterArbitraryLine() throws Exception {
        String sql = 
            "select ID, JGEOMETRY from JPA_JGEOMETRY where mdsys.sdo_filter(" + 
            "jgeometry, mdsys.sdo_geometry(2,null,null, " + 
            "mdsys.sdo_elem_info_array(1,2,1), " + 
            "mdsys.sdo_ordinate_array(10,10, 20,20, 30,30, 45,45)), " + 
            "'QUERYTYPE=WINDOW') = 'TRUE' ORDER BY ID";

        SQLReader reader = new SQLReader(getServerSession(STRUCT_CONVERTER_PU), sql);

        JGeometry line = 
            JGeometry.createLinearLineString(new double[] { 10, 10, 20, 20, 30, 
                                                            30, 45, 45 }, 2, 
                                             0);

        EntityManager em = createEntityManager(STRUCT_CONVERTER_PU);
        Query query = em.createQuery("SELECT ss FROM SimpleSpatial ss WHERE FUNC('mdsys.sdo_FILTER', ss.JGeometry, :otherGeometry, :params) = 'TRUE' ORDER BY ss.id ASC");
        query.setParameter("otherGeometry", line);
        query.setParameter("params", "QUERYTYPE=WINDOW");
        List<Spatial> results = query.getResultList(); 

        String compareResult = reader.compare(results);

        assertNull(compareResult, compareResult);
    }

    /**
     * SDO_WITHIN_DISTANCE using a dynamic rectangular window with lower left
     * and upper right coordinates of {(1,1), (20,20)}
     */
    public void testSDOWithinDistanceRectangle() throws Exception {
        String sql = 
            "select ID, JGEOMETRY from JPA_JGEOMETRY where mdsys.sdo_within_distance(" + 
            "jgeometry, mdsys.sdo_geometry(3,null,null, mdsys.sdo_elem_info_array(1,3,3), " + 
            "mdsys.sdo_ordinate_array(1,1, 20, 20)), " + 
            "'DISTANCE=10') = 'TRUE' ORDER BY ID";

        SQLReader reader = new SQLReader(getServerSession(STRUCT_CONVERTER_PU), sql);

        JGeometry rectangle = 
            JGeometry.createLinearPolygon(new double[] { 1, 1, 1, 20, 10, 20, 
                                                         20, 1, 1, 1 }, 2, 0);

        EntityManager em = createEntityManager(STRUCT_CONVERTER_PU);
        Query query = em.createQuery("SELECT ss FROM SimpleSpatial ss WHERE FUNC('mdsys.sdo_WITHIN_DISTANCE', ss.JGeometry, :otherGeometry, :params) = 'TRUE' ORDER BY ss.id ASC");
        query.setParameter("otherGeometry", rectangle);
        query.setParameter("params", "DISTANCE=10");
        List<Spatial> results = query.getResultList(); 

        String compareResult = reader.compare(results);

        assertNull(compareResult, compareResult);
    }

    public void testSDOWithinDistanceRectangleUsingMaxResolution() throws Exception {
        String sql = 
            "select ID, JGEOMETRY from JPA_JGEOMETRY where mdsys.sdo_within_distance(" + 
            "jgeometry, mdsys.sdo_geometry(3,null,null, mdsys.sdo_elem_info_array(1,3,3), " + 
            "mdsys.sdo_ordinate_array(1,1, 20, 20)), " + 
            "'DISTANCE=10 MAX_RESOLUTION=5') = 'TRUE' ORDER BY ID";

        SQLReader reader = new SQLReader(getServerSession(STRUCT_CONVERTER_PU), sql);

        JGeometry rectangle = 
            JGeometry.createLinearPolygon(new double[] { 1, 1, 1, 20, 10, 20, 
                                                         20, 1, 1, 1 }, 2, 0);

        EntityManager em = createEntityManager(STRUCT_CONVERTER_PU);
        Query query = em.createQuery("SELECT ss FROM SimpleSpatial ss WHERE FUNC('mdsys.sdo_WITHIN_DISTANCE', ss.JGeometry, :otherGeometry, :params) = 'TRUE' ORDER BY ss.id ASC");
        query.setParameter("otherGeometry", rectangle);
        query.setParameter("params", "DISTANCE=10 MAX_RESOLUTION=5");
        List<Spatial> results = query.getResultList(); 

        String compareResult = reader.compare(results);

        assertNull(compareResult, compareResult);
    }

    /**
     * SDO_WITHIN_DISTANCE using a with a circle of radius 10 around (0,0)
     */
    public void testSDOWithinDistanceCircle() throws Exception {
        String sql = 
            "select ID, JGEOMETRY from JPA_JGEOMETRY where " + "mdsys.sdo_within_distance(jgeometry, " + 
            "mdsys.sdo_geometry(3,null,null, " + 
            "mdsys.sdo_elem_info_array(1,3,4), " + 
            "mdsys.sdo_ordinate_array(-10,0, 0, 10, 10, 0)), " + 
            "'DISTANCE=10') = 'TRUE' ORDER BY ID";

        SQLReader reader = new SQLReader(getServerSession(STRUCT_CONVERTER_PU), sql);

        JGeometry circle = JGeometry.createCircle(-10, 0, 0, 10, 10, 0, 0);

        EntityManager em = createEntityManager(STRUCT_CONVERTER_PU);
        Query query = em.createQuery("SELECT ss FROM SimpleSpatial ss WHERE FUNC('mdsys.sdo_WITHIN_DISTANCE', ss.JGeometry, :otherGeometry, :params) = 'TRUE' ORDER BY ss.id ASC");
        query.setParameter("otherGeometry", circle);
        query.setParameter("params", "DISTANCE=10");
        List<Spatial> results = query.getResultList(); 

        String compareResult = reader.compare(results);

        assertNull(compareResult, compareResult);
    }

    /**
     * SDO_WITHIN_DISTANCE using an arbitrary line string {(10,10), (20, 20), (30, 30), (45,45)}
     */
    public void testSDOWithinDistanceArbitraryLine() throws Exception {
        String sql = 
            "select ID, JGEOMETRY from JPA_JGEOMETRY where " + "mdsys.sdo_within_distance(jgeometry, " + 
            "mdsys.sdo_geometry(2,null,null, " + 
            "mdsys.sdo_elem_info_array(1,2,1), " + 
            "mdsys.sdo_ordinate_array(10,10, 20,20, 30,30, 45,45)), " + 
            "'DISTANCE=10') = 'TRUE' ORDER BY ID";

        SQLReader reader = new SQLReader(getServerSession(STRUCT_CONVERTER_PU), sql);

        JGeometry line = 
            JGeometry.createLinearLineString(new double[] { 10, 10, 20, 20, 30, 
                                                            30, 45, 45 }, 2, 
                                             0);

        EntityManager em = createEntityManager(STRUCT_CONVERTER_PU);
        Query query = em.createQuery("SELECT ss FROM SimpleSpatial ss WHERE FUNC('mdsys.sdo_WITHIN_DISTANCE', ss.JGeometry, :otherGeometry, :params) = 'TRUE' ORDER BY ss.id ASC");
        query.setParameter("otherGeometry", line);
        query.setParameter("params", "DISTANCE=10");
        List<Spatial> results = query.getResultList(); 

        String compareResult = reader.compare(results);

        assertNull(compareResult, compareResult);
    }

    /**
     * SDO_WITHIN_DISTANCE with NULL params matching a known circle geometry (1004)
     */
    public void testSDOWithinDistanceNullParamsMatchingCircle1004() throws Exception {
        String sql = 
            "select ID, JGEOMETRY from JPA_JGEOMETRY where " + "mdsys.sdo_within_distance(jgeometry, mdsys.sdo_geometry(3, " + 
            "NULL, null, mdsys.sdo_elem_info_array(1,3,4), " + 
            "mdsys.sdo_ordinate_array(1, 0, 0, 1, 0, -1)), " + 
            "NULL) = 'TRUE' ORDER BY ID";

        SQLReader reader = new SQLReader(getServerSession(STRUCT_CONVERTER_PU), sql);

        JGeometry circle = JGeometry.createCircle(1, 0, 0, 1, 0, -1, 0);

        EntityManager em = createEntityManager(STRUCT_CONVERTER_PU);
        Query query = em.createQuery("SELECT ss FROM SimpleSpatial ss WHERE FUNC('mdsys.sdo_WITHIN_DISTANCE', ss.JGeometry, :otherGeometry, :params) = 'TRUE' ORDER BY ss.id ASC");
        query.setParameter("otherGeometry", circle);
        query.setParameter("params", null);
        List<Spatial> results = query.getResultList(); 

        String compareResult = reader.compare(results);

        assertNull(compareResult, compareResult);
    }

    /**
     * SDO_WITHIN_DISTANCE with NULL params not matching existing
     */
    public void testSDOWithinDistanceNullParamsNotMatching() throws Exception {
        String sql = 
            "select ID, JGEOMETRY from JPA_JGEOMETRY where " + "mdsys.sdo_within_distance(jgeometry, mdsys.sdo_geometry(3, " + 
            "NULL, null, mdsys.sdo_elem_info_array(1,3,4), " + 
            "mdsys.sdo_ordinate_array(10, 0, 0, 10, 0, -10)), " + 
            "NULL) = 'TRUE' ORDER BY ID";

        SQLReader reader = new SQLReader(getServerSession(STRUCT_CONVERTER_PU), sql);

        JGeometry circle = JGeometry.createCircle(10, 0, 0, 10, 0, -10, 0);

        EntityManager em = createEntityManager(STRUCT_CONVERTER_PU);
        Query query = em.createQuery("SELECT ss FROM SimpleSpatial ss WHERE FUNC('mdsys.sdo_WITHIN_DISTANCE', ss.JGeometry, :otherGeometry, :params) = 'TRUE' ORDER BY ss.id ASC");
        query.setParameter("otherGeometry", circle);
        query.setParameter("params", null);
        List<Spatial> results = query.getResultList(); 

        String compareResult = reader.compare(results);

        assertNull(compareResult, compareResult);
    }
    
    /**
     * SDO_RELATE using a dynamic rectangular window with lower left
     * and upper right coordinates of {(1,1), (20,20)}
     * The test explores different binding options of the original sdo sql from testSDORelateRectangle.
     * The observed behaviour - attempt to bind the third parameter (the second null in "mdsys.sdo_geometry(3,null,null,..."
     * cause exception.
     * That's the reason why jpql query in testSDORelateRectangle2 test can't use binding -
     * it results in exactly the same exception.   
     */
    public void testSdoBinding() throws Exception {
        // could be used for debugging
        boolean shouldPrintPassed = false;
        boolean shouldPrintFailed = false;
        boolean shouldPrintExceptionStackTrace = false;
        
        // Commented out below the original sql - it is run in bindNone case
/*        String sql = 
            "select ID, JGEOMETRY from JPA_JGEOMETRY where mdsys.sdo_relate(jgeometry, " + 
            "mdsys.sdo_geometry(3,null,null, mdsys.sdo_elem_info_array(1,3,3),  " + 
            "mdsys.sdo_ordinate_array(1,1, 20, 20)), " + 
            "'MASK=ANYINTERACT QUERYTYPE=WINDOW') = 'TRUE' ORDER BY ID";*/
        
        // parameter values
        Object[] values = {3, null, null, 1, 3, 3, 1, 1, 20, 20, "MASK=ANYINTERACT QUERYTYPE=WINDOW", "TRUE"};
        // indicates which parameter should be bound (true), which should not (false)
        // passes
        boolean[] bindNone = {false, false, false, false, false, false, false, false, false, false, false, false};
        // fails
        boolean[] bindAll = {true, true, true, true, true, true, true, true, true, true, true, true};
        // fails
        boolean[] bindOnlyThird = {false, false, true, false, false, false, false, false, false, false, false, false};
        // passes
        boolean[] bindAllButThird = {true, true, false, true, true, true, true, true, true, true, true, true};
        boolean[][] binds = {bindNone, bindAll, bindAllButThird };
        Map<boolean[], String> passed = new HashMap();
        Map<boolean[], String> failed = new HashMap();
        for(int i=0; i < binds.length; i++) {
            boolean[] bind = binds[i];
            String sql = 
                "select ID, JGEOMETRY from JPA_JGEOMETRY where mdsys.sdo_relate(jgeometry, " + 
                "mdsys.sdo_geometry("+(bind[0]?"#0":"3")+","+(bind[1]?"#1":"null")+","+(bind[2]?"#2":"null")+", mdsys.sdo_elem_info_array("+(bind[3]?"#3":"1")+","+(bind[4]?"#4":"3")+","+(bind[5]?"#5":"3")+"),  " + 
                "mdsys.sdo_ordinate_array("+(bind[6]?"#6":"1")+","+(bind[7]?"#7":"1")+", "+(bind[8]?"#8":"20")+", "+(bind[9]?"#9":"20")+")), " +
                (bind[10]?"#10":"'MASK=ANYINTERACT QUERYTYPE=WINDOW'")+") = "+(bind[11]?"#11":"'TRUE'")+" ORDER BY ID";
            List<String> argumentNames = new ArrayList(values.length);
            List argumentValues = new ArrayList(values.length);
            for(int j=0; j < values.length; j++) {
                if(bind[j]) {
                    argumentNames.add(Integer.toString(j));
                    argumentValues.add(values[j]);
                }
            }
            try {
                SQLReader reader = new SQLReader(getServerSession(STRUCT_CONVERTER_PU), sql, argumentNames, argumentValues);
                if(shouldPrintPassed) {
                    System.out.println("passed:");
                    System.out.println(sql);
                }
                passed.put(bind, sql);
            } catch (Exception ex) {
                if(shouldPrintExceptionStackTrace) {
                    ex.printStackTrace();
                }
                if(shouldPrintFailed) {
                    System.out.println("FAILED:");
                    System.out.println(sql);
                }
                failed.put(bind, sql);
            }
        }
        
        // Observed sdo behaviour - binding the third parameter causes failure 
        String errorMsg = "";
        Iterator<Map.Entry<boolean[],String>> it = failed.entrySet().iterator();
        while(it.hasNext()) {
            Map.Entry<boolean[],String> entry = it.next();
            boolean[] bind = entry.getKey();
            if(!bind[2]) {
                errorMsg += entry.getValue() + '\n';
            }
        }
        if(errorMsg.length() > 0) {
            errorMsg = "Unexpected failures: \n" + errorMsg;
            fail(errorMsg);
        }
    }
}
