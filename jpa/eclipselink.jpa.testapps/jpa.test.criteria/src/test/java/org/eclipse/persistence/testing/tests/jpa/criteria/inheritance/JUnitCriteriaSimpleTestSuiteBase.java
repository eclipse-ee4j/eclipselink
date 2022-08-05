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
//       JUnitCriteriaSimpleTestSuite must be kept in sync (to avoid only failing on WebSphere under Derby)
//       (ideally there should be only one copy of the code - the other suite should reference or subclass for changes)
//       see
//       org.eclipse.persistence.testing.tests.jpa.criteria.JUnitCriteriaSimpleTestSuite.simpleModTest():1796
//       org.eclipse.persistence.testing.tests.jpa.criteria.metamodel.JUnitCriteriaSimpleTestSuite.simpleModTest():1766
//       - 321902: this copied code should be renamed, merged or subclassed off the original
package org.eclipse.persistence.testing.tests.jpa.criteria.inheritance;

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
import org.eclipse.persistence.jpa.JpaEntityManager;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.testing.framework.jpa.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.inheritance.Car;
import org.eclipse.persistence.testing.models.jpa.inheritance.InheritanceTableCreator;
import org.eclipse.persistence.testing.models.jpa.inheritance.Person;
import org.eclipse.persistence.testing.models.jpa.inheritance.SportsCar;
import org.eclipse.persistence.testing.tests.jpa.criteria.JUnitDomainObjectComparer;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Map;

import static org.eclipse.persistence.testing.tests.jpa.criteria.inheritance.JUnitCriteriaSimpleTestSuiteBase.Attributes.Person_car;
import static org.eclipse.persistence.testing.tests.jpa.criteria.inheritance.JUnitCriteriaSimpleTestSuiteBase.Attributes.SportsCar_maxSpeed;

/**
 * @author cdelahun
 * Converted from JUnitJPQLSimpleTestSuite
 */
public abstract class JUnitCriteriaSimpleTestSuiteBase<T> extends JUnitTestCase {
    public enum Attributes {
        Person_car, SportsCar_maxSpeed
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
    }

    public JUnitCriteriaSimpleTestSuiteBase(String name) {
        super(name);
        setPuName(getPersistenceUnitName());
        setWrapper();
        populateAttributes();
    }

    @Override
    public String getPersistenceUnitName() {
        return "inheritance";
    }

    //This method is run at the end of EVERY test case method

    @Override
    public void tearDown() {
        clearCache();
    }

    //This suite contains all tests contained in this class

    public static <T extends JUnitCriteriaSimpleTestSuiteBase<?>> Test suite(Class<T> implementingClass) {
        TestSuite suite = new TestSuite();
        suite.setName("JUnitCriteriaSimpleTestSuite");

        try {
            Constructor<T> constructor = implementingClass.getConstructor(String.class);
            suite.addTest(constructor.newInstance("testSetup"));
            suite.addTest(constructor.newInstance("oneToOneCastTest"));
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

        new InheritanceTableCreator().replaceTables(session);
    }

    public void oneToOneCastTest(){
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {

            Person rudy = new Person();
            rudy.setName("Rudy");
            em.persist(rudy);
            SportsCar sportsCar = new SportsCar();
            sportsCar.setMaxSpeed(200);
            em.persist(sportsCar);
            rudy.setCar(sportsCar);

            Person theo = new Person();
            theo.setName("Theo");
            em.persist(theo);
            Car car = new Car();
            em.persist(car);
            theo.setCar(car);

            em.flush();
            em.clear();
            clearCache();

            ReadAllQuery query = new ReadAllQuery();
            Expression selectionCriteria = new ExpressionBuilder().get("car").treat(SportsCar.class).get("maxSpeed").equal(200);
            query.setSelectionCriteria(selectionCriteria);
            query.setReferenceClass(Person.class);
            query.dontUseDistinct();
            //query.setShouldFilterDuplicates(false);
            Query jpaQuery = em.unwrap(JpaEntityManager.class).createQuery(query);
            List<?> expectedResult = jpaQuery.getResultList();

            clearCache();
            em.clear();
            //"SELECT e from Employee e join cast(e.project, LargeProject) p where p.budget = 1000
            CriteriaBuilder qb1 = em.getCriteriaBuilder();
            CriteriaQuery<Person> cq1 = qb1.createQuery(Person.class);

            Root<Person> root = wrapper.from(cq1, Person.class);
            Join<Person, Car> join = wrapper.join(root, Person_car);
            jakarta.persistence.criteria.Expression<?> exp = wrapper.get((Path<SportsCar>)join.as(SportsCar.class), SportsCar_maxSpeed);
            cq1.where(qb1.equal(exp, 200) );

            List<Person> result = em.createQuery(cq1).getResultList();
            assertTrue("OneToOne cast failed.", comparer.compareObjects(result, expectedResult));
        } finally {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
    }
}


