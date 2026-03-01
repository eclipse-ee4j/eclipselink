/*
 * Copyright (c) 2026 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */
package org.eclipse.persistence.testing.tests.jpa.persistence32.nofile;

import java.util.Map;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jakarta.persistence.PersistenceConfiguration;
import jakarta.persistence.PersistenceUnitTransactionType;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.testing.framework.jpa.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.persistence32.nofile.TypeNoFileEntity;

/**
 * Verify jakarta.persistence 3.2 API changes in {@link jakarta.persistence.EntityManagerFactory}
 * and new {@link jakarta.persistence.PersistenceConfiguration} without any persistence.xml file.
 */
public class EntityManagerFactoryTest extends JUnitTestCase {

    // Test Persistence.createEntityManagerFactory(PersistenceConfiguration)
    public void testCreateCustomEntityManagerFactoryWithoutPersistenceXMLFile() {
        PersistenceConfiguration configuration = createPersistenceConfiguration("persistence32_nofile");
        final int ID = 1;
        TypeNoFileEntity typeNoFileEntity = new TypeNoFileEntity(ID, "Type Name 1");
        try (EntityManagerFactory emfFromConfig = Persistence.createEntityManagerFactory(configuration)) {
            try (EntityManager em = emfFromConfig.createEntityManager()) {
                EntityTransaction et = em.getTransaction();
                try {
                    et.begin();
                    em.persist(typeNoFileEntity);
                    et.commit();
                } catch (Exception e) {
                    et.rollback();
                    throw e;
                }
            }
            try (EntityManager em = emfFromConfig.createEntityManager()) {
                TypeNoFileEntity typeNoFileEntityFound = em.find(TypeNoFileEntity.class, ID);
                assertEquals(typeNoFileEntity, typeNoFileEntityFound);
            }
        }
    }

    private PersistenceConfiguration createPersistenceConfiguration(String puName) {
        PersistenceConfiguration configuration = new PersistenceConfiguration(puName);
        Map<String , String> persistenceProperties = this.getPersistenceProperties();
        persistenceProperties.put(PersistenceUnitProperties.SCHEMA_GENERATION_DATABASE_ACTION, "drop-and-create");
        configuration.properties(persistenceProperties);
        configuration.managedClass(org.eclipse.persistence.testing.models.jpa.persistence32.nofile.TypeNoFileEntity.class);
        configuration.transactionType(PersistenceUnitTransactionType.RESOURCE_LOCAL);
        configuration.provider("org.eclipse.persistence.jpa.PersistenceProvider");
        return configuration;
    }
}
