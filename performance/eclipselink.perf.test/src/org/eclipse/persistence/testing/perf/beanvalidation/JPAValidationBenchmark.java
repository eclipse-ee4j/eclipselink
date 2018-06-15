/*
 * Copyright (c) 2015, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Marcel Valovy - initial implementation
package org.eclipse.persistence.testing.perf.beanvalidation;

import org.eclipse.persistence.jpa.PersistenceProvider;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.infra.Blackhole;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

/**
 * Performance baseline for JPA processes using Bean Validation.
 * Tests provide comparison in performance of bean validation callbacks occurring during basic JPA mapping
 * processes, with objects that are constrained against the same objects that are not constrained.
 *
 * @author Marcel Valovy
 */
@State(Scope.Benchmark)
public class JPAValidationBenchmark {

    private EntityManagerFactory emf;
    private EntityManager em;

    @Benchmark
    public void testJpaAnnotated(Blackhole bh) throws Exception {
        final EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        EmployeeAnnotated employee = new EmployeeAnnotated().withAge(51289).withPersonalName("Robert Paulson")
                .withPhoneNumber("(420)333-4444").withId(250);
        em.persist(employee);
        transaction.rollback();
        bh.consume(transaction);
        bh.consume(employee);
    }

    @Benchmark
    public void testJpa(Blackhole bh) throws Exception {
        final EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        Employee employee = new Employee().withAge(51289).withPersonalName("Robert Paulson")
                .withPhoneNumber("(420)333-4444").withId(250);
        em.persist(employee);
        transaction.rollback();
        bh.consume(transaction);
        bh.consume(employee);
    }

    /**
     * Initial setup.
     */
    @Setup
    public void prepare() throws Exception {
        prepareJPA();
    }

    /**
     * Clean-up.
     */
    @TearDown
    public void tearDown() throws Exception {
        emf = null;
        em.close();
        em = null;
    }

    private void prepareJPA() throws Exception {
        emf = new PersistenceProvider().createEntityManagerFactory("my-app", null);
        em = emf.createEntityManager();
    }
}
