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
//     Martin Vojtek - 2.6 - initial implementation
package org.eclipse.persistence.testing.perf.smallxml;

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.eclipse.persistence.testing.perf.smallxml.workorder.DoWorkItem;
import org.eclipse.persistence.testing.perf.smallxml.workorder.DoWorkItemResponse;
import org.eclipse.persistence.testing.perf.smallxml.workorder.ObjectFactory;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

/**
 * This benchmark demonstrates small xml marshal and unmarshal.
 *
 * @author Martin Vojtek (martin.vojtek@oracle.com)
 *
 */
@State(Scope.Benchmark)
public class SmallXmlBenchmark {

    private static final String DO_WORK_ITEM_XML = "org/eclipse/persistence/testing/perf/smallxml/workorder/doWorkItem.xml";
    private static final String DO_WORK_ITEM_RESPONSE_XML = "org/eclipse/persistence/testing/perf/smallxml/workorder/doWorkItemResponse.xml";

    private JAXBContext jaxbContext;
    private DoWorkItem doWorkItem;
    private DoWorkItemResponse doWorkItemResponse;

    /*
     * Initial setup.
     */
    @Setup
    public void prepare() throws Exception {
        prepareJAXBContext();
        prepareDoWorkItem();
        prepareDoWorkItemResponse();
    }

    @Benchmark
    public void testWorkOrderUnmarshal(Blackhole bh) throws Exception {
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        Object workOrderItemElement = unmarshaller.unmarshal(Thread.currentThread().getContextClassLoader().getResource(DO_WORK_ITEM_XML));
        bh.consume(workOrderItemElement);
    }

    @Benchmark
    public void testWorkOrderMarshal(Blackhole bh) throws Exception {
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        StringWriter writer = new StringWriter();
        marshaller.marshal(doWorkItem, writer);
        bh.consume(writer);
    }

    @Benchmark
    public void testWorkOrderOutputStreamMarshal(Blackhole bh) throws Exception {
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        marshaller.marshal(doWorkItem, baos);
        bh.consume(baos.toString());
    }
    @Benchmark
    public void testWorkOrderResponseUnmarshal(Blackhole bh) throws Exception {
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        Object workOrderItemElement = unmarshaller.unmarshal(Thread.currentThread().getContextClassLoader().getResource(DO_WORK_ITEM_RESPONSE_XML));
        bh.consume(workOrderItemElement);
    }

    @Benchmark
    public void testWorkOrderResponseMarshal(Blackhole bh) throws Exception {
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        StringWriter writer = new StringWriter();
        marshaller.marshal(doWorkItemResponse, writer);
        bh.consume(writer);
    }

    @Benchmark
    public void testWorkOrderResponseOutputStreamMarshal(Blackhole bh) throws Exception {
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        marshaller.marshal(doWorkItemResponse, baos);
        bh.consume(baos.toString());
    }
    private void prepareDoWorkItemResponse() {
        doWorkItemResponse = new DoWorkItemResponse();
        doWorkItemResponse.setReturn(true);
    }

    private void prepareDoWorkItem() {
        doWorkItem = new DoWorkItem();
        doWorkItem.setWid(5);
        doWorkItem.setWorkLocation(7);
    }

    private void prepareJAXBContext() throws Exception {
        jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
    }
}
