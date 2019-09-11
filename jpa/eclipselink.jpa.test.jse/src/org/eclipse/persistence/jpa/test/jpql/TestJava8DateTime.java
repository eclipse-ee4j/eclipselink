/*
 * Copyright (c) 2019 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2019 IBM Corporation. All rights reserved.
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
//     09/10/2019 - 2.7 Will Dazey
//       - 550951: Parameter binding for java.time types results in SQL syntax exception
package org.eclipse.persistence.jpa.test.jpql;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

import org.eclipse.persistence.internal.databaseaccess.Platform;
import org.eclipse.persistence.internal.jpa.EntityManagerImpl;
import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.jpql.model.SimpleTimeEntity;
import org.junit.Assume;
import org.junit.Test;
import org.junit.runner.RunWith;

/*
 * Several expressions disable parameter binding support:
 *     CASE, CONCAT, LIKE-ESCAPE, LOCATE, MOD, NOT LIKE-ESCAPE, SUBSTRING, TRIM FROM, ect
 * 
 * When parameter binding is disabled for an expression (and the platform doesn't support binding)
 * then incorrect SQL syntax can occur, causing query exceptions
 */
@RunWith(EmfRunner.class)
public class TestJava8DateTime {

    @Emf(name = "timeEMF", createTables = DDLGen.DROP_CREATE, classes = { SimpleTimeEntity.class })
    private EntityManagerFactory timeEMF;

    @Test
    public void testCASE_ParameterBinding() {

        //LocalDate
        EntityManager em = timeEMF.createEntityManager();
        try {
            Query query = em.createQuery(
                  "SELECT "
                    + "CASE "
                        + "WHEN t.simpleLocalDate <= ?1 THEN 'OLD' "
                        + "ELSE 'Other' END "
                + "FROM SimpleTimeEntity t ");
            query.setParameter(1, java.time.LocalDate.now());
            query.getResultList();
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if(em.isOpen()) {
                em.close();
            }
        }

        //LocalTime
        em = timeEMF.createEntityManager();
        try {
            Query query = em.createQuery(
                  "SELECT "
                    + "CASE "
                        + "WHEN t.simpleLocalTime <= ?1 THEN 'OLD' "
                        + "ELSE 'Other' END "
                + "FROM SimpleTimeEntity t ");
            query.setParameter(1, java.time.LocalTime.now());
            query.getResultList();
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if(em.isOpen()) {
                em.close();
            }
        }

        //LocalDateTime
        em = timeEMF.createEntityManager();
        try {
            Query query = em.createQuery(
                  "SELECT "
                    + "CASE "
                        + "WHEN t.simpleLocalDateTime <= ?1 THEN 'OLD' "
                        + "ELSE 'Other' END "
                + "FROM SimpleTimeEntity t ");
            query.setParameter(1, java.time.LocalDateTime.now());
            query.getResultList();
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if(em.isOpen()) {
                em.close();
            }
        }

        //OffsetTime
        em = timeEMF.createEntityManager();
        try {
            Query query = em.createQuery(
                  "SELECT "
                    + "CASE "
                        + "WHEN t.simpleOffsetTime <= ?1 THEN 'OLD' "
                        + "ELSE 'Other' END "
                + "FROM SimpleTimeEntity t ");
            query.setParameter(1, java.time.OffsetTime.now());
            query.getResultList();
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if(em.isOpen()) {
                em.close();
            }
        }

        //OffsetDateTime
        em = timeEMF.createEntityManager();
        try {
            Query query = em.createQuery(
                  "SELECT "
                    + "CASE "
                        + "WHEN t.simpleOffsetDateTime <= ?1 THEN 'OLD' "
                        + "ELSE 'Other' END "
                + "FROM SimpleTimeEntity t ");
            query.setParameter(1, java.time.OffsetDateTime.now());
            query.getResultList();
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if(em.isOpen()) {
                em.close();
            }
        }
    }

    @Test
    public void testCONCAT_ParameterBinding() {

        //LocalDate
        EntityManager em = timeEMF.createEntityManager();
        try {
            Query query = em.createQuery(
                    "SELECT t.id "
                  + "FROM SimpleTimeEntity t "
                  + "WHERE t.simpleString = CONCAT(?1, t.simpleLocalDate) "
                      + "AND t.simpleLocalDate <= ?2 ");
            query.setParameter(1, "TIME: ");
            query.setParameter(2, java.time.LocalDate.now());
            query.getResultList();
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if(em.isOpen()) {
                em.close();
            }
        }

        //LocalTime
        em = timeEMF.createEntityManager();
        try {
            Query query = em.createQuery(
                    "SELECT t.id "
                  + "FROM SimpleTimeEntity t "
                  + "WHERE t.simpleString = CONCAT(?1, t.simpleLocalTime) "
                      + "AND t.simpleLocalTime <= ?2 ");
            query.setParameter(1, "TIME: ");
            query.setParameter(2, java.time.LocalTime.now());
            query.getResultList();
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if(em.isOpen()) {
                em.close();
            }
        }

        //LocalDateTime
        em = timeEMF.createEntityManager();
        try {
            Query query = em.createQuery(
                    "SELECT t.id "
                  + "FROM SimpleTimeEntity t "
                  + "WHERE t.simpleString = CONCAT(?1, t.simpleLocalDateTime) "
                      + "AND t.simpleLocalDateTime <= ?2 ");
            query.setParameter(1, "TIME: ");
            query.setParameter(2, java.time.LocalDateTime.now());
            query.getResultList();
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if(em.isOpen()) {
                em.close();
            }
        }

        //OffsetTime
        em = timeEMF.createEntityManager();
        try {
            Query query = em.createQuery(
                    "SELECT t.id "
                  + "FROM SimpleTimeEntity t "
                  + "WHERE t.simpleString = CONCAT(?1, t.simpleOffsetTime) "
                      + "AND t.simpleOffsetTime <= ?2 ");
            query.setParameter(1, "TIME: ");
            query.setParameter(2, java.time.OffsetTime.now());
            query.getResultList();
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if(em.isOpen()) {
                em.close();
            }
        }

        //OffsetDateTime
        em = timeEMF.createEntityManager();
        try {
            Query query = em.createQuery(
                    "SELECT t.id "
                  + "FROM SimpleTimeEntity t "
                  + "WHERE t.simpleString = CONCAT(?1, t.simpleOffsetDateTime) "
                      + "AND t.simpleOffsetDateTime <= ?2 ");
            query.setParameter(1, "TIME: ");
            query.setParameter(2, java.time.OffsetDateTime.now());
            query.getResultList();
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if(em.isOpen()) {
                em.close();
            }
        }
    }

    @Test
    public void testLIKEESCAPE_ParameterBinding() {
        EntityManager em = timeEMF.createEntityManager();

        Platform platform = ((EntityManagerImpl)em).getDatabaseSession().getDatasourcePlatform();
        //DB2Z: ESCAPE character support seems buggy, disabling test
        boolean notSupported = platform.isDB2Z();
        Assume.assumeFalse("Platform " + platform + " is not supported for this test", notSupported);

        //LocalDate
        try {
            Query query = em.createQuery(
                    "SELECT t.id "
                  + "FROM SimpleTimeEntity t "
                  + "WHERE t.simpleString LIKE ?1 ESCAPE '!' "
                  + "GROUP BY t.id "
                  + "HAVING MIN(t.simpleLocalDate) <= ?2 ");
            query.setParameter(1, "Test" + "%");
            query.setParameter(2, java.time.LocalDate.now());
            query.getResultList();
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if(em.isOpen()) {
                em.close();
            }
        }

        //LocalTime
        em = timeEMF.createEntityManager();
        try {
            Query query = em.createQuery(
                    "SELECT t.id "
                  + "FROM SimpleTimeEntity t "
                  + "WHERE t.simpleString LIKE ?1 ESCAPE '!' "
                  + "GROUP BY t.id "
                  + "HAVING MIN(t.simpleLocalTime) <= ?2 ");
            query.setParameter(1, "Test" + "%");
            query.setParameter(2, java.time.LocalTime.now());
            query.getResultList();
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if(em.isOpen()) {
                em.close();
            }
        }

        //LocalDateTime
        em = timeEMF.createEntityManager();
        try {
            Query query = em.createQuery(
                    "SELECT t.id "
                  + "FROM SimpleTimeEntity t "
                  + "WHERE t.simpleString LIKE ?1 ESCAPE '!' "
                  + "GROUP BY t.id "
                  + "HAVING MIN(t.simpleLocalDateTime) <= ?2 ");
            query.setParameter(1, "Test" + "%");
            query.setParameter(2, java.time.LocalDateTime.now());
            query.getResultList();
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if(em.isOpen()) {
                em.close();
            }
        }

        //OffsetTime
        em = timeEMF.createEntityManager();
        try {
            Query query = em.createQuery(
                    "SELECT t.id "
                  + "FROM SimpleTimeEntity t "
                  + "WHERE t.simpleString LIKE ?1 ESCAPE '!' "
                  + "GROUP BY t.id "
                  + "HAVING MIN(t.simpleOffsetTime) <= ?2 ");
            query.setParameter(1, "Test" + "%");
            query.setParameter(2, java.time.OffsetTime.now());
            query.getResultList();
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if(em.isOpen()) {
                em.close();
            }
        }

        //OffsetDateTime
        em = timeEMF.createEntityManager();
        try {
            Query query = em.createQuery(
                    "SELECT t.id "
                  + "FROM SimpleTimeEntity t "
                  + "WHERE t.simpleString LIKE ?1 ESCAPE '!' "
                  + "GROUP BY t.id "
                  + "HAVING MIN(t.simpleOffsetDateTime) <= ?2 ");
            query.setParameter(1, "Test" + "%");
            query.setParameter(2, java.time.OffsetDateTime.now());
            query.getResultList();
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if(em.isOpen()) {
                em.close();
            }
        }
    }

    @Test
    public void testLOCATE_ParameterBinding() {

        //LocalDate
        EntityManager em = timeEMF.createEntityManager();
        try {
            Query query = em.createQuery(
                    "SELECT t.id "
                  + "FROM SimpleTimeEntity t "
                  + "WHERE LOCATE(?1, t.simpleString) > 0 "
                      + "AND t.simpleLocalDate <= ?2 ");
            query.setParameter(1, "PST");
            query.setParameter(2, java.time.LocalDate.now());
            query.getResultList();
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if(em.isOpen()) {
                em.close();
            }
        }

        //LocalTime
        em = timeEMF.createEntityManager();
        try {
            Query query = em.createQuery(
                    "SELECT t.id "
                  + "FROM SimpleTimeEntity t "
                  + "WHERE LOCATE(?1, t.simpleString) > 0 "
                      + "AND t.simpleLocalTime <= ?2 ");
            query.setParameter(1, "PST");
            query.setParameter(2, java.time.LocalTime.now());
            query.getResultList();
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if(em.isOpen()) {
                em.close();
            }
        }

        //LocalDateTime
        em = timeEMF.createEntityManager();
        try {
            Query query = em.createQuery(
                    "SELECT t.id "
                  + "FROM SimpleTimeEntity t "
                  + "WHERE LOCATE(?1, t.simpleString) > 0 "
                      + "AND t.simpleLocalDateTime <= ?2 ");
            query.setParameter(1, "PST");
            query.setParameter(2, java.time.LocalDateTime.now());
            query.getResultList();
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if(em.isOpen()) {
                em.close();
            }
        }

        //OffsetTime
        em = timeEMF.createEntityManager();
        try {
            Query query = em.createQuery(
                    "SELECT t.id "
                  + "FROM SimpleTimeEntity t "
                  + "WHERE LOCATE(?1, t.simpleString) > 0 "
                      + "AND t.simpleOffsetTime <= ?2 ");
            query.setParameter(1, "PST");
            query.setParameter(2, java.time.OffsetTime.now());
            query.getResultList();
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if(em.isOpen()) {
                em.close();
            }
        }

        //OffsetDateTime
        em = timeEMF.createEntityManager();
        try {
            Query query = em.createQuery(
                    "SELECT t.id "
                  + "FROM SimpleTimeEntity t "
                  + "WHERE LOCATE(?1, t.simpleString) > 0 "
                      + "AND t.simpleOffsetDateTime <= ?2 ");
            query.setParameter(1, "PST");
            query.setParameter(2, java.time.OffsetDateTime.now());
            query.getResultList();
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if(em.isOpen()) {
                em.close();
            }
        }
    }

    @Test
    public void testMOD_ParameterBinding() {

        //LocalDate
        EntityManager em = timeEMF.createEntityManager();
        try {
            //MOD expression
            Query query = em.createQuery(
                    "SELECT t.id "
                  + "FROM SimpleTimeEntity t "
                  + "WHERE MOD(t.simpleInteger, 2) > ?1 "
                      + "AND t.simpleLocalDate <= ?2 ");
            query.setParameter(1, 4);
            query.setParameter(2, java.time.LocalDate.now());
            query.getResultList();
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if(em.isOpen()) {
                em.close();
            }
        }

        //LocalTime
        em = timeEMF.createEntityManager();
        try {
            //MOD expression
            Query query = em.createQuery(
                    "SELECT t.id "
                  + "FROM SimpleTimeEntity t "
                  + "WHERE MOD(t.simpleInteger, 2) > ?1 "
                      + "AND t.simpleLocalTime <= ?2 ");
            query.setParameter(1, 4);
            query.setParameter(2, java.time.LocalTime.now());
            query.getResultList();
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if(em.isOpen()) {
                em.close();
            }
        }

        //LocalDateTime
        em = timeEMF.createEntityManager();
        try {
            //MOD expression
            Query query = em.createQuery(
                    "SELECT t.id "
                  + "FROM SimpleTimeEntity t "
                  + "WHERE MOD(t.simpleInteger, 2) > ?1 "
                      + "AND t.simpleLocalDateTime <= ?2 ");
            query.setParameter(1, 4);
            query.setParameter(2, java.time.LocalDateTime.now());
            query.getResultList();
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if(em.isOpen()) {
                em.close();
            }
        }

        //OffsetTime
        em = timeEMF.createEntityManager();
        try {
            //MOD expression
            Query query = em.createQuery(
                    "SELECT t.id "
                  + "FROM SimpleTimeEntity t "
                  + "WHERE MOD(t.simpleInteger, 2) > ?1 "
                      + "AND t.simpleOffsetTime <= ?2 ");
            query.setParameter(1, 4);
            query.setParameter(2, java.time.OffsetTime.now());
            query.getResultList();
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if(em.isOpen()) {
                em.close();
            }
        }

        //OffsetDateTime
        em = timeEMF.createEntityManager();
        try {
            //MOD expression
            Query query = em.createQuery(
                    "SELECT t.id "
                  + "FROM SimpleTimeEntity t "
                  + "WHERE MOD(t.simpleInteger, 2) > ?1 "
                      + "AND t.simpleOffsetDateTime <= ?2 ");
            query.setParameter(1, 4);
            query.setParameter(2, java.time.OffsetDateTime.now());
            query.getResultList();
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if(em.isOpen()) {
                em.close();
            }
        }
    }

    @Test
    public void testNOTLIKEESCAPE_ParameterBinding() {
        EntityManager em = timeEMF.createEntityManager();

        Platform platform = ((EntityManagerImpl)em).getDatabaseSession().getDatasourcePlatform();
        //DB2Z: ESCAPE character support seems buggy, disabling test
        boolean notSupported = platform.isDB2Z();
        Assume.assumeFalse("Platform " + platform + " is not supported for this test", notSupported);

        //LocalDate
        try {
            Query query = em.createQuery(
                    "SELECT t.id "
                  + "FROM SimpleTimeEntity t "
                  + "WHERE t.simpleString NOT LIKE ?1 ESCAPE '!' "
                  + "GROUP BY t.id "
                  + "HAVING MIN(t.simpleLocalDate) <= ?2 ");
            query.setParameter(1, "Test" + "%");
            query.setParameter(2, java.time.LocalDate.now());
            query.getResultList();
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if(em.isOpen()) {
                em.close();
            }
        }

        //LocalTime
        em = timeEMF.createEntityManager();
        try {
            Query query = em.createQuery(
                    "SELECT t.id "
                  + "FROM SimpleTimeEntity t "
                  + "WHERE t.simpleString NOT LIKE ?1 ESCAPE '!' "
                  + "GROUP BY t.id "
                  + "HAVING MIN(t.simpleLocalTime) <= ?2 ");
            query.setParameter(1, "Test" + "%");
            query.setParameter(2, java.time.LocalTime.now());
            query.getResultList();
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if(em.isOpen()) {
                em.close();
            }
        }

        //LocalDateTime
        em = timeEMF.createEntityManager();
        try {
            Query query = em.createQuery(
                    "SELECT t.id "
                  + "FROM SimpleTimeEntity t "
                  + "WHERE t.simpleString NOT LIKE ?1 ESCAPE '!' "
                  + "GROUP BY t.id "
                  + "HAVING MIN(t.simpleLocalDateTime) <= ?2 ");
            query.setParameter(1, "Test" + "%");
            query.setParameter(2, java.time.LocalDateTime.now());
            query.getResultList();
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if(em.isOpen()) {
                em.close();
            }
        }

        //OffsetTime
        em = timeEMF.createEntityManager();
        try {
            Query query = em.createQuery(
                    "SELECT t.id "
                  + "FROM SimpleTimeEntity t "
                  + "WHERE t.simpleString NOT LIKE ?1 ESCAPE '!' "
                  + "GROUP BY t.id "
                  + "HAVING MIN(t.simpleOffsetTime) <= ?2 ");
            query.setParameter(1, "Test" + "%");
            query.setParameter(2, java.time.OffsetTime.now());
            query.getResultList();
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if(em.isOpen()) {
                em.close();
            }
        }

        //OffsetDateTime
        em = timeEMF.createEntityManager();
        try {
            Query query = em.createQuery(
                    "SELECT t.id "
                  + "FROM SimpleTimeEntity t "
                  + "WHERE t.simpleString NOT LIKE ?1 ESCAPE '!' "
                  + "GROUP BY t.id "
                  + "HAVING MIN(t.simpleOffsetDateTime) <= ?2 ");
            query.setParameter(1, "Test" + "%");
            query.setParameter(2, java.time.OffsetDateTime.now());
            query.getResultList();
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if(em.isOpen()) {
                em.close();
            }
        }
    }

    @Test
    public void testSUBSTRING_ParameterBinding() {

        //LocalDate
        EntityManager em = timeEMF.createEntityManager();
        try {
            Query query = em.createQuery(
                    "SELECT t.id "
                  + "FROM SimpleTimeEntity t "
                  + "WHERE SUBSTRING(t.simpleString, 1, 4) = ?1 "
                      + "AND t.simpleLocalDate <= ?2 ");
            query.setParameter(1, "TEST");
            query.setParameter(2, java.time.LocalDate.now());
            query.getResultList();
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if(em.isOpen()) {
                em.close();
            }
        }

        //LocalTime
        em = timeEMF.createEntityManager();
        try {
            Query query = em.createQuery(
                    "SELECT t.id "
                  + "FROM SimpleTimeEntity t "
                  + "WHERE SUBSTRING(t.simpleString, 1, 4) = ?1 "
                      + "AND t.simpleLocalTime <= ?2 ");
            query.setParameter(1, "TEST");
            query.setParameter(2, java.time.LocalTime.now());
            query.getResultList();
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if(em.isOpen()) {
                em.close();
            }
        }

        //LocalDateTime
        em = timeEMF.createEntityManager();
        try {
            Query query = em.createQuery(
                    "SELECT t.id "
                  + "FROM SimpleTimeEntity t "
                  + "WHERE SUBSTRING(t.simpleString, 1, 4) = ?1 "
                      + "AND t.simpleLocalDateTime <= ?2 ");
            query.setParameter(1, "TEST");
            query.setParameter(2, java.time.LocalDateTime.now());
            query.getResultList();
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if(em.isOpen()) {
                em.close();
            }
        }

        //OffsetTime
        em = timeEMF.createEntityManager();
        try {
            Query query = em.createQuery(
                    "SELECT t.id "
                  + "FROM SimpleTimeEntity t "
                  + "WHERE SUBSTRING(t.simpleString, 1, 4) = ?1 "
                      + "AND t.simpleOffsetTime <= ?2 ");
            query.setParameter(1, "TEST");
            query.setParameter(2, java.time.OffsetTime.now());
            query.getResultList();
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if(em.isOpen()) {
                em.close();
            }
        }

        //OffsetDateTime
        em = timeEMF.createEntityManager();
        try {
            Query query = em.createQuery(
                    "SELECT t.id "
                  + "FROM SimpleTimeEntity t "
                  + "WHERE SUBSTRING(t.simpleString, 1, 4) = ?1 "
                      + "AND t.simpleOffsetDateTime <= ?2 ");
            query.setParameter(1, "TEST");
            query.setParameter(2, java.time.OffsetDateTime.now());
            query.getResultList();
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if(em.isOpen()) {
                em.close();
            }
        }
    }

    @Test
    public void testTRIMFROM_ParameterBinding() {

        //LocalDate
        EntityManager em = timeEMF.createEntityManager();
        try {
            Query query = em.createQuery(
                    "SELECT t.id "
                  + "FROM SimpleTimeEntity t "
                  + "WHERE t.simpleString = TRIM(BOTH ?1 FROM ?2) "
                      + "AND t.simpleLocalDate <= ?3 ");
            query.setParameter(1, 'X');
            query.setParameter(2, "XTestX");
            query.setParameter(3, java.time.LocalDate.now());
            query.getResultList();
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if(em.isOpen()) {
                em.close();
            }
        }

        //LocalTime
        em = timeEMF.createEntityManager();
        try {
            Query query = em.createQuery(
                    "SELECT t.id "
                  + "FROM SimpleTimeEntity t "
                  + "WHERE t.simpleString = TRIM(BOTH ?1 FROM ?2) "
                      + "AND t.simpleLocalTime <= ?3 ");
            query.setParameter(1, 'X');
            query.setParameter(2, "XTestX");
            query.setParameter(3, java.time.LocalTime.now());
            query.getResultList();
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if(em.isOpen()) {
                em.close();
            }
        }

        //LocalDateTime
        em = timeEMF.createEntityManager();
        try {
            Query query = em.createQuery(
                    "SELECT t.id "
                  + "FROM SimpleTimeEntity t "
                  + "WHERE t.simpleString = TRIM(BOTH ?1 FROM ?2) "
                      + "AND t.simpleLocalDateTime <= ?3 ");
            query.setParameter(1, 'X');
            query.setParameter(2, "XTestX");
            query.setParameter(3, java.time.LocalDateTime.now());
            query.getResultList();
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if(em.isOpen()) {
                em.close();
            }
        }

        //OffsetTime
        em = timeEMF.createEntityManager();
        try {
            Query query = em.createQuery(
                    "SELECT t.id "
                  + "FROM SimpleTimeEntity t "
                  + "WHERE t.simpleString = TRIM(BOTH ?1 FROM ?2) "
                      + "AND t.simpleOffsetTime <= ?3 ");
            query.setParameter(1, 'X');
            query.setParameter(2, "XTestX");
            query.setParameter(3, java.time.OffsetTime.now());
            query.getResultList();
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if(em.isOpen()) {
                em.close();
            }
        }

        //OffsetDateTime
        em = timeEMF.createEntityManager();
        try {
            Query query = em.createQuery(
                    "SELECT t.id "
                  + "FROM SimpleTimeEntity t "
                  + "WHERE t.simpleString = TRIM(BOTH ?1 FROM ?2) "
                      + "AND t.simpleOffsetDateTime <= ?3 ");
            query.setParameter(1, 'X');
            query.setParameter(2, "XTestX");
            query.setParameter(3, java.time.OffsetDateTime.now());
            query.getResultList();
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if(em.isOpen()) {
                em.close();
            }
        }
    }
}
