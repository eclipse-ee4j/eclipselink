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
//              Oracle - initial implementation
package org.eclipse.persistence.testing.perf.jpa.tests.basic;

import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

/**
 * Benchmarks for JPA reading data (large amount - 10000 rows per each request em.find()) with JPA L2 cache disabled.
 *
 * @author Oracle
 */
@State(Scope.Benchmark)
//@BenchmarkMode(Mode.AverageTime)
public class JPAReadLargeAmmountNoCacheTests extends JPAReadLargeAmmountAbstract {

    public String getPersistenceUnitName() {
        return "jpa-performance-read-no-cache";
    }
}
