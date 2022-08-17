/*
 * Copyright (c) 2022 IBM and/or its affiliates. All rights reserved.
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
//     IBM - initial API and implementation

package org.eclipse.persistence.jpa.test.uuid;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;

import java.util.UUID;

import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.framework.Property;
import org.eclipse.persistence.jpa.test.uuid.model.EmbeddableUUID_ID;
import org.eclipse.persistence.jpa.test.uuid.model.UUIDAutoGenEntity;
import org.eclipse.persistence.jpa.test.uuid.model.UUIDEmbeddableIdEntity;
import org.eclipse.persistence.jpa.test.uuid.model.UUIDIdClassEntity;
import org.eclipse.persistence.jpa.test.uuid.model.UUIDUUIDEntity;
import org.eclipse.persistence.jpa.test.uuid.model.UUIDUUIDGenEntity;
import org.eclipse.persistence.jpa.test.uuid.model.UUID_IDClass;
import org.eclipse.persistence.jpa.test.uuid.model.XMLUUIDAutoGenEntity;
import org.eclipse.persistence.jpa.test.uuid.model.XMLEmbeddableUUID_ID;
import org.eclipse.persistence.jpa.test.uuid.model.XMLUUIDEmbeddableIdEntity;
import org.eclipse.persistence.jpa.test.uuid.model.XMLUUIDEntity;
import org.eclipse.persistence.jpa.test.uuid.model.XMLUUIDIdClassEntity;
import org.eclipse.persistence.jpa.test.uuid.model.XMLUUIDUUIDGenEntity;
import org.eclipse.persistence.jpa.test.uuid.model.XMLUUID_IDClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

@RunWith(EmfRunner.class)
public class TestUUIDGenerator {
	@Emf(name = "uuidGeneratorsEmf", createTables = DDLGen.DROP_CREATE, classes = {
			EmbeddableUUID_ID.class, UUID_IDClass.class, UUIDEmbeddableIdEntity.class, UUIDIdClassEntity.class, UUIDUUIDEntity.class,
			UUIDUUIDGenEntity.class, UUIDAutoGenEntity.class,
			XMLEmbeddableUUID_ID.class, XMLUUID_IDClass.class, XMLUUIDEmbeddableIdEntity.class , XMLUUIDIdClassEntity.class, XMLUUIDEntity.class,
			XMLUUIDUUIDGenEntity.class, XMLUUIDAutoGenEntity.class},
			mappingFiles = { "META-INF/uuid-orm.xml"},
			properties = { @Property(name="eclipselink.cache.shared.default", value="false") } )
    private EntityManagerFactory uuidEmf;

	@Test
    public void testBasicUUIDIdentity() {
		EntityManager em = uuidEmf.createEntityManager();

        try {
            em.getTransaction().begin();

            UUID id = UUID.randomUUID();
            UUIDUUIDEntity entity = new UUIDUUIDEntity();
            entity.setId(id);
            entity.setName("Victor Von Doom");
            em.persist(entity);

            em.getTransaction().commit();

            em.clear();
            assertFalse(em.contains(entity));

            UUIDUUIDEntity findEntity = em.find(UUIDUUIDEntity.class, id);
            assertNotNull(findEntity);
            assertEquals(id, findEntity.getId());
            assertNotSame(id, findEntity.getId());
            assertNotSame(entity, findEntity);
        } catch (AssertionError ae) {
            throw ae;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }

	@Test
    public void testBasicUUIDIdentity_XML() {
		EntityManager em = uuidEmf.createEntityManager();

        try {
            em.getTransaction().begin();

            UUID id = UUID.randomUUID();
            XMLUUIDEntity entity = new XMLUUIDEntity();
            entity.setId(id);
            entity.setName("Victor Von Doom");
            em.persist(entity);

            em.getTransaction().commit();

            em.clear();
            assertFalse(em.contains(entity));

            XMLUUIDEntity findEntity = em.find(XMLUUIDEntity.class, id);
            assertNotNull(findEntity);
            assertEquals(id, findEntity.getId());
            assertNotSame(id, findEntity.getId());
            assertNotSame(entity, findEntity);
        } catch (AssertionError ae) {
            throw ae;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }

	@Test
    public void testBasic_EmbeddableID_UUIDIdentity() {
		EntityManager em = uuidEmf.createEntityManager();

        try {
            em.getTransaction().begin();

            UUID id = UUID.randomUUID();
            EmbeddableUUID_ID emb_id = new EmbeddableUUID_ID();
            emb_id.setId(id);

            UUIDEmbeddableIdEntity entity = new UUIDEmbeddableIdEntity();
            entity.setId(emb_id);
            entity.setName("Kang the Conquerer");
            em.persist(entity);

            em.getTransaction().commit();

            em.clear();
            assertFalse(em.contains(entity));

            EmbeddableUUID_ID find_emb_id = new EmbeddableUUID_ID();
            find_emb_id.setId(id);
            UUIDEmbeddableIdEntity findEntity = em.find(UUIDEmbeddableIdEntity.class, find_emb_id);

            assertNotNull(findEntity);
            assertEquals(emb_id, findEntity.getId());
            assertNotSame(emb_id, findEntity.getId());
            assertNotSame(entity, findEntity);
        } catch (AssertionError ae) {
            throw ae;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }

	@Test
    public void testBasic_EmbeddableID_UUIDIdentity_XML() {
		EntityManager em = uuidEmf.createEntityManager();

        try {
            em.getTransaction().begin();

            UUID id = UUID.randomUUID();
            XMLEmbeddableUUID_ID emb_id = new XMLEmbeddableUUID_ID();
            emb_id.setEId(id);

            XMLUUIDEmbeddableIdEntity entity = new XMLUUIDEmbeddableIdEntity();
            entity.setId(emb_id);
            entity.setName("Kang the Conquerer");
            em.persist(entity);

            em.getTransaction().commit();

            em.clear();
            assertFalse(em.contains(entity));

            XMLEmbeddableUUID_ID find_emb_id = new XMLEmbeddableUUID_ID();
            find_emb_id.setEId(id);
            XMLUUIDEmbeddableIdEntity findEntity = em.find(XMLUUIDEmbeddableIdEntity.class, find_emb_id);

            assertNotNull(findEntity);
            assertEquals(emb_id, findEntity.getId());
            assertNotSame(emb_id, findEntity.getId());
            assertNotSame(entity, findEntity);
        } catch (AssertionError ae) {
            throw ae;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }

	@Test
    public void testBasic_IDClass_UUIDIdentity() {
		EntityManager em = uuidEmf.createEntityManager();

        try {
            em.getTransaction().begin();

            UUID id = UUID.randomUUID();
            long lid = System.currentTimeMillis();

            UUIDIdClassEntity entity = new UUIDIdClassEntity();
            entity.setUuid_id(id);
            entity.setL_id(lid);
            entity.setName("Thanos the Mad Titan");
            em.persist(entity);

            em.getTransaction().commit();

            em.clear();
            assertFalse(em.contains(entity));

            UUID_IDClass find_idclass = new UUID_IDClass();
            find_idclass.setUuid_id(id);
            find_idclass.setL_id(lid);

            UUIDIdClassEntity findEntity = em.find(UUIDIdClassEntity.class, find_idclass);
            assertNotNull(findEntity);
            assertEquals(id, findEntity.getUuid_id());
            assertNotSame(id, findEntity.getUuid_id());
            assertNotSame(entity, findEntity);
        } catch (AssertionError ae) {
            throw ae;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }

	@Test
    public void testBasic_IDClass_UUIDIdentity_XML() {
		EntityManager em = uuidEmf.createEntityManager();

        try {
            em.getTransaction().begin();

            UUID id = UUID.randomUUID();
            long lid = System.currentTimeMillis();

            XMLUUIDIdClassEntity entity = new XMLUUIDIdClassEntity();
            entity.setUuid_id(id);
            entity.setL_id(lid);
            entity.setName("Thanos the Mad Titan");
            em.persist(entity);

            em.getTransaction().commit();

            em.clear();
            assertFalse(em.contains(entity));

            XMLUUID_IDClass find_idclass = new XMLUUID_IDClass();
            find_idclass.setUuid_id(id);
            find_idclass.setL_id(lid);

            XMLUUIDIdClassEntity findEntity = em.find(XMLUUIDIdClassEntity.class, find_idclass);
            assertNotNull(findEntity);
            assertEquals(id, findEntity.getUuid_id());
            assertNotSame(id, findEntity.getUuid_id());
            assertNotSame(entity, findEntity);
        } catch (AssertionError ae) {
            throw ae;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }

    @Test // https://github.com/eclipse-ee4j/eclipselink/issues/1625
    public void testBasicUUIDIdentity_AUTO_Generator() {
		EntityManager em = uuidEmf.createEntityManager();

        try {
            em.getTransaction().begin();

            UUIDAutoGenEntity entity = new UUIDAutoGenEntity();
            entity.setName("Ultron");
            em.persist(entity);

            em.getTransaction().commit();

            UUID id = entity.getId();
            assertNotNull(id);

            em.clear();
            assertFalse(em.contains(entity));

            UUIDAutoGenEntity findEntity = em.find(UUIDAutoGenEntity.class, id);
            assertNotNull(findEntity);
            assertEquals(id, findEntity.getId());
            assertNotSame(id, findEntity.getId());
            assertNotSame(entity, findEntity);
        } catch (AssertionError ae) {
            throw ae;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }

    @Test // https://github.com/eclipse-ee4j/eclipselink/issues/1625
    public void testBasicUUIDIdentity_AUTO_Generator_XML() {
		EntityManager em = uuidEmf.createEntityManager();

        try {
            em.getTransaction().begin();

            XMLUUIDAutoGenEntity entity = new XMLUUIDAutoGenEntity();
            entity.setName("Ultron");
            em.persist(entity);

            em.getTransaction().commit();

            UUID id = entity.getId();
            assertNotNull(id);

            em.clear();
            assertFalse(em.contains(entity));

            XMLUUIDAutoGenEntity findEntity = em.find(XMLUUIDAutoGenEntity.class, id);
            assertNotNull(findEntity);
            assertEquals(id, findEntity.getId());
            assertNotSame(id, findEntity.getId());
            assertNotSame(entity, findEntity);
        } catch (AssertionError ae) {
            throw ae;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }

	@Test
    public void testBasicUUIDIdentity_UUID_Generator() {
		EntityManager em = uuidEmf.createEntityManager();

        try {
            em.getTransaction().begin();

            UUIDUUIDGenEntity entity = new UUIDUUIDGenEntity();
            entity.setName("Ultron");
            em.persist(entity);

            em.getTransaction().commit();

            UUID id = entity.getId();
            assertNotNull(id);

            em.clear();
            assertFalse(em.contains(entity));

            UUIDUUIDGenEntity findEntity = em.find(UUIDUUIDGenEntity.class, id);
            assertNotNull(findEntity);
            assertEquals(id, findEntity.getId());
            assertNotSame(id, findEntity.getId());
            assertNotSame(entity, findEntity);
        } catch (AssertionError ae) {
            throw ae;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }

	@Test
    public void testBasicUUIDIdentity_UUID_Generator_XML() {
		EntityManager em = uuidEmf.createEntityManager();

        try {
            em.getTransaction().begin();

            XMLUUIDUUIDGenEntity entity = new XMLUUIDUUIDGenEntity();
            entity.setName("Ultron");
            em.persist(entity);

            em.getTransaction().commit();

            UUID id = entity.getId();
            assertNotNull(id);

            em.clear();
            assertFalse(em.contains(entity));

            XMLUUIDUUIDGenEntity findEntity = em.find(XMLUUIDUUIDGenEntity.class, id);
            assertNotNull(findEntity);
            assertEquals(id, findEntity.getId());
            assertNotSame(id, findEntity.getId());
            assertNotSame(entity, findEntity);
        } catch (AssertionError ae) {
            throw ae;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }


}
