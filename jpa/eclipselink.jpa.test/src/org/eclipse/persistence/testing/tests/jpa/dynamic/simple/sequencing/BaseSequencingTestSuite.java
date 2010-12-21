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
package org.eclipse.persistence.testing.tests.jpa.dynamic.simple.sequencing;

//java eXtension imports
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

//JUnit4 imports
import org.junit.Assert;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

//EclipseLink imports
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.dynamic.DynamicEntity;
import org.eclipse.persistence.dynamic.DynamicType;
import org.eclipse.persistence.internal.dynamic.DynamicTypeImpl;
import org.eclipse.persistence.jpa.JpaHelper;
import org.eclipse.persistence.jpa.dynamic.JPADynamicHelper;
import org.eclipse.persistence.sessions.IdentityMapAccessor;
import org.eclipse.persistence.sessions.server.Server;

//domain-specific (testing) imports

public abstract class BaseSequencingTestSuite  {

    static final String TABLE_NAME = "SIMPLE_TABLE_SEQ";
    public static final String SEQ_TABLE_NAME = "TEST_SEQ";
    static final String ENTITY_TYPE = "Simple";
    
    //test fixtures
    static EntityManagerFactory emf;
    static JPADynamicHelper helper;
    
    @Test
    public void verifyConfig() throws Exception {
        Server session = JpaHelper.getServerSession(emf);
        ClassDescriptor descriptor = session.getClassDescriptorForAlias(ENTITY_TYPE);
        assertNotNull("No descriptor found for alias: " + ENTITY_TYPE, descriptor);
        DynamicTypeImpl simpleType = (DynamicTypeImpl)helper.getType(ENTITY_TYPE);
        assertNotNull("EntityType not found for alias: " + ENTITY_TYPE, simpleType);
        assertEquals(descriptor, simpleType.getDescriptor());
    }

    @Test
    public void createSingleInstances() {
        Server session = JpaHelper.getServerSession(emf);
        DynamicTypeImpl simpleType = (DynamicTypeImpl)helper.getType(ENTITY_TYPE);
        EntityManager em = emf.createEntityManager();
        DynamicEntity simpleInstance = createSimpleInstance(1);
        int simpleCount = ((Number)em.createQuery("SELECT COUNT(o) FROM " + ENTITY_TYPE + " o").getSingleResult()).intValue();
        assertEquals(1, simpleCount);
        IdentityMapAccessor cache = session.getIdentityMapAccessor();
        assertTrue(cache.containsObjectInIdentityMap(simpleInstance));
        em.clear();
        cache.initializeAllIdentityMaps();
        DynamicEntity findResult = (DynamicEntity)em.find(simpleType.getJavaClass(), 1);
        assertNotNull(findResult);
        assertEquals(simpleInstance.get("id"), findResult.get("id"));
        assertEquals(simpleInstance.get("value1"), findResult.get("value1"));
        em.close();
    }

    @Test
    public void createTwoInstances() {
        EntityManager em = emf.createEntityManager();
        DynamicTypeImpl simpleType = (DynamicTypeImpl)helper.getType(ENTITY_TYPE);
        DynamicEntity simpleInstance1 = createSimpleInstance(1);
        DynamicEntity simpleInstance2 = createSimpleInstance(2);
        int simpleCount = 
            ((Number)em.createQuery("SELECT COUNT(o) FROM " + ENTITY_TYPE + " o").getSingleResult()).intValue();
        assertEquals(2, simpleCount);
        IdentityMapAccessor cache = helper.getSession().getIdentityMapAccessor();
        assertTrue(cache.containsObjectInIdentityMap(simpleInstance1));
        assertTrue(cache.containsObjectInIdentityMap(simpleInstance2));
        em.clear();
        cache.initializeAllIdentityMaps();
        DynamicEntity findResult1 = (DynamicEntity)em.find(simpleType.getJavaClass(), 1);
        DynamicEntity findResult2 = (DynamicEntity)em.find(simpleType.getJavaClass(), 2);
        assertNotNull(findResult1);
        assertNotNull(findResult2);
        assertEquals(simpleInstance1.get("id"), findResult1.get("id"));
        assertEquals(simpleInstance2.get("value1"), findResult2.get("value1"));
        em.close();
    }

    public DynamicEntity createSimpleInstance(int expectedId) {
        EntityManager em = emf.createEntityManager();
        DynamicType simpleEntityType = helper.getType(ENTITY_TYPE);
        Assert.assertNotNull(simpleEntityType);
        DynamicEntity simpleInstance = simpleEntityType.newDynamicEntity();
        simpleInstance.set("value1", TABLE_NAME);
        em.getTransaction().begin();
        assertEquals(0, simpleInstance.get("id"));
        em.persist(simpleInstance);
        em.getTransaction().commit();
        // test after commit - in case sequencing involves round-trip to DB
        assertEquals(expectedId, simpleInstance.get("id"));
        em.close();
        return simpleInstance;
    }
    
}