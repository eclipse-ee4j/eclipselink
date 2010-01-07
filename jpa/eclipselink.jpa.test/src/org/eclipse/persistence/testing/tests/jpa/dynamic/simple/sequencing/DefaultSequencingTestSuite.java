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
 *     dclarke - Dynamic Persistence INCUBATION - Enhancement 200045
 *               http://wiki.eclipse.org/EclipseLink/Development/Dynamic
 *     mnorman - tweaks to work from Ant command-line,
 *               get database properties from System, etc.
 *     
 * This code is being developed under INCUBATION and is not currently included 
 * in the automated EclipseLink build. The API in this code may change, or 
 * may never be included in the product. Please provide feedback through mailing 
 * lists or the bug database.
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
import org.eclipse.persistence.sequencing.TableSequence;

//domain-specific (testing) imports
import org.eclipse.persistence.testing.tests.jpa.dynamic.DynamicTestHelper;
import static org.eclipse.persistence.testing.tests.jpa.dynamic.DynamicTestHelper.DYNAMIC_PERSISTENCE_NAME;

public class DefaultSequencingTestSuite extends BaseSequencingTestSuite {

    @BeforeClass
    public static void setUp() {
        emf = DynamicTestHelper.createEMF(DYNAMIC_PERSISTENCE_NAME);
        boolean isMySQL = JpaHelper.getServerSession(emf).getDatasourcePlatform().
            getClass().getName().contains("MySQLPlatform");
        assumeTrue(isMySQL);
        helper = new JPADynamicHelper(emf);
        DynamicClassLoader dcl = helper.getDynamicClassLoader();
        Class<?> javaType = dcl.createDynamicClass("model.sequencing." + ENTITY_TYPE);
        DynamicTypeBuilder typeBuilder = new JPADynamicTypeBuilder(javaType, null, TABLE_NAME);
        typeBuilder.setPrimaryKeyFields("SID");
        typeBuilder.addDirectMapping("id", int.class, "SID");
        typeBuilder.addDirectMapping("value1", String.class, "VAL_1");
        TableSequence defaultSequence = (TableSequence)helper.getSession().getLogin().getDefaultSequence();
        defaultSequence.setTableName(SEQ_TABLE_NAME); 
        typeBuilder.configureSequencing(ENTITY_TYPE + "_SEQ", "SID");
        helper.addTypes(true, true, typeBuilder.getType());
    }

    @AfterClass
    public static void tearDown() {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.createNativeQuery("DROP TABLE " + TABLE_NAME).executeUpdate();
        em.createNativeQuery("DROP TABLE " + SEQ_TABLE_NAME).executeUpdate();
        em.getTransaction().commit();
        em.close();
        emf.close();
    }

    @Before
    public void clearSimpleTypeInstances() {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.createQuery("DELETE FROM " + ENTITY_TYPE).executeUpdate();
        em.createNativeQuery("UPDATE " + SEQ_TABLE_NAME + " SET SEQ_COUNT = 0").executeUpdate();
        em.getTransaction().commit();
        em.close();
        JpaHelper.getServerSession(emf).getSequencingControl().initializePreallocated();
    }
}