/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
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
//     09/12/2018 - Will Dazey
//       - 391279: Add support for Unidirectional OneToMany mappings with non-nullable values
package org.eclipse.persistence.jpa.test.mapping;

import java.util.ArrayList;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.framework.Property;
import org.eclipse.persistence.jpa.test.mapping.model.CommentA;
import org.eclipse.persistence.jpa.test.mapping.model.CommentB;
import org.eclipse.persistence.jpa.test.mapping.model.CommentC;
import org.eclipse.persistence.jpa.test.mapping.model.CommentD;
import org.eclipse.persistence.jpa.test.mapping.model.ComplexIdA;
import org.eclipse.persistence.jpa.test.mapping.model.ComplexIdB;
import org.eclipse.persistence.jpa.test.mapping.model.ComplexIdC;
import org.eclipse.persistence.jpa.test.mapping.model.ComplexIdD;
import org.eclipse.persistence.jpa.test.mapping.model.PostA;
import org.eclipse.persistence.jpa.test.mapping.model.PostB;
import org.eclipse.persistence.jpa.test.mapping.model.PostC;
import org.eclipse.persistence.jpa.test.mapping.model.PostD;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * EclipseLink performs Inserts to UnidirectionalOneToMany mappings through
 * deferred Updates. EclipseLink first INSERTS null FK values in the owning
 * table, then schedules UPDATES to update to the correct value.
 *
 * However, this does not work if the field is 'nullable=false'. If that
 * configuration exists, then the driver will throw an exception when
 * attempting to INSERT a null value
 */
@RunWith(EmfRunner.class)
public class TestUnidirectionalOneToMany {
    @Emf(createTables = DDLGen.DROP_CREATE,
            classes = { CommentA.class, CommentB.class, CommentC.class, CommentD.class, 
                    ComplexIdA.class, ComplexIdB.class, ComplexIdC.class, ComplexIdD.class,
                    PostA.class, PostB.class, PostC.class, PostD.class },
            properties = {
                @Property(name = "eclipselink.cache.shared.default", value = "false"),
                @Property(name = "eclipselink.logging.parameters", value = "true"),
                @Property(name = "eclipselink.logging.exceptions", value = "true"),
                @Property(name = "eclipselink.logging.level.sql", value = "FINEST")})
    private EntityManagerFactory emf;

    /**
     * This case will test the 'PostInsert' scenario.
     * EclipseLink will create a 'PostInsert' deferred event upon postInsert() of the PostA object.
     * That event is meant as a reminder to UPDATE CommentA objects at the end of the commit.
     *
     * Instead, we want to intercept that event and merge the CommentA value into the INSERT that is done earlier in the commit.
     * Then, we remove the event since it is now not needed.
     */
    @Test
    public void testInsertOneToManyUni() {

        EntityManager em = emf.createEntityManager();

        em.getTransaction().begin();

        PostA post = new PostA(new ComplexIdA(9));
        post.setComments(new ArrayList<CommentA>());
        post.getComments().add(new CommentA());
        post.getComments().add(new CommentA());
        post.getComments().add(new CommentA());

        em.persist(post);

        em.getTransaction().commit();
    }

    /**
     * This case will test the 'PostUpdate' scenario.
     * EclipseLink will create a 'PostUpdate' deferred event upon postUpdate() of the PostA object.
     * That event is meant as a reminder to UPDATE CommentA objects at the end of the commit.
     *
     * Instead, we want to intercept that event and merge the CommentA value into the INSERT that is done earlier in the commit.
     * Then, we remove the event since it is now not needed.
     */
    @Test
    public void testUpdateOneToManyUni() {

        EntityManager em = emf.createEntityManager();

        em.getTransaction().begin();

        PostA post = new PostA(new ComplexIdA(10));
        em.persist(post);
        em.flush();

        post.setComments(new ArrayList<CommentA>());
        post.getComments().add(new CommentA());
        post.getComments().add(new CommentA());
        post.getComments().add(new CommentA());

        em.persist(post);

        em.getTransaction().commit();
    }

    @Test
    public void testInsertComplexOneToManyUni() {

        EntityManager em = emf.createEntityManager();

        em.getTransaction().begin();

        PostB post = new PostB(new ComplexIdB(3, 4));
        post.setComments(new ArrayList<CommentB>());
        post.getComments().add(new CommentB("a"));
        post.getComments().add(new CommentB("b"));
        post.getComments().add(new CommentB("c"));

        em.persist(post);

        em.getTransaction().commit();
    }

    @Test
    public void testUpdateComplexOneToManyUni() {

        EntityManager em = emf.createEntityManager();

        em.getTransaction().begin();

        PostB post = new PostB(new ComplexIdB(5, 6));
        em.persist(post);
        em.flush();

        post.setComments(new ArrayList<CommentB>());
        post.getComments().add(new CommentB("d"));
        post.getComments().add(new CommentB("e"));
        post.getComments().add(new CommentB("f"));

        em.persist(post);

        em.getTransaction().commit();
    }

    @Test
    public void testInsertComplexCOneToManyUni() {
        EntityManager em = null;
        try {
            em = emf.createEntityManager();

            em.getTransaction().begin();

            PostC post = new PostC(5L);
            post.setComments(new ArrayList<CommentC>());
            post.getComments().add(new CommentC(new ComplexIdC(5L, "Type1"), "a"));
            post.getComments().add(new CommentC(new ComplexIdC(5L, "Type2"), "b"));
            post.getComments().add(new CommentC(new ComplexIdC(5L, "Type3"), "c"));

            em.persist(post);

            em.getTransaction().commit();
        } finally {
            if(em != null) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                if(em.isOpen()) {
                    em.close();
                }
            }
        }
    }

    @Test
    public void testUpdateComplexCOneToManyUni() {
        EntityManager em = null;
        try {
            em = emf.createEntityManager();

            em.getTransaction().begin();

            PostC post = new PostC(6L);
            em.persist(post);
            em.flush();

            post.setComments(new ArrayList<CommentC>());
            post.getComments().add(new CommentC(new ComplexIdC(6L, "Type4"), "d"));
            post.getComments().add(new CommentC(new ComplexIdC(6L, "Type5"), "3"));
            post.getComments().add(new CommentC(new ComplexIdC(6L, "Type6"), "f"));

            em.persist(post);

            em.getTransaction().commit();
        } finally {
            if(em != null) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                if(em.isOpen()) {
                    em.close();
                }
            }
        }
    }

    @Test
    public void testInsertComplexDOneToManyUni() {
        EntityManager em = null;
        try {
            em = emf.createEntityManager();

            em.getTransaction().begin();

            PostD post = new PostD(new ComplexIdD(20, 21));
            post.setComments(new ArrayList<CommentD>());
            post.getComments().add(new CommentD(new ComplexIdD(20, 21), "a"));

            em.persist(post);

            em.getTransaction().commit();
        } finally {
            if(em != null) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                if(em.isOpen()) {
                    em.close();
                }
            }
        }
    }

    @Test
    public void testUpdateComplexDOneToManyUni() {
        EntityManager em = null;
        try {
            em = emf.createEntityManager();

            em.getTransaction().begin();

            PostD post = new PostD(new ComplexIdD(22, 23));
            em.persist(post);
            em.flush();

            post.setComments(new ArrayList<CommentD>());
            post.getComments().add(new CommentD(new ComplexIdD(22, 23), "d"));

            em.persist(post);

            em.getTransaction().commit();
        } finally {
            if(em != null) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                if(em.isOpen()) {
                    em.close();
                }
            }
        }
    }
}
