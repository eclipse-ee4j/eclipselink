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
//     dclarke - Dynamic Persistence
//       http://wiki.eclipse.org/EclipseLink/Development/Dynamic
//       (https://bugs.eclipse.org/bugs/show_bug.cgi?id=200045)
//     mnorman - tweaks to work from Ant command-line,
//               get database properties from System, etc.
//
package org.eclipse.persistence.testing.tests.jpa.dynamic.simple.sequencing;

//java eXtension imports

import jakarta.persistence.EntityManager;
import org.eclipse.persistence.dynamic.DynamicClassLoader;
import org.eclipse.persistence.dynamic.DynamicTypeBuilder;
import org.eclipse.persistence.jpa.JpaHelper;
import org.eclipse.persistence.jpa.dynamic.JPADynamicHelper;
import org.eclipse.persistence.jpa.dynamic.JPADynamicTypeBuilder;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.platform.database.DatabasePlatform;
import org.eclipse.persistence.sequencing.NativeSequence;
import org.eclipse.persistence.sessions.server.Server;
import org.eclipse.persistence.testing.tests.jpa.dynamic.DynamicTestHelper;
import org.eclipse.persistence.tools.schemaframework.SchemaManager;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import static org.eclipse.persistence.testing.tests.jpa.dynamic.DynamicTestHelper.DYNAMIC_PERSISTENCE_NAME;
import static org.junit.Assume.assumeTrue;

public class NativeSequencingTestSuite extends BaseSequencingTestSuite {

    @BeforeClass
    public static void setUp() {
        emf = DynamicTestHelper.createEMF(DYNAMIC_PERSISTENCE_NAME);
        final DatabasePlatform platform = JpaHelper.getServerSession(emf).getPlatform();
        if (!platform.isMySQL()) {
            JpaHelper.getServerSession(emf).getSessionLog().log(SessionLog.WARNING,
                    NativeSequencingTestSuite.class.getName() + " requires MySQL.");
        }
        assumeTrue(platform.isMySQL());
        helper = new JPADynamicHelper(emf);
        DynamicClassLoader dcl = helper.getDynamicClassLoader();
        Class<?> javaType = dcl.createDynamicClass("model.sequencing." + ENTITY_TYPE);
        DynamicTypeBuilder typeBuilder = new JPADynamicTypeBuilder(javaType, null, TABLE_NAME);
        typeBuilder.setPrimaryKeyFields("SID");
        typeBuilder.addDirectMapping("id", int.class, "SID");
        typeBuilder.addDirectMapping("value1", String.class, "VAL_1");
        NativeSequence sequence = new NativeSequence();
        sequence.setPreallocationSize(1);
        helper.getSession().getProject().getLogin().setDefaultSequence(sequence);
        sequence.onConnect(helper.getSession().getPlatform());
        typeBuilder.configureSequencing(sequence, ENTITY_TYPE + "_SEQ", "SID");

        helper.addTypes(true, true, typeBuilder.getType());
    }

    @AfterClass
    public static void tearDown() {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        try{
            em.createNativeQuery("DROP TABLE " + TABLE_NAME).executeUpdate();
            em.getTransaction().commit();
        } catch (Exception e){}
        em.close();
        emf.close();
    }

    @Before
    public void clearSimpleTypeInstances() {
        final DatabasePlatform platform = JpaHelper.getServerSession(emf).getPlatform();
        if (!platform.isMySQL()) {
            JpaHelper.getServerSession(emf).getSessionLog().log(SessionLog.WARNING,
                    NativeSequencingTestSuite.class.getName() + " requires MySQL.");
        }
        assumeTrue(platform.isMySQL());
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.createQuery("DELETE FROM " + ENTITY_TYPE).executeUpdate();
        em.createNativeQuery("ALTER TABLE " + TABLE_NAME + " DROP COLUMN SID").executeUpdate();
        em.createNativeQuery("ALTER TABLE " + TABLE_NAME + " ADD COLUMN SID INTEGER PRIMARY KEY AUTO_INCREMENT FIRST").executeUpdate();
        em.close();
        Server session = JpaHelper.getServerSession(emf);
        new SchemaManager(session).replaceSequences();
        session.getSequencingControl().initializePreallocated();
    }

}
