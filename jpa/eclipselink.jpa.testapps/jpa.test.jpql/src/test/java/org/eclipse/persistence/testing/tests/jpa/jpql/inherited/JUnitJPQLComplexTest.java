/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
//
//     04/11/2017-2.6 Will Dazey
//       - 512386: Add constructor initialization with CONCAT test
//     01/23/2018-2.7 Will Dazey
//       - 530214: trim operation should not bind parameters
package org.eclipse.persistence.testing.tests.jpa.jpql.inherited;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.jpa.JpaEntityManager;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.testing.framework.jpa.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.advanced.holders.EmployeeDetail;
import org.eclipse.persistence.testing.models.jpa.inherited.Accredidation;
import org.eclipse.persistence.testing.models.jpa.inherited.Becks;
import org.eclipse.persistence.testing.models.jpa.inherited.BecksTag;
import org.eclipse.persistence.testing.models.jpa.inherited.BeerConsumer;
import org.eclipse.persistence.testing.models.jpa.inherited.Birthday;
import org.eclipse.persistence.testing.models.jpa.inherited.Blue;
import org.eclipse.persistence.testing.models.jpa.inherited.Corona;
import org.eclipse.persistence.testing.models.jpa.inherited.CoronaTag;
import org.eclipse.persistence.testing.models.jpa.inherited.ExpertBeerConsumer;
import org.eclipse.persistence.testing.models.jpa.inherited.InheritedTableManager;
import org.eclipse.persistence.testing.models.jpa.inherited.TelephoneNumber;
import org.eclipse.persistence.testing.tests.jpa.jpql.JUnitDomainObjectComparer;
import org.junit.Assert;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * <p>
 * <b>Purpose</b>: Test complex EJBQL functionality.
 * <p>
 * <b>Description</b>: This class creates a test suite, initializes the database
 * and adds tests to the suite.
 * <p>
 * <b>Responsibilities</b>:
 * <ul>
 * <li> Run tests for complex EJBQL functionality
 * </ul>
 * @see JUnitDomainObjectComparer
 */

public class JUnitJPQLComplexTest extends JUnitTestCase
{
    static JUnitDomainObjectComparer comparer;        //the global comparer object used in all tests

    public JUnitJPQLComplexTest()
    {
        super();
    }

    public JUnitJPQLComplexTest(String name) {
        super(name);
        setPuName(getPersistenceUnitName());
    }

    @Override
    public String getPersistenceUnitName() {
        return "inherited";
    }

    //This method is run at the end of EVERY test case method
    @Override
    public void tearDown()
    {
        clearCache();
    }

    //This suite contains all tests contained in this class
    public static Test suite()
    {
        TestSuite suite = new TestSuite();
        suite.setName("JUnitJPQLComplexTest");
        suite.addTest(new JUnitJPQLComplexTest("testSetup"));

        List<String> tests = new ArrayList<>();
        tests.add("complexConstructorMapTest");
        tests.add("mapContainerPolicyMapKeyInSelectTest");
        tests.add("mapContainerPolicyMapValueInSelectTest");
        tests.add("mapContainerPolicyMapEntryInSelectTest");
        tests.add("mapContainerPolicyMapKeyInSelectionCriteriaTest");
        tests.add("mapContainerPolicyMapValueInSelectionCriteriaTest");
        tests.add("mappedKeyMapContainerPolicyMapKeyInSelectionCriteriaTest");
        tests.add("mappedKeyMapContainerPolicyMapKeyInSelectTest");
        tests.add("mappedKeyMapContainerPolicyMapEntryInSelectTest");
        tests.add("mappedKeyMapContainerPolicyEmbeddableMapKeyInSelectionCriteriaTest");
        tests.add("mappedKeyMapContainerPolicyElementCollectionSelectionCriteriaTest");
        tests.add("mappedKeyMapContainerPolicyNavigateMapKeyInEntityTest");
        tests.add("mappedKeyMapContainerPolicyNavigateMapKeyInEmbeddableTest");
        tests.add("complexIndexOfInSelectClauseTest");
        tests.add("complexIndexOfInWhereClauseTest");

        Collections.sort(tests);
        for (String test : tests) {
            suite.addTest(new JUnitJPQLComplexTest(test));
        }
        return suite;
    }

    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
        clearCache();
        //get session to start setup
        DatabaseSession session = getPersistenceUnitServerSession();

        //initialize the global comparer object
        comparer = new JUnitDomainObjectComparer();

        //set the session for the comparer to use
        comparer.setSession((AbstractSession)session.getActiveSession());

        new InheritedTableManager().replaceTables(session);
    }

    public void complexConstructorMapTest()
    {
//        if (isOnServer()) {
//            // Not work on the server.
//            return;
//        }
        JpaEntityManager em = createEntityManager().unwrap(JpaEntityManager.class);

        beginTransaction(em);
        BeerConsumer consumer = new BeerConsumer();
        consumer.setName("Marvin Monroe");
        em.persist(consumer);
        Blue blue = new Blue();
        blue.setAlcoholContent(5.0f);
        blue.setUniqueKey(BigInteger.ONE);
        consumer.addBlueBeerToConsume(blue);
        em.persist(blue);
        em.flush();


        // constructor query using a map key
        String jpqlString = "SELECT NEW org.eclipse.persistence.testing.models.jpa.advanced.holders.EmployeeDetail('Mel', 'Ott', Key(b)) FROM BeerConsumer bc join bc.blueBeersToConsume b";
        Query query = em.createQuery(jpqlString);
        EmployeeDetail result = (EmployeeDetail)query.getSingleResult();
        EmployeeDetail expectedResult = new EmployeeDetail("Mel", "Ott", BigInteger.ONE);

        rollbackTransaction(em);
        Assert.assertEquals("Constructor with variable argument Test Case Failed", result, expectedResult);
    }

    public void complexIndexOfInSelectClauseTest(){
        EntityManager em = createEntityManager();
        beginTransaction(em);
        ExpertBeerConsumer consumer = new ExpertBeerConsumer();
        consumer.setAccredidation(new Accredidation());
        consumer.getDesignations().add("guru");
        consumer.getDesignations().add("beer-meister");
        em.persist(consumer);
        em.flush();
        List<Integer> expectedResult = new ArrayList<>();
        expectedResult.add(0);
        expectedResult.add(1);
        clearCache();
        String ejbqlString = "select index(d) from EXPERT_CONSUMER e join e.designations d";

        List<?> result = em.createQuery(ejbqlString).getResultList();

        rollbackTransaction(em);
        Assert.assertTrue("complexIndexOfInSelectClauseTest failed", comparer.compareObjects(result, expectedResult));
    }

    public void complexIndexOfInWhereClauseTest(){
        EntityManager em = createEntityManager();
        beginTransaction(em);
        ExpertBeerConsumer consumer = new ExpertBeerConsumer();
        consumer.setAccredidation(new Accredidation());
        consumer.getDesignations().add("guru");
        consumer.getDesignations().add("beer-meister");
        em.persist(consumer);
        em.flush();
        String expectedResult = "guru";
        clearCache();
        String ejbqlString = "select d from EXPERT_CONSUMER e join e.designations d where index(d) = 0";

        String result = (String)em.createQuery(ejbqlString).getSingleResult();

        rollbackTransaction(em);
        Assert.assertEquals("complexIndexOfInWhereClauseTest failed", result, expectedResult);
    }

    public void mapContainerPolicyMapKeyInSelectTest(){
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {

        BeerConsumer consumer = new BeerConsumer();
        consumer.setName("Marvin Monroe");
        em.persist(consumer);
        Blue blue = new Blue();
        blue.setAlcoholContent(5.0f);
        blue.setUniqueKey(BigInteger.ONE);
        consumer.addBlueBeerToConsume(blue);
        em.persist(blue);
        BeerConsumer consumer2 = new BeerConsumer();
        consumer2.setName("Marvin Monroe2");
        em.persist(consumer2);
        Blue blue2 = new Blue();
        blue2.setAlcoholContent(5.0f);
        blue2.setUniqueKey(BigInteger.valueOf(2));
        consumer2.addBlueBeerToConsume(blue2);
        em.persist(blue2);
        em.flush();
        Vector<BigInteger> expectedResult = new Vector<>();
        expectedResult.add(BigInteger.ONE);

        clearCache();
        String ejbqlString = "SELECT KEY(b) FROM BeerConsumer bc join bc.blueBeersToConsume b where bc.name = 'Marvin Monroe'";

        List<?> result = em.createQuery(ejbqlString).getResultList();
        Assert.assertTrue("mapContainerPolicyMapKeyInSelectTest failed", comparer.compareObjects(result, expectedResult));

        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

    public void mapContainerPolicyMapValueInSelectTest(){
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {

        BeerConsumer consumer = new BeerConsumer();
        consumer.setName("Marvin Monroe");
        em.persist(consumer);
        Blue blue = new Blue();
        blue.setAlcoholContent(5.0f);
        blue.setUniqueKey(BigInteger.ONE);
        consumer.addBlueBeerToConsume(blue);
        em.persist(blue);
        BeerConsumer consumer2 = new BeerConsumer();
        consumer2.setName("Marvin Monroe2");
        em.persist(consumer2);
        Blue blue2 = new Blue();
        blue2.setAlcoholContent(5.0f);
        blue2.setUniqueKey(BigInteger.valueOf(2));
        consumer2.addBlueBeerToConsume(blue2);
        em.persist(blue2);
        em.flush();
        Vector<Blue> expectedResult = new Vector<>();
        expectedResult.add(blue);

        clearCache();
        String ejbqlString = "SELECT VALUE(b) FROM BeerConsumer bc join bc.blueBeersToConsume b where bc.name = 'Marvin Monroe'";

        List<?> result = em.createQuery(ejbqlString).getResultList();
        Assert.assertTrue("mapContainerPolicyMapValueInSelectTest failed", comparer.compareObjects(result, expectedResult));

        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

    public void mapContainerPolicyMapEntryInSelectTest(){
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {

        BeerConsumer consumer = new BeerConsumer();
        consumer.setName("Marvin Monroe");
        em.persist(consumer);
        Blue blue = new Blue();
        blue.setAlcoholContent(5.0f);
        blue.setUniqueKey(BigInteger.ONE);
        consumer.addBlueBeerToConsume(blue);
        em.persist(blue);
        BeerConsumer consumer2 = new BeerConsumer();
        consumer2.setName("Marvin Monroe2");
        em.persist(consumer2);
        Blue blue2 = new Blue();
        blue2.setAlcoholContent(5.0f);
        blue2.setUniqueKey(BigInteger.valueOf(2));
        consumer2.addBlueBeerToConsume(blue2);
        em.persist(blue2);
        em.flush();


        clearCache();
        String ejbqlString = "SELECT ENTRY(b) FROM BeerConsumer bc join bc.blueBeersToConsume b where bc.name = 'Marvin Monroe'";

        List<?> result = em.createQuery(ejbqlString).getResultList();

            Assert.assertEquals("Incorrect number of values returned", 1, result.size());
        Assert.assertTrue("Did not return a Map.Entry", result.get(0) instanceof Map.Entry);
        Map.Entry<?, ?> entry = (Map.Entry<?, ?>)result.get(0);
            Assert.assertEquals("Keys do not match", entry.getKey(), BigInteger.ONE);
        Assert.assertTrue("Values do not match", comparer.compareObjects(entry.getValue(), blue));

        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

    public void mapContainerPolicyMapKeyInSelectionCriteriaTest(){
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {

        BeerConsumer consumer = new BeerConsumer();
        consumer.setName("Marvin Monroe");
        em.persist(consumer);
        Blue blue = new Blue();
        blue.setAlcoholContent(5.0f);
        blue.setUniqueKey(BigInteger.ONE);
        consumer.addBlueBeerToConsume(blue);
        em.persist(blue);
        BeerConsumer consumer2 = new BeerConsumer();
        consumer2.setName("Marvin Monroe2");
        em.persist(consumer2);
        Blue blue2 = new Blue();
        blue2.setAlcoholContent(5.0f);
        blue2.setUniqueKey(BigInteger.valueOf(2));
        consumer2.addBlueBeerToConsume(blue2);
        em.persist(blue2);
        em.flush();
        Vector<BeerConsumer> expectedResult = new Vector<>();
        expectedResult.add(consumer);

        clearCache();
        String ejbqlString = "SELECT bc FROM BeerConsumer bc join bc.blueBeersToConsume b where KEY(b) = 1";

        List<?> result = em.createQuery(ejbqlString).getResultList();
        Assert.assertTrue("mapContainerPolicyMapKeyInSelectionCriteriaTest failed", comparer.compareObjects(result, expectedResult));

        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

    public void mapContainerPolicyMapValueInSelectionCriteriaTest(){
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {

        BeerConsumer consumer = new BeerConsumer();
        consumer.setName("Marvin Monroe");
        em.persist(consumer);
        Blue blue = new Blue();
        blue.setAlcoholContent(5.0f);
        blue.setUniqueKey(BigInteger.ONE);
        consumer.addBlueBeerToConsume(blue);
        em.persist(blue);
        BeerConsumer consumer2 = new BeerConsumer();
        consumer2.setName("Marvin Monroe2");
        em.persist(consumer2);
        Blue blue2 = new Blue();
        blue2.setAlcoholContent(5.0f);
        blue2.setUniqueKey(BigInteger.valueOf(2));
        consumer2.addBlueBeerToConsume(blue2);
        em.persist(blue2);
        em.flush();
        Vector<BeerConsumer> expectedResult = new Vector<>();
        expectedResult.add(consumer);

        clearCache();
        String ejbqlString = "SELECT bc FROM BeerConsumer bc join bc.blueBeersToConsume b where VALUE(b).uniqueKey = 1";

        List<?> result = em.createQuery(ejbqlString).getResultList();
        Assert.assertTrue("mapContainerPolicyMapValueInSelectionCriteriaTest failed", comparer.compareObjects(result, expectedResult));

        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

    public void mappedKeyMapContainerPolicyMapKeyInSelectionCriteriaTest(){
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {

        BeerConsumer consumer = new BeerConsumer();
        consumer.setName("Marvin Monroe");
        em.persist(consumer);
        Becks becks = new Becks();
        becks.setAlcoholContent(5.0);
        BecksTag tag = new BecksTag();
        tag.setCallNumber("123");
        consumer.addBecksBeerToConsume(becks, tag);
        em.persist(becks);
        em.persist(tag);
        BeerConsumer consumer2 = new BeerConsumer();
        consumer.setName("Marvin Monroe2");
        em.persist(consumer2);
        Becks becks2 = new Becks();
        becks2.setAlcoholContent(5.0);
        BecksTag tag2 = new BecksTag();
        tag2.setCallNumber("1234");
        consumer2.addBecksBeerToConsume(becks2, tag2);
        em.persist(becks2);
        em.persist(tag2);
        em.flush();
        Vector<BeerConsumer> expectedResult = new Vector<>();
        expectedResult.add(consumer);

        clearCache();
        String ejbqlString = "SELECT bc FROM BeerConsumer bc join bc.becksBeersToConsume b where Key(b).callNumber = '123'";

        List<?> result = em.createQuery(ejbqlString).getResultList();

        Assert.assertTrue("mappedKeyMapContainerPolicyMapKeyInSelectionCriteriaTest failed", comparer.compareObjects(result, expectedResult));

        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

    public void mappedKeyMapContainerPolicyMapKeyInSelectTest(){
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {

        BeerConsumer consumer = new BeerConsumer();
        consumer.setName("Marvin Monroe");
        em.persist(consumer);
        Becks becks = new Becks();
        becks.setAlcoholContent(5.0);
        BecksTag tag = new BecksTag();
        tag.setCallNumber("123");
        consumer.addBecksBeerToConsume(becks, tag);
        em.persist(becks);
        em.persist(tag);
        BeerConsumer consumer2 = new BeerConsumer();
        consumer.setName("Marvin Monroe2");
        em.persist(consumer2);
        Becks becks2 = new Becks();
        becks2.setAlcoholContent(5.0);
        BecksTag tag2 = new BecksTag();
        tag2.setCallNumber("1234");
        consumer2.addBecksBeerToConsume(becks2, tag2);
        em.persist(becks2);
        em.persist(tag2);
        em.flush();
        Vector<BecksTag> expectedResult = new Vector<>();
        expectedResult.add(tag);

        clearCache();
        String ejbqlString = "SELECT Key(b) FROM BeerConsumer bc join bc.becksBeersToConsume b where Key(b).callNumber = '123'";

        List<?> result = em.createQuery(ejbqlString).getResultList();

        Assert.assertTrue("mappedKeyMapContainerPolicyMapKeyInSelectTest failed", comparer.compareObjects(result, expectedResult));

        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

    public void mappedKeyMapContainerPolicyMapEntryInSelectTest(){
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {

        BeerConsumer consumer = new BeerConsumer();
        consumer.setName("Marvin Monroe");
        em.persist(consumer);
        Becks becks = new Becks();
        becks.setAlcoholContent(5.0);
        BecksTag tag = new BecksTag();
        tag.setCallNumber("123");
        consumer.addBecksBeerToConsume(becks, tag);
        em.persist(becks);
        em.persist(tag);
        BeerConsumer consumer2 = new BeerConsumer();
        consumer2.setName("Marvin Monroe2");
        em.persist(consumer2);
        Becks becks2 = new Becks();
        becks2.setAlcoholContent(5.0);
        BecksTag tag2 = new BecksTag();
        tag2.setCallNumber("1234");
        consumer2.addBecksBeerToConsume(becks2, tag2);
        em.persist(becks2);
        em.persist(tag2);
        em.flush();

        clearCache();
        String ejbqlString = "SELECT ENTRY(b) FROM BeerConsumer bc join bc.becksBeersToConsume b where Key(b) = :becksTag";

        List<?> result = em.createQuery(ejbqlString).setParameter("becksTag", tag).getResultList();

            Assert.assertEquals("Incorrect number of values returned", 1, result.size());
        Assert.assertTrue("Did not return a Map.Entry", result.get(0) instanceof Map.Entry);
        Map.Entry<?, ?> entry = (Map.Entry<?, ?>)result.get(0);
        Assert.assertTrue("Keys do not match", comparer.compareObjects(entry.getKey(), tag));
        Assert.assertTrue("Values do not match", comparer.compareObjects(entry.getValue(), becks));

        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

    public void mappedKeyMapContainerPolicyEmbeddableMapKeyInSelectionCriteriaTest(){
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {

        BeerConsumer consumer = new BeerConsumer();
        consumer.setName("Marvin Monroe");
        em.persist(consumer);
        Corona corona = new Corona();
        corona.setAlcoholContent(5.0);
        CoronaTag tag = new CoronaTag();
        tag.setCode("123");
        tag.setNumber(123);
        consumer.addCoronaBeerToConsume(corona, tag);
        em.persist(corona);
        BeerConsumer consumer2 = new BeerConsumer();
        consumer2.setName("Marvin Monroe2");
        em.persist(consumer2);
        Corona corona2 = new Corona();
        corona2.setAlcoholContent(5.0);
        CoronaTag tag2 = new CoronaTag();
        tag2.setCode("1234");
        tag2.setNumber(1234);
        consumer2.addCoronaBeerToConsume(corona2, tag2);
        em.persist(corona2);
        em.flush();
        Vector<BeerConsumer> expectedResult = new Vector<>();
        expectedResult.add(consumer);

        clearCache();
        String ejbqlString = "SELECT bc FROM BeerConsumer bc join bc.coronaBeersToConsume b where Key(b).code = :key";

        List<?> result = em.createQuery(ejbqlString).setParameter("key", "123").getResultList();

        Assert.assertTrue("mappedKeyMapContainerPolicyEmbeddableMapKeyInSelectionCriteriaTest failed", comparer.compareObjects(result, expectedResult));

        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

    public void mappedKeyMapContainerPolicyElementCollectionSelectionCriteriaTest(){
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {

        ExpertBeerConsumer consumer = new ExpertBeerConsumer();
        consumer.setAccredidation(new Accredidation());
        consumer.setName("Marvin Monroe");
        Birthday bday = new Birthday();
        bday.setDay(25);
        bday.setMonth(6);
        bday.setYear(2009);
        consumer.addCelebration(bday, "Lots of Cake!");
        ExpertBeerConsumer consumer2 = new ExpertBeerConsumer();
        consumer2.setAccredidation(new Accredidation());
        consumer2.setName("Marvin Monroe2");
        Birthday bday2 = new Birthday();
        bday2.setDay(25);
        bday2.setMonth(6);
        bday2.setYear(2001);
        consumer2.addCelebration(bday, "Lots of food!");

        em.persist(consumer);
        em.flush();
        Vector<BeerConsumer> expectedResult = new Vector<>();
        expectedResult.add(consumer);

        clearCache();
        String ejbqlString = "SELECT bc FROM EXPERT_CONSUMER bc join bc.celebrations c where Key(c).day = :celebration";

        List<?> result = em.createQuery(ejbqlString).setParameter("celebration", 25).getResultList();

        Assert.assertTrue("mappedKeyMapContainerPolicyElementCollctionSelectionCriteriaTest failed", comparer.compareObjects(result, expectedResult));

        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

    public void mappedKeyMapContainerPolicyNavigateMapKeyInEntityTest(){
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {

        BeerConsumer consumer = new BeerConsumer();
        consumer.setName("Marvin Monroe");
        em.persist(consumer);
        Becks becks = new Becks();
        becks.setAlcoholContent(5.0);
        BecksTag tag = new BecksTag();
        tag.setCallNumber("123");
        consumer.addBecksBeerToConsume(becks, tag);
        em.persist(becks);
        em.persist(tag);
        BeerConsumer consumer2 = new BeerConsumer();
        consumer2.setName("Marvin Monroe2");
        em.persist(consumer2);
        Becks becks2 = new Becks();
        becks2.setAlcoholContent(5.0);
        BecksTag tag2 = new BecksTag();
        tag2.setCallNumber("1234");
        consumer2.addBecksBeerToConsume(becks2, tag2);
        em.persist(becks2);
        em.persist(tag2);
        em.flush();
        Vector<String> expectedResult = new Vector<>();
        expectedResult.add("123");

        clearCache();
        String ejbqlString = "SELECT KEY(becks).callNumber from BeerConsumer bc join bc.becksBeersToConsume becks where bc.name = 'Marvin Monroe'";

        List<?> result = em.createQuery(ejbqlString).getResultList();

        Assert.assertTrue("mappedKeyMapContainerPolicyNavigateMapKeyInEntityTest failed", comparer.compareObjects(result, expectedResult));

        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

    public void mappedKeyMapContainerPolicyNavigateMapKeyInEmbeddableTest(){
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {

        BeerConsumer consumer = new BeerConsumer();
        consumer.setName("Marvin Monroe");
        em.persist(consumer);
        Corona corona = new Corona();
        corona.setAlcoholContent(5.0);
        CoronaTag tag = new CoronaTag();
        tag.setCode("123");
        tag.setNumber(123);
        consumer.addCoronaBeerToConsume(corona, tag);
        em.persist(corona);
        BeerConsumer consumer2 = new BeerConsumer();
        consumer2.setName("Marvin Monroe2");
        em.persist(consumer2);
        Corona corona2 = new Corona();
        corona2.setAlcoholContent(5.0);
        CoronaTag tag2 = new CoronaTag();
        tag2.setCode("1234");
        tag2.setNumber(1234);
        consumer2.addCoronaBeerToConsume(corona2, tag2);
        em.persist(corona2);
        em.flush();
        Vector<String> expectedResult = new Vector<>();
        expectedResult.add("123");

        clearCache();
        String ejbqlString = "SELECT KEY(c).code from BeerConsumer bc join bc.coronaBeersToConsume c where bc.name = 'Marvin Monroe'";

        List<?> result = em.createQuery(ejbqlString).getResultList();

        Assert.assertTrue("mappedKeyMapContainerPolicyNavigateMapKeyInEmbeddableTest failed", comparer.compareObjects(result, expectedResult));

        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

    public void mappedContainerPolicyCompoundMapKeyTest(){
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {

        BeerConsumer consumer = new BeerConsumer();
        consumer.setName("Marvin Monroe");
        em.persist(consumer);
        TelephoneNumber number = new TelephoneNumber();
        number.setType("Home");
        number.setAreaCode("975");
        number.setNumber("1234567");
        em.persist(number);
        consumer.addTelephoneNumber(number);
        BeerConsumer consumer2 = new BeerConsumer();
        consumer2.setName("Marvin Monroe2");
        em.persist(consumer2);
        TelephoneNumber number2 = new TelephoneNumber();
        number2.setType("Home");
        number2.setAreaCode("974");
        number2.setNumber("1234567");
        em.persist(number2);
        consumer2.addTelephoneNumber(number2);
        em.flush();
        Vector<TelephoneNumber> expectedResult = new Vector<>();
        expectedResult.add(number);

        clearCache();
        String ejbqlString = "SELECT KEY(number) from BeerConsumer bc join bc.telephoneNumbers number where bc.name = 'Marvin Monroe'";

        List<?> result = em.createQuery(ejbqlString).getResultList();

        Assert.assertTrue("mappedContainerPolicyCompoundMapKeyTest failed", comparer.compareObjects(result, expectedResult));

        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

    // Bug 331124, GLASSFISH-19316
    // Test that join to element collections work.
    public void testElementCollection() {
        EntityManager em = createEntityManager();
        Query query = em.createQuery("Select b from BeerConsumer b join b.commentLookup c where KEY(c).number > 0");
        query.getResultList();
        query = em.createQuery("Select b from BeerConsumer b join b.commentLookup c where c <> ''");
        query.getResultList();
        query = em.createQuery("Select b from BeerConsumer b join b.redStripes r where r.alcoholContent > 0");
        query.getResultList();
        query = em.createQuery("Select b from BeerConsumer b join b.redStripes r where KEY(r) <> ''");
        query.getResultList();
        query = em.createQuery("Select b from BeerConsumer b join b.redStripesByAlcoholContent r where KEY(r) > 0");
        query.getResultList();
        query = em.createQuery("Select b from BeerConsumer b join b.redStripesByAlcoholContent r where r.alcoholContent > 0");
        query.getResultList();
        closeEntityManager(em);
    }

}
