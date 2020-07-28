/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.jpa.dynamic.simple;

import static org.eclipse.persistence.testing.tests.jpa.dynamic.DynamicTestHelper.DYNAMIC_PERSISTENCE_NAME;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

//javase imports
import java.util.Calendar;

//java eXtensions
import javax.persistence.EntityManager;

//EclipseLink imports
import org.eclipse.persistence.dynamic.DynamicClassLoader;
import org.eclipse.persistence.dynamic.DynamicEntity;
import org.eclipse.persistence.dynamic.DynamicTypeBuilder;
import org.eclipse.persistence.jpa.dynamic.DynamicIdentityPolicy;
import org.eclipse.persistence.jpa.dynamic.JPADynamicHelper;
import org.eclipse.persistence.jpa.dynamic.JPADynamicTypeBuilder;
//domain-specific (testing) imports
import org.eclipse.persistence.testing.tests.jpa.dynamic.DynamicTestHelper;
//JUnit4 imports
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class SimpleTypeCompositeKeyTestSuite extends SimpleTypeTestSuite {

    @BeforeClass
    public static void setUp() throws Exception {
        emf = DynamicTestHelper.createEMF(DYNAMIC_PERSISTENCE_NAME);
        helper = new JPADynamicHelper(emf);
        DynamicClassLoader dcl = helper.getDynamicClassLoader();
        Class<?> javaType = dcl.createDynamicClass("model.Simple");
        DynamicTypeBuilder typeBuilder = new JPADynamicTypeBuilder(javaType, null, "SIMPLE_TYPE");
        typeBuilder.setPrimaryKeyFields("SID1", "SID2");
        typeBuilder.addDirectMapping("id1", int.class, "SID1");
        typeBuilder.addDirectMapping("id2", int.class, "SID2");
        typeBuilder.addDirectMapping("value1", String.class, "VAL_1");
        typeBuilder.addDirectMapping("value2", boolean.class, "VAL_2");
        typeBuilder.addDirectMapping("value3", Calendar.class, "VAL_3");
        typeBuilder.addDirectMapping("value4", Character.class, "VAL_4");
        helper.addTypes(true, true, typeBuilder.getType());
        simpleType = helper.getType("Simple");
    }

    @AfterClass
    public static void tearDown() throws Exception {
        if (emf != null && emf.isOpen()) {
            EntityManager em = emf.createEntityManager();
            em.getTransaction().begin();
            em.createNativeQuery("DROP TABLE SIMPLE_TYPE").executeUpdate();
            em.getTransaction().commit();
            em.close();
            emf.close();
        }
    }

    @Override
    @Test
    public void verifyConfig() throws Exception {
        super.verifyConfig();
        assertNotNull(simpleType.getDescriptor().getCMPPolicy());
        assertEquals(Object[].class,
            ((DynamicIdentityPolicy)simpleType.getDescriptor().getCMPPolicy()).getPKClass());
    }

    @Override
    protected void assertDefaultValues(DynamicEntity simpleInstance) {
        assertNotNull(simpleInstance);

        assertEquals("id1 not default value",
            0, simpleInstance.<Integer>get("id1").intValue());
        assertEquals("id2 not default value",
            0, simpleInstance.<Integer>get("id2").intValue());
        assertFalse("value1 set on new instance", simpleInstance.isSet("value1"));
        assertEquals("value2 not default value on new instance",
            false, simpleInstance.<Boolean>get("value2").booleanValue());
        assertFalse("value3 set on new instance", simpleInstance.isSet("value3"));
        assertFalse("value4 set on new instance", simpleInstance.isSet("value4"));
    }

    @Override
    public DynamicEntity createSimpleInstance(int id) {
        EntityManager em = emf.createEntityManager();
        assertNotNull(simpleType);
        DynamicEntity simpleInstance = simpleType.newDynamicEntity();
        simpleInstance.set("id1", id);
        simpleInstance.set("id2", id);
        simpleInstance.set("value2", true);
        assertEquals(0, ((Number)em.createQuery("SELECT COUNT(s) FROM Simple s").getSingleResult()).intValue());
        em.getTransaction().begin();
        em.persist(simpleInstance);
        em.getTransaction().commit();
        assertEquals(1, ((Number)em.createQuery("SELECT COUNT(s) FROM Simple s").getSingleResult()).intValue());
        DynamicEntity foundEntity = find(em, 1);
        assertNotNull(foundEntity);
        assertEquals(simpleInstance.<Number>get("id1"), foundEntity.<Number>get("id1"));
        assertEquals(simpleInstance.<Number>get("id2"), foundEntity.<Number>get("id2"));
        assertEquals(simpleInstance.get("value1"), foundEntity.get("value1"));
        assertEquals(simpleInstance.<Boolean>get("value2"), foundEntity.<Boolean>get("value2"));
        em.close();
        return simpleInstance;
    }

    @Override
    protected DynamicEntity find(EntityManager em, Object id) {
        return em.find(simpleType.getJavaClass(), new Object[] { id, id });
    }
}
