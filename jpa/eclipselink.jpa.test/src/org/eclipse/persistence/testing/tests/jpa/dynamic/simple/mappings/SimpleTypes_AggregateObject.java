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
package org.eclipse.persistence.testing.tests.jpa.dynamic.simple.mappings;

//javase imports

//java eXtensions
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

//JUnit4 imports
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertSame;
import static org.junit.Assume.assumeTrue;

//EclipseLink imports
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.dynamic.DynamicClassLoader;
import org.eclipse.persistence.dynamic.DynamicEntity;
import org.eclipse.persistence.dynamic.DynamicType;
import org.eclipse.persistence.internal.descriptors.changetracking.AggregateAttributeChangeListener;
import org.eclipse.persistence.internal.dynamic.DynamicEntityImpl;
import org.eclipse.persistence.internal.dynamic.DynamicTypeImpl;
import org.eclipse.persistence.jpa.JpaHelper;
import org.eclipse.persistence.jpa.dynamic.JPADynamicHelper;
import org.eclipse.persistence.jpa.dynamic.JPADynamicTypeBuilder;
import org.eclipse.persistence.mappings.AggregateObjectMapping;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.testing.tests.jpa.dynamic.DynamicTestHelper;
import static org.eclipse.persistence.testing.tests.jpa.dynamic.DynamicTestHelper.DYNAMIC_PERSISTENCE_NAME;

public class SimpleTypes_AggregateObject {

    //test fixtures
    static EntityManagerFactory emf = null;
    static JPADynamicHelper helper = null;
    
    @BeforeClass
    public static void setUp() {
        emf = DynamicTestHelper.createEMF(DYNAMIC_PERSISTENCE_NAME);
        boolean isMySQL = JpaHelper.getServerSession(emf).getDatasourcePlatform().
            getClass().getName().contains("MySQLPlatform");
        assumeTrue(isMySQL);
        helper = new JPADynamicHelper(emf);
        DynamicClassLoader dcl = helper.getDynamicClassLoader();
        Class<?> simpleTypeB = dcl.createDynamicClass("model.SimpleB");
        JPADynamicTypeBuilder bFactory = new JPADynamicTypeBuilder(simpleTypeB, null);
        bFactory.addDirectMapping("value2", boolean.class, "VAL_2");
        bFactory.addDirectMapping("value3", String.class, "VAL_3");
        Class<?> simpleTypeC = dcl.createDynamicClass("model.SimpleC");
        JPADynamicTypeBuilder cFactory = new JPADynamicTypeBuilder(simpleTypeC, null);
        cFactory.addDirectMapping("value4", double.class, "VAL_4");
        cFactory.addDirectMapping("value5", String.class, "VAL_5");
        Class<?> simpleTypeA = dcl.createDynamicClass("model.SimpleA");
        JPADynamicTypeBuilder aFactory = new JPADynamicTypeBuilder(simpleTypeA, null, "SIMPLE_TYPE_A");
        aFactory.setPrimaryKeyFields("SID");
        aFactory.addDirectMapping("id", int.class, "SID");
        aFactory.addDirectMapping("value1", String.class, "VAL_1");
        aFactory.addAggregateObjectMapping("b", bFactory.getType(), true);
        aFactory.addAggregateObjectMapping("c", cFactory.getType(), false);

        helper.addTypes(true, true, aFactory.getType(), bFactory.getType(), cFactory.getType());
    }

    @AfterClass
    public static void tearDown() {
        if (emf != null && emf.isOpen()) {
            EntityManager em = emf.createEntityManager();
            em.getTransaction().begin();
            em.createNativeQuery("DROP TABLE SIMPLE_TYPE_A").executeUpdate();
            em.getTransaction().commit();
            em.close();
            emf.close();
        }
    }

    @After
    public void clearDynamicTables() {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.createQuery("DELETE FROM SimpleA").executeUpdate();
        em.getTransaction().commit();
        em.close();
    }

    @Test
    public void verifyConfig() throws Exception {
        ClassDescriptor descriptorA = helper.getSession().getClassDescriptorForAlias("SimpleA");
        assertNotNull("No descriptor found for alias='SimpleA'", descriptorA);
        DynamicTypeImpl simpleTypeA = (DynamicTypeImpl)helper.getType("SimpleA");
        assertNotNull("'SimpleA' EntityType not found", simpleTypeA);
        assertEquals(descriptorA, simpleTypeA.getDescriptor());
        DirectToFieldMapping a_id = (DirectToFieldMapping)descriptorA.getMappingForAttributeName("id");
        assertEquals(int.class, a_id.getAttributeClassification());
        DirectToFieldMapping a_value1 = (DirectToFieldMapping) descriptorA.getMappingForAttributeName("value1");
        assertEquals(String.class, a_value1.getAttributeClassification());
        ClassDescriptor descriptorB = helper.getSession().getClassDescriptorForAlias("SimpleB");
        assertNotNull("No descriptor found for alias='SimpleB'", descriptorB);
        DynamicTypeImpl simpleTypeB = (DynamicTypeImpl)helper.getType("SimpleB");
        assertNotNull("'SimpleB' EntityType not found", simpleTypeB);
        assertEquals(descriptorB, simpleTypeB.getDescriptor());
        DirectToFieldMapping b_value2 = (DirectToFieldMapping)descriptorB.getMappingForAttributeName("value2");
        assertEquals(boolean.class, b_value2.getAttributeClassification());
        DirectToFieldMapping b_value3 = (DirectToFieldMapping)descriptorB.getMappingForAttributeName("value3");
        assertEquals(String.class, b_value3.getAttributeClassification());
        assertTrue(descriptorB.isAggregateDescriptor());
        AggregateObjectMapping a_b = (AggregateObjectMapping)descriptorA.getMappingForAttributeName("b");
        assertSame(descriptorB.getJavaClass(), a_b.getReferenceDescriptor().getJavaClass());
        assertTrue(a_b.isNullAllowed());
        ClassDescriptor descriptorC = helper.getSession().getClassDescriptorForAlias("SimpleC");
        assertNotNull("No descriptor found for alias='SimpleB'", descriptorB);
        DynamicTypeImpl simpleTypeC = (DynamicTypeImpl)helper.getType("SimpleC");
        assertNotNull("'SimpleC' EntityType not found", simpleTypeC);
        assertEquals(descriptorB, simpleTypeB.getDescriptor());
        DirectToFieldMapping c_value4 = (DirectToFieldMapping)descriptorC.getMappingForAttributeName("value4");
        assertEquals(double.class, c_value4.getAttributeClassification());
        DirectToFieldMapping c_value5 = (DirectToFieldMapping)descriptorC.getMappingForAttributeName("value5");
        assertEquals(String.class, c_value5.getAttributeClassification());
        assertTrue(descriptorB.isAggregateDescriptor());
        AggregateObjectMapping a_c = (AggregateObjectMapping)descriptorA.getMappingForAttributeName("c");
        assertSame(descriptorC.getJavaClass(), a_c.getReferenceDescriptor().getJavaClass());
        assertFalse(a_c.isNullAllowed());
    }

    @Test
    public void verifyProperties() {
        DynamicTypeImpl simpleTypeA = (DynamicTypeImpl)helper.getType("SimpleA");
        assertNotNull(simpleTypeA);
        assertEquals(4, simpleTypeA.getNumberOfProperties());
        assertEquals("id", simpleTypeA.getPropertiesNames().get(0));
        assertEquals("value1", simpleTypeA.getPropertiesNames().get(1));
        assertEquals("b", simpleTypeA.getPropertiesNames().get(2));
        assertEquals("c", simpleTypeA.getPropertiesNames().get(3));
    }

    @Test
    public void createSimpleA() {
        DynamicTypeImpl simpleTypeA = (DynamicTypeImpl)helper.getType("SimpleA");
        assertNotNull(simpleTypeA);
        DynamicEntity a = simpleTypeA.newDynamicEntity();
        assertNotNull(a);
        assertEquals(a.get("id"),0);
        assertFalse(a.isSet("id"));
        assertFalse(a.isSet("value1"));
        assertFalse(a.isSet("b"));
        DynamicType typeC = helper.getType("SimpleC");
        assertEquals(a.get("c").getClass(), typeC.newDynamicEntity().getClass());
        DynamicEntity c = a.<DynamicEntity>get("c");
        assertNotNull(c);
        assertEquals(c.get("value4"), 0.0);
        assertFalse(c.isSet("value5"));
    }

    @Test
    public void persistSimpleA() {
        DynamicTypeImpl simpleTypeA = (DynamicTypeImpl)helper.getType("SimpleA");
        assertNotNull(simpleTypeA);
        EntityManager em = emf.createEntityManager();
        DynamicEntity simpleInstance = simpleTypeA.newDynamicEntity();
        simpleInstance.set("id", 1);
        simpleInstance.set("value1", "A1");
        em.getTransaction().begin();
        em.persist(simpleInstance);
        em.getTransaction().commit();
        int simpleCount = ((Number) em.createQuery("SELECT COUNT(s) FROM SimpleA s").getSingleResult()).intValue();
        assertEquals(1, simpleCount);
        em.close();
    }

    @Test
    public void verifyChangTracking() {
        persistSimpleA();
        DynamicTypeImpl simpleTypeA = (DynamicTypeImpl)helper.getType("SimpleA");
        assertNotNull(simpleTypeA);
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        DynamicEntityImpl a = (DynamicEntityImpl)em.find(simpleTypeA.getJavaClass(), 1);
        assertNotNull(a);
        assertNotNull(a._persistence_getPropertyChangeListener());
        DynamicEntityImpl c = a.<DynamicEntityImpl>get("c");
        assertNotNull(c);
        assertNotNull(c._persistence_getPropertyChangeListener());
        assertTrue(c._persistence_getPropertyChangeListener() instanceof AggregateAttributeChangeListener);
        em.getTransaction().rollback();
        em.close();
    }

    @Test
    public void createSimpleAwithSimpleB() {
        DynamicType simpleTypeA = helper.getType("SimpleA");
        assertNotNull(simpleTypeA);
        DynamicType simpleTypeB = helper.getType("SimpleB");
        assertNotNull(simpleTypeB);
        EntityManager em = emf.createEntityManager();
        assertNotNull(helper.getSession().getDescriptorForAlias("SimpleB"));
        DynamicEntity simpleInstanceB = simpleTypeB.newDynamicEntity();
        simpleInstanceB.set("value2", true);
        simpleInstanceB.set("value3", "B2");
        DynamicEntity simpleInstanceA = simpleTypeA.newDynamicEntity();
        simpleInstanceA.set("id", 2);
        simpleInstanceA.set("value1", "A2");
        simpleInstanceA.set("b", simpleInstanceB);
        em.getTransaction().begin();
        em.persist(simpleInstanceA);
        em.getTransaction().commit();
        int simpleCountA = ((Number)em.createQuery("SELECT COUNT(s) FROM SimpleA s").getSingleResult()).intValue();
        assertEquals(1, simpleCountA);
        em.close();
    }
}