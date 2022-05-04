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
//       - Issue 1442: Implement New Jakarta Persistence 3.1 Features
package org.eclipse.persistence.jpa.test.criteria;

import java.math.RoundingMode;
import java.text.DecimalFormat;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import org.eclipse.persistence.jpa.test.criteria.model.NumberEntity;
import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.framework.Property;
import org.eclipse.persistence.sessions.Session;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test math functions in CriteriaBuilder.
 * Added to Jakarta Persistence API as PR #351
 */
@RunWith(EmfRunner.class)
public class TestMathFunctions {

    @Emf(createTables = DDLGen.DROP_CREATE,
            classes = {
                    NumberEntity.class
            },
            properties = {
                    @Property(name = "eclipselink.cache.shared.default", value = "false"),
                    // This property remaps String from VARCHAR->NVARCHAR(or db equivalent)
                    @Property(name = "eclipselink.target-database-properties",
                            value = "UseNationalCharacterVaryingTypeForString=true"),
            })
    private EntityManagerFactory emf;

    private final NumberEntity[] NUMBER = {
            new NumberEntity(0, 0L, 0D),
            new NumberEntity(1, 1L, 1D),
            new NumberEntity(2, -1L, -1D),
            new NumberEntity(3, 42L, 42.42D),
            new NumberEntity(4, -342L, -342.42D),
            new NumberEntity(5, 4L, 4D),
            new NumberEntity(6, -4L, -4D),
            new NumberEntity(7, 4L, 14.23D),
            new NumberEntity(8, 6L, 44.7542383252D),
            new NumberEntity(9, 8L, -214.2457321233D)
    };

    @Before
    public void setup() {
        final EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            for (NumberEntity e : NUMBER) {
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
            em.createQuery("DELETE FROM NumberEntity e").executeUpdate();
            em.flush();
            em.getTransaction().commit();
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }

    // Verify result rounded to 10 decimal places as String operation.
    // Precision of Java and relational DB floating point operation may not be the same.
    private static void verifyRoundedDouble(final double expected, final double returned) {
        // Round values down to 10 decimal places
        DecimalFormat df = new DecimalFormat("#.##########");
        df.setRoundingMode(RoundingMode.DOWN);
        String expectedRounded = df.format(expected);
        String returnedRounded = df.format(returned);
        Assert.assertEquals(expectedRounded, returnedRounded);
    }

    // Call SELECT SIGN(n.longValue) FROM NumberEntity n WHERE n.id = id
    // using CriteriaQuery
    private static Integer callSign(final EntityManager em, final int id) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Integer> cq = cb.createQuery(Integer.class);
        Root<NumberEntity> number = cq.from(NumberEntity.class);
        cq.select(cb.sign(number.get("longValue")));
        cq.where(cb.equal(number.get("id"), id));
        return em.createQuery(cq).getSingleResult();
    }

    // Call SIGN(n) on n=0.
    @Test
    public void testSignMethodWithZero() {
        try (final EntityManager em = emf.createEntityManager()) {
            Integer result = callSign(em, 0);
            Assert.assertEquals(Integer.valueOf(0), result);
        }
    }

    // Call SIGN(n) on n>0.
    @Test
    public void testSignMethodWithPositive() {
        try (final EntityManager em = emf.createEntityManager()) {
            Integer result = callSign(em, 3);
            Assert.assertEquals(Integer.valueOf(1), result);
        }
    }

    // Call SIGN(n) on n<0.
    @Test
    public void testSignMethodWithNegative() {
        try (final EntityManager em = emf.createEntityManager()) {
            Integer result = callSign(em, 4);
            Assert.assertEquals(Integer.valueOf(-1), result);
        }
    }

    // Call SELECT CEILING(n.doubleValue) FROM NumberEntity n WHERE n.id = id
    // using CriteriaQuery
    private static Double callCeiling(final EntityManager em, final int id) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Double> cq = cb.createQuery(Double.class);
        Root<NumberEntity> number = cq.from(NumberEntity.class);
        cq.select(cb.ceiling(number.get("doubleValue")));
        cq.where(cb.equal(number.get("id"), id));
        return em.createQuery(cq).getSingleResult();
    }

    // Call CEILING(n) on n=0.
    @Test
    public void testCeilingMethodWithZero() {
        try (final EntityManager em = emf.createEntityManager()) {
            Double result = callCeiling(em, 0);
            Assert.assertEquals(Double.valueOf(0), result);
        }
    }

    // Call CEILING(n) on n>0.
    @Test
    public void testCeilingMethodWithPositive() {
        try (final EntityManager em = emf.createEntityManager()) {
            Double result = callCeiling(em, 3);
            Assert.assertEquals(
                    Double.valueOf(NUMBER[3].getLongValue().intValue()+1), result);
        }
    }

    // Call CEILING(n) on n<0.
    @Test
    public void testCeilingMethodWithNegative() {
        try (final EntityManager em = emf.createEntityManager()) {
            Double result = callCeiling(em, 4);
            Assert.assertEquals(
                    Double.valueOf(NUMBER[4].getLongValue().intValue()), result);
        }
    }

    // Call SELECT FLOOR(n.doubleValue) FROM NumberEntity n WHERE n.id = id
    // using CriteriaQuery
    private static Double callFloor(final EntityManager em, final int id) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Double> cq = cb.createQuery(Double.class);
        Root<NumberEntity> number = cq.from(NumberEntity.class);
        cq.select(cb.floor(number.get("doubleValue")));
        cq.where(cb.equal(number.get("id"), id));
        return em.createQuery(cq).getSingleResult();
    }

    // Call FLOOR(n) on n=0.
    @Test
    public void testFloorMethodWithZero() {
        try (final EntityManager em = emf.createEntityManager()) {
            Double result = callFloor(em, 0);
            Assert.assertEquals(Double.valueOf(0), result);
        }
    }

    // Call FLOOR(n) on n>0.
    @Test
    public void testFloorMethodWithPositive() {
        try (final EntityManager em = emf.createEntityManager()) {
            Double result = callFloor(em, 3);
            Assert.assertEquals(
                    Double.valueOf(NUMBER[3].getLongValue().intValue()), result);
        }
    }

    // Call FLOOR(n) on n<0.
    @Test
    public void testFloorMethodWithNegative() {
        try (final EntityManager em = emf.createEntityManager()) {
            Double result = callFloor(em, 4);
            Assert.assertEquals(
                    Double.valueOf(NUMBER[4].getLongValue().intValue()-1), result);
        }
    }

    // Call SELECT EXP(n.doubleValue) FROM NumberEntity n WHERE n.id = id
    // using CriteriaQuery
    private static Double callExp(final EntityManager em, final int id) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Double> cq = cb.createQuery(Double.class);
        Root<NumberEntity> number = cq.from(NumberEntity.class);
        cq.select(cb.exp(number.get("doubleValue")));
        cq.where(cb.equal(number.get("id"), id));
        return em.createQuery(cq).getSingleResult();
    }

    // Call EXP(0).
    @Test
    public void testExpMethodWithZero() {
        try (final EntityManager em = emf.createEntityManager()) {
            Double result = callExp(em, 0);
            Assert.assertEquals(
                    Double.valueOf(Math.exp(NUMBER[0].getDoubleValue())), result);
        }
    }

    // Call EXP(1). Result is rounded down to 10 decimal places.
    @Test
    public void testExpMethodWithPlusOne() {
        try (final EntityManager em = emf.createEntityManager()) {
            verifyRoundedDouble(Math.exp(NUMBER[1].getDoubleValue()), callExp(em, 1));
        }
    }

    // Call EXP(-1). Result is rounded down to 10 decimal places.
    @Test
    public void testExpMethodWithMinusOne() {
        try (final EntityManager em = emf.createEntityManager()) {
            verifyRoundedDouble(Math.exp(NUMBER[2].getDoubleValue()), callExp(em, 2));
        }
    }

    // Call EXP(n) on n>0. Result is rounded down to 10 decimal places.
    @Test
    public void testExpMethodWithPositive() {
        try (final EntityManager em = emf.createEntityManager()) {
            verifyRoundedDouble(Math.exp(NUMBER[5].getDoubleValue()), callExp(em, 5));
        }
    }

    // Call EXP(n) on n<0. Result is rounded down to 10 decimal places.
    @Test
    public void testExpMethodWithNegative() {
        try (final EntityManager em = emf.createEntityManager()) {
            verifyRoundedDouble(Math.exp(NUMBER[6].getDoubleValue()), callExp(em, 6));
        }
    }

    // Call SELECT LN(n.doubleValue) FROM NumberEntity n WHERE n.id = id
    // using CriteriaQuery
    private static Double callLn(final EntityManager em, final int id) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Double> cq = cb.createQuery(Double.class);
        Root<NumberEntity> number = cq.from(NumberEntity.class);
        cq.select(cb.ln(number.get("doubleValue")));
        cq.where(cb.equal(number.get("id"), id));
        return em.createQuery(cq).getSingleResult();
    }

    // Call LN(0). Domain of f(x): y = ln(x) is (0,infinity) so it shall throw an exception or return null.
    @Test
    public void testLnMethodWithZero() {
        try (final EntityManager em = emf.createEntityManager()) {
            Double result = callLn(em, 0);
            Assert.assertNull(result);
        } catch (PersistenceException pe) {
            // Expected to be thrown
        }
    }

    // Call LN(1). Result is rounded down to 10 decimal places.
    @Test
    public void testLnMethodWithPlusOne() {
        try (final EntityManager em = emf.createEntityManager()) {
            verifyRoundedDouble(Math.log(NUMBER[1].getDoubleValue()), callLn(em, 1));
        }
    }

    // Call LN(-1). Domain of f(x): y = ln(x) is (0,infinity) so it shall throw an exception or return null.
    @Test
    public void testLnMethodWithMinusOne() {
        try (final EntityManager em = emf.createEntityManager()) {
            Double result = callLn(em, 2);
            Assert.assertNull(result);
        } catch (PersistenceException pe) {
            // Expected to be thrown
        }
    }

    // Call LN(n) on n>0. Result is rounded down to 10 decimal places.
    @Test
    public void testLnMethodWithPositive() {
        try (final EntityManager em = emf.createEntityManager()) {
            verifyRoundedDouble(Math.log(NUMBER[5].getDoubleValue()), callLn(em, 5));
        }
    }

    // Call LN(n) on n<0. Domain of f(x): y = ln(x) is (0,infinity) so it shall throw an exception or return null.
    @Test
    public void testLnMethodWithNegative() {
        try (final EntityManager em = emf.createEntityManager()) {
            Double result = callLn(em, 6);
            Assert.assertNull(result);
        } catch (PersistenceException pe) {
            // Expected to be thrown
        }
    }

    // Call SELECT POWER(n.:field, exponent) FROM NumberEntity n WHERE n.id = id
    // using CriteriaQuery
    // Matches Expression<Double> power(Expression<? extends Number> x, Number y) prototype
    private static Double callPower(final EntityManager em, final int exponent, final int id, final String field) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Double> cq = cb.createQuery(Double.class);
        Root<NumberEntity> number = cq.from(NumberEntity.class);
        cq.select(cb.power(number.get(field), exponent));
        cq.where(cb.equal(number.get("id"), id));
        return em.createQuery(cq).getSingleResult();
    }

    // Call POWER(n.longValue, 2) on long n=0.
    @Test
    public void testPower2MethodWithZeroBase() {
        // org.apache.derby.client.am.SqlException: The resulting value is outside the range for the data type DOUBLE.
        Assume.assumeFalse(emf.unwrap(Session.class).getPlatform().isDerby());
        try (final EntityManager em = emf.createEntityManager()) {
            Double result = callPower(em, 2, 0, "longValue");
            Assert.assertEquals(
                    Double.valueOf(Math.pow(NUMBER[0].getLongValue(), 2)), result);
        }
    }

    // Call POWER(n.longValue, 2) on long n>0.
    @Test
    public void testPower2MethodWithPositiveLongBase() {
        try (final EntityManager em = emf.createEntityManager()) {
            Double result = callPower(em, 2, 3, "longValue");
            Assert.assertEquals(
                    Double.valueOf(Math.pow(NUMBER[3].getLongValue(), 2)), Double.valueOf(Math.round(result)));
        }
    }

    // Call POWER(n.doubleValue, 2) on double n>0.
    @Test
    public void testPower2MethodWithPositiveDoubleBase() {
        try (final EntityManager em = emf.createEntityManager()) {
            Double result = callPower(em, 2, 3, "doubleValue");
            if (emf.unwrap(Session.class).getPlatform().isDerby()) {
                MatcherAssert.assertThat(
                        Math.abs(Math.pow(NUMBER[3].getDoubleValue(), 2) - result), Matchers.lessThan(0.0000001d));
            } else {
                Assert.assertEquals(
                        Double.valueOf(Math.pow(NUMBER[3].getDoubleValue(), 2)), result);
            }
        }
    }

    // Call POWER(n.longValue, 2) on long n<0.
    @Test
    public void testPower2MethodWithNegativeLongBase() {
        // org.apache.derby.client.am.SqlException: The resulting value is outside the range for the data type DOUBLE.
        Assume.assumeFalse(emf.unwrap(Session.class).getPlatform().isDerby());
        try (final EntityManager em = emf.createEntityManager()) {
            Double result = callPower(em, 2, 4, "longValue");
            Assert.assertEquals(
                    Double.valueOf(Math.pow(NUMBER[4].getLongValue(), 2)), result);
        }
    }

    // Call POWER(n.doubleValue, 2) on double n<0.
    @Test
    public void testPower2MethodWithNegativeDoubleBase() {
        // org.apache.derby.client.am.SqlException: The resulting value is outside the range for the data type DOUBLE.
        Assume.assumeFalse(emf.unwrap(Session.class).getPlatform().isDerby());
        try (final EntityManager em = emf.createEntityManager()) {
            Double result = callPower(em, 2, 4, "doubleValue");
            Assert.assertEquals(
                    Double.valueOf(Math.pow(NUMBER[4].getDoubleValue(), 2)), result);
        }
    }

    // Call POWER(n.longValue, 3) on long n<0.
    @Test
    public void testPower3MethodWithNegativeLongBase() {
        // org.apache.derby.client.am.SqlException: The resulting value is outside the range for the data type DOUBLE.
        Assume.assumeFalse(emf.unwrap(Session.class).getPlatform().isDerby());
        try (final EntityManager em = emf.createEntityManager()) {
            Double result = callPower(em, 3, 4, "longValue");
            Assert.assertEquals(
                    Double.valueOf(Math.pow(NUMBER[4].getLongValue(), 3)), result);
        }
    }

    // Call POWER(n.doubleValue, 3) on double n<0.
    @Test
    public void testPower3MethodWithNegativeDoubleBase() {
        // org.apache.derby.client.am.SqlException: The resulting value is outside the range for the data type DOUBLE.
        Assume.assumeFalse(emf.unwrap(Session.class).getPlatform().isDerby());
        try (final EntityManager em = emf.createEntityManager()) {
            Double result = callPower(em, 3, 4, "doubleValue");
            Assert.assertEquals(
                    Double.valueOf(Math.pow(NUMBER[4].getDoubleValue(), 3)), result);
        }
    }

    // Call SELECT POWER(n.doubleValue, n.longValue) FROM NumberEntity n WHERE n.id = id
    // using CriteriaQuery
    // Matches Expression<Double> power(Expression<? extends Number> x, Expression<? extends Number> y) prototype
    private static Double callExprPower(final EntityManager em, final int id) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Double> cq = cb.createQuery(Double.class);
        Root<NumberEntity> number = cq.from(NumberEntity.class);
        cq.select(cb.power(number.get("doubleValue"), number.get("longValue")));
        cq.where(cb.equal(number.get("id"), id));
        return em.createQuery(cq).getSingleResult();
    }

    // Call POWER(n.doubleValue, n.longValue) on id=7: [14.23D,4L] (result fits in double with no exponent).
    @Test
    public void testPowerMethodWithPositiveArgs() {
        try (final EntityManager em = emf.createEntityManager()) {
            Double result = callExprPower(em, 7);
            if (emf.unwrap(Session.class).getPlatform().isDerby()) {
                MatcherAssert.assertThat(
                        Math.abs(Math.pow(NUMBER[7].getDoubleValue(), NUMBER[7].getLongValue()) - result),
                        Matchers.lessThan(0.0000001d));
            } else {
                Assert.assertEquals(
                        Double.valueOf(Math.pow(NUMBER[7].getDoubleValue(), NUMBER[7].getLongValue())), result);
            }
        }
    }

    // Call SELECT ROUND(n.doubleValue, d) FROM NumberEntity n WHERE n.id = id
    // using CriteriaQuery
    // Matches Expression<Double> power(Expression<? extends Number> x, Number y) prototype
    private static Double callRound(final EntityManager em, final int d, final int id) {
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Double> cq = cb.createQuery(Double.class);
            Root<NumberEntity> number = cq.from(NumberEntity.class);
            cq.select(cb.round(number.get("doubleValue"), d));
            cq.where(cb.equal(number.get("id"), id));
            return em.createQuery(cq).getSingleResult();
        } catch (Throwable t) {
            t.printStackTrace();
            throw t;
        }
    }

    // Call ROUND(n) on n>0.
    @Test
    public void testRoundMethodWithPositive() {
        try (final EntityManager em = emf.createEntityManager()) {
            Double result = callRound(em, 6,8);
            Assert.assertEquals(Double.valueOf(44.754238D), result);
        }
    }

    // Call ROUND(n) on n<0.
    @Test
    public void testRoundMethodWithNegative() {
        try (final EntityManager em = emf.createEntityManager()) {
            Double result = callRound(em, 6, 9);
            Assert.assertEquals(Double.valueOf(-214.245732D), result);
        }
    }

}
