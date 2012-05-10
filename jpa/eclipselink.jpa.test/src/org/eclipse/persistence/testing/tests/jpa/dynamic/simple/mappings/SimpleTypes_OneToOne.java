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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assume.assumeTrue;

//EclipseLink imports
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.dynamic.DynamicClassLoader;
import org.eclipse.persistence.dynamic.DynamicEntity;
import org.eclipse.persistence.dynamic.DynamicType;
import org.eclipse.persistence.dynamic.DynamicTypeBuilder;
import org.eclipse.persistence.exceptions.DynamicException;
import org.eclipse.persistence.jpa.JpaHelper;
import org.eclipse.persistence.jpa.dynamic.JPADynamicHelper;
import org.eclipse.persistence.jpa.dynamic.JPADynamicTypeBuilder;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.mappings.OneToOneMapping;
import org.eclipse.persistence.testing.tests.jpa.dynamic.DynamicTestHelper;
import static org.eclipse.persistence.testing.tests.jpa.dynamic.DynamicTestHelper.DYNAMIC_PERSISTENCE_NAME;

public class SimpleTypes_OneToOne {

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
        DynamicTypeBuilder bFactory = new JPADynamicTypeBuilder(simpleTypeB, null, "SIMPLE_TYPE_B");
        bFactory.setPrimaryKeyFields("SID");
        bFactory.addDirectMapping("id", int.class, "SID");
        bFactory.addDirectMapping("value1", String.class, "VAL_1");
        Class<?> simpleTypeA = dcl.createDynamicClass("model.SimpleA");
        DynamicTypeBuilder aFactory = new JPADynamicTypeBuilder(simpleTypeA, null, "SIMPLE_TYPE_A");
        aFactory.setPrimaryKeyFields("SID");
        aFactory.addDirectMapping("id", int.class, "SID");
        aFactory.addDirectMapping("value1", String.class, "VAL_1");
        aFactory.addOneToOneMapping("b", bFactory.getType(), "B_FK").setCascadePersist(true);
        helper.addTypes(true, true, aFactory.getType(), bFactory.getType());
    }

    @AfterClass
    public static void shutdown() {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.createNativeQuery("DROP TABLE SIMPLE_TYPE_A").executeUpdate();
        em.createNativeQuery("DROP TABLE SIMPLE_TYPE_B").executeUpdate();
        em.getTransaction().commit();
        em.close();
        emf.close();
    }

    @After
    public void clearDynamicTables() {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.createQuery("DELETE FROM SimpleA").executeUpdate();
        em.createQuery("DELETE FROM SimpleB").executeUpdate();
        em.getTransaction().commit();
        em.close();
    }

    @Test
    public void verifyConfig() throws Exception {
        ClassDescriptor descriptorA = helper.getSession().getClassDescriptorForAlias("SimpleA");
        assertNotNull("No descriptor found for alias='SimpleA'", descriptorA);
        DynamicType simpleTypeA = helper.getType("SimpleA");
        assertNotNull("'SimpleA' EntityType not found", simpleTypeA);
        assertEquals(descriptorA, simpleTypeA.getDescriptor());
        DirectToFieldMapping a_id = (DirectToFieldMapping)descriptorA.getMappingForAttributeName("id");
        assertEquals(int.class, a_id.getAttributeClassification());
        DirectToFieldMapping a_value1 = (DirectToFieldMapping)descriptorA.getMappingForAttributeName("value1");
        assertEquals(String.class, a_value1.getAttributeClassification());
        ClassDescriptor descriptorB = helper.getSession().getClassDescriptorForAlias("SimpleB");
        assertNotNull("No descriptor found for alias='SimpleB'", descriptorB);
        DynamicType simpleTypeB = helper.getType("SimpleB");
        assertNotNull("'SimpleB' EntityType not found", simpleTypeB);
        assertEquals(descriptorB, simpleTypeB.getDescriptor());
        DirectToFieldMapping b_id = (DirectToFieldMapping)descriptorB.getMappingForAttributeName("id");
        assertEquals(int.class, b_id.getAttributeClassification());
        DirectToFieldMapping b_value1 = (DirectToFieldMapping)descriptorB.getMappingForAttributeName("value1");
        assertEquals(String.class, b_value1.getAttributeClassification());
        OneToOneMapping a_b = (OneToOneMapping)descriptorA.getMappingForAttributeName("b");
        assertEquals(descriptorB, a_b.getReferenceDescriptor());
    }

    @Test
    public void createSimpleA() {
        DynamicType simpleTypeA = helper.getType("SimpleA");
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
    public void createSimpleB() {
        DynamicType simpleTypeB = helper.getType("SimpleB");
        assertNotNull(simpleTypeB);
        EntityManager em = emf.createEntityManager();
        DynamicEntity simpleInstance = simpleTypeB.newDynamicEntity();
        simpleInstance.set("id", 1);
        simpleInstance.set("value1", "B1");
        em.getTransaction().begin();
        em.persist(simpleInstance);
        em.getTransaction().commit();
        int simpleCount = ((Number) em.createQuery("SELECT COUNT(s) FROM SimpleB s").getSingleResult()).intValue();
        assertEquals(1, simpleCount);
        em.close();
    }

    @Test
    public void createSimpleAwithSimpleB() {
        DynamicType simpleTypeA = helper.getType("SimpleA");
        assertNotNull(simpleTypeA);
        DynamicType simpleTypeB = helper.getType("SimpleB");
        assertNotNull(simpleTypeB);
        EntityManager em = emf.createEntityManager();
        assertNotNull(JpaHelper.getServerSession(emf).getDescriptorForAlias("SimpleB"));
        DynamicEntity simpleInstanceB = simpleTypeB.newDynamicEntity();
        simpleInstanceB.set("id", 2);
        simpleInstanceB.set("value1", "B2");
        DynamicEntity simpleInstanceA = simpleTypeA.newDynamicEntity();
        simpleInstanceA.set("id", 2);
        simpleInstanceA.set("value1", "A2");
        simpleInstanceA.set("b", simpleInstanceB);
        em.getTransaction().begin();
        em.persist(simpleInstanceB);
        em.persist(simpleInstanceA);
        em.getTransaction().commit();
        int simpleCountB = ((Number) em.createQuery("SELECT COUNT(s) FROM SimpleB s").getSingleResult()).intValue();
        assertEquals(1, simpleCountB);
        int simpleCountA = ((Number) em.createQuery("SELECT COUNT(s) FROM SimpleA s").getSingleResult()).intValue();
        assertEquals(1, simpleCountA);
        em.close();
    }

    @Test(expected=DynamicException.class)
    public void invalidTypeSet() {
        DynamicType simpleTypeA = helper.getType("SimpleA");
        assertNotNull(simpleTypeA);
        DynamicType simpleTypeB = helper.getType("SimpleB");
        assertNotNull(simpleTypeB);
        assertNotNull(JpaHelper.getServerSession(emf).getDescriptorForAlias("SimpleB"));
        DynamicEntity simpleInstanceB = simpleTypeB.newDynamicEntity();
        simpleInstanceB.set("id", 2);
        simpleInstanceB.set("value1", "B2");
        DynamicEntity simpleInstanceA = simpleTypeA.newDynamicEntity();
        simpleInstanceA.set("id", 2);
        simpleInstanceA.set("value1", "A2");
        simpleInstanceA.set("b", simpleInstanceA);
    }
}