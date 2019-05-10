/*
 * Copyright (c) 1998, 2019 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2019 IBM Corporation. All rights reserved.
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
//     dclarke - Dynamic Persistence
//       http://wiki.eclipse.org/EclipseLink/Development/Dynamic
//       (https://bugs.eclipse.org/bugs/show_bug.cgi?id=200045)
//     mnorman - tweaks to work from Ant command-line,
//               get database properties from System, etc.
//
package org.eclipse.persistence.testing.tests.dynamic.simple.mappings;

//JUnit4 imports

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.dynamic.DynamicClassLoader;
import org.eclipse.persistence.dynamic.DynamicEntity;
import org.eclipse.persistence.dynamic.DynamicHelper;
import org.eclipse.persistence.dynamic.DynamicType;
import org.eclipse.persistence.dynamic.DynamicTypeBuilder;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.internal.descriptors.changetracking.AggregateAttributeChangeListener;
import org.eclipse.persistence.internal.dynamic.DynamicEntityImpl;
import org.eclipse.persistence.mappings.AggregateObjectMapping;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.queries.ReportQuery;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.eclipse.persistence.testing.tests.dynamic.DynamicTestingHelper.createSession;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

//EclipseLink imports
//domain-specific (testing) imports

public class SimpleTypes_AggregateObject {

    //test fixtures
    static DatabaseSession session = null;
    static DynamicHelper dynamicHelper = null;

    @BeforeClass
    public static void setUp() {
        session = createSession();
        dynamicHelper = new DynamicHelper(session);
        DynamicClassLoader dcl = dynamicHelper.getDynamicClassLoader();
        Class<?> simpleTypeB = dcl.createDynamicClass("simple.mappings.SimpleB");
        DynamicTypeBuilder bTypeBuilder = new DynamicTypeBuilder(simpleTypeB, null);
        bTypeBuilder.addDirectMapping("value2", boolean.class, "VAL_2");
        bTypeBuilder.addDirectMapping("value3", String.class, "VAL_3");

        Class<?> simpleTypeC = dcl.createDynamicClass("simple.mappings.SimpleC");
        DynamicTypeBuilder cTypeBuilder = new DynamicTypeBuilder(simpleTypeC, null);
        cTypeBuilder.addDirectMapping("value4", double.class, "VAL_4");
        cTypeBuilder.addDirectMapping("value5", String.class, "VAL_5");

        Class<?> simpleTypeA = dcl.createDynamicClass("simple.mappings.SimpleA");
        DynamicTypeBuilder aTypeBuilder = new DynamicTypeBuilder(simpleTypeA, null, "SIMPLE_TYPE_A");
        aTypeBuilder.setPrimaryKeyFields("SID");
        aTypeBuilder.addDirectMapping("id", int.class, "SID");
        aTypeBuilder.addDirectMapping("value1", String.class, "VAL_1");
        aTypeBuilder.addAggregateObjectMapping("b", bTypeBuilder.getType(), true);
        aTypeBuilder.addAggregateObjectMapping("c", cTypeBuilder.getType(), false);

        dynamicHelper.addTypes(true, true, aTypeBuilder.getType(), bTypeBuilder.getType(), cTypeBuilder.getType());
    }

    @AfterClass
    public static void tearDown() {
        session.executeNonSelectingSQL("DROP TABLE SIMPLE_TYPE_A");
        session.logout();
        session = null;
        dynamicHelper = null;
    }

    @After
    public void clearDynamicTables() {
        session.executeNonSelectingSQL("DELETE FROM SIMPLE_TYPE_A");
    }

    @Test
    public void verifyConfig() throws Exception {
        ClassDescriptor descriptorA = dynamicHelper.getSession().getClassDescriptorForAlias("SimpleA");
        assertNotNull("No descriptor found for alias='SimpleA'", descriptorA);

        DynamicType simpleTypeA = dynamicHelper.getType("SimpleA");
        assertNotNull("'SimpleA' EntityType not found", simpleTypeA);
        assertEquals(descriptorA, simpleTypeA.getDescriptor());
        DirectToFieldMapping a_id = (DirectToFieldMapping) descriptorA.getMappingForAttributeName("id");
        assertEquals(int.class, a_id.getAttributeClassification());
        DirectToFieldMapping a_value1 = (DirectToFieldMapping) descriptorA.getMappingForAttributeName("value1");
        assertEquals(String.class, a_value1.getAttributeClassification());

        ClassDescriptor descriptorB = dynamicHelper.getSession().getClassDescriptorForAlias("SimpleB");
        assertNotNull("No descriptor found for alias='SimpleB'", descriptorB);

        DynamicType simpleTypeB = dynamicHelper.getType("SimpleB");
        assertNotNull("'SimpleB' EntityType not found", simpleTypeB);
        assertEquals(descriptorB, simpleTypeB.getDescriptor());
        DirectToFieldMapping b_value2 = (DirectToFieldMapping) descriptorB.getMappingForAttributeName("value2");
        assertEquals(boolean.class, b_value2.getAttributeClassification());
        DirectToFieldMapping b_value3 = (DirectToFieldMapping) descriptorB.getMappingForAttributeName("value3");
        assertEquals(String.class, b_value3.getAttributeClassification());
        assertTrue(descriptorB.isAggregateDescriptor());

        AggregateObjectMapping a_b = (AggregateObjectMapping) descriptorA.getMappingForAttributeName("b");
        assertSame(descriptorB.getJavaClass(), a_b.getReferenceDescriptor().getJavaClass());
        assertTrue(a_b.isNullAllowed());

        ClassDescriptor descriptorC = dynamicHelper.getSession().getClassDescriptorForAlias("SimpleC");
        assertNotNull("No descriptor found for alias='SimpleB'", descriptorB);

        DynamicType simpleTypeC = dynamicHelper.getType("SimpleC");
        assertNotNull("'SimpleC' EntityType not found", simpleTypeC);
        assertEquals(descriptorB, simpleTypeB.getDescriptor());
        DirectToFieldMapping c_value4 = (DirectToFieldMapping) descriptorC.getMappingForAttributeName("value4");
        assertEquals(double.class, c_value4.getAttributeClassification());
        DirectToFieldMapping c_value5 = (DirectToFieldMapping) descriptorC.getMappingForAttributeName("value5");
        assertEquals(String.class, c_value5.getAttributeClassification());
        assertTrue(descriptorB.isAggregateDescriptor());

        AggregateObjectMapping a_c = (AggregateObjectMapping) descriptorA.getMappingForAttributeName("c");
        assertSame(descriptorC.getJavaClass(), a_c.getReferenceDescriptor().getJavaClass());
        assertFalse(a_c.isNullAllowed());
    }

    @Test
    public void verifyProperties() {
        DynamicType simpleTypeA = dynamicHelper.getType("SimpleA");
        assertNotNull(simpleTypeA);

        assertEquals(4, simpleTypeA.getNumberOfProperties());
        assertEquals("id", simpleTypeA.getPropertiesNames().get(0));
        assertEquals("value1", simpleTypeA.getPropertiesNames().get(1));
        assertEquals("b", simpleTypeA.getPropertiesNames().get(2));
        assertEquals("c", simpleTypeA.getPropertiesNames().get(3));
    }

    @Test
    public void createSimpleA() {
        DynamicType simpleTypeA = dynamicHelper.getType("SimpleA");
        assertNotNull(simpleTypeA);

        DynamicEntity a = simpleTypeA.newDynamicEntity();

        assertNotNull(a);
        assertEquals(((Number) a.get("id")).intValue(),0);
        assertFalse(a.isSet("id"));
        assertFalse(a.isSet("value1"));
        assertFalse(a.isSet("b"));
        DynamicType typeC = dynamicHelper.getType("SimpleC");
        assertEquals(a.get("c").getClass(), typeC.newDynamicEntity().getClass());
        DynamicEntity c = a.get("c");
        assertNotNull(c);
        assertEquals(((Number) c.get("value4")).doubleValue(), 0.0, 0.01);
        assertFalse(c.isSet("value5"));
    }

    @Test
    public void persistSimpleA() {
        DynamicType simpleTypeA = dynamicHelper.getType("SimpleA");
        assertNotNull(simpleTypeA);

        DynamicEntity simpleInstance = simpleTypeA.newDynamicEntity();
        simpleInstance.set("id", 1);
        simpleInstance.set("value1", "A1");

        UnitOfWork uow = session.acquireUnitOfWork();
        uow.registerNewObject(simpleInstance);
        uow.commit();

        ReportQuery countQuery = dynamicHelper.newReportQuery("SimpleA", new ExpressionBuilder());
        countQuery.addCount();
        countQuery.setShouldReturnSingleValue(true);
        int simpleCount = ((Number) session.executeQuery(countQuery)).intValue();
        assertEquals(1, simpleCount);
    }

    @Test
    public void verifyChangTracking() {
        persistSimpleA();

        DynamicType simpleTypeA = dynamicHelper.getType("SimpleA");
        assertNotNull(simpleTypeA);

        UnitOfWork uow = session.acquireUnitOfWork();

        ReadObjectQuery roq = dynamicHelper.newReadObjectQuery("SimpleA");
        roq.setSelectionCriteria(roq.getExpressionBuilder().get("id").equal(1));

        DynamicEntityImpl sharedA = (DynamicEntityImpl) session.executeQuery(roq);
        assertNotNull(sharedA);
        assertNull(sharedA._persistence_getPropertyChangeListener());

        DynamicEntityImpl a = (DynamicEntityImpl) uow.executeQuery(roq);
        assertNotNull(a);
        assertNotNull(a._persistence_getPropertyChangeListener());

        DynamicEntityImpl c = a.<DynamicEntityImpl> get("c");
        assertNotNull(c);
        assertNotNull(c._persistence_getPropertyChangeListener());
        assertTrue(c._persistence_getPropertyChangeListener() instanceof AggregateAttributeChangeListener);

        uow.release();
    }

    @Test
    public void createSimpleAwithSimpleB() {
        DynamicType simpleTypeA = dynamicHelper.getType("SimpleA");
        assertNotNull(simpleTypeA);
        DynamicType simpleTypeB = dynamicHelper.getType("SimpleB");
        assertNotNull(simpleTypeB);

        assertNotNull(session.getDescriptorForAlias("SimpleB"));

        DynamicEntity simpleInstanceB = simpleTypeB.newDynamicEntity();
        simpleInstanceB.set("value2", true);
        simpleInstanceB.set("value3", "B2");

        DynamicEntity simpleInstanceA = simpleTypeA.newDynamicEntity();
        simpleInstanceA.set("id", 2);
        simpleInstanceA.set("value1", "A2");
        simpleInstanceA.set("b", simpleInstanceB);

        UnitOfWork uow = session.acquireUnitOfWork();
        uow.registerNewObject(simpleInstanceA);
        uow.commit();

        ReportQuery countQuery = dynamicHelper.newReportQuery("SimpleA", new ExpressionBuilder());
        countQuery.addCount();
        countQuery.setShouldReturnSingleValue(true);

        int simpleCountA = ((Number) session.executeQuery(countQuery)).intValue();
        assertEquals(1, simpleCountA);
    }

}
