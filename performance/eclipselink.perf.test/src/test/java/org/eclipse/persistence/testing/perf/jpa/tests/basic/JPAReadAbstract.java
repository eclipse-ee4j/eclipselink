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

import jakarta.persistence.EntityManager;
import jakarta.persistence.Persistence;
import jakarta.persistence.EntityManagerFactory;

import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.ArrayRecord;
import org.eclipse.persistence.sessions.DatabaseRecord;
import org.eclipse.persistence.testing.perf.jpa.model.basic.DetailEntity;
import org.eclipse.persistence.testing.perf.jpa.model.basic.MasterEntity;
import org.eclipse.persistence.testing.tests.performance.emulateddb.EmulatedConnection;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.TearDown;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

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
        try {
            prepareData(em);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        } finally {
            if (em != null) {
                em.close();
            }
        }
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
                if (masterEntity.getId() != i) {
                    throw new RuntimeException("MasterEntity ID:\t" + masterEntity.getId() + " doesn't match with find key:\t" + i);
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

    private synchronized void prepareData(EntityManager em) {
        AbstractSession session = em.unwrap(AbstractSession.class);
        EmulatedConnection conn = (EmulatedConnection)session.getAccessor().getConnection();
        Vector<DatabaseField> masterFields = new Vector<>(session.getDescriptor(MasterEntity.class).getAllFields());
        DatabaseField[] masterFieldsArray = masterFields.toArray(new DatabaseField[0]);
        Vector<DatabaseField> detailFields = new Vector<>(session.getDescriptor(DetailEntity.class).getAllFields());
        DatabaseField[] detailFieldsArray = detailFields.toArray(new DatabaseField[0]);
        for (int i = 1; i <= getMasterSize(); i++) {
            List<DatabaseRecord> masterRows = new ArrayList<>(getMasterSize());
            masterRows.add(new ArrayRecord(masterFields, masterFieldsArray, new Object[]{i, "Master name " + i}));
            conn.putRows("SELECT ID, NAME FROM P2_MASTER WHERE (ID = " + i + ")", masterRows);
            List<DatabaseRecord> detailRows = new ArrayList<>(getDetailSize());
            for (int j = 1; j <= getDetailSize(); j++) {
                detailRows.add(new ArrayRecord(detailFields, detailFieldsArray, new Object[] {i * DETAIL_ID_STEP + j, "Detail name " + i * DETAIL_ID_STEP + j, i}));
            }
            conn.putRows("SELECT ID, NAME, MASTER_ID_FK FROM P2_DETAIL WHERE (MASTER_ID_FK = " + i + ")", detailRows);
        }
    }

    public abstract String getPersistenceUnitName();

    public abstract int getMasterSize();

    public abstract int getDetailSize();
}
