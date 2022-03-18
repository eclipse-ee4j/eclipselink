/*
 * Copyright (c) 2022 Oracle and/or its affiliates. All rights reserved.
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
//     02/01/2022: Tomas Kraus
//       - Issue 1442: Implement New JPA API 3.1.0 Features
package org.eclipse.persistence.jpa.test.criteria;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.criteria.Root;

import org.eclipse.persistence.jpa.test.criteria.model.DateTimeEntity;
import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.framework.Property;
import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.logging.SessionLog;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test {@code LocalTime}/{@code LocalDate}/{@code LocalDateTime} functions in CriteriaBuilder.
 * Added to JPA-API as PR #352
 */
@RunWith(EmfRunner.class)
public class TestDateTimeFunctions {

    @Emf(createTables = DDLGen.DROP_CREATE,
            classes = {
                    DateTimeEntity.class
            },
            properties = {
                    @Property(name = "eclipselink.cache.shared.default", value = "false"),
                    // This property remaps String from VARCHAR->NVARCHAR(or db equivalent)
                    @Property(name = "eclipselink.target-database-properties",
                            value = "UseNationalCharacterVaryingTypeForString=true"),
            })
    private EntityManagerFactory emf;

    private final LocalDateTime[] TS = {
            LocalDateTime.of(1970, 1, 1, 1, 11, 11),
            LocalDateTime.of(1970, 1, 1, 0, 0, 0, 0)
    };

    private final DateTimeEntity[] ENTITY = {
            new DateTimeEntity(1, TS[0].toLocalTime(), TS[0].toLocalDate(), TS[0]), // reserved for testCriteriaUpdateLocalTime
            new DateTimeEntity(2, TS[0].toLocalTime(), TS[0].toLocalDate(), TS[0]), // reserved for testCriteriaUpdateLocalDate
            new DateTimeEntity(3, TS[0].toLocalTime(), TS[0].toLocalDate(), TS[0]), // reserved for testCriteriaUpdateLocalDateTime
            new DateTimeEntity(4, TS[1].toLocalTime(), TS[1].toLocalDate(), TS[1])  // reserved for testCriteriaQueryWhereLocalTime
                                                                                       //              testCriteriaQueryWhereLocalTimeReturnsEmpty
                                                                                       //              testCriteriaQueryWhereLocalDate
                                                                                       //              testCriteriaQueryWhereLocalDateReturnsEmpty
                                                                                       //              testCriteriaQueryWhereLocalDateTime
                                                                                       //              testCriteriaQueryWhereLocalDateTimeReturnsEmpty
                                                                                       //              testCriteriaQuerySelectLocalTime
                                                                                       //              testCriteriaQuerySelectLocalDate
                                                                                       //              testCriteriaQuerySelectLocalDateTime
    };

    // Database vs. Java timezone offset in seconds. Must be applied to LocalDateTime calculations.
    private long dbOffset = 0;

    // Update database vs. Java timezone offset using current database time.
    private void updateDbOffset() {
        final EntityManager em = emf.createEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<LocalTime> cq = cb.createQuery(LocalTime.class);
            cq.select(cb.localTime());
            Root<DateTimeEntity> entity = cq.from(DateTimeEntity.class);
            cq.where(cb.equal(entity.get("id"), 1));
            LocalTime dbTime = em.createQuery(cq).getSingleResult();
            LocalTime javaTime = LocalTime.now();
            this.dbOffset = dbTime.truncatedTo(ChronoUnit.SECONDS).toSecondOfDay() - javaTime.truncatedTo(ChronoUnit.SECONDS).toSecondOfDay();
        } catch (Throwable t) {
            AbstractSessionLog.getLog().log(SessionLog.WARNING, "Can't update DB offset: " + t.getMessage());
            t.printStackTrace();
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }

    }

    @Before
    public void setup() {
        final EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            for (DateTimeEntity e : ENTITY) {
                em.persist(e);
            }
            em.flush();
            em.getTransaction().commit();
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
        updateDbOffset();
    }

    @After
    public void cleanup() {
        final EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM DateTimeEntity e").executeUpdate();
            em.flush();
            em.getTransaction().commit();
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }

    // Test CriteriaUpdate of localTime.
    // Strategy: Update 1:11:11 to current LocalTime and verify that time in database
    //           differs in less than 30 seconds from current time.
    // May fail when database time is not set properly.
    @Test
    public void testCriteriaUpdateLocalTime() {
        final EntityManager em = emf.createEntityManager();
        try {
            // Run the update query: Set current time
            em.getTransaction().begin();
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaUpdate<DateTimeEntity> cu = cb.createCriteriaUpdate(DateTimeEntity.class);
            Root<DateTimeEntity> entity = cu.from(DateTimeEntity.class);
            cu.set("time", cb.localTime());
            cu.where(cb.equal(entity.get("id"), 1));
            em.createQuery(cu).executeUpdate();
            em.flush();
            em.getTransaction().commit();
            // Verify updated entity
            DateTimeEntity data = em.find(DateTimeEntity.class, 1);
            long diffMilis = Duration.between(data.getTime(), LocalTime.now().plusSeconds(dbOffset + 1)).toMillis();
            // Positive value means that test did not pass midnight.
            if (diffMilis > 0) {
                MatcherAssert.assertThat(diffMilis, Matchers.lessThan(30000L));
            // Midnight pass correction.
            } else {
                MatcherAssert.assertThat(86400000L + diffMilis, Matchers.lessThan(30000L));
            }
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }

    // Test CriteriaUpdate of localDate.
    // Strategy: Update 1. 1. 1970 to current LocalDate and verify that time in database
    //           differs in less than two days from current date. One day difference
    //           may be caused by midnight being just passed.
    // May fail when database time is not set properly.
    @Test
    public void testCriteriaUpdateLocalDate() {
        final EntityManager em = emf.createEntityManager();
        try {
            // Run the update query: Set current time
            em.getTransaction().begin();
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaUpdate<DateTimeEntity> cu = cb.createCriteriaUpdate(DateTimeEntity.class);
            Root<DateTimeEntity> entity = cu.from(DateTimeEntity.class);
            cu.set("date", cb.localDate());
            cu.where(cb.equal(entity.get("id"), 2));
            em.createQuery(cu).executeUpdate();
            em.flush();
            em.getTransaction().commit();
            // Verify updated entity
            DateTimeEntity data = em.find(DateTimeEntity.class, 2);
            Period diff = Period.between(data.getDate(), LocalDate.now());
            MatcherAssert.assertThat(diff.getYears(), Matchers.equalTo(0));
            MatcherAssert.assertThat(diff.getMonths(), Matchers.equalTo(0));
            MatcherAssert.assertThat(diff.getDays(), Matchers.lessThan(2));
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }

    // Test CriteriaUpdate of localDateTime.
    // Strategy: Update 1. 1. 1970 1:11:11 to current LocalDateTime and verify that timestamp in database
    //           differs in less than 30 seconds from current timestamp.
    // May fail when database time is not set properly.
    @Test
    public void testCriteriaUpdateLocalDateTime() {
        final EntityManager em = emf.createEntityManager();
        try {
            // Run the update query: Set current time
            em.getTransaction().begin();
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaUpdate<DateTimeEntity> cu = cb.createCriteriaUpdate(DateTimeEntity.class);
            Root<DateTimeEntity> entity = cu.from(DateTimeEntity.class);
            cu.set("datetime", cb.localDateTime());
            cu.where(cb.equal(entity.get("id"), 3));
            em.createQuery(cu).executeUpdate();
            em.flush();
            em.getTransaction().commit();
            // Verify updated entity
            DateTimeEntity data = em.find(DateTimeEntity.class, 3);
            long diffMilis = Duration.between(data.getDatetime(), LocalDateTime.now().plusSeconds(dbOffset + 1)).toMillis();
            MatcherAssert.assertThat(diffMilis, Matchers.lessThan(30000L));
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }

    // Test CriteriaQuery with localTime in WHERE condition.
    // Strategy: SELECT DateTimeEntity WHERE time column < localTime AND id = :id
    //           LocalTime in entity is set to 0:00:00.0.
    @Test
    public void testCriteriaQueryWhereLocalTime() {
        final EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<DateTimeEntity> cq = cb.createQuery(DateTimeEntity.class);
            Root<DateTimeEntity> entity = cq.from(DateTimeEntity.class);
            cq.select(entity);
            cq.where(cb.and(cb.lessThan(entity.get("time"), cb.localTime()), cb.equal(entity.get("id"), 4)));
            em.createQuery(cq).getSingleResult();
            em.getTransaction().commit();
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }

    // Test CriteriaQuery with localTime in WHERE condition with no record matching it.
    // Strategy: SELECT DateTimeEntity WHERE time column > localTime AND id = :id
    //           localTime in entity is set to 0:00:00.0
    // Such a record does not exist so empty result is expected.
    @Test
    public void testCriteriaQueryWhereLocalTimeReturnsEmpty() {
        final EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<DateTimeEntity> cq = cb.createQuery(DateTimeEntity.class);
            Root<DateTimeEntity> entity = cq.from(DateTimeEntity.class);
            cq.select(entity);
            cq.where(cb.and(cb.greaterThan(entity.get("time"), cb.localTime()), cb.equal(entity.get("id"), 4)));
            List<DateTimeEntity> data = em.createQuery(cq).getResultList();
            em.getTransaction().commit();
            MatcherAssert.assertThat(data.size(), Matchers.equalTo(0));
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }

    // Test CriteriaQuery with localDate in WHERE condition.
    // Strategy: SELECT DateTimeEntity WHERE time column < localTime AND id = :id
    //           LocalDate in entity is set to 1.1.1970.
    @Test
    public void testCriteriaQueryWhereLocalDate() {
        final EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<DateTimeEntity> cq = cb.createQuery(DateTimeEntity.class);
            Root<DateTimeEntity> entity = cq.from(DateTimeEntity.class);
            cq.select(entity);
            cq.where(cb.and(cb.lessThan(entity.get("date"), cb.localDate()), cb.equal(entity.get("id"), 4)));
            em.createQuery(cq).getSingleResult();
            em.getTransaction().commit();
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }

    // Test CriteriaQuery with localDate in WHERE condition with no record matching it.
    // Strategy: SELECT DateTimeEntity WHERE time column > localTime AND id = :id
    //           LocalDate in entity is set to 1.1.1970.
    // Such a record does not exist so empty result is expected
    @Test
    public void testCriteriaQueryWhereLocalDateReturnsEmpty() {
        final EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<DateTimeEntity> cq = cb.createQuery(DateTimeEntity.class);
            Root<DateTimeEntity> entity = cq.from(DateTimeEntity.class);
            cq.select(entity);
            cq.where(cb.and(cb.greaterThan(entity.get("date"), cb.localDate()), cb.equal(entity.get("id"), 4)));
            List<DateTimeEntity> data = em.createQuery(cq).getResultList();
            em.getTransaction().commit();
            MatcherAssert.assertThat(data.size(), Matchers.equalTo(0));
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }

    // Test CriteriaQuery with localDateTime in WHERE condition.
    // Strategy: SELECT DateTimeEntity WHERE time column < localDateTime AND id = :id
    //           LocalDate in entity is set to 1.1.1970 0:00:00.
    @Test
    public void testCriteriaQueryWhereLocalDateTime() {
        final EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<DateTimeEntity> cq = cb.createQuery(DateTimeEntity.class);
            Root<DateTimeEntity> entity = cq.from(DateTimeEntity.class);
            cq.select(entity);
            cq.where(cb.and(cb.lessThan(entity.get("datetime"), cb.localDateTime()), cb.equal(entity.get("id"), 4)));
            em.createQuery(cq).getSingleResult();
            em.getTransaction().commit();
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }

    // Test CriteriaQuery with localDateTime in WHERE condition with no record matching it.
    // Strategy: SELECT DateTimeEntity WHERE time column > localDateTime AND id = :id
    //           LocalDate in entity is set to 1.1.1970 0:00:00.
    // Such a record does not exist so empty result is expected
    @Test
    public void testCriteriaQueryWhereLocalDateTimeReturnsEmpty() {
        final EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<DateTimeEntity> cq = cb.createQuery(DateTimeEntity.class);
            Root<DateTimeEntity> entity = cq.from(DateTimeEntity.class);
            cq.select(entity);
            cq.where(cb.and(cb.greaterThan(entity.get("datetime"), cb.localDateTime()), cb.equal(entity.get("id"), 4)));
            List<DateTimeEntity> data = em.createQuery(cq).getResultList();
            em.getTransaction().commit();
            MatcherAssert.assertThat(data.size(), Matchers.equalTo(0));
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }

    // Test CriteriaQuery with localTime in SELECT column list
    @Test
    public void testCriteriaQuerySelectLocalTime() {
        final EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<LocalTime> cq = cb.createQuery(LocalTime.class);
            Root<DateTimeEntity> entity = cq.from(DateTimeEntity.class);
            cq.select(cb.localTime());
            cq.where(cb.equal(entity.get("id"), 4));
            LocalTime time = em.createQuery(cq).getSingleResult();
            em.getTransaction().commit();
            long diffMilis = Duration.between(time, LocalTime.now().plusSeconds(1 + dbOffset)).toMillis();
            // Positive value means that test did not pass midnight.
            if (diffMilis > 0) {
                MatcherAssert.assertThat(diffMilis, Matchers.lessThan(30000L));
            // Midnight pass correction.
            } else {
                MatcherAssert.assertThat(86400000L + diffMilis, Matchers.lessThan(30000L));
            }
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }

    // Test CriteriaQuery with localDate in SELECT column list
    @Test
    public void testCriteriaQuerySelectLocalDate() {
        final EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<LocalDate> cq = cb.createQuery(LocalDate.class);
            Root<DateTimeEntity> entity = cq.from(DateTimeEntity.class);
            cq.select(cb.localDate());
            cq.where(cb.equal(entity.get("id"), 4));
            LocalDate date = em.createQuery(cq).getSingleResult();
            em.getTransaction().commit();
            Period diff = Period.between(date, LocalDate.now());
            MatcherAssert.assertThat(diff.getYears(), Matchers.equalTo(0));
            MatcherAssert.assertThat(diff.getMonths(), Matchers.equalTo(0));
            MatcherAssert.assertThat(diff.getDays(), Matchers.lessThan(2));
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }

    // Test CriteriaQuery with localDateTime in SELECT column list
    @Test
    public void testCriteriaQuerySelectLocalDateTime() {
        final EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<LocalDateTime> cq = cb.createQuery(LocalDateTime.class);
            Root<DateTimeEntity> entity = cq.from(DateTimeEntity.class);
            cq.select(cb.localDateTime());
            cq.where(cb.equal(entity.get("id"), 4));
            LocalDateTime datetime = em.createQuery(cq).getSingleResult();
            em.getTransaction().commit();
            long diffMilis = Duration.between(datetime, LocalDateTime.now().plusSeconds(dbOffset + 1)).toMillis();
            MatcherAssert.assertThat(diffMilis, Matchers.lessThan(30000L));
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }

}
