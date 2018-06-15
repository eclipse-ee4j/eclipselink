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
//     Martin Vojtek - 2.6.0 - initial implementation
package org.eclipse.persistence.testing.perf;

import org.eclipse.persistence.testing.perf.beanvalidation.MOXyValidationBenchmark;
import org.eclipse.persistence.testing.perf.jpa.persistence_content_handler.PersistenceContentHandlerBenchmark;
import org.eclipse.persistence.testing.perf.json.marshal.JsonMarshalBenchmark;
import org.eclipse.persistence.testing.perf.json.unmarshal.JsonUnmarshalBenchmark;
import org.eclipse.persistence.testing.perf.json.writer.JsonWriterBenchmark;
import org.eclipse.persistence.testing.perf.largexml.LargeXmlBenchmark;
import org.eclipse.persistence.testing.perf.smallxml.SmallXmlBenchmark;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

/**
 * This class wraps other benchmarks. It is analogy to JUnit suites.
 *
 * @author Martin Vojtek (martin.vojtek@oracle.com)
 *
 */
public class Benchmarks {
    public static void main(String[] args) throws RunnerException {

        int warmupIterations = 20;
        int measurementIterations = 20;
        String resultFile = "jmh-result.txt";
        String resultFormat = "text";

        if (null != args && args.length == 4) {
            warmupIterations = Integer.parseInt(args[0]);
            measurementIterations = Integer.parseInt(args[1]);
            resultFile = args[2];
            resultFormat = args[3];
        }

        Options opt = new OptionsBuilder()
                .include(getInclude(SmallXmlBenchmark.class))
                .include(getInclude(LargeXmlBenchmark.class))
                .include(getInclude(PersistenceContentHandlerBenchmark.class))
                .include(getInclude(JsonMarshalBenchmark.class))
                .include(getInclude(JsonUnmarshalBenchmark.class))
                .include(getInclude(JsonWriterBenchmark.class))
                .include(getInclude(MOXyValidationBenchmark.class))
                 // tests that are not part of regular test-harness
//                .include(getInclude(JPAValidationBenchmark.class))
//                .include(getInclude(ReferenceResolverBenchmark.class))
//                .include(getInclude(CaseInsensitiveUnmarshalBenchmark.class))
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
