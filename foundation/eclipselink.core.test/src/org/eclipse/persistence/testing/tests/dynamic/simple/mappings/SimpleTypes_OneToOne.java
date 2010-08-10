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
import static org.junit.Assert.assertNotNull;

//EclipseLink imports
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.dynamic.DynamicClassLoader;
import org.eclipse.persistence.dynamic.DynamicEntity;
import org.eclipse.persistence.dynamic.DynamicHelper;
import org.eclipse.persistence.dynamic.DynamicType;
import org.eclipse.persistence.dynamic.DynamicTypeBuilder;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.mappings.OneToOneMapping;
import org.eclipse.persistence.queries.ReportQuery;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.UnitOfWork;

//domain-specific (testing) imports
import org.eclipse.persistence.testing.tests.dynamic.QuerySQLTracker;
import static org.eclipse.persistence.testing.tests.dynamic.DynamicTestingHelper.createSession;

public class SimpleTypes_OneToOne {

    //test fixtures
    static DatabaseSession session = null;
    static DynamicHelper dynamicHelper = null;
    static QuerySQLTracker qTracker = null;
    
    @BeforeClass
    public static void setUp() {
        session = createSession();
        // force logging level
        session.setLogLevel(SessionLog.FINE);
        dynamicHelper = new DynamicHelper(session);
        DynamicClassLoader dcl = dynamicHelper.getDynamicClassLoader();
        Class<?> simpleTypeB = dcl.createDynamicClass("simple.mappings.SimpleB");
        DynamicTypeBuilder bFactory = new DynamicTypeBuilder(simpleTypeB, null, "SIMPLE_TYPE_B");
        bFactory.setPrimaryKeyFields("SID");
        bFactory.addDirectMapping("id", int.class, "SID");
        bFactory.addDirectMapping("value1", String.class, "VAL_1");

        Class<?> simpleTypeA = dcl.createDynamicClass("simple.mappings.SimpleA");
        DynamicTypeBuilder aFactory = new DynamicTypeBuilder(simpleTypeA, null, "SIMPLE_TYPE_A");
        aFactory.setPrimaryKeyFields("SID");
        aFactory.addDirectMapping("id", int.class, "SID");
        aFactory.addDirectMapping("value1", String.class, "VAL_1");
        aFactory.addOneToOneMapping("b", bFactory.getType(), "B_FK");

        dynamicHelper.addTypes(true, true, aFactory.getType(), bFactory.getType());
        qTracker = QuerySQLTracker.install(session);
    }
    
    @AfterClass
    public static void tearDown() {
        session.executeNonSelectingSQL("DROP TABLE SIMPLE_TYPE_A");
        session.executeNonSelectingSQL("DROP TABLE SIMPLE_TYPE_B");
        session.logout();
        session = null;
        dynamicHelper = null;
    }

    @After
    public void clearDynamicTables() {
        session.executeNonSelectingSQL("DELETE FROM SIMPLE_TYPE_A");
        session.executeNonSelectingSQL("DELETE FROM SIMPLE_TYPE_B");
        qTracker.reset();
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
        DirectToFieldMapping b_id = (DirectToFieldMapping) descriptorB.getMappingForAttributeName("id");
        assertEquals(int.class, b_id.getAttributeClassification());
        DirectToFieldMapping b_value1 = (DirectToFieldMapping) descriptorB.getMappingForAttributeName("value1");
        assertEquals(String.class, b_value1.getAttributeClassification());

        OneToOneMapping a_b = (OneToOneMapping) descriptorA.getMappingForAttributeName("b");
        assertEquals(descriptorB, a_b.getReferenceDescriptor());
    }

    @Test
    public void createSimpleA() {
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
    }

    @Test
    public void createSimpleB() {
        DynamicType simpleTypeB = dynamicHelper.getType("SimpleB");
        Assert.assertNotNull(simpleTypeB);

        DynamicEntity simpleInstance = simpleTypeB.newDynamicEntity();
        simpleInstance.set("id", 1);
        simpleInstance.set("value1", "B1");

        UnitOfWork uow = session.acquireUnitOfWork();
        uow.registerNewObject(simpleInstance);
        uow.commit();

        ReportQuery countQuery = dynamicHelper.newReportQuery("SimpleB", new ExpressionBuilder());
        countQuery.addCount();
        countQuery.setShouldReturnSingleValue(true);
        int simpleCount = ((Number) session.executeQuery(countQuery)).intValue();
        Assert.assertEquals(1, simpleCount);
    }

    @Test
    public void createSimpleAwithSimpleB() {
        DynamicType simpleTypeA = dynamicHelper.getType("SimpleA");
        Assert.assertNotNull(simpleTypeA);
        DynamicType simpleTypeB = dynamicHelper.getType("SimpleB");
        Assert.assertNotNull(simpleTypeB);
        Assert.assertNotNull(session.getDescriptorForAlias("SimpleB"));

        DynamicEntity simpleInstanceB = simpleTypeB.newDynamicEntity();
        simpleInstanceB.set("id", 2);
        simpleInstanceB.set("value1", "B2");

        DynamicEntity simpleInstanceA = simpleTypeA.newDynamicEntity();
        simpleInstanceA.set("id", 2);
        simpleInstanceA.set("value1", "A2");
        simpleInstanceA.set("b", simpleInstanceB);

        UnitOfWork uow = session.acquireUnitOfWork();
        uow.registerNewObject(simpleInstanceA);
        uow.registerNewObject(simpleInstanceB);
        uow.commit();

        assertEquals(2, qTracker.getTotalSQLINSERTCalls());
        // There is no reason for a shallow insert and an update in this mapping
        assertEquals("No update expected for new objects with 1:1", 0, qTracker.getTotalSQLUPDATECalls());

        ReportQuery countQuery = dynamicHelper.newReportQuery("SimpleB", new ExpressionBuilder());
        countQuery.addCount();
        countQuery.setShouldReturnSingleValue(true);
        Assert.assertEquals(1, ((Number) session.executeQuery(countQuery)).intValue());

        countQuery = dynamicHelper.newReportQuery("SimpleA", new ExpressionBuilder());
        countQuery.addCount();
        countQuery.setShouldReturnSingleValue(true);
        Assert.assertEquals(1, ((Number) session.executeQuery(countQuery)).intValue());

    }

}