/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     tware - initial API and implementation as part of Query Downcast feature
package org.eclipse.persistence.testing.tests.jpa.advanced;

import java.math.BigInteger;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.jpa.JpaQuery;
import org.eclipse.persistence.jpa.JpaEntityManager;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.queries.ReportQuery;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.inheritance.Jalopy;
import org.eclipse.persistence.testing.models.jpa.inheritance.OffRoadTireInfo;
import org.eclipse.persistence.testing.models.jpa.inheritance.PassengerPerformanceTireInfo;
import org.eclipse.persistence.testing.models.jpa.inheritance.PerformanceTireInfo;
import org.eclipse.persistence.testing.models.jpa.inheritance.Person;
import org.eclipse.persistence.testing.models.jpa.inheritance.Bus;
import org.eclipse.persistence.testing.models.jpa.inheritance.NonFueledVehicle;
import org.eclipse.persistence.testing.models.jpa.inheritance.SportsCar;
import org.eclipse.persistence.testing.models.jpa.inheritance.TireInfo;
import org.eclipse.persistence.testing.models.jpa.inheritance.Vehicle;
import org.eclipse.persistence.testing.models.jpa.advanced.AdvancedTableCreator;
import org.eclipse.persistence.testing.models.jpa.advanced.LargeProject;
import org.eclipse.persistence.testing.models.jpa.advanced.Employee;
import org.eclipse.persistence.testing.models.jpa.advanced.Project;
import org.eclipse.persistence.testing.models.jpa.advanced.SmallProject;
import org.eclipse.persistence.testing.models.jpa.inheritance.Boat;
import org.eclipse.persistence.testing.models.jpa.inheritance.Car;
import org.eclipse.persistence.testing.models.jpa.inheritance.Company;
import org.eclipse.persistence.testing.models.jpa.inheritance.InheritanceTableCreator;
import org.eclipse.persistence.testing.models.jpa.inherited.BeerConsumer;
import org.eclipse.persistence.testing.models.jpa.inherited.Blue;
import org.eclipse.persistence.testing.models.jpa.inherited.BlueLight;
import org.eclipse.persistence.testing.models.jpa.inherited.InheritedTableManager;

public class QueryCastTestSuite extends JUnitTestCase {

    public QueryCastTestSuite() {
        super();
    }

    public QueryCastTestSuite(String name) {
        super(name);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("QueryCastTestSuite");

        suite.addTest(new QueryCastTestSuite("testSetup"));
        suite.addTest(new QueryCastTestSuite("testDowncastOneToManyLeafQueryKey"));
        suite.addTest(new QueryCastTestSuite("testDowncastOneToManyMidHierarchyQueryKey"));
        suite.addTest(new QueryCastTestSuite("testDowncastManyToManyQueryKey"));
        suite.addTest(new QueryCastTestSuite("testDowncastOneToManyLeafExpressionBuilder"));
        suite.addTest(new QueryCastTestSuite("testDowncastOneToManyMidHierarchyExpressionBuilder"));
        suite.addTest(new QueryCastTestSuite("testDowncastManyToManyExpressionBuilder"));
        suite.addTest(new QueryCastTestSuite("testDowncastInSelect"));
        suite.addTest(new QueryCastTestSuite("testDowncastSingleTableQueryKey"));
        suite.addTest(new QueryCastTestSuite("testDoubleDowncastOneToManyLeafQueryKey"));
        suite.addTest(new QueryCastTestSuite("testDoubleDowncastSeparateClass"));
        suite.addTest(new QueryCastTestSuite("testDowncastRelationshipTraversal"));
        suite.addTest(new QueryCastTestSuite("testDoubleDowncastOneToOne"));
        suite.addTest(new QueryCastTestSuite("testSelectCast"));

        suite.addTest(new QueryCastTestSuite("testCastInSubselect"));
        suite.addTest(new QueryCastTestSuite("testDowncastWithFetchJoin"));
        suite.addTest(new QueryCastTestSuite("testDoubleTreatOnRoot"));
        suite.addTest(new QueryCastTestSuite("testDoubleTreatOnRootSTI"));
        suite.addTest(new QueryCastTestSuite("testTreatInSelect"));
        suite.addTest(new QueryCastTestSuite("testTreatInSelectSTI"));
        suite.addTest(new QueryCastTestSuite("testTreatInFrom"));
        suite.addTest(new QueryCastTestSuite("testTreatInFromSTI"));
        suite.addTest(new QueryCastTestSuite("testTreatInWhere"));
        suite.addTest(new QueryCastTestSuite("testTreatInWhereSTI"));
        suite.addTest(new QueryCastTestSuite("testTreatUsingAndOr"));
        suite.addTest(new QueryCastTestSuite("testTreatUsingAndOrSTI"));
        suite.addTest(new QueryCastTestSuite("testTreatUsingJoinOverDowncastRelationship"));
        suite.addTest(new QueryCastTestSuite("testReturningTypeOnTreat"));
        suite.addTest(new QueryCastTestSuite("testReturningTypeOnTreatSTI"));
        return suite;
    }

    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
        new AdvancedTableCreator().replaceTables(JUnitTestCase.getServerSession());
        new InheritanceTableCreator().replaceTables(JUnitTestCase.getServerSession());
        new InheritedTableManager().replaceTables(JUnitTestCase.getServerSession());
        // Force uppercase for Postgres.
        if (getServerSession().getPlatform().isPostgreSQL()) {
            getServerSession().getLogin().setShouldForceFieldNamesToUpperCase(true);
        }
    }

    public void testDowncastOneToManyLeafQueryKey(){
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {

            Company company = new Company();
            company.setName("Acme");
            em.persist(company);
            Boat boat = new Boat();
            boat.setModel("speed");
            em.persist(boat);
            Car car = new Car();
            car.setDescription("A car");
            em.persist(car);
            company.getVehicles().add(boat);
            boat.setOwner(company);
            company.getVehicles().add(car);
            car.setOwner(company);

            company = new Company();
            company.setName("WidgetCo");
            em.persist(company);
            boat = new Boat();
            boat.setModel("fishing");
            em.persist(boat);
            company.getVehicles().add(boat);
            boat.setOwner(company);
            em.flush();

            clearCache();
            em.clear();

            Query query = em.createQuery("Select c from Company c join treat(c.vehicles as Boat) b where b.model = 'speed'");
            List resultList = query.getResultList();

            assertTrue("Incorrect results returned, expected 1 but returned "+resultList.size(), resultList.size() == 1);
        } finally {
            if (this.isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
    }

    public void testDowncastOneToManyMidHierarchyQueryKey(){
        EntityManager em = createEntityManager();

        beginTransaction(em);
        try {
            Company company = new Company();
            company.setName("Acme");
            em.persist(company);
            Boat boat = new Boat();
            boat.setModel("speed");
            em.persist(boat);
            Car car = new Car();
            car.setDescription("A car");
            em.persist(car);
            NonFueledVehicle nfv = new NonFueledVehicle();
            nfv.setColor("Blue");
            em.persist(nfv);
            company.getVehicles().add(nfv);
            nfv.setOwner(company);
            company.getVehicles().add(boat);
            boat.setOwner(company);
            company.getVehicles().add(car);
            car.setOwner(company);

            company = new Company();
            company.setName("WidgetCo");
            em.persist(company);
            nfv = new NonFueledVehicle();
            nfv.setColor("Red");
            em.persist(nfv);
            company.getVehicles().add(nfv);
            nfv.setOwner(company);
            em.flush();

            clearCache();
            em.clear();

            Query query = em.createQuery("Select c from Company c join treat(c.vehicles as NonFueledVehicle) v where v.color = 'Blue'");
            List resultList = query.getResultList();

            assertTrue("Incorrect results returned", resultList.size() == 1);
        } finally {
            if (this.isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
    }

    public void testDowncastManyToManyQueryKey(){
        EntityManager em = createEntityManager();

        beginTransaction(em);
        try {
            LargeProject proj = new LargeProject();
            proj.setBudget(1000);
            proj.setName("test1");
            em.persist(proj);

            SmallProject sp = new SmallProject();
            sp.setName("sp1");
            em.persist(sp);

            Employee emp = new Employee();
            emp.setFirstName("Reggie");
            emp.setLastName("Josephson");
            emp.addProject(proj);
            proj.addTeamMember(emp);
            emp.addProject(sp);
            sp.addTeamMember(emp);
            em.persist(emp);

            emp = new Employee();
            emp.setFirstName("Ron");
            emp.setLastName("Josephson");
            emp.addProject(sp);
            sp.addTeamMember(emp);
            em.persist(emp);

            em.flush();

            clearCache();
            em.clear();

            Query query = em.createQuery("Select e from Employee e join treat(e.projects as LargeProject) p where p.budget > 100");
            List resultList = query.getResultList();

            assertTrue("Incorrect results returned", resultList.size() == 1);
        } finally {
            if (this.isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
    }

    public void testDowncastOneToManyLeafExpressionBuilder(){
        EntityManager em = createEntityManager();

        beginTransaction(em);
        try {
            Company company = new Company();
            company.setName("Acme");
            em.persist(company);
            Boat boat = new Boat();
            boat.setModel("speed");
            em.persist(boat);
            Car car = new Car();
            car.setDescription("A car");
            em.persist(car);
            company.getVehicles().add(boat);
            boat.setOwner(company);
            company.getVehicles().add(car);
            car.setOwner(company);

            company = new Company();
            company.setName("WidgetCo");
            em.persist(company);
            boat = new Boat();
            boat.setModel("fishing");
            em.persist(boat);
            company.getVehicles().add(boat);
            boat.setOwner(company);
            em.flush();


            clearCache();
            em.clear();
            // create a shell query using JPA and alter the expression criteria
            // this allows us to access the base of the query - we can't do that with a JPQL join - we can as of JPA 2.1
            JpaQuery query = (JpaQuery)em.createQuery("Select v from Vehicle v");
            ReadAllQuery raq = new ReadAllQuery(Vehicle.class);
            query.setDatabaseQuery(raq);
            Expression exp = raq.getExpressionBuilder();
            Expression  criteria = exp.treat(Boat.class).get("model").equal("speed");
            raq.setSelectionCriteria(criteria);
            List resultList = query.getResultList();

            assertTrue("Incorrect results returned", resultList.size() == 1);
        } finally {
            if (this.isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
    }

    public void testDowncastOneToManyMidHierarchyExpressionBuilder(){
        EntityManager em = createEntityManager();

        beginTransaction(em);
        try {
            Company company = new Company();
            company.setName("Acme");
            em.persist(company);
            Boat boat = new Boat();
            boat.setModel("speed");
            boat.setColor("Blue");
            em.persist(boat);
            Car car = new Car();
            car.setDescription("A car");
            em.persist(car);
            NonFueledVehicle nfv = new NonFueledVehicle();
            nfv.setColor("Blue");
            em.persist(nfv);
            company.getVehicles().add(nfv);
            nfv.setOwner(company);
            company.getVehicles().add(boat);
            boat.setOwner(company);
            company.getVehicles().add(car);
            car.setOwner(company);

            company = new Company();
            company.setName("WidgetCo");
            em.persist(company);
            nfv = new NonFueledVehicle();
            nfv.setColor("Red");
            em.persist(nfv);
            company.getVehicles().add(nfv);
            nfv.setOwner(company);
            em.flush();

            clearCache();
            em.clear();

            // create a shell query using JPA and alter the expression criteria
            // this allows us to access the base of the query - we can't do that with a JPQL join
            JpaQuery query = (JpaQuery)em.createQuery("Select v from Vehicle v");
            ReadAllQuery raq = new ReadAllQuery(Vehicle.class);
            query.setDatabaseQuery(raq);
            Expression exp = raq.getExpressionBuilder();
            Expression  criteria = exp.treat(NonFueledVehicle.class).get("color").equal("Blue");
            raq.setSelectionCriteria(criteria);
            List resultList = query.getResultList();

            assertTrue("Incorrect results returned", resultList.size() == 2);
        } finally {
            if (this.isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
    }

    public void testDowncastManyToManyExpressionBuilder(){
        EntityManager em = createEntityManager();

        beginTransaction(em);
        try {
            LargeProject proj = new LargeProject();
            proj.setBudget(1000);
            proj.setName("test1");
            em.persist(proj);

            SmallProject sp = new SmallProject();
            sp.setName("sp1");
            em.persist(sp);

            Employee emp = new Employee();
            emp.setFirstName("Reggie");
            emp.setLastName("Josephson");
            emp.addProject(proj);
            proj.addTeamMember(emp);
            emp.addProject(sp);
            sp.addTeamMember(emp);
            em.persist(emp);

            emp = new Employee();
            emp.setFirstName("Ron");
            emp.setLastName("Josephson");
            emp.addProject(sp);
            sp.addTeamMember(emp);
            em.persist(emp);

            em.flush();

            clearCache();
            em.clear();

            // create a shell query using JPA and alter the expression criteria
            // this allows us to access the base of the query
            JpaQuery query = (JpaQuery)em.createQuery("Select p from Project p");
            ReadAllQuery raq = new ReadAllQuery(Project.class);
            query.setDatabaseQuery(raq);
            Expression exp = raq.getExpressionBuilder();
            Expression criteria = exp.treat(LargeProject.class).get("budget").greaterThan(100);
            raq.setSelectionCriteria(criteria);
            List resultList = query.getResultList();

            assertTrue("Incorrect results returned", resultList.size() == 1);
        } finally {
            if (this.isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
    }

    public void testDowncastInSelect(){
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            LargeProject proj = new LargeProject();
            proj.setBudget(1000);
            proj.setName("test1");
            em.persist(proj);

            SmallProject sp = new SmallProject();
            sp.setName("sp1");
            em.persist(sp);
            em.flush();

            clearCache();
            em.clear();
            ExpressionBuilder builder = new ExpressionBuilder(Project.class);
            ReportQuery rq = new ReportQuery(Project.class, builder);
            rq.addAttribute("project", builder.treat(LargeProject.class).get("budget"));
            rq.setSelectionCriteria(builder.type().equal(LargeProject.class));
            List resultList = (List)((JpaEntityManager)em.getDelegate()).getActiveSession().executeQuery(rq);
            assertTrue("Incorrect results returned", resultList.size() == 1);

            //Test equivalent JPQL as well
            Query query = em.createQuery("Select treat(c as LargeProject).budget from Project c");
            List JPQLresultList = query.getResultList();
            assertTrue("Incorrect results returned", JPQLresultList.size() == 1);
        } finally {
            if (this.isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
    }

    public void testDowncastSingleTableQueryKey(){
        EntityManager em = createEntityManager();

        beginTransaction(em);
        try {
            BeerConsumer consumer = new BeerConsumer();
            consumer.setName("John");
            em.persist(consumer);
            Blue blue = new Blue();
            blue.setAlcoholContent(5f);
            blue.setUniqueKey(new BigInteger("4531"));
            em.persist(blue);

            BlueLight blueLight = new BlueLight();
            blueLight.setDiscount(10);
            blueLight.setUniqueKey(new BigInteger("4533"));
            em.persist(blueLight);

            consumer.addBlueBeerToConsume(blueLight);
            blueLight.setBeerConsumer(consumer);

            consumer.addBlueBeerToConsume(blue);
            consumer.addBlueBeerToConsume(blueLight);

            consumer = new BeerConsumer();
            consumer.setName("Frank");
            em.persist(consumer);

            blueLight = new BlueLight();
            blueLight.setDiscount(5);
            blueLight.setUniqueKey(new BigInteger("4532"));
            em.persist(blueLight);

            consumer.addBlueBeerToConsume(blueLight);
            blueLight.setBeerConsumer(consumer);

            em.flush();

            clearCache();
            em.clear();

            Query query = em.createQuery("Select b from BeerConsumer b join treat(b.blueBeersToConsume as BlueLight) bl where bl.discount = 10");
            List resultList = query.getResultList();

            assertTrue("Incorrect results returned", resultList.size() == 1);
        } finally {
            if (this.isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
    }

    public void testDoubleDowncastOneToManyLeafQueryKey(){
        EntityManager em = createEntityManager();

        beginTransaction(em);
        try {
            Company company = new Company();
            company.setName("Acme");
            em.persist(company);
            Boat boat = new Boat();
            boat.setModel("speed");
            em.persist(boat);
            Car car = new Car();
            car.setDescription("A car");
            car.setFuelType("unleaded");
            em.persist(car);
            company.getVehicles().add(boat);
            boat.setOwner(company);
            company.getVehicles().add(car);
            car.setOwner(company);

            company = new Company();
            company.setName("WidgetCo");
            em.persist(company);
            boat = new Boat();
            boat.setModel("fishing");
            em.persist(boat);
            company.getVehicles().add(boat);
            boat.setOwner(company);
            em.flush();

            clearCache();
            em.clear();

            Query query = em.createQuery("Select c from Company c join treat(c.vehicles as Boat) b where b.model = 'speed' or b.model = 'fishing'");
            List resultList = query.getResultList();

            assertTrue("Incorrect results returned", resultList.size() == 2);
        } finally {
            if (this.isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
    }

    public void testDoubleDowncastSeparateClass(){
        EntityManager em = createEntityManager();

        beginTransaction(em);
        try {
            Company company = new Company();
            company.setName("Acme");
            em.persist(company);
            Boat boat = new Boat();
            boat.setModel("speed");
            em.persist(boat);
            Car car = new Car();
            car.setDescription("A car");
            car.setFuelType("unleaded");
            em.persist(car);
            company.getVehicles().add(boat);
            boat.setOwner(company);
            company.getVehicles().add(car);
            car.setOwner(company);

            company = new Company();
            company.setName("WidgetCo");
            em.persist(company);
            boat = new Boat();
            boat.setModel("fishing");
            em.persist(boat);
            company.getVehicles().add(boat);
            boat.setOwner(company);
            em.flush();

            clearCache();
            em.clear();

            Query query = em.createQuery("Select distinct c from Company c left join treat(c.vehicles as Boat) b left join treat(c.vehicles as FueledVehicle) f where b.model = 'fishing' or f.fuelType = 'unleaded'");
            List resultList = query.getResultList();

            assertTrue("Incorrect results returned", resultList.size() == 2);
        } finally {
            if (this.isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
    }

    public void testDowncastRelationshipTraversal(){
        EntityManager em = createEntityManager();

        beginTransaction(em);
        try {
            Company company = new Company();
            company.setName("Acme");
            em.persist(company);
            Boat boat = new Boat();
            boat.setModel("speed");
            em.persist(boat);
            Bus bus = new Bus();
            bus.setDescription("A bus");
            bus.setFuelType("unleaded");
            em.persist(bus);
            Person person = new Person();
            person.setName("Driver");
            bus.setBusDriver(person);
            em.persist(person);

            company.getVehicles().add(boat);
            boat.setOwner(company);
            company.getVehicles().add(bus);
            bus.setOwner(company);

            company = new Company();
            company.setName("WidgetCo");
            em.persist(company);
            bus = new Bus();
            bus.setDescription("B bus");
            bus.setFuelType("unleaded");
            em.persist(bus);
            person = new Person();
            person.setName("Driver2");
            bus.setBusDriver(person);
            em.persist(person);
            company.getVehicles().add(boat);
            boat.setOwner(company);
            em.flush();

            clearCache();
            em.clear();

            Query query = em.createQuery("Select distinct c from Company c left join treat(c.vehicles as Bus) b where b.busDriver.name = 'Driver'");
            List resultList = query.getResultList();
            assertTrue("Incorrect results returned", resultList.size() == 1);
        } finally {
            if (this.isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
    }

    public void testDoubleDowncastOneToOne(){
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
            Jalopy car = new Jalopy();
            car.setColor("Red");
            car.setPercentRust(20);
            em.persist(car);
            theo.setCar(car);

            em.flush();

            clearCache();
            em.clear();

            // The following query casts across a 1-1
            // as a result of our 1-1 optimization, the same expression is used twice
            // causing an exception
            Query query = em.createQuery("Select distinct p from Person p left join treat(p.car as SportsCar) s left join treat(p.car as Jalopy) j where s.maxSpeed = 200 or j.percentRust = 20");
            List resultList = query.getResultList();
            assertTrue("Incorrect results returned", resultList.size() == 2);
        } finally {
            if (this.isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
    }

    public void testSelectCast(){
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            LargeProject proj = new LargeProject();
            proj.setBudget(1000);
            proj.setName("test1");
            em.persist(proj);

            LargeProject lp = new LargeProject();
            lp.setBudget(100);
            lp.setName("sp1");
            em.persist(lp);

            Employee emp = new Employee();
            emp.setFirstName("Reggie");
            emp.setLastName("Josephson");
            emp.addProject(proj);
            proj.addTeamMember(emp);
            emp.addProject(lp);
            lp.addTeamMember(emp);
            em.persist(emp);

            emp = new Employee();
            emp.setFirstName("Ron");
            emp.setLastName("Josephson");
            emp.addProject(lp);
            lp.addTeamMember(emp);
            em.persist(emp);
            em.flush();

            clearCache();
            em.clear();

            Query query = em.createQuery("Select max(l.budget) from Employee e join treat(e.projects as LargeProject) l");
            List resultList = query.getResultList();

            assertTrue("Incorrect result size returned", resultList.size() == 1);
            assertTrue("Incorrect results returned", (Double)resultList.get(0) == 1000);
        } finally {
            if (this.isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
    }

    public void testCastInSubselect(){
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            LargeProject proj = new LargeProject();
            proj.setBudget(1000);
            proj.setName("test1");
            em.persist(proj);

            LargeProject lp = new LargeProject();
            lp.setBudget(100);
            lp.setName("sp1");
            em.persist(lp);

            Employee emp = new Employee();
            emp.setFirstName("Reggie");
            emp.setLastName("Josephson");
            emp.addProject(proj);
            proj.addTeamMember(emp);
            emp.addProject(lp);
            lp.addTeamMember(emp);
            emp.setSalary(10000);
            em.persist(emp);

            emp = new Employee();
            emp.setFirstName("Ron");
            emp.setLastName("Josephson");
            emp.addProject(lp);
            lp.addTeamMember(emp);
            em.persist(emp);
            emp.setSalary(100);
            em.flush();

            clearCache();
            em.clear();

            Query query = em.createQuery("select e from Employee e where e.salary > (Select max(l.budget) from Employee emp join treat(emp.projects as LargeProject) l)");
            List resultList = query.getResultList();

            assertTrue("Incorrect result size returned", resultList.size() == 1);
        } finally {
            if (this.isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
    }

    public void testDowncastWithFetchJoin(){
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
            Jalopy car = new Jalopy();
            car.setColor("Red");
            car.setPercentRust(20);
            em.persist(car);
            theo.setCar(car);

            em.flush();

            clearCache();
            em.clear();

            Query query = em.createQuery("Select p from Person p join fetch p.car join treat(p.car as SportsCar) s where s.maxSpeed = 200");
            List resultList = query.getResultList();
            Person person = (Person)resultList.get(0);
            assertTrue("Incorrect result size returned", resultList.size() == 1);
            assertNotNull("The car was not fetched.", person.car);
        } finally {
            if (this.isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
    }

    public void testDoubleTreatOnRoot(){
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
            Jalopy car = new Jalopy();
            car.setColor("Red");
            car.setPercentRust(20);
            em.persist(car);
            theo.setCar(car);

            em.flush();

            clearCache();
            em.clear();

            // The following query casts the base expression twice
            Query query = em.createQuery("Select c from Car c where treat(c as SportsCar).maxSpeed = 200 or treat(c as Jalopy).percentRust = 20");
            List resultList = query.getResultList();
            assertTrue("Incorrect results returned", resultList.size() == 2);
        } finally {
            if (this.isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
    }

    public void testDoubleTreatOnRootSTI(){
        EntityManager em = createEntityManager();

        beginTransaction(em);
        try {
            PerformanceTireInfo pti110 = new PerformanceTireInfo();
            pti110.setSpeedRating(110);
            em.persist(pti110);

            PerformanceTireInfo pti120 = new PerformanceTireInfo();
            pti120.setSpeedRating(120);
            em.persist(pti120);

            PassengerPerformanceTireInfo ppti = new PassengerPerformanceTireInfo();
            ppti.setSpeedRating(120);
            em.persist(ppti);

            em.flush();

            clearCache();
            em.clear();

            // The following query casts the base expression twice
            Query query = em.createQuery("Select t from TireInfo t where treat(t as PerformanceTireInfo).speedRating = 110 or treat(t as PassengerPerformanceTireInfo).speedRating = 120");
            List resultList = query.getResultList();
            assertTrue("Incorrect results returned", resultList.size() == 2);
        } finally {
            if (this.isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
    }

    public void testTreatInSelect(){
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            LargeProject proj = new LargeProject();
            proj.setBudget(1000);
            proj.setName("test1");
            em.persist(proj);

            SmallProject sp = new SmallProject();
            sp.setName("sp1");
            em.persist(sp);
            em.flush();

            clearCache();
            em.clear();

            Query query = em.createQuery("Select treat(c as LargeProject).budget from Project c");
            List resultList = query.getResultList();
            assertTrue("Incorrect results returned, expected 1, received: "+resultList.size(), resultList.size() == 1);
        } finally {
            if (this.isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
    }

    public void testTreatInSelectSTI(){
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            PerformanceTireInfo pTire = new PerformanceTireInfo();
            pTire.setSpeedRating(100);
            pTire.setPressure(32);
            em.persist(pTire);

            TireInfo tire = new TireInfo();
            tire.setPressure(28);
            em.persist(tire);
            em.flush();

            clearCache();
            em.clear();
            Query query = em.createQuery("Select treat(c as PerformanceTireInfo).speedRating from TireInfo c");
            List resultList = query.getResultList();
            assertTrue("Incorrect results returned, expected 1, received: "+resultList.size(), resultList.size() == 1);
        } finally {
            if (this.isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
    }

    public void testTreatInFrom(){
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
            sportsCar.setUser(rudy);
            Company c = new Company();
            c.setName("test");
            em.persist(c);
            sportsCar.setOwner(c);
            c.getVehicles().add(sportsCar);

            Person theo = new Person();
            theo.setName("Theo");
            em.persist(theo);
            Jalopy car = new Jalopy();
            car.setColor("Red");
            car.setPercentRust(20);
            em.persist(car);
            theo.setCar(car);

            em.flush();
            clearCache();
            em.clear();

            Query query = em.createQuery("Select b.maxSpeed from Person o join treat(o.car as SportsCar) b");
            List resultList = query.getResultList();
            assertTrue("Incorrect results returned, expected 1 received:"+resultList.size(), resultList.size() == 1);
        } finally {
            if (this.isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
    }

    public void testTreatInFromSTI(){
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            Bus bus1 = new Bus();
            em.persist(bus1);
            PerformanceTireInfo pTire = new PerformanceTireInfo();
            pTire.setSpeedRating(110);
            pTire.setPressure(32);
            em.persist(pTire);
            bus1.getTires().add(pTire);

            Bus bus2 = new Bus();
            em.persist(bus2);
            TireInfo tire = new TireInfo();
            tire.setPressure(28);
            em.persist(tire);
            bus2.getTires().add(tire);
            PassengerPerformanceTireInfo otherTire = new PassengerPerformanceTireInfo();
            otherTire.setSpeedRating(100);
            otherTire.setPressure(31);
            em.persist(otherTire);
            bus2.getTires().add(otherTire);

            em.flush();
            clearCache();
            em.clear();

            Query query = em.createQuery("Select b.speedRating from Bus o join treat(o.tires as PerformanceTireInfo) b");

            List resultList = query.getResultList();
            assertTrue("Incorrect results returned, expected 2 received:"+resultList.size(), resultList.size() == 2);
        } finally {
            if (this.isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
    }

    public void testTreatInWhere(){
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
            sportsCar.setUser(rudy);
            Company c = new Company();
            c.setName("test");
            em.persist(c);
            sportsCar.setOwner(c);
            c.getVehicles().add(sportsCar);

            Person theo = new Person();
            theo.setName("Theo");
            em.persist(theo);
            Jalopy car = new Jalopy();
            car.setColor("Red");
            car.setPercentRust(20);
            em.persist(car);
            theo.setCar(car);

            em.flush();
            clearCache();
            em.clear();

            Query query = em.createQuery("Select o from Person o where treat(o.car as SportsCar).maxSpeed = 200");
            List resultList = query.getResultList();

            assertTrue("Incorrect results returned, expected 1 received:"+resultList.size(), resultList.size() == 1);
        } finally {
            if (this.isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
    }

    public void testTreatInWhereSTI(){
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            Bus bus1 = new Bus();
            em.persist(bus1);
            PerformanceTireInfo pTire = new PerformanceTireInfo();
            pTire.setSpeedRating(110);
            pTire.setPressure(32);
            em.persist(pTire);
            bus1.getTires().add(pTire);

            Bus bus2 = new Bus();
            em.persist(bus2);
            TireInfo tire = new TireInfo();
            tire.setPressure(28);
            em.persist(tire);
            bus2.getTires().add(tire);
            PassengerPerformanceTireInfo otherTire = new PassengerPerformanceTireInfo();
            otherTire.setSpeedRating(100);
            otherTire.setPressure(31);
            em.persist(otherTire);
            bus2.getTires().add(otherTire);

            em.flush();

            clearCache();
            em.clear();

            Query query = em.createQuery("Select b from Bus b where treat(b.tires as PerformanceTireInfo).speedRating >100");
            List resultList = query.getResultList();
            assertTrue("Incorrect results returned, expected 1 received:"+resultList.size(), resultList.size() == 1);
        } finally {
            if (this.isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
    }

    //more complex example
    public void testTreatUsingAndOr(){
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            Person rudy = new Person();
            rudy.setName("Rudy");
            em.persist(rudy);
            SportsCar sportsCar = new SportsCar();
            sportsCar.setMaxSpeed(200);
            sportsCar.setColor("Red");
            em.persist(sportsCar);
            rudy.setCar(sportsCar);
            sportsCar.setUser(rudy);
            Company c = new Company();
            c.setName("test");
            em.persist(c);
            sportsCar.setOwner(c);
            c.getVehicles().add(sportsCar);

            Person theo = new Person();
            theo.setName("Theo");
            em.persist(theo);
            Jalopy jalopy = new Jalopy();
            jalopy.setColor("Red");
            jalopy.setPercentRust(20);
            em.persist(jalopy);
            theo.setCar(jalopy);

            Person daisy = new Person();
            daisy.setName("Daisy");
            em.persist(daisy);
            Car car = new Car();
            car.setColor("Red");
            em.persist(car);
            daisy.setCar(car);

            OffRoadTireInfo orti = new OffRoadTireInfo();
            orti.setName("IThinkThereforIAm");
            em.persist(orti);

            em.flush();
            clearCache();
            em.clear();

            Query query = em.createQuery("Select p from Person p left join p.car c where (c.color = 'Red') AND " +
                    "(treat(c as SportsCar).maxSpeed = 200 OR treat(c as Jalopy).percentRust = 20)");

            List resultList = query.getResultList();
            assertEquals("Incorrect results returned, expected 2 received:"+resultList.size(), resultList.size(), 2);
        } finally {
            if (this.isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
    }

    public void testTreatUsingAndOrSTI(){
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            Bus bus1 = new Bus();
            em.persist(bus1);
            PerformanceTireInfo pTire = new PerformanceTireInfo();
            pTire.setSpeedRating(150);
            pTire.setPressure(32);
            em.persist(pTire);
            bus1.getTires().add(pTire);

            Bus bus2 = new Bus();
            em.persist(bus2);
            TireInfo tire = new TireInfo();
            tire.setPressure(28);
            em.persist(tire);
            bus2.getTires().add(tire);
            PassengerPerformanceTireInfo otherTire = new PassengerPerformanceTireInfo();
            otherTire.setSpeedRating(130);
            otherTire.setPressure(31);
            em.persist(otherTire);
            bus2.getTires().add(otherTire);

            em.flush();
            clearCache();
            em.clear();

            Query query = em.createQuery("Select b from Bus b join b.tires t where (t.pressure > 0) AND " +
                    "(treat(t as PassengerPerformanceTireInfo).speedRating > 100 OR treat(t as OffRoadTireInfo).name = 'notExist')");

            List resultList = query.getResultList();
            assertEquals("Incorrect results returned, expected 1 received:"+resultList.size(), 1, resultList.size());
        } finally {
            if (this.isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
    }

    public void testReturningTypeOnTreat(){
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {

            SportsCar sportsCar = new SportsCar();
            sportsCar.setMaxSpeed(200);
            sportsCar.setColor("Red");
            em.persist(sportsCar);
            Company c = new Company();
            c.setName("test");
            em.persist(c);
            sportsCar.setOwner(c);
            c.getVehicles().add(sportsCar);

            Jalopy jalopy = new Jalopy();
            jalopy.setColor("Red");
            jalopy.setPercentRust(20);
            em.persist(jalopy);

            Car car = new Car();
            car.setColor("Red");
            em.persist(car);

            em.flush();
            clearCache();
            em.clear();

            Query query = em.createQuery("Select TYPE(treat(c as Car)) from Vehicle c");
            List resultList = query.getResultList();
            assertEquals("Incorrect results returned, expected 3 received:"+resultList.size(), 3, resultList.size());
        } finally {
            if (this.isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
    }

    public void testReturningTypeOnTreatSTI(){
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            Bus bus1 = new Bus();
            em.persist(bus1);
            PerformanceTireInfo pTire = new PerformanceTireInfo();
            pTire.setSpeedRating(150);
            pTire.setPressure(32);
            em.persist(pTire);
            bus1.getTires().add(pTire);

            Bus bus2 = new Bus();
            em.persist(bus2);
            TireInfo tire = new TireInfo();
            tire.setPressure(28);
            em.persist(tire);
            bus2.getTires().add(tire);
            PassengerPerformanceTireInfo otherTire = new PassengerPerformanceTireInfo();
            otherTire.setSpeedRating(130);
            otherTire.setPressure(31);
            em.persist(otherTire);
            bus2.getTires().add(otherTire);

            em.flush();
            clearCache();
            em.clear();

            Query query = em.createQuery("Select TYPE(treat(t as PerformanceTireInfo)) from TireInfo t");
            List resultList = query.getResultList();

            assertEquals("Incorrect results returned, expected 2 received:"+resultList.size(), 2, resultList.size());
        } finally {
            if (this.isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
    }

    //385350 - JPQL TREAT followed by JOIN problem.
    public void testTreatUsingJoinOverDowncastRelationship(){
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
            sportsCar.setUser(rudy);
            Company c = new Company();
            c.setName("test");
            em.persist(c);
            sportsCar.setOwner(c);
            c.getVehicles().add(sportsCar);

            Person theo = new Person();
            theo.setName("Theo");
            em.persist(theo);
            Jalopy car = new Jalopy();
            car.setColor("Red");
            car.setPercentRust(20);
            em.persist(car);
            theo.setCar(car);

            em.flush();
            clearCache();
            em.clear();
            Query query = em.createQuery("Select o from Person p join treat(p.car as SportsCar) s join s.owner o");
            List resultList = query.getResultList();
            for (Object result: resultList) {
                assertTrue("query did not return intances of Company, instead it returned :"+result, (result instanceof Company));
            }
            assertEquals("Incorrect results returned, expected 1 received:"+resultList.size(), resultList.size(), 1);
        } finally {
            if (this.isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
    }
}

