/*
 * Copyright (c) 2021 IBM Corporation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */
package org.eclipse.persistence.jpa.test.property;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.internal.jpa.EntityManagerFactoryImpl;
import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.framework.Property;
import org.eclipse.persistence.jpa.test.property.model.GenericEntity;
import org.eclipse.persistence.platform.database.DatabasePlatform;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(EmfRunner.class)
/**
 * Tests the 'eclipselink.sql.allow-convert-result-to-boolean' persistence property. 
 * By enabling this property, the result type of a case select with Booleans should be an integer
 */
public class TestConvertResultToBoolean {

    @Emf(createTables = DDLGen.DROP_CREATE, classes = { GenericEntity.class }, 
            properties = { @Property(name="eclipselink.logging.level", value="FINE"), 
                    @Property(name=PersistenceUnitProperties.ALLOW_CONVERT_RESULT_TO_BOOLEAN, value="false")})
    private EntityManagerFactory emf;

    private static boolean POPULATED = false;

    @Test
    public void testCASE_DisableConvertResultToBoolean() {
        if (emf == null)
            return;

        if(!POPULATED) 
            populate();

        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            Query query = em.createQuery(""
                    + "SELECT ("
                       + "CASE "
                       + "WHEN g.itemInteger1 = 1 THEN TRUE "
                       + "ELSE FALSE "
                       + "END "
                    + ") "
                    + "FROM GenericEntity g ORDER BY g.itemInteger1 ASC");

            List<?> intList = query.getResultList();
            assertNotNull(intList);
            assertEquals(2, intList.size());

            DatabasePlatform platform = getPlatform(emf);
            if(platform.isDB2() || platform.isDerby()) {
                assertEquals(Integer.valueOf(1), intList.get(0));
                assertEquals(Integer.valueOf(0), intList.get(1));
            } else if(platform.isOracle()) {
                assertEquals(new java.math.BigDecimal(1), intList.get(0));
                assertEquals(new java.math.BigDecimal(0), intList.get(1));
            } else {
                assertEquals(Long.valueOf(1), intList.get(0));
                assertEquals(Long.valueOf(0), intList.get(1));
            }
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if(em.isOpen()) {
                em.close();
            }
        }
    }

    private void populate() {
        EntityManager em = emf.createEntityManager();
        try {

            GenericEntity tbl1 = new GenericEntity();
            tbl1.setKeyString("Key01");
            tbl1.setItemString1("A");
            tbl1.setItemInteger1(1);

            GenericEntity tbl2 = new GenericEntity();
            tbl2.setKeyString("Key02");
            tbl2.setItemString1("B");
            tbl2.setItemInteger1(2);

            em.getTransaction().begin();
            em.persist(tbl1);
            em.persist(tbl2);
            em.getTransaction().commit();

            POPULATED = true;
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if(em.isOpen()) {
                em.close();
            }
        }
    }

    private DatabasePlatform getPlatform(EntityManagerFactory emf) {
        return ((EntityManagerFactoryImpl)emf).getServerSession().getPlatform();
    }
}