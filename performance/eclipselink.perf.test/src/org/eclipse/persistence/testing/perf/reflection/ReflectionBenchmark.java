/*
 * Copyright (c) 2014, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Marcel Valovy - 2.6 - initial implementation
package org.eclipse.persistence.testing.perf.reflection;

import org.eclipse.persistence.internal.security.PrivilegedGetDeclaredFields;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.infra.Blackhole;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * Performance tests for Reflection in EclipseLink. They use auxiliary copies of the new ReflectionUtils
 * class and also some new test classes like ThreadLocalReflectionUtils, and in some tests, a modified version of the
 * old PrivilegedAccessHelper is called with the only difference that
 * {@link PrivilegedAccessHelper#shouldUsePrivilegedAccess()} always returns true.
 *
 * Tests are ran in multi-threaded environment.
 *
 * @author Marcel Valovy <marcel.valovy@oracle.com>
 */
@State(Scope.Benchmark)
public class ReflectionBenchmark {

    public static final int THREADS = 12;
    public static int random = 0;

    @Setup
    public void prepare() throws Exception {
    }

    @Benchmark
    @Threads(THREADS)
    public void testNewPrivilegedAction(Blackhole bh) throws Exception {
        Field[] fields;
        if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()) {
            fields = AccessController.doPrivileged(new PrivilegedAction<Field[]>() {
                @Override
                public Field[] run() {
                    return getRandomClazz().getDeclaredFields();
                }
            });
        } else {
            throw new Error();
        }
        bh.consume(fields);
    }

    @Benchmark
    @Threads(THREADS)
    public void testPrivilegedAccessHelper(Blackhole bh) throws Exception {
        Field[] f;
        if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()) {
            f = AccessController.doPrivileged(new PrivilegedGetDeclaredFields(getRandomClazz()));
        } else {
            throw new Error();
        }
        bh.consume(f);
    }

    @Benchmark
    @Threads(THREADS)
    public void testReflectionUtils(Blackhole bh) throws Exception {
        Field[] f = ReflectionUtils.getDeclaredFields(getRandomClazz());
        bh.consume(f);
    }

    @Benchmark
    @Threads(THREADS)
    public void testSimpleConcurrentReflectionUtils(Blackhole bh) throws Exception {
        Field[] f = SimpleConcurrentReflectionUtils.getDeclaredFields(getRandomClazz());
        bh.consume(f);
    }

    @Benchmark
    @Threads(THREADS)
    public void testThreadLocalReflectionUtils(Blackhole bh) throws Exception {
        Field[] f = ThreadLocalReflectionUtils.UTILS.getDeclaredFieldsTL(getRandomClazz());
        bh.consume(f);
    }

    @Benchmark
    @Threads(THREADS)
    public void testNOSECURITYPrivilegedAccessHelper(Blackhole bh) throws Exception {
        Field[] f;
        f = PrivilegedAccessHelper.getDeclaredFields(getRandomClazz());
        bh.consume(f);
    }

    @Benchmark
    @Threads(THREADS)
    public void testNOSECURITYReflectionUtils(Blackhole bh) throws Exception {
        Field[] f = ReflectionUtils.getDeclaredFields2(getRandomClazz());
        bh.consume(f);
    }

    @Benchmark
    @Threads(1)
    public void testSingleThreadedNewPrivilegedAction(Blackhole bh) throws Exception {
        Field[] fields;
        if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()) {
            fields = AccessController.doPrivileged(new PrivilegedAction<Field[]>() {
                @Override
                public Field[] run() {
                    return getRandomClazz().getDeclaredFields();
                }
            });
        } else {
            throw new Error();
        }
        bh.consume(fields);
    }

    @Benchmark
    @Threads(1)
    public void testSingleThreadedPrivilegedAccessHelper(Blackhole bh) throws Exception {
        Field[] f;
        if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()) {
            f = AccessController.doPrivileged(new PrivilegedGetDeclaredFields(getRandomClazz()));
        } else {
            throw new Error();
        }
        bh.consume(f);
    }

    @Benchmark
    @Threads(1)
    public void testSingleThreadedReflectionUtils(Blackhole bh) throws Exception {
        Field[] f = ReflectionUtils.getDeclaredFields(getRandomClazz());
        bh.consume(f);
    }

    @Benchmark
    @Threads(1)
    public void testSingleThreadedSimpleConcurrentReflectionUtils(Blackhole bh) throws Exception {
        Field[] f = SimpleConcurrentReflectionUtils.getDeclaredFields(getRandomClazz());
        bh.consume(f);
    }

    @Benchmark
    @Threads(1)
    public void testSingleThreadedThreadLocalReflectionUtils(Blackhole bh) throws Exception {
        Field[] f = ThreadLocalReflectionUtils.UTILS.getDeclaredFieldsTL(getRandomClazz());
        bh.consume(f);
    }

    @Benchmark
    @Threads(4)
    public void testThreadPerCoreNewPrivilegedAction(Blackhole bh) throws Exception {
        Field[] fields;
        if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()) {
            fields = AccessController.doPrivileged(new PrivilegedAction<Field[]>() {
                @Override
                public Field[] run() {
                    return getRandomClazz().getDeclaredFields();
                }
            });
        } else {
            throw new Error();
        }
        bh.consume(fields);
    }

    @Benchmark
    @Threads(4)
    public void testThreadPerCorePrivilegedAccessHelper(Blackhole bh) throws Exception {
        Field[] f;
        if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()) {
            f = AccessController.doPrivileged(new PrivilegedGetDeclaredFields(getRandomClazz()));
        } else {
            throw new Error();
        }
        bh.consume(f);
    }

    @Benchmark
    @Threads(4)
    public void testThreadPerCoreReflectionUtils(Blackhole bh) throws Exception {
        Field[] f = ReflectionUtils.getDeclaredFields(getRandomClazz());
        bh.consume(f);
    }

    @Benchmark
    @Threads(4)
    public void testThreadPerCoreSimpleConcurrentReflectionUtils(Blackhole bh) throws Exception {
        Field[] f = SimpleConcurrentReflectionUtils.getDeclaredFields(getRandomClazz());
        bh.consume(f);
    }

    @Benchmark
    @Threads(4)
    public void testThreadPerCoreThreadLocalReflectionUtils(Blackhole bh) throws Exception {
        Field[] f = ThreadLocalReflectionUtils.UTILS.getDeclaredFieldsTL(getRandomClazz());
        bh.consume(f);
    }

    private Class<? extends Serializable> getRandomClazz() {
        return random++ % 2 == 1 ? String.class : Boolean.class;
    }

}
