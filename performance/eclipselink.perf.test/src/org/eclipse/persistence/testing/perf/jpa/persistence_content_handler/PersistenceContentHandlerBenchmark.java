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
package org.eclipse.persistence.testing.perf.jpa.persistence_content_handler;

import java.io.StringReader;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.eclipse.persistence.internal.jpa.deployment.xml.parser.PersistenceContentHandler;
import org.eclipse.persistence.internal.jpa.deployment.xml.parser.XMLExceptionHandler;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

/**
 * This benchmark demonstrates PersistenceContentHandler performance.
 *
 * @author Martin Vojtek (martin.vojtek@oracle.com)
 *
 */
@State(Scope.Benchmark)
public class PersistenceContentHandlerBenchmark {

    public static final String DYNAMIC_PERSISTENCE_NAME = "dynamic";
    static final String DYNAMIC_PERSISTENCE_XML =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
        "<persistence version=\"1.0\" xmlns=\"http://java.sun.com/xml/ns/persistence\" " +
            "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " +
            "xsi:schemaLocation=\"http://java.sun.com/xml/ns/persistence " +
            "http://java.sun.com/xml/ns/persistence/persistence_1_0.xsd\">" +
            "<persistence-unit name=\"" + DYNAMIC_PERSISTENCE_NAME +
                "\" transaction-type=\"RESOURCE_LOCAL\">" +
                "<exclude-unlisted-classes>true</exclude-unlisted-classes>" +
            "</persistence-unit>" +
        "</persistence>";

    private SAXParserFactory spf;

    /*
     * Initial setup.
     */
    @Setup
    public void prepare() throws Exception {
        spf = SAXParserFactory.newInstance();
        spf.setNamespaceAware(true);
    }

    @Benchmark
    public void testPersistenceContentHandler(Blackhole bh) throws Exception {
        PersistenceContentHandler myContentHandler = new PersistenceContentHandler();
        SAXParser sp = spf.newSAXParser();
        XMLReader xmlReader = sp.getXMLReader();
        XMLExceptionHandler xmlErrorHandler = new XMLExceptionHandler();
        xmlReader.setErrorHandler(xmlErrorHandler);
        xmlReader.setContentHandler(myContentHandler);
        InputSource inputSource = new InputSource(new StringReader(DYNAMIC_PERSISTENCE_XML));
        xmlReader.parse(inputSource);
        bh.consume(inputSource);
        bh.consume(myContentHandler);
        bh.consume(xmlReader);
    }
}
