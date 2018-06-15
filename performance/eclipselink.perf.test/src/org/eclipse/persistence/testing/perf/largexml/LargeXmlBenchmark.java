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
package org.eclipse.persistence.testing.perf.largexml;

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.eclipse.persistence.testing.perf.largexml.bigpo.ObjectFactory;
import org.eclipse.persistence.testing.perf.largexml.bigpo.WrappedPurchaseOrderType;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

/**
 * This benchmark demonstrates large xml marshal and unmarshal.
 *
 * @author Martin Vojtek (martin.vojtek@oracle.com)
 *
 */
@State(Scope.Benchmark)
public class LargeXmlBenchmark {
    private static final String BIG_PURCHASE_ORDER_XML = "org/eclipse/persistence/testing/perf/largexml/bigpo/BigPurchaseOrder.xml";
    private static final String A_LOT_OF_ITEMS_XML = "org/eclipse/persistence/testing/perf/largexml/bigpo/LotOfItems.xml";

    private JAXBContext jaxbContext;
    private WrappedPurchaseOrderType bigPurchaseOrder;
    private WrappedPurchaseOrderType lotOfItemsOrder;

    /*
     * Initial setup.
     */
    @Setup
    public void prepare() throws Exception {
        prepareJAXBContext();
        prepareLotOfItems();
        prepareBigPurchaseOrder();
    }

    @Benchmark
    public void testBigPurchaseOrderUnmarshal(Blackhole bh) throws Exception {
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        Object bigPurchaseOrder = unmarshaller.unmarshal(Thread.currentThread().getContextClassLoader().getResource(BIG_PURCHASE_ORDER_XML));
        bh.consume(bigPurchaseOrder);
    }

    @Benchmark
    public void testBigPurchaseOrderMarshal(Blackhole bh) throws Exception {
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        StringWriter writer = new StringWriter();
        marshaller.marshal(bigPurchaseOrder, writer);
        bh.consume(writer);
    }

    @Benchmark
    public void testBigPurchaseOrderOutputStreamMarshal(Blackhole bh) throws Exception {
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        marshaller.marshal(bigPurchaseOrder, baos);
        bh.consume(baos.toString());
    }
    @Benchmark
    public void testALotOfItemsUnmarshal(Blackhole bh) throws Exception {
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        Object aLotOfItems = unmarshaller.unmarshal(Thread.currentThread().getContextClassLoader().getResource(A_LOT_OF_ITEMS_XML));
        bh.consume(aLotOfItems);
    }

    @Benchmark
    public void testALotOfItemsMarshal(Blackhole bh) throws Exception {
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        StringWriter writer = new StringWriter();
        marshaller.marshal(lotOfItemsOrder, writer);
        bh.consume(writer);
    }

    @Benchmark
    public void testALotOfItemsOutputStreamMarshal(Blackhole bh) throws Exception {
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        marshaller.marshal(lotOfItemsOrder, baos);
        bh.consume(baos.toString());
    }
    private void prepareBigPurchaseOrder() throws Exception {
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        @SuppressWarnings("rawtypes")
        JAXBElement jaxbElement = (JAXBElement)unmarshaller.unmarshal(Thread.currentThread().getContextClassLoader().getResource(BIG_PURCHASE_ORDER_XML));
        bigPurchaseOrder = (WrappedPurchaseOrderType) jaxbElement.getValue();
    }

    private void prepareLotOfItems() throws Exception {
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        @SuppressWarnings("rawtypes")
        JAXBElement jaxbElement = (JAXBElement)unmarshaller.unmarshal(Thread.currentThread().getContextClassLoader().getResource(A_LOT_OF_ITEMS_XML));
        lotOfItemsOrder = (WrappedPurchaseOrderType) jaxbElement.getValue();
    }

    private void prepareJAXBContext() throws Exception {
        jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
    }
}
