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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.jpa.lob;

import java.lang.Integer;
import java.lang.reflect.Field;

import javax.persistence.EntityManager;
import junit.framework.*;

import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.weaving.PersistenceWeaved;
import org.eclipse.persistence.testing.models.jpa.lob.Image;
import org.eclipse.persistence.testing.models.jpa.lob.ImageSimulator;
import org.eclipse.persistence.testing.models.jpa.lob.LobTableCreator;
import org.eclipse.persistence.testing.models.jpa.lob.SerializableNonEntity;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;

/**
 * JUnit test case(s) for the TopLink EntityMappingsXMLProcessor.
 */
public class LobJUnitTestCase extends JUnitTestCase {

    private static Integer imageId;
    private static Image originalImage;

    public LobJUnitTestCase() {
        super();
    }

    public LobJUnitTestCase(String name) {
        super(name);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("Lob Model");
        suite.addTest(new LobJUnitTestCase("testSetup"));
        suite.addTest(new LobJUnitTestCase("testMerge"));
        suite.addTest(new LobJUnitTestCase("testCreate"));
        suite.addTest(new LobJUnitTestCase("testRead"));
        suite.addTest(new LobJUnitTestCase("testUpdate"));
        suite.addTest(new LobJUnitTestCase("testDelete"));
        suite.addTest(new LobJUnitTestCase("testMerge"));
        suite.addTest(new LobJUnitTestCase("testRetrieveLazyBasicAfterTxnReadCommit"));

        return suite;
    }

    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
        new LobTableCreator().replaceTables(JUnitTestCase.getServerSession());
        clearCache();
    }

    public void testCreate() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            Image image = ImageSimulator.generateImage(4800, 4500);
            originalImage = image;
            em.persist(image);
            imageId = image.getId();
            commitTransaction(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw e;
        }
        closeEntityManager(em);
    }

    public void testDelete() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            em.remove(em.find(Image.class, imageId));
            commitTransaction(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw e;
        }
        assertTrue("Error deleting Image", em.find(Image.class, imageId) == null);
        closeEntityManager(em);
    }

    public void testRead() throws Exception {
        clearCache();
        EntityManager em = createEntityManager();
        beginTransaction(em);
        Image image = em.find(Image.class, imageId);
        try {
             assertTrue(image.getId() == imageId);
             // Check lazy loading of lob, if agent was used.
             if (image instanceof PersistenceWeaved) {
                 Field field = image.getClass().getDeclaredField("audio");
                 field.setAccessible(true);
                 if (field.get(image) != null) {
                     fail("Lazy basic should not be fetched.");
                 }
            }
             commitTransaction(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            throw e;
        } finally {
            closeEntityManager(em);
        }

        // entity manager has been closed - image is detached object.
        assertTrue("byte-arrays do not match", Helper.compareByteArrays(image.getAudio(), originalImage.getAudio()));
        assertTrue("char-arrays do not match", Helper.compareCharArrays(image.getCommentary(), originalImage.getCommentary()));
        assertTrue("Byte-arrays do not match", Helper.compareArrays(image.getPicture(), originalImage.getPicture()));
        assertTrue(image.getScript().equals(originalImage.getScript()));
    }

    public void testUpdate() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            Image image = em.find(Image.class, imageId);
            image.setAudio(null);
            image.setCommentary(null);
            image.setPicture(null);
            image.setScript(null);
            em.merge(image);
            commitTransaction(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw e;
        }
        Image image = em.find(Image.class, imageId);
        assertNull(image.getAudio());
        assertNull(image.getCommentary());
        assertNull(image.getPicture());
        assertNull(image.getScript());
        closeEntityManager(em);
    }

    public void testMerge() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            Image image = new Image();
            image.setId(5001);
            SerializableNonEntity sne = new SerializableNonEntity();
            sne.setSomeValue(1l);
            image.setCustomAttribute1(sne);
            em.persist(image);
            commitTransaction(em);
            closeEntityManager(em);

            image.getCustomAttribute1().setSomeValue(2l);
            em = createEntityManager();
            beginTransaction(em);
            em.merge(image);
            commitTransaction(em);

            em.clear();
            clearCache();

            beginTransaction(em);
            image = em.find(Image.class, 5001);
            assertTrue("Image.customAttribute1 not correctly updated.", image.getCustomAttribute1().getSomeValue() == 2l);

            em.remove(image);
            commitTransaction(em);
            clearCache();
        } catch (RuntimeException e) {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw e;
        }
        closeEntityManager(em);
    }

    //Bug#465051 : Lazy basic attribute not getting loaded
    public void testRetrieveLazyBasicAfterTxnReadCommit() {
        
        Image image = null;

        try {
            EntityManager em = createEntityManager();
            beginTransaction(em);
            image = new Image();
            image.setId(1);
            image.setAudio(new byte[] {1,2}); //Lazy basic
            image.setPicture(new Byte[] {3,4}); //Needed to create a changeset in next txn
            em.persist(image);
            int imageId = image.getId();
            
            commitTransaction(em);
            //clear cache
            clearCache();
            
            // Create new entity manager to avoid extended uow of work cache hits.
            em = createEntityManager();
            beginTransaction(em);
            //delete to begin txn
            em.createQuery("DELETE FROM Image i WHERE i.id > " + imageId).executeUpdate();
            //read within transaction
            image = em.find(Image.class, imageId);
            image.setPicture(new Byte[] {6,7});
            
            commitTransaction(em);
            
            //read with a new entity manager
            em = createEntityManager();
            image = em.find(Image.class, imageId);
            assertNotNull("lazy basic entity audio is null ", image.getAudio());
            
        } finally {
            // clean up test data
            if (image != null) {
                EntityManager em = createEntityManager();
                image = em.find(Image.class, image.getId());
                if (image != null) {
                    beginTransaction(em);
                    image = em.merge(image);
                    em.remove(image);
                    commitTransaction(em);
                }
                closeEntityManager(em);
            }
        }
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(LobJUnitTestCase.suite());
    }
}
