/*
 * Copyright (c) 2015, 2019 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2015, 2019 IBM Corporation. All rights reserved.
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
//     03/13/2015 - Jody Grassel
//       - 462103 : SQL for Stored Procedure named parameter with DB2 generated with incorrect marker
package org.eclipse.persistence.jpa.test.storedproc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.ParameterMode;
import javax.persistence.StoredProcedureQuery;

import org.eclipse.persistence.internal.databaseaccess.Platform;
import org.eclipse.persistence.internal.jpa.EntityManagerFactoryImpl;
import org.eclipse.persistence.jpa.test.basic.model.Dog;
import org.eclipse.persistence.jpa.test.basic.model.Person;
import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.framework.Property;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(EmfRunner.class)
public class TestDB2Platform {
    private static final String DB2_STOREDPROC_CREATE_SQL =
            "CREATE OR REPLACE PROCEDURE TSPONE " +
            "(IN dogid INTEGER) " +
            "DYNAMIC RESULT SETS 1 " +
            "LANGUAGE SQL " +
            "BEGIN " +
               "DECLARE result_set_1 CURSOR WITH RETURN FOR " +
                   "SELECT name FROM Dog " +
                   "WHERE id=dogid; " +
               "OPEN result_set_1; " +
               "END";

    @Emf(createTables = DDLGen.DROP_CREATE, classes = { Dog.class, Person.class },
            properties = { @Property(name = "eclipselink.cache.shared.default", value = "false") })
    private EntityManagerFactory emf;

    @Test
    public void testStoredProcedure() throws Exception {
        if (emf == null)
            return;

        EntityManager em = emf.createEntityManager();
        try {
            //Test for DB2 only
            //DB2Z does not support this stored procedure syntax
            Platform pl = emf.unwrap(EntityManagerFactoryImpl.class).getDatabaseSession().getDatasourcePlatform();
            if(!(pl.isDB2() && !pl.isDB2Z())) {
                return;
            }

            setupStoredProc(em);

            List<Dog> dogList = createDogs(emf);

            StoredProcedureQuery spq = em.createStoredProcedureQuery("TSPONE");
            spq.registerStoredProcedureParameter("dogid", Integer.class, ParameterMode.IN);
            spq.setParameter("dogid", dogList.get(0).getId());

            List<?> res = spq.getResultList();
            assertNotNull(res);
            assertEquals(1, res.size());
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (em != null) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                em.close();
            }
        }
    }

    private void setupStoredProc(EntityManager em) throws Exception {
        String stmt = DB2_STOREDPROC_CREATE_SQL;

        em.getTransaction().begin();
        Connection conn = em.unwrap(Connection.class);
        CallableStatement cs = conn.prepareCall(stmt);
        cs.execute();
        em.getTransaction().commit();
    }

    private List<Dog> createDogs(EntityManagerFactory emf) throws Exception {
        EntityManager em = emf.createEntityManager();
        try {
            ArrayList<Dog> dogList = new ArrayList<Dog>();
            em.getTransaction().begin();

            for (int i = 10; i > 0; i--) {
                Dog aDog = new Dog();
                aDog.setName("Dog " + i);
                em.persist(aDog);
                dogList.add(aDog);
            }

            em.getTransaction().commit();

            return dogList;
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }
}
