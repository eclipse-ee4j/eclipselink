/*******************************************************************************
 * Copyright (c) 2005, 2009 SAP. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     SAP - initial API and implementation
 ******************************************************************************/

package org.eclipse.persistence.testing.tests.wdf.jpa1.relation;

import java.sql.Date;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.EntityManager;

import org.eclipse.persistence.testing.framework.wdf.JPAEnvironment;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Department;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Employee;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Patent;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Project;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Review;
import org.eclipse.persistence.testing.tests.wdf.jpa1.JPA1Base;
import org.junit.Test;

@SuppressWarnings("unchecked")
public class TestMultipleRelationships extends JPA1Base {
    private static final int HANS_ID_VALUE = 1;
    private static final int FRED_ID_VALUE = 2;
    private static final Integer HANS_ID = new Integer(HANS_ID_VALUE);
    private static final Project PUHLEN = new Project("G\u00fcrteltiere puhlen");
    private static final Project PINSELN = new Project("B\u00e4uche pinseln");
    private static final Project FALTEN = new Project("Zitronen falten");
    private static final Project ZAEHLEN = new Project("Erbsen z\u00e4hlen");
    private static final Project LINKEN = new Project("Eclipse linken");
    private static final Review PERFORMANCE = new Review(1, Date.valueOf("2005-10-07"), "performance");
    private static final Review PASSION = new Review(2, Date.valueOf("2005-10-08"), "passion");
    private static final Review PROFICIENCY = new Review(3, Date.valueOf("2005-10-09"), "proficiency");
    private static final Patent LIGHT_BULB = new Patent("light bulb", 1879, "artificial light source", java.sql.Date
            .valueOf("1879-11-05"));
    private static final Patent PHONOGRAPGH = new Patent("phonograph", 1877, "simple voice recorder", java.sql.Date
            .valueOf("1877-01-13"));
    private static final Patent ALTERNATING_CURRENT = new Patent("alternating current", 1888, "alternating current", Date
            .valueOf("1888-03-17"));
    private static final Patent HELICOPTER = new Patent("helicopter", 1922, "flying machine", Date.valueOf("1922-11-11")); // Etienne

    // Oehmichen

    private void seedDataModel() throws SQLException {
        /*
         * 5 Projects:
         */
        clearAllTables(); // clear all tables;
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            env.beginTransaction(em);
            Department dep = new Department(17, "diverses");
            em.persist(dep);
            em.persist(PUHLEN);
            em.persist(PINSELN);
            em.persist(FALTEN);
            em.persist(ZAEHLEN);
            em.persist(LINKEN);
            em.persist(LIGHT_BULB);
            em.persist(PHONOGRAPGH);
            em.persist(ALTERNATING_CURRENT);
            em.persist(HELICOPTER);
            em.persist(PERFORMANCE);
            em.persist(PASSION);
            em.persist(PROFICIENCY);
            Employee hans = new Employee(HANS_ID_VALUE, "Hans", "Wurst", dep);
            Set<Project> hansProjects = new HashSet<Project>();
            hansProjects.add(PUHLEN);
            hansProjects.add(PINSELN);
            hansProjects.add(FALTEN);
            hans.setProjects(hansProjects);
            hans.addReview(PERFORMANCE);
            hans.addReview(PASSION);
            Employee fred = new Employee(FRED_ID_VALUE, "Fred", "vom Jupiter", dep);
            Set<Project> fredProjects = new HashSet<Project>();
            fredProjects.add(FALTEN);
            fredProjects.add(ZAEHLEN);
            fred.setProjects(fredProjects);
            fred.addReview(PROFICIENCY);
            Set<Patent> fredsPatents = new HashSet<Patent>();
            fredsPatents.add(LIGHT_BULB);
            fredsPatents.add(PHONOGRAPGH);
            fred.setPatents(fredsPatents);
            Set<Patent> hansPatents = new HashSet<Patent>();
            hansPatents.add(PHONOGRAPGH);
            hansPatents.add(ALTERNATING_CURRENT);
            hans.setPatents(hansPatents);
            em.persist(hans);
            em.persist(fred);
            env.commitTransactionAndClear(em);
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testCheckHans() throws SQLException {
        seedDataModel(); // clear all tables;
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            env.beginTransaction(em);
            Employee hans = em.find(Employee.class, HANS_ID);
            Set reviews = hans.getReviews();
            verify(reviews != null, "Hans has no reviews");
            verify(reviews.size() == 2, "Hans has wrong number of reviews");
            verify(reviews.contains(PASSION), "Hans has no passion!");
            verify(reviews.contains(PERFORMANCE), "Hans has no performance!");
            Set projects = hans.getProjects();
            verify(projects != null, "Hans has no projects");
            verify(projects.size() == 3, "Hans has wrong number of projects");
            verify(projects.contains(PUHLEN), "Hans misses project " + PUHLEN.getName());
            verify(projects.contains(PINSELN), "Hans misses project " + PINSELN.getName());
            verify(projects.contains(FALTEN), "Hans misses project " + FALTEN.getName());
            Collection patents = hans.getPatents();
            verify(patents != null, "Hans has no patens");
            verify(patents.size() == 2, "Hans has wrong number of patents");
            verify(patents.contains(PHONOGRAPGH), "Hans misses patent " + PHONOGRAPGH.getId().getName());
            verify(patents.contains(ALTERNATING_CURRENT), "Hans misses patent " + ALTERNATING_CURRENT.getId().getName());
            env.rollbackTransactionAndClear(em);
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testDeletePatent() throws SQLException {
        seedDataModel(); // clear all tables;
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            env.beginTransaction(em);
            Employee hans = em.find(Employee.class, HANS_ID);
            Collection patents = hans.getPatents();
            patents.remove(PHONOGRAPGH);
            env.commitTransactionAndClear(em);
            env.beginTransaction(em);
            hans = em.find(Employee.class, HANS_ID);
            Set reviews = hans.getReviews();
            verify(reviews != null, "Hans has no reviews");
            verify(reviews.size() == 2, "Hans has wrong number of reviews");
            verify(reviews.contains(PASSION), "Hans has no passion!");
            verify(reviews.contains(PERFORMANCE), "Hans has no performance!");
            Set projects = hans.getProjects();
            verify(projects != null, "Hans has no projects");
            verify(projects.size() == 3, "Hans has wrong number of projects");
            verify(projects.contains(PUHLEN), "Hans misses project " + PUHLEN.getName());
            verify(projects.contains(PINSELN), "Hans misses project " + PINSELN.getName());
            verify(projects.contains(FALTEN), "Hans misses project " + FALTEN.getName());
            patents = hans.getPatents();
            verify(patents != null, "Hans has no patens");
            verify(patents.size() == 1, "Hans has wrong number of projects");
            verify(patents.contains(ALTERNATING_CURRENT), "Hans misses patent " + LIGHT_BULB.getId().getName());
            env.rollbackTransactionAndClear(em);
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testAddReview() throws SQLException {
        seedDataModel(); // clear all tables;
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            env.beginTransaction(em);
            Employee hans = em.find(Employee.class, HANS_ID);
            hans.getReviews().add(PROFICIENCY);
            env.commitTransactionAndClear(em);
            env.beginTransaction(em);
            hans = em.find(Employee.class, HANS_ID);
            Set reviews = hans.getReviews();
            verify(reviews != null, "Hans has no reviews");
            verify(reviews.size() == 3, "Hans has wrong number of reviews");
            verify(reviews.contains(PASSION), "Hans has no passion!");
            verify(reviews.contains(PERFORMANCE), "Hans has no performance!");
            verify(reviews.contains(PROFICIENCY), "Hans has no performance!");
            Set projects = hans.getProjects();
            verify(projects != null, "Hans has no projects");
            verify(projects.size() == 3, "Hans has wrong number of projects");
            verify(projects.contains(PUHLEN), "Hans misses project " + PUHLEN.getName());
            verify(projects.contains(PINSELN), "Hans misses project " + PINSELN.getName());
            verify(projects.contains(FALTEN), "Hans misses project " + FALTEN.getName());
            Collection patents = hans.getPatents();
            verify(patents != null, "Hans has no patens");
            verify(patents.size() == 2, "Hans has wrong number of patents");
            verify(patents.contains(PHONOGRAPGH), "Hans misses patent " + PHONOGRAPGH.getId().getName());
            verify(patents.contains(ALTERNATING_CURRENT), "Hans misses patent " + ALTERNATING_CURRENT.getId().getName());
            env.rollbackTransactionAndClear(em);
        } finally {
            closeEntityManager(em);
        }
    }
}
