/*
 * Copyright (c) 2024 Oracle and/or its affiliates. All rights reserved.
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
//              Oracle - initial implementation
package org.eclipse.persistence.testing.perf.jpa.tests.basic;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.persistence.EntityManagerFactory;

import org.eclipse.persistence.testing.perf.jpa.model.basic.DetailEntity;
import org.eclipse.persistence.testing.perf.jpa.model.basic.MasterEntity;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.TearDown;

/**
 * Benchmarks for JPA reading data.
 *
 * @author Oracle
 */
public abstract class JPAReadAbstract {

    public static final int DETAIL_ID_STEP = 10000;

    private EntityManagerFactory emf = Persistence.createEntityManagerFactory(getPersistenceUnitName());

    @Setup
    public void setup() {
        EntityManager em = emf.createEntityManager();
        prepareData(em);
    }

    @TearDown
    public void tearDown() {
        emf.close();
    }

    /**
     * Read MasterEntity and DetailEntity (fetch = FetchType.EAGER).
     */
    @Benchmark
    public void testReadEntity() {
        EntityManager em = null;
        try {
            em = emf.createEntityManager();
            for (long i = 1; i <= getMasterSize(); i++) {
                MasterEntity masterEntity = em.find(MasterEntity.class, i);
                if (masterEntity == null) {
                    throw new RuntimeException("MasterEntity is null!");
                }
                if (masterEntity.getDetails().size() < getDetailSize()) {
                    throw new RuntimeException("No of DetailEntities is |" + masterEntity.getDetails().size() + "| less than expected |" + getDetailSize() + "|!");
                }
            }
        } catch (Throwable e) {
            throw new RuntimeException(e);
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    private void prepareData(EntityManager em) {
        try {
            em.getTransaction().begin();
            for (int i = 1; i <= getMasterSize(); i++) {
                MasterEntity masterEntity = new MasterEntity(i, "Master name " + i);
                em.persist(masterEntity);
                for (int j = 1; j <= getDetailSize(); j++) {
                    DetailEntity detailEntity = new DetailEntity(i * DETAIL_ID_STEP + j, "Detail name " + j, masterEntity);
                    masterEntity.getDetails().add(detailEntity);
                    em.persist(detailEntity);
                }
            }
            em.getTransaction().commit();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
        }
    }

    public abstract String getPersistenceUnitName();

    public abstract int getMasterSize();

    public abstract int getDetailSize();
}
