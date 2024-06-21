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
//     Oracle - initial API and implementation
package org.eclipse.persistence.testing.helidon.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.eclipse.persistence.testing.helidon.models.MasterEntity;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestMasterDao {

    private final static long MASTER_ID = 1L;
    private final static String MASTER_NAME = "Master 1";


    @Test
    public void findMasterTest() {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("HelidonPu");
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        MasterDao helidonMasterDao = new MasterDao();
        helidonMasterDao.setEntityManager(entityManager);
        MasterEntity masterResult = helidonMasterDao.find(MasterEntity.class, MASTER_ID);
        assertEquals(MASTER_ID, masterResult.getId());
        assertEquals(MASTER_NAME, masterResult.getName());
        assertTrue(masterResult.getDetails().size() > 1);
    }


}
