/*
 * Copyright (c) 2024 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2024 IBM Corporation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */
package org.eclipse.persistence.jpa.test.jpql;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.ObjIntConsumer;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.jpql.model.JPQLEntity;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * This test reproduces the issues #2136, #1867 and #1717.
 *
 * @author Igor Mukhin
 */
@RunWith(EmfRunner.class)
public class TestArgumentListFunctionExpressionConcurrency {

    private static final int MAX_THREADS = Math.min(Runtime.getRuntime().availableProcessors(), 4);
    private static final int ITERATIONS_PER_THREAD = 1000;

    @Emf(name = "argumentListFunctionExpressionConcurrencyEMF", createTables = DDLGen.DROP_CREATE, classes = { JPQLEntity.class })
    private EntityManagerFactory emf;

    @Test
    public void testConcurrentUseOfCoalesce() throws Exception {
        runInParallel((em, i) -> {
                String jpql = "SELECT p FROM JPQLEntity p"
                            + " WHERE p.string1 = coalesce(p.string2, '" + cacheBuster(i) + "')";

                em.createQuery(jpql, JPQLEntity.class).getResultList();
        });
    }

    @Test
    public void testConcurrentUseOfCaseCondition() throws Exception {
        runInParallel((em, i) -> {
                String jpql = "SELECT p FROM JPQLEntity p"
                            + " WHERE p.string1 = case when p.string2 = '" + cacheBuster(i) + "' then null else p.string1 end";

                em.createQuery(jpql, JPQLEntity.class).getResultList();
        });
    }

    private static String cacheBuster(Integer i) {
        return "cacheBuster." + Thread.currentThread().getName() + "." + i;
    }

    private void runInParallel(ObjIntConsumer<EntityManager> runnable) throws Exception {
        AtomicReference<Exception> exception = new AtomicReference<>();

        // start all threads
        List<Thread> threads = new ArrayList<>();
        for (int t = 0; t < MAX_THREADS; t++) {
            Thread thread = new Thread(() -> {
                try {
                    for (int i = 0; i < ITERATIONS_PER_THREAD; i++) {
                        if (exception.get() != null) {
                            return;
                        }

                        EntityManager em = emf.createEntityManager();
                        try {
                            runnable.accept(em, i);
                        } finally {
                            em.close();
                        }

                    }
                } catch (Exception e) {
                    exception.set(e);
                }
            });
            threads.add(thread);
            thread.start();
        }

        // wait for all threads to finish
        threads.forEach(thread -> {
            try {
                thread.join();
            } catch (InterruptedException e) {
                exception.set(e);
            }
        });

        // throw the first exception that occurred
        if (exception.get() != null) {
            throw exception.get();
        }
    }
}
