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
//     02/08/2013-2.5 Chris Delahunt
//       - 374771 - JPA 2.1 TREAT support
package org.eclipse.persistence.testing.tests.jpa21.advanced;

import java.math.BigInteger;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.advanced.Employee;
import org.eclipse.persistence.testing.models.jpa.advanced.LargeProject;
import org.eclipse.persistence.testing.models.jpa.advanced.Project;
import org.eclipse.persistence.testing.models.jpa.advanced.SmallProject;
import org.eclipse.persistence.testing.models.jpa.inheritance.Boat;
import org.eclipse.persistence.testing.models.jpa.inheritance.Bus;
import org.eclipse.persistence.testing.models.jpa.inheritance.Car;
import org.eclipse.persistence.testing.models.jpa.inheritance.Company;
import org.eclipse.persistence.testing.models.jpa.inheritance.FueledVehicle;
import org.eclipse.persistence.testing.models.jpa.inheritance.Jalopy;
import org.eclipse.persistence.testing.models.jpa.inheritance.NonFueledVehicle;
import org.eclipse.persistence.testing.models.jpa.inheritance.OffRoadTireInfo;
import org.eclipse.persistence.testing.models.jpa.inheritance.PassengerPerformanceTireInfo;
import org.eclipse.persistence.testing.models.jpa.inheritance.PerformanceTireInfo;
import org.eclipse.persistence.testing.models.jpa.inheritance.Person;
import org.eclipse.persistence.testing.models.jpa.inheritance.SportsCar;
import org.eclipse.persistence.testing.models.jpa.inheritance.TireInfo;
import org.eclipse.persistence.testing.models.jpa.inheritance.Vehicle;
import org.eclipse.persistence.testing.models.jpa.inherited.BeerConsumer;
import org.eclipse.persistence.testing.models.jpa.inherited.Blue;
import org.eclipse.persistence.testing.models.jpa.inherited.BlueLight;
import org.eclipse.persistence.testing.models.jpa21.advanced.animals.Animal;
import org.eclipse.persistence.testing.models.jpa21.advanced.animals.Beaver;
import org.eclipse.persistence.testing.models.jpa21.advanced.animals.Rodent;

import junit.framework.Test;
import junit.framework.TestSuite;

public class CriteriaQueryCastTestSuite extends JUnitTestCase {

    public CriteriaQueryCastTestSuite() {
        super();
    }

    public CriteriaQueryCastTestSuite(String name) {
        super(name);
        setPuName("MulitPU-1");
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("QueryCastTestSuite");

        suite.addTest(new CriteriaQueryCastTestSuite("testSetup"));
// Bug 532018 - Can't use org.eclipse.persistence.testing.models.jpa entities in JPA 2.1 test
//        suite.addTest(new CriteriaQueryCastTestSuite("testDowncastOneToManyLeafQueryKey"));
//        suite.addTest(new CriteriaQueryCastTestSuite("testDowncastOneToManyMidHierarchyQueryKey"));
//        suite.addTest(new CriteriaQueryCastTestSuite("testDowncastManyToManyQueryKey"));
//        suite.addTest(new CriteriaQueryCastTestSuite("testDowncastOneToManyLeafExpressionBuilder"));
//        suite.addTest(new CriteriaQueryCastTestSuite("testDowncastOneToManyMidHierarchyExpressionBuilder"));
//        suite.addTest(new CriteriaQueryCastTestSuite("testDowncastManyToManyExpressionBuilder"));
//        suite.addTest(new CriteriaQueryCastTestSuite("testDowncastInSelect"));
//        suite.addTest(new CriteriaQueryCastTestSuite("testDowncastSingleTableQueryKey"));
//        suite.addTest(new CriteriaQueryCastTestSuite("testDoubleDowncastOneToManyLeafQueryKey"));
//        suite.addTest(new CriteriaQueryCastTestSuite("testDoubleDowncastSeparateClass"));
//        suite.addTest(new CriteriaQueryCastTestSuite("testDowncastRelationshipTraversal"));
//        suite.addTest(new CriteriaQueryCastTestSuite("testDoubleDowncastOneToOne"));
//        suite.addTest(new CriteriaQueryCastTestSuite("testSelectCast"));
//        suite.addTest(new CriteriaQueryCastTestSuite("testCastInSubselect"));
//        suite.addTest(new CriteriaQueryCastTestSuite("testDowncastWithFetchJoin"));
//        suite.addTest(new CriteriaQueryCastTestSuite("testDoubleTreatOnRoot"));
//        suite.addTest(new CriteriaQueryCastTestSuite("testDoubleTreatOnRootSTI"));
//        suite.addTest(new CriteriaQueryCastTestSuite("testTreatInFrom"));
//        suite.addTest(new CriteriaQueryCastTestSuite("testTreatInFromSTI"));
//        suite.addTest(new CriteriaQueryCastTestSuite("testTreatInWhere"));
//        suite.addTest(new CriteriaQueryCastTestSuite("testTreatInWhereSTI"));
//        suite.addTest(new CriteriaQueryCastTestSuite("testTreatUsingAndOr"));
//        suite.addTest(new CriteriaQueryCastTestSuite("testTreatUsingAndOrSTI"));
//        suite.addTest(new CriteriaQueryCastTestSuite("testTreatUsingJoinOverDowncastRelationship"));

        suite.addTest(new CriteriaQueryCastTestSuite("testTreatOverInheritance"));
        suite.addTest(new CriteriaQueryCastTestSuite("testTreatOverInheritanceWithCount"));
        suite.addTest(new CriteriaQueryCastTestSuite("testTreatOverInheritanceWithCountJPQL"));
        return suite;
    }

    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
// Bug 532018 - Can't use org.eclipse.persistence.testing.models.jpa entities in JPA 2.1 test
//        new AdvancedTableCreator().replaceTables(getPersistenceUnitServerSession());
//        new InheritanceTableCreator().replaceTables(getPersistenceUnitServerSession());
//        new InheritedTableManager().replaceTables(getPersistenceUnitServerSession());
//        // Force uppercase for Postgres.
//        if (getPersistenceUnitServerSession().getPlatform().isPostgreSQL()) {
//            getPersistenceUnitServerSession().getLogin().setShouldForceFieldNamesToUpperCase(true);
//        }

        EntityManager em = createEntityManager("AnimalsPU");
        beginTransaction(em);
        try {
            Animal.initAnimals(em);
            commitTransaction(em);
        } finally {
            if (this.isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
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

            //Query query = em.createQuery("Select c from Company c join treat(c.vehicles as Boat) b where b.model = 'speed'");
            CriteriaBuilder qb = em.getCriteriaBuilder();
            CriteriaQuery<Company> cq = qb.createQuery(Company.class);
            Root<Company> root = cq.from(Company.class);
            Join vehicleJoin = root.join("vehicles");
            Join boatJoin = qb.treat(vehicleJoin, Boat.class);
            cq.where(qb.equal(boatJoin.get("model"), "speed"));

            List resultList = em.createQuery(cq).getResultList();

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

            //Query query = em.createQuery("Select c from Company c join treat(c.vehicles as NonFueledVehicle) v where v.color = 'Blue'");
            CriteriaBuilder qb = em.getCriteriaBuilder();
            CriteriaQuery<Company> cq = qb.createQuery(Company.class);
            Root<Company> root = cq.from(Company.class);
            Join vehicleJoin = root.join("vehicles");
            Join nonFueledJoin = qb.treat(vehicleJoin, NonFueledVehicle.class);
            cq.where(qb.equal(nonFueledJoin.get("color"), "Blue"));

            List resultList = em.createQuery(cq).getResultList();

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

            //Query query = em.createQuery("Select e from Employee e join treat(e.projects as LargeProject) p where p.budget > 100");
            CriteriaBuilder qb = em.getCriteriaBuilder();
            CriteriaQuery<Employee> cq = qb.createQuery(Employee.class);
            Root<Employee> root = cq.from(Employee.class);
            Join projectsJoin = root.join("projects");
            Join largeProjectJoin = qb.treat(projectsJoin, LargeProject.class);
            cq.where(qb.gt(largeProjectJoin.get("budget"), 100));

            List resultList = em.createQuery(cq).getResultList();

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

            //JpaQuery query = (JpaQuery)em.createQuery("Select v from Vehicle v where treat(v as Boat).model = 'speed'");
            CriteriaBuilder qb = em.getCriteriaBuilder();
            CriteriaQuery<Vehicle> cq = qb.createQuery(Vehicle.class);
            Root<Vehicle> root = cq.from(Vehicle.class);
            Root boatRoot = qb.treat(root, Boat.class);
            cq.where(qb.equal(boatRoot.get("model"), "speed"));

            List resultList = em.createQuery(cq).getResultList();

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

            //JpaQuery query = (JpaQuery)em.createQuery("Select v from Vehicle v where treat(v as NonFueledVehicle).color = 'Blue'");
            CriteriaBuilder qb = em.getCriteriaBuilder();
            CriteriaQuery<Vehicle> cq = qb.createQuery(Vehicle.class);
            Root<Vehicle> root = cq.from(Vehicle.class);
            Root<NonFueledVehicle> nonFueledVehicleRoot = qb.treat(root, NonFueledVehicle.class);
            cq.where(qb.equal(nonFueledVehicleRoot.get("color"), "Blue"));

            List resultList = em.createQuery(cq).getResultList();

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

            //JpaQuery query = (JpaQuery)em.createQuery("Select p from Project p where treat (p as LargeProject).budget > 100");
            CriteriaBuilder qb = em.getCriteriaBuilder();
            CriteriaQuery<Project> cq = qb.createQuery(Project.class);
            Root<Project> root = cq.from(Project.class);
            Root largeProjectRoot = qb.treat(root, LargeProject.class);
            cq.where(qb.gt(largeProjectRoot.get("budget"), 100));

            List resultList = em.createQuery(cq).getResultList();

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

            //this would work in the past if TYPE was added to the where clause
            //Query query = em.createQuery("Select treat(c as LargeProject).budget from Project c");
            CriteriaBuilder qb = em.getCriteriaBuilder();
            CriteriaQuery<Project> cq = qb.createQuery(Project.class);
            Root<Project> root = cq.from(Project.class);
            Root largeProjectRoot = qb.treat(root, LargeProject.class);
            cq.select(largeProjectRoot.get("budget"));

            List resultList = em.createQuery(cq).getResultList();

            assertTrue("Incorrect results returned", resultList.size() == 1);
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

            //Query query = em.createQuery("Select b from BeerConsumer b join treat(b.blueBeersToConsume as BlueLight) bl where bl.discount = 10");
            CriteriaBuilder qb = em.getCriteriaBuilder();
            CriteriaQuery<BeerConsumer> cq = qb.createQuery(BeerConsumer.class);
            Root<BeerConsumer> root = cq.from(BeerConsumer.class);
            Join blueLightJoin = qb.treat((Join<Object, Object>) root.join("blueBeersToConsume"), BlueLight.class);
            cq.where(qb.equal(blueLightJoin.get("discount"), 10));

            List resultList = em.createQuery(cq).getResultList();

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

            //Query query = em.createQuery("Select c from Company c join treat(c.vehicles as Boat) b where b.model = 'speed' or b.model = 'fishing'");
            CriteriaBuilder qb = em.getCriteriaBuilder();
            CriteriaQuery<Company> cq = qb.createQuery(Company.class);
            Root<Company> root = cq.from(Company.class);
            Join boatJoin = qb.treat((Join<Object, Object>) root.join("vehicles"), Boat.class);
            cq.where(qb.or(qb.equal(boatJoin.get("model"), "speed"), qb.equal(boatJoin.get("model"), "fishing")));

            List resultList = em.createQuery(cq).getResultList();

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

            //Query query = em.createQuery("Select distinct c from Company c left join treat(c.vehicles as Boat) b left join treat(c.vehicles as FueledVehicle) f where b.model = 'fishing' or f.fuelType = 'unleaded'");
            CriteriaBuilder qb = em.getCriteriaBuilder();
            CriteriaQuery<Company> cq = qb.createQuery(Company.class);
            Root<Company> root = cq.from(Company.class);
            Join b = qb.treat((Join<Object, Object>) root.join("vehicles", JoinType.LEFT), Boat.class);
            Join f = qb.treat((Join<Object, Object>) root.join("vehicles", JoinType.LEFT), FueledVehicle.class);
            cq.where(qb.or(qb.equal(b.get("model"), "fishing"), qb.equal(f.get("fuelType"), "unleaded")));

            List resultList = em.createQuery(cq).getResultList();

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

            //Query query = em.createQuery("Select distinct c from Company c left join treat(c.vehicles as Bus) b where b.busDriver.name = 'Driver'");
            CriteriaBuilder qb = em.getCriteriaBuilder();
            CriteriaQuery<Company> cq = qb.createQuery(Company.class);
            cq.distinct(true);
            Root<Company> root = cq.from(Company.class);
            Join b = qb.treat((Join<Object, Object>) root.join("vehicles", JoinType.LEFT), Bus.class);
            cq.where(qb.equal(b.get("busDriver").get("name"), "Driver"));

            List resultList = em.createQuery(cq).getResultList();

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
            //Query query = em.createQuery("Select distinct p from Person p left join treat(p.car as SportsCar) s left join treat(p.car as Jalopy) j where s.maxSpeed = 200 or j.percentRust = 20");
            CriteriaBuilder qb = em.getCriteriaBuilder();
            CriteriaQuery<Person> cq = qb.createQuery(Person.class);
            cq.distinct(true);
            Root<Person> root = cq.from(Person.class);
            Join s = qb.treat((Join<Object, Object>) root.join("car", JoinType.LEFT), SportsCar.class);
            Join j = qb.treat((Join<Object, Object>) root.join("car", JoinType.LEFT), Jalopy.class);
            cq.where(qb.or(qb.equal(s.get("maxSpeed"), 200), qb.equal(j.get("percentRust"), 20)));

            List resultList = em.createQuery(cq).getResultList();
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

            //Query query = em.createQuery("Select max(l.budget) from Employee e join treat(e.projects as LargeProject) l");
            CriteriaBuilder qb = em.getCriteriaBuilder();
            CriteriaQuery<Number> cq = qb.createQuery(Number.class);
            Root<Employee> root = cq.from(Employee.class);
            Join l = qb.treat((Join<Object, Object>) root.join("projects"), LargeProject.class);
            cq.select(qb.max(l.get("budget")));

            List resultList = em.createQuery(cq).getResultList();

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

            //Query query = em.createQuery("select e from Employee e where e.salary > (Select max(l.budget) from Employee emp join treat(emp.projects as LargeProject) l)");
            CriteriaBuilder qb = em.getCriteriaBuilder();
            CriteriaQuery<Employee> cq = qb.createQuery(Employee.class);
            Root<Employee> root = cq.from(Employee.class);
            Subquery<Number> sq = cq.subquery(Number.class);
            Root<Employee> sRoot = sq.from(Employee.class);
            Join l = qb.treat((Join<Object, Object>) root.join("projects"), LargeProject.class);
            sq.select(qb.max(l.get("budget")));
            cq.where(qb.gt(root.<Number>get("salary"), sq));

            List resultList = em.createQuery(cq).getResultList();

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

            //Query query = em.createQuery("Select p from Person p join fetch p.car join treat(p.car as SportsCar) s where s.maxSpeed = 200");
            CriteriaBuilder qb = em.getCriteriaBuilder();
            CriteriaQuery<Person> cq = qb.createQuery(Person.class);
            Root<Person> root = cq.from(Person.class);
            root.fetch("car");
            Join s = qb.treat((Join<Object, Object>) root.join("car"), SportsCar.class);
            cq.where(qb.equal(s.get("maxSpeed"), 200));

            List resultList = em.createQuery(cq).getResultList();
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

    //last spec example
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
            //Query query = em.createQuery("Select c from Car c where treat(c as SportsCar).maxSpeed = 200 or treat(c as Jalopy).percentRust = 20");
            CriteriaBuilder qb = em.getCriteriaBuilder();
            CriteriaQuery<Car> cq = qb.createQuery(Car.class);
            Root<Car> root = cq.from(Car.class);
            cq.where(qb.or(qb.equal( qb.treat(root, SportsCar.class).get("maxSpeed"), 200), qb.equal( qb.treat(root, Jalopy.class).get("percentRust"), 20)));

            List resultList = em.createQuery(cq).getResultList();
            assertTrue("Incorrect results returned", resultList.size() == 2);
        } finally {
            if (this.isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
    }

  //last spec example
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
            //Query query = em.createQuery("Select t from TireInfo t where treat(t as PerformanceTireInfo).speedRating = 110 or treat(t as PassengerPerformanceTireInfo).speedRating = 120");
            CriteriaBuilder qb = em.getCriteriaBuilder();
            CriteriaQuery<TireInfo> cq = qb.createQuery(TireInfo.class);
            Root<TireInfo> root = cq.from(TireInfo.class);
            cq.where(qb.or(qb.equal( qb.treat(root, PerformanceTireInfo.class).get("speedRating"), 110), qb.equal( qb.treat(root, PassengerPerformanceTireInfo.class).get("speedRating"), 120)));

            List resultList = em.createQuery(cq).getResultList();
            assertTrue("Incorrect results returned", resultList.size() == 2);
        } finally {
            if (this.isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
    }

  //first Spec example
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

            //Query query = em.createQuery("Select s.maxSpeed from Person o join treat(o.car as SportsCar) s");
            CriteriaBuilder qb = em.getCriteriaBuilder();
            CriteriaQuery<Person> cq = qb.createQuery(Person.class);
            Root<Person> root = cq.from(Person.class);
            Join s = qb.treat((Join<Object, Object>) root.join("car"), SportsCar.class);
            cq.select(s.get("maxSpeed"));

            List resultList = em.createQuery(cq).getResultList();

            assertTrue("Incorrect results returned", resultList.size() == 1);
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

            //Query query = em.createQuery("Select b.speedRating from Bus o join treat(o.tires as PerformanceTireInfo) b");
            CriteriaBuilder qb = em.getCriteriaBuilder();
            CriteriaQuery<Bus> cq = qb.createQuery(Bus.class);
            Root<Bus> root = cq.from(Bus.class);
            Join b = qb.treat((Join<Object, Object>) root.join("tires"), PerformanceTireInfo.class);
            cq.select(b.get("speedRating"));

            List resultList = em.createQuery(cq).getResultList();
            assertTrue("Incorrect results returned", resultList.size() == 2);
        } finally {
            if (this.isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
    }

  //second Spec example,
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

            //Query query = em.createQuery("Select o from Person o where treat(o.car as SportsCar).maxSpeed = 200");
            CriteriaBuilder qb = em.getCriteriaBuilder();
            CriteriaQuery<Person> cq = qb.createQuery(Person.class);
            Root<Person> root = cq.from(Person.class);
            cq.where(qb.equal(qb.treat(root.get("car"), SportsCar.class).get("maxSpeed"), 200));

            List resultList = em.createQuery(cq).getResultList();

            assertTrue("Incorrect results returned", resultList.size() == 1);
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

            //Query query = em.createQuery("Select b from Bus b where treat(b.tires as PerformanceTireInfo).speedRating >100");
            CriteriaBuilder qb = em.getCriteriaBuilder();
            CriteriaQuery<Bus> cq = qb.createQuery(Bus.class);
            Root<Bus> root = cq.from(Bus.class);
            Join b = qb.treat((Join<Object, Object>) root.join("tires"), PerformanceTireInfo.class);
            cq.where(qb.greaterThan(qb.treat(root.get("tires"), PerformanceTireInfo.class).<Integer>get("speedRating"), 100));

            List resultList = em.createQuery(cq).getResultList();
            assertTrue("Incorrect results returned", resultList.size() == 2);
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
            Company company = new Company();
            company.setName("test");
            em.persist(company);
            sportsCar.setOwner(company);
            company.getVehicles().add(sportsCar);

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

            em.flush();
            clearCache();
            em.clear();

            //Query query = em.createQuery("Select p from Person p join p.car c where (c.color = 'Red') AND " +
            //        "(treat(c as SportsCar).maxSpeed = 200 OR treat(c as Jalopy).percentRust = 20)");
            CriteriaBuilder qb = em.getCriteriaBuilder();
            CriteriaQuery<Person> cq = qb.createQuery(Person.class);
            Root<Person> root = cq.from(Person.class);
            Join c = root.join("car");
            cq.where(qb.and( qb.equal(c.get("color"), "Red"),
                             qb.or( qb.equal(qb.treat(c, SportsCar.class).get("maxSpeed"), 200),
                                     qb.equal(qb.treat(c, Jalopy.class).get("percentRust"), 20)
                                   )
                            ));

            List resultList = em.createQuery(cq).getResultList();
            assertEquals("Incorrect results returned", 2, resultList.size());
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

            //Query query = em.createQuery("Select b from Bus b join b.tires t where (t.pressure > 0) AND " +
            //        "(treat(t as PassengerPerformanceTireInfo).speedRating > 100 OR treat(t as OffRoadTireInfo).name = 'doesNotExist')");
            CriteriaBuilder qb = em.getCriteriaBuilder();
            CriteriaQuery<Bus> cq = qb.createQuery(Bus.class);
            Root<Bus> root = cq.from(Bus.class);
            Join t = root.join("tires");
            cq.where(qb.and( qb.gt(t.get("pressure"), 0),
                             qb.or( qb.equal(qb.treat(t, PassengerPerformanceTireInfo.class).get("maxSpeed"), 200),
                                     qb.equal(qb.treat(t, OffRoadTireInfo.class).get("name"), "doesNotExist")
                                   )
                            ));

            List resultList = em.createQuery(cq).getResultList();
            assertEquals("Incorrect results returned", 1, resultList.size());
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


            //this returns user.id instead of Person objects
            //query = em.createQuery("Select u from Person o join treat(o.car as SportsCar) b join b.user u");
            CriteriaBuilder qb = em.getCriteriaBuilder();
            CriteriaQuery<Person> cq = qb.createQuery(Person.class);
            Root<Person> root = cq.from(Person.class);
            Join b = qb.treat((Join<Object, Object>) root.join("car"), SportsCar.class);
            Join u = b.join("user");
            cq.select(u);

            List resultList = em.createQuery(cq).getResultList();

            for (Object result: resultList) {
                assertTrue("query did not return intances of Company, instead it returned :"+result, (result instanceof Person));
            }
            assertTrue("Incorrect results returned", resultList.size() == 1);
        } finally {
            if (this.isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
    }

    /**
     * Test scenario from bug 531726: Treat over 2 subsequent inheritance using
     * direct {@code select}. Using CriteriaQuery to build the query.
     */
    public void testTreatOverInheritance() {
        EntityManager em = createEntityManager("AnimalsPU");
        beginTransaction(em);
        try {
            CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
            CriteriaQuery<Animal> createQuery = criteriaBuilder.createQuery(Animal.class);
            Root<Animal> from = createQuery.from(Animal.class);

            Root<Rodent> treat1 = criteriaBuilder.treat(from, Rodent.class);
            Root<Beaver> treat2 = criteriaBuilder.treat(from, Beaver.class);

            Predicate equal1 = criteriaBuilder.isNotNull(treat1.get("name"));
            Predicate equal2 = criteriaBuilder.isNotNull(treat2.get("name"));

            createQuery.select(from);
            createQuery.where(equal1, equal2);
            TypedQuery<Animal> query = em.createQuery(createQuery);
            List<Animal> result = query.getResultList();
            assertEquals("Animals count:", 2, result.size());
        } finally {
            if (this.isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
    }

    /**
     * Test scenario from bug 531726: Treat over 2 subsequent inheritance using
     * {@code select count}. Using CriteriaQuery to build the query.
     */
    public void testTreatOverInheritanceWithCount() {
        EntityManager em = createEntityManager("AnimalsPU");
        beginTransaction(em);
        try {

            CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
            CriteriaQuery<Long> createQuery = criteriaBuilder.createQuery(Long.class);
            Root<Animal> from = createQuery.from(Animal.class);

            Root<Rodent> treat1 = criteriaBuilder.treat(from, Rodent.class);
            Root<Beaver> treat2 = criteriaBuilder.treat(from, Beaver.class);

            Predicate equal1 = criteriaBuilder.isNotNull(treat1.get("name"));
            Predicate equal2 = criteriaBuilder.isNotNull(treat2.get("name"));

            createQuery.select(criteriaBuilder.count(from));
            createQuery.where(equal1, equal2);

            TypedQuery<Long> query = em.createQuery(createQuery);
            Long result = query.getSingleResult();
            assertEquals("Animals count:", 2, result.longValue());
        } finally {
            if (this.isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
    }

    /**
     * Test scenario from bug 531726: Treat over 2 subsequent inheritance using {@code select count}.
     * Using JPQL to build the query.
     */
    public void testTreatOverInheritanceWithCountJPQL() {
        EntityManager em = createEntityManager("AnimalsPU");
        beginTransaction(em);
        try {
            TypedQuery<Long> query = em.createQuery(
                    "SELECT count(a.name) " +
                      "FROM Animal a " +
                      "JOIN TREAT (a AS Rodent) r " +
                      "JOIN TREAT (a AS Beaver) b " +
                     "WHERE r IS NOT null " +
                       "AND b IS NOT null ",
                      Long.class);
            Long result = query.getSingleResult();
            assertEquals("Animals count:", 2, result.longValue());
        } finally {
            if (this.isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
    }

    @Override
    public String getPersistenceUnitName() {
       return "MulitPU-1";
    }
}

