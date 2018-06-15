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
package org.eclipse.persistence.testing.perf.moxy.referenceresolver;

import org.eclipse.persistence.jaxb.JAXBContext;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.JAXBMarshaller;
import org.eclipse.persistence.jaxb.JAXBUnmarshaller;
import org.eclipse.persistence.testing.perf.moxy.referenceresolver.ClassicMoxyContainer;
import org.eclipse.persistence.testing.perf.moxy.referenceresolver.Component;
import org.eclipse.persistence.testing.perf.moxy.referenceresolver.Layer;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.infra.Blackhole;

import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.StringWriter;
import java.util.HashMap;

/**
 * Performance test case which measures running time of Marshalling and Unmarshalling a huge container.
 *
 * @author Marcel Valovy - marcel.valovy@oracle.com
 * @see <a href="https://bugs.eclipse.org/bugs/show_bug.cgi?id=437920">EclipseLink Forum, Bug 437920.</a>
 */
@State(Scope.Benchmark)
public class ReferenceResolverBenchmark {

    private ClassicMoxyContainer c;
    private JAXBMarshaller marshaller;
    private JAXBUnmarshaller unmarshaller;
    private Unmarshaller unmarshallerJaxb;
    private Marshaller marshallerJaxb;

    @Benchmark
    public void testReferenceResolverMarshal(Blackhole bh) throws Exception {
        StringWriter writer = new StringWriter();
        marshaller.marshal(c, writer);

        bh.consume(writer);
    }

    @Benchmark
    public void testReferenceResolverUnmarshal(Blackhole bh) throws Exception {
        ClassicMoxyContainer cmc = ClassicMoxyContainer.class.cast(unmarshaller.unmarshal(new FileReader("fc.xml")));

        bh.consume(cmc);
    }

    public void testReferenceResolverMarshalJAXBRI(Blackhole bh) throws Exception {
        StringWriter writer = new StringWriter();
        marshallerJaxb.marshal(c, writer);

        bh.consume(writer);
    }

    @Benchmark
    public void testReferenceResolverUnmarshalJAXBRI(Blackhole bh) throws Exception {
        ClassicMoxyContainer cmc = ClassicMoxyContainer.class.cast(unmarshallerJaxb.unmarshal(new FileReader("fc.xml")));
        bh.consume(cmc);
    }

    /**
     * Initial setup.
     */
    @Setup
    public void prepare() throws Exception {
        /* Create and assign case-sensitive unmarshaller */
        c = ClassicMoxyContainer.createHugeContainer();
        JAXBContext context = (JAXBContext) JAXBContextFactory.createContext(
                new Class[]{ClassicMoxyContainer.class, Layer.class, Component.class}, new HashMap());
        javax.xml.bind.JAXBContext contextJaxb = JAXBContext.newInstance(
                ClassicMoxyContainer.class, Layer.class, Component.class);
        marshaller = context.createMarshaller();
        marshaller.setProperty(JAXBMarshaller.JAXB_FORMATTED_OUTPUT, true);
        unmarshaller = context.createUnmarshaller();
        marshallerJaxb = contextJaxb.createMarshaller();
        marshallerJaxb.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        unmarshallerJaxb = contextJaxb.createUnmarshaller();

        marshaller.marshal(c, new FileWriter("fc.xml"));
    }

    /**
     * Clean-up.
     */
    @TearDown
    public void tearDown() throws Exception {
        //noinspection ResultOfMethodCallIgnored
        new File("fc.xml").delete();
    }

}
