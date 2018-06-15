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
package org.eclipse.persistence.testing.perf.moxy.casesensitivity;

import org.eclipse.persistence.jaxb.JAXBContext;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.JAXBUnmarshaller;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.testing.perf.moxy.casesensitivity.correctCase.LoremIpsum;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.infra.Blackhole;

import java.io.File;

/**
 * Performance tests for case insensitive vs case sensitive (aka with feature off) unmarshalling.
 *
 * @author Marcel Valovy - marcel.valovy@oracle.com
 */
@State(Scope.Benchmark)
public class CaseInsensitiveUnmarshalBenchmark {

    private File file;

    private JAXBUnmarshaller unmCorrectCaseSensitive;
    private JAXBUnmarshaller unmOtherCaseInsensitive;

    @Benchmark
    public void testUnmarshalCorrectCaseSensitive(Blackhole bh) throws Exception {
        LoremIpsum loremCorrectCase
                = (LoremIpsum) unmCorrectCaseSensitive.unmarshal(file);

        bh.consume(loremCorrectCase);
    }

    @Benchmark
    public void testUnmarshalOtherCaseInsensitive (Blackhole bh) throws Exception {
        org.eclipse.persistence.testing.perf.moxy.casesensitivity.otherCase.LoremIpsum loremOtherCase
                = (org.eclipse.persistence.testing.perf.moxy.casesensitivity.otherCase.LoremIpsum) unmOtherCaseInsensitive.unmarshal(file);

        bh.consume(loremOtherCase);
    }

    /**
     * Initial setup.
     */
    @Setup
    public void prepare() throws Exception {
        /* Create and assign case-sensitive unmarshaller */
        JAXBContext ctxCorrectCaseSensitive =
                (JAXBContext) JAXBContextFactory.createContext(new Class[]{LoremIpsum.class},null);
        unmCorrectCaseSensitive = ctxCorrectCaseSensitive.createUnmarshaller();

        /* Create and assign case-insensitive unmarshaller */
        JAXBContext ctxOtherCaseInsensitive = (JAXBContext) JAXBContextFactory.
                createContext(new Class[]{ org.eclipse.persistence.testing.perf.moxy.casesensitivity.otherCase
                        .LoremIpsum.class }, null);
        unmOtherCaseInsensitive = ctxOtherCaseInsensitive.createUnmarshaller();
        unmOtherCaseInsensitive.setProperty(UnmarshallerProperties.UNMARSHALLING_CASE_INSENSITIVE, Boolean.TRUE);

        file = new File(Thread.currentThread().getContextClassLoader().getResource
                ("org/eclipse/persistence/testing/perf/casesensitivity/loremIpsum.xml").getPath());
    }

    /**
     * Clean-up.
     */
    @TearDown
    public void tearDown() throws Exception {
        unmCorrectCaseSensitive = null;
        unmOtherCaseInsensitive = null;
    }

}
