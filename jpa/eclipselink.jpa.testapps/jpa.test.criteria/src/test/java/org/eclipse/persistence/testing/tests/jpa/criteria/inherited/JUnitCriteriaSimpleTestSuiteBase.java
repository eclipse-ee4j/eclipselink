/*
 * Copyright (c) 1998, 2022 Oracle and/or its affiliates. All rights reserved.
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
//     Jun 29, 2009-1.0M6 Chris Delahunt
//       - TODO Bug#: Bug Description
//     07/05/2010-2.1.1 Michael O'Brien
//       - 321716: modelgen and jpa versions of duplicate code in both copies of
//       JUnitCriteriaSimpleTest must be kept in sync (to avoid only failing on WebSphere under Derby)
//       (ideally there should be only one copy of the code - the other suite should reference or subclass for changes)
//       see
//       org.eclipse.persistence.testing.tests.jpa.criteria.JUnitCriteriaSimpleTest.simpleModTest():1796
//       org.eclipse.persistence.testing.tests.jpa.criteria.metamodel.JUnitCriteriaSimpleTest.simpleModTest():1766
//       - 321902: this copied code should be renamed, merged or subclassed off the original
package org.eclipse.persistence.testing.tests.jpa.criteria.inherited;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Fetch;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.testing.framework.jpa.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.inherited.BeerConsumer;
import org.eclipse.persistence.testing.models.jpa.inherited.Blue;
import org.eclipse.persistence.testing.models.jpa.inherited.BlueLight;
import org.eclipse.persistence.testing.models.jpa.inherited.InheritedTableManager;
import org.eclipse.persistence.testing.tests.jpa.criteria.JUnitDomainObjectComparer;

import java.lang.reflect.Constructor;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import static org.eclipse.persistence.testing.tests.jpa.criteria.inherited.JUnitCriteriaSimpleTestSuiteBase.Attributes.BeerConsumer_blueBeersToConsume;
import static org.eclipse.persistence.testing.tests.jpa.criteria.inherited.JUnitCriteriaSimpleTestSuiteBase.Attributes.BlueLight_discount;

/**
 * @author cdelahun
 * Converted from JUnitJPQLSimpleTestSuite
 */
public abstract class JUnitCriteriaSimpleTestSuiteBase<T> extends JUnitTestCase {
    public enum Attributes {
        BeerConsumer_blueBeersToConsume, BlueLight_discount
    }

    protected interface CriteriaQueryWrapper {
        <X,Y> Root<X> from(CriteriaQuery<Y> query, Class<X> entityClass);
        <X,Y> Fetch<X,Y> fetch(Root<X> root, Attributes attributeKey, JoinType joinType);
        <X,Y> jakarta.persistence.criteria.Expression<Y> get(Path<X> path, Attributes attributeKey);
        <X,Y> Join<X,Y> join(Root<X> root, Attributes attributeKey);
        <X,Y> Join<X,Y> join(Root<X> root, Attributes attributeKey, JoinType joinType);
    }

    protected CriteriaQueryWrapper wrapper;

    protected abstract void setWrapper();

    protected Map<Attributes,T> attributes;

    protected abstract void populateAttributes();

    static JUnitDomainObjectComparer comparer; //the global comparer object used in all tests

    public JUnitCriteriaSimpleTestSuiteBase() {
        super();
        setWrapper();
        populateAttributes();
        setPuName(getPersistenceUnitName());
    }

    public JUnitCriteriaSimpleTestSuiteBase(String name) {
        super(name);
        setWrapper();
        populateAttributes();
        setPuName(getPersistenceUnitName());
    }

    @Override
    public String getPersistenceUnitName() {
        return "inherited";
    }

    //This method is run at the end of EVERY test case method

    @Override
    public void tearDown() {
        clearCache();
    }

    //This suite contains all tests contained in this class

    public static <T extends JUnitCriteriaSimpleTestSuiteBase> Test suite(Class<T> implementingClass) {
        TestSuite suite = new TestSuite();
        suite.setName("JUnitCriteriaSimpleTest");

        try {
            Constructor<T> constructor = implementingClass.getConstructor(String.class);
            suite.addTest(constructor.newInstance("testSetup"));
            suite.addTest(constructor.newInstance("mapCastTest"));
        }
        catch (Exception x) {
            fail(Helper.printStackTraceToString(x));
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

    public void mapCastTest(){
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {

            BeerConsumer bc1 = new BeerConsumer();
            bc1.setName("George");
            em.persist(bc1);
            Blue blue = new Blue();
            blue.setUniqueKey(new BigInteger("1"));
            em.persist(blue);
            bc1.addBlueBeerToConsume(blue);
            blue.setBeerConsumer(bc1);

            BeerConsumer bc2 = new BeerConsumer();
            bc2.setName("Scott");
            em.persist(bc2);
            BlueLight blueLight = new BlueLight();
            blueLight.setDiscount(10);
            blueLight.setUniqueKey(new BigInteger("2"));
            em.persist(blueLight);
            blueLight.setBeerConsumer(bc2);
            bc2.addBlueBeerToConsume(blueLight);

            em.flush();
            em.clear();
            clearCache();

            ReadAllQuery query = new ReadAllQuery();
            Expression selectionCriteria = new ExpressionBuilder().anyOf("blueBeersToConsume").treat(BlueLight.class).get("discount").equal(10);
            query.setSelectionCriteria(selectionCriteria);
            query.setReferenceClass(BeerConsumer.class);
            query.dontUseDistinct();

            Query jpaQuery = ((org.eclipse.persistence.internal.jpa.EntityManagerImpl)em.getDelegate()).createQuery(query);
            List<?> expectedResult = jpaQuery.getResultList();

            clearCache();
            em.clear();

            //"SELECT e from Employee e join cast(e.project, LargeProject) p where p.budget = 1000
            CriteriaBuilder qb1 = em.getCriteriaBuilder();
            CriteriaQuery<BeerConsumer> cq1 = qb1.createQuery(BeerConsumer.class);

            Root<BeerConsumer> root = wrapper.from(cq1, BeerConsumer.class);
            Join<BeerConsumer, Blue> join = wrapper.join(root, BeerConsumer_blueBeersToConsume);
            jakarta.persistence.criteria.Expression<?> exp = wrapper.get((Path<?>)join.as(BlueLight.class), BlueLight_discount);
            cq1.where(qb1.equal(exp, 10) );

            List<BeerConsumer> result = em.createQuery(cq1).getResultList();
            assertTrue("LargeProject cast failed.", comparer.compareObjects(result, expectedResult));
        } finally {
            if (isTransactionActive(em)) {
                this.rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
    }
}
