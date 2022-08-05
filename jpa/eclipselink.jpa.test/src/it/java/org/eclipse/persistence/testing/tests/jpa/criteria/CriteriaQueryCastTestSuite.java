/*
 * Copyright (c) 2011, 2022 Oracle and/or its affiliates. All rights reserved.
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
//     tware - initial API and implementation as part of Query Downcast feature
//     02/08/2013-2.5 Chris Delahunt
//       - 374771 - JPA 2.1 TREAT support
package org.eclipse.persistence.testing.tests.jpa.criteria;

import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.testing.framework.jpa.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.advanced.AdvancedTableCreator;
import org.eclipse.persistence.testing.models.jpa.advanced.Employee;
import org.eclipse.persistence.testing.models.jpa.advanced.LargeProject;
import org.eclipse.persistence.testing.models.jpa.advanced.Project;
import org.eclipse.persistence.testing.models.jpa.advanced.SmallProject;

public class CriteriaQueryCastTestSuite extends JUnitTestCase {

    public CriteriaQueryCastTestSuite() {
        super();
    }

    public CriteriaQueryCastTestSuite(String name) {
        super(name);
//        setPuName("MulitPU-1");
    }

//    @Override
//    public String getPersistenceUnitName() {
//        return "MulitPU-1";
//    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("CriteriaQueryCastTestSuite");

        suite.addTest(new CriteriaQueryCastTestSuite("testSetup"));
        suite.addTest(new CriteriaQueryCastTestSuite("testDowncastOneToManyLeafQueryKey"));
        suite.addTest(new CriteriaQueryCastTestSuite("testDowncastOneToManyMidHierarchyQueryKey"));
        suite.addTest(new CriteriaQueryCastTestSuite("testDowncastManyToManyQueryKey"));
        suite.addTest(new CriteriaQueryCastTestSuite("testDowncastOneToManyLeafExpressionBuilder"));
        suite.addTest(new CriteriaQueryCastTestSuite("testDowncastOneToManyMidHierarchyExpressionBuilder"));
        suite.addTest(new CriteriaQueryCastTestSuite("testDowncastManyToManyExpressionBuilder"));
        suite.addTest(new CriteriaQueryCastTestSuite("testDowncastInSelect"));
        suite.addTest(new CriteriaQueryCastTestSuite("testDoubleDowncastOneToManyLeafQueryKey"));
        suite.addTest(new CriteriaQueryCastTestSuite("testDoubleDowncastSeparateClass"));
        suite.addTest(new CriteriaQueryCastTestSuite("testDowncastRelationshipTraversal"));
        suite.addTest(new CriteriaQueryCastTestSuite("testDoubleDowncastOneToOne"));
        suite.addTest(new CriteriaQueryCastTestSuite("testSelectCast"));
        suite.addTest(new CriteriaQueryCastTestSuite("testCastInSubselect"));
        suite.addTest(new CriteriaQueryCastTestSuite("testDowncastWithFetchJoin"));
        suite.addTest(new CriteriaQueryCastTestSuite("testDoubleTreatOnRoot"));
        suite.addTest(new CriteriaQueryCastTestSuite("testDoubleTreatOnRootSTI"));
        suite.addTest(new CriteriaQueryCastTestSuite("testTreatInFrom"));
        suite.addTest(new CriteriaQueryCastTestSuite("testTreatInFromSTI"));
        suite.addTest(new CriteriaQueryCastTestSuite("testTreatInWhere"));
        suite.addTest(new CriteriaQueryCastTestSuite("testTreatInWhereSTI"));
        suite.addTest(new CriteriaQueryCastTestSuite("testTreatUsingAndOr"));
        suite.addTest(new CriteriaQueryCastTestSuite("testTreatUsingAndOrSTI"));
        suite.addTest(new CriteriaQueryCastTestSuite("testTreatUsingJoinOverDowncastRelationship"));
        return suite;
    }

    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
        new AdvancedTableCreator().replaceTables(getPersistenceUnitServerSession());
        // Force uppercase for Postgres.
        if (getPersistenceUnitServerSession().getPlatform().isPostgreSQL()) {
            getPersistenceUnitServerSession().getLogin().setShouldForceFieldNamesToUpperCase(true);
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
            Join l = qb.treat(root.join("projects"), LargeProject.class);
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
            Join l = qb.treat(root.join("projects"), LargeProject.class);
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
}

