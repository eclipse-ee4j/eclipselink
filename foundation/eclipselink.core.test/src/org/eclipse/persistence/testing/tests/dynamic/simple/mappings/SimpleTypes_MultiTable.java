/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     dclarke - Dynamic Persistence
 *       http://wiki.eclipse.org/EclipseLink/Development/Dynamic 
 *       (https://bugs.eclipse.org/bugs/show_bug.cgi?id=200045)
 *     mnorman - tweaks to work from Ant command-line,
 *               get database properties from System, etc.
 *
 ******************************************************************************/
package org.eclipse.persistence.testing.tests.dynamic.simple.mappings;

//JUnit4 imports
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

//EclipseLink imports
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.dynamic.DynamicClassLoader;
import org.eclipse.persistence.dynamic.DynamicEntity;
import org.eclipse.persistence.dynamic.DynamicHelper;
import org.eclipse.persistence.dynamic.DynamicType;
import org.eclipse.persistence.dynamic.DynamicTypeBuilder;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.internal.dynamic.DynamicEntityImpl;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.queries.ReportQuery;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.UnitOfWork;

//domain-specific (testing) imports
import static org.eclipse.persistence.testing.tests.dynamic.DynamicTestingHelper.createSession;

public class SimpleTypes_MultiTable {

    //test fixtures
    static DatabaseSession session = null;
    static DynamicHelper dynamicHelper = null;
    
    @BeforeClass
    public static void setUp() {
        session = createSession();
        dynamicHelper = new DynamicHelper(session);
        DynamicClassLoader dcl = dynamicHelper.getDynamicClassLoader();
        Class<?> simpleTypeA = dcl.createDynamicClass("simple.mappings.SimpleA");
        DynamicTypeBuilder typeBuilder = new DynamicTypeBuilder(simpleTypeA, null, "SIMPLE_TYPE_A", "SIMPLE_TYPE_B", "SIMPLE_TYPE_C");
        typeBuilder.setPrimaryKeyFields("SIMPLE_TYPE_A.SID");
        typeBuilder.addDirectMapping("id", int.class, "SIMPLE_TYPE_A.SID");
        typeBuilder.addDirectMapping("value1", String.class, "SIMPLE_TYPE_A.VAL_1");
        typeBuilder.addDirectMapping("value2", boolean.class, "SIMPLE_TYPE_B.VAL_2");
        typeBuilder.addDirectMapping("value3", String.class, "SIMPLE_TYPE_B.VAL_3");
        typeBuilder.addDirectMapping("value4", double.class, "SIMPLE_TYPE_C.VAL_4");
        typeBuilder.addDirectMapping("value5", String.class, "SIMPLE_TYPE_C.VAL_5");
        dynamicHelper.addTypes(true, true, typeBuilder.getType());
    }

    @AfterClass
    public static void tearDown() {
        session.executeNonSelectingSQL("DROP TABLE SIMPLE_TYPE_C");
        session.executeNonSelectingSQL("DROP TABLE SIMPLE_TYPE_B");
        session.executeNonSelectingSQL("DROP TABLE SIMPLE_TYPE_A");
        session.logout();
        session = null;
        dynamicHelper = null;
    }

    @After
    public void clearDynamicTables() {
        session.executeNonSelectingSQL("DELETE FROM SIMPLE_TYPE_C");
        session.executeNonSelectingSQL("DELETE FROM SIMPLE_TYPE_B");
        session.executeNonSelectingSQL("DELETE FROM SIMPLE_TYPE_A");
    }
    
    @Test
    public void verifyConfig() throws Exception {
        ClassDescriptor descriptorA = dynamicHelper.getSession().getClassDescriptorForAlias("SimpleA");
        assertNotNull("No descriptor found for alias='SimpleA'", descriptorA);

        DynamicType simpleTypeA = dynamicHelper.getType("SimpleA");
        assertNotNull("'SimpleA' EntityType not found", simpleTypeA);
        assertEquals(descriptorA, simpleTypeA.getDescriptor());

        assertTrue(descriptorA.hasMultipleTables());
        assertEquals(3, descriptorA.getTables().size());

        DirectToFieldMapping a_id = (DirectToFieldMapping) descriptorA.getMappingForAttributeName("id");
        assertEquals(int.class, a_id.getAttributeClassification());
        DirectToFieldMapping a_value1 = (DirectToFieldMapping) descriptorA.getMappingForAttributeName("value1");
        assertEquals(String.class, a_value1.getAttributeClassification());

        DirectToFieldMapping a_value2 = (DirectToFieldMapping) descriptorA.getMappingForAttributeName("value2");
        assertEquals(boolean.class, a_value2.getAttributeClassification());

        DirectToFieldMapping a_value3 = (DirectToFieldMapping) descriptorA.getMappingForAttributeName("value3");
        assertEquals(String.class, a_value3.getAttributeClassification());

        DirectToFieldMapping a_value4 = (DirectToFieldMapping) descriptorA.getMappingForAttributeName("value4");
        assertEquals(double.class, a_value4.getAttributeClassification());

        DirectToFieldMapping a_value5 = (DirectToFieldMapping) descriptorA.getMappingForAttributeName("value5");
        assertEquals(String.class, a_value5.getAttributeClassification());
    }

    @Test
    public void verifyProperties() {
        DynamicType simpleTypeA = dynamicHelper.getType("SimpleA");
        Assert.assertNotNull(simpleTypeA);

        assertEquals(6, simpleTypeA.getNumberOfProperties());
        assertTrue(simpleTypeA.getPropertiesNames().contains("id"));
        assertEquals(int.class, simpleTypeA.getPropertyType("id"));
        assertTrue("value1", simpleTypeA.getPropertiesNames().contains("value1"));
        assertEquals(String.class, simpleTypeA.getPropertyType("value1"));
        assertTrue("value2", simpleTypeA.getPropertiesNames().contains("value2"));
        assertEquals(boolean.class, simpleTypeA.getPropertyType("value2"));
        assertTrue("value3", simpleTypeA.getPropertiesNames().contains("value3"));
        assertEquals(String.class, simpleTypeA.getPropertyType("value3"));
        assertTrue("value4", simpleTypeA.getPropertiesNames().contains("value4"));
        assertEquals(double.class, simpleTypeA.getPropertyType("value4"));
        assertTrue("value5", simpleTypeA.getPropertiesNames().contains("value5"));
        assertEquals(String.class, simpleTypeA.getPropertyType("value5"));

    }

    @Test
    public void createSimpleA() {
        DynamicType simpleTypeA = dynamicHelper.getType("SimpleA");
        Assert.assertNotNull(simpleTypeA);

        DynamicEntity a = simpleTypeA.newDynamicEntity();

        assertNotNull(a);
        assertEquals(a.get("id"), 0);
        assertFalse(a.isSet("value1"));
        assertEquals(a.get("value2"), false);
        assertFalse(a.isSet("value3"));
        assertEquals(a.get("value4"), 0.0);
        assertFalse(a.isSet("value5"));
    }

    @Test
    public void persistSimpleA() {
        DynamicType simpleTypeA = dynamicHelper.getType("SimpleA");
        Assert.assertNotNull(simpleTypeA);

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
        Assert.assertEquals(1, simpleCount);

        session.release();
    }

    @Test
    public void verifyChangeTracking() {
        persistSimpleA();
        DynamicType simpleTypeA = dynamicHelper.getType("SimpleA");
        Assert.assertNotNull(simpleTypeA);

        UnitOfWork uow = session.acquireUnitOfWork();

        ReadObjectQuery findQuery = dynamicHelper.newReadObjectQuery("SimpleA");
        findQuery.setSelectionCriteria(findQuery.getExpressionBuilder().get("id").equal(1));
        DynamicEntityImpl a = (DynamicEntityImpl)uow.executeQuery(findQuery);

        assertNotNull(a);
        assertNotNull(a._persistence_getPropertyChangeListener());

        uow.release();
        session.release();
    }

}