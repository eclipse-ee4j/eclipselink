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
package org.eclipse.persistence.testing.tests.jpa.dynamic.simple.sequencing;

//java eXtension imports
import javax.persistence.EntityManager;

//JUnit4 imports
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import static org.junit.Assume.assumeTrue;

//EclipseLink imports
import org.eclipse.persistence.dynamic.DynamicClassLoader;
import org.eclipse.persistence.dynamic.DynamicTypeBuilder;
import org.eclipse.persistence.jpa.JpaHelper;
import org.eclipse.persistence.jpa.dynamic.JPADynamicHelper;
import org.eclipse.persistence.jpa.dynamic.JPADynamicTypeBuilder;
import org.eclipse.persistence.sequencing.UnaryTableSequence;

//domain-specific (testing) imports
import org.eclipse.persistence.testing.tests.jpa.dynamic.DynamicTestHelper;
import static org.eclipse.persistence.testing.tests.jpa.dynamic.DynamicTestHelper.DYNAMIC_PERSISTENCE_NAME;

public class UnaryTableSequencingTestSuite extends BaseSequencingTestSuite {

    @BeforeClass
    public static void setUp() {
        emf = DynamicTestHelper.createEMF(DYNAMIC_PERSISTENCE_NAME);
        boolean isMySQL = JpaHelper.getServerSession(emf).getDatasourcePlatform().
            getClass().getName().contains("MySQLPlatform");
        assumeTrue(isMySQL);
        helper = new JPADynamicHelper(emf);
        DynamicClassLoader dcl = helper.getDynamicClassLoader();
        UnaryTableSequence sequence = new UnaryTableSequence(SEQ_TABLE_NAME);
        sequence.setCounterFieldName("SEQ_VALUE");
        sequence.setPreallocationSize(5);
        helper.getSession().getProject().getLogin().setDefaultSequence(sequence);
        sequence.onConnect(helper.getSession().getPlatform());
        Class<?> javaType = dcl.createDynamicClass("model.sequencing." + ENTITY_TYPE);
        DynamicTypeBuilder typeBuilder = new JPADynamicTypeBuilder(javaType, null, TABLE_NAME);
        typeBuilder.setPrimaryKeyFields("SID");
        typeBuilder.addDirectMapping("id", int.class, "SID");
        typeBuilder.addDirectMapping("value1", String.class, "VAL_1");
        typeBuilder.configureSequencing(sequence, SEQ_TABLE_NAME, "SID");
        helper.addTypes(true, true, typeBuilder.getType());
    }

    @Before
    public void clearSimpleTypeInstances() {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.createQuery("DELETE FROM " + ENTITY_TYPE).executeUpdate();
        em.createNativeQuery("UPDATE TEST_SEQ SET SEQ_VALUE = 0").executeUpdate();
        em.getTransaction().commit();
        em.close();

        JpaHelper.getServerSession(emf).getSequencingControl().initializePreallocated();
    }

    @AfterClass
    public static void shutdown() {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.createNativeQuery("DROP TABLE " + TABLE_NAME).executeUpdate();
        em.createNativeQuery("DROP TABLE " + SEQ_TABLE_NAME).executeUpdate();
        em.getTransaction().commit();
        em.close();
        emf.close();
    }
}