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
package org.eclipse.persistence.testing.tests.jpa.structconverter;

import javax.persistence.EntityManager;

import junit.framework.Test;
import junit.framework.TestSuite;
import oracle.spatial.geometry.JGeometry;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.expressions.spatial.SpatialExpressionFactory;
import org.eclipse.persistence.expressions.spatial.SpatialParameters;
import org.eclipse.persistence.expressions.spatial.SpatialParameters.Mask;
import org.eclipse.persistence.platform.database.converters.StructConverter;
import org.eclipse.persistence.queries.ReadAllQuery;

import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.structconverter.SimpleSpatial;
import org.eclipse.persistence.testing.models.jpa.structconverter.SimpleXMLSpatial;
import org.eclipse.persistence.testing.models.jpa.structconverter.JGeometryTableCreator;
import org.eclipse.persistence.testing.models.jpa.structconverter.DummyStructConverterType;

/**
 * TestSuite to ensure Annotation-based struct converters get properly added to the project
 * 
 * This test suite ensures the project is properly set-up and does a sanity test using one of the
 * converters.  Other tests are handled by the StructConverter tests suites in the TestBrowser.
 * @author tware
 *
 */
public class StructConverterTestSuite extends JUnitTestCase {

    public static final String STRUCT_CONVERTER_PU = "structConverter";
    public static final String XML_STRUCT_CONVERTER_PU = "xmlStructConverter";
    
    public boolean supported = false;

    public StructConverterTestSuite(){
    }

    public StructConverterTestSuite(String name){
        super(name);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("Struct Converter Test Suite");
        suite.addTest(new StructConverterTestSuite("testSetup"));
        suite.addTest(new StructConverterTestSuite("testPlatform"));
        suite.addTest(new StructConverterTestSuite("testXMLPlatform"));
        suite.addTest(new StructConverterTestSuite("testSimpleSpatialWrite"));
        suite.addTest(new StructConverterTestSuite("testSimpleXMLSpatialWrite"));
        suite.addTest(new StructConverterTestSuite("testSimpleReadQuery"));
        return suite;
    }

    public void testSetup() {
        if (!getServerSession(STRUCT_CONVERTER_PU).getPlatform().isOracle()) {
            supported = false;
            return;
        }
        clearCache(STRUCT_CONVERTER_PU);
        getServerSession(STRUCT_CONVERTER_PU).executeNonSelectingSQL("DELETE FROM USER_SDO_GEOM_METADATA WHERE TABLE_NAME = 'JPA_JGEOMETRY'");
        new JGeometryTableCreator().replaceTables(JUnitTestCase.getServerSession(STRUCT_CONVERTER_PU));
        getServerSession(STRUCT_CONVERTER_PU).executeNonSelectingSQL("INSERT INTO USER_SDO_GEOM_METADATA(TABLE_NAME, COLUMN_NAME, DIMINFO) VALUES('JPA_JGEOMETRY', 'JGEOMETRY'," +
                " mdsys.sdo_dim_array(mdsys.sdo_dim_element('X', -100, 100, 0.005), mdsys.sdo_dim_element('Y', -100, 100, 0.005)))");

        getServerSession(STRUCT_CONVERTER_PU).executeNonSelectingSQL("CREATE INDEX test_idx on JPA_JGEOMETRY(jgeometry) indextype is mdsys.spatial_index parameters ('sdo_level=5 sdo_numtiles=6')");
    }
    
    /**
     * Ensure the DatabasePlatform is setup with the proper StructConverters
     */
    public void testPlatform() {
        if (this.supported) {
            EntityManager em = createEntityManager(STRUCT_CONVERTER_PU);
            try{
                // trigger deploy
                em.find(SimpleSpatial.class, new Long(1));
            } catch (Exception e){};
            
            StructConverter converter = getServerSession(STRUCT_CONVERTER_PU).getPlatform().getTypeConverters().get(JGeometry.class);
            assertNotNull("Platform does not have correct JGeometryConverter.", converter);
            assertTrue("JGeometery struct converter is wrong type.", converter.getClass().getName().indexOf("JGeometryConverter") >= 0);
            
            converter = getServerSession(STRUCT_CONVERTER_PU).getPlatform().getTypeConverters().get(DummyStructConverterType.class);
            assertNotNull("Platform does not have correct DummyStructConverter.", converter);
            assertTrue("JGeometery struct converter is wrong type.", converter.getClass().getName().indexOf("DummyStructConverter") >= 0);
        }
    }
    
    /**
     * Ensure the DatabasePlatform is setup with the proper StructConverters
     */
    public void testXMLPlatform() {
        if (this.supported && !isOnServer()) {
            EntityManager em = createEntityManager(XML_STRUCT_CONVERTER_PU);
            try {
                // trigger deploy
                em.find(SimpleXMLSpatial.class, new Long(1));
            } catch (Exception e){};
            
            StructConverter converter = getServerSession(XML_STRUCT_CONVERTER_PU).getPlatform().getTypeConverters().get(JGeometry.class);
            assertNotNull("Platform does not have correct JGeometryConverter.", converter);
            assertTrue("JGeometery struct converter is wrong type.", converter.getClass().getName().indexOf("JGeometryConverter") >= 0);
            
            converter = getServerSession(XML_STRUCT_CONVERTER_PU).getPlatform().getTypeConverters().get(DummyStructConverterType.class);
            assertNotNull("Platform does not have correct DummyStructConverter.", converter);
            assertTrue("JGeometery struct converter is wrong type.", converter.getClass().getName().indexOf("DummyStructConverter") >= 0);
        }
    }
    
    /**
     * Sanity test to ensure a read and a write with a Converter specified by 
     * annotations work properly
     */
    public void testSimpleSpatialWrite(){
        if (supported){
            EntityManager em = createEntityManager(STRUCT_CONVERTER_PU);
    
            beginTransaction(em);
            SimpleSpatial simpleSpatial = new SimpleSpatial(1000, pointCluster1());
            em.persist(simpleSpatial);
            em.flush();
            
            em.clear();
            
            simpleSpatial = em.find(SimpleSpatial.class, new Long(1000));
            
            assertNotNull("JGeometry was not properly read in.", simpleSpatial.getJGeometry());
            rollbackTransaction(em);
        }
    }
    
    /**
     * Sanity test to ensure a read and a write with a Converter specified by 
     * xml work properly
     */
    public void testSimpleXMLSpatialWrite() {
        if (this.supported && !isOnServer()) {
            EntityManager em = createEntityManager(XML_STRUCT_CONVERTER_PU);
    
            beginTransaction(em);
            SimpleXMLSpatial simpleSpatial = new SimpleXMLSpatial(1000, pointCluster1());
            em.persist(simpleSpatial);
            em.flush();
            
            em.clear();
            
            simpleSpatial = em.find(SimpleXMLSpatial.class, new Long(1000));
            
            assertNotNull("JGeometry was not properly read in.", simpleSpatial.getJGeometry());
            rollbackTransaction(em);
        }
    }
    
    /**
     * Tests if SpatialExpressions will work properly without hung up when using ANYINTERACT query type
     */
    public void testSimpleReadQuery(){
        if (supported) {
            JGeometry circle = JGeometry.createCircle(-10, 0, 0, 10, 10, 0, 0);
            JGeometry newCircle = JGeometry.createCircle(0, -10, 0, 10, 10, 0, 0);
            EntityManager em = createEntityManager(STRUCT_CONVERTER_PU);
            try{
                beginTransaction(em);
                SimpleSpatial simpleSpatial = new SimpleSpatial(2000, circle);
                em.persist(simpleSpatial);
                commitTransaction(em);
            }catch (RuntimeException ex){
                if (isTransactionActive(em)){
                    rollbackTransaction(em);
                }
                throw ex;
            }
    
            clearCache(STRUCT_CONVERTER_PU);
            
            ReadAllQuery raq = new ReadAllQuery(SimpleSpatial.class);
            ExpressionBuilder eb = raq.getExpressionBuilder();
    
            SpatialParameters parameters = new SpatialParameters();
            parameters.setQueryType(SpatialParameters.QueryType.WINDOW).setMask(Mask.ANYINTERACT);
            Expression selectionCriteria = SpatialExpressionFactory.relate(eb.get("jGeometry"), newCircle, parameters);
            raq.setSelectionCriteria(selectionCriteria);
            raq.addAscendingOrdering("id");
            getServerSession(STRUCT_CONVERTER_PU).executeQuery(raq);
        }
    }
    
    /**
     * mdsys.sdo_geometry(5,
     *                   NULL, null,
     *                   mdsys.sdo_elem_info_array(1,1,3),
     *                   mdsys.sdo_ordinate_array(1.1, 1.1, 2.2, 2.2, 3.3, 4.4)));
     */
    public JGeometry pointCluster1() {
        Object[] points = 
            new Object[] { new double[] { 1.1, 1.1 }, new double[] { 2.2,                                                                      2.2 }, 
                           new double[] { 3.3, 4.4 } };
        return JGeometry.createMultiPoint(points, 2, 0);
    }
}
