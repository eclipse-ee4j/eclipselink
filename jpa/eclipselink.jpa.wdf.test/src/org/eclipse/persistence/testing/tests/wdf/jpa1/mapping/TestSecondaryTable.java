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

package org.eclipse.persistence.testing.tests.wdf.jpa1.mapping;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.EntityManager;
import javax.persistence.OptimisticLockException;
import org.junit.Test;

import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Department;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Employee;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Project;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Review;

import org.eclipse.persistence.testing.framework.wdf.JPAEnvironment;
import org.eclipse.persistence.testing.framework.wdf.ToBeInvestigated;
import org.eclipse.persistence.testing.tests.wdf.jpa1.JPA1Base;

public class TestSecondaryTable extends JPA1Base {

    @Test
    public void testFieldsInSecondTable() throws SQLException {
        String projectName = "trallala";
        int plannedDays = 7;
        int usedDays = 8;
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            Project project = new Project(projectName);
            project.setPlannedDays(plannedDays);
            project.setUsedDays(usedDays);
            env.beginTransaction(em);
            em.persist(project);
            env.commitTransaction(em); // insert
            env.beginTransaction(em);
            project = em.merge(project);
            verify(project.getName().equals(projectName), "wrong name");
            verify(project.getPlannedDays() == plannedDays, "wrong number of plannedDays");
            verify(project.getUsedDays() == usedDays, "wrong number of usedDays");
            plannedDays = 17;
            usedDays = 18;
            project.setPlannedDays(plannedDays);
            project.setUsedDays(usedDays);
            env.commitTransaction(em); // update
            env.beginTransaction(em);
            project = em.merge(project);
            verify(project.getName().equals(projectName), "wrong name");
            verify(project.getPlannedDays() == plannedDays, "wrong number of plannedDays");
            verify(project.getUsedDays() == usedDays, "wrong number of usedDays");
            em.remove(project);
            env.commitTransaction(em); // delete
            Integer projectId = project.getId();
            con = env.getDataSource().getConnection();
            pstmt = con.prepareStatement("select count(*) from TMP_PROJECT_DETAILS where PROJECT_ID = ?");
            pstmt.setInt(1, projectId.intValue());
            rs = pstmt.executeQuery();
            rs.next();
            verify(rs.getInt(1) == 0, "secondary table not empty");
        } finally {
            rs.close();
            pstmt.close();
            con.close();
            closeEntityManager(em);
        }
    }

    @Test
    public void testSecondaryTableInRelation() {
        String projectName1 = "P1";
        String projectName2 = "P2";
        int plannedDays1 = 17;
        int usedDays1 = 18;
        int plannedDays2 = 27;
        int usedDays2 = 28;
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            Project project1 = new Project(projectName1);
            project1.setPlannedDays(plannedDays1);
            project1.setUsedDays(usedDays1);
            Project project2 = new Project(projectName2);
            project2.setPlannedDays(plannedDays2);
            project2.setUsedDays(usedDays2);
            Set<Project> projects = new HashSet<Project>();
            projects.add(project1);
            projects.add(project2);
            Department dep = new Department(7, "dep7");
            Employee horst = new Employee(13, "Horst", "Bullerjahn", dep);
            horst.setProjects(projects);
            env.beginTransaction(em);
            em.persist(dep);
            em.persist(project1);
            em.persist(project2);
            em.persist(horst);
            env.commitTransaction(em);
            env.beginTransaction(em);
            horst = em.merge(horst);
            projects = horst.getProjects();
            verify(projects.size() == 2, "wrong number of projects");
            for (Project p : projects) {
                verify(p.getName().equals(projectName1) || p.getName().equals(projectName2), "wrong projectName");
                if (p.getName().equals(projectName1)) {
                    verify(p.getPlannedDays() == plannedDays1, "wrong number of plannedDays");
                    verify(p.getUsedDays() == usedDays1, "wrong number of usedDays");
                }
                if (p.getName().equals(projectName2)) {
                    verify(p.getPlannedDays() == plannedDays2, "wrong number of plannedDays");
                    verify(p.getUsedDays() == usedDays2, "wrong number of usedDays");
                }
            }
            env.rollbackTransaction(em);
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    @ToBeInvestigated
    public void testOptimisticLockWithSecondaryTable() {
        JPAEnvironment env = getEnvironment();
        EntityManager em1 = env.getEntityManagerFactory().createEntityManager();
        EntityManager em2 = env.getEntityManagerFactory().createEntityManager();
        int id = 27;
        int version = 0;
        Review rev1 = new Review(id, Date.valueOf("2006-04-26"), "blah");
        try {
            env.beginTransaction(em1);
            em1.persist(rev1);
            rev1.setSuccessRate((short) 0);
            env.commitTransactionAndClear(em1);
            env.beginTransaction(em1);
            rev1 = em1.find(Review.class, Integer.valueOf(id));
            verify(rev1 != null, "Review is null");
            version = rev1.getVersion();
            env.beginTransaction(em2);
            Review rev2 = em2.find(Review.class, Integer.valueOf(id));
            rev2.setSuccessRate((short) 10); // 1 update
            env.commitTransactionAndClear(em2);
            rev1.setSuccessRate((short) 20); // 2 update
            env.commitTransactionAndClear(em1);
            flop("OptimisticLockException not thrown");
        } catch (OptimisticLockException ole) {
            // $JL-EXC$ expected behavior
        } finally {
            closeEntityManager(em1);
            closeEntityManager(em2);
        }
        verify(version == rev1.getVersion(), "wrong version");
    }
}
