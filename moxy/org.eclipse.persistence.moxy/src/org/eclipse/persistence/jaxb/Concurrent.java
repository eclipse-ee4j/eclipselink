/******************************************************************************
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
 *******************************************************************************/
package org.eclipse.persistence.jaxb;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Helper class for MOXy concurrency.
 */
public class Concurrent {

    /**
     * Returns managed executor service if available. Otherwise returns Executors.newSingleThreadExecutor().
     */
    public static ExecutorService getExecutorService() {
        Crate.Tuple<ExecutorService, Boolean> o = new Crate.Tuple<>();
        try {
            InitialContext jndiCtx = new InitialContext();
            // type:      javax.enterprise.concurrent.ManagedExecutorService
            // jndi-name: concurrent/ThreadPool
            return ((ExecutorService) jndiCtx.lookup("java:comp/env/concurrent/ThreadPool"));
        } catch (NamingException ignored) {
            // aka continue to proceed with retrieving jdk executor
        }

        return Executors.newSingleThreadExecutor();
    }
}