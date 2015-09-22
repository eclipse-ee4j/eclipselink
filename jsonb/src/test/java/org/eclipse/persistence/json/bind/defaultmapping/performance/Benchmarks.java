package org.eclipse.persistence.json.bind.defaultmapping.performance;

import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

/**
 * Created by dakral on 19.11.2015.
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
                .include(getInclude(PerformanceTest.class))
                //.include(PerformanceTest.class.getCanonicalName())
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
