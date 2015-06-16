/**
 * ****************************************************************************
 * Copyright (c) 2015 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 * <p/>
 * Contributors:
 *      Marcel Valovy - initial API and implementation
 * ****************************************************************************
 */
package org.eclipse.persistence.internal.jpa.metadata.beanvalidation;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.eclipse.persistence.internal.cache.AdvancedProcessor;
import org.eclipse.persistence.internal.cache.ComputableTask;

/**
 * Helper class for concurrency, exploiting advantages of memoizer concept.
 *
 * @author Marcel Valovy - marcel.valovy@oracle.com
 * @since 2.6
 */
public class Concurrent {

    /**
     * cache storing already computed results
     */
    private static AdvancedProcessor cache = new AdvancedProcessor();

    /**
     * @return crate, containing two payloads:
     *              payload1 is ExecutorService - managed or jdk,
     *              payload2 is boolean, contains value true for managed executor, false for jdk executor
     */
    static Crate.Tuple<ExecutorService, Boolean> getManagedSingleThreadedExecutorService() {
        return cache.compute(GetManagedExecutorService.C, "executorService");
    }

    @SuppressWarnings("unchecked")
    private static class GetManagedExecutorService<A, V> implements ComputableTask<A, V> {

        private static final GetManagedExecutorService<String, Crate.Tuple<ExecutorService,
                        Boolean>> C = new GetManagedExecutorService<>();

        @Override
        public V compute(A arg) throws InterruptedException {
            Crate.Tuple<ExecutorService, Boolean> o = new Crate.Tuple<>();
            try {
                InitialContext jndiCtx = new InitialContext();
                // type:      javax.enterprise.concurrent.ManagedExecutorService
                // jndi-name: concurrent/ThreadPool
                o.setPayload1((ExecutorService) jndiCtx.lookup("java:comp/env/concurrent/ThreadPool"));
                if (o.getPayload1() != null) {
                    o.setPayload2(true);
                    return (V) o;
                }
            } catch (NamingException ignored) {
                // aka continue to proceed with retrieving jdk executor
            }
            o.setPayload1(Executors.newSingleThreadExecutor());
            o.setPayload2(false);
            return (V) o;
        }

        // Note: Here we do not need equals and hashCode because there are no fields. But remember to create these
        // methods for any ComputableTask class you create that has a state!
    }

}
