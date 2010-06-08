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

//javase imports
import java.util.Collection;
import java.util.List;

//JUnit4 imports
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import junit.framework.Assert;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

//EclipseLink imports
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.dynamic.DynamicClassLoader;
import org.eclipse.persistence.dynamic.DynamicEntity;
import org.eclipse.persistence.dynamic.DynamicHelper;
import org.eclipse.persistence.dynamic.DynamicType;
import org.eclipse.persistence.dynamic.DynamicTypeBuilder;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.mappings.ManyToManyMapping;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.queries.ReportQuery;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.UnitOfWork;

//domain-specific (testing) imports
import static org.eclipse.persistence.testing.tests.dynamic.DynamicTestingHelper.createSession;

public class SimpleTypes_ManyToMany {

    //test fixtures
    static DatabaseSession session = null;
    static DynamicHelper dynamicHelper = null;
    static DynamicType aType = null;
    static DynamicType bType = null;

    @BeforeClass
    public static void setUp() {
        session = createSession();
        dynamicHelper = new DynamicHelper(session);
        DynamicClassLoader dcl = dynamicHelper.getDynamicClassLoader();
        Class<?> simpleTypeA = dcl.createDynamicClass("simple.mappings.SimpleA");
        DynamicTypeBuilder aFactory = new DynamicTypeBuilder(simpleTypeA, null, "SIMPLE_TYPE_A");
        aFactory.setPrimaryKeyFields("SID");
        aType = aFactory.getType();

        Class<?> simpleTypeB = dcl.createDynamicClass("simple.mappings.SimpleB");
        DynamicTypeBuilder bFactory = new DynamicTypeBuilder(simpleTypeB, null, "SIMPLE_TYPE_B");
        bFactory.setPrimaryKeyFields("SID");
        bType = bFactory.getType();

        bFactory.addDirectMapping("id", int.class, "SID");
        bFactory.addDirectMapping("value1", String.class, "VAL_1");

        aFactory.addDirectMapping("id", int.class, "SID");
        aFactory.addDirectMapping("value1", String.class, "VAL_1");
        aFactory.addManyToManyMapping("b", bFactory.getType(), "SIMPLE_A_B");

        dynamicHelper.addTypes(true, true, aFactory.getType(), bFactory.getType());
    }

    @AfterClass
    public static void tearDown() {
        session.executeNonSelectingSQL("DROP TABLE SIMPLE_A_B");
        session.executeNonSelectingSQL("DROP TABLE SIMPLE_TYPE_B");
        session.executeNonSelectingSQL("DROP TABLE SIMPLE_TYPE_A");
        session.logout();
        session = null;
        dynamicHelper = null;
    }

    @After
    public void clearDynamicTables() {
        session.executeNonSelectingSQL("DELETE FROM SIMPLE_A_B");
        session.executeNonSelectingSQL("DELETE FROM SIMPLE_TYPE_B");
        session.executeNonSelectingSQL("DELETE FROM SIMPLE_TYPE_A");
    }

    @Test
    public void verifyConfig() throws Exception {
        ClassDescriptor descriptorA = session.getClassDescriptorForAlias("SimpleA");
        assertNotNull("No descriptor found for alias='SimpleA'", descriptorA);

        assertNotNull("'SimpleA' EntityType not found", aType);
        assertEquals(descriptorA, aType.getDescriptor());
        DirectToFieldMapping a_id = (DirectToFieldMapping) descriptorA.getMappingForAttributeName("id");
        assertEquals(int.class, a_id.getAttributeClassification());
        DirectToFieldMapping a_value1 = (DirectToFieldMapping) descriptorA.getMappingForAttributeName("value1");
        assertEquals(String.class, a_value1.getAttributeClassification());

        ClassDescriptor descriptorB = session.getClassDescriptorForAlias("SimpleB");
        assertNotNull("No descriptor found for alias='SimpleB'", descriptorB);

        assertNotNull("'SimpleB' EntityType not found", bType);
        assertEquals(descriptorB, bType.getDescriptor());
        DirectToFieldMapping b_id = (DirectToFieldMapping) descriptorB.getMappingForAttributeName("id");
        assertEquals(int.class, b_id.getAttributeClassification());
        DirectToFieldMapping b_value1 = (DirectToFieldMapping) descriptorB.getMappingForAttributeName("value1");
        assertEquals(String.class, b_value1.getAttributeClassification());

        ManyToManyMapping a_b = (ManyToManyMapping) descriptorA.getMappingForAttributeName("b");
        assertEquals(descriptorB, a_b.getReferenceDescriptor());
    }

    @Test
    public void createSimpleA() {
        DynamicEntity simpleInstance = aType.newDynamicEntity();
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
        DynamicEntity simpleInstance = bType.newDynamicEntity();
        simpleInstance.set("id", 1);
        simpleInstance.set("value1", "B1");

        UnitOfWork uow = session.acquireUnitOfWork();
        uow.registerNewObject(simpleInstance);
        uow.commit();

        ReportQuery countQuery = dynamicHelper.newReportQuery(bType.getName(), new ExpressionBuilder());
        countQuery.addCount();
        countQuery.setShouldReturnSingleValue(true);
        int simpleCount = ((Number) session.executeQuery(countQuery)).intValue();
        Assert.assertEquals(1, simpleCount);
    }

    @Test
    public void createAwithB() {
        DynamicEntity simpleInstanceB = bType.newDynamicEntity();
        simpleInstanceB.set("id", 1);
        simpleInstanceB.set("value1", "B2");

        DynamicEntity simpleInstanceA = aType.newDynamicEntity();
        simpleInstanceA.set("id", 1);
        simpleInstanceA.set("value1", "A2");
        simpleInstanceA.<Collection<DynamicEntity>>get("b").add(simpleInstanceA);

        UnitOfWork uow = session.acquireUnitOfWork();
        uow.registerNewObject(simpleInstanceB);
        uow.registerNewObject(simpleInstanceA);
        uow.commit();

        ReportQuery countQueryB = dynamicHelper.newReportQuery("SimpleB", new ExpressionBuilder());
        countQueryB.addCount();
        countQueryB.setShouldReturnSingleValue(true);
        int simpleCountB = ((Number) session.executeQuery(countQueryB)).intValue();
        Assert.assertEquals(1, simpleCountB);

        ReportQuery countQueryA = dynamicHelper.newReportQuery("SimpleA", new ExpressionBuilder());
        countQueryA.addCount();
        countQueryA.setShouldReturnSingleValue(true);
        int simpleCountA = ((Number) session.executeQuery(countQueryA)).intValue();
        Assert.assertEquals(1, simpleCountA);
    }

    @Ignore
    public void createAwithExistingB() {
        // TODO
    }

    @Test
    public void removeRelationshop() {
        createAwithB();

        UnitOfWork uow = session.acquireUnitOfWork();

        ReadObjectQuery roq = dynamicHelper.newReadObjectQuery("SimpleA");
        roq.setSelectionCriteria(roq.getExpressionBuilder().get("id").equal(1));
        DynamicEntity a = (DynamicEntity) session.executeQuery(roq);
        assertNotNull(a);

        List<DynamicEntity> bs = a.<List<DynamicEntity>> get("b");
        assertNotNull(bs);
        assertEquals(1, bs.size());
        bs.remove(0);

        uow.commit();
    }

    @Ignore
    public void addAtoB() {
        // TODO
    }

}