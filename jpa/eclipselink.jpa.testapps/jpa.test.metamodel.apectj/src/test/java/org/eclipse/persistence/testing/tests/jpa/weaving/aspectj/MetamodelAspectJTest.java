/*
 * Copyright (c) 2023 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation
package org.eclipse.persistence.testing.tests.jpa.weaving.aspectj;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.metamodel.Metamodel;
import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.testing.framework.jpa.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.weaving.aspectj.ItemAspectJ;

/**
 * This test is to verify that managed classes like Entities, Entity Listeners weaved by AspectJ will be functional.
 * Test for bugfix: java.lang.StackOverflowError after bumping from 4.0.0 to 4.0.1 #1832 - https://github.com/eclipse-ee4j/eclipselink/issues/1832
 * There was issue to get some class metadata through ASM org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAsmFactory.MetadataMethodVisitor*
 * Except entityManagerFactory.getMetamodel() there are some JPA basic operations called (merge(), find(), remove()).
 */
public class MetamodelAspectJTest extends JUnitTestCase {

    public static final String PERSISTENCE_UNIT_NAME = "metamodel1_aspectj";

    private static final long ID = 1L;
    private static final String NAME = "Master 1";

    EntityManagerFactory entityManagerFactory = null;

    public MetamodelAspectJTest() {
        super();
    }

    public MetamodelAspectJTest(String name) {
        super(name);
    }

    @Override
    public String getPersistenceUnitName() {
        return PERSISTENCE_UNIT_NAME;
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("MetamodelTest with ApectJ Weaved Managed Classes");
        suite.addTest(new MetamodelAspectJTest("testSetup"));
        suite.addTest(new MetamodelAspectJTest("testMetamodel"));
        return suite;
    }

    @Override
    public void setUp() {
        super.setUp();
        entityManagerFactory = getEntityManagerFactory(PERSISTENCE_UNIT_NAME);
    }

        /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
        new MetamodelAspectJTableCreator().replaceTables(getPersistenceUnitServerSession());
        clearCache();
    }

    public void testMetamodel() {
        Metamodel metamodel = entityManagerFactory.getMetamodel();
        assertNotNull(entityManagerFactory);
        assertNotNull(metamodel);

        EntityManager em = entityManagerFactory.createEntityManager();

        try {
            em.getTransaction().begin();
            ItemAspectJ entity = new ItemAspectJ(ID, "aaaa");
            em.persist(entity);
            em.getTransaction().commit();
            em.getTransaction().begin();
            entity.setName(NAME);
            em.merge(entity);
            em.getTransaction().commit();
            ItemAspectJ entity1 = em.find(ItemAspectJ.class, ID);
            assertNotNull(entity1);
            assertEquals(ID, entity1.getId());
            assertEquals(NAME, entity1.getName());
            em.getTransaction().begin();
            em.remove(entity);
            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
        finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }
}
