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
package org.eclipse.persistence.testing.tests.jpa.criteria.advanced;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.jpa.JpaEntityManager;
import org.eclipse.persistence.jpa.JpaQuery;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.ReportQuery;
import org.eclipse.persistence.testing.framework.jpa.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.advanced.AdvancedTableCreator;
import org.eclipse.persistence.testing.models.jpa.advanced.Employee;
import org.eclipse.persistence.testing.models.jpa.advanced.LargeProject;
import org.eclipse.persistence.testing.models.jpa.advanced.Project;
import org.eclipse.persistence.testing.models.jpa.advanced.SmallProject;

import java.util.List;

public class QueryCastTest extends JUnitTestCase {

    public QueryCastTest() {
        super();
    }

    public QueryCastTest(String name) {
        super(name);
        setPuName(getPersistenceUnitName());
    }

    @Override
    public String getPersistenceUnitName() {
        return "advanced";
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("QueryCastTest");

        suite.addTest(new QueryCastTest("testSetup"));
        suite.addTest(new QueryCastTest("testDowncastManyToManyQueryKey"));
        suite.addTest(new QueryCastTest("testDowncastManyToManyExpressionBuilder"));
        suite.addTest(new QueryCastTest("testDowncastInSelect"));
        suite.addTest(new QueryCastTest("testSelectCast"));

        suite.addTest(new QueryCastTest("testCastInSubselect"));
        suite.addTest(new QueryCastTest("testTreatInSelect"));
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

            Query query = em.createQuery("Select e from Employee e join treat(e.projects as LargeProject) p where p.budget > 100");
            List<?> resultList = query.getResultList();

            assertEquals("Incorrect results returned", 1, resultList.size());
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
            JpaQuery<?> query = (JpaQuery<?>)em.createQuery("Select p from Project p");
            ReadAllQuery raq = new ReadAllQuery(Project.class);
            query.setDatabaseQuery(raq);
            Expression exp = raq.getExpressionBuilder();
            Expression criteria = exp.treat(LargeProject.class).get("budget").greaterThan(100);
            raq.setSelectionCriteria(criteria);
            List<?> resultList = query.getResultList();

            assertEquals("Incorrect results returned", 1, resultList.size());
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
            List<?> resultList = (List<?>)((JpaEntityManager)em.getDelegate()).getActiveSession().executeQuery(rq);
            assertEquals("Incorrect results returned", 1, resultList.size());

            //Test equivalent JPQL as well
            Query query = em.createQuery("Select treat(c as LargeProject).budget from Project c");
            List<?> JPQLresultList = query.getResultList();
            assertEquals("Incorrect results returned", 1, JPQLresultList.size());
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
            List<?> resultList = query.getResultList();

            assertEquals("Incorrect result size returned", 1, resultList.size());
            assertEquals("Incorrect results returned", 1000D, (Double) resultList.get(0));
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
            List<?> resultList = query.getResultList();

            assertEquals("Incorrect result size returned", 1, resultList.size());
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
            List<?> resultList = query.getResultList();
            assertEquals("Incorrect results returned, expected 1, received: " + resultList.size(), 1, resultList.size());
        } finally {
            if (this.isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
    }
}

