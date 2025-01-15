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

package org.eclipse.persistence.testing.tests.jpa.criteria.advanced.compositepk;

import java.util.List;
import java.util.ArrayList;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.*;

import jakarta.persistence.EntityManager;

import junit.framework.*;

import org.eclipse.persistence.testing.framework.jpa.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.advanced.AdvancedTableCreator;
import org.eclipse.persistence.testing.models.jpa.advanced.compositepk.CompositePKTableCreator;
import org.eclipse.persistence.testing.models.jpa.advanced.compositepk.Department;
import org.eclipse.persistence.testing.models.jpa.advanced.compositepk.DepartmentPK;
import org.eclipse.persistence.testing.models.jpa.advanced.compositepk.Scientist;
import org.eclipse.persistence.testing.models.jpa.advanced.compositepk.ScientistPK;

public class AdvancedCompositePKJunitTest extends JUnitTestCase {
    private static DepartmentPK m_departmentPK;
    private static ScientistPK m_scientist1PK, m_scientist2PK, m_scientist3PK, m_jScientistPK;

    public AdvancedCompositePKJunitTest() {
        super();
    }

    public AdvancedCompositePKJunitTest(String name) {
        super(name);
        setPuName(getPersistenceUnitName());
    }

    @Override
    public String getPersistenceUnitName() {
        return "advanced-compositepk";
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("AdvancedCompositePKJunitTest");

        suite.addTest(new AdvancedCompositePKJunitTest("testSetup"));
        suite.addTest(new AdvancedCompositePKJunitTest("testAnyAndAll"));

        return suite;
    }

    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
        new CompositePKTableCreator().replaceTables(getPersistenceUnitServerSession());
        new AdvancedTableCreator().replaceTables(getPersistenceUnitServerSession());
        clearCache();
    }

    //bug gf672 - JPQL Select query with IN/ANY in WHERE clause and subselect fails.
    public void testAnyAndAll() {
        EntityManager em = createEntityManager();

        beginTransaction(em);
        try {
            CriteriaBuilder qb = em.getCriteriaBuilder();
            CriteriaQuery<Scientist> cq = qb.createQuery(Scientist.class);
            Subquery sq = cq.subquery(Scientist.class);
            Root<Scientist> from_scientist = cq.from(Scientist.class);

            cq.where(qb.equal(from_scientist, qb.any(sq)));
            Query query1 = em.createQuery(cq);

            List<Scientist> results1 = query1.getResultList();

            //Query query2 = em.createQuery("SELECT s FROM Scientist s WHERE s = ALL (SELECT s2 FROM Scientist s2)");
            qb = em.getCriteriaBuilder();
            cq = qb.createQuery(Scientist.class);
            sq = cq.subquery(Scientist.class);
            from_scientist = cq.from(Scientist.class);

            cq.where(qb.equal(from_scientist, qb.all(sq)));
            Query query2 = em.createQuery(cq);
            List<Scientist> results2 = query2.getResultList();

            //Query query3 = em.createQuery("SELECT s FROM Scientist s WHERE s.department = ALL (SELECT DISTINCT d FROM Department d WHERE d.name = 'DEPT A' AND d.role = 'ROLE A' AND d.location = 'LOCATION A')");
            qb = em.getCriteriaBuilder();
            cq = qb.createQuery(Scientist.class);
            sq = cq.subquery(Department.class);
            sq.distinct(true);
            Root<Department> from_department = sq.from(Department.class);
            sq.where(qb.and(qb.and(qb.equal(from_department.get("name"), "DEPT A"),qb.equal(from_department.get("role"), "ROLE A")), qb.equal(from_department.get("location"), "LOCATION A")) );

            from_scientist = cq.from(Scientist.class);

            cq.where(qb.equal(from_scientist.get("department"), qb.all(sq)));
            Query query3 = em.createQuery(cq);
            List<Scientist> results3 = query3.getResultList();

            //Query query4 = em.createQuery("SELECT s FROM Scientist s WHERE s.department = ANY (SELECT DISTINCT d FROM Department d JOIN d.scientists ds JOIN ds.cubicle c WHERE c.code = 'G')");
            qb = em.getCriteriaBuilder();
            cq = qb.createQuery(Scientist.class);
            sq = cq.subquery(Department.class);
            sq.distinct(true);
            from_department = sq.from(Department.class);
            Join c = from_department.join("scientists").join("cubicle");
            sq.where(qb.equal(c.get("code"), "G") );

            from_scientist = cq.from(Scientist.class);

            cq.where(qb.equal(from_scientist.get("department"), qb.any(sq)));
            Query query4 = em.createQuery(cq);
            List<Scientist> results4 = query4.getResultList();
            // control queries

            //Query controlQuery1 = em.createQuery("SELECT s FROM Scientist s");
            Query controlQuery1 = em.createQuery(em.getCriteriaBuilder().createQuery(Scientist.class));
            List<Scientist> controlResults1 = controlQuery1.getResultList();

            List<Scientist> controlResults2;
            if(controlResults1.size() == 1) {
                controlResults2 = controlResults1;
            } else {
                controlResults2 = new ArrayList<>();
            }

            //Query controlQuery3 = em.createQuery("SELECT s FROM Scientist s JOIN s.department d WHERE d.name = 'DEPT A' AND d.role = 'ROLE A' AND d.location = 'LOCATION A'");
            qb = em.getCriteriaBuilder();
            cq = qb.createQuery(Scientist.class);
            from_scientist = cq.from(Scientist.class);
            Join d = from_scientist.join("department");
            cq.where(qb.and(qb.and(qb.equal(d.get("name"), "DEPT A"), qb.equal(d.get("role"), "ROLE A")), qb.equal(d.get("location"), "LOCATION A")) );

            Query controlQuery3 = em.createQuery(cq);
            List<Scientist> controlResults3 = controlQuery3.getResultList();

            //Query controlQuery4 = em.createQuery("SELECT s FROM Scientist s WHERE EXISTS (SELECT DISTINCT d FROM Department d JOIN d.scientists ds JOIN ds.cubicle c WHERE c.code = 'G' AND d = s.department)");
            qb = em.getCriteriaBuilder();
            cq = qb.createQuery(Scientist.class);

            sq = cq.subquery(Department.class);
            sq.distinct(true);
            from_department = sq.from(Department.class);
            from_scientist = cq.from(Scientist.class);
            c = from_department.join("scientists").join("cubicle");
            sq.where(qb.and(qb.equal(c.get("code"), "G"), qb.equal(from_department, from_scientist.get("department"))) );

            cq.where(qb.exists(sq) );

            Query controlQuery4 = em.createQuery(cq);
            List<Scientist> controlResults4 = controlQuery4.getResultList();

            //compare results - they should be the same
            compareResults(results1, controlResults1, "query1");
            compareResults(results2, controlResults2, "query2");
            compareResults(results3, controlResults3, "query3");
            compareResults(results4, controlResults4, "query4");
        } finally {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
    }

    protected void compareResults(List results, List controlResults, String testName) {
        if(results.size() != controlResults.size()) {
            fail(testName + ": results.size() = " + results.size() + "; controlResults.size() = " + controlResults.size());
        }
        for (Object s : results) {
            if(!controlResults.contains(s)) {
                fail(testName + ": " + s + "contained in results but not in controlResults");
            }
        }
    }
}
