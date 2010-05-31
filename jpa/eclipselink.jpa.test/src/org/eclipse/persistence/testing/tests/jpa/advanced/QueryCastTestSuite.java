/*******************************************************************************
 * Copyright (c) 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     tware - initial API and implementation as part of Query Downcast feature
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.jpa.advanced;

import java.math.BigInteger;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.jpa.JpaQuery;
import org.eclipse.persistence.jpa.JpaEntityManager;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.ReportQuery;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.inheritance.Jalopy;
import org.eclipse.persistence.testing.models.jpa.inheritance.Person;
import org.eclipse.persistence.testing.models.jpa.inheritance.Bus;
import org.eclipse.persistence.testing.models.jpa.inheritance.NonFueledVehicle;
import org.eclipse.persistence.testing.models.jpa.inheritance.SportsCar;
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
        
        assertTrue("Incorrect results returned", resultList.size() == 1);
        rollbackTransaction(em);
    }
    
    public void testDowncastOneToManyMidHierarchyQueryKey(){
        EntityManager em = createEntityManager();

        beginTransaction(em);

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
        rollbackTransaction(em);
    }
    
    public void testDowncastManyToManyQueryKey(){
        EntityManager em = createEntityManager();
        
        beginTransaction(em);
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
        rollbackTransaction(em);
    }
    
    public void testDowncastOneToManyLeafExpressionBuilder(){
        EntityManager em = createEntityManager();

        beginTransaction(em);

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
        // this allows us to access the base of the query - we can't do that with a JPQL join
        JpaQuery query = (JpaQuery)em.createQuery("Select v from Vehicle v");
        ReadAllQuery raq = new ReadAllQuery(Vehicle.class);
        query.setDatabaseQuery(raq);
        Expression exp = raq.getExpressionBuilder();
        Expression  criteria = exp.as(Boat.class).get("model").equal("speed");
        raq.setSelectionCriteria(criteria);
        List resultList = query.getResultList();
        
        assertTrue("Incorrect results returned", resultList.size() == 1);
        rollbackTransaction(em);
    }
    
    public void testDowncastOneToManyMidHierarchyExpressionBuilder(){
        EntityManager em = createEntityManager();

        beginTransaction(em);

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
        Expression  criteria = exp.as(NonFueledVehicle.class).get("color").equal("Blue");
        raq.setSelectionCriteria(criteria);
        List resultList = query.getResultList();
        
        assertTrue("Incorrect results returned", resultList.size() == 2);
        rollbackTransaction(em);
    }
    
    public void testDowncastManyToManyExpressionBuilder(){
        EntityManager em = createEntityManager();
        
        beginTransaction(em);
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
        // this allows us to access the base of the query - we can't do that with a JPQL join
        JpaQuery query = (JpaQuery)em.createQuery("Select p from Project p");
        ReadAllQuery raq = new ReadAllQuery(Project.class);
        query.setDatabaseQuery(raq);
        Expression exp = raq.getExpressionBuilder();
        Expression criteria = exp.as(LargeProject.class).get("budget").greaterThan(100);
        raq.setSelectionCriteria(criteria);
        List resultList = query.getResultList();
        
        assertTrue("Incorrect results returned", resultList.size() == 1);
        rollbackTransaction(em);
    }
    
    public void testDowncastInSelect(){
        EntityManager em = createEntityManager();
        beginTransaction(em);
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
        rq.addAttribute("project", builder.as(LargeProject.class).get("budget"));
        rq.setSelectionCriteria(builder.type().equal(LargeProject.class));
        List resultList = (List)((JpaEntityManager)em.getDelegate()).getActiveSession().executeQuery(rq);
        assertTrue("Incorrect results returned", resultList.size() == 1);
        rollbackTransaction(em);
    }
    
    public void testDowncastSingleTableQueryKey(){
        EntityManager em = createEntityManager();
        
        beginTransaction(em);
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
        rollbackTransaction(em);
    }

    public void testDoubleDowncastOneToManyLeafQueryKey(){
        EntityManager em = createEntityManager();

        beginTransaction(em);

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
        rollbackTransaction(em);
    }
    
    public void testDoubleDowncastSeparateClass(){
        EntityManager em = createEntityManager();

        beginTransaction(em);

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
        
        Query query = (Query)em.createQuery("Select distinct c from Company c left join treat(c.vehicles as Boat) b left join treat(c.vehicles as FueledVehicle) f where b.model = 'fishing' or f.fuelType = 'unleaded'");
        List resultList = query.getResultList();
        
        assertTrue("Incorrect results returned", resultList.size() == 2);
        rollbackTransaction(em);
    }
    
    public void testDowncastRelationshipTraversal(){
        EntityManager em = createEntityManager();

        beginTransaction(em);

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
        rollbackTransaction(em);
    }

    public void testDoubleDowncastOneToOne(){
        EntityManager em = createEntityManager();

        beginTransaction(em);
        
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
        try {
            // The following query casts across a 1-1
            // as a result of our 1-1 optimization, the same expression is used twice
            // causing an exception
            Query query = (JpaQuery)em.createQuery("Select distinct p from Person p left join treat(p.car as SportsCar) s left join treat(p.car as Jalopy) j where s.maxSpeed = 200 or j.percentRust = 20");
            List resultList = query.getResultList();
            assertTrue("Incorrect results returned", resultList.size() == 2);
        } finally {
            rollbackTransaction(em);
        }
    }
    
    public void testSelectCast(){
        EntityManager em = createEntityManager();
        beginTransaction(em);

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
        rollbackTransaction(em);
    }
    
    public void testCastInSubselect(){
        EntityManager em = createEntityManager();
        beginTransaction(em);

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
        rollbackTransaction(em);
    }
    
    public void testDowncastWithFetchJoin(){
        EntityManager em = createEntityManager();

        beginTransaction(em);
        
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
        try {
            Query query = (JpaQuery)em.createQuery("Select p from Person p join fetch p.car join treat(p.car as SportsCar) s where s.maxSpeed = 200");
            List resultList = query.getResultList();
            Person person = (Person)resultList.get(0);
            assertTrue("Incorrect result size returned", resultList.size() == 1);
            assertNotNull("The car was not fetched.", person.car);
        } finally {
            rollbackTransaction(em);
        }
    }
}

