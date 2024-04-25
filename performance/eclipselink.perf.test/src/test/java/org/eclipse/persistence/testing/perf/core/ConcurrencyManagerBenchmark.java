/*
 * Copyright (c) 2024 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial implementation
package org.eclipse.persistence.testing.perf.core;

import org.eclipse.persistence.internal.helper.ConcurrencyManager;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

/**
 * This benchmark verify performance of {@code org.eclipse.persistence.internal.helper.ConcurrencyManager}.
 *
 */
@State(Scope.Benchmark)
public class ConcurrencyManagerBenchmark {

    @Benchmark
    public void testAcquireRelease(Blackhole bh) throws Exception {
        ConcurrencyManager concurrencyManager = new ConcurrencyManager();
        concurrencyManager.acquire();
        concurrencyManager.release();
    }
}
