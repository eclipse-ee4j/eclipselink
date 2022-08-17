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
package org.eclipse.persistence.testing.tests.jpa.structconverter;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
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
import org.eclipse.persistence.sessions.server.ServerSession;
import org.eclipse.persistence.testing.framework.jpa.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.structconverter.DummyStructConverterType;
import org.eclipse.persistence.testing.models.jpa.structconverter.JGeometryTableCreator;
import org.eclipse.persistence.testing.models.jpa.structconverter.SimpleSpatial;
import org.eclipse.persistence.testing.models.jpa.structconverter.SimpleXMLSpatial;

/**
 * TestSuite to ensure Annotation-based struct converters get properly added to the project
 *
 * This test suite ensures the project is properly set-up and does a sanity test using one of the
 * converters.  Other tests are handled by the StructConverter tests suites in the TestBrowser.
 * @author tware
 *
 */
public class XmlStructConverterTest extends StructConverterTest {

    public XmlStructConverterTest(){
    }

    public XmlStructConverterTest(String name){
        super(name);
    }

    @Override
    public String getPersistenceUnitName() {
        return "xmlStructConverter";
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("Struct Converter Test Suite");
        suite.addTest(new XmlStructConverterTest("testSetup"));
        suite.addTest(new XmlStructConverterTest("testXMLPlatform"));
        suite.addTest(new XmlStructConverterTest("testSimpleXMLSpatialWrite"));
        return suite;
    }

    /**
     * Ensure the DatabasePlatform is setup with the proper StructConverters
     */
    public void testXMLPlatform() {
        if (this.supported) {
            EntityManager em = createEntityManager();
            try {
                // trigger deploy
                em.find(SimpleXMLSpatial.class, 1L);
            } catch (Exception e){}

            StructConverter converter = getPersistenceUnitServerSession().getPlatform().getTypeConverters().get(JGeometry.class);
            assertNotNull("Platform does not have correct JGeometryConverter.", converter);
            assertTrue("JGeometery struct converter is wrong type.", converter.getClass().getName().contains("JGeometryConverter"));

            converter = getPersistenceUnitServerSession().getPlatform().getTypeConverters().get(DummyStructConverterType.class);
            assertNotNull("Platform does not have correct DummyStructConverter.", converter);
            assertTrue("JGeometery struct converter is wrong type.", converter.getClass().getName().contains("DummyStructConverter"));
        }
    }

    /**
     * Sanity test to ensure a read and a write with a Converter specified by
     * xml work properly
     */
    public void testSimpleXMLSpatialWrite() {
        if (this.supported) {
            EntityManager em = createEntityManager();

            beginTransaction(em);
            SimpleXMLSpatial simpleSpatial = new SimpleXMLSpatial(1000, pointCluster1());
            em.persist(simpleSpatial);
            em.flush();

            em.clear();

            simpleSpatial = em.find(SimpleXMLSpatial.class, 1000L);

            assertNotNull("JGeometry was not properly read in.", simpleSpatial.getJGeometry());
            rollbackTransaction(em);
        }
    }

}
