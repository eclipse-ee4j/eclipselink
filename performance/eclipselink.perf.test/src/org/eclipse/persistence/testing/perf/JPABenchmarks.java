/*
 * Copyright (c) 2016, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//              ljungmann - initial implementation
package org.eclipse.persistence.testing.perf;

import org.eclipse.persistence.testing.perf.jpa.tests.basic.JPAMetadataProcessingTests;
import org.eclipse.persistence.testing.perf.jpa.tests.basic.MethodHandleComparisonTests;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

public class JPABenchmarks {

    public static void main(String[] args) throws RunnerException {

        int warmupIterations = 20;
        int measurementIterations = 20;
        String resultFile = "jmh-jpa-result.txt";
        String resultFormat = "text";

        if (null != args && args.length == 4) {
            warmupIterations = Integer.parseInt(args[0]);
            measurementIterations = Integer.parseInt(args[1]);
            resultFile = args[2];
            resultFormat = args[3];
        }

        Options opt = new OptionsBuilder()
                .include(getInclude(JPAMetadataProcessingTests.class))
                .include(getInclude(MethodHandleComparisonTests.class))
                .jvmArgsPrepend("-javaagent:" + System.getProperty("eclipselink.agent"))
                .result(resultFile)
                .resultFormat(ResultFormatType.valueOf(resultFormat.toUpperCase()))
                .warmupIterations(warmupIterations)
                .measurementIterations(measurementIterations)
                .forks(1)
                .build();

        new Runner(opt).run();
    }

    private static String getInclude(Class<?> cls) {
        return ".*" + cls.getSimpleName() + ".*";
    }
}
