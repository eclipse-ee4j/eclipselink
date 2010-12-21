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
import java.util.HashMap;
import java.util.Vector;

//JUnit4 imports
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
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
import org.eclipse.persistence.exceptions.DynamicException;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.mappings.OneToManyMapping;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.queries.ReportQuery;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.UnitOfWork;

//domain-specific (testing) imports
import static org.eclipse.persistence.testing.tests.dynamic.DynamicTestingHelper.createSession;

public class SimpleTypes_OneToMany {

    //test fixtures
    static DatabaseSession session = null;
    static DynamicHelper dynamicHelper = null;
    
    @BeforeClass
    public static void setUp() {
        session = createSession();
        dynamicHelper = new DynamicHelper(session);
        DynamicClassLoader dcl = dynamicHelper.getDynamicClassLoader();
        // Create Dynamic Classes
        Class<?> simpleTypeA = dcl.createDynamicClass("simple.mappings.SimpleA");
        Class<?> simpleTypeB = dcl.createDynamicClass("simple.mappings.SimpleB");

        // Build dynamic types with mappings
        DynamicTypeBuilder aTypeBuilder = new DynamicTypeBuilder(simpleTypeA, null, "SIMPLE_TYPE_A");
        aTypeBuilder.setPrimaryKeyFields("SID");

        DynamicTypeBuilder bTypeBuilder = new DynamicTypeBuilder(simpleTypeB, null, "SIMPLE_TYPE_B");
        bTypeBuilder.setPrimaryKeyFields("SID");

        bTypeBuilder.addDirectMapping("id", int.class, "SID");
        bTypeBuilder.addDirectMapping("value1", String.class, "VAL_1");
        bTypeBuilder.addOneToOneMapping("a", aTypeBuilder.getType(), "A_FK");

        aTypeBuilder.addDirectMapping("id", int.class, "SID");
        aTypeBuilder.addDirectMapping("value1", String.class, "VAL_1");
        aTypeBuilder.addOneToManyMapping("b", bTypeBuilder.getType(), "A_FK");

        dynamicHelper.addTypes(true, true, aTypeBuilder.getType(), bTypeBuilder.getType());
    }
    
    @AfterClass
    public static void tearDown() {
        session.executeNonSelectingSQL("DROP TABLE SIMPLE_TYPE_B");
        session.executeNonSelectingSQL("DROP TABLE SIMPLE_TYPE_A");
        session.logout();
        session = null;
        dynamicHelper = null;
    }

    @After
    public void clearDynamicTables() {
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

        OneToManyMapping a_b = (OneToManyMapping) descriptorA.getMappingForAttributeName("b");
        assertEquals(descriptorB, a_b.getReferenceDescriptor());
    }

    @Test
    public void createSimpleA() {
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

        session.release();
    }

    @Test
    public void createSimpleB() {
        DynamicType simpleTypeB = dynamicHelper.getType("SimpleB");
        assertNotNull(simpleTypeB);

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
        assertEquals(1, simpleCount);

    }

    @Test
    public void createAwithB() {
        DynamicType simpleTypeA = dynamicHelper.getType("SimpleA");
        assertNotNull(simpleTypeA);
        DynamicType simpleTypeB = dynamicHelper.getType("SimpleB");
        assertNotNull(simpleTypeB);

        assertNotNull(session.getDescriptorForAlias("SimpleB"));

        DynamicEntity simpleInstanceB = simpleTypeB.newDynamicEntity();
        simpleInstanceB.set("id", 1);
        simpleInstanceB.set("value1", "B2");

        DynamicEntity simpleInstanceA = simpleTypeA.newDynamicEntity();
        simpleInstanceA.set("id", 1);
        simpleInstanceA.set("value1", "A2");
        simpleInstanceA.<Collection<DynamicEntity>> get("b").add(simpleInstanceB);

        simpleInstanceB.set("a", simpleInstanceA);

        UnitOfWork uow = session.acquireUnitOfWork();
        uow.registerNewObject(simpleInstanceB);
        uow.registerNewObject(simpleInstanceA);
        uow.commit();

        ReportQuery countQuery = dynamicHelper.newReportQuery("SimpleB", new ExpressionBuilder());
        countQuery.addCount();
        countQuery.setShouldReturnSingleValue(true);
        int simpleCountB = ((Number) session.executeQuery(countQuery)).intValue();
        assertEquals(1, simpleCountB);

        countQuery = dynamicHelper.newReportQuery("SimpleA", new ExpressionBuilder());
        countQuery.addCount();
        countQuery.setShouldReturnSingleValue(true);
        int simpleCountA = ((Number) session.executeQuery(countQuery)).intValue();
        assertEquals(1, simpleCountA);

        session.release();
    }

    @Test
    public void removeAwithB_PrivateOwned() {
        createAwithB();

        DynamicType simpleAType = dynamicHelper.getType("SimpleA");
        ((OneToManyMapping) simpleAType.getDescriptor().getMappingForAttributeName("b")).setIsPrivateOwned(true);
        UnitOfWork uow = session.acquireUnitOfWork();

        ReadObjectQuery findQuery = dynamicHelper.newReadObjectQuery("SimpleA");
        findQuery.setSelectionCriteria(findQuery.getExpressionBuilder().get("id").equal(1));
        DynamicEntity a = (DynamicEntity) uow.executeQuery(findQuery);

        assertNotNull(a);
        ReportQuery countQuery = dynamicHelper.newReportQuery("SimpleB", new ExpressionBuilder());
        countQuery.addCount();
        countQuery.setShouldReturnSingleValue(true);
        int simpleCountB = ((Number) session.executeQuery(countQuery)).intValue();
        assertEquals(1, simpleCountB);
        countQuery = dynamicHelper.newReportQuery("SimpleA", new ExpressionBuilder());
        countQuery.addCount();
        countQuery.setShouldReturnSingleValue(true);
        int simpleCountA = ((Number) session.executeQuery(countQuery)).intValue();
        assertEquals(1, simpleCountA);

        uow.deleteObject(a);
        // em.remove(a.get("b", List.class).get(0));

        uow.commit();

        countQuery = dynamicHelper.newReportQuery("SimpleB", new ExpressionBuilder());
        countQuery.addCount();
        countQuery.setShouldReturnSingleValue(true);
        simpleCountB = ((Number) session.executeQuery(countQuery)).intValue();
        assertEquals(0, simpleCountB);
        countQuery = dynamicHelper.newReportQuery("SimpleA", new ExpressionBuilder());
        countQuery.addCount();
        countQuery.setShouldReturnSingleValue(true);
        simpleCountA = ((Number) session.executeQuery(countQuery)).intValue();
        assertEquals(0, simpleCountA);

    }

    @Test
    public void verifyNewSimpleA() throws Exception {
        DynamicEntity newA = dynamicHelper.getType("SimpleA").newDynamicEntity();
        assertNotNull(newA);
        assertEquals(newA.get("id"), 0);
        assertFalse(newA.isSet("value1"));
        assertEquals(Vector.class, newA.get("b").getClass());
        Object b = newA.get("b");
        assertNotNull(b);
        assertTrue(b instanceof Collection);
    }

    @Test(expected=DynamicException.class)
    public void verifyNewSimpleA_InvalidB_Map() throws Exception {
        DynamicEntity newA = dynamicHelper.getType("SimpleA").newDynamicEntity();
        newA.set("b", new HashMap());
    }

    @Test(expected=DynamicException.class)
    public void verifyNewSimpleA_InvalidB_Object() throws Exception {
        DynamicEntity newA = dynamicHelper.getType("SimpleA").newDynamicEntity();
        newA.set("b", new Object());
    }

    @Test(expected=DynamicException.class)
    public void verifyNewSimpleA_InvalidB_A() throws Exception {
        DynamicEntity newA = dynamicHelper.getType("SimpleA").newDynamicEntity();
        newA.set("b", dynamicHelper.getType("SimpleA").newDynamicEntity());
    }

    @Test(expected=DynamicException.class)
    public void verifyNewSimpleA_InvalidB_B() throws Exception {
        DynamicEntity newA = dynamicHelper.getType("SimpleA").newDynamicEntity();
        newA.set("b", dynamicHelper.getType("SimpleB").newDynamicEntity());
    }

    @Test(expected=DynamicException.class)
    public void verifyNewSimpleA_InvalidB_NULL() throws Exception {
        DynamicEntity newA = dynamicHelper.getType("SimpleA").newDynamicEntity();
        newA.set("b", null);
    }
    
    @Test
    public void createAwithExistingB() {
        // TODO Assert.fail("Not Yet Implemented");
    }

    @Test
    public void removeBfromA() {
        // TODO Assert.fail("Not Yet Implemented");
    }

    @Test
    public void addAtoB() {
        // TODO Assert.fail("Not Yet Implemented");
    }

}