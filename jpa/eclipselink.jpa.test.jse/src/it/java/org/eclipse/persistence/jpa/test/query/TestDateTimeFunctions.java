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
//     03/09/2022: Tomas Kraus
//       - Issue 1442: Implement New Jakarta Persistence 3.1 Features
package org.eclipse.persistence.jpa.test.query;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;

import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.framework.Property;
import org.eclipse.persistence.jpa.test.query.model.DateTimeQueryEntity;
import org.eclipse.persistence.sessions.Session;
import org.junit.After;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test {@code LocalTime}/{@code LocalDate}/{@code LocalDateTime} functions in queries.
 * Added to JPA-API as PR #352
 */
@RunWith(EmfRunner.class)
public class TestDateTimeFunctions {
    @Emf(createTables = DDLGen.DROP_CREATE,
            classes = {
                    DateTimeQueryEntity.class
            },
            properties = {
                    @Property(name = "eclipselink.cache.shared.default", value = "false"),
                    // This property remaps String from VARCHAR->NVARCHAR(or db equivalent)
                    @Property(name = "eclipselink.target-database-properties",
                            value = "UseNationalCharacterVaryingTypeForString=true"),
            })
    private EntityManagerFactory emf;

    private final LocalDateTime[] TS = {
            LocalDateTime.of(2022, 3, 9, 14, 30, 25, 0),
            LocalDateTime.now(),
            LocalDateTime.of(2022, 06, 07, 12, 0),
            LocalDateTime.of(2022, 7, 28, 12, 32, 46, 123456789)
    };

    private final DateTimeQueryEntity[] ENTITY = {
            new DateTimeQueryEntity(1, TS[0].toLocalTime(), TS[0].toLocalDate(), TS[0]),
            new DateTimeQueryEntity(2, TS[1].toLocalTime(), TS[1].toLocalDate(), TS[1]),
            new DateTimeQueryEntity(3, TS[2].toLocalTime(), TS[2].toLocalDate(), TS[2]),
            new DateTimeQueryEntity(4, TS[3].toLocalTime(), TS[3].toLocalDate(), TS[3]),
    };

    @Before
    public void setup() {
        final EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            for (DateTimeQueryEntity e : ENTITY) {
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
    }

    @After
    public void cleanup() {
        final EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM DateTimeQueryEntity e").executeUpdate();
            em.flush();
            em.getTransaction().commit();
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }

    // Test JPQL EXTRACT(YEAR FROM date)
    @Test
    public void testCriteriaExtractYearFromDate() {
        final EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            TypedQuery<Number> q = em.createQuery("SELECT EXTRACT(YEAR FROM e.dateValue) FROM DateTimeQueryEntity e WHERE e.id = :id", Number.class);
            q.setParameter("id", 1);
            long y = q.getSingleResult().longValue();
            em.getTransaction().commit();
            assertEquals(y, 2022L);
        } catch (Throwable t) {
            t.printStackTrace();
            throw t;
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }

    // Test JPQL EXTRACT(QUARTER FROM date)
    @Test
    public void testCriteriaExtractQuarterFromDate() {
        final EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            TypedQuery<Number> q = em.createQuery("SELECT EXTRACT(QUARTER FROM e.dateValue) FROM DateTimeQueryEntity e WHERE e.id = :id", Number.class);
            q.setParameter("id", 1);
            long y = q.getSingleResult().longValue();
            em.getTransaction().commit();
            assertEquals(y, 1L);
        } catch (Throwable t) {
            t.printStackTrace();
            throw t;
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }

    // Test JPQL EXTRACT(MONTH FROM date)
    @Test
    public void testCriteriaExtractMonthFromDate() {
        final EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            TypedQuery<Number> q = em.createQuery("SELECT EXTRACT(MONTH FROM e.dateValue) FROM DateTimeQueryEntity e WHERE e.id = :id", Number.class);
            q.setParameter("id", 1);
            long y = q.getSingleResult().longValue();
            em.getTransaction().commit();
            assertEquals(y, 3L);
        } catch (Throwable t) {
            t.printStackTrace();
            throw t;
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }

    // Test JPQL EXTRACT(DAY FROM date)
    @Test
    public void testCriteriaExtractDayFromDate() {
        final EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            TypedQuery<Number> q = em.createQuery("SELECT EXTRACT(DAY FROM e.dateValue) FROM DateTimeQueryEntity e WHERE e.id = :id", Number.class);
            q.setParameter("id", 1);
            long y = q.getSingleResult().longValue();
            em.getTransaction().commit();
            assertEquals(y, 9L);
        } catch (Throwable t) {
            t.printStackTrace();
            throw t;
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }

    // Test JPQL EXTRACT(HOUR FROM time)
    @Test
    public void testCriteriaExtractHourFromTime() {
        final EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            TypedQuery<Number> q = em.createQuery("SELECT EXTRACT(HOUR FROM e.timeValue) FROM DateTimeQueryEntity e WHERE e.id = :id", Number.class);
            q.setParameter("id", 1);
            long y = q.getSingleResult().longValue();
            em.getTransaction().commit();
            assertEquals(y, 14L);
        } catch (Throwable t) {
            t.printStackTrace();
            throw t;
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }

    // Test JPQL EXTRACT(MINUTE FROM time)
    @Test
    public void testCriteriaExtractMinuteFromTime() {
        final EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            TypedQuery<Number> q = em.createQuery("SELECT EXTRACT(MINUTE FROM e.timeValue) FROM DateTimeQueryEntity e WHERE e.id = :id", Number.class);
            q.setParameter("id", 1);
            long y = q.getSingleResult().longValue();
            em.getTransaction().commit();
            assertEquals(y, 30L);
        } catch (Throwable t) {
            t.printStackTrace();
            throw t;
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }

    // Test JPQL EXTRACT(SECOND FROM time)
    @Test
    public void testCriteriaExtractSecondFromTime() {
        final EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            TypedQuery<Number> q = em.createQuery("SELECT EXTRACT(SECOND FROM e.timeValue) FROM DateTimeQueryEntity e WHERE e.id = :id", Number.class);
            q.setParameter("id", 1);
            long y = q.getSingleResult().longValue();
            em.getTransaction().commit();
            assertEquals(y, 25L);
        } catch (Throwable t) {
            t.printStackTrace();
            throw t;
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }

    // Test JPQL EXTRACT(YEAR FROM datetime)
    @Test
    public void testCriteriaExtractYearFromDateTime() {
        final EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            TypedQuery<Number> q = em.createQuery("SELECT EXTRACT(YEAR FROM e.datetimeValue) FROM DateTimeQueryEntity e WHERE e.id = :id", Number.class);
            q.setParameter("id", 1);
            long y = q.getSingleResult().longValue();
            em.getTransaction().commit();
            assertEquals(y, 2022L);
        } catch (Throwable t) {
            t.printStackTrace();
            throw t;
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }

    // Test JPQL EXTRACT(QUARTER FROM datetime)
    @Test
    public void testCriteriaExtractQuarterFromDateTime() {
        final EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            TypedQuery<Number> q = em.createQuery("SELECT EXTRACT(QUARTER FROM e.datetimeValue) FROM DateTimeQueryEntity e WHERE e.id = :id", Number.class);
            q.setParameter("id", 1);
            long y = q.getSingleResult().longValue();
            em.getTransaction().commit();
            assertEquals(y, 1L);
        } catch (Throwable t) {
            t.printStackTrace();
            throw t;
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }

    // Test JPQL EXTRACT(MONTH FROM datetime)
    @Test
    public void testCriteriaExtractMonthFromDateTime() {
        final EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            TypedQuery<Number> q = em.createQuery("SELECT EXTRACT(MONTH FROM e.datetimeValue) FROM DateTimeQueryEntity e WHERE e.id = :id", Number.class);
            q.setParameter("id", 1);
            long y = q.getSingleResult().longValue();
            em.getTransaction().commit();
            assertEquals(y, 3L);
        } catch (Throwable t) {
            t.printStackTrace();
            throw t;
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }

    // Test JPQL EXTRACT(DAY FROM datetime)
    @Test
    public void testCriteriaExtractDayFromDateTime() {
        final EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            TypedQuery<Number> q = em.createQuery("SELECT EXTRACT(DAY FROM e.datetimeValue) FROM DateTimeQueryEntity e WHERE e.id = :id", Number.class);
            q.setParameter("id", 1);
            long y = q.getSingleResult().longValue();
            em.getTransaction().commit();
            assertEquals(y, 9L);
        } catch (Throwable t) {
            t.printStackTrace();
            throw t;
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }

    // Test JPQL EXTRACT(HOUR FROM datetime)
    @Test
    public void testCriteriaExtractHourFromDateTime() {
        final EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            TypedQuery<Number> q = em.createQuery("SELECT EXTRACT(HOUR FROM e.datetimeValue) FROM DateTimeQueryEntity e WHERE e.id = :id", Number.class);
            q.setParameter("id", 1);
            long y = q.getSingleResult().longValue();
            em.getTransaction().commit();
            assertEquals(y, 14L);
        } catch (Throwable t) {
            t.printStackTrace();
            throw t;
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }

    // Test JPQL EXTRACT(MINUTE FROM datetime)
    @Test
    public void testCriteriaExtractMinuteFromDateTime() {
        final EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            TypedQuery<Number> q = em.createQuery("SELECT EXTRACT(MINUTE FROM e.datetimeValue) FROM DateTimeQueryEntity e WHERE e.id = :id", Number.class);
            q.setParameter("id", 1);
            long y = q.getSingleResult().longValue();
            em.getTransaction().commit();
            assertEquals(y, 30L);
        } catch (Throwable t) {
            t.printStackTrace();
            throw t;
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }

    // Test JPQL EXTRACT(SECOND FROM datetime)
    @Test
    public void testCriteriaExtractSecondFromDateTime() {
        final EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            TypedQuery<Number> q = em.createQuery("SELECT EXTRACT(SECOND FROM e.datetimeValue) FROM DateTimeQueryEntity e WHERE e.id = :id", Number.class);
            q.setParameter("id", 1);
            long y = q.getSingleResult().longValue();
            em.getTransaction().commit();
            assertEquals(y, 25L);
        } catch (Throwable t) {
            t.printStackTrace();
            throw t;
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }

    // Test LocalDateTime.now() in WHERE clause
    // SELECT e FROM DateTimeQueryEntity e WHERE e.datetime = :dateTime
    @Test
    public void testLocalDateTime() {
        final EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            TypedQuery<DateTimeQueryEntity> query = em.createNamedQuery("DateTimeQueryEntity.findByLocalDateTime", DateTimeQueryEntity.class);
            query.setParameter("datetimeValue", TS[1]);
            List<DateTimeQueryEntity> result = query.getResultList();
            em.getTransaction().commit();
            assertEquals(result.size(), 1);
        } catch (Throwable t) {
            t.printStackTrace();
            throw t;
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }

    // Test JPQL query from issue 1540
    @Test
    public void testIssue1539LocalDate() {
        final EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            // Record with ID = 1 contains year 2022
            TypedQuery<Number> query = em.createQuery("SELECT EXTRACT(YEAR FROM qdte.dateValue) FROM DateTimeQueryEntity qdte WHERE qdte.id = 1", Number.class);
            Number year = query.getSingleResult();
            em.getTransaction().commit();
            // Check returned year value
            assertEquals(year.intValue(),2022);
        } catch (Throwable t) {
            t.printStackTrace();
            throw t;
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }

    // Test JPQL query from issue 1540
    @Test
    public void testIssue1539LocalDateTime() {
        final EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            // Record with ID = 1 contains year 2022
            TypedQuery<DateTimeQueryEntity> query = em.createQuery("SELECT qdte FROM DateTimeQueryEntity qdte WHERE EXTRACT(YEAR FROM qdte.dateValue) = 2022", DateTimeQueryEntity.class);
            List<DateTimeQueryEntity> result = query.getResultList();
            em.getTransaction().commit();
            // Returned list of values must contain record with ID = 1.
            boolean found = false;
            for (DateTimeQueryEntity item : result) {
                if (item.getId() == 1) {
                    found = true;
                }
            }
            assertTrue("Record with ID = 1 containing year 2022 was not found", found);
        } catch (Throwable t) {
            t.printStackTrace();
            throw t;
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }

    // Test JPQL EXTRACT(WEEK FROM date) to check whether ISO_WEEK is used in MS SQL - issue 1550
    @Test
    public void testIssue1550ExtractIsoWeek() {
        // Derby does not support WEEK
        Assume.assumeFalse(emf.unwrap(Session.class).getPlatform().isDerby());
        final EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            TypedQuery<Number> q = em.createQuery("SELECT EXTRACT(WEEK FROM qdte.dateValue) FROM DateTimeQueryEntity qdte WHERE qdte.id = :id", Number.class);
            q.setParameter("id", 3);
            long y = q.getSingleResult().longValue();
            em.getTransaction().commit();
            assertEquals(y, 23L);
        } catch (Throwable t) {
            t.printStackTrace();
            throw t;
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }

    // Test whether JPQL EXTRACT for YEAR/QUARTER/MONTH/WEEK/DAY returns an integer - issue 1574
    @Test
    public void testIssue1574LocalDate() {
        final EntityManager em = emf.createEntityManager();
        final String[] extractOps = emf.unwrap(Session.class).getPlatform().isDerby()
                ? new String[] {"YEAR", "QUARTER", "MONTH", "DAY"}
                : new String[] {"YEAR", "QUARTER", "MONTH", "WEEK", "DAY"};
        try {
            em.getTransaction().begin();
            for (String operand : extractOps) {
                TypedQuery<Number> query = em.createQuery("SELECT EXTRACT(" + operand + " FROM qdte.dateValue) FROM DateTimeQueryEntity qdte WHERE qdte.id = 1", Number.class);
                Number value = query.getSingleResult();
                assertTrue(value instanceof Integer || value instanceof Long);
            }
            em.getTransaction().commit();
        } catch (Throwable t) {
            t.printStackTrace();
            throw t;
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }

    // Test whether JPQL EXTRACT for HOUR/MINUTE returns an integer - issue 1574
    @Test
    public void testIssue1574LocalTime() {
        final EntityManager em = emf.createEntityManager();
        final String[] extractOps = new String[] {"HOUR", "MINUTE"};
        try {
            em.getTransaction().begin();
            for (String operand : extractOps) {
                TypedQuery<Number> query = em.createQuery("SELECT EXTRACT(" + operand + " FROM qdte.timeValue) FROM DateTimeQueryEntity qdte WHERE qdte.id = 1", Number.class);
                Number value = query.getSingleResult();
                assertTrue(value instanceof Integer);
            }
            em.getTransaction().commit();
        } catch (Throwable t) {
            t.printStackTrace();
            throw t;
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }

    // Test whether JPQL EXTRACT for SECOND returns a floating point - issue 1574
    @Test
    public void testIssue1574LocalTimeSecond() {
        final EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            TypedQuery<Number> query = em.createQuery("SELECT EXTRACT(SECOND FROM qdte.timeValue) FROM DateTimeQueryEntity qdte WHERE qdte.id = 1", Number.class);
            Number value = query.getSingleResult();
            assertTrue(value instanceof Double);
            em.getTransaction().commit();
        } catch (Throwable t) {
            t.printStackTrace();
            throw t;
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }

    // Test whether JPQL EXTRACT for SECOND returns a floating point with proper nanoseconds fraction
    @Test
    public void testLocalTimeSecondFraction() {
        // Derby does not support SECOND fractions
        Assume.assumeFalse(emf.unwrap(Session.class).getPlatform().isDerby());
        final EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            TypedQuery<Number> query = em.createQuery("SELECT EXTRACT(SECOND FROM qdte.timeValue) FROM DateTimeQueryEntity qdte WHERE qdte.id = 4", Number.class);
            Number value = query.getSingleResult();
            assertTrue(value instanceof Double);
            double secWithNs = (double)TS[3].getNano() / 1000000000 + TS[3].getSecond();
            double diff = Math.abs(secWithNs - value.doubleValue());
            assertTrue(diff < 0.000001);
            em.getTransaction().commit();
        } catch (Throwable t) {
            t.printStackTrace();
            throw t;
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }
}
