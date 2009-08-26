/*******************************************************************************
 * Copyright (c) 1998, 2009 Oracle. All rights reserved.
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

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestSuite;
import oracle.spatial.geometry.JGeometry;

import org.eclipse.persistence.platform.database.converters.StructConverter;

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
        TestSuite suite = new TestSuite("Struct Converter Test Suite");
        suite.addTest(new StructConverterTestSuite("testPlatform"));
        suite.addTest(new StructConverterTestSuite("testXMLPlatform"));
        suite.addTest(new StructConverterTestSuite("testSimpleSpatialWrite"));
        suite.addTest(new StructConverterTestSuite("testSimpleXMLSpatialWrite"));

        return new TestSetup(suite) {

            protected void setUp(){
            }

            protected void tearDown() {
                clearCache();
            }
        };
    }
    
    public void setUp () {
        super.setUp();
        if (JUnitTestCase.getServerSession().getPlatform().isOracle()){
            supported = true; 
            clearCache();
            new JGeometryTableCreator().replaceTables(JUnitTestCase.getServerSession());
        } else {
            JUnitTestCase.getServerSession().logMessage("Struct Converter JGeometry test not run because not running on OraclePlatform.");
        }
    }
    
    /**
     * Ensure the DatabasePlatform is setup with the proper StructConverters
     */
    public void testPlatform(){
        if (supported){
            EntityManager em = createEntityManager(STRUCT_CONVERTER_PU);
            try{
                // trigger deploy
                em.find(SimpleSpatial.class, new Long(1));
            } catch (Exception e){};
            
            StructConverter converter = ((org.eclipse.persistence.jpa.JpaEntityManager)em).getActiveSession().getPlatform().getTypeConverters().get(JGeometry.class);
            assertNotNull("Platform does not have correct JGeometryConverter.", converter);
            assertTrue("JGeometery struct converter is wrong type.", converter.getClass().getName().indexOf("JGeometryConverter") >= 0);
            
            converter = ((org.eclipse.persistence.jpa.JpaEntityManager)em).getActiveSession().getPlatform().getTypeConverters().get(DummyStructConverterType.class);
            assertNotNull("Platform does not have correct DummyStructConverter.", converter);
            assertTrue("JGeometery struct converter is wrong type.", converter.getClass().getName().indexOf("DummyStructConverter") >= 0);
        }
    }
    
    /**
     * Ensure the DatabasePlatform is setup with the proper StructConverters
     */
    public void testXMLPlatform(){
        if (supported){
            EntityManager em = createEntityManager(XML_STRUCT_CONVERTER_PU);
            
            try {
                // trigger deploy
                em.find(SimpleXMLSpatial.class, new Long(1));
            } catch (Exception e){};
            
            StructConverter converter = ((org.eclipse.persistence.jpa.JpaEntityManager)em).getActiveSession().getPlatform().getTypeConverters().get(JGeometry.class);
            assertNotNull("Platform does not have correct JGeometryConverter.", converter);
            assertTrue("JGeometery struct converter is wrong type.", converter.getClass().getName().indexOf("JGeometryConverter") >= 0);
            
            converter = ((org.eclipse.persistence.jpa.JpaEntityManager)em).getActiveSession().getPlatform().getTypeConverters().get(DummyStructConverterType.class);
            assertNotNull("Platform does not have correct DummyStructConverter.", converter);
            assertTrue("DummyStructConveter is wrong type.", converter.getClass().getName().indexOf("DummyStructConverter") >= 0);
        }
    }
    
    /**
     * Sanity test to ensure a read and a write with a Converter specified by 
     * annotations work properly
     */
    public void testSimpleSpatialWrite(){
        if (supported){
            EntityManager em = createEntityManager(STRUCT_CONVERTER_PU);

            em.getTransaction().begin();
            SimpleSpatial simpleSpatial = new SimpleSpatial(1000, pointCluster1());
            em.persist(simpleSpatial);
            em.flush();
            
            em.clear();
            
            simpleSpatial = em.find(SimpleSpatial.class, new Long(1000));
            
            assertNotNull("JGeometry was not properly read in.", simpleSpatial.getJGeometry());
            em.getTransaction().rollback();
        }
    }
    
    /**
     * Sanity test to ensure a read and a write with a Converter specified by 
     * xml work properly
     */
    public void testSimpleXMLSpatialWrite(){
        if (supported){
            EntityManager em = createEntityManager(XML_STRUCT_CONVERTER_PU);

            em.getTransaction().begin();
            SimpleXMLSpatial simpleXmlSpatial = new SimpleXMLSpatial(1000, pointCluster1());
            em.persist(simpleXmlSpatial);
            em.flush();
            
            em.clear();
            
            simpleXmlSpatial = em.find(SimpleXMLSpatial.class, new Long(1000));
            
            assertNotNull("JGeometry was not properly read in.", simpleXmlSpatial.getJGeometry());
            em.getTransaction().rollback();
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
