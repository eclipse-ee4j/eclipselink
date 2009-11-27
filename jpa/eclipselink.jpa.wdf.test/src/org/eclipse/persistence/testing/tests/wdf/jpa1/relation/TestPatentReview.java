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

import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.junit.Test;

import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Patent;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.PatentReview;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.PatentId;

import org.eclipse.persistence.testing.tests.wdf.jpa1.JPA1Base;
import org.eclipse.persistence.testing.framework.wdf.JPAEnvironment;

public class TestPatentReview extends JPA1Base {

    @Test
    public void testToOneRelationshipWithEmbddedKey() throws SQLException {
        clearAllTables();
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        try {
            final String FUSSBALL = "Fussball";
            env.beginTransaction(em);
            PatentReview patentReview = new PatentReview();
            Patent patent = new Patent(FUSSBALL, 1857, "das Fu\u00dfballspiel", Date.valueOf("1857-01-01"));
            patentReview.setId(17);
            patentReview.setPatent(patent);
            patentReview.setReviewText("Review des Patents des Fu\u00dfballspiels");
            em.persist(patent);
            em.persist(patentReview);
            env.commitTransactionAndClear(em);
            Object found = em.find(PatentReview.class, Integer.valueOf(17));
            verify(found != null, "nothing found");
            verify(found instanceof PatentReview, "wrong instance: " + found.getClass().getName());
            PatentReview review = (PatentReview) found;
            verify(review.getPatent() != null, "patent is null");
            Patent pat = review.getPatent();
            PatentId id = pat.getId();
            verify(FUSSBALL.equals(id.getName()), "patent has wrong name: " + id.getName());
            verify(id.getYear() == 1857, "patent has wrong year: " + id.getYear());
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testNoEnBlocLoading() throws SQLException {
        clearAllTables();
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        try {
            env.beginTransaction(em);

            final String PARSER = "SQL Parser";
            PatentReview patentReview = new PatentReview();
            Patent patent = new Patent(PARSER, 2002, "SQL Parser", Date.valueOf("2002-01-01"));
            patentReview.setId(20);
            patentReview.setPatent(patent);
            patentReview.setReviewText("Review vom SQL Parser Patent");
            em.persist(patent);
            em.persist(patentReview);

            final String SORTER = "Foreign Key Sorter";
            patentReview = new PatentReview();
            patent = new Patent(SORTER, 2008, "Foreign Key Sorter", Date.valueOf("2008-01-01"));
            patentReview.setId(21);
            patentReview.setPatent(patent);
            patentReview.setReviewText("Review vom Foreign Key Sorter");
            em.persist(patent);
            em.persist(patentReview);

            final String MONITOR = "JPA Monitor";
            patentReview = new PatentReview();
            patent = new Patent(MONITOR, 2007, "JPA Monitor", Date.valueOf("2007-01-01"));
            patentReview.setId(22);
            patentReview.setPatent(patent);
            patentReview.setReviewText("Review vom JPA Monitor");
            em.persist(patent);
            em.persist(patentReview);

            final String MANAGER = "Environment Manager";
            patentReview = new PatentReview();
            patent = new Patent(MANAGER, 2005, "Environment Manager", Date.valueOf("2005-01-01"));
            patentReview.setId(23);
            patentReview.setPatent(patent);
            patentReview.setReviewText("Review vom Environment Manager");
            em.persist(patent);
            em.persist(patentReview);

            env.commitTransactionAndClear(em);

            env.beginTransaction(em);
            Query query = em.createQuery("select r from PatentReview r");
            query.getResultList();

        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testNativeQuery() throws SQLException {
        clearAllTables();
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        try {
            final String FUSSBALL = "Fussball";
            env.beginTransaction(em);
            PatentReview patentReview = new PatentReview();
            Patent patent = new Patent(FUSSBALL, 1857, "das Fu\u00dfballspiel", Date.valueOf("1857-01-01"));
            patentReview.setId(18);
            patentReview.setPatent(patent);
            patentReview.setReviewText("Review des Patents des Fu\u00dfballspiels");
            em.persist(patent);
            em.persist(patentReview);
            env.commitTransactionAndClear(em);
            Query query = em
                    .createNativeQuery(
                            "select * from TMP_REVIEW join TMP_REVIEW_DETAILS on TMP_REVIEW.ID = TMP_REVIEW_DETAILS.REVIEW_ID where ID = 18",
                            PatentReview.class);
            Object found = query.getSingleResult();
            verify(found != null, "nothing found");
            verify(found instanceof PatentReview, "wrong instance: " + found.getClass().getName());
            PatentReview review = (PatentReview) found;
            verify(review.getPatent() != null, "patent is null");
            Patent pat = review.getPatent();
            PatentId id = pat.getId();
            verify(FUSSBALL.equals(id.getName()), "patent has wrong name: " + id.getName());
            verify(id.getYear() == 1857, "patent has wrong year: " + id.getYear());
        } finally {
            closeEntityManager(em);
        }
    }
}
